LOCAL_PATH:= $(call my-dir)

#add by yangjiajun@wind-mobi.com 2017-10-10 begin
include $(CLEAR_VARS)
LOCAL_MODULE := libmylcdtest
LOCAL_PROPRIETARY_MODULE := true
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS = SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX = .so
LOCAL_SRC_FILES_32 := lib/libmylcdtest.so
LOCAL_SRC_FILES_64 := lib64/libmylcdtest.so
LOCAL_MULTILIB := both
include $(BUILD_PREBUILT)
#add by yangjiajun@wind-mobi.com 2017-10-10 end

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    bootanimation_main.cpp \
    audioplay.cpp \
    BootAnimation.cpp

LOCAL_CFLAGS += -DGL_GLEXT_PROTOTYPES -DEGL_EGLEXT_PROTOTYPES

LOCAL_CFLAGS += -Wall -Werror -Wunused -Wunreachable-code

LOCAL_C_INCLUDES += \
    external/tinyalsa/include \
    frameworks/wilhelm/include \
    $(LOCAL_PATH)/../../libs/regionalization

ifeq (ATT, $(TARGET_SKU))
    LOCAL_SRC_FILES += BootRingtone.cpp \
                    Utility.cpp
    LOCAL_CFLAGS += -DRINGTONE_THREAD
endif 


LOCAL_SHARED_LIBRARIES := \
    libcutils \
    liblog \
    libandroidfw \
    libutils \
    libbinder \
    libui \
    libskia \
    libEGL \
    libGLESv1_CM \
    libgui \
    libOpenSLES \
    libtinyalsa \
    libbase \
    libregionalization

	
#add by yangjiajun@wind-mobi.com 2017-10-10 begin
LOCAL_SHARED_LIBRARIES += libmylcdtest
#add by yangjiajun@wind-mobi.com 2017-10-10 end

LOCAL_MODULE:= bootanimation

LOCAL_INIT_RC := bootanim.rc

ifdef TARGET_32_BIT_SURFACEFLINGER
LOCAL_32_BIT_ONLY := true
endif

include $(BUILD_EXECUTABLE)
