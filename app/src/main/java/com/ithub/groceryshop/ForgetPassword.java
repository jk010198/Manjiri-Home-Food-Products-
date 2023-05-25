package com.ithub.groceryshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ithub.groceryshop.Models.RegistrationModel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity {

    EditText editText_email, editText_password, editText_confirmpassword;
    String email, passwrord, confirmpassword;
    DatabaseReference dbref;
    ProgressDialog progressDialog;
    String db_email, db_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        editText_email = findViewById(R.id.et_forgetpassword_email);
        editText_password = findViewById(R.id.et_forgetpassword_password);
        editText_confirmpassword = findViewById(R.id.et_forgetpassword_conf_password);
        progressDialog = new ProgressDialog(this);

        dbref = FirebaseDatabase.getInstance().getReference("Customers");
        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.app_logo);
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
                    String Validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

                    Matcher matcher = Pattern.compile(Validemail).matcher(email);
                    if (email.isEmpty() || passwrord.isEmpty() || confirmpassword.isEmpty()) {

                        if (email.isEmpty()) {
                            editText_email.setError("Please enter email.");
                            progressDialog.dismiss();
                        }

                        if (passwrord.isEmpty()) {
                            editText_password.setError("Please enter password.");
                            progressDialog.dismiss();
                        }

                        if (confirmpassword.isEmpty()) {
                            editText_confirmpassword.setError("Please enter password.");
                            progressDialog.dismiss();
                        }
                    }

                    if (!(matcher.matches())) {
                        editText_email.setError("Please enter valid email-id.");
                        progressDialog.dismiss();
                    } else if (passwrord.isEmpty()) {
                        editText_password.setError("Please enter password.");
                        progressDialog.dismiss();
                    } else if (confirmpassword.isEmpty()) {
                        editText_confirmpassword.setError("Please enter password.");
                        progressDialog.dismiss();
                    } else if (passwrord.length() < 6 || passwrord.length() > 12) {
                        editText_password.setError("Password should minimum 6 and maximum 12.");
                        progressDialog.dismiss();
                    } else if (confirmpassword.length() < 6 || confirmpassword.length() > 12) {
                        editText_confirmpassword.setError("Password should minimum 6 and maximum 12.");
                        progressDialog.dismiss();
                    } else if (!(passwrord.equals(confirmpassword))) {
                        editText_password.setError("Passowrd does not match.");
                        editText_confirmpassword.setError("Password does not match.");
                        progressDialog.dismiss();
                    } else {

                        dbref.orderByChild("email").equalTo(email)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            RegistrationModel rm = ds.getValue(RegistrationModel.class);

                                            db_email = rm.getEmail();
                                            db_id = rm.getId();

                                            if (email.equals(db_email)) {
                                                updateData(passwrord);
                                            } else {
                                                Toast.makeText(ForgetPassword.this, "This email is not register with us please enter valid email-id.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        //Toast.makeText(ForgetPassword.this, "", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                    //////////////////////////////////
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

    public void btnChangedPassword(View view) {
        progressDialog.show();
        email = editText_email.getText().toString();
        passwrord = editText_password.getText().toString();
        confirmpassword = editText_confirmpassword.getText().toString();

        NetCheckTask task = new NetCheckTask();
        task.execute();
    }


    public void updateData(String password) {
        dbref.child(db_id).child("password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPassword.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }
}