
//Function to put the processor to sleep mode
void sleepNow() {
     
  set_sleep_mode(SLEEP_MODE_IDLE);
  sleep_enable();       
  
  power_adc_disable();
  power_spi_disable();
  power_timer0_disable();
  power_timer1_disable();
  power_timer2_disable();
  power_twi_disable();
  
  sleep_mode();          

 //After waking                            
  sleep_disable();                                 
  power_all_enable();
   
}

