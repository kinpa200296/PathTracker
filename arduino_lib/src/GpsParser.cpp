#include "GpsParser.h"

GpsParser::GpsParser(int buffer_length) {
    _buffer = new DataString(buffer_length);
    _prefix = new DataString(PREFIX_LENGTH);

    _ready = false;
    _ignore = false;
    _counter = 0;
}

GpsParser::~GpsParser(){
    delete _buffer;
    delete _prefix;
}

void GpsParser::add(char ch) {
    _counter++;
    if(_prefix->length() < PREFIX_LENGTH && !_ignore){
        _prefix->append(ch);
    }
    if (_prefix->length() == PREFIX_LENGTH && _buffer->length() == 0){
        if (_prefix->equals(TARGET_PREFIX)){
            _ignore = false;
        }
        else{
            _ignore = true;
            _prefix->clear();
        }
        if (_counter == PREFIX_LENGTH){
            return;
        }
    }
    if (_prefix->length() == PREFIX_LENGTH && !_ignore){
        _buffer->append(ch);
    }
    if (ch == '\n'){
        if (!_ignore){
            _ready = true;
        }
        _prefix->clear();
        _ignore = false;
        _counter = 0;
    }
}

bool GpsParser::isReady() {
    return _ready;
}

DataString* GpsParser::getBufferedData() {
    if (_ready) {
        return _buffer;
    }
}

void GpsParser::reset() {
    _ready = false;
    _ignore = false;
    _buffer->clear();
    _prefix->clear();
}
