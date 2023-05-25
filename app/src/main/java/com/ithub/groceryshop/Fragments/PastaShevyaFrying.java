package com.ithub.groceryshop.Fragments;


import android.app.ProgressDialog;
import android.app.Service;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ithub.groceryshop.Adaptors.ProductAdapter;
import com.ithub.groceryshop.Models.Product_model;
import com.ithub.groceryshop.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastaShevyaFrying extends Fragment {

    RecyclerView rv_pasta_shevya_frying;
    TextView textView_noData_pasta_shevya_frying;
    List<Product_model> productlist_pasta_shevya_frying;
    ProductAdapter productAdapter;
    DatabaseReference dbref_products;
    ProgressDialog progressDialog;

    public PastaShevyaFrying() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pasta_shevya_frying, container, false);
        rv_pasta_shevya_frying = view.findViewById(R.id.rv_pasta_shevya_frying);
        textView_noData_pasta_shevya_frying = view.findViewById(R.id.tv_nodata_pasta_shevya_frying);
        progressDialog = new ProgressDialog(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_pasta_shevya_frying.setLayoutManager(linearLayoutManager);

        dbref_products = FirebaseDatabase.getInstance().getReference("Products").child("Pasta_shevya_frying");
        productlist_pasta_shevya_frying = new ArrayList<>();

        internetChecker internetChecker = new internetChecker();
        internetChecker.execute();
        return  view;
    }

    public void mainMethod() {
        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbref_products.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productlist_pasta_shevya_frying.clear();

                for (DataSnapshot getorder : dataSnapshot.getChildren()) {
                    Product_model pm = getorder.getValue(Product_model.class);
                    productlist_pasta_shevya_frying.add(pm);
                    progressDialog.dismiss();
                }

                // when list is empty text will appear
                if (!(productlist_pasta_shevya_frying.size() > 0)) {
                    textView_noData_pasta_shevya_frying.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                } else {
                    textView_noData_pasta_shevya_frying.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();
                }

                productAdapter = new ProductAdapter(productlist_pasta_shevya_frying, getActivity());
                Collections.sort(productlist_pasta_shevya_frying);
                rv_pasta_shevya_frying.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

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