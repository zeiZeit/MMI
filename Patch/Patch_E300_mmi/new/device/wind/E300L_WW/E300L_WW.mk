TARGET_USES_AOSP := true
TARGET_USES_AOSP_FOR_AUDIO := false
TARGET_USES_QCOM_BSP := false

ifeq ($(TARGET_USES_AOSP),true)
TARGET_DISABLE_DASH := true
endif

DEVICE_PACKAGE_OVERLAYS := device/wind/E300L_WW/overlay
# Default vendor configuration.
ifeq ($(ENABLE_VENDOR_IMAGE),)
ENABLE_VENDOR_IMAGE :=true
endif

# Disable QTIC until it's brought up in split system/vendor
# configuration to avoid compilation breakage.
#xiongshigui@wind-mobi.com 20171215 mod begin, remove unused qcom apks
ifeq ($(ENABLE_VENDOR_IMAGE), true)
TARGET_USES_QTIC := false
endif
#xiongshigui@wind-mobi.com 20171215 mod end

BOARD_HAVE_QCOM_FM := true

#xiongshigui@wind-mobi.com 20171204 mod begin
TARGET_USES_NQ_NFC := false
#xiongshigui@wind-mobi.com 20171204 mod end

ENABLE_AB ?= false

TARGET_KERNEL_VERSION := 3.18

TARGET_ENABLE_QC_AV_ENHANCEMENTS := true

-include $(QCPATH)/common/config/qtic-config.mk

# Enable features in video HAL that can compile only on this platform
TARGET_USES_MEDIA_EXTENSIONS := true

# media_profiles and media_codecs xmls for msm8937
ifeq ($(TARGET_ENABLE_QC_AV_ENHANCEMENTS), true)
PRODUCT_COPY_FILES += device/qcom/msm8937_32/media/media_profiles_8937.xml:system/etc/media_profiles.xml \
                      device/qcom/msm8937_32/media/media_profiles_8937.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_profiles_vendor.xml \
                      device/qcom/msm8937_32/media/media_profiles_8956.xml:system/etc/media_profiles_8956.xml \
                      device/qcom/msm8937_32/media/media_profiles_8956.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_profiles_8956.xml \
                      device/qcom/msm8937_32/media/media_codecs_8937.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs.xml \
                      device/qcom/msm8937_32/media/media_codecs_8956.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_8956.xml \
                      device/qcom/msm8937_32/media/media_codecs_performance_8937.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_performance.xml
endif

PRODUCT_COPY_FILES += device/wind/E300L_WW/whitelistedapps.xml:system/etc/whitelistedapps.xml \
                      device/wind/E300L_WW/gamedwhitelist.xml:system/etc/gamedwhitelist.xml

# video seccomp policy files
PRODUCT_COPY_FILES += \
    device/qcom/msm8937_32/seccomp/mediacodec-seccomp.policy:$(TARGET_COPY_OUT_VENDOR)/etc/seccomp_policy/mediacodec.policy \
    device/qcom/msm8937_32/seccomp/mediaextractor-seccomp.policy:$(TARGET_COPY_OUT_VENDOR)/etc/seccomp_policy/mediaextractor.policy

PRODUCT_PROPERTY_OVERRIDES += \
    vendor.vidc.disable.split.mode=1

PRODUCT_PROPERTY_OVERRIDES += \
           dalvik.vm.heapminfree=4m \
           dalvik.vm.heapstartsize=16m
$(call inherit-product, frameworks/native/build/phone-xhdpi-2048-dalvik-heap.mk)
$(call inherit-product, device/qcom/common/common64.mk)
#add by microarray start
$(call inherit-product, device/common/fingerprint/mafp/maproduct.mk)
#add by microarray end

#add by sunwave start
$(call inherit-product, device/common/fingerprint/sunwave/sunwave.mk)
#add by sunwave end

#zhangkaiyuan@wind-mobi.com 20171201 begin
$(call inherit-product, device/common/tuxera/tuxera.mk)
#zhangkaiyuan@wind-mobi.com 20171201 end
TARGET_VENDOR := wind
PRODUCT_NAME := E300L_WW
PRODUCT_DEVICE := E300L_WW
PRODUCT_BRAND := Android
PRODUCT_MODEL := E300L_WW for arm64

