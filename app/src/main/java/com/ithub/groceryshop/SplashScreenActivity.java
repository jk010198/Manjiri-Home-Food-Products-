package com.ithub.groceryshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imageView;
    int myAppVersion = 2, serverAppVersion = 0; //2nd version 02-03-2021
    ProgressDialog progressDialog;
    DatabaseReference dbref_AppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.iv);
        progressDialog = new ProgressDialog(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dbref_AppVersion = FirebaseDatabase.getInstance().getReference("App_version");

        FirebaseMessaging.getInstance().subscribeToTopic("user_notify");
        Intent intent = getIntent();
        boolean exitcode = intent.getBooleanExtra("exit_code", false);
        methodGetFirebaseDatabaseAppVersion();

        if (exitcode) {
            finish();
            System.exit(0);
        }

        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.app_logo);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NetCheckTask task = new NetCheckTask();
                task.execute();
            }
        }, 5000);

    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    class NetCheckTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 0;
            try {
                Socket socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
                socket.connect(socketAddress, 1500);
                socket.close();
                result = 1;
            } catch (IOException e) {
                e.printStackTrace();
                result = 0;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (isConnected()) {
                if (result == 1) {
                    /////////////////////Checking For Latest Version///////

                    if (myAppVersion >= serverAppVersion) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), UserHomePageActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        }, 2000);
                    } else {
                        AlertDialog.Builder adb = new AlertDialog.Builder(SplashScreenActivity.this);
                        adb.setTitle("Update Mangiri Home Food Product.");
                        adb.setIcon(R.drawable.app_logo);
                        adb.setMessage("The new version of Mangiri Home Food Product App is available now. To use Mangiri Home Food Product please update the app from PlayStore. \n\nThank you.");
                        adb.setPositiveButton("Update App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                                startActivity(intent);
                                finish();
                            }
                        });
                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                        adb.show();
                    }
                    ///////////////////
                }

                if (result == 0) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection...", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Reload.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    snackbar.show();
                }
            } else {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection available...", Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("Reload.", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                snackbar.show();
            }
            super.onPostExecute(result);
        }

    }

    public void methodGetFirebaseDatabaseAppVersion() {
        dbref_AppVersion.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serverAppVersion = dataSnapshot.child("Version").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}
/*
/*if (appClose.equals("no")) {
                                    Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                } else if (appClose.equals("yes")) {
                                    adbLockDown = new AlertDialog.Builder(SplashScreenActivity.this);
                                    adbLockDown.setTitle("KTC Life.");
                                    adbLockDown.setMessage(appCloseMsg);
                                    adbLockDown.setCancelable(false);
                                    adbLockDown.setPositiveButton(appCloseMsgBtn1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
                                    adbLockDown.show();
                                }*/