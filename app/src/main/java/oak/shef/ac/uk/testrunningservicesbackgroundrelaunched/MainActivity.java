/**
 * created by Fabio Ciravegna, The University of Sheffield, f.ciravegna@shef.ac.uk
 * LIcence: MIT
 * Copyright (c) 2016 (c) Fabio Ciravegna

 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Intent mServiceIntent;
    private SensorService mSensorService;

    Context ctx;

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marshmallowPermission();


        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult()...");

        int grantTotal = 0;
        Log.w(TAG, "permissions: " + Arrays.toString(permissions) +
                ", grantResults: " + Arrays.toString(grantResults));

        for (int i = 0; i < grantResults.length; i++) {
            grantTotal += grantResults[i];
        }
        Log.w(TAG, "grantTotal: " + grantTotal);

        if (grantTotal < 0) {
            marshmallowPermission();
        } else {
            switch (requestCode) {
                case 1:
                    if ((grantResults.length > 0) &&
                            (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        //initAction();
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult(), Permission denied.");
                        //Toast.makeText(getApplicationContext(), "Permission denied",
                        Toast.makeText(getApplicationContext(), "permission_denied",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }


    //private final static String mPID = String.valueOf(android.os.Process.myPid());
    final static String mPID = String.valueOf(android.os.Process.myPid());
    //final static String cmds01 = "logcat *:v *:w *:e *:d *:i | grep \"(" + mPID + ")\" -f ";
    final static String cmds01 = "logcat *:v | grep \"(" + mPID + ")\" -f ";



    private boolean marshmallowPermission()
    {
        Log.i(TAG, "marshmallowPermission() ...");

        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //int blePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int sysPermissionState = PackageManager.PERMISSION_GRANTED;
        //Log.d(TAG,  "blePermission: " + blePermission +
        //        ", storagePermission: " + storagePermission +
        //        ", sysPermissionState: " + sysPermissionState);

        if (storagePermission != sysPermissionState)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH_ADMIN))
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{   Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                        },  1);
                ////Toast.makeText(this, "Please give App those permission To Run ...", Toast.LENGTH_LONG).show();
                Log.d(TAG, " Error !! PERMISSION_DENIED ");
                return false;
            }
            else
                return true;
        }
        else
        {
            Log.d(TAG, " PERMISSION_GRANTED ");
            return true;
        }
    }

}


