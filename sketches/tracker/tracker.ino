#include <SoftwareSerial.h>
#include <SPI.h>
#include <SD.h>
#include "tr.h"

// int bluetoothRX = 0;
// int bluetoothTX = 1;
int gpsRX = 2;
int gpsTX = 3;

int sdCSPin = 10;
// SD card SPI:
// MOSI - pin 11
// MISO - pin 12
// SCK - pin 13

bool sentenceReady = false;
bool sdCardPresent = false;
char sentence[150];
byte sentenceSize = 0;
File dataFile;

unsigned long prevWriteTime;
// unsigned long writeDelay = WRITE_DELAY;

// SoftwareSerial bluetoothSerial(bluetoothRX, bluetoothTX);
SoftwareSerial gpsSerial(gpsRX, gpsTX);

void setup() {
  Serial.begin(9600);
  gpsSerial.begin(9600);
  // bluetoothSerial.begin(9600);
  sdCardPresent = SD.begin(sdCSPin);
  if (sdCardPresent){
    Serial.println("card present");
    SD.remove("log.dat");
    dataFile = SD.open("log.dat", FILE_WRITE);
    prevWriteTime = millis();
    // bluetoothSerial.println("card present");
  }
  else{
    Serial.println("card not present");
    // bluetoothSerial.println("card not present");
  }
}

void loop() {
  
  if (gpsSerial.available() > 0){
    checkGpsSerial();
  }
  else{
    if (prevWriteTime + WRITE_DELAY < millis()){
      Serial.println(millis(), DEC);
      dataFile.flush();
      prevWriteTime += 10*WRITE_DELAY;
      Serial.println(millis(), DEC);
    }
  }
  if (sentenceReady){
    processSentence();
  }
}

void processSentence(){
  Serial.write(sentence, sentenceSize);
  // bluetoothSerial.println(sentence);
  
  // File dataFile = SD.open("log.dat", FILE_WRITE);
  if (dataFile && sdCardPresent){
    dataFile.write(sentence, sentenceSize);
    prevWriteTime = millis();
    // dataFile.close();
  }
  
  sentenceSize  = 0;
  sentence[sentenceSize] = 0;
  sentenceReady = false;
}

void checkGpsSerial(){  
  while (gpsSerial.available() > 0){
    char ch = (char)gpsSerial.read();
    sentence[sentenceSize] = ch;
    sentenceSize++;
    sentence[sentenceSize] = 0;
    if (ch == '\n'){
      if (str_check(sentence,"$GPRMC")){
        sentenceReady = true;
      }
      else{
        sentenceSize  = 0;
        sentence[sentenceSize] = 0;
      }
    }
  }
}

bool str_check(char *input, char *pattern){
  for (; *pattern; pattern++, input++){
    if (*input != *pattern){
      return false;
    }
  }
  return true;
}


