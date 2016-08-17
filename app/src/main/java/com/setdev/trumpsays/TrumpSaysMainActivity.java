package com.setdev.trumpsays;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;
import com.setdev.trumpsays.interfaces.OnTrumpGoFragmentInteractionListener;
import com.setdev.trumpsays.utils.Constants;
import com.setdev.trumpsays.utils.TypefaceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TrumpSaysMainActivity extends FragmentActivity implements
        OnTrumpGoFragmentInteractionListener {

    private static final String TAG = "TrumpSaysMainActivity";
    //private View mainRootView;

    public Tracker appTracker;

    private Handler handler = new Handler();
    final Runnable goToGame = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "goToGame");
            createAndAddFragment("Initialize","GameFragment", GameFragment.class, true, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        OneSignal.startInit(this).init();
        setContentView(R.layout.activity_trump_says_main);
        applyCustomStyles();
        checkPermission();

        AnalyticsApp app = (AnalyticsApp) getApplication();
        appTracker = app.getDefaultTracker();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onFragmentInteraction(String previousFragment, String upcomingFragment, Bundle data) {
        Log.d(TAG, "onFragmentInteraction: " + upcomingFragment);
        switch(upcomingFragment) {
            case "GameFragment":
                createAndAddFragment(previousFragment, upcomingFragment, GameFragment.class, true, data);
                break;
            case "TrackPlayAgain":
                Log.d(TAG, "TrackPlayAgain");

                appTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Play Again")
                        .build());
                break;
            case "TrackShare":
                Log.d(TAG, "TrackShare");

                appTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Share")
                        .build());
                break;
            case "TrackShirts":
                Log.d(TAG, "TrackShirts");

                appTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Shirt Link")
                        .build());
                break;

        }
    }

    public void createAndAddFragment(String previousFragment, String upcomingFragment, Class<? extends Fragment> fragClass, boolean
            addToBackStack, Bundle bundleData) {

        Fragment frag = getSupportFragmentManager().findFragmentByTag(upcomingFragment);
        if(frag == null) {
            Log.d(TAG, "frag is null: " + upcomingFragment);

            try {
                frag = fragClass.newInstance();
                frag.setArguments(bundleData);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "frag is not null : " + upcomingFragment);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, frag, upcomingFragment);
        if(addToBackStack)
        {
            ft.addToBackStack(upcomingFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount()==0)
        {
            onBackPressed();
        }
    }

    private void assignClickListeners() {
    }

    private void applyCustomStyles()
    {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Avenir-Medium.ttf"); //

    }

    private void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"CAMERA PERMISSIONS ARE NOT GRANTED");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(TrumpSaysMainActivity.this);
                builder.setMessage("You must enable the camera to play");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(TrumpSaysMainActivity.this,
                                new String[]{android.Manifest.permission.CAMERA},
                                Constants.MY_PERMISSIONS_REQUEST_ACCESS_CAMERA);
                    }
                });
                AlertDialog permissionRequestDialog = builder.create();
                permissionRequestDialog.show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        Constants.MY_PERMISSIONS_REQUEST_ACCESS_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
//            Toast.makeText(getContext(), "Camera ready", Toast.LENGTH_SHORT).show();
            onFragmentInteraction("Initialize", "GameFragment", null);

        }
    }

    public void hideSplashScreen() {
//        getWindow().getDecorView().findViewById(R.id.splash_screen).setVisibility(View.GONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {

            case Constants.MY_PERMISSIONS_REQUEST_ACCESS_CAMERA:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG,"CAMERA PERMISSIONS REQUEST GRANTED");
                    handler.post(goToGame);
                } else {

                    Log.d(TAG,"CAMERA PERMISSIONS REQUEST DENIED");
                    Toast.makeText(this,
                            "The camera is required to play!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }

}
