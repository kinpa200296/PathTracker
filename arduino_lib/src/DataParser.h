#ifndef DataParser_h
#define DataParser_h

#include "Common.h"
#include "DataString.h"
#include "GpsData.h"

#define NMEA_DATA_SEPARATOR ','

class DataParser{
private:
    char **_strings;

    byte _parse_byte(const char *str, int cnt);
    long _parse_long(const char *str);
    float _parse_float(const char *str);
public:
    DataParser(DataString *str);
    ~DataParser();

    GpsData* getData();
};

#endif
