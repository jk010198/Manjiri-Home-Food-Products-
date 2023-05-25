package com.ithub.groceryshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ithub.groceryshop.Fragments.ChataniThechaLonche;
import com.ithub.groceryshop.Fragments.Dryfruits;
import com.ithub.groceryshop.Fragments.FarsanSnack;
import com.ithub.groceryshop.Fragments.GheeOil;
import com.ithub.groceryshop.Fragments.Kirana;
import com.ithub.groceryshop.Fragments.Grains_dal;
import com.ithub.groceryshop.Fragments.KokanMeva;
import com.ithub.groceryshop.Fragments.MasalaAkkha;
import com.ithub.groceryshop.Fragments.Masala_Mirchi_powder;
import com.ithub.groceryshop.Fragments.Mukhvas;
import com.ithub.groceryshop.Fragments.Papad;
import com.ithub.groceryshop.Fragments.PastaShevyaFrying;
import com.ithub.groceryshop.Fragments.Home_made_atta;
import com.ithub.groceryshop.Fragments.Rice;
import com.ithub.groceryshop.Fragments.Sarbat;
import com.ithub.groceryshop.Fragments.SocialAdvertisment;
import com.ithub.groceryshop.Models.FeedbackModel;
import com.ithub.groceryshop.Models.RegistrationModel;
import com.ithub.groceryshop.Utils.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserHomePageActivity extends AppCompatActivity {

    ////////product tabs layout component ////////////
    private TabLayout mtabLayout;
    private ViewPager mviewPager;

    public static TextView totalBudgetCount;
    DatabaseHandler db;
    //String username, dbname, dbwhatsappno;
    TextView textView_marquetag;
    DatabaseReference dbref_marqueetext, dbref_aboutus, dbref_notice, dbref_terms_condition, dbref_offers, /*dbref,*/
            dbref_feedback, dbref_notify;
    ProgressDialog progressDialog;
    String current_date_time;
    SimpleDateFormat dateFormat;
    Date currentTime;
    /////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        mtabLayout = findViewById(R.id.tablayout);
        mviewPager = findViewById(R.id.viewpager);
        textView_marquetag = findViewById(R.id.textView_marque_tag);
        progressDialog = new ProgressDialog(this);

        //dbref = FirebaseDatabase.getInstance().getReference("Customers");
        dbref_marqueetext = FirebaseDatabase.getInstance().getReference("MarqueeText");
        dbref_aboutus = FirebaseDatabase.getInstance().getReference("Aboutus");
        dbref_notice = FirebaseDatabase.getInstance().getReference("Notice");
        dbref_terms_condition = FirebaseDatabase.getInstance().getReference("Termscondition");
        dbref_offers = FirebaseDatabase.getInstance().getReference("Offers");
        dbref_feedback = FirebaseDatabase.getInstance().getReference("Feedback");
        dbref_notify = FirebaseDatabase.getInstance().getReference("Notification");

        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.app_logo);
        progressDialog.show();

        ///// marquee text set-up //////////
        dbref_marqueetext.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView_marquetag.setText(dataSnapshot.child("text").getValue(String.class));
                textView_marquetag.setSelected(true);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserHomePageActivity.this, "Server error.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        //////////////////////////

        setUpViewPager(mviewPager);
        mtabLayout.setupWithViewPager(mviewPager);
        db = new DatabaseHandler(this);

        Intent intent = getIntent();
        boolean isRefresh = intent.getBooleanExtra("refreshed", true);
        if (isRefresh) {
            showOffers();
        }

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 10000);

        /////////////////// Check permissions /////////////////////////////////
        if ((ActivityCompat.checkSelfPermission(UserHomePageActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") !=
                PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(UserHomePageActivity.this, "android.permission.CALL_PHONE") !=
                PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(UserHomePageActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CALL_PHONE"}, 1212);
        } else {

        }
        ///////////////

        if (!(restorePrefData())) {
            showTermServicesDialog();
        }
        ///////////////
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isTermconditionOpenedBefore = pref.getBoolean("isTermOpnend", false);
        return isTermconditionOpenedBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isTermOpnend", true);
        editor.commit();
    }

    // add fragment's here //////
    private void setUpViewPager(ViewPager viewPager) {
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new Dryfruits(), "DryFruits");
        adapter.addFragment(new Mukhvas(), "Mukhvas");
        adapter.addFragment(new Home_made_atta(), "Home Made Atta");
        adapter.addFragment(new Masala_Mirchi_powder(), "Masala and Mirchi powder");
        adapter.addFragment(new MasalaAkkha(), "Masala Akkha");
        adapter.addFragment(new FarsanSnack(), "Farsan / Namkin");
        adapter.addFragment(new ChataniThechaLonche(), "Chatani / Thecha / Lonche");
        adapter.addFragment(new Kirana(), "Kirana");
        adapter.addFragment(new Papad(), "Papad");
        adapter.addFragment(new PastaShevyaFrying(), "Pasta / Shevya / Frying");
        adapter.addFragment(new GheeOil(), "Ghee / Oil");
        adapter.addFragment(new Grains_dal(), "Grains / Dal");
        adapter.addFragment(new KokanMeva(), "Kokan Meva");
        adapter.addFragment(new Sarbat(), "Sarbat");
        adapter.addFragment(new Rice(), "Rice");
        adapter.addFragment(new SocialAdvertisment(), "Advertisement");

        viewPager.setAdapter(adapter);
    }
    ///////////////////////////

    class viewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mfragmentList = new ArrayList<>();
        private final List<String> mframentTitleList = new ArrayList<>();

        public viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mfragmentList.get(i);
        }

        @Override
        public int getCount() {
            return mfragmentList.size();
        }

        public void addFragment(Fragment fragment, String string) {
            mfragmentList.add(fragment);
            mframentTitleList.add(string);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mframentTitleList.get(position);
        }
    }
    /////////////

    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.cart_icon, menu);

        /*if (!(sp.contains("username") && sp.contains("password"))) {
            MenuItem mitem = menu.findItem(R.id.logout);
            mitem.setVisible(false);
        }*/

        final MenuItem findItem = menu.findItem(R.id.action_cart);
        findItem.setVisible(true);
        View actionView = findItem.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                menu.performIdentifierAction(findItem.getItemId(), 0);
            }
        });
        totalBudgetCount = actionView.findViewById(R.id.actionbar_notifcation_textview);
        TextView textView = totalBudgetCount;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(db.getCartCount());
        totalBudgetCount.setText(sb.toString());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.action_cart:
                if (db.getCartCount() > 0) {
                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "No item in cart", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.refresh_menu:
                setUpViewPager(mviewPager);
                mtabLayout.setupWithViewPager(mviewPager);
                break;

            case R.id.offer:
                showOffers();
                break;

            case R.id.notices:
                showNotices();
                break;

            case R.id.vision_mission:
                showVisionMission();
                break;

            case R.id.features:
                showFeatures();
                break;

            case R.id.terms_conditon:
                showTermServicesDialog();
                break;

            case R.id.aboutus:
                showAboutus();
                break;

            case R.id.shareapp:
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", "Manjiri Home Food Products");
                StringBuilder sb = new StringBuilder();
                sb.append("Let me recommend you this application as, I also use this Application to buy fresh home products from Manjiri Home Food Products.\n\n ");
                sb.append("https://play.google.com/store/apps/details?id=");
                sb.append(BuildConfig.APPLICATION_ID);
                intent.putExtra("android.intent.extra.TEXT", sb.toString());
                startActivity(Intent.createChooser(intent, "choose one"));
                break;

            case R.id.feedback:
                currentTime();
                feedback();
                break;
        }
        return true;
    }

    public void currentTime() {
        currentTime = Calendar.getInstance().getTime();
        dateFormat = new SimpleDateFormat("hh:mm:ss aa");
        current_date_time = "(" + dateFormat.format(currentTime) + ")";
    }

    public void setCartCounter(String str) {
        totalBudgetCount.setText(str);
    }

    public void showOffers() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_offer);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        progressDialog.show();

        dbref_offers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView iv = dialog.findViewById(R.id.imageview_dialog_offers);
                ((TextView) dialog.findViewById(R.id.textView_offers)).setText(dataSnapshot.child("offers").getValue(String.class));
                String imageurl = dataSnapshot.child("offersimg").getValue(String.class);
                if ((imageurl.equals(""))) {
                    Glide.with(UserHomePageActivity.this)
                            .load(R.drawable.offer_img)
                            .into(iv);
                } else {
                    Glide.with(UserHomePageActivity.this)
                            .load(imageurl)
                            .into(iv);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(UserHomePageActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showNotices() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_notices);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        progressDialog.show();

        dbref_notice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) dialog.findViewById(R.id.textView_notices)).setText(dataSnapshot.child("notice").getValue(String.class));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(UserHomePageActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void feedback() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        final EditText editText_feedback, editText_feedback_name, editText_feedback_mobileno;
        Button btn_submit;

        editText_feedback_name = dialog.findViewById(R.id.et_feedback_name);
        editText_feedback_mobileno = dialog.findViewById(R.id.et_feedback_mobileno);
        editText_feedback = dialog.findViewById(R.id.et_feedback);
        btn_submit = dialog.findViewById(R.id.btn_feedback_submit);

        ConnectivityManager mgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnected()) {
                // Internet Available
                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        String name = editText_feedback_name.getText().toString();
                        String mobile = editText_feedback_mobileno.getText().toString();
                        String msg = editText_feedback.getText().toString();
                        if (!name.isEmpty() || !mobile.isEmpty() || !(mobile.length() < 10) || !msg.isEmpty()) {
                            String id = dbref_feedback.push().getKey();
                            FeedbackModel feedbackModel = new FeedbackModel(id, name, mobile, msg);

                            dbref_feedback.child(id).setValue(feedbackModel);
                            dbref_notify.child("key").setValue("1234");
                            dbref_notify.child("msg").setValue("Feedback Recived from " + name + "," + current_date_time);
                            Toast.makeText(UserHomePageActivity.this, "Feedback sent successfully.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(UserHomePageActivity.this, "Please fill properly.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No Internet connection.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        } else {
            Toast.makeText(this, "No Internet connection.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showVisionMission() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_vision_mission);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        ((TextView) dialog.findViewById(R.id.textView_vision_head)).setText("Vision");
        ((TextView) dialog.findViewById(R.id.textView_vision_data)).setText(R.string.vision);

        ((TextView) dialog.findViewById(R.id.textView_mission_head)).setText("Mission");
        ((TextView) dialog.findViewById(R.id.textView_mission_data)).setText(R.string.mission);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showFeatures() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_features);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        ((TextView) dialog.findViewById(R.id.textView_features_head)).setText("Features");
        ((TextView) dialog.findViewById(R.id.textView_features_data)).setText(R.string.features);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_and_condition);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        progressDialog.show();

        dbref_terms_condition.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) dialog.findViewById(R.id.textView_lastupdate)).setText("last update " + dataSnapshot.child("lastupdatedate").getValue(String.class));
                ((TextView) dialog.findViewById(R.id.textView_terms_conditions)).setText(dataSnapshot.child("termscondition").getValue(String.class));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(UserHomePageActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                savePrefsData();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showAboutus() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_aboutus);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        progressDialog.show();

        dbref_aboutus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) dialog.findViewById(R.id.textView_aboutus)).setText(dataSnapshot.child("aboutus").getValue(String.class));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(UserHomePageActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    //////// checking permission ////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1212) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You have to grant the permissions.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(UserHomePageActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CALL_PHONE"}, 1212);
            }
        }
    }
    /////////////////////////////

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.dialog_title);
        adb.setIcon(R.drawable.app_logo);
        adb.setMessage("Do you want to exit ?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                intent.putExtra("exit_code", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }
}