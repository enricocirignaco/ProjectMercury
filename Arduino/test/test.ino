
#include <SoftwareSerial.h>

SoftwareSerial BT(9, 10);
char x ;
String string = "#14.34*14.00*64.76*1100.67*2130*";

void setup() {
  Serial.begin(9600);
  BT.begin(9600);
}

void loop() {
  
  if (BT.available())
    x = BT.read();

    if(x == 'a') {
    BT.print(string);
    }

}

