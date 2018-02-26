LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files) \
    src/com/android/fmradio/IFmTest.aidl

#LOCAL_JAVA_LIBRARIES += mediatek-framework
#LOCAL_JAVA_LIBRARIES += telephony-common
#LOCAL_STATIC_JAVA_LIBRARIES := guava
#LOCAL_STATIC_JAVA_LIBRARIES += services.core

LOCAL_PACKAGE_NAME := FactoryAutoTest
LOCAL_CERTIFICATE := platform

LOCAL_DEX_PREOPT = false
LOCAL_JAVA_LIBRARIES += qcom.fmradio

LOCAL_JNI_SHARED_LIBRARIES := libqcomfm_jni

#LOCAL_PROGUARD_ENABLED := full obfuscation
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
