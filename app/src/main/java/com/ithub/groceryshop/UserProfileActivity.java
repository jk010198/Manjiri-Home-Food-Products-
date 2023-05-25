package com.ithub.groceryshop;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserProfileActivity extends AppCompatActivity {

    EditText editText_name, editText_emailid, editText_occupation, editText_whatsappnumber, editText_alternetnumber, editText_address,
            editText_landmark, editText_password, editText_confirmpassword;
    String name, occupation, whatsappnumber, alternetnumber, address, landmark, password, confirmpassword, intent_username, sp_username, id;
    Spinner spinner_area;
    List<String> areaList = new ArrayList<String>();
    DatabaseReference dbref_area;
    SharedPreferences sp;
    Button btnUpdate;
    DatabaseReference dbref;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editText_name = findViewById(R.id.et_updateprofile_fullname);
        editText_emailid = findViewById(R.id.et_updateprofile_emailid);
        editText_occupation = findViewById(R.id.et_updateprofile_occupation);
        editText_whatsappnumber = findViewById(R.id.et_updateprofile_whatsappmobilenumber);
        editText_alternetnumber = findViewById(R.id.et_updateprofile_mobilenumber);
        editText_address = findViewById(R.id.et_updateprofile_address);
        editText_landmark = findViewById(R.id.et_updateprofile_landmark);
        spinner_area = findViewById(R.id.spinner_userprofile_area);
        editText_password = findViewById(R.id.et_updateprofile_password);
        editText_confirmpassword = findViewById(R.id.et_updateprofile_confirmpassword);
        btnUpdate = findViewById(R.id.btnupdate);
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait.");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.app_logo);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        intent_username = getIntent().getStringExtra("username");

        dbref = FirebaseDatabase.getInstance().getReference("Customers");
        dbref_area = FirebaseDatabase.getInstance().getReference("Area");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodUpdateData();
            }
        });

        dbref_area.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.show();
                areaList.clear();

                areaList.add("Select Area");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    areaList.add(ds.child("area").getValue().toString());
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
                /////////////////////////// spinner /////////////////////////////
                Adapter adapter = new ArrayAdapter<String>(UserProfileActivity.this, android.R.layout.simple_dropdown_item_1line, areaList);
                spinner_area.setAdapter((SpinnerAdapter) adapter);
                spinner_area.setBackgroundResource(R.drawable.round_edittext);

                if (!(sp.contains("username"))) {
                    loadData(intent_username);
                } else {
                    sp_username = sp.getString("username", "");
                    loadData(sp_username);
                }

                spinner_area.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ((TextView) spinner_area.getSelectedView()).setTextColor(Color.BLACK);
                        ((TextView) spinner_area.getSelectedView()).setTextSize(15);
                    }
                });
                //////////////////////////////////////////////
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void loadData(String username) {
        progressDialog.show();

        dbref.orderByChild("email").equalTo(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            RegistrationModel rm = ds.getValue(RegistrationModel.class);
                            id = rm.getId();
                            editText_name.setText(rm.getName());
                            editText_emailid.setText(rm.getEmail());
                            editText_occupation.setText(rm.getOccuption());
                            editText_whatsappnumber.setText(rm.getWhatsappnumber());
                            editText_alternetnumber.setText(rm.getMobileno());
                            editText_address.setText(rm.getAddress());
                            editText_landmark.setText(rm.getLandmark());
                            spinner_area.setSelection(getIndex(spinner_area, rm.getArea()));
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //setup area
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                return i;
            }
        }
        return 0;
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
                    ///////////////////// internet gotted ///////
                    if (name.isEmpty() || occupation.isEmpty() || whatsappnumber.isEmpty() || address.isEmpty() || landmark.isEmpty() ||
                            spinner_area.getSelectedItem().equals("Select Area")) {

                        if (name.isEmpty()) {
                            editText_name.setError("Please enter your full name.");
                            progressDialog.dismiss();
                        }

                        if (occupation.isEmpty()) {
                            editText_occupation.setError("Please enter your Occupation.");
                            progressDialog.dismiss();
                        }

                        if (whatsappnumber.isEmpty()) {
                            editText_whatsappnumber.setError("Please enter WhatsApp number.");
                            progressDialog.dismiss();
                        }

                        if (address.isEmpty()) {
                            editText_address.setError("Please enter address.");
                            progressDialog.dismiss();
                        }

                        if (landmark.isEmpty()) {
                            editText_landmark.setError("Please enter landmark.");
                            progressDialog.dismiss();
                        }

                        if (spinner_area.getSelectedItem().equals("Select Area")) {
                            Toast.makeText(UserProfileActivity.this, "Please select area.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    if (name.isEmpty()) {
                        editText_name.setError("Please enter your full name.");
                        progressDialog.dismiss();
                    } else if (occupation.isEmpty()) {
                        editText_occupation.setError("Please enter your Occupation.");
                        progressDialog.dismiss();
                    } else if (whatsappnumber.isEmpty() || whatsappnumber.length() < 10) {
                        editText_whatsappnumber.setError("Please enter valid number.");
                        progressDialog.dismiss();
                    } else if (alternetnumber.length() >= 1 && alternetnumber.length() < 10) {
                        editText_alternetnumber.setError("Please enter valid mobile number.");
                        progressDialog.dismiss();
                    } else if (address.isEmpty()) {
                        editText_address.setError("Please enter address");
                        progressDialog.dismiss();
                    } else if (spinner_area.getSelectedItem().equals("Select Area")) {
                        Toast.makeText(UserProfileActivity.this, "Please select area.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (password.length() >= 1 && password.length() < 6 && password.length() < 12) {
                        editText_password.setError("Password should minimum 6 and maximum 12.");
                        progressDialog.dismiss();
                    } else if (confirmpassword.length() >= 1 && confirmpassword.length() < 6 && confirmpassword.length() < 12) {
                        editText_confirmpassword.setError("Password should minimum 6 and maximum 12.");
                        progressDialog.dismiss();
                    } else if (!(password.equals(confirmpassword))) {
                        editText_password.setError("Passowrd does not match.");
                        editText_confirmpassword.setError("Password does not match.");
                        progressDialog.dismiss();
                    } else {

                        dbref.child(id).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                        dbref.child(id).child("occuption").setValue(occupation).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                        dbref.child(id).child("address").setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                        dbref.child(id).child("landmark").setValue(landmark).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                        dbref.child(id).child("area").setValue(spinner_area.getSelectedItem()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                        if (editText_password.getText().toString().length() > 0 && editText_password.getText().toString().length() < 12) {
                            dbref.child(id).child("password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    onBackPressed();
                                }
                            });
                        }
                        updateContactNumber();
                        Toast.makeText(UserProfileActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                    }
                    //////////////////////////////////////////////////////
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

    public void methodUpdateData() {

        name = editText_name.getText().toString();
        occupation = editText_occupation.getText().toString();
        whatsappnumber = editText_whatsappnumber.getText().toString();
        alternetnumber = editText_alternetnumber.getText().toString();
        address = editText_address.getText().toString();
        landmark = editText_landmark.getText().toString();
        password = editText_password.getText().toString();
        confirmpassword = editText_confirmpassword.getText().toString();

        NetCheckTask task = new NetCheckTask();
        task.execute();
    }

    public void updateContactNumber() {

        if (!(whatsappnumber.equals(editText_whatsappnumber.getText().toString()))) {
            dbref.orderByChild("whatsappnumber").equalTo(whatsappnumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progressDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "WhatsApp number is already exists, please enter another number.", Toast.LENGTH_SHORT).show();
                    } else {
                        dbref.child(id).child("whatsappnumber").setValue(editText_whatsappnumber.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }

        if (!(alternetnumber.equals(editText_alternetnumber.getText().toString()))) {
            dbref.orderByChild("mobileno").equalTo(alternetnumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progressDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "Alternet mobile number is already exists, please enter another number.", Toast.LENGTH_SHORT).show();
                    } else {
                        dbref.child(id).child("mobileno").setValue(editText_whatsappnumber.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
}