#add by yinlili -s
WIND_PRODUCT_NAME := WW_X00PD
WIND_DEVICE_NAME := auto
WIND_PRODUCT_BRAND := asus
WIND_PRODUCT_MODEL := ASUS_X00PD
WIND_PRODUCT_MANUFACTURER := asus
WIND_PORDUCT_BOARD := MSM8917
WIND_PRODUCT_CARRIER := WW
WIND_MTP_NAME := ASUS_X00PD
TARGET_SKU := WW
TARGET_PROJECT := ZB555KL
WIND_PRODUCT_HARDWARE := W14MA1B2-3 
WIND_ASUS_FOTA := yes
#add by yinlili -e

#xiongshigui@wind-mobi.com 20171108 add begin
ifneq (,$(TARGET_PROJECT))
ifneq (,$(TARGET_SKU))
#xiongshigui@wind-mobi.com 20180124 add begin for factory build
BUILD_NO_GMS=no
BUILD_NO_THIRD_PARTY=no
BUILD_NO_ASUS_APK=no
#xiongshigui@wind-mobi.com 20180124 add end
include vendor/ims/products/ims.mk
endif
endif
#xiongshigui@wind-mobi.com 20171108 add end

#zhangyanbin@wind-mobi.com 20171227 add for first_api_level begin
PRODUCT_SHIPPING_API_LEVEL := 26
#zhangyanbin@wind-mobi.com 20171227 add for first_api_level end

#add by yinlili@wind-mobi 20171024 -s for feature#5227
PRODUCT_PROPERTY_OVERRIDES += ro.product.hardware=$(WIND_PRODUCT_HARDWARE)
PRODUCT_PROPERTY_OVERRIDES += ro.build.asus.version=$(ASUSVERSION)
PRODUCT_PROPERTY_OVERRIDES += ro.wind.build.date=$(WDBUILDDATE)

#add by yinlili@wind-mobi.com -s for feature#5227
ifneq ($(strip $(TARGET_SKU)),)
   PRODUCT_PROPERTY_OVERRIDES += ro.custom.build.version=$(VER_OUTER)
endif 

ifneq ($(filter CN JP ,$(TARGET_SKU)),)
    PRODUCT_PROPERTY_OVERRIDES += ro.product.locale.region=$(TARGET_SKU)
else
    PRODUCT_PROPERTY_OVERRIDES += ro.product.locale.region=US
endif
PRODUCT_PROPERTY_OVERRIDES += ro.target_product=$(TARGET_PROJECT)


PRODUCT_COPY_FILES += \
     device/wind/E300L_WW/devconf.json:system/etc/devconf.json
#add by yinlili@wind-mobi 20171024 -e for feature#5227

PRODUCT_BOOT_JARS += tcmiface

ifneq ($(strip $(QCPATH)),)
PRODUCT_BOOT_JARS += WfdCommon
#PRODUCT_BOOT_JARS += com.qti.dpmframework
#PRODUCT_BOOT_JARS += dpmapi
#PRODUCT_BOOT_JARS += com.qti.location.sdk
#Android oem shutdown hook
PRODUCT_BOOT_JARS += oem-services
endif

ifeq ($(strip $(BOARD_HAVE_QCOM_FM)),true)
PRODUCT_BOOT_JARS += qcom.fmradio
endif #BOARD_HAVE_QCOM_FM

DEVICE_MANIFEST_FILE := device/wind/E300L_WW/manifest.xml
DEVICE_MATRIX_FILE   := device/qcom/common/compatibility_matrix.xml

# default is nosdcard, S/W button enabled in resource
PRODUCT_CHARACTERISTICS := nosdcard

# When can normal compile this module, need module owner enable below commands
# font rendering engine feature switch
#-include $(QCPATH)/common/config/rendering-engine.mk
#ifneq (,$(strip $(wildcard $(PRODUCT_RENDERING_ENGINE_REVLIB))))
#    MULTI_LANG_ENGINE := REVERIE
#    MULTI_LANG_ZAWGYI := REVERIE
#endif

ifneq ($(TARGET_DISABLE_DASH), true)
    PRODUCT_BOOT_JARS += qcmediaplayer
