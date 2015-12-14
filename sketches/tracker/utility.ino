unsigned long getULong(File f){
  byte res[4];
  res[0] = f.read();
  res[1] = f.read();
  res[2] = f.read();
  res[3] = f.read();
  return *((unsigned long*)res);
}

String getString(File f){
  String res = "";
  char ch = f.read();
  while (ch != '\n' && ch != -1){
    if (ch != 13){
      res += ch;
    }
    ch = f.read();
  }
  return res;
}

String generateFileName(unsigned long cnt){
  String res = "path0000.dat";
  for (int i = 8; i > 0 && cnt > 0; i--, cnt /= 10){
    res[i - 1] = (cnt % 10) + 48;
  }
  return res;
}

