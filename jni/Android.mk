LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS := -O3 -DNDEBUG -Wall -Wunused-parameter

LOCAL_C_INCLUDES    += com_wind_factoryautotest_tools.h
LOCAL_SRC_FILES := \
        tools.cpp

LOCAL_SHARED_LIBRARIES := libcutils
LOCAL_LDLIBS :=-L$(SYSROOT)/usr/lib -llog

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE    := libfat_tools
include $(BUILD_SHARED_LIBRARY)
