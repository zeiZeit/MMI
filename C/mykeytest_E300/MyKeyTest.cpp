#define LOG_TAG "MyKeyTest"

#include <utils/Log.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cstring>
#include <stdint.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <errno.h>

#include <input/Input.h>
#include <input/InputDevice.h>
#include <input/Keyboard.h>
#include <input/KeyLayoutMap.h>
#include <input/VirtualKeyMap.h>
#include <utils/String8.h>
#include <utils/Errors.h>
#include <utils/PropertyMap.h>
#include <cutils/properties.h>

#include<fstream>
#include<iomanip>

#define BITS_PER_LONG (sizeof(unsigned long) * 8)
#define BITS_TO_LONGS(x) (((x) + BITS_PER_LONG - 1) / BITS_PER_LONG)

#define test_bit(bit, array) \
    ((array)[(bit)/BITS_PER_LONG] & (1 << ((bit) % BITS_PER_LONG)))

#define DEVICE_SIZE 16

using namespace android;

static struct pollfd *ufds;
static int mNextDeviceId = 0;
static KeyMap keyMap[DEVICE_SIZE];
//static int key_array[] = {3/*HOME*/, 4/*back*/, 24/*vol_up*/, 25/*vol_down*/, 26/*power*/};	//for Z200
static int key_array[] = {24/*vol_up*/, 25/*vol_down*/, 26/*power*/,79/*headsethook*/};	//for E260
//static int key_array[] = {24/*vol_up*/, 25/*vol_down*/, 26/*power*/};	//for Z205
static int down_key_flag[] = {0X001, 0X004, 0X010,0X040};
static int up_key_flag[] = {0X002, 0X008, 0X020,0X080};
static int array_len = 4;
static int success_val = 255; //127
static int keytestval = 0;

static void process_keyevent(int32_t key_code, int is_down) {
	int i;
	for(i = 0; i < array_len; i ++) {
		if(key_code == key_array[i]) {
			ALOGE("key array[i] = %d  keycode = %d",key_array[i],key_code);
	        if(is_down) {
		        keytestval = keytestval | down_key_flag[i];
				ALOGI("keytestval = %d",keytestval);
	        }else{
		        keytestval = keytestval | up_key_flag[i];
				ALOGI("keytestval = %d",keytestval);
	        }
			break;
		}
	}
}

static void filter_key_device(const char *devicePath) {
	char buffer[80];
	unsigned long ev_bits[BITS_TO_LONGS(EV_MAX)];

	ALOGD("Opening device: %s", devicePath);

	int fd = open(devicePath, O_RDWR | O_CLOEXEC);
	if(fd < 0) {
		ALOGE("could not open %s, %s\n", devicePath, strerror(errno));
		return;
	}

	ioctl(fd, EVIOCGBIT(0, sizeof(ev_bits)), ev_bits);
	if (!test_bit(EV_KEY, ev_bits)) {
		ALOGD("not key device!");
		close(fd);
		return;
	}

	InputDeviceIdentifier identifier;

	// Get device name.
	if(ioctl(fd, EVIOCGNAME(sizeof(buffer) - 1), &buffer) < 1) {
		//fprintf(stderr, "could not get device name for %s, %s\n", devicePath, strerror(errno));
	} else {
		buffer[sizeof(buffer) - 1] = '\0';
		identifier.name.setTo(buffer);
	}

	// Get device driver version.
	int driverVersion;
	if(ioctl(fd, EVIOCGVERSION, &driverVersion)) {
		ALOGE("could not get driver version for %s, %s\n", devicePath, strerror(errno));
		close(fd);
		return;
	}

	// Get device identifier.
	struct input_id inputId;
	if(ioctl(fd, EVIOCGID, &inputId)) {
		ALOGE("could not get device input id for %s, %s\n", devicePath, strerror(errno));
		close(fd);
		return;
	}
	identifier.bus = inputId.bustype;
	identifier.product = inputId.product;
	identifier.vendor = inputId.vendor;
	identifier.version = inputId.version;

	// Get device physical location.
	if(ioctl(fd, EVIOCGPHYS(sizeof(buffer) - 1), &buffer) < 1) {
		//fprintf(stderr, "could not get location for %s, %s\n", devicePath, strerror(errno));
	} else {
		buffer[sizeof(buffer) - 1] = '\0';
		identifier.location.setTo(buffer);
	}

	// Get device unique id.
	if(ioctl(fd, EVIOCGUNIQ(sizeof(buffer) - 1), &buffer) < 1) {
		//fprintf(stderr, "could not get idstring for %s, %s\n", devicePath, strerror(errno));
	} else {
		buffer[sizeof(buffer) - 1] = '\0';
		identifier.uniqueId.setTo(buffer);
	}

	// Fill in the descriptor.
	//assignDescriptorLocked(identifier);

	// Make file descriptor non-blocking for use with poll().
	if (fcntl(fd, F_SETFL, O_NONBLOCK)) {
		ALOGE("Error %d making device file descriptor non-blocking.", errno);
		close(fd);
		return;
	}

	int deviceId = mNextDeviceId ++;
	//Device* device = new Device(fd, deviceId, String8(devicePath), identifier);
	String8 configurationFile;
	PropertyMap* configuration = NULL;
	//-----------------------------------------------------------------------------------------------------------------2
	configurationFile = getInputDeviceConfigurationFilePathByDeviceIdentifier(
	                        identifier, INPUT_DEVICE_CONFIGURATION_FILE_TYPE_CONFIGURATION);
	if (configurationFile.isEmpty()) {
		ALOGD("No input device configuration file found for device '%s'.",
		      identifier.name.string());
	} else {
		status_t status = PropertyMap::load(configurationFile,
		                                    &configuration);
		if (status) {
			ALOGE("Error loading input device configuration file for device '%s'.  "
			      "Using default configuration.",
			      identifier.name.string());
		}
	}
	//-----------------------------------------------------------------------------------------------------------------3
	//ALOGD("keyMap is null? %d", (&keyMap[deviceId] == NULL));
	keyMap[deviceId].load(identifier, configuration);
	//delete device;

	ufds[deviceId].fd = fd;
	ufds[deviceId].events = POLLIN;
}

