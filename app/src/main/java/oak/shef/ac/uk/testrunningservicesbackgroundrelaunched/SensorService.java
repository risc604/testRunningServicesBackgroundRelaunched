package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static oak.shef.ac.uk.testrunningservicesbackgroundrelaunched.MainActivity.cmds01;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    private static final String TAG = SensorService.class.getSimpleName();
    public int counter=0;
    public SensorService(Context applicationContext) {
        super();
        logFileCreated();    //debug message.
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000 * 3); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void logFileCreated()
    {
        try
        {
            //final String logFilePath = "/storage/emulated/0/Download/"+"Log_mt24.txt";
            final String logFilePath =  Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Download/Log_SBR.txt";
            final String cmds00 = "logcat -d -f ";
            //final String cmds01 = "logcat *:e *:i | grep \"(" + mPID + ")\"";

            //String mPID = String.valueOf(android.os.Process.myPid());
            //String cmds01 = "logcat *:e *:i | grep \"(" + mPID + ")\"";

            File f = new File(logFilePath);
            if (f.exists() && !f.isDirectory())
            {
                if (!f.delete())
                {
                    Log.w(TAG, "FAIL !! file delete NOT ok.");
                }
                else
                {
                    f = new File(logFilePath);
                }
            }

            java.lang.Process process = Runtime.getRuntime().exec(cmds01 + logFilePath);
            Log.w(TAG, "logFileCreated(), process: " + process.toString() +
                    ", path: " + logFilePath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}