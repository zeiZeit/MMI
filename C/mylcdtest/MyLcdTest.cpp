#define LOG_NDEBUG 0
#define LOG_TAG "MyLcdTest"

#include <utils/Log.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <dirent.h>
#include <fcntl.h>
#include <errno.h>

#include <cutils/properties.h>
#include <ui/PixelFormat.h>
#include <ui/Rect.h>
#include <ui/Region.h>
#include <ui/DisplayInfo.h>

#include <gui/ISurfaceComposer.h>
#include <gui/Surface.h>
#include <gui/SurfaceComposerClient.h>
#include <private/gui/LayerState.h>

#include <SkBitmap.h>
#include <SkStream.h>
#include <SkImageDecoder.h>

#include <GLES/gl.h>
#include <GLES/glext.h>
#include <EGL/eglext.h>

#include <unistd.h>
#include <limits.h>
#include <sys/types.h>
#include <sys/stat.h>

using namespace android;

int mylcdtest(void) {
	sp<SurfaceComposerClient> mSession = new SurfaceComposerClient();
	sp<IBinder> dtoken(SurfaceComposerClient::getBuiltInDisplay(
	                       ISurfaceComposer::eDisplayIdMain));
	DisplayInfo dinfo;
	status_t status = SurfaceComposerClient::getDisplayInfo(dtoken, &dinfo);
	if (status)
		return -1;

	SurfaceComposerClient::setDisplayProjection(dtoken, DisplayState::eOrientationDefault, Rect(dinfo.w, dinfo.h), Rect(dinfo.w, dinfo.h));
	// create the native surface
	sp<SurfaceControl> control = mSession->createSurface(String8("lcd_test"),
	                             dinfo.w, dinfo.h, PIXEL_FORMAT_RGB_565);

	SurfaceComposerClient::openGlobalTransaction();
	control->setLayer(0x2000010);
	SurfaceComposerClient::closeGlobalTransaction();

	sp<Surface> s = control->getSurface();

	EGLint w, h;
	EGLint numConfigs;
	EGLConfig config;
	EGLSurface surface;
	EGLContext context;

	EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);

	ALOGD("initialize opengl and egl");
	EGLBoolean eglret = eglInitialize(display, 0, 0);
	if (eglret == EGL_FALSE) {
		ALOGE("eglInitialize(display, 0, 0) return EGL_FALSE");
	}
	bool bETC1Movie = false;
	if (!bETC1Movie) {
		const EGLint attribs[] = {
			EGL_RED_SIZE,   8,
			EGL_GREEN_SIZE, 8,
			EGL_BLUE_SIZE,  8,
			EGL_DEPTH_SIZE, 0,
			EGL_NONE
		};
		eglChooseConfig(display, attribs, &config, 1, &numConfigs);
		context = eglCreateContext(display, config, NULL, NULL);
	} else {
		const EGLint attribs[] = {
			EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
			EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
			EGL_RED_SIZE,   5,
			EGL_GREEN_SIZE, 6,
			EGL_BLUE_SIZE,  5,
			EGL_DEPTH_SIZE, 16,
			EGL_NONE
		};
		eglChooseConfig(display, attribs, &config, 1, &numConfigs);
		int attrib_list[] = {EGL_CONTEXT_CLIENT_VERSION, 2,
		                     EGL_NONE, EGL_NONE
		                    };
		context = eglCreateContext(display, config, EGL_NO_CONTEXT, attrib_list);
	}

	surface = eglCreateWindowSurface(display, config, s.get(), NULL);
	eglret = eglQuerySurface(display, surface, EGL_WIDTH, &w);
	if (eglret == EGL_FALSE) {
		ALOGE("eglQuerySurface(display, surface, EGL_WIDTH, &w) return EGL_FALSE");
	}
	eglret = eglQuerySurface(display, surface, EGL_HEIGHT, &h);
	if (eglret == EGL_FALSE) {
		ALOGE("eglQuerySurface(display, surface, EGL_HEIGHT, &h) return EGL_FALSE");
	}

	if (eglMakeCurrent(display, surface, surface, context) == EGL_FALSE) {
		ALOGE("eglMakeCurrent(display, surface, surface, context) return EGL_FALSE");
		return NO_INIT;
	}

	//glShadeModel(GL_FLAT);
	glDisable(GL_DITHER);
	glDisable(GL_SCISSOR_TEST);

	char cv[PROPERTY_VALUE_MAX] = {0};
	int cc = 1;
	const char *fifo_name = "/data/lcdtest_fifo";
	int pipe_fd = -1;
	int res = 0;
	const int open_mode = O_RDONLY;

	if(access(fifo_name, F_OK) == -1) {
		res = mkfifo(fifo_name, 0777);
		if(res != 0) {
			ALOGE("Could not create fifo %s\n", fifo_name);
		}
	}

	ALOGE("create fifo result: %d\n", res);

	while(res == 0) {
		property_get("sys.wind.color", cv, "1");
		cc = atoi(cv);
		if(cc == 0) {
			//property_set("sys.wind.colortest", "0");
			break;
		}
		switch(cc) {
		case 1:
			glClearColor(1,0,0,1);
			break;
		case 2:
			glClearColor(0,1,0,1);
			break;
		case 3:
			glClearColor(0,0,1,1);
			break;
		case 4:
			glClearColor(0,0,0,1);
			break;
		case 5:
			glClearColor(1,1,1,1);
			break;
		case 6:
			glClearColor(0.39,0.39,0.39,1);
			break;
		}
		glClear(GL_COLOR_BUFFER_BIT);
		eglSwapBuffers(display, surface);
		//usleep(2000000);
		pipe_fd = open(fifo_name, open_mode);
		if(pipe_fd > 0) {
			close(pipe_fd);
		}
	}

	eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
	eglDestroyContext(display, context);
	eglDestroySurface(display, surface);
	s.clear();
	control.clear();
	eglTerminate(display);
	return 0;
}

