#include "PathTracker.h"

PathTracker::PathTracker() {
    _currentState = STATE_IDLE;
    _currentCommand = COMMAND_NO_COMMAND;
    _buffer = (byte*) malloc(MESSAGE_BUFFER);
    _bufferPos = 0;
    _buffer[_bufferPos] = 0;
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
    if (b) {
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

byte PathTracker::getByte(byte pos) {
    if (_bufferPos > pos){
        return _buffer[pos];
    }
    else{
        return 0;
    }
}

int PathTracker::getInt(byte pos) {
    if (_bufferPos > pos + 1){
        return *((int*)(_buffer + pos));
    }
    else{
        return 0;
    }
}

long PathTracker::getLong(byte pos) {
    if (_bufferPos > pos + 3){
        return *((long*)(_buffer + pos));
    }
    else{
        return 0;
    }
}

float PathTracker::getFloat(byte pos) {
    if (_bufferPos > pos + 3){
        return *((float*)(_buffer + pos));
    }
    else{
        return 0.0;
    }
}

char* PathTracker::getString(byte pos) {
    if (_bufferPos > pos){
        return (char*)(_buffer + pos);
    }
    else{
        return "";
    }
}
