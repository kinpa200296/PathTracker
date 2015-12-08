#include <SoftwareSerial.h>

int gpsRX = 2;
int gpsTX = 3;

bool sentenceReady = false;
String sentence = "";

SoftwareSerial gpsSerial(gpsRX, gpsTX);

void setup() {
  Serial.begin(9600);
  gpsSerial.begin(9600);
  sentence.reserve(200);
}

void loop() {
  checkGpsSerial();
  
  if (sentenceReady){
    processSentence();
  }
  
}

void processSentence(){
  Serial.println(sentence);

  sentence = "";
  sentenceReady = false;
}

void checkGpsSerial(){
  while (gpsSerial.available() > 0){
    char ch = (char)gpsSerial.read();
    sentence += ch;
    if (ch == '\n'){
      sentenceReady = true;
    }
  }
}


