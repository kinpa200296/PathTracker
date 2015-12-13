#ifndef GpsData_h
#define GpsData_h

#include "Common.h"

#define GPS_DATA_SIZE 18
#define GPS_DATA_POS_LAT_DEG 0
#define GPS_DATA_POS_LAT_MIN 1
#define GPS_DATA_POS_LONG_DEG 5
#define GPS_DATA_POS_LONG_MIN 6
#define GPS_DATA_POS_TIME 10
#define GPS_DATA_POS_DATE 14

class GpsData{
private:
    byte _latitudeDegrees;
    float _latitudeMinutes;
    byte _longitudeDegrees;
    float _longitudeMinutes;
    long _date;
    long _time;
    bool _active;

    GpsData();

public:
    GpsData(bool active, long time, long date, byte latDeg, float latMin, byte longDeg, float longMin);
    ~GpsData();

    byte getLatitudeDegrees();
    float getLatitudeMinutes();
    byte getLongitudeDegrees();
    float getLongitudeMinutes();
    long getDate();
    long getTime();
    bool isActive();

    byte* toBytes();
    static GpsData* fromBytes(byte* data);
};

#endif
