package com.example.settingmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FrontPage extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.front_page);
		
		final Button quick = (Button) findViewById(R.id.quick);
		final Button editor = (Button) findViewById(R.id.editor);
		
		final LinearLayout favorites = (LinearLayout) findViewById(R.id.favoritesScroll);
		final LayoutParams favParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
		int numFavs = 0;
		
		//!-------------------------Build favorites-------------------------!//
		File[] files = getApplicationContext().getFilesDir().listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				//check if favorite
				try {
					FileInputStream input_stream = openFileInput(file.getName());
					InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
					BufferedReader buffered_reader = new BufferedReader(input_stream_reader);
					String received = "";
					StringBuilder string_builder = new StringBuilder();
					while ((received = buffered_reader.readLine()) != null)
						string_builder.append(received);
					input_stream.close();
					String settings = string_builder.toString();
					Log.d("FrontPage", "Checking if favorite...");
					if(!settings.substring(8, 9).equals("1") || numFavs > 4)
						continue;
					Log.d("FrontPage", "Favorite found!");
					final Button fav = new Button(this);
					fav.setText(file.getName());
					fav.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							AudioManager audiomanager =(AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
							BluetoothAdapter blueTooth = BluetoothAdapter.getDefaultAdapter();  
							WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
							String settings = "";
							
							try
							{
								FileInputStream input_stream = openFileInput(fav.getText().toString());
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
										 ConnectivityManager conman = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);   							   
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
										 ConnectivityManager conman = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);   							   
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
								Toast.makeText(getApplicationContext(), fav.getText() + " Loaded!", 15).show();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
					favorites.addView(fav, favParams);
					numFavs++;
				}
				catch(Exception e) { Log.d("Catch", "Failed to build favorites");}
			}
		}
		//!-----------------------End build favorites-----------------------!//
		
		quick.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Instant.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(i);
				finish();
			}
		});
		
		editor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(i);
				finish();
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