#include "DataParser.h"

DataParser::DataParser(DataString *str) {
    _strings = str->split(NMEA_DATA_SEPARATOR);
}

DataParser::~DataParser() {
    free(_strings);
}

GpsData* DataParser::getData() {
    byte latDeg = 0, longDeg = 0;
    float latMin = 0.0, longMin = 0.0;
    long date = 0, time = 0;
    bool active;

    if (_strings[1][0] == 'A'){
        active = true;
    }
    else{
        active = false;
    }

    if (strlen(_strings[0]) != 0){
        _strings[0][6] = 0;
        time = _parseLong(_strings[0]);
    }

    if (strlen(_strings[8]) != 0){
        date = _parseLong(_strings[8]);
    }

    if (strlen(_strings[2]) != 0){
        latDeg = _parseByte(_strings[2], 2);
        latMin = _parseFloat(_strings[2] + 2);
    }

    if (strlen(_strings[4]) != 0){
        longDeg = _parseByte(_strings[4], 3);
        longMin = _parseFloat(_strings[4] + 3);
    }

    if (strlen(_strings[3]) != 0){
        if (_strings[3][0] != 'N'){
            latDeg = -latDeg;
        }
    }

    if (strlen(_strings[5]) != 0){
        if (_strings[5][0] != 'E'){
            longDeg = -longDeg;
        }
    }

    return new GpsData(active, time, date, latDeg, latMin, longDeg, longMin);
}

long DataParser::_parseLong(const char *str) {
    long res = 0;
    long tmp = 1;
    for (int i = strlen(str); i > 0; i--){
        res += (str[i - 1] - 48) * tmp;
        tmp *= 10;
    }
    return res;
}

byte DataParser::_parseByte(const char *str, int cnt) {
    byte res = 0;
    byte tmp = 1;
    for (; cnt > 0; cnt--){
        res += (str[cnt - 1] - 48) * tmp;
        tmp *= 10;
    }
    return res;
}

float DataParser::_parseFloat(const char *str) {
    float res = 0.0;
    float tmp = 1.0;
    res += 10.0 * (str[0] - 48) + str[1] - 48;
    for (int i = 3; str[i]; i++){
        tmp /= 10.0;
        res += (str[i] - 48) * tmp;
    }
    return res;
}
