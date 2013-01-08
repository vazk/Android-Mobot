LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include $(CLEAR_VARS)
LOCAL_MODULE    := RobotComm
LOCAL_SRC_FILES := Communication.cpp \
                   SocketManager.cpp
LOCAL_LDLIBS    := -llog 
LOCAL_C_INCLUDES       := external/include jni/external/include

include $(BUILD_SHARED_LIBRARY)
