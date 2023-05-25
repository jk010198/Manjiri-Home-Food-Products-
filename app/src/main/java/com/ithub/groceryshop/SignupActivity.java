package com.ithub.groceryshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ithub.groceryshop.Models.RegistrationModel;
import com.ithub.groceryshop.databinding.ActivitySignupBinding;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding mBinding;

    TextView textView_forwardto_login;
    EditText editText_fullname, editText_emailid, editText_occupation, editText_whatsappnumber, editText_mobilenumber, editText_address, editText_landmark,
            editText_password, editText_confirmpassword;
    FrameLayout btnSignin;
    String id, name, emailid, occupation, whatsapp_number, alternate_mobilenumber, address, landmark, password, confirm_password;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        textView_forwardto_login = findViewById(R.id.textView_signup_forwardtologin);

        editText_fullname = findViewById(R.id.et_signup_fullname);
        editText_emailid = findViewById(R.id.et_signup_emailid);
        editText_occupation = findViewById(R.id.et_signup_occupation);
        editText_whatsappnumber = findViewById(R.id.et_signup_whatsappmobilenumber);
        editText_mobilenumber = findViewById(R.id.et_signup_mobilenumber);
        editText_address = findViewById(R.id.et_signup_address);
        editText_landmark = findViewById(R.id.et_signup_landmark);
        editText_password = findViewById(R.id.et_signup_password);
        editText_confirmpassword = findViewById(R.id.et_signup_confirmpassword);
        btnSignin = findViewById(R.id.btnSignin);

        progressDialog = new ProgressDialog(this);
        textView_forwardto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        // ref from firebase database
        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference("Customers");
        ///////////////

        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait");
        progressDialog.setIcon(R.drawable.app_logo);
        progressDialog.setCancelable(false);
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

                    Matcher matcher = Pattern.compile(Validemail).matcher(emailid);

                    if (name.isEmpty() || emailid.isEmpty() || occupation.isEmpty() || whatsapp_number.isEmpty() || address.isEmpty() || landmark.isEmpty()
                            || password.isEmpty() || confirm_password.isEmpty()) {

                        if (name.isEmpty()) {
                            editText_fullname.setError("Please enter name.");
                            progressDialog.dismiss();
                        }

                        if (emailid.isEmpty() || (!(matcher.matches()))) {
                            editText_emailid.setError("Please enter valid email.");
                            progressDialog.dismiss();
                        }

                        if (occupation.isEmpty()) {
                            editText_occupation.setError("Please enter your Occupation.");
                            progressDialog.dismiss();
                        }

                        if (whatsapp_number.isEmpty()) {
                            editText_whatsappnumber.setError("Please enter WhatsApp number.");
                            progressDialog.dismiss();
                        }

                        if (address.isEmpty()) {
                            editText_address.setError("Please enter address");
                            progressDialog.dismiss();
                        }

                        if (landmark.isEmpty()) {
                            editText_landmark.setError("Please enter landmark");
                            progressDialog.dismiss();
                        }

                        if (password.isEmpty()) {
                            editText_password.setError("Please enter password.");
                            progressDialog.dismiss();
                        }

                        if (confirm_password.isEmpty()) {
                            editText_confirmpassword.setError("Please enter password.");
                            progressDialog.dismiss();
                        }
                    }

                    if (name.isEmpty()) {
                        editText_fullname.setError("Please enter name.");
                        progressDialog.dismiss();
                    } else if (!(matcher.matches())) {
                        editText_emailid.setError("Please enter valid email.");
                        progressDialog.dismiss();
                    } else if (occupation.isEmpty()) {
                        editText_occupation.setError("Please enter your Occupation.");
                        progressDialog.dismiss();
                    } else if (whatsapp_number.isEmpty() || whatsapp_number.length() < 10) {
                        editText_whatsappnumber.setError("Please enter valid number.");
                        progressDialog.dismiss();
                    } else if (alternate_mobilenumber.length() >= 1 && alternate_mobilenumber.length() < 10) {
                        editText_mobilenumber.setError("Please enter valid mobile number.");
                        progressDialog.dismiss();
                    } else if (address.isEmpty()) {
                        editText_address.setError("Please enter address.");
                        progressDialog.dismiss();
                    } else if (landmark.isEmpty()) {
                        editText_landmark.setError("Please enter landmark.");
                        progressDialog.dismiss();
                    } else if (password.isEmpty()) {
                        editText_password.setError("Please enter password.");
                        progressDialog.dismiss();
                    } else if (confirm_password.isEmpty()) {
                        editText_confirmpassword.setError("Please enter password.");
                        progressDialog.dismiss();
                    } else if (password.length() < 6 || password.length() > 12) {
                        editText_password.setError("Password should minimum 6 and maximum 12.");
                        progressDialog.dismiss();
                    } else if (confirm_password.length() < 6 || confirm_password.length() > 12) {
                        editText_confirmpassword.setError("Password should minimum 6 and maximum 12.");
                        progressDialog.dismiss();
                    } else if (!(password.equals(confirm_password))) {
                        editText_password.setError("Passowrd does not match.");
                        editText_confirmpassword.setError("Password does not match.");
                        progressDialog.dismiss();
                    } else {
                        contactCheakingMethod();
                    }
                    //////////////////////////
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

    public void btnSignin(View view) {

        progressDialog.show();
        name = editText_fullname.getText().toString();
        emailid = editText_emailid.getText().toString();
        occupation = editText_occupation.getText().toString();
        whatsapp_number = editText_whatsappnumber.getText().toString();
        alternate_mobilenumber = editText_mobilenumber.getText().toString();
        address = editText_address.getText().toString();
        landmark = editText_landmark.getText().toString();
        password = editText_password.getText().toString();
        confirm_password = editText_confirmpassword.getText().toString();

        NetCheckTask task = new NetCheckTask();
        task.execute();
    }

    public void contactCheakingMethod() {

        dbref.orderByChild("whatsappnumber").equalTo(whatsapp_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "WhatsApp number is already exists, please enter another number.", Toast.LENGTH_LONG).show();
                } else {
                    if (!(alternate_mobilenumber.isEmpty())) {
                        dbref.orderByChild("mobileno").equalTo(alternate_mobilenumber).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignupActivity.this, "Alternet mobile number is already exists, please enter another number.", Toast.LENGTH_SHORT).show();
                                } else {
                                    addUser();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(SignupActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        addUser();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignupActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void addUser() {
        auth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(SignupActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            id = dbref.push().getKey();
                            RegistrationModel rm = new RegistrationModel(id, "", name, emailid, occupation, whatsapp_number,
                                    alternate_mobilenumber, address, landmark, "", password);
                            dbref.child(id).setValue(rm);
                            Toast.makeText(SignupActivity.this, "Sign in Successful.", Toast.LENGTH_SHORT).show();
                            animatedButtonWidth();
                            fadeOutTextSetProgress();
                            nextAction();
                        } else {
                            Toast.makeText(SignupActivity.this, "Sorry this Email has already taken.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void animatedButtonWidth() {
        ValueAnimator animator = ValueAnimator.ofInt(mBinding.btnSignin.getMeasuredWidth(), getFinalWidth());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.btnSignin.getLayoutParams();
                layoutParams.width = value;
                mBinding.btnSignin.requestLayout();
            }
        });

        animator.setDuration(250);
        animator.start();
    }

    private void fadeOutTextSetProgress() {
        mBinding.tvSign.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showProgressDialog();
            }
        }).start();
    }

    private void showProgressDialog() {
        mBinding.progressbar.setVisibility(View.VISIBLE);
        mBinding.progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
    }

    private void nextAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealButton();
                fadeOutProgressDialog();
                delayStartNextActivity();
            }
        }, 1500);
    }

    private void revealButton() {
        mBinding.btnSignin.setElevation(0f);
        mBinding.view.setVisibility(View.VISIBLE);

        int x = mBinding.view.getWidth();
        int y = mBinding.view.getHeight();

        int startX = (int) (getFinalWidth() / 2 + mBinding.btnSignin.getX());
        int startY = (int) (getFinalWidth() / 2 + mBinding.btnSignin.getY());

        float radius = Math.max(x, y) * 1.2f;
        Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.view, startX, startY, getFinalWidth(), radius);
        reveal.setDuration(350);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                finish();
            }
        });
        reveal.start();
    }

    private void fadeOutProgressDialog() {
        mBinding.progressbar.animate().alpha(0f).setDuration(200).start();
    }

    private void delayStartNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }, 100);
    }

    private int getFinalWidth() {
        return (int) getResources().getDimension(R.dimen.get_width);
    }
}