package com.example.ivan.workshopa;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Light;
import com.aware.Proximity;
import com.aware.Rotation;
import com.aware.providers.Accelerometer_Provider;


public class MainActivity extends ActionBarActivity {

    //private static AccelerometerObserver accelObs;


    //Media Player implementation
    private MediaPlayer mediaPlayer;
    private Button controlPlayerButton;
    private AudioManager audio;
    public static final int UPDATE_THE_VOLUME = 0;
    public VolumeHandler volumeHandler;

    //Accelerometer
    public static double acc_x;
    public static double acc_y;
    public static double acc_z;
    public static long timestamp_acc;
    //rotation
    public static double rotation_x;
    public static double rotation_y;
    public static double rotation_z;
    public static double rotation_cos;
    public static long timestamp_rot;
    //light
    public static double light;
    public static long timestamp_lig;
    //proximity
    public static double proximity;
    public static long timestamp_prx;
    //microphone
    public static double microphone;
    //volume
    public static double volume;
    public static long timestamp_vol;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button to play/stop player
        controlPlayerButton = (Button) findViewById(R.id.togglebutton);
        //initializing the MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeHandler = new VolumeHandler(Looper.getMainLooper());




        //Register the Content Observer
     //   accelObs = new AccelerometerObserver(new Handler());
      // getContentResolver().registerContentObserver(Accelerometer_Provider.Accelerometer_Data.CONTENT_URI, true, accelObs);

        //Tell AWARE that I want to modify the settings and activate the accelerometer


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


            if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
                //Do something
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

                Log.d("PLAYER","Down " + String.valueOf(audio.getStreamVolume(AudioManager.STREAM_MUSIC)));
                return true;
            }
            if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
                //Do something

                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                Log.d("PLAYER", "UP " + String.valueOf(audio.getStreamVolume(AudioManager.STREAM_MUSIC)));
                return true;
            }
         return false;



    }

    //Listener for tougle button start/stop play
    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        Log.d("PLAYER", "Button toggled");
        if (on) {
            // Start play
            mediaPlayer.start();

            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ROTATION, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_PROXIMITY, true);

            IntentFilter filter = new IntentFilter();
            filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);
            filter.addAction(Light.ACTION_AWARE_LIGHT);
            filter.addAction(Rotation.ACTION_AWARE_ROTATION);
            filter.addAction(Proximity.ACTION_AWARE_PROXIMITY);
            registerReceiver(contextBR, filter);


            sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        } else {
            // Stop play
            mediaPlayer.stop();
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, false);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, false);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ROTATION, false);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_PROXIMITY, false);


            if(contextBR != null)
                unregisterReceiver(contextBR);
            sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        }
    }
    //Handler to update the volume
    private final class VolumeHandler extends Handler {

        public VolumeHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_THE_VOLUME: {

                    Log.d("VOLUME","SET VOLUME to " + msg.arg1);

//                    Toast t = Toast.makeText(context, (String) msg.obj,
//                            Toast.LENGTH_SHORT);
//                    t.show();
                }
                default:
                    break;
            }
        }
    }


    private ContextReceiver contextBR = new ContextReceiver();
    public class ContextReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            //Sensors Data


            //Get the raw data
            ContentValues acc_data = (ContentValues)intent.getParcelableExtra(Accelerometer.EXTRA_DATA);
            acc_x = (double)acc_data.get("double_values_0");
            acc_y = (double)acc_data.get("double_values_1");
            acc_z = (double)acc_data.get("double_values_2");
            timestamp_acc = (long)acc_data.get("timestamp");
            //Rotation data
            ContentValues rotation_data = (ContentValues)intent.getParcelableExtra(Rotation.EXTRA_DATA);
            rotation_x = (double)rotation_data.get("double_values_0");
            rotation_y = (double)rotation_data.get("double_values_1");
            rotation_z = (double)rotation_data.get("double_values_2");
            rotation_cos = (double)rotation_data.get("double_values_3");
            timestamp_rot = (long)rotation_data.get("timestamp");
            //Light
            ContentValues light_data = (ContentValues)intent.getParcelableExtra(Light.EXTRA_DATA);
            light = (double)light_data.get("double_light_lux");
            timestamp_lig = (long)light_data.get("timestamp");
            //Proximity
            ContentValues proximity_data = (ContentValues)intent.getParcelableExtra(Proximity.EXTRA_DATA);
            proximity = (double)proximity_data.get("double_proximity");
            timestamp_prx = (long)proximity_data.get("timestamp");
            microphone=0;
            volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            ContentValues data = new ContentValues();
            data.put(Provider.Smart_Volume_Data.TIMESTAMP, System.currentTimeMillis());
            data.put(Provider.Smart_Volume_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
            data.put(Provider.Smart_Volume_Data.A_VALUES_0,acc_x);
            data.put(Provider.Smart_Volume_Data.A_VALUES_1,acc_y);
            data.put(Provider.Smart_Volume_Data.A_VALUES_2,acc_z);
            data.put(Provider.Smart_Volume_Data.R_VALUES_0,rotation_x);
            data.put(Provider.Smart_Volume_Data.R_VALUES_1,rotation_y);
            data.put(Provider.Smart_Volume_Data.R_VALUES_2,rotation_z);
            data.put(Provider.Smart_Volume_Data.LUX,light);
            data.put(Provider.Smart_Volume_Data.PROXIMITY,proximity);
            data.put(Provider.Smart_Volume_Data.MICROPHONE,microphone);
            data.put(Provider.Smart_Volume_Data.VOLUME,volume);
            getContentResolver().insert(Provider.Smart_Volume_Data.CONTENT_URI, data);

            //Process raw data
            Log.d("DEMO", "ACC: " + acc_data.get("timestamp").toString()+ "Light: " + light_data.get("timestamp").toString());
        }


    }


//    private static AccelerometerBR accelBR = new AccelerometerBR();
//    public static class AccelerometerBR extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent){
//            //Get the raw data
//            ContentValues raw_data = (ContentValues)intent.getParcelableExtra(Accelerometer.EXTRA_DATA);
//
//            //Process raw data
//            Log.d("DEMO", raw_data.toString() );
//        }
//    }

    //Observe changes in the database
    //ATTENTION THE CONTENT OBSERVER BUFFER 250 samples before launching onChange.
//    public class AccelerometerObserver extends ContentObserver {
//        /**
//         * Creates a content observer.
//         *
//         * @param handler The handler to run {@link #onChange} on, or null if none.
//         */
//        public AccelerometerObserver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange){
//            super.onChange(selfChange);
//            Log.d("AXIS X", "Inside onChange");
//            //This is not working right now
//            Cursor raw_data = getContentResolver().query(Accelerometer_Provider.Accelerometer_Data.CONTENT_URI,
//                    null,
//                    null,
//                    null,
//                    null);
//            if (raw_data != null && raw_data.moveToFirst()){
//                do {
//                    double x = raw_data.getDouble(raw_data.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_0));
//                    Log.d("AXIS X", "X="+x);
//                }
//                while (raw_data.moveToNext());
//            }
//            if (raw_data!=null && ! raw_data.isClosed()){
//                raw_data.close();
//            }
//        }
//
//
//    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, false);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        if(contextBR != null)
         unregisterReceiver(contextBR);
        //getContentResolver().unregisterContentObserver(accelObs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
