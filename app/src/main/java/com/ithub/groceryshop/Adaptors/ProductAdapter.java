package com.ithub.groceryshop.Adaptors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ithub.groceryshop.Models.PricesModel;
import com.ithub.groceryshop.Models.Product_model;
import com.ithub.groceryshop.R;
import com.ithub.groceryshop.UserHomePageActivity;
import com.ithub.groceryshop.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ProductAdapter extends Adapter<ProductAdapter.MyViewHolder> implements Filterable {

    public Context context;
    public DatabaseHandler dbcart;
    public List<Product_model> mFilteredList;
    public List<Product_model> modelList;

    public class MyViewHolder extends ViewHolder implements OnClickListener {
        ImageView iv_logo;
        ImageView iv_minus;
        ImageView iv_plus;
        LinearLayout ll_adding;
        LinearLayout ll_prices;
        Spinner sp_prices;
        TextView tv_contetiy, tv_discount_price, tv_title, tv_product_discription;

        public MyViewHolder(View view) {
            super(view);
            this.tv_title = (TextView) view.findViewById(R.id.tv_product_title);
            this.tv_discount_price = (TextView) view.findViewById(R.id.tv_product_discount_price);
            this.tv_contetiy = (TextView) view.findViewById(R.id.tv_product_contetiy);
            this.iv_logo = (ImageView) view.findViewById(R.id.iv_product_img);
            this.iv_plus = (ImageView) view.findViewById(R.id.iv_product_plus);
            this.iv_minus = (ImageView) view.findViewById(R.id.iv_product_minus);
            this.ll_prices = (LinearLayout) view.findViewById(R.id.ll_product_prices);
            this.ll_adding = (LinearLayout) view.findViewById(R.id.ll_product_adding);
            this.sp_prices = (Spinner) view.findViewById(R.id.sp_product_price);
            this.tv_product_discription = view.findViewById(R.id.tv_product_discription);
            this.iv_minus.setOnClickListener(this);
            this.iv_plus.setOnClickListener(this);
            this.sp_prices.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    TextView textView = (TextView) ((LinearLayout) adapterView.getChildAt(0)).getChildAt(0);
                    textView.setPadding(0, 0, 0, 0);
                    textView.setTextSize(2, 12.0f);
                    PricesModel pricesModel = (PricesModel) ((Product_model) ProductAdapter.this.modelList.get(MyViewHolder.this.getAdapterPosition())).getPricesModelArrayList().get(MyViewHolder.this.sp_prices.getSelectedItemPosition());
                    String str = "0";
                    StringBuilder sb = new StringBuilder();
                    sb.append(ProductAdapter.this.context.getResources().getString(R.string.currency));
                    sb.append(pricesModel.getPrice());
                    TextView textView3 = MyViewHolder.this.tv_discount_price;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(ProductAdapter.this.context.getResources().getString(R.string.currency));
                    sb2.append(String.valueOf(Double.valueOf(pricesModel.getPrice()).doubleValue()));
                    textView3.setText(sb2.toString());
                    if (ProductAdapter.this.dbcart.isInCartPrice(pricesModel.getPrice_id())) {
                        MyViewHolder.this.tv_contetiy.setText(ProductAdapter.this.dbcart.getInCartItemQtyPrice(pricesModel.getPrice_id()));
                    } else {
                        MyViewHolder.this.tv_contetiy.setText(str);
                    }
                    Double.valueOf(Double.parseDouble(ProductAdapter.this.dbcart.getInCartItemQtyPrice(pricesModel.getPrice_id())));
                }
            });
        }

        public void onClick(View view) {
            int id = view.getId();
            int adapterPosition = getAdapterPosition();
            if (id == R.id.iv_product_plus) {
                this.tv_contetiy.setText(String.valueOf(Integer.valueOf(this.tv_contetiy.getText().toString()).intValue() + 1));
                addremoveProduct(adapterPosition);
            } else if (id == R.id.iv_product_minus) {
                int i = 0;
                if (!this.tv_contetiy.getText().toString().equalsIgnoreCase("")) {
                    i = Integer.valueOf(this.tv_contetiy.getText().toString()).intValue();
                }
                if (i > 0) {
                    this.tv_contetiy.setText(String.valueOf(i - 1));
                }
                addremoveProduct(adapterPosition);
            }
        }

        private void addremoveProduct(int i) {
            if (((Product_model) ProductAdapter.this.modelList.get(i)).getPricesModelArrayList().size() > 0) {
                HashMap hashMap = new HashMap();
                String product_id = ((Product_model) ProductAdapter.this.modelList.get(i)).getProduct_id();
                String str = DatabaseHandler.COLUMN_ID;
                hashMap.put(str, product_id);
                hashMap.put(DatabaseHandler.COLUMN_CAT_ID, ((Product_model) ProductAdapter.this.modelList.get(i)).getCategory_id());
                hashMap.put(DatabaseHandler.COLUMN_IMAGE, ((Product_model) ProductAdapter.this.modelList.get(i)).getProduct_image());
                hashMap.put(DatabaseHandler.COLUMN_INCREAMENT, ((Product_model) ProductAdapter.this.modelList.get(i)).getIncreament());
                hashMap.put(DatabaseHandler.COLUMN_NAME, ((Product_model) ProductAdapter.this.modelList.get(i)).getProduct_name());
                hashMap.put("price", ((Product_model) ProductAdapter.this.modelList.get(i)).getPrice());
                hashMap.put(DatabaseHandler.COLUMN_STOCK, ((Product_model) ProductAdapter.this.modelList.get(i)).getIn_stock());
                hashMap.put(DatabaseHandler.COLUMN_TITLE, ((Product_model) ProductAdapter.this.modelList.get(i)).getTitle());
                hashMap.put(DatabaseHandler.COLUMN_UNIT, ((Product_model) ProductAdapter.this.modelList.get(i)).getUnit());
                hashMap.put(DatabaseHandler.COLUMN_UNIT_VALUE, ((Product_model) ProductAdapter.this.modelList.get(i)).getUnit_value());
                hashMap.put(DatabaseHandler.COLUMN_IN_STOCK, ((Product_model) ProductAdapter.this.modelList.get(i)).getIn_stock());
                hashMap.put(DatabaseHandler.COLUMN_DISCOUNT, ((Product_model) ProductAdapter.this.modelList.get(i)).getDiscount());
                PricesModel pricesModel = (PricesModel) ((Product_model) ProductAdapter.this.modelList.get(getAdapterPosition())).getPricesModelArrayList().get(this.sp_prices.getSelectedItemPosition());
                String price_id = ((PricesModel) ((Product_model) ProductAdapter.this.modelList.get(getAdapterPosition())).getPricesModelArrayList().get(this.sp_prices.getSelectedItemPosition())).getPrice_id();
                Gson create = new GsonBuilder().create();
                hashMap.put(DatabaseHandler.COLUMN_PRICES, create.toJsonTree(((Product_model) ProductAdapter.this.modelList.get(i)).getPricesModelArrayList()).getAsJsonArray().toString());
                hashMap.put(DatabaseHandler.COLUMN_PRICES_SELECTED, create.toJsonTree(pricesModel).getAsJsonObject().toString());
                String str2 = DatabaseHandler.COLUMN_PRICES_SELECTED_ID;
                hashMap.put(str2, price_id);
                String str3 = "0";
                if (this.tv_contetiy.getText().toString().equalsIgnoreCase(str3)) {
                    ProductAdapter.this.dbcart.removeItemFromCartPrice(pricesModel.getPrice_id());
                    this.tv_contetiy.setText(str3);
                } else if (ProductAdapter.this.dbcart.isInCart((String) hashMap.get(str))) {
                    ProductAdapter.this.dbcart.setCart(hashMap, Float.valueOf(this.tv_contetiy.getText().toString()));
                } else {
                    ProductAdapter.this.dbcart.setCart(hashMap, Float.valueOf(this.tv_contetiy.getText().toString()));
                }
                Double.valueOf(Double.parseDouble(ProductAdapter.this.dbcart.getInCartItemQtyPrice((String) hashMap.get(str2))));
                UserHomePageActivity userhomeactivity = (UserHomePageActivity) ProductAdapter.this.context;
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(ProductAdapter.this.dbcart.getCartCount());
                userhomeactivity.setCartCounter(sb.toString());
                return;
            }
            Double valueOf = Double.valueOf(((Product_model) ProductAdapter.this.modelList.get(i)).getStock());
            if (valueOf.doubleValue() < ((double) Integer.valueOf(this.tv_contetiy.getText().toString()).intValue())) {
                Context access$100 = ProductAdapter.this.context;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Product stock limit is: ");
                sb2.append(valueOf);
                Toast.makeText(access$100, sb2.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public ProductAdapter(List<Product_model> list, Context context2) {
        this.modelList = list;
        this.mFilteredList = list;
        this.dbcart = new DatabaseHandler(context2);
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_list_product, viewGroup, false);
        this.context = viewGroup.getContext();
        return new MyViewHolder(inflate);
    }

    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        final Product_model product_model = (Product_model) this.modelList.get(i);
        RequestManager with = Glide.with(this.context);
        StringBuilder sb = new StringBuilder();
        sb.append(product_model.getProduct_image());
        // img place holder
        with.load(sb.toString()).centerCrop().placeholder((int) R.drawable.app_logo).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(myViewHolder.iv_logo);
        myViewHolder.tv_title.setText(product_model.getProduct_name());
        myViewHolder.iv_logo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog maindialog = new Dialog(context);
                maindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                maindialog.setContentView(R.layout.big_image);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(maindialog.getWindow().getAttributes());
                lp.width = 630;
                lp.height = 630;

                ImageView imageView = maindialog.findViewById(R.id.dialog_imageview);

                RequestManager with = Glide.with(context);
                StringBuilder sb = new StringBuilder();
                sb.append(product_model.getProduct_image());
                // img place holder
                with.load(sb.toString()).centerCrop().placeholder((int) R.drawable.app_logo).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(imageView);
                maindialog.show();
                maindialog.getWindow().setAttributes(lp);
            }
        });

        myViewHolder.tv_product_discription.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                discriptionPopup(product_model.getProduct_name(), product_model.getProduct_description());
            }
        });
        String str = " ";
        if (product_model.getPricesModelArrayList().size() > 0) {
            myViewHolder.ll_prices.setVisibility(View.VISIBLE);//0
            myViewHolder.ll_adding.setVisibility(View.VISIBLE);//0
            ArrayList arrayList = new ArrayList();
            Iterator it = product_model.getPricesModelArrayList().iterator();
            while (it.hasNext()) {
                PricesModel pricesModel = (PricesModel) it.next();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(pricesModel.getQty());
                sb2.append(str);
                sb2.append(pricesModel.getUnit());
                arrayList.add(sb2.toString());
            }
            myViewHolder.sp_prices.setAdapter(new ArrayAdapter(this.context, R.layout.row_list_spinner, R.id.tv_sp, arrayList));
            PricesModel pricesModel2 = (PricesModel) product_model.getPricesModelArrayList().get(0);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(this.context.getResources().getString(R.string.tv_pro_price));
            sb3.append(product_model.getUnit_value());
            sb3.append(str);
            sb3.append(product_model.getUnit());
            sb3.append(str);
            sb3.append(this.context.getResources().getString(R.string.currency));
            sb3.append(str);
            sb3.append(product_model.getPrice());
            if (this.dbcart.isInCart(product_model.getProduct_id())) {
                myViewHolder.tv_contetiy.setText(this.dbcart.getCartItemQty(product_model.getProduct_id()));
            } else {
                myViewHolder.tv_contetiy.setText("0");
            }
            Double.valueOf(Double.parseDouble(this.dbcart.getInCartItemQtyPrice(pricesModel2.getPrice_id())));
            return;
        }
        myViewHolder.ll_adding.setVisibility(View.GONE);//8
        myViewHolder.ll_prices.setVisibility(View.GONE);//8
        TextView textView2 = myViewHolder.tv_discount_price;
        StringBuilder sb4 = new StringBuilder();
        sb4.append(this.context.getResources().getString(R.string.currency));
        sb4.append(str);
        sb4.append(product_model.getPrice());
        textView2.setText(sb4.toString());
        if (this.dbcart.isInCart(product_model.getProduct_id())) {
            myViewHolder.tv_contetiy.setText(this.dbcart.getCartItemQty(product_model.getProduct_id()));
        }
        Double.valueOf(Double.parseDouble(this.dbcart.getInCartItemQty(product_model.getProduct_id())));
        Double.valueOf(Double.parseDouble(product_model.getPrice()));
    }

    public int getItemCount() {
        return this.mFilteredList.size();
    }

    public void discriptionPopup(String pro_name, String discription) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Manjiri Virar-Online Grocery.");
        adb.setIcon(R.drawable.app_logo);
        adb.setMessage("Product :- " + pro_name + "\n" + "Discription :- " + discription);
        adb.setNegativeButton("OK", null);
        adb.show();
    }

    public Filter getFilter() {
        return new Filter() {
            /* access modifiers changed from: protected */
            public FilterResults performFiltering(CharSequence charSequence) {
                String charSequence2 = charSequence.toString();
                if (charSequence2.isEmpty()) {
                    ProductAdapter hotLatestProductAdapter = ProductAdapter.this;
                    hotLatestProductAdapter.mFilteredList = hotLatestProductAdapter.modelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (Product_model product_model : ProductAdapter.this.modelList) {
                        if (product_model.getProduct_name().toLowerCase().contains(charSequence2)) {
                            arrayList.add(product_model);
                        }
                    }
                    ProductAdapter.this.mFilteredList = arrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = ProductAdapter.this.mFilteredList;
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ProductAdapter.this.mFilteredList = (ArrayList) filterResults.values;
                ProductAdapter.this.notifyDataSetChanged();
            }
        };
    }
}