static void scan_dir(const char *dirname) {
	char devname[PATH_MAX];
	char *filename;
	DIR *dir;
	struct dirent *de;
	dir = opendir(dirname);
	if(dir == NULL)
		return;
	strcpy(devname, dirname);
	filename = devname + strlen(devname);
	*filename++ = '/';
	ufds = (pollfd*)calloc(DEVICE_SIZE, sizeof(ufds[0]));
	while((de = readdir(dir))) {
		if(de->d_name[0] == '.' &&
		        (de->d_name[1] == '\0' ||
		         (de->d_name[1] == '.' && de->d_name[2] == '\0')))
			continue;
		strcpy(filename, de->d_name);
		filter_key_device(devname);
	}
	closedir(dir);
}

static void testKeys() {
	struct input_event event;
	int i, res;
	//int j, count = 0;
	int32_t outKeycode = 0;
	uint32_t outFlags = 0;
	ALOGD("start key monitor...");
	scan_dir("/dev/input");
	ALOGD("scan end!");
	while(1) {
		poll(ufds, mNextDeviceId, -1);
		for(i = 0; i < mNextDeviceId; i++) {
			if(ufds[i].revents) {
				if(ufds[i].revents & POLLIN) {
					ALOGD("before read...");
					res = read(ufds[i].fd, &event, sizeof(event));
					if(res < (int)sizeof(event)) {
						return;
					}
					if(event.type == 1) {
						keyMap[i].keyLayoutMap->mapKey(event.code, 0, &outKeycode, &outFlags);
						process_keyevent(outKeycode, event.value);
						/*count = 0;
						for(j = 0; j < array_len; j ++){
						    count += key_down[j] + key_up[j];
						}*/
						ALOGD("process_keyevent code: %x   outKeycode: %d    keytestval: %x", event.code, outKeycode, keytestval);
						char buffer[128];
						sprintf(buffer, "%d", keytestval);
						ALOGD("buffer is %s ",buffer);
						property_set("sys.wind.keytestval", buffer);
						if(keytestval == success_val) {
							return;
						}
					}
				}
			}
		}
	}
	return;
}

void write_file(const char *path,const char *value){
    FILE *fp = fopen(path, "w");
    if(fp){
        fwrite(value, 1, strlen(value), fp);
        fclose(fp);
    }
}

int main(int argc, char** argv) {
	ALOGD("mykeytest start... argc: %d   argv[0]: %s\n", argc, argv[0]);
    if(argc > 1 && strlen(argv[1]) > 0){
        char c = *(argv[1]);
        switch(c){
            case '1':
                write_file("/sys/class/leds/red/brightness", *(argv[1]+1)=='1'?"255":"0");
                break;
            case '2':
                write_file("/sys/class/leds/green/brightness", *(argv[1]+1)=='1'?"255":"0");
                break;
            /*case '3':
                write_file("/sys/class.../red/brightness", *(argv[1]+1)=='1'?"255":"0");
                break;*/
        }
        return 0;
    }
	testKeys();
    return 0;
}
