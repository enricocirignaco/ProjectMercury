
//Function sendString
void sendString() {

  String temperature_string;
  String temperature2_string;
  String humidity_string;
  String pressure_string;
  String voltage_string;
  
  getSensor();

  
  temperature_string   =  String(temperature -1 +60, 2);            //ATTENTION!!! IMPORTANT!!! SENSOR BROKEN!!!
  temperature2_string  =  String(temperature2 -1, 2);
  humidity_string      =  String(humidity -10 +60, 2);              //ATTENTION!!! IMPORTANT!!! SENSOR BROKEN!!!
  pressure_string      =  String(pressure, 2);
  voltage_string       =  String(voltage);
  

  message_string = ("#" + temperature_string + temperature2_string + humidity_string + pressure_string + voltage_string + "~");
  Bluetooth.print(message_string);
  //Serial.println(tmessage_strig);
  
}

