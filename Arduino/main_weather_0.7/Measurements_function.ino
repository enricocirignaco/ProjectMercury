
//function to get all the sensor's data
void getSensor() {


  //Read the battery voltage from the analog input
  voltage = analogRead(sensorPin);

  
  //Read humidity and temperature from the si7021 sensor
  humidity = si7021.getRH();
  temperature = si7021.readTemp();


  //Read pressure and temperature from the bpm180 sensor  
  char status; 
  status = bmp180.startTemperature();
  if (status != 0) {
    
    delay(status);
    status = bmp180.getTemperature(temperature2);
    if (status != 0) {
      
      status = bmp180.startPressure(3);
      if (status != 0) {
        
        delay(status);
        status = bmp180.getPressure(pressure, temperature2);
        if (status != 0) {
          
        }
      }
    }
  } 
   
}
