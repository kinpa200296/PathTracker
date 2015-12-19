#include <SoftwareSerial.h>
#include <SPI.h>
#include <SD.h>

#include <PathTracker.h>
// Pins for connecting gps
int gpsRX = 2;
int gpsTX = 3;

int sdCSPin = 10;
// SD card SPI:
// MOSI - pin 11
// MISO - pin 12
// SCK - pin 13

bool sdCardPresent = false;
// file into which current path is recorded
File dataFile;
char *pathsFileName = "paths.dat", *counterFileName = "counter.dat";

// c++ class from PathTracker library that parses gps data
GpsParser parser(100);
// c++ class from PathTracker library that holds current state of device
// and parses incoming commands from Serial(whether it is commands from USB or Bluetooth)
PathTracker tracker;

// counter used for generating unique filenames
// prevWriteTime used together with WRITE_DELAY from c++ lib
// to make dataFile flush after parsing GPS data
unsigned long prevWriteTime, counter;

// used to communicate over UART with GPS reciever 
SoftwareSerial gpsSerial(gpsRX, gpsTX);

void setup() {
  Serial.begin(9600);
  gpsSerial.begin(9600);
  sdCardPresent = SD.begin(sdCSPin);
  if (sdCardPresent){
    
    // trying to find if counter already has a saved value
    if (SD.exists(counterFileName)){
      File counterFile = SD.open(counterFileName, FILE_READ);
      counter = getULong(counterFile);
      counterFile.close();
    }
    else{
      // if not set 0 and save
      counter = 0;
      saveCounter();
    }
    // checking if file describing paths exists
    // if not we will make one
    if (!SD.exists(pathsFileName)){
      File pathsFile = SD.open(pathsFileName, FILE_WRITE);
      pathsFile.close();
    }
    prevWriteTime = millis();
  }
}

void loop() {
  
  if (gpsSerial.available() > 0){
    // if anything came from GPS read it first
    checkGpsSerial();
  }
  else if(Serial.available() > 0){
    // if any input via USB or Bluetooth read it
    readCommand();
  }
  else{
    if (parser.isReady()){
      // GPS sent enough data to start parcing
      processSentence();
    }
    if (sdCardPresent && prevWriteTime + WRITE_DELAY < millis()){
      // flush dataFile if it is time
      dataFile.flush();
      prevWriteTime += 10*WRITE_DELAY;
    }
  }
}

// used to keep counter value between shut downs
void saveCounter(){
  if (sdCardPresent){
    File counterFile = SD.open(counterFileName, FILE_WRITE);
    counterFile.seek(0);
    counterFile.write(((byte*)&counter), 4);
    counterFile.close();
  }
}

// called when GpsParser gathered enough data from GPS module
void processSentence(){
  // parsing string of GPS data
  DataParser *dataParser = new DataParser(parser.getBufferedData());
  // getting GPS data in numeric form
  GpsData *data = dataParser->getData();
  delete dataParser;
  byte *bytes = data->toBytes();
  
  if (data->isActive()){
    // if enough sattelites are caught
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
    // if enough sattelites are caught
    if (tracker.broadcastEnabled()){
      response(RESULT_BROADCAST, "Void");
    }
  }

  // free memory to prevent leaks
  free(bytes);
  delete data;

  // reseting internal buffer
  parser.reset();
}

// read all available data from GPS module
void checkGpsSerial(){  
  while (gpsSerial.available() > 0){
    char ch = (char)gpsSerial.read();
    parser.add(ch);
  }
}

// analyzes message coming from Bluetooth or USB
void readCommand(){
  while(Serial.available() > 0){
    byte b = (byte)Serial.read();
    if (tracker.analyze(b)){
      // if message finished we should process it
      processCommand();
    }
  }
}

// send response to message via Bluetooth or USB
void response(byte result, const char* msg){
  byte msgSize = strlen(msg);
  Serial.write(result);
  Serial.write(msgSize);
  Serial.print(msg);
}

void response(byte result, const byte* msg, byte msgSize){
  Serial.write(result);
  Serial.write(msgSize);
  Serial.write(msg, msgSize);
}

// do requested action
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

// sends current PathTracker state
void sendState(){
  byte state = tracker.getState();
  response(RESULT_STATE, &state, 1);
}

// sends paths list via Bluetooth or USB
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

// starts recording new path
void newPath(bool buttonPressed){
  if (sdCardPresent){
    counter++;
    saveCounter();
    
    String fileName = generateFileName(counter);
    // put a record about new path
    File pathsFile = SD.open(pathsFileName, FILE_WRITE);
    if (strlen(tracker.getString(0)) != 0){
      pathsFile.println(tracker.getString(0));
    }
    else{
      // give tag = filename if tag from command message is empty
      pathsFile.println(fileName);
    }
    pathsFile.println(fileName);
    pathsFile.close();
    // stop existing path recording if any
    tracker.stop();
    if (dataFile){
      dataFile.close();
    }
    // start recording a new path
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

// move file cursor to path with specified id
void seekPath(File f, unsigned long pathId){
  String s;
  for (int i = 0; f.peek() != -1 && i < pathId; i++){
    s = getString(f);
    s = getString(f);
  }
}

// send requsted path
void sendPath(){
  bool broadcastState = tracker.broadcastEnabled();
  if (broadcastState){
    // just in case normally should be useless
    tracker.disableBroadcast();
  }
  if (sdCardPresent){
    // find path fileName
    File pathsFile = SD.open(pathsFileName, FILE_READ);
    seekPath(pathsFile, tracker.getULong(0));
    String pathName = getString(pathsFile);
    String pathFileName = getString(pathsFile);
    pathsFile.close();
    // try opening file found in records
    File pathFile;
    if (pathFileName != ""){
      pathFile = SD.open(pathFileName, FILE_READ);
    }
    // send data if found
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

// deletes specified path
void deletePath(){
  bool broadcastState = tracker.broadcastEnabled();
  if (broadcastState){
    tracker.disableBroadcast();
  }
  if (sdCardPresent){
    File pathsFile = SD.open(pathsFileName, FILE_READ);
    // save into temp file
    File tempFile = SD.open("temp.dat", FILE_WRITE);
    while(pathsFile.peek() != -1){
      tempFile.println(getString(pathsFile));
    }
    pathsFile.close();
    tempFile.close();
    // delete records
    SD.remove(pathsFileName);
    // restore records from temp file skipping specified record
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

