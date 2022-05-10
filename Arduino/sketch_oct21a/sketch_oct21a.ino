
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
SoftwareSerial Bluetooth(7,6);

//Declare the variables
float Humidity = 0;
float TemperatureA = 0;
double TemperatureB = 0;
double Absolute_pressure = 0;
double Relative_pressure = 0;
int altitude = 0;
int sensorPin = A0;
int sensor_value = 0;
float sensor_voltage = 0;
float battery_voltage = 0;
int battery_level = 0;


void setup() {

  //Initializing the objects
  si7021.begin();
  bmp180.begin();
  Bluetooth.begin(9600);   //Baut rate to comunicate with the Bluetooth module
}


void loop() {

  //If the Bluetooth module receive the word "wake" from any device, wake up the processor
  if(Bluetooth.available()) {
    if(Bluetooth.readString() == "wake") {
      
        Bluetooth.print("Arduino Pro mini 8Mhz\nMini weather station v0.2\nPlease enter your courrent altitude in meters...\n\n");
        sleepNow();
        enter_2();
    }
    //If the Bluetooth module receive any another word, delete it and go in sleep mode
    else {
      Bluetooth.flush();
      sleepNow();
    }
  }
}
