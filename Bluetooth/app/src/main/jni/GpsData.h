#ifndef GpsData_h
#define GpsData_h

#include <jni.h>

#include "Common.h"

#ifdef __cplusplus
extern "C" {
#endif

#define GPS_DATA_SIZE 18
#define GPS_DATA_POS_LAT_DEG 0
#define GPS_DATA_POS_LAT_MIN 1
#define GPS_DATA_POS_LONG_DEG 5
#define GPS_DATA_POS_LONG_MIN 6
#define GPS_DATA_POS_TIME 10
#define GPS_DATA_POS_DATE 14

JNIEXPORT jboolean JNICALL
        Java_com_pathtracker_android_bluetooth_GpsData_parseData(JNIEnv *env, jobject obj, jbyteArray bytes);

#ifdef __cplusplus
}
#endif

#endif
