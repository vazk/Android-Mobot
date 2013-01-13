#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <setjmp.h>

#define  LOG_TAG    "TEST"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C" {

    JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_connect(JNIEnv* env, jobject thiz, jstring ip, jint port);
    JNIEXPORT int  JNICALL Java_com_mobot_CommunicationManager_write(JNIEnv* env, jobject thiz, jbyteArray data, jint size);
    JNIEXPORT int  JNICALL Java_com_mobot_CommunicationManager_read(JNIEnv* env, jobject thiz, jbyteArray da, jint size);
    JNIEXPORT void JNICALL Java_com_mobot_CommunicationManager_close(JNIEnv* env, jobject thiz);
    JNIEXPORT bool JNICALL Java_com_mobot_CommunicationManager_isConnected(JNIEnv* env, jobject thiz);


    JNIEXPORT bool Java_com_mobot_CommunicationManager_commandHeartBeat(JNIEnv* env, jobject thiz);
    JNIEXPORT bool Java_com_mobot_CommunicationManager_commandAck(JNIEnv* env, jobject thiz);
    JNIEXPORT bool Java_com_mobot_CommunicationManager_commandQuit(JNIEnv* env, jobject thiz);
    JNIEXPORT bool Java_com_mobot_CommunicationManager_commandStop(JNIEnv* env, jobject thiz);
    JNIEXPORT bool Java_com_mobot_CommunicationManager_commandDrive(JNIEnv* env, jobject thiz, jfloat left, jfloat right);

}