endif

#Android EGL implementation
PRODUCT_PACKAGES += libGLES_android

# Audio configuration file
-include $(TOPDIR)hardware/qcom/audio/configs/msm8937/msm8937.mk

# MIDI feature
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.midi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.midi.xml

#FEATURE_OPENGLES_EXTENSION_PACK support string config file
PRODUCT_COPY_FILES += \
        frameworks/native/data/etc/android.hardware.opengles.aep.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.opengles.aep.xml

PRODUCT_PACKAGES += android.hardware.media.omx@1.0-impl

#ANT+ stack
PRODUCT_PACKAGES += \
    AntHalService \
    libantradio \
    antradio_app

# Display/Graphics
 PRODUCT_PACKAGES += \
     android.hardware.graphics.allocator@2.0-impl \
     android.hardware.graphics.allocator@2.0-service \
     android.hardware.graphics.mapper@2.0-impl \
     android.hardware.graphics.composer@2.1-impl \
     android.hardware.graphics.composer@2.1-service \
     android.hardware.memtrack@1.0-impl \
     android.hardware.memtrack@1.0-service \
     android.hardware.light@2.0-impl \
     android.hardware.light@2.0-service \
     android.hardware.configstore@1.0-service \
     android.hardware.broadcastradio@1.0-impl

PRODUCT_PACKAGES += wcnss_service

# MSM IRQ Balancer configuration file
PRODUCT_COPY_FILES += \
    device/wind/E300L_WW/msm_irqbalance.conf:$(TARGET_COPY_OUT_VENDOR)/etc/msm_irqbalance.conf

#wlan driver
PRODUCT_COPY_FILES += \
    device/wind/E300L_WW/WCNSS_qcom_cfg.ini:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/WCNSS_qcom_cfg.ini \
    device/qcom/msm8937_32/WCNSS_wlan_dictionary.dat:persist/WCNSS_wlan_dictionary.dat \
    device/wind/E300L_WW/WCNSS_qcom_wlan_nv.bin:persist/WCNSS_qcom_wlan_nv.bin

PRODUCT_PACKAGES += \
    wpa_supplicant_overlay.conf \
    p2p_supplicant_overlay.conf

#for wlan
PRODUCT_PACKAGES += \
    wificond \
    wifilogd
# Feature definition files for msm8937
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.sensor.accelerometer.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.accelerometer.xml \
    frameworks/native/data/etc/android.hardware.sensor.compass.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.compass.xml \
    frameworks/native/data/etc/android.hardware.sensor.light.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.light.xml \
    frameworks/native/data/etc/android.hardware.sensor.proximity.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.proximity.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepcounter.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepcounter.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepdetector.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepdetector.xml

PRODUCT_PACKAGES += telephony-ext
PRODUCT_BOOT_JARS += telephony-ext

# Defined the locales
#add by yinlili@wind-mobi.com 20171202 -s for feature4915
#PRODUCT_LOCALES += th_TH vi_VN tl_PH hi_IN ar_EG ru_RU tr_TR pt_BR bn_IN mr_IN ta_IN te_IN zh_HK \
#        in_ID my_MM km_KH sw_KE uk_UA pl_PL sr_RS sl_SI fa_IR kn_IN ml_IN ur_IN gu_IN or_IN
#add by yinlili@wind-mobi.com 20171202 -e for feature4915

# When can normal compile this module, need module owner enable below commands
# Add the overlay path
#PRODUCT_PACKAGE_OVERLAYS := $(QCPATH)/qrdplus/Extension/res \
#        $(QCPATH)/qrdplus/globalization/multi-language/res-overlay \
#        $(PRODUCT_PACKAGE_OVERLAYS)
#PRODUCT_PACKAGE_OVERLAYS := $(QCPATH)/qrdplus/Extension/res \
        $(PRODUCT_PACKAGE_OVERLAYS)

# Powerhint configuration file
PRODUCT_COPY_FILES += \
     device/wind/E300L_WW/powerhint.xml:system/etc/powerhint.xml

#Healthd packages
PRODUCT_PACKAGES += android.hardware.health@1.0-impl \
                   android.hardware.health@1.0-convert \
                   android.hardware.health@1.0-service \
                   libhealthd.msm

