#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jni.h>
#include <android/log.h>
//#include <cutils/log.h>
//#include <utils/Log.h>
#include <com_wind_factoryautotest_tools.h>

#define LOG_TAG "yangjiajun"
#undef LOG
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

JNIEXPORT jboolean JNICALL Java_com_wind_factoryautotest_Utils_checkCompass(JNIEnv *env, jclass thiz, jint x, jint y, jint z, jintArray array1){
    if(thiz || y || z){}
    //arr[0]: min, arr[1]: max, arr[2]: pre value, arr[3]: change num, arr[4]: pre_time, arr[5]: total_time
    jint arr[] = {0, 0, 0, 0};
    /*struct timeval tv;
    long ct = 0;
    gettimeofday(&tv, NULL);
    ct = tv.tv_sec * 1000 + tv.tv_usec / 1000;*/
    env->GetIntArrayRegion(array1, 0, 4, arr);
    if(arr[0] == 0){
        arr[0] = x;
    }else if(x < arr[0]){
        arr[0] = x;
    }
    if(arr[1] == 0){
        arr[1] = x;
    }else if(x > arr[1]){
        arr[1] = x;
    }
    if(arr[2] == 0){
        arr[2] = x;
        arr[3] = arr[3] + 1;
        //arr[4] = ct;
    }else if(x != arr[2]){
        /*if(x - arr[2] > 2 || x - arr[2] < -2){
            arr[4] = ct;
        }*/
        arr[2] = x;
        if(arr[3] < 6){
            arr[3] = arr[3] + 1;
        }
        /*if(ct - arr[4] > 3000){
            arr[5] = 1;
        }*/
    }
    env->SetIntArrayRegion(array1, 0, 4, arr);
    if(arr[3] > 5 && arr[1] - arr[0] > 5 /*&& arr[5] == 1*/){
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

jboolean checkData(jfloat x, jfloat y, jfloat z, jfloat *arr, bool isGsensor){
    int i = 0;
    if(arr[0] > 0){
        arr[0] = arr[0] - 1;
        return JNI_FALSE;
    }
    if(arr[1] == 0){
        arr[2] = x;
        arr[6] = y;
        arr[10] = z;
        if(isGsensor){
            if(z > 8 || z < -8){
                arr[1] = 1;
            }
        }else{
            arr[1] = 1;
        }
    }else{
        if(arr[14] == 0){
            for(i = 2; i < 6; i ++){
                if(x == arr[i]){
                    break;
                }
                if(arr[i] == 0){
                    arr[i] = x;
                    if(i == 5){
                        arr[14] = 1;
                    }
                    break;
                }
            }
        }
        if(arr[15] == 0){
            for(i = 6; i < 10; i ++){
                if(x == arr[i]){
                    break;
                }
                if(arr[i] == 0){
                    arr[i] = x;
                    if(i == 9){
                        arr[15] = 1;
                    }
                    break;
                }
            }
        }
        if(arr[16] == 0){
            for(i = 10; i < 14; i ++){
                if(x == arr[i]){
                    break;
                }
                if(arr[i] == 0){
                    arr[i] = x;
                    if(i == 13){
                        arr[16] = 1;
                    }
                    break;
                }
            }
        }
        if(arr[14] == 1 && arr[15] == 1 && arr[16] == 1){
            return JNI_TRUE;
        }
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_wind_factoryautotest_Utils_checkGyro(JNIEnv *env, jclass thiz, jfloat x, jfloat y, jfloat z, jfloatArray array1){
    if(thiz || y || z){}
    jfloat arr[17];
    jboolean ret = JNI_FALSE;
    env->GetFloatArrayRegion(array1, 0, 17, arr);
    ret = checkData(x, y, x, arr, false);
    env->SetFloatArrayRegion(array1, 0, 17, arr);
    return ret;
}

JNIEXPORT jboolean JNICALL Java_com_wind_factoryautotest_Utils_checkGsensor(JNIEnv *env, jclass thiz, jfloat x, jfloat y, jfloat z, jfloatArray array1){
    if(thiz || y || z){}
    jfloat arr[17];
    jboolean ret = JNI_FALSE;
    env->GetFloatArrayRegion(array1, 0, 17, arr);
    ret = checkData(x, y, x, arr, true);
    env->SetFloatArrayRegion(array1, 0, 17, arr);
    return ret;
}

const char *kClassPathName = "com/wind/factoryautotest/Utils";

JNINativeMethod kMethods[] = {
        { "checkGsensor", "(FFF[F)Z", (void *)Java_com_wind_factoryautotest_Utils_checkGsensor },
        { "checkGyro", "(FFF[F)Z", (void *)Java_com_wind_factoryautotest_Utils_checkGyro },
        { "checkCompass", "(III[I)Z", (void *)Java_com_wind_factoryautotest_Utils_checkCompass },
};

int registerNativeMethods(JNIEnv* env, const char* className,
                          JNINativeMethod* gMethods, int numMethods) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if(reserved){}
    if (vm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    if (!registerNativeMethods(env, kClassPathName,
                               kMethods, 3)) {
        return -1;
    }
    return JNI_VERSION_1_6;
}
