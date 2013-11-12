package com.example.settingmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.example.settingmanager.MainActivity;

import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Switch wifiCheckBox = (Switch) findViewById(R.id.switchWifi);
		final Switch blueToothCheckBox = (Switch) findViewById(R.id.switchBluetooth);
		final Switch mobileNetworkCheckBox = (Switch) findViewById(R.id.switchMobile);
		final Switch ringCheckBox = (Switch) findViewById(R.id.switchRing);
		final Switch vibrateCheckBox = (Switch) findViewById(R.id.switchVibrate);
		final Switch silentCheckBox = (Switch) findViewById(R.id.switchSilent);
		final Button createButton = (Button) findViewById(R.id.mainCreate);
		final Button loadButton = (Button) findViewById(R.id.mainLoad);
		final EditText settingName = (EditText) findViewById(R.id.name);
		final Context context = this;
		final Spinner spinner = (Spinner) findViewById(R.id.spinner);
		final Button deleteButton = (Button) findViewById(R.id.delete);
		final ArrayList<String> spinnerArray = new ArrayList<String>();
		File[] files = context.getFilesDir().listFiles();
		spinnerArray.add("");
		for (File file : files) {
			if (!file.isDirectory()) {
				spinnerArray.add(file.getName());
				Log.d("File", file.getName());
			}
		}
		spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, spinnerArray));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				((TextView)arg1).setText(null);
				settingName.setText(spinner.getItemAtPosition(arg2).toString());
				//visualize settings
				try {
					FileInputStream input_stream = openFileInput(settingName.getText().toString());
					InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
					BufferedReader buffered_reader = new BufferedReader(input_stream_reader);
					String received = "";
					StringBuilder string_builder = new StringBuilder();
					while ((received = buffered_reader.readLine()) != null)
						string_builder.append(received);
					input_stream.close();
					String settings = string_builder.toString();
					//Will change the BlueTooth settings
					if (settings.substring(0, 1).equals("1"))
						blueToothCheckBox.setChecked(true);
					else
						blueToothCheckBox.setChecked(false);
					//Will change the WiFi settings
					if (settings.substring(1,2).equals("1"))
						wifiCheckBox.setChecked(true);
					else
						wifiCheckBox.setChecked(false);
					//Will change the 3G settings
					if (settings.substring(2,3).equals("1"))
						mobileNetworkCheckBox.setChecked(true);
					else
						mobileNetworkCheckBox.setChecked(false);
					if (settings.substring(3,4).equals("0"))
						ringCheckBox.setChecked(true);
					else if (settings.substring(3,4).equals("1"))
						vibrateCheckBox.setChecked(true);
					else if (settings.substring(3,4).equals("2"))
						silentCheckBox.setChecked(true);
				}
				catch (Exception e) {
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		vibrateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					if(silentCheckBox.isChecked())
						silentCheckBox.setChecked(false);
					if(ringCheckBox.isChecked())
						ringCheckBox.setChecked(false);
				}
			}
		});
		ringCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					if(vibrateCheckBox.isChecked())
						vibrateCheckBox.setChecked(false);
					if(silentCheckBox.isChecked())
						silentCheckBox.setChecked(false);
				}
			}
		});
		
		silentCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					if(vibrateCheckBox.isChecked())
						vibrateCheckBox.setChecked(false);
					if(ringCheckBox.isChecked())
						ringCheckBox.setChecked(false);
				}
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File file = new File(context.getFilesDir(), settingName.getText().toString());
				file.delete();
				
				spinnerArray.clear();
				spinnerArray.add("");
				File[] files = context.getFilesDir().listFiles();
				for (File inFile : files) {
					if (!inFile.isDirectory()) {
						spinnerArray.add(inFile.getName());
						Log.d("File", inFile.getName());
					}
				}
				spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, spinnerArray));
			}
		});
		
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
					
					spinnerArray.clear();
					spinnerArray.add("");
					File[] files = context.getFilesDir().listFiles();
					for (File inFile : files) {
						if (!inFile.isDirectory()) {
							spinnerArray.add(inFile.getName());
							Log.d("File", inFile.getName());
						}
					}
					spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, spinnerArray));
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