PRODUCT_FULL_TREBLE_OVERRIDE := true

PRODUCT_VENDOR_MOVE_ENABLED := true

#for android_filesystem_config.h
PRODUCT_PACKAGES += \
    fs_config_files

# Sensor HAL conf file
 PRODUCT_COPY_FILES += \
     device/wind/E300L_WW/sensors/hals.conf:$(TARGET_COPY_OUT_VENDOR)/etc/sensors/hals.conf

PRODUCT_SUPPORTS_VERITY := true
PRODUCT_SYSTEM_VERITY_PARTITION := /dev/block/bootdevice/by-name/system
ifeq ($(ENABLE_VENDOR_IMAGE), true)
PRODUCT_VENDOR_VERITY_PARTITION := /dev/block/bootdevice/by-name/vendor
endif

# Enable logdumpd service only for non-perf bootimage
ifeq ($(findstring perf,$(KERNEL_DEFCONFIG)),)
    ifeq ($(TARGET_BUILD_VARIANT),user)
        PRODUCT_DEFAULT_PROPERTY_OVERRIDES+= \
            ro.logdumpd.enabled=0
    else
        #PRODUCT_DEFAULT_PROPERTY_OVERRIDES+= \
            ro.logdumpd.enabled=1
    endif
else
    PRODUCT_DEFAULT_PROPERTY_OVERRIDES+= \
        ro.logdumpd.enabled=0
endif

# Vibrator
PRODUCT_PACKAGES += \
    android.hardware.vibrator@1.0-impl \
    android.hardware.vibrator@1.0-service

# Power
PRODUCT_PACKAGES += \
    android.hardware.power@1.0-service \
    android.hardware.power@1.0-impl

PRODUCT_PACKAGES += \
    android.hardware.usb@1.0-service
# Added by leadcore fingerprint	
PRODUCT_PACKAGES += \
    android.hardware.biometrics.fingerprint@2.1-service

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.fingerprint.xml:system/etc/permissions/android.hardware.fingerprint.xml
	
#modifed by liqiang@wind-mobi.com on 20171220 begin
ifneq ($(TARGET_BUILD_VARIANT),user)
	PRODUCT_PACKAGES += WindDiagProcess
	PRODUCT_PACKAGES += PhoneInfoTest
endif
#modifed by  liqiang@wind-mobi.com on 20171018 end

#modifed by  chenyangqing@wind-mobi.com on 20180111 end
PRODUCT_COPY_FILES += \
    device/common/fingerprint/leadcore/ca/fpsensor_fingerprint.default.so:vendor/lib64/hw/fpsensor_fingerprint.default.so \
    device/common/fingerprint/leadcore/ca/fp_ext_svc2.so:vendor/lib64/fp_ext_svc2.so \
    device/common/fingerprint/leadcore/ca/android.vendor.fpsensorhidlsvc@2.0.so:vendor/lib64/android.vendor.fpsensorhidlsvc@2.0.so \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b00:/vendor/etc/firmware/fngap64_8917.b00 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b01:/vendor/etc/firmware/fngap64_8917.b01 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b02:/vendor/etc/firmware/fngap64_8917.b02 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b03:/vendor/etc/firmware/fngap64_8917.b03 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b04:/vendor/etc/firmware/fngap64_8917.b04 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b05:/vendor/etc/firmware/fngap64_8917.b05 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.b06:/vendor/etc/firmware/fngap64_8917.b06 \
    device/common/fingerprint/leadcore/ta/fngap64_8917.mdt:/vendor/etc/firmware/fngap64_8917.mdt \
	device/common/fingerprint/leadcore/ta/fngap64_8937.b00:/vendor/etc/firmware/fngap64_8937.b00 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.b01:/vendor/etc/firmware/fngap64_8937.b01 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.b02:/vendor/etc/firmware/fngap64_8937.b02 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.b03:/vendor/etc/firmware/fngap64_8937.b03 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.b04:/vendor/etc/firmware/fngap64_8937.b04 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.b05:/vendor/etc/firmware/fngap64_8937.b05 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.b06:/vendor/etc/firmware/fngap64_8937.b06 \
    device/common/fingerprint/leadcore/ta/fngap64_8937.mdt:/vendor/etc/firmware/fngap64_8937.mdt
