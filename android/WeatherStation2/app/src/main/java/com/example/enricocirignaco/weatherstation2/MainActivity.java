
package com.example.enricocirignaco.weatherstation2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    // Declaring variables

    int battery_voltage;
    int battery_percent;
    int last_battery;

    String last_time;

    String string_temperature;
    String string_temperature2;
    String string_humidity;
    String string_pressure;
    String string_battery;

    String string_last_temperature2;
    String string_last_temperature;
    String string_last_humidity;
    String string_last_pressure;

    // Declaring TextViews
    TextView temperature_text;
    TextView humidity_text;
    TextView pressure_text;
    TextView battery_text;

    TextView last_temperature_text;
    TextView last_humidity_text;
    TextView last_pressure_text;
    TextView last_battery_text;

    TextView battery_voltage_text;

    Toolbar main_toolbar;

    // Declaring Handler Variables and Objects
    private StringBuilder recDataString = new StringBuilder();
    Handler bluetoothIn;
    final int handlerState = 0;

    // Declaring Bluetooth Variables and objects
    //String address = "AB:60:86:56:34:02";         //SPP MAC address
    String MACaddress = "98:D3:32:10:84:60";       //HC_05 MAC address
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBtConnected = false;
    private ConnectedThread mConnectedThread;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;


    //==============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get "last" data from SharedPreference
        getSharedPref();

        // Setup layout
        setContentView(R.layout.activity_main);
        Layout_setup();

        // Setup Bluetooth
        SetupBT();

        // get data from BT
        getHandler();

    }


    //==============================================================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // if "Connect" Button has pushed
        if (id == R.id.connect_button) {

            //Call the class to connect
            new ConnectBT().execute();
        }

        // if "disconnect" Button has pushed
        if (id == R.id.disconnect_button) {

            //Call the class to disconnect
            DisconnectBT();
        }

        if (id == R.id.settings_button) {
            //Start the SettingActivity and layout
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);


        }
        return true;
    }

    //==============================================================================================

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        invalidateOptionsMenu();

        if (isBtConnected == true) {
            getMenuInflater().inflate(R.menu.menu_activity_2, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_activity, menu);

        }
        return true;
    }

    //==============================================================================================

    // Setup widgets, TextViews and toolbar
    public void Layout_setup() {

        temperature_text = (TextView) findViewById(R.id.temperature_value_id);
        humidity_text = (TextView) findViewById(R.id.humidity_value_id);
        pressure_text = (TextView) findViewById(R.id.pressure_value_id);
        battery_text = (TextView) findViewById(R.id.battery_value_id);

        last_temperature_text = (TextView) findViewById(R.id.last_temperature_id);
        last_humidity_text = (TextView) findViewById(R.id.last_humidity_id);
        last_pressure_text = (TextView) findViewById(R.id.last_pressure_id);
        last_battery_text = (TextView) findViewById(R.id.last_battery_id);

        battery_voltage_text = (TextView) findViewById(R.id.battery_voltage_id);

        last_temperature_text.setText(string_last_temperature + "°C" + "(" + last_time + ")");
        last_humidity_text.setText(string_last_humidity + "% (" + last_time + ")");
        last_pressure_text.setText(string_last_pressure + "hPa" + "(" + last_time + ")");
        last_battery_text.setText(last_battery + "%" + "(" + last_time + ")");

        // Setup the toolbar
        main_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(main_toolbar);
        setStatusBar();
    }

    //==============================================================================================

    // Layout to create after receiving data
    void Layout_loop() {


        //voltage value (0 - 1023)
        int voltage_value = Integer.parseInt(string_battery);
        //voltage (0 - 3300)
        int voltage = (3300 * voltage_value) / 1023;
        // battery voltage (4200 - 3000)
        battery_voltage = (int) (voltage / 0.72);
        // battery percent (0 - 100)
        // remapping battery voltage from 3000-4200 range to 0-100 range
        battery_percent = ((battery_voltage - 3000) * 100) / 1200;

        temperature_text.setText(string_temperature + "°C\n" + string_temperature2 + "°C");
        humidity_text.setText(string_humidity + "%");
        pressure_text.setText(string_pressure + "\n  hPa");
        battery_text.setText("  " + battery_percent + "%");
        battery_voltage_text.setText(battery_voltage + "mV");

    }

    //==============================================================================================

    // Setup Bluetooth Options
    public void SetupBT() {

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Show a mensag. that the device has no bluetooth adapter
            msg("Bluetooth Device Not Available");

            //finish apk
            finish();
        } else if (!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }


    }

    //==============================================================================================

    // function to display a string like a Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    //==============================================================================================

    // Connect Bluetooth Process
    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(MACaddress);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }

            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Try again.");
            } else {
                msg("Connected.");
                isBtConnected = true;
                mConnectedThread.write("a");
                setStatusBar();
            }
            progress.dismiss();
        }
    }

    //==============================================================================================

    // Disconnect Bluetooth process
    private void DisconnectBT() {

        // store the last measurement in the SharedPreferences
        storeSharedPref();

        // terminate the transfer
        mConnectedThread.write("b");

        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        msg("Disconnected");
        isBtConnected = false;
        setStatusBar();
    }

    //==============================================================================================

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        //read method
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                msg("Error");

            }
        }
    }

    //==============================================================================================

    // Get the current time      **if Date is true it return too the date**
    String time(boolean Date) {

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String currentTimeString = currentDateTimeString.substring(13, 18);

        if (Date == true) {
            return currentDateTimeString;
        } else {
            return currentTimeString;
        }


    }

    //==============================================================================================

    // Store "last" data into shared preferences

    private void storeSharedPref() {

        // Create SharedPreferences file's name
        String name_string = "com.example.enricocirignaco.myapplication.lastMeasurements";
        // Create object of SharedPreferences.
        SharedPreferences sharedData = getSharedPreferences(name_string, MODE_PRIVATE);

        //now get Editor
        SharedPreferences.Editor editor = sharedData.edit();

        Log.i("last battery", string_battery);

        //put your value
        editor.putString("temperature_key", string_temperature);
        editor.putString("temperature#_key", string_temperature2);
        editor.putString("humidity_key", string_humidity);
        editor.putString("pressure_key", string_pressure);
        editor.putInt("battery_key", battery_percent);
        editor.putString("time_key", time(false));

        //commits your edits
        editor.commit();
    }
    //==============================================================================================

    // Get "last" data from shared preferences
    private void getSharedPref() {

        // Create SharedPreferences file's name
        String name_string = "com.example.enricocirignaco.myapplication.lastMeasurements";
        // Create object of SharedPreferences.
        SharedPreferences sharedData = getSharedPreferences(name_string, MODE_PRIVATE);

        //extract your value
        string_last_temperature = sharedData.getString("temperature_key", "Empty");
        string_last_temperature2 = sharedData.getString("temperature#_key", "Empty");
        string_last_humidity = sharedData.getString("humidity_key", "Empty");
        string_last_pressure = sharedData.getString("pressure_key", "Empty");
        last_battery = sharedData.getInt("battery_key", 0);
        last_time = sharedData.getString("time_key", "Empty");

    }

    //==============================================================================================

    // get data from Handler
    private void getHandler() {

        bluetoothIn = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {              //if message is what we want
                    String readMessage = (String) msg.obj;   // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);       //keep appending to string until #
                    int endOfLineIndex = recDataString.indexOf("~");// determine the end-of-line
                    if (endOfLineIndex > 0) {                       // make sure there data before #
                        if (recDataString.charAt(0) == '#')        //if it starts with # we know it is what we are looking for
                        {
                            string_temperature = recDataString.substring(1, 5);  //get sensor value from string between indices 1-5
                            string_temperature2 = recDataString.substring(6, 10);
                            string_humidity = recDataString.substring(11, 15);
                            string_pressure = recDataString.substring(16, 22);
                            string_battery = recDataString.substring(22, 25);

                            Layout_loop();
                            //mUpdateLastData.store();
                        }
                        recDataString.delete(0, recDataString.length());    //clear all string data
                    }

                }
            }
        };

    }

    //==============================================================================================

    // set the StatusBar Color
    public void setStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(main_toolbar != null) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                //if Device is connect
                if(isBtConnected != false) {
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.connectedColor));
                }

                //if Device is disconnect
                else {
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.disconnectedColor));

                }

            }
        }


    }

    //==============================================================================================



}


/** #14.3414.0064.761100.67210~ **/