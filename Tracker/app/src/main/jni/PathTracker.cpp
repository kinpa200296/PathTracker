#include "PathTracker.h"

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandListPath(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_LIST_PATHS;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandSendPath(JNIEnv *env, jclass type, jbyteArray bytes, jstring idStr){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_SEND_PATH;
    data[msgLen++] = 0;
    jboolean copy = (jboolean) true;
    const char *str = env->GetStringUTFChars(idStr, &copy);
    for (int i = 0; str[i]; i++){
        data[msgLen++] = str[i];
    }
    env->ReleaseStringUTFChars(idStr, str);

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandDeletePath(JNIEnv *env, jclass type, jbyteArray bytes, jstring idStr){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_DELETE_PATH;
    data[msgLen++] = 0;
    jboolean copy = (jboolean) true;
    const char *str = env->GetStringUTFChars(idStr, &copy);
    for (int i = 0; str[i]; i++){
        data[msgLen++] = str[i];
    }
    env->ReleaseStringUTFChars(idStr, str);

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandNewPath(JNIEnv *env, jclass type, jbyteArray bytes, jstring name){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_NEW_PATH;
    data[msgLen++] = 0;
    jboolean copy = (jboolean) true;
    const char *str = env->GetStringUTFChars(name, &copy);
    for (int i = 0; str[i]; i++){
        data[msgLen++] = str[i];
    }
    env->ReleaseStringUTFChars(name, str);

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandEnableBroadcast(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_ENABLE_BROADCAST;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandDisableBroadcast(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_DISABLE_BROADCAST;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandPausePath(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_PAUSE_PATH;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandResumePath(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_RESUME_PATH;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandStopPath(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_STOP_PATH;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jint JNICALL
Java_com_pathtracker_android_bluetooth_PathTracker_commandGetState(JNIEnv *env, jclass type, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    int msgLen = 0;
    data[msgLen++] = COMMAND_GET_STATE;
    data[msgLen++] = 0;

    env->ReleaseByteArrayElements(bytes, data, 0);

    data[1] = (jbyte) (msgLen - 2);
    return msgLen;
}

JNIEXPORT jstring JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_bytesToString(JNIEnv *env, jobject obj, jbyteArray bytes){
    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    jstring res = env->NewStringUTF((char*)data);

    env->ReleaseByteArrayElements(bytes, data, 0);

    return res;
}