#modifed by  chenyangqing@wind-mobi.com on 20180111 end

# Camera configuration file. Shared by passthrough/binderized camera HAL
PRODUCT_PACKAGES += camera.device@3.2-impl
PRODUCT_PACKAGES += camera.device@1.0-impl
PRODUCT_PACKAGES += android.hardware.camera.provider@2.4-impl
# Enable binderized camera HAL
PRODUCT_PACKAGES += android.hardware.camera.provider@2.4-service

PRODUCT_PACKAGES += \
    vendor.display.color@1.0-service \
    vendor.display.color@1.0-impl

PRODUCT_PACKAGES += \
    libandroid_net \
    libandroid_net_32

#Enable Lights Impl HAL Compilation
PRODUCT_PACKAGES += android.hardware.light@2.0-impl

#Thermal
PRODUCT_PACKAGES += android.hardware.thermal@1.0-impl \
                    android.hardware.thermal@1.0-service

#set KMGK_USE_QTI_SERVICE to true to enable QTI KEYMASTER and GATEKEEPER HIDLs
ifeq ($(ENABLE_VENDOR_IMAGE), true)
KMGK_USE_QTI_SERVICE := true
endif

#Enable AOSP KEYMASTER and GATEKEEPER HIDLs
ifneq ($(KMGK_USE_QTI_SERVICE), true)
PRODUCT_PACKAGES += android.hardware.gatekeeper@1.0-impl \
                    android.hardware.gatekeeper@1.0-service \
                    android.hardware.keymaster@3.0-impl \
                    android.hardware.keymaster@3.0-service
endif

PRODUCT_PROPERTY_OVERRIDES += rild.libpath=/vendor/lib64/libril-qc-qmi-1.so

ifeq ($(ENABLE_AB),true)
#A/B related packages
PRODUCT_PACKAGES += update_engine \
                   update_engine_client \
                   update_verifier \
                   bootctrl.msm8937 \
                   brillo_update_payload \
                   android.hardware.boot@1.0-impl \
                   android.hardware.boot@1.0-service
#Boot control HAL test app
PRODUCT_PACKAGES_DEBUG += bootctl
endif

#lizusheng@wind-mobi.com 20171122 add start
PRODUCT_COPY_FILES += \
    vendor/ims/data/etc/features/customization-device.xml:system/etc/AsusSystemUIRes/customization-device.xml
#lizusheng@wind-mobi.com 20171122 add end

#zhangyanbin@wind-mobi.com 20171023 add for user debug start
PRODUCT_PROPERTY_OVERRIDES += ro.wind.enable_adb_user_build = 1
#zhangyanbin@wind-mobi.com 20171023 add for user debug end

#chenyangqing@wind-mobi.com 20171120 add for feature#4945 start
PRODUCT_PROPERTY_OVERRIDES += ro.hardware.fp_position = back
PRODUCT_PROPERTY_OVERRIDES += ro.hardware.fp_shape = round
#chenyangqing@wind-mobi.com 20171120 add for feature#4945 end

#lizusheng@wind-mobi.com 20171204 for Feature #4905 add start
PRODUCT_PROPERTY_OVERRIDES += persist.asus.qc.gcf=0
PRODUCT_PROPERTY_OVERRIDES += ro.asus.phone.ipcall = 0
PRODUCT_PROPERTY_OVERRIDES += ro.asus.network.types = 7
PRODUCT_PROPERTY_OVERRIDES += ro.asus.phone.dual_nt_sup = 3
#lizusheng@wind-mobi.com 20171204 for Feature #4905 add end

#huangyouzhong@wind-mobi.com 20171225 1130828 add start
PRODUCT_PROPERTY_OVERRIDES += ro.asus.phone.hac = 1
PRODUCT_PROPERTY_OVERRIDES += ro.asus.phone.sipcall = 1
#huangyouzhong@wind-mobi.com 20171225 1130828 add end

