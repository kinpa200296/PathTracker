#ifndef PathTracker_h
#define PathTracker_h

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

#include "Common.h"
#include "Result.h"
#include "Command.h"

#define MESSAGE_BUFFER 100

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandListPath(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandSendPath(JNIEnv *env, jclass type, jbyteArray bytes, jstring idStr);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandDeletePath(JNIEnv *env, jclass type, jbyteArray bytes, jstring idStr);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandNewPath(JNIEnv *env, jclass type, jbyteArray bytes, jstring name);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandEnableBroadcast(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandDisableBroadcast(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandPausePath(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandResumePath(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandStopPath(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jint JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_commandGetState(JNIEnv *env, jclass type, jbyteArray bytes);

JNIEXPORT jboolean JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_isMessageEnd(JNIEnv *env, jobject obj, jbyte b);

JNIEXPORT jstring JNICALL
        Java_com_pathtracker_android_bluetooth_PathTracker_bytesToString(JNIEnv *env, jobject obj, jbyteArray bytes);

#ifdef __cplusplus
}
#endif

#endif
