
//function to get all the sensor's data
void getSensor() {

  //Read and computes the battery voltage and battery level
  sensor_value = analogRead(sensorPin);
  
  sensor_voltage = (3.3 * sensor_value) / 1023;

  battery_voltage = sensor_voltage / 0.72;              

  battery_level = map(battery_voltage*100, 300, 420, 0, 100);    

  //Read humidity and temperature from the si7021 sensor
  Humidity = si7021.getRH();

  TemperatureA = si7021.readTemp();

  //Read pressure and temperature from the bpm180 sensor  
  char status; 
  status = bmp180.startTemperature();
  if (status != 0) {
    
    delay(status);
    status = bmp180.getTemperature(TemperatureB);
    if (status != 0) {
      
      status = bmp180.startPressure(3);
      if (status != 0) {
        
        delay(status);
        status = bmp180.getPressure(Absolute_pressure, TemperatureB);
        if (status != 0) {
          
          Relative_pressure = bmp180.sealevel(Absolute_pressure,altitude);
        }
      }
    }
  } 
}
