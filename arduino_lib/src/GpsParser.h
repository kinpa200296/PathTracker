#ifndef GpsReader_h
#define GpsReader_h

#include "Common.h"
#include "DataString.h"

#define TARGET_PREFIX "$GPRMC,"
#define PREFIX_LENGTH 7

class GpsParser{
private:
    DataString *_buffer;
    DataString *_prefix;
    bool _ready;
    bool _ignore;
    int _counter;
public:
    GpsParser(int buffer_length);
    ~GpsParser();

    void add(char ch);
    bool isReady();
    DataString* getBufferedData();
    void reset();
};

#endif
