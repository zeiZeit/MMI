#include <jni.h>

#ifndef _Included_com_wind_factoryautotest_tools
#define _Included_com_wind_factoryautotest_tools

JNIEXPORT jboolean JNICALL Java_com_wind_factoryautotest_Utils_checkCompass
  (JNIEnv *, jclass, jint, jint, jint, jintArray);

JNIEXPORT jboolean JNICALL Java_com_wind_factoryautotest_Utils_checkGsensor
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloatArray);

JNIEXPORT jboolean JNICALL Java_com_wind_factoryautotest_Utils_checkGyro
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloatArray);

#endif
