#include "GpsData.h"

JNIEXPORT jboolean JNICALL
        Java_com_pathtracker_android_bluetooth_GpsData_parseData(JNIEnv *env, jobject obj, jbyteArray bytes){

    jclass type = env->FindClass("com/pathtracker/android/bluetooth/GpsData");

    jfieldID latDegId = env->GetFieldID(type, "_latitudeDegrees", "B");
    jfieldID longDegId = env->GetFieldID(type, "_longitudeDegrees", "B");
    jfieldID latMinId = env->GetFieldID(type, "_latitudeMinutes", "F");
    jfieldID longMinId = env->GetFieldID(type, "_longitudeMinutes", "F");
    jfieldID timeId = env->GetFieldID(type, "_time", "I");
    jfieldID dateId = env->GetFieldID(type, "_date", "I");

    if (env->GetArrayLength(bytes) != GPS_DATA_SIZE){
        env->SetByteField(obj, latDegId, 0);
        env->SetByteField(obj, longDegId, 0);
        env->SetFloatField(obj, latMinId, 0.0F);
        env->SetFloatField(obj, longMinId, 0.0F);
        env->SetIntField(obj, timeId, 0);
        env->SetIntField(obj, dateId, 0);
        return (jboolean) false;
    }

    jboolean isCopy = (jboolean) false;
    jbyte *data = env->GetByteArrayElements(bytes, &isCopy);

    env->SetByteField(obj, latDegId, *(data + GPS_DATA_POS_LAT_DEG));
    env->SetByteField(obj, longDegId, *(data + GPS_DATA_POS_LONG_DEG));
    env->SetFloatField(obj, latMinId, *((jfloat*)(data + GPS_DATA_POS_LAT_MIN)));
    env->SetFloatField(obj, longMinId, *((jfloat*)(data + GPS_DATA_POS_LONG_MIN)));
    env->SetIntField(obj, timeId, *((jint*)(data + GPS_DATA_POS_TIME)));
    env->SetIntField(obj, dateId, *((jint*)(data + GPS_DATA_POS_DATE)));

    env->ReleaseByteArrayElements(bytes, data, 0);

    return (jboolean) true;
}
