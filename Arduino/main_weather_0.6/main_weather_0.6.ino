
//Including the libreries
#include <Wire.h>
#include <avr/power.h>
#include <avr/sleep.h>
#include <SoftwareSerial.h>
#include <SFE_BMP180.h>
#include <SparkFun_Si7021_Breakout_Library.h>

//Declare the objects
Weather si7021;
SFE_BMP180 bmp180;
SoftwareSerial Bluetooth(6, 7);

//Declare the constants
const byte sensorPin = A0;
  
//Declare the variables
float  temperature, humidity; 
double temperature2, pressure;
long voltage;

char key;
String message_string;

void setup() {
  
  //Initializing the objects
  si7021.begin();
  bmp180.begin();
  Bluetooth.begin(9600);   //Baut rate to comunicate with the Bluetooth module
  sleepNow();
}


void loop() {
  
  //If the Bluetooth module receive something, store it into the variable key
  if(Bluetooth.available()) {
    key = Bluetooth.read();
  }

  // if the passkey is 'a' start the mesourements
  if(key == 'a') {
    sendString();
    }

  // if the passkey is 'b' end trasfering data and go to sleep mode
  if(key == 'b') {
    sleepNow();
  }

}