#xuyi@wind-mobi.com 20171102 add start
PRODUCT_PACKAGES += TpAutoTest
#xuyi@wind-mobi.com 20171102 add end
#zhangcong@wind-mobi.com add 2018/1/19 start
PRODUCT_PACKAGES += HimaxMPAP
#zhangcong@wind-mobi.com add 2018/1/19 end

# xuyi@wind-mobi.com 20171211 add for BugReporter begin
PRODUCT_COPY_FILES += device/wind/common/savelogs.sh:system/bin/savelogs.sh
PRODUCT_COPY_FILES += device/wind/common/savelogs_complete.sh:system/bin/savelogs_complete.sh

#PRODUCT_COPY_FILES += device/wind/common/logcat_service.sh:system/bin/logcat_service.sh
#PRODUCT_COPY_FILES += device/wind/common/logcatr_service.sh:system/bin/logcatr_service.sh
#PRODUCT_COPY_FILES += device/wind/common/logcate_service.sh:system/bin/logcate_service.sh
#PRODUCT_COPY_FILES += device/wind/common/checklogcat.sh:system/bin/checklogcat.sh
PRODUCT_COPY_FILES += device/wind/common/modemlog.sh:system/bin/modemlog.sh
PRODUCT_COPY_FILES += device/wind/common/Diag.cfg:system/etc/Diag.cfg
PRODUCT_COPY_FILES += device/wind/common/tcpdump:system/bin/tcpdump

PRODUCT_PROPERTY_OVERRIDES += persist.asus.mupload.enable=0 \
		persist.asus.autoupload.enable=0 \
		persist.asus.savelogs=0 \
		persist.asus.csc.3gupload=0 \
		persist.asus.csc.deletelog=0 \
		ro.build.asus.version=$(ASUSVERSION) \
		persist.asus.batterystats.dump=0
# xuyi@wind-mobi.com 20171211 add for BugReporter end

#qiancheng@wind-mobi.com 20171023 add start
PRODUCT_PACKAGES += MAFactoryTest
PRODUCT_PACKAGES += fpExtensionSvc2
PRODUCT_PACKAGES += FpFactoryO
PRODUCT_PACKAGES += libfccon
PRODUCT_PACKAGES += libmdtv_test_jni
PRODUCT_PACKAGES += libdevicedata

#gyroscope
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.sensor.gyroscope.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.gyroscope.xml \
#qiancehng@wind-mobi.com 20171023 add end

#xiejiaming@wind-mobi.com add feature 4444 s
PRODUCT_PACKAGES += WindRuntimeTest
PRODUCT_COPY_FILES += \
    vendor/wind/packages/apps/WindRuntimeTest/qmesa/QMESA_64:vendor/bin/QMESA_64 \
    vendor/wind/packages/apps/WindRuntimeTest/qmesa/qmesa.sh:vendor/bin/qmesa.sh
#xiejiaming@wind-mobi.com add feature 4444 e

#xiejiaming@wind-mobi.com add s
PRODUCT_PROPERTY_OVERRIDES += ro.wind.asus_contacts=1
PRODUCT_PROPERTY_OVERRIDES += asuscontacts.adn.init.done=1
#xiejiaming@wind-mobi.com add e

#xiejiaming@wind-mobi.com add widevine s
PRODUCT_PROPERTY_OVERRIDES += drm.service.enabled=true
PRODUCT_PACKAGES += com.google.widevine.software.drm.xml \
    com.google.widevine.software.drm
PRODUCT_PACKAGES += libwvdrmengine
#xiejiaming@wind-mobi.com add widevine e

#add by cenxingcan@wind-mobi.com 20171201 begin
## support Vowifi
QCOM_WFC_SUPPORT := yes
## Support VoLTE
QCOM_VOLTE_SUPPORT := yes
## Support ViLTE
QCOM_VILTE_SUPPORT := no
#add by cenxingcan@wind-mobi.com 20171201 end

#chenyangqing@wind-mobi.com 20171210 add for voucher key begin 
PRODUCT_PACKAGES += VerifyVouchers
PRODUCT_PACKAGES += libGVoucher
#chenyangqing@wind-mobi.com 20171210 add for voucher key begin

