#include <SoftwareSerial.h>
#include <SPI.h>
#include <SD.h>

#include <PathTracker.h>

int gpsRX = 2;
int gpsTX = 3;

int sdCSPin = 10;
// SD card SPI:
// MOSI - pin 11
// MISO - pin 12
// SCK - pin 13

bool sdCardPresent = false;
File dataFile;

GpsParser parser(100);

unsigned long prevWriteTime;

SoftwareSerial gpsSerial(gpsRX, gpsTX);

void setup() {
  Serial.begin(9600);
  gpsSerial.begin(9600);
  sdCardPresent = SD.begin(sdCSPin);
  if (sdCardPresent){
    Serial.println("card present");
    if (SD.exists("log.dat")){
      SD.remove("log.dat");
    }
    dataFile = SD.open("log.dat", FILE_WRITE);
    prevWriteTime = millis();
  }
  else{
    Serial.println("card not present");
  }
}

void loop() {
  
  if (gpsSerial.available() > 0){
    checkGpsSerial();
  }
  else{
    if (parser.isReady()){
      processSentence();
    }
    if (sdCardPresent && prevWriteTime + WRITE_DELAY < millis()){
      dataFile.flush();
      prevWriteTime += 10*WRITE_DELAY;
    }
  }
}

void processSentence(){
  DataParser *dataParser = new DataParser(parser.getBufferedData());
  GpsData *data = dataParser->getData();
  delete dataParser;
  
  if (data->isActive()){
    // Serial.write(parser.getBufferedData()->getData(), parser.getBufferedData()->length());
    Serial.println("Active");
    Serial.print("latitude: ");
    Serial.print(data->get_latitude_degrees());
    Serial.print(" deg ");
    Serial.print(data->get_latitude_minutes(), 5);
    Serial.println(" min");
    Serial.print("longitude: ");
    Serial.print(data->get_longitude_degrees());
    Serial.print(" deg ");
    Serial.print(data->get_longitude_minutes(), 5);
    Serial.println(" min");
    Serial.print("time: ");
    Serial.print(data->get_time());
    Serial.print("; date: ");
    Serial.println(data->get_date());
  }
  else{
    // Serial.write(parser.getBufferedData()->getData(), parser.getBufferedData()->length());
    Serial.println("Void");
    if (data->get_time() != 0){
      Serial.print("time: ");
      Serial.print(data->get_time());
    }
    if (data->get_date() != 0){
      Serial.print("; date: ");
      Serial.println(data->get_date());
    }
  }
  
  if (dataFile && sdCardPresent){
    //dataFile.write(data->getData(), data->length());
    prevWriteTime = millis();
  }
  
  delete data;
  parser.reset();
}

void checkGpsSerial(){  
  while (gpsSerial.available() > 0){
    char ch = (char)gpsSerial.read();
    parser.add(ch);
  }
}


