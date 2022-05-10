
//Function enter_2
void enter_2() {
  
  //Receive the altitude value and send to the device all data
  if(Bluetooth.available()) {
   
    altitude = Bluetooth.parseInt();
    
    Bluetooth.print("Your altitute is: ");
    Bluetooth.print(altitude);
    Bluetooth.print(" meters\n\nPlease wait, Measurement in progress!\n\n");
    
    getSensor();

    Bluetooth.print("Relative humidity ±2%:   ");
    Bluetooth.print(Humidity);
    
    Bluetooth.print(" %\nTemperature A ±0.4C:     ");
    Bluetooth.print(TemperatureA);
    
    Bluetooth.print(" C\nAbsolute pressure ±1hPa: ");
    Bluetooth.print(Absolute_pressure);
    
    Bluetooth.print(" hPa\nRelative pressure ±1hPa: ");
    Bluetooth.print(Relative_pressure);
    
    Bluetooth.print(" hPa\nTemperature B ±1C:       ");
    Bluetooth.print(TemperatureB);
    
    Bluetooth.print(" C\n\nBattery Level: ");
    Bluetooth.print(battery_level);
    Bluetooth.print(" %\nBattery Voltage: ");
    Bluetooth.print(battery_voltage);
    Bluetooth.print(" Volts\n\n");

    //Put the processor in sleep mode
    Bluetooth.print("I'm tired, I go to sleep\n");
    sleepNow();
  }
}

