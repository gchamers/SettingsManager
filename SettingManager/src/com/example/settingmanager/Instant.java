package com.example.settingmanager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Instant extends Activity {
	boolean userChangeVol = false, userChangeVib = false, visualize = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instant_page);
		
		final Switch wifiCheckBox = (Switch) findViewById(R.id.switchWifi);
		final Switch blueToothCheckBox = (Switch) findViewById(R.id.switchBluetooth);
		final Switch mobileNetworkCheckBox = (Switch) findViewById(R.id.switchMobile);
		final Switch ringCheckBox = (Switch) findViewById(R.id.switchRing);
		final Switch vibrateCheckBox = (Switch) findViewById(R.id.switchVibrate);
		final Switch rotateCheckBox = (Switch) findViewById(R.id.switchRotate);
		final Switch mediaSwitch = (Switch) findViewById(R.id.Media);
		final SeekBar seekRinger = (SeekBar) findViewById(R.id.seekRinger);
		final SeekBar seekMedia = (SeekBar) findViewById(R.id.seekMedia);
		final TextView ringerText = (TextView) findViewById(R.id.ringerText);
		final TextView mediaText = (TextView) findViewById(R.id.mediaText);
		final Context context = this;
		final BluetoothAdapter blueTooth = BluetoothAdapter.getDefaultAdapter();
		final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		final AudioManager audiomanager =(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		
		blueToothCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
				{
					if(isChecked)
						blueTooth.enable();
					else
						blueTooth.disable();
				}
			}
		});
		
		wifiCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
				{
					if(isChecked)
						wifi.setWifiEnabled(true);
					else
						wifi.setWifiEnabled(false);
				}
			}
		});
		
		mobileNetworkCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
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
						 if(isChecked)
							 setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
						 else
							 setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
					}
					catch (Exception e){}
				}
			}
		});
		
		ringCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
				{
					if(isChecked)
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						if(vibrateCheckBox.isChecked())
							userChangeVol = true;
						vibrateCheckBox.setChecked(false);
						seekRinger.setEnabled(true);
					}
					else
					{
						Toast.makeText(getApplicationContext(), "" + userChangeVib, 5).show();
						if(!userChangeVib)
							audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						else
							userChangeVib = false;
						seekRinger.setEnabled(false);
						seekRinger.setProgress(0);
						ringerText.setText("Ring Volume");
					}
				}
			}
		});
		
		seekRinger.setMax(7);
		seekRinger.setEnabled(false);
		seekRinger.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ringerText.setText("Ringer Volume = " + progress);
				audiomanager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
			}
		});
		
		vibrateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
				{
					if(isChecked)
					{
						audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
						if(ringCheckBox.isChecked())
							userChangeVib = true;
						ringCheckBox.setChecked(false);
					}
					else
					{
						if(!userChangeVol)
							audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						else
							userChangeVol = false;
					}
				}
			}
		});
		
		rotateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
				{
					if(isChecked)
						Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
					else
						Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				}
			}
		});
		
		mediaSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!visualize)
				{
					if(isChecked)
					{
						seekMedia.setEnabled(true);
					}
					else
					{
						seekMedia.setEnabled(false);
						seekMedia.setProgress(0);
						mediaText.setText("Media Volume");
						audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					}
				}
			}
		});
		
		seekMedia.setMax(7);
		seekMedia.setEnabled(false);
		seekMedia.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mediaText.setText("Media Volume = " + progress);
				audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
		
		//!--------------------visualize current settings--------------------!
				visualize = true;
				//bluetooth
				if(blueTooth.isEnabled())
					blueToothCheckBox.setChecked(true);
				//wifi
				if(wifi.isWifiEnabled())
					wifiCheckBox.setChecked(true);
				//mobile data
				//TODO: This isn't working
				boolean mobileDataEnabled = false;
			    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			    try {
			        Class cmClass = Class.forName(cm.getClass().getName());
			        Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			        method.setAccessible(true); 
			        mobileDataEnabled = (Boolean)method.invoke(cm);
			    } catch (Exception e) {}
				if(mobileDataEnabled)
					mobileNetworkCheckBox.setChecked(true);
				//ringer
				if(audiomanager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
				{
					ringCheckBox.setChecked(true);
					seekRinger.setEnabled(true);
					seekRinger.setProgress(audiomanager.getStreamVolume(AudioManager.STREAM_RING));
					//ringerText.setText("Ringer Volume = " + seekRinger.getProgress());
				}
				//vibrate
				else if(audiomanager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
					vibrateCheckBox.setChecked(true);
				//rotate
				if (android.provider.Settings.System.getInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
					rotateCheckBox.setChecked(true);
				//media
				if(audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0)
				{
					mediaSwitch.setChecked(true);
					seekMedia.setEnabled(true);
					seekMedia.setProgress(audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC));
					//mediaText.setText("Media Volume = " + seekMedia.getProgress());
				}
			    visualize = false;
				//!--------------------end visualize--------------------!
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId())
		{
			case R.id.back:
				Intent i = new Intent(getApplicationContext(), FrontPage.class);
				startActivity(i);
				finish();
				break;
			default:
				break;
		}
		return true;
	}
}