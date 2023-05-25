package com.ithub.groceryshop.Adaptors;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ithub.groceryshop.CartActivity;
import com.ithub.groceryshop.Models.PricesModel;
import com.ithub.groceryshop.R;
import com.ithub.groceryshop.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ProductHolder> {
    Activity activity;
    DatabaseHandler dbHandler;
    int lastpostion;
    ArrayList<HashMap<String, String>> list;

    class ProductHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView iv_logo;
        ImageView iv_minus;
        ImageView iv_plus;
        TextView tv_contetiy;
        TextView tv_discount_price;
        TextView tv_mrp;
        TextView tv_remove;
        TextView tv_title;
        TextView tv_total;
        TextView tv_unit;

        ProductHolder(View view) {
            super(view);
            this.tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            this.tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            this.tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            this.iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            this.iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            this.iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            this.tv_unit = (TextView) view.findViewById(R.id.tv_subcat_unit);
            this.tv_discount_price = (TextView) view.findViewById(R.id.tv_subcat_discount_price);
            this.tv_mrp = (TextView) view.findViewById(R.id.tv_product_mrp);
            this.tv_remove = (TextView) view.findViewById(R.id.tv_cart_remove);
            this.iv_minus.setOnClickListener(this);
            this.iv_plus.setOnClickListener(this);
            this.tv_remove.setOnClickListener(this);
        }

        public void onClick(View view) {
            int id = view.getId();
            int adapterPosition = getAdapterPosition();
            HashMap hashMap = (HashMap) Cart_adapter.this.list.get(adapterPosition);
            String str = DatabaseHandler.COLUMN_PRICES_SELECTED_ID;
            if (id == R.id.iv_subcat_minus) {
                int i = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase("")) {
                    i = Integer.valueOf(tv_contetiy.getText().toString()).intValue();
                }
                if (i > 0) {
                    tv_contetiy.setText(String.valueOf(i - 1));
                }
                if (tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    dbHandler.removeItemFromCartPrice((String) hashMap.get(str));
                    list.remove(adapterPosition);
                    notifyDataSetChanged();
                    updateintent();
                    return;
                }
                updateCount(hashMap);
            } else if (id == R.id.iv_subcat_plus) {
                tv_contetiy.setText(String.valueOf(Integer.valueOf(tv_contetiy.getText().toString()).intValue() + 1));
                updateCount(hashMap);
            } else if (id == R.id.tv_cart_remove) {
                dbHandler.removeItemFromCartPrice((String) hashMap.get(str));
                list.remove(adapterPosition);
                notifyDataSetChanged();
                updateintent();
            }
        }

        private void updateCount(HashMap<String, String> hashMap) {
            dbHandler.setCart(hashMap, Float.valueOf(this.tv_contetiy.getText().toString()));
            PricesModel pricesModel = (PricesModel) new GsonBuilder().create().fromJson((String) hashMap.get(DatabaseHandler.COLUMN_PRICES_SELECTED), new TypeToken<PricesModel>() {
            }.getType());
            Double valueOf = Double.valueOf(Double.parseDouble(this.tv_contetiy.getText().toString()));
            Double valueOf2 = Double.valueOf(Double.valueOf(pricesModel.getPrice()).doubleValue());
            String obj = Cart_adapter.this.activity.toString();
            StringBuilder sb = new StringBuilder();
            sb.append("ADD");
            sb.append(valueOf2);
            Log.e(obj, sb.toString());
            tv_total.setText(String.format(Locale.getDefault(), "%.2f", new Object[]{Double.valueOf(valueOf2.doubleValue() * valueOf.doubleValue())}));
            CartActivity.tv_total.setText("");
            updateintent();
        }
    }

    public Cart_adapter(Activity activity2, ArrayList<HashMap<String, String>> arrayList) {
        this.list = arrayList;
        this.activity = activity2;
        this.dbHandler = new DatabaseHandler(activity2);
    }

    public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProductHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_rv_cart, viewGroup, false));
    }

    public void onBindViewHolder(ProductHolder productHolder, int i) {
        HashMap hashMap = (HashMap) this.list.get(i);
        RequestManager with = Glide.with(this.activity);
        StringBuilder sb = new StringBuilder();
        //sb.append(BaseURL.IMG_PRODUCT_URL);
        sb.append((String) hashMap.get(DatabaseHandler.COLUMN_IMAGE));
        with.load(sb.toString()).centerCrop().placeholder((int) R.drawable.app_logo).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(productHolder.iv_logo);
        productHolder.tv_title.setText((CharSequence) hashMap.get(DatabaseHandler.COLUMN_NAME));
        PricesModel pricesModel = (PricesModel) new GsonBuilder().create().fromJson((String) hashMap.get(DatabaseHandler.COLUMN_PRICES_SELECTED), new TypeToken<PricesModel>() {
        }.getType());
        TextView textView = productHolder.tv_unit;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(pricesModel.getQty());
        sb2.append(" ");
        sb2.append(pricesModel.getUnit());
        textView.setText(sb2.toString());
        productHolder.tv_mrp.setVisibility(View.GONE);
        productHolder.tv_discount_price.setText(String.valueOf(Double.valueOf(pricesModel.getPrice()).doubleValue()));
        productHolder.tv_contetiy.setText((CharSequence) hashMap.get(DatabaseHandler.COLUMN_QTY));
        Double valueOf = Double.valueOf(Double.parseDouble(this.dbHandler.getInCartItemQtyPrice((String) hashMap.get(DatabaseHandler.COLUMN_PRICES_SELECTED_ID))));
        Double valueOf2 = Double.valueOf(Double.valueOf(pricesModel.getPrice()).doubleValue());
        productHolder.tv_total.setText(String.format(Locale.getDefault(), "%.2f", new Object[]{Double.valueOf(valueOf2.doubleValue() * valueOf.doubleValue())}));
        productHolder.setIsRecyclable(false);
    }

    public int getItemCount() {
        return this.list.size();
    }

    /* access modifiers changed from: private */
    public void updateintent() {
        Intent intent = new Intent("Grocery_cart");
        intent.putExtra("param", "update");
        this.activity.sendBroadcast(intent);
    }
}