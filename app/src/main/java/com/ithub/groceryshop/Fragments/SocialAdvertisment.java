package com.ithub.groceryshop.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ithub.groceryshop.Adaptors.SocialAdsAdapter;
import com.ithub.groceryshop.Models.SocialAdsModel;
import com.ithub.groceryshop.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialAdvertisment extends Fragment {

    ListView listview_social_advertisement;
    TextView textView_noData_social_adverticement;
    List<SocialAdsModel> list_social_adverticement;
    SocialAdsAdapter socialAdsAdapter;
    DatabaseReference dbref_advertisement;
    ProgressDialog progressDialog;
    TextView textView_ads_companyname, textView_ads_companycontactno, textView_ads_alternetcontactnumber,
            textView_ads_companycoredetails, textView_ads_companyaddress;
    ImageView imageView_adv_img;

    public SocialAdvertisment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_advertisment, container, false);

        listview_social_advertisement = view.findViewById(R.id.listview_social_ads);
        textView_noData_social_adverticement = view.findViewById(R.id.tv_nodata_social_ads);
        progressDialog = new ProgressDialog(getActivity());

        dbref_advertisement = FirebaseDatabase.getInstance().getReference("Advertisement");
        list_social_adverticement = new ArrayList<>();

        internetChecker internetChecker = new internetChecker();
        internetChecker.execute();
        return view;
    }

    public void mainMethod() {

        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIcon(R.drawable.app_logo);
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbref_advertisement.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_social_adverticement.clear();
                for (DataSnapshot getorder : dataSnapshot.getChildren()) {
                    SocialAdsModel sam = getorder.getValue(SocialAdsModel.class);
                    list_social_adverticement.add(sam);
                    progressDialog.dismiss();
                }

                //////////////////////////////// when list is empty no-data will appear /////////////////////
                if (!(list_social_adverticement.size() > 0)) {
                    textView_noData_social_adverticement.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                } else {
                    textView_noData_social_adverticement.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();
                }
                //////////////////////////////////////////////////////

                socialAdsAdapter = new SocialAdsAdapter(getActivity(), list_social_adverticement);
                Collections.sort(list_social_adverticement);
                listview_social_advertisement.setAdapter(socialAdsAdapter);

                listview_social_advertisement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SocialAdsModel sam = (SocialAdsModel) parent.getItemAtPosition(position);
                        showAdvertisement(sam.getImage(), sam.getBusiness_name(), sam.getBusiness_contact(), sam.getAlternet_contact(),
                                sam.getBusiness_basic_info(), sam.getBusiness_address(), getContext());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void showAdvertisement(String image, String name, String contactno, String alternet_contactnumber, String core_details,
                                  String address, final Context context) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_social_ads);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        imageView_adv_img = dialog.findViewById(R.id.image_social_ads);

        if (image.equals("")) {
            Glide
                    .with(getActivity())
                    .load(R.drawable.advertisement_img)
                    .into(imageView_adv_img);
        } else {
            Glide
                    .with(getActivity())
                    .load(image)
                    .into(imageView_adv_img);
        }

        textView_ads_companyname = dialog.findViewById(R.id.textView_company_name);
        textView_ads_companycontactno = dialog.findViewById(R.id.textView_company_contactdetails);
        textView_ads_alternetcontactnumber = dialog.findViewById(R.id.textView_company_alternetcontactdetails);
        textView_ads_companycoredetails = dialog.findViewById(R.id.textView_company_coredetails);
        textView_ads_companyaddress = dialog.findViewById(R.id.textView_company_address);

        textView_ads_companyname.setText(name);
        textView_ads_companycontactno.setText(contactno);
        textView_ads_companycontactno.setText(contactno);
        textView_ads_alternetcontactnumber.setText(alternet_contactnumber);
        textView_ads_companycoredetails.setText(core_details);
        textView_ads_companyaddress.setText(address);

        textView_ads_companycontactno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", textView_ads_companycontactno.getText());
                manager.setPrimaryClip(clipData);
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close_social_ads)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        textView_ads_companycontactno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + textView_ads_companycontactno.getText().toString())));
            }
        });

        textView_ads_alternetcontactnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + textView_ads_alternetcontactnumber.getText().toString())));
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Service.CONNECTIVITY_SERVICE);

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

    class internetChecker extends AsyncTask<String, Void, Integer> {

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
                    mainMethod();
                }

                if (result == 0) {
                    Toast.makeText(getActivity(), " No internet available ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), " No internet connection available ", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
}