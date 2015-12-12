#include "DataString.h"

DataString::DataString(int max_length) {
    _max_length = max_length + 1;
    _data = (char*) malloc(max_length + 1);
    _current_length = 0;
    _data[_current_length] = 0;
}

DataString::~DataString() {
    free(_data);
}

int DataString::length() {
    return _current_length;
}

int DataString::max_length() {
    return _max_length;
}

void DataString::clear() {
    _current_length = 0;
    _data[_current_length] = 0;
}

void DataString::append(char ch) {
    _data[_current_length] = ch;
    _current_length++;
    if (_current_length > _max_length) {
        _current_length = _max_length;
    }
    _data[_current_length] = 0;
}

bool DataString::equals(const char *other) {
    for (char *str = _data; *str || *other; str++, other++){
        if (*str != *other){
            return false;
        }
    }
    return true;
}

const char* DataString::getData() {
    return _data;
}

char** DataString::split(char separator) {
    int cnt = 0;
    for (char *ch = _data; *ch; ch++){
        if (*ch == separator){
            cnt++;
        }
    }
    char **res = (char**)malloc(cnt* sizeof(char*));
    cnt = 0;
    res[cnt] = _data;
    for (char *ch = _data; *ch; ch++){
        if (*ch == separator){
            cnt++;
            *ch = 0;
            res[cnt] = ch + 1;
        }
    }
    return res;
}