#add by chenyangqing@wind-mobi.com 20171211 begin
PRODUCT_PROPERTY_OVERRIDES += persist.asus.fp.wakeup_support=true
PRODUCT_PROPERTY_OVERRIDES += persist.asus.fp.wakeup=true
PRODUCT_PROPERTY_OVERRIDES += persist.sys.fp.navigation=1
#add by chenyangqing@wind-mobi.com 20171211 end

#yunbo@wind-mobi.com 20171211  feature5094 start
PRODUCT_PROPERTY_OVERRIDES += ro.build.batterymaster=1 \
                              ro.sys.batsafty=111111111111
#yunbo@wind-mobic.om 20171211 end

#zhaochonghuan@wind-mobi.com 20171213 add for Feature #4571 begin
PRODUCT_PACKAGES += AsusFMService
PRODUCT_PACKAGES += AsusFMRadio
PRODUCT_PACKAGES += com.asus.fm.xml
#zhaochonghuan@wind-mobi.com 20171213 add for Feature #4571 end

#songyan01@wind-mobi.com add 20171211 add for Feature #4543 start
PRODUCT_PACKAGES += SMMI
#songyan01@wind-mobi.com add 20171211 add for Feature #4543 end

# wangyan@wind-mobi.com 20180102 add feature#5854 start
PRODUCT_PACKAGES += hostapd_cmd
# wangyan@wind-mobi.com 20180102 add feature#5854 end

#wangyan@wind-mobi.com add 20171214 Feature #4857 start
#PRODUCT_COPY_FILES += \
#    device/common/audiowizard/libicepower_1.16_oreo.so:vendor/lib/soundfx/libicepower.so \
#    device/common/audiowizard/icesoundconfig.def:system/etc/icesoundconfig.def \
#    device/common/audiowizard/icesoundpresets.def:system/etc/icesoundpresets.def
#wangyan@wind-mobi.com add 20171214 Feature #4857 end
#liujianbo@wind-mobi.com 20171214 add for feature#7348 begin
PRODUCT_COPY_FILES += \
    device/wind/E300L_WW/privapp-permissions-E300L_WW.xml:system/etc/permissions/privapp-permissions-E300L_WW.xml
#liujianbo@wind-mobi.com 20171214 add for feature#7348 end

#zhangcong@wind-mobi.com add 2017/12/14 start
PRODUCT_PROPERTY_OVERRIDES += \
    wifi.version.driver=CNSS.PR.4.0-00422-M8953BAAAANAZW-1 \
    bt.version.driver=CNSS.PR.4.0-00422-M8953BAAAANAZW-1 \
    gps.version.driver=S.JO.3.0-00398-8937_GENNS_PACK-1
#zhangcong@wind-mobi.com add 2017/12/14 end

#liujianbo@wind-mobi.com 20171225 add for aptX begin
PRODUCT_COPY_FILES += \
    vendor/qcom/proprietary/bluetooth/aptXlib/libaptX_encoder.so:system/lib64/libaptX_encoder.so \
    vendor/qcom/proprietary/bluetooth/aptXlib/libaptXHD_encoder.so:system/lib64/libaptXHD_encoder.so \
    device/qcom/common/media/audio_policy.conf:system/etc/audio_policy.conf \
    hardware/qcom/audio/configs/common/audio_policy_configuration.xml:system/etc/audio_policy_configuration.xml
#liujianbo@wind-mobi.com 20171225 add for aptX end

#add by zuozhuang@wind-mobi.com 20170912 start
PRODUCT_COPY_FILES += device/wind/E300L_WW/wind_factory/libfat_tools.so:system/lib/libfat_tools.so
PRODUCT_COPY_FILES += device/wind/E300L_WW/wind_factory/libfat_tools64.so:system/lib64/libfat_tools.so
PRODUCT_COPY_FILES += frameworks/base/cmds/bootanimation/lib/libmylcdtest.so:system/lib/libmylcdtest.so
PRODUCT_COPY_FILES += frameworks/base/cmds/bootanimation/lib64/libmylcdtest.so:system/lib64/libmylcdtest.so
PRODUCT_COPY_FILES += device/wind/E300L_WW/wind_factory/mykeytest:vendor/bin/mykeytest
PRODUCT_PACKAGES += factory_auto_test \
    FactoryAutoTest
#add by zuozhuang@wind-mobi.com 20170912 end

