#include "PathTracker.h"

PathTracker::PathTracker() {
    _currentState = STATE_IDLE;
    _currentCommand = COMMAND_NO_COMMAND;
    _buffer = (byte*) malloc(MESSAGE_BUFFER);
    _bufferPos = 0;
    _buffer[_bufferPos] = 0;
    _broadcastEnabled = false;
}

PathTracker::~PathTracker() {
    free(_buffer);
}

byte PathTracker::getState() {
    return _currentState;
}

byte PathTracker::getCommand() {
    return _currentCommand;
}

bool PathTracker::analyze(byte b) {
    if (b != MESSAGE_END) {
        if (_currentCommand == COMMAND_NO_COMMAND) {
            _bufferPos = 0;
            _buffer[_bufferPos] = 0;
            _currentCommand = b;
        }
        else if (_bufferPos < MESSAGE_BUFFER) {
            _buffer[_bufferPos] = b;
            _bufferPos++;
            _buffer[_bufferPos] = 0;
        }
        return false;
    }
    return true;
}

void PathTracker::resetCommand() {
    _currentCommand = COMMAND_NO_COMMAND;
}

byte PathTracker::getByte(byte offset) {
    if (_bufferPos > offset){
        return _buffer[offset];
    }
    else{
        return 0;
    }
}

char* PathTracker::getString(byte offset) {
    if (_bufferPos > offset){
        return (char*)(_buffer + offset);
    }
    else{
        return "";
    }
}

unsigned long PathTracker::getULong(byte offset) {
    return _parseULong((char*)(_buffer + offset));
}

unsigned long PathTracker::_parseULong(const char *str) {
    unsigned long res = 0;
    unsigned long tmp = 1;
    int len = 0;
    for (; str[len]; len++);
    for (int i = len; i > 0; i--){
        res += (str[i - 1] - 48) * tmp;
        tmp *= 10;
    }
    return res;
}

bool PathTracker::broadcastEnabled() {
    return _broadcastEnabled;
}

void PathTracker::enableBroadcast() {
    _broadcastEnabled = true;
}

void PathTracker::disableBroadcast() {
    _broadcastEnabled = false;
}

void PathTracker::pause() {
    _currentState = STATE_WAITING;
}

void PathTracker::stop() {
    _currentState = STATE_IDLE;
}

void PathTracker::resume() {
    _currentState = STATE_RECORDING;
}

bool PathTracker::paused() {
    return _currentState == STATE_WAITING;
}

bool PathTracker::resumed() {
    return _currentState == STATE_RECORDING;
}

bool PathTracker::stopped() {
    return _currentState == STATE_IDLE;
}
