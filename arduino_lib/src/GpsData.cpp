#include "GpsData.h"

GpsData::GpsData() {
    _active = false;
    _time = 0;
    _date = 0;
    _latitudeDegrees = 0;
    _latitudeMinutes = 0.0;
    _longitudeDegrees = 0;
    _longitudeMinutes = 0.0;
}

GpsData::GpsData(bool active, long time, long date, byte latDeg, float latMin, byte longDeg, float longMin) {
    _active = active;
    _time = time;
    _date = date;
    _latitudeDegrees = latDeg;
    _latitudeMinutes = latMin;
    _longitudeDegrees = longDeg;
    _longitudeMinutes = longMin;
}

GpsData::~GpsData() { }

byte GpsData::getLatitudeDegrees() {
    return _latitudeDegrees;
}

float GpsData::getLatitudeMinutes() {
    return _latitudeMinutes;
}

byte GpsData::getLongitudeDegrees() {
    return _longitudeDegrees;
}

float GpsData::getLongitudeMinutes() {
    return _longitudeMinutes;
}

bool GpsData::isActive() {
    return _active;
}

long GpsData::getTime() {
    return _time;
}

long GpsData::getDate() {
    return _date;
}

byte* GpsData::toBytes() {
    byte *res = (byte*) malloc(GPS_DATA_SIZE);

    *(res + GPS_DATA_POS_LAT_DEG) = _latitudeDegrees;
    *((float*)(res + GPS_DATA_POS_LAT_MIN)) = _latitudeMinutes;
    *(res + GPS_DATA_POS_LONG_DEG) = _longitudeDegrees;
    *((float*)(res + GPS_DATA_POS_LONG_MIN)) = _longitudeMinutes;
    *((long*)(res + GPS_DATA_POS_TIME)) = _time;
    *((long*)(res + GPS_DATA_POS_DATE)) = _date;

    return res;
}

GpsData* GpsData::fromBytes(byte *data) {
    GpsData *res = new GpsData();

    res->_latitudeDegrees = *(data + GPS_DATA_POS_LAT_DEG);
    res->_latitudeMinutes = *((float*)(data + GPS_DATA_POS_LAT_MIN));
    res->_longitudeDegrees = *(data + GPS_DATA_POS_LONG_DEG);
    res->_longitudeMinutes = *((float*)(data + GPS_DATA_POS_LONG_MIN));
    res->_time = *((long*)(data + GPS_DATA_POS_TIME));
    res->_date = *((long*)(data + GPS_DATA_POS_DATE));

    return res;
}
