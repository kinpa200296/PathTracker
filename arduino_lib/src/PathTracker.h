#ifndef PathTracker_h
#define PathTracker_h

#include "Common.h"

#include "GpsParser.h"
#include "DataParser.h"
#include "Command.h"
#include "State.h"
#include "Result.h"

#define WRITE_DELAY (unsigned long)500
#define MESSAGE_END (byte)0
#define MESSAGE_BUFFER 100

class PathTracker{
private:
    byte _currentState;
    byte _currentCommand;
    byte *_buffer;
    byte _bufferPos;
    bool _broadcastEnabled;

    unsigned long _parseULong(const char *str);
public:
    PathTracker();
    ~PathTracker();

    byte getState();
    byte getCommand();

    bool analyze(byte b);
    void resetCommand();

    byte getByte(byte offset);
    char* getString(byte offset);
    unsigned long getULong(byte offset);
    bool broadcastEnabled();
    void enableBroadcast();
    void disableBroadcast();
    void pause();
    void resume();
    void stop();
    bool paused();
    bool resumed();
    bool stopped();
};

#endif
