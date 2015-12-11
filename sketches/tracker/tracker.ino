#include <SoftwareSerial.h>
#include <SPI.h>
#include <SD.h>

#include <PathTracker.h>

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
File dataFile;

GpsParser parser(100);

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
    if (SD.exists("log.dat")){
      SD.remove("log.dat");
    }
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
      // Serial.println(millis(), DEC);
      dataFile.flush();
      prevWriteTime += 10*WRITE_DELAY;
      // Serial.println(millis(), DEC);
    }
  }
  if (parser.isReady()){
    processSentence();
  }
}

void processSentence(){
  DataString *data = parser.getBufferedData();
  Serial.write(data->getData(), data->length());
  // bluetoothSerial.println(sentence);
  
  // File dataFile = SD.open("log.dat", FILE_WRITE);
  if (dataFile && sdCardPresent){
    dataFile.write(data->getData(), data->length());
    prevWriteTime = millis();
    // dataFile.close();
  }
  
  parser.reset();
}

void checkGpsSerial(){  
  while (gpsSerial.available() > 0){
    char ch = (char)gpsSerial.read();
    parser.add(ch);
  }
}


