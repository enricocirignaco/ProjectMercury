
#include <SoftwareSerial.h>

SoftwareSerial BT(10, 11); 

void setup() {
  Serial.begin(9600);
  BT.begin(9600);
}

void loop() {
  
  if (mySerial.available())
    Serial.write(mySerial.read());
  if (Serial.available())
    mySerial.write(Serial.read());
}

