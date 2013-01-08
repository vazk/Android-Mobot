#include "Communication.hpp"
#include "SocketManager.hpp"
#include "Command.hpp"

static SocketManager socketManager;

JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_connect(JNIEnv* env,jobject thiz, jstring ip, jint port)
{
    const char* ipPtr = env->GetStringUTFChars(ip, 0);
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

JNIEXPORT void JNICALL Java_com_mobot_CommunicationManager_commandQuit(JNIEnv* env, jobject thiz)
{
    Command cmd;
    cmd.data.type = Command::CMD_QUIT;
    socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH);
}

JNIEXPORT void JNICALL Java_com_mobot_CommunicationManager_commandStop(JNIEnv* env, jobject thiz)
{
    Command cmd;
    cmd.data.type = Command::CMD_DRIVE;
    cmd.data.drive.left = 0;
    cmd.data.drive.right = 0;
    socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH);
}

JNIEXPORT void JNICALL Java_com_mobot_CommunicationManager_commandDrive(JNIEnv* env, jobject thiz, jfloat left, jfloat right)
{
    Command cmd;
    cmd.data.type = Command::CMD_DRIVE;
    cmd.data.drive.left = left * 255;
    cmd.data.drive.right = right * 255;
    socketManager.write(cmd.data.raw, Command::COMMAND_PACKET_LENGTH);
}
