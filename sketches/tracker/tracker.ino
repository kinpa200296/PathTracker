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
char *pathsFileName = "paths.dat", *counterFileName = "counter.dat";

GpsParser parser(100);
PathTracker tracker;

unsigned long prevWriteTime, counter;

SoftwareSerial gpsSerial(gpsRX, gpsTX);

void setup() {
  Serial.begin(9600);
  gpsSerial.begin(9600);
  sdCardPresent = SD.begin(sdCSPin);
  if (sdCardPresent){
    //Serial.println("card present");
    if (SD.exists(counterFileName)){
      File counterFile = SD.open(counterFileName, FILE_READ);
      counter = getULong(counterFile);
      counterFile.close();
    }
    else{
      counter = 0;
      saveCounter();
    }
    if (!SD.exists(pathsFileName)){
      File pathsFile = SD.open(pathsFileName, FILE_WRITE);
      pathsFile.close();
    }
    prevWriteTime = millis();
  }
}

void loop() {
  
  if (gpsSerial.available() > 0){
    checkGpsSerial();
  }
  else if(Serial.available() > 0){
    readCommand();
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

void saveCounter(){
  if (sdCardPresent){
    File counterFile = SD.open(counterFileName, FILE_WRITE);
    counterFile.seek(0);
    counterFile.write(((byte*)&counter), 4);
    counterFile.close();
  }
}

void processSentence(){
  DataParser *dataParser = new DataParser(parser.getBufferedData());
  GpsData *data = dataParser->getData();
  delete dataParser;
  byte *bytes = data->toBytes();
  
  if (data->isActive()){
    if (tracker.broadcastEnabled()){
      response(RESULT_BROADCAST, "Active");
      response(RESULT_BROADCAST, bytes, GPS_DATA_SIZE);
    }
    
    if (dataFile && sdCardPresent && tracker.resumed()){
      dataFile.write(bytes, GPS_DATA_SIZE);
      prevWriteTime = millis();
    }
  }
  else{
    if (tracker.broadcastEnabled()){
      response(RESULT_BROADCAST, "Void");
    }
  }

  free(bytes);
  delete data;
  parser.reset();
}

void checkGpsSerial(){  
  while (gpsSerial.available() > 0){
    char ch = (char)gpsSerial.read();
    parser.add(ch);
  }
}

void readCommand(){
  while(Serial.available() > 0){
    byte b = (byte)Serial.read();
    if (tracker.analyze(b)){
      processCommand();
    }
  }
}

void response(byte result, const char* msg){
  Serial.write(result);
  Serial.print(msg);
  Serial.write(MESSAGE_END);
  Serial.println();
}

void response(byte result, const byte* msg, int msgSize){
  Serial.write(result);
  Serial.write(msg, msgSize);
  Serial.write(MESSAGE_END);
  Serial.println();
}

void processCommand(){
  switch(tracker.getCommand()){
    case COMMAND_LIST_PATHS:
      // Serial.println("List Paths");
      listPaths();
      break;
    case COMMAND_SEND_PATH:
      // Serial.println("Send Path");
      sendPath();
      break;
    case COMMAND_DELETE_PATH:
      // Serial.println("Delete Path");
      deletePath();
      break;
    case COMMAND_NEW_PATH:
      // Serial.println("New Path");
      newPath(false);
      break;
    case COMMAND_ENABLE_BROADCAST:
      tracker.enableBroadcast();
      response(RESULT_BROADCAST_ENABLED, "");
      break;
    case COMMAND_DISABLE_BROADCAST:
      tracker.disableBroadcast();
      response(RESULT_BROADCAST_DISABLED, "");
      break;
    case COMMAND_PAUSE_PATH:
      if (dataFile){
        tracker.pause();
        response(RESULT_PATH_PAUSED, "");
      }
      else{
        response(RESULT_ERROR, "NoDataFile");
      }
      break;
    case COMMAND_RESUME_PATH:
      if(dataFile){
        tracker.resume();
        response(RESULT_PATH_RESUMED, "");
      }
      else{
        response(RESULT_ERROR, "NoDataFile");
      }
      break;
    case COMMAND_STOP_PATH:
      if (dataFile){
        dataFile.close();
        tracker.stop();
        response(RESULT_PATH_STOPPED, "");
      }
      else{
        response(RESULT_ERROR, "NoDataFile");
      }
      break;
    case COMMAND_GET_STATE:
      sendState();
      break;
    default:
      response(RESULT_UNKNOWN_COMMAND, "Unknown");
      break;
  }
  tracker.resetCommand();
}

void sendState(){
  byte state = tracker.getState();
  response(RESULT_STATE, &state, 1);
}

void listPaths(){
  bool broadcastState = tracker.broadcastEnabled();
  if (broadcastState){
    tracker.disableBroadcast();
  }
  if (sdCardPresent){
    File pathsFile = SD.open(pathsFileName, FILE_READ);
    unsigned long cnt = 0;
    while(pathsFile.peek() != -1){
      String s = getString(pathsFile);
      response(RESULT_PATH_LIST_ITEM, s.c_str());
      s = getString(pathsFile);
      cnt++;
    }
    if (cnt == 0){
      response(RESULT_ERROR, "NoPaths");
    }
    else{
      response(RESULT_PATHS_LIST_SENT, "");
    }
  }
  else{
    response(RESULT_ERROR, "NoCard");
  }
  if (broadcastState){
    tracker.enableBroadcast();
  }
}

void newPath(bool buttonPressed){
  if (sdCardPresent){
    counter++;
    saveCounter();
    
    String fileName = generateFileName(counter);
    
    File pathsFile = SD.open(pathsFileName, FILE_WRITE);
    if (!buttonPressed){
      pathsFile.println(tracker.getString(0));
    }
    else{
      pathsFile.println(fileName);
    }
    pathsFile.println(fileName);
    pathsFile.close();
    tracker.stop();
    if (dataFile){
      dataFile.close();
    }
    dataFile = SD.open(fileName.c_str(), FILE_WRITE);
    if (dataFile){
    response(RESULT_PATH_ADDED, tracker.getString(0));
    tracker.resume();
    }
    else{
      response(RESULT_ERROR, "NoFileCreated");
    }
  }
  else{
    response(RESULT_ERROR, "NoCard");
  }
}

void seekPath(File f, unsigned long pathId){
  String s;
  for (int i = 0; f.peek() != -1 && i < pathId; i++){
    s = getString(f);
    s = getString(f);
  }
}

void sendPath(){
  bool broadcastState = tracker.broadcastEnabled();
  if (broadcastState){
    tracker.disableBroadcast();
  }
  if (sdCardPresent){
    File pathsFile = SD.open(pathsFileName, FILE_READ);
    seekPath(pathsFile, tracker.getULong(0));
    String pathName = getString(pathsFile);
    String pathFileName = getString(pathsFile);
    pathsFile.close();
    File pathFile;
    if (pathFileName != ""){
      pathFile = SD.open(pathFileName, FILE_READ);
    }
    if (pathFile){
      byte data[GPS_DATA_SIZE];
      while (pathFile.peek() != -1){
        for (int i = 0; pathFile.peek() != -1 && i < GPS_DATA_SIZE; i++){
          data[i] = pathFile.read();
        }
        response(RESULT_PATH_PART, data, GPS_DATA_SIZE);
      }
      pathFile.close();
      response(RESULT_PATH_SENT, pathName.c_str());
    }
    else{
      response(RESULT_ERROR, "PathNotFound");
    }
  }
  else{
    response(RESULT_ERROR, "NoCard");
  }
  if (broadcastState){
    tracker.enableBroadcast();
  }
}

void deletePath(){
  bool broadcastState = tracker.broadcastEnabled();
  if (broadcastState){
    tracker.disableBroadcast();
  }
  if (sdCardPresent){
    File pathsFile = SD.open(pathsFileName, FILE_READ);
    File tempFile = SD.open("temp.dat", FILE_WRITE);
    while(pathsFile.peek() != -1){
      tempFile.println(getString(pathsFile));
    }
    pathsFile.close();
    tempFile.close();
    SD.remove(pathsFileName);
    pathsFile = SD.open(pathsFileName, FILE_WRITE);
    tempFile = SD.open("temp.dat", FILE_READ);
    unsigned long pathId = tracker.getULong(0);
    for (int i = 0; i < pathId && tempFile.peek() != -1; i++){
      pathsFile.println(getString(tempFile));
      pathsFile.println(getString(tempFile));
    }
    String pathName = getString(tempFile);
    String pathFileName = getString(tempFile);
    if (pathFileName != ""){
      char str[pathFileName.length() + 1];
      pathFileName.getBytes((byte*)str, pathFileName.length() + 1);
      SD.remove(str);
      response(RESULT_PATH_DELETED, pathName.c_str());
    }
    else{
      response(RESULT_ERROR, "PathNotFound");
    }
    while (tempFile.peek() != -1){
      pathsFile.println(getString(tempFile));
    }
    pathsFile.close();
    tempFile.close();
    SD.remove("temp.dat");
  }
  else{
    response(RESULT_ERROR, "NoCard");
  }
  if (broadcastState){
    tracker.enableBroadcast();
  }
}

