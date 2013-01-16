#ifndef ROBOT_DRIVER_HPP
#define ROBOT_DRIVER_HPP

#include "Command.hpp"
#include "SocketManager.hpp"
#include <jni.h>
#include <pthread.h>
#include <string>


class RobotDriver
{
public:
    RobotDriver();
    bool start(const std::string& ip, int port);
    void stop();
    bool isRunning();

    bool commandDrive(float left, float right);

private:
    bool startHeartbeat();
    void stopHeartbeat();
    void setConnectionData(const std::string& ip, int port);
    bool sendHeartbitCommand();

    static void* heartbeatFunc(void*);

private:
    bool            mShouldStop;
    SocketManager   mSocketManager;
    pthread_mutex_t mMutex;
    pthread_t       mHeartbeatThread;
    std::string     mIP;
    int             mPort;
    static const int HEARTBEAT_TIMEOUT_MS = 150;
    static const int RECONNECT_TIMEOUT_MS = 300;
};

extern "C" {
    JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_init(JNIEnv* env, jobject thiz);
    JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_start(JNIEnv* env, jobject thiz, jstring ip, jint port);
    JNIEXPORT void JNICALL Java_com_mobot_RobotDriver_stop(JNIEnv* env, jobject thiz);
    JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_isRunning(JNIEnv* env, jobject thiz);
    JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_commandDrive(JNIEnv* env, jobject thiz, jfloat left, jfloat right);
}

#endif // ROBOT_DRIVER_HPP
