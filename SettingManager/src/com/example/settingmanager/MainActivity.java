package com.example.settingmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.example.settingmanager.MainActivity;

import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final CheckBox wifiCheckBox = (CheckBox) findViewById(R.id.wifiCheckbox);
		final CheckBox blueToothCheckBox = (CheckBox) findViewById(R.id.bluetoothCheckbox);
		final CheckBox mobileNetworkCheckBox = (CheckBox) findViewById(R.id.mobileNetworkCheckBox);
		final CheckBox ringCheckBox = (CheckBox) findViewById(R.id.ringCheckbox);
		final CheckBox vibrateCheckBox = (CheckBox) findViewById(R.id.vibrateCheckbox);
		final CheckBox silentCheckBox = (CheckBox) findViewById(R.id.silentCheckbox);
		final Button createButton = (Button) findViewById(R.id.createButton);
		final Button loadButton = (Button) findViewById(R.id.loadButton);
		final EditText settingName = (EditText) findViewById(R.id.settingNameEditText);
		final Context context = this;
		
		createButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//bluetooth, wifi, mobile, ringer
				File file = new File(context.getFilesDir(), settingName.getText().toString());
				String settings = "";
				FileOutputStream output_stream;
				try
				{
					output_stream = openFileOutput(settingName.getText().toString(), context.MODE_PRIVATE);
					if (blueToothCheckBox.isChecked())
					{
						settings = settings + "1";
					}
					else
					{
						settings = settings + "0";
					}
					if (wifiCheckBox.isChecked())
					{
						settings = settings + "1";
					}
					else
					{
						settings = settings + "0";
					}
					if (mobileNetworkCheckBox.isChecked())
					{
						settings = settings + "1";
					}
					else
					{
						settings = settings + "0";
					}
					//Ring = 0, Vibrate = 1, Silent = 2
					if (ringCheckBox.isChecked())
					{
						settings = settings + "0";
					}
					else if (vibrateCheckBox.isChecked())
					{
						settings = settings + "1";
					}
					else
					{
						settings = settings + "2";
					}
						
					output_stream.write(settings.getBytes());
					output_stream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
			}
		});
		
		loadButton.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
                AudioManager audiomanager =(AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            	BluetoothAdapter blueTooth = BluetoothAdapter.getDefaultAdapter();  
				WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                String settings = "";
                Boolean bluetoothState = false;
                Boolean wifiState = false;
                Boolean mobileNetworkState = false;
                Boolean ringState = false;
                Boolean vibrateState = false;
                Boolean silentState = false;
                
				//File file = new File(context.getFilesDir(), settingName.getText().toString());
				//String temp_string = "testing 1 2 3";
				//FileOutputStream output_stream;
				try
				{
					//output_stream = openFileOutput(settingName.getText().toString(), context.MODE_PRIVATE);
					//output_stream.write(temp_string.getBytes());
					//output_stream.close();
					
					//AssetManager asset_manager = context.getAssets();
					//InputStream input_stream = asset_manager.open(settingName.getText().toString());
					FileInputStream input_stream = openFileInput(settingName.getText().toString());
					InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
					BufferedReader buffered_reader = new BufferedReader(input_stream_reader);
					String received = "";
					StringBuilder string_builder = new StringBuilder();
					while ((received = buffered_reader.readLine()) != null)
					{
						string_builder.append(received);
					}
					input_stream.close();
					settings = string_builder.toString();
					//Will change the BlueTooth settings
					if (settings.substring(0, 1).equals("1"))
					{
						blueTooth.enable();
					}
					else
					{
						blueTooth.disable();
					}
					//Will change the WiFi settings
					if (settings.substring(1,2).equals("1"))
					{
						wifi.setWifiEnabled(true);
					}
					else
					{
						wifi.setWifiEnabled(false);
					}
	                //Will change the 3G settings
					if (settings.substring(2,3).equals("1"))
					{
		            	try
						{		
							 ConnectivityManager conman = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);   							   
							 Class conmanClass = Class.forName(conman.getClass().getName());   
							 Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");   
							 iConnectivityManagerField.setAccessible(true);   
							 Object iConnectivityManager = iConnectivityManagerField.get(conman);   
							 Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());   
							 Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);   		   
							 setMobileDataEnabledMethod.setAccessible(true);   
							 setMobileDataEnabledMethod.invoke(iConnectivityManager, true);	 
						}
						catch (Exception e)
						{		
						}
					}
					else
					{
		            	try
						{		
							 ConnectivityManager conman = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);   							   
							 Class conmanClass = Class.forName(conman.getClass().getName());   
							 Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");   
							 iConnectivityManagerField.setAccessible(true);   
							 Object iConnectivityManager = iConnectivityManagerField.get(conman);   
							 Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());   
							 Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);   		   
							 setMobileDataEnabledMethod.setAccessible(true);   
							 setMobileDataEnabledMethod.invoke(iConnectivityManager, false);	 
						}
						catch (Exception e)
						{		
						}
					}
					if (settings.substring(3,4).equals("0"))
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					}
					else if (settings.substring(3,4).equals("1"))
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					}
					else if (settings.substring(3,4).equals("2"))
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					}
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
                
                /*
                String filename = settingName.getText().toString();
                FileOutputStream outputStream;
                try
                {
                	outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                	outputStream.write("hello".getBytes());
                	outputStream.close();
                }
                catch (Exception e)
                {
                	
                }
                */
                
        		SharedPreferences sharedPref = getSharedPreferences("settings", 0);
        		SharedPreferences.Editor editor = sharedPref.edit();
        		editor.putBoolean("wifi", wifiCheckBox.isChecked());
        		editor.putBoolean("bluetooth", blueToothCheckBox.isChecked());
        		editor.putBoolean("mobile", mobileNetworkCheckBox.isChecked());
        		editor.putBoolean("ring", ringCheckBox.isChecked());
        		editor.putBoolean("vibrate", vibrateCheckBox.isChecked());
        		editor.putBoolean("silent", silentCheckBox.isChecked());
        		editor.commit();
        		
        		Boolean wifiTemp = sharedPref.getBoolean("wifi", false);
        		Boolean bluetooth = sharedPref.getBoolean("bluetooth", false);
        		Boolean mobile = sharedPref.getBoolean("mobile", false);
        		Boolean ring = sharedPref.getBoolean("ring", false);
        		Boolean vibrate = sharedPref.getBoolean("vibrate", false);
        		Boolean silent = sharedPref.getBoolean("silent", false);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
