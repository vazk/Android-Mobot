#include "Communication.hpp"
#include "SocketManager.hpp"
#include "Command.hpp"
#include <iostream>

SocketManager socketManager;

JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_connect(JNIEnv* env, jobject thiz, jstring ip, jint port)
{
    const char* ipPtr = env->GetStringUTFChars(ip, 0);
    std::cout<<"ipPtr: "<<ipPtr<<std::endl;
    std::cout<<"port: "<<port<<std::endl;
    bool status = socketManager.connectToServer(ipPtr, port);
    env->ReleaseStringUTFChars(ip, ipPtr);

    return status;
}

JNIEXPORT int JNICALL Java_com_mobot_CommunicationManager_write(JNIEnv* env, jobject thiz, jbyteArray data, jint size)
{
    return 0;
}

JNIEXPORT int JNICALL Java_com_mobot_CommunicationManager_read(JNIEnv* env, jobject thiz, jbyteArray data, jint size)
{
    return 0;
}

JNIEXPORT void JNICALL Java_com_mobot_CommunicationManager_close(JNIEnv* env, jobject thiz)
{
}

JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_commandAck(JNIEnv* env, jobject thiz)
{
    Command cmd;
    cmd.data.type = Command::CMD_ACK;
    return socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH) == Command::COMMAND_PACKET_LENGTH;
}

JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_commandQuit(JNIEnv* env, jobject thiz)
{
    Command cmd;
    cmd.data.type = Command::CMD_QUIT;
    return socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH) == Command::COMMAND_PACKET_LENGTH;
}

JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_commandStop(JNIEnv* env, jobject thiz)
{
    Command cmd;
    cmd.data.type = Command::CMD_DRIVE;
    cmd.data.drive.left = 0;
    cmd.data.drive.right = 0;
    return socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH) == Command::COMMAND_PACKET_LENGTH;
}

JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_commandDrive(JNIEnv* env, jobject thiz, jfloat left, jfloat right)
{
    Command cmd;
    cmd.data.type = Command::CMD_DRIVE;
    cmd.data.drive.left = left * 255;
    cmd.data.drive.right = right * 255;
    return socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH) == Command::COMMAND_PACKET_LENGTH;
}
