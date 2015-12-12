#ifndef GpsData_h
#define GpsData_h

#include "Common.h"

class GpsData{
private:
    byte _latitude_degrees;
    float _latitude_minutes;
    byte _longitude_degrees;
    float _longitude_minutes;
    long _date;
    long _time;
    bool _active;

public:
    GpsData(bool active, long time, long date, byte lat_deg, float lat_min, byte long_deg, float long_min);
    ~GpsData();

    byte get_latitude_degrees();
    float get_latitude_minutes();
    byte get_longitude_degrees();
    float get_longitude_minutes();
    long get_date();
    long get_time();
    bool isActive();

};

#endif
