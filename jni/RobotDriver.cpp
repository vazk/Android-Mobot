#include "RobotDriver.hpp"
#include "Logger.hpp"
#include <unistd.h>

RobotDriver* gRobot = NULL;

RobotDriver::RobotDriver()
 : mState(STOPPED)
{
    Logger::initialize(LDEBUG);
    pthread_mutex_init(&mMutex, NULL);
    LOG(LINFO)<<"RD: ctor"<<std::endl;
}    

bool
RobotDriver::start(const std::string& ip, int port)
{
    LOG(LINFO)<<"RD: starting"<<std::endl;
    pthread_mutex_lock(&mMutex);
    gRobot->setConnectionData(ip, port);
    bool status = startHeartbeat();
    pthread_mutex_unlock(&mMutex);
    return status;
}

void
RobotDriver::stop()
{
    LOG(LINFO)<<"RD: stopping"<<std::endl;
    pthread_mutex_lock(&mMutex);
    stopHeartbeat();
    mSocketManager.close();
    pthread_mutex_unlock(&mMutex);
}

void*
RobotDriver::heartbeatFunc(void* ptr)
{
    RobotDriver* rd = (RobotDriver*)ptr;
    //rd->mShouldStop = false;
    //while(!rd->mShouldStop) {
    rd->mState = RUNNING;
    while(rd->mState == RUNNING) {

        if(!rd->mSocketManager.isConnected()) {
            LOG(LINFO)<<"HB: trying to (re)connect"<<std::endl;
            rd->mSocketManager.connectToServer(rd->mIP, rd->mPort);
            usleep(RobotDriver::RECONNECT_TIMEOUT_MS * 1000);
            continue;
            
        }
        LOG(LINFO)<<"HB: sending a ping"<<std::endl;
        rd->sendHeartbitCommand();
        usleep(RobotDriver::HEARTBEAT_TIMEOUT_MS * 1000);
    }
    rd->mState = STOPPED;
    return NULL;
}

bool
RobotDriver::startHeartbeat() 
{
    if(mState == RUNNING) {
        LOG(LINFO)<<"RD: heartbeat is already started."<<std::endl;
        return true;
    }
    LOG(LINFO)<<"RD: starting heartbeat"<<std::endl;
    if(pthread_create(&mHeartbeatThread, NULL, heartbeatFunc, this)) {
        LOG(LERROR)<<"failed to create the heartbeat thread..."<<std::endl;
	    return false;
    }
    return true;
}

void
RobotDriver::stopHeartbeat() 
{
    LOG(LINFO)<<"RD: stopping heartbeat"<<std::endl;
    //mShouldStop = true;
    mState = STOP_REQUESTED;
    pthread_join(mHeartbeatThread, NULL);
}

bool
RobotDriver::isRunning() 
{
    pthread_mutex_lock(&mMutex);
    bool status = mSocketManager.isConnected();
    pthread_mutex_unlock(&mMutex);
    return status;
}

void
RobotDriver::setConnectionData(const std::string& ip, int port)
{
    mIP = ip;
    mPort = port;
}

bool
RobotDriver::sendHeartbitCommand()
{
    Command cmd;
    cmd.data.type = Command::CMD_HEARTBEAT;
    mSocketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH);
    return mSocketManager.isConnected();
}

bool
RobotDriver::commandDrive(float left, float right)
{
    Command cmd;
    cmd.data.type = Command::CMD_DRIVE;
    cmd.data.drive.left = left * 255;
    cmd.data.drive.right = right * 255;
    mSocketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH);
    return mSocketManager.isConnected();
}







JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_init(JNIEnv* env, jobject thiz)
{
    if(gRobot != NULL) {
        delete gRobot;
    }
    gRobot = new RobotDriver();
}


JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_start(JNIEnv* env, jobject thiz, jstring ip, jint port)
{
    if(gRobot == NULL) {
        LOG(LERROR)<<"RD: RobotDriver is not initialized! Quitting!"<<std::endl;
        return false;
    }
    const char* ipPtr = env->GetStringUTFChars(ip, 0);
    bool status = gRobot->start(ipPtr, port);
    env->ReleaseStringUTFChars(ip, ipPtr);
    return status;
}

JNIEXPORT void JNICALL Java_com_mobot_RobotDriver_stop(JNIEnv* env, jobject thiz)
{
    if(gRobot == NULL) {
        LOG(LERROR)<<"RD: RobotDriver is not initialized! Quitting!"<<std::endl;
        return;
    }
    gRobot->stop();
}

JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_isRunning(JNIEnv* env, jobject thiz)
{
    if(gRobot == NULL) {
        LOG(LERROR)<<"RD: RobotDriver is not initialized! Quitting!"<<std::endl;
        return false;
    }
    gRobot->isRunning();
}


JNIEXPORT bool JNICALL Java_com_mobot_RobotDriver_commandDrive(JNIEnv* env, jobject thiz, jfloat left, jfloat right)
{
    if(gRobot == NULL) {
        LOG(LERROR)<<"RD: RobotDriver is not initialized! Quitting!"<<std::endl;
        return false;
    }
    gRobot->commandDrive(left, right);
}
