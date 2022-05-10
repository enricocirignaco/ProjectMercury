
#include <SoftwareSerial.h>
#include <SFE_BMP180.h>
#include <SI7021.h>
#include <Wire.h>


#define ALTITUDE 700
SI7021 si7021;
SFE_BMP180 bmp180;
SoftwareSerial Bluetooth(7,6);

void setup() {

  si7021.begin();
  bmp180.begin();
  Serial.begin(9600);
  Bluetooth.begin(9600);
  pinMode(2, OUTPUT);
  digitalWrite(2, HIGH);
}


void loop() {
  
  int temperature = si7021.getCelsiusHundredths();
    temperature = temperature / 100;
    

    int humidity = si7021.getHumidityPercent();
    
    
  double T, P, p0;
  char status;
  
  status = bmp180.startTemperature();
  if (status != 0) {
    
    delay(status);
    status = bmp180.getTemperature(T);
    if (status != 0) {
      
      status = bmp180.startPressure(3);
      if (status != 0) {
        
        delay(status);
        status = bmp180.getPressure(P, T);
        if (status != 0) {
          
          p0 = bmp180.sealevel(P,ALTITUDE);
        }
      }
    }
  } 

  if(Serial.available()) {
    Serial.read();
    Serial.print("Temperatura si7021:  ");
    Serial.println(temperature);
    Serial.print("Umdita:              ");
    Serial.println(humidity);
    Serial.print("Temperatura bmp180:  ");
    Serial.println(T);
    Serial.print("Pressione assoluta:  ");
    Serial.println(P);
    Serial.print("Pressione relativa:  ");
    Serial.println(p0);   
    Serial.println("BYE");
  }
  

  if(Bluetooth.available()) {
    Bluetooth.read();
    Bluetooth.print("Temperatura si7021:  ");
    Bluetooth.println(temperature);
    Bluetooth.print("Umdita:              ");
    Bluetooth.println(humidity);
    Bluetooth.print("Temperatura bmp180:  ");
    Bluetooth.println(T);
    Bluetooth.print("Pressione assoluta:  ");
    Bluetooth.println(P);
    Bluetooth.print("Pressione relativa:  ");
    Bluetooth.println(p0);   
    Bluetooth.println("BYE");
  }
  delay(100);
  
}
