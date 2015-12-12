#include "DataParser.h"

DataParser::DataParser(DataString *str) {
    _strings = str->split(NMEA_DATA_SEPARATOR);
}

DataParser::~DataParser() {
    free(_strings);
}

GpsData* DataParser::getData() {
    byte lat_deg, long_deg;
    float lat_min, long_min;
    long date, time;
    bool active;

    if (_strings[1][0] == 'A'){
        active = true;
    }
    else{
        active = false;
    }

    if (strlen(_strings[0]) != 0){
        _strings[0][6] = 0;
        time = _parse_long(_strings[0]);
    }

    if (strlen(_strings[8]) != 0){
        date = _parse_long(_strings[8]);
    }

    if (strlen(_strings[2]) != 0){
        lat_deg = _parse_byte(_strings[2], 2);
        lat_min = _parse_float(_strings[2] + 3);
    }

    if (strlen(_strings[4]) != 0){
        long_deg = _parse_byte(_strings[4], 3);
        long_min = _parse_float(_strings[4] + 3);
    }

    if (strlen(_strings[3]) != 0){
        if (_strings[3][0] != 'N'){
            lat_deg = -lat_deg;
        }
    }

    if (strlen(_strings[5]) != 0){
        if (_strings[5][0] != 'E'){
            long_deg = -long_deg;
        }
    }

    return new GpsData(active, time, date, lat_deg, lat_min, long_deg, long_min);
}

long DataParser::_parse_long(const char *str) {
    long res = 0;
    for (int i = strlen(str); i > 0; i--){
        res *= 10;
        res += str[i - 1] - 48;
    }
    return res;
}

byte DataParser::_parse_byte(const char *str, int cnt) {
    byte res = 0;
    for (; cnt > 0; cnt--){
        res *= 10;
        res += str[cnt - 1] - 48;
    }
}

float DataParser::_parse_float(const char *str) {
    float res = 0.0;
    float tmp = 1.0;
    res += 10.0 * (str[0] - 48) + str[1] - 48;
    for (int i = 3; str[i]; i++){
        tmp /= 10.0;
        res += tmp * (str[i] - 48);
    }
    return res;
}
