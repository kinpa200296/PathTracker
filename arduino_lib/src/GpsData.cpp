#include "GpsData.h"

GpsData::GpsData(bool active, long time, long date, byte lat_deg, float lat_min, byte long_deg, float long_min) {
    _active = active;
    _time = time;
    _date = date;
    _latitude_degrees = lat_deg;
    _latitude_minutes = lat_min;
    _longitude_degrees = long_deg;
    _longitude_minutes = long_min;
}

byte GpsData::get_latitude_degrees() {
    return _latitude_degrees;
}

float GpsData::get_latitude_minutes() {
    return _latitude_minutes;
}

byte GpsData::get_longitude_degrees() {
    return _longitude_degrees;
}

float GpsData::get_longitude_minutes() {
    return _longitude_minutes;
}

bool GpsData::isActive() {
    return _active;
}

long GpsData::get_time() {
    return _time;
}

long GpsData::get_date() {
    return _date;
}
