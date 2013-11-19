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
import android.media.MediaRouter.VolumeCallback;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
	int ringVol = 0, mediaVol = 0;
	
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
		final Switch rotateCheckBox = (Switch) findViewById(R.id.switchRotate);
		final Switch mediaSwitch = (Switch) findViewById(R.id.Media);
		final Button createButton = (Button) findViewById(R.id.mainCreate);
		final Button loadButton = (Button) findViewById(R.id.mainLoad);
		final EditText settingName = (EditText) findViewById(R.id.name);
		final SeekBar seekRinger = (SeekBar) findViewById(R.id.seekRinger);
		final SeekBar seekMedia = (SeekBar) findViewById(R.id.seekMedia);
		final TextView ringerText = (TextView) findViewById(R.id.ringerText);
		final TextView mediaText = (TextView) findViewById(R.id.mediaText);
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
					else {
						ringCheckBox.setChecked(false);
						vibrateCheckBox.setChecked(false);
						silentCheckBox.setChecked(false);
					}
					if(settings.substring(4,5).equals("1"))
						rotateCheckBox.setChecked(true);
					else
						rotateCheckBox.setChecked(false);
					seekRinger.setProgress(Integer.parseInt(settings.substring(5,6)));
					ringerText.setText("Ringer Volume = " + settings.substring(5,6));
					if(settings.substring(6,7).equals("1"))
						mediaSwitch.setEnabled(true);
					else
						mediaSwitch.setEnabled(false);
					seekMedia.setProgress(Integer.parseInt(settings.substring(7,8)));
					mediaText.setText("Media Volume = " + settings.substring(7,8));
				}
				catch (Exception e) {
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		seekRinger.setMax(7);
		seekRinger.setEnabled(false);
		seekRinger.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(!fromUser)
					return;
				ringVol = progress;
				ringerText.setText("Ringer Volume = " + ringVol);
			}
		});
		
		seekMedia.setMax(7);
		seekMedia.setEnabled(false);
		seekMedia.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(!fromUser)
					return;
				mediaVol = progress;
				mediaText.setText("Media Volume = " + mediaVol);
			}
		});
		
		mediaSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					seekMedia.setEnabled(true);
				else
				{
					mediaText.setText("Media Volume");
					seekMedia.setProgress(0);
					seekMedia.setEnabled(false);
				}
			}
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
					seekRinger.setEnabled(true);
				}
				else
				{
					ringerText.setText("Ringer Volume");
					seekRinger.setProgress(0);
					seekRinger.setEnabled(false);
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
				
				blueToothCheckBox.setChecked(false);
				wifiCheckBox.setChecked(false);
				mobileNetworkCheckBox.setChecked(false);
				ringCheckBox.setChecked(false);
				vibrateCheckBox.setChecked(false);
				silentCheckBox.setChecked(false);
				rotateCheckBox.setChecked(false);
				
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
					else if (silentCheckBox.isChecked())
					{
						settings = settings + "2";
					}
					else
						settings = settings + "3";
					if(rotateCheckBox.isChecked())
						settings = settings + "1";
					else
						settings = settings + "0";
					if(seekRinger.isEnabled())
						settings = settings + String.valueOf(ringVol);
					else
						settings = settings + 0;
					if(mediaSwitch.isEnabled())
						settings = settings + "1";
					else
						settings = settings + "0";
					settings = settings + String.valueOf(mediaVol);
						
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
						audiomanager.setStreamVolume(AudioManager.STREAM_RING, Integer.parseInt(settings.substring(5,6)), 0);
					}
					else if (settings.substring(3,4).equals("1"))
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					}
					else if (settings.substring(3,4).equals("2"))
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					}
					if(settings.substring(4,5).equals("0"))
						Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
					else
						Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
					if (settings.substring(6,7).equals("1"))
						audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(settings.substring(7,8)), 0);						
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
