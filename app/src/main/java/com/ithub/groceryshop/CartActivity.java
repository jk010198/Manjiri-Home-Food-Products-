package com.ithub.groceryshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ithub.groceryshop.Adaptors.Cart_adapter;
import com.ithub.groceryshop.Utils.DatabaseHandler;

public class CartActivity extends AppCompatActivity {

    public Button btn_checkout;
    public DatabaseHandler db;
    public RecyclerView rv_cart;
    public TextView tv_item;
    public static TextView tv_total;

    private BroadcastReceiver mCart = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("param").contentEquals("update")) {
                updateData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        btn_checkout = findViewById(R.id.btn_cart_checkout);
        rv_cart = findViewById(R.id.rv_cart);
        tv_total = findViewById(R.id.tv_cart_total);
        tv_item = findViewById(R.id.tv_cart_item);
        rv_cart = findViewById(R.id.rv_cart);
        rv_cart.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHandler(this);

        Cart_adapter cart_adapter = new Cart_adapter(this, db.getCartAll());
        rv_cart.setAdapter(cart_adapter);
        cart_adapter.notifyDataSetChanged();
        updateData();

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeliveryDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void updateData() {
        tv_total.setText(db.getTotalDiscountAmount());
        tv_item.setText(String.valueOf(db.getCartCount()));

        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(db.getCartCount());
        UserHomePageActivity.totalBudgetCount.setText(sb.toString());
        if (String.valueOf(db.getCartCount()).equals("0")) {
            onBackPressed();
        }
    }

    private void showClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title);
        builder.setMessage((CharSequence) "Are you sure!\nyou want to delete all cart products ?");
        builder.setIcon(R.drawable.app_logo);
        builder.setNegativeButton((CharSequence) "Cancel", null);
        builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                db.clearCart();
                Cart_adapter cart_adapter = new Cart_adapter(CartActivity.this, CartActivity.this.db.getCartAll());
                rv_cart.setAdapter(cart_adapter);
                cart_adapter.notifyDataSetChanged();
                updateData();
            }
        });
        builder.show();
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(this.mCart);
    }

    public void onResume() {
        super.onResume();
        registerReceiver(this.mCart, new IntentFilter("Grocery_cart"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), UserHomePageActivity.class);
        intent.putExtra("refreshed", false);
        startActivity(intent);
    }

    public void allCartDelete(View view) {
        showClearDialog();
    }
}