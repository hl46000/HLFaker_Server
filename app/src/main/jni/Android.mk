# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CPP_FEATURES := exceptions
LOCAL_MODULE := scrtData
LOCAL_CFLAGS := -fpermissive -fno-rtti -fno-exceptions
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_SRC_FILES := security/securityData.cpp
LOCAL_LDLIBS := -ldl -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_CPP_FEATURES := exceptions
LOCAL_MODULE := hlhooker
LOCAL_CFLAGS := -fpermissive -fno-rtti -fno-exceptions
LOCAL_CFLAGS += -fvisibility=hidden -fvisibility-inlines-hidden -std=c++1y
LOCAL_CFLAGS += -g0 -O3 -fomit-frame-pointer
LOCAL_LDFLAGS += -Wl,--strip-all
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_SRC_FILES := hlhooker/hlhooker.cpp
LOCAL_SHARED_LIBRARIES := AK
LOCAL_LDLIBS := -ldl -llog
#LOCAL_LDLIBS += -L$(LOCAL_PATH)/../../../../app/src/main/jniLibs/$(TARGET_ARCH_ABI)/lib$(LOCAL_SHARED_LIBRARIES)$(TARGET_SONAME_EXTENSION)
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := AK
LOCAL_SRC_FILES         := $(LOCAL_PATH)/../jniLibs/$(TARGET_ARCH_ABI)/lib$(LOCAL_MODULE)$(TARGET_SONAME_EXTENSION)
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_EXPORT_LDLIBS     := -llog
include $(PREBUILT_SHARED_LIBRARY)
