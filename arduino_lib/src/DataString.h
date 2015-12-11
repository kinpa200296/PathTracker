#ifndef DataString_h
#define DataString_h

#include "stdlib.h"

class DataString{
private:
    int _max_length;
    char *_data;
    int _current_length;
public:
    DataString(int max_length);
    ~DataString();

    int length();
    int max_length();
    void clear();
    void append(char ch);
    bool equals(const char *other);
    const char* getData();
};

#endif
