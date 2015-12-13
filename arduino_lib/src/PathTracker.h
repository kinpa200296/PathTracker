#ifndef PathTracker_h
#define PathTracker_h

#include "Common.h"

#include "GpsParser.h"
#include "DataParser.h"
#include "Command.h"
#include "State.h"
#include "Result.h"

#define WRITE_DELAY (unsigned long)500
#define MESSAGE_END 0
#define MESSAGE_BUFFER 100

class PathTracker{
private:
    byte _currentState;
    byte _currentCommand;
    byte *_buffer;
    byte _bufferPos;
public:
    PathTracker();
    ~PathTracker();

    byte getState();
    byte getCommand();

    bool analyze(byte b);
    void resetCommand();

    byte getByte(byte pos);
    int getInt(byte pos);
    float getFloat(byte pos);
    long getLong(byte pos);
    char* getString(byte pos);
};

#endif
