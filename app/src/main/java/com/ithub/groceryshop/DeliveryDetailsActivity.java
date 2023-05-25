package com.ithub.groceryshop;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ithub.groceryshop.Models.OrdersModel;
import com.ithub.groceryshop.Models.PricesModel;
import com.ithub.groceryshop.Models.RegistrationModel;
import com.ithub.groceryshop.Utils.DatabaseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryDetailsActivity extends AppCompatActivity {

    Button btn_ok;
    LinearLayout black_linear, popup_linear;
    ImageView imageView;
    Animation fromsmall, fromnothing, for_icon, togo;

    EditText editText_name, editText_whatsappno, editText_alt_mobileno, editText_address, editText_landmark;
    String firebaseId, name, whatsapp_number, alternet_mobilenumber, address, landmark, /*intent_username, sp_username,*/
            cart = "",
            customer_id, order_weight;
    DatabaseReference dbref_area, dbref_notify, dbref_agent;
    ProgressDialog progressDialog;
    List<String> areaList = new ArrayList<String>();
    List<String> agentList = new ArrayList<String>();
    static DatabaseHandler db;
    TextView tv_item_count, tv_cart_total;
    DatabaseReference /*dbref_customers,*/ dbref_orders;
    static String current_date_time;
    Calendar calendar;
    Spinner area_spinner, agent_spinner;
    boolean indialogNo = false;
    ArrayList<HashMap<String, String>> list;

    ////////////////////////////////// pdf component //////////////////////////////////////////
    static String pro_name, pro_price, pro_quantity, pro_qty_unit;
    static int kg = 0, gram = 0, liter = 0, total_gram_oneproduct = 0, total_kg_oneproduct = 0, total_liter_oneproduct = 0;
    static ArrayList<HashMap<String, String>> viewcart1;
    Calendar c;
    static String current_date;
    static Font catFont, redFont, subFont, smallBold;
    int counter = 999;
    static String cust_name, cust_address, cust_orderid, cust_mobile;
    /////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivey_details);

        imageView = findViewById(R.id.popup_imgview);

        editText_name = findViewById(R.id.et_deliverydetails_fullname);
        editText_whatsappno = findViewById(R.id.et_deliverydetails_whatsappmobilenumber);
        editText_alt_mobileno = findViewById(R.id.et_deliverydetails_mobilenumber);
        editText_address = findViewById(R.id.et_deliverydetails_address);
        editText_landmark = findViewById(R.id.et_deliverydetails_landmark);
        tv_cart_total = findViewById(R.id.tv_deli_totalamt);
        tv_item_count = findViewById(R.id.tv_deli_itemcount);
        area_spinner = findViewById(R.id.spinner_area);
        agent_spinner = findViewById(R.id.spinner_agent);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.dialog_title);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.app_logo);

        //dbref_customers = FirebaseDatabase.getInstance().getReference("Customers");
        dbref_orders = FirebaseDatabase.getInstance().getReference("Customers_orders");
        dbref_area = FirebaseDatabase.getInstance().getReference("Area");
        dbref_agent = FirebaseDatabase.getInstance().getReference("Agent");
        dbref_notify = FirebaseDatabase.getInstance().getReference("Notification");

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
                //////////// spinner //////////////
                Adapter adapter = new ArrayAdapter<String>(DeliveryDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, areaList);
                area_spinner.setAdapter((SpinnerAdapter) adapter);
                area_spinner.setBackgroundResource(R.drawable.round_edittext);

                area_spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ((TextView) area_spinner.getSelectedView()).setTextColor(Color.BLACK);
                        ((TextView) area_spinner.getSelectedView()).setTextSize(15);
                    }
                });
                //////////////////////////////
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(DeliveryDetailsActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

        dbref_agent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressDialog.show();
                agentList.clear();

                agentList.add("Select Agent");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    agentList.add(ds.child("agentname").getValue().toString());
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();

                /////////////////////////// spinner agent /////////////////////////////
                Adapter adapter_agent = new ArrayAdapter<String>(DeliveryDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, agentList);
                agent_spinner.setAdapter((SpinnerAdapter) adapter_agent);
                agent_spinner.setBackgroundResource(R.drawable.round_edittext);

                agent_spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ((TextView) agent_spinner.getSelectedView()).setTextColor(Color.BLACK);
                        ((TextView) agent_spinner.getSelectedView()).setTextSize(15);
                    }
                });
                ///////////////////////////////
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(DeliveryDetailsActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
            }
        });

        db = new DatabaseHandler(this);

        list = db.getCartAll();
        viewcart1 = db.getCartAll();

        ////// for pdf ////////
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        c = Calendar.getInstance();
        Calendar today = c;
        current_date = sdf.format(c.getTime());
        ////////////////

        ///// for orders //////////
        SimpleDateFormat sdf_order = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        calendar = Calendar.getInstance();
        Calendar day = calendar;
        current_date_time = sdf_order.format(calendar.getTime());
        //////////////////

        catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD);
        redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.NORMAL, BaseColor.RED);
        subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
                Font.BOLD);
        smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD);

        tv_item_count.setText(String.valueOf(db.getCartCount()));
        tv_cart_total.setText(String.valueOf(db.getTotalDiscountAmount()));

        for (int i = 0; i < db.getCartAll().size(); i++) {
            HashMap hashMap = (HashMap) this.list.get(i);
            PricesModel pricesModel = (PricesModel) new GsonBuilder().create().fromJson((String) hashMap.get(DatabaseHandler.COLUMN_PRICES_SELECTED),
                    new TypeToken<PricesModel>() {
                    }.getType());

            String product_qty_unit = pricesModel.getQty() + " " + pricesModel.getUnit();
            String p_qty = String.valueOf(hashMap.get(DatabaseHandler.COLUMN_QTY));

            int price = Integer.parseInt(pricesModel.getPrice());
            int product_qty = Integer.parseInt(p_qty);
            int total = price * product_qty;

            cart += "*" + hashMap.get(DatabaseHandler.COLUMN_NAME) + " @ " + product_qty_unit + " # " + price + " $ " + p_qty + "!"
                    + total + "%";
            pro_qty_unit = product_qty_unit.toLowerCase().trim();
            Log.d("543jk", "pro_unit" + product_qty_unit);
            if (pro_qty_unit.contains("kg")) {
                kg = Integer.parseInt(pro_qty_unit.substring(0, (pro_qty_unit.indexOf("k") - 1)));
                int kg_qty = Integer.parseInt(p_qty);
                total_kg_oneproduct += kg * kg_qty;
                Log.d("543jk", "kg" + total_kg_oneproduct);
            }

            if (pro_qty_unit.contains("gm")) {
                gram = Integer.parseInt(pro_qty_unit.substring(0, (pro_qty_unit.indexOf("g") - 1)));
                int gram_qty = Integer.parseInt(p_qty);
                total_gram_oneproduct += gram * gram_qty;
                Log.d("543jk", "gram" + total_gram_oneproduct);
            }

            if (pro_qty_unit.contains("ltr")) {
                liter = Integer.parseInt(pro_qty_unit.substring(0, (pro_qty_unit.indexOf("l") - 1)));
                int liter_qty = Integer.parseInt(p_qty);
                total_liter_oneproduct += liter * liter_qty;
                Log.d("543jk", "liter" + total_liter_oneproduct);
            }
        }

        //////// animation popup after placed order///////
        black_linear = findViewById(R.id.ll_black);
        popup_linear = findViewById(R.id.ll_popup);

        btn_ok = findViewById(R.id.btn_ok);
        fromsmall = AnimationUtils.loadAnimation(this, R.anim.fromsmall);
        fromnothing = AnimationUtils.loadAnimation(this, R.anim.fromnothing);
        for_icon = AnimationUtils.loadAnimation(this, R.anim.for_icon);
        togo = AnimationUtils.loadAnimation(this, R.anim.togo);

        btn_ok.setVisibility(View.GONE);

        black_linear.setAlpha(0);
        popup_linear.setAlpha(0);
        imageView.setVisibility(View.GONE);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                black_linear.setAnimation(togo);
                popup_linear.setAnimation(togo);
                imageView.setAnimation(togo);
                imageView.setVisibility(View.GONE);

                db.clearCart();
                startActivity(new Intent(getApplicationContext(), UserHomePageActivity.class));
            }
        });
        //////////////

        orderWeightCalulation();
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
                    //////// internet gotted ////

                    if (name.isEmpty() || whatsapp_number.isEmpty() || address.isEmpty() || landmark.isEmpty())
                            {
                        if (name.isEmpty()) {
                            editText_name.setError("Please enter name.");
                            progressDialog.dismiss();
                        }

                        if (whatsapp_number.isEmpty()) {
                            editText_whatsappno.setError("Please enter whatsApp number.");
                            progressDialog.dismiss();
                        }

                        if (whatsapp_number.length() < 10) {
                            editText_whatsappno.setError("Please enter valid whatsApp number.");
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

                        /*if (area_spinner.getSelectedItem().equals("Select Area")) {
                            Toast.makeText(DeliveryDetailsActivity.this, "Please select area.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        if (agent_spinner.getSelectedItem().equals("Select Agent")) {
                            Toast.makeText(DeliveryDetailsActivity.this, "Please select area.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }*/
                    }

                    if (name.isEmpty()) {
                        editText_name.setError("Please enter your name.");
                        progressDialog.dismiss();
                    } else if (whatsapp_number.isEmpty() || whatsapp_number.length() < 10) {
                        editText_whatsappno.setError("Please enter valid number.");
                        progressDialog.dismiss();
                    } else if (alternet_mobilenumber.length() >= 1 && alternet_mobilenumber.length() < 10) {
                        editText_alt_mobileno.setError("Please enter valid mobile number.");
                        progressDialog.dismiss();
                    } else if (address.isEmpty()) {
                        editText_address.setError("Please enter address");
                        progressDialog.dismiss();
                    } else if (landmark.isEmpty()) {
                        editText_landmark.setError("Please enter landmark.");
                        progressDialog.dismiss();
                    } /*else if (area_spinner.getSelectedItem().equals("Select Area")) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Please select area.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (agent_spinner.getSelectedItem().equals("Select Agent")) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Please select agent.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }*/ else {
                        try {
                            if (isStoragePermissionGranted1()) {
                                createPDF_Placeorder();
                            } else {
                                final AlertDialog.Builder adb = new AlertDialog.Builder(DeliveryDetailsActivity.this);
                                adb.setTitle(R.string.dialog_title);
                                adb.setCancelable(false);
                                adb.setMessage("If you want to placd order without Bill, Press Yes & if you want to get Bill, Press No and grant permission.");
                                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        indialogNo = true;
                                        if (isStoragePermissionGranted1()) {
                                            createPDF_Placeorder();
                                        }
                                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                == PackageManager.PERMISSION_DENIED) {
                                            indialogNo = false;
                                            dialog.dismiss();
                                            progressDialog.dismiss();

                                            AlertDialog.Builder adb1 = new AlertDialog.Builder(DeliveryDetailsActivity.this);
                                            adb1.setTitle(R.string.dialog_title);
                                            adb1.setMessage("You Want to place Order without Bill.");
                                            adb1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            adb1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    createPDF_Placeorder();
                                                }
                                            });
                                            adb1.show();
                                        }
                                    }
                                });
                                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        createPDF_Placeorder();
                                    }
                                });
                                adb.show();
                            }
                        } catch (Exception e) {
                        }
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


    public void btnSubmitPlaceOrder(View view) {
        progressDialog.show();

        firebaseId = dbref_orders.push().getKey();
        name = editText_name.getText().toString();
        whatsapp_number = editText_whatsappno.getText().toString();
        alternet_mobilenumber = editText_alt_mobileno.getText().toString();
        address = editText_address.getText().toString();
        landmark = editText_landmark.getText().toString();

        NetCheckTask task = new NetCheckTask();
        task.execute();
    }

    public void createPDF_Placeorder() {
        try {
            Random r = new Random();
            int no = r.nextInt(counter);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Manjiri Home Food Products/"
                    + current_date + "_" + no + ".pdf");

            String order_id = current_date + "_" + no;

            orderWeightCalulation();

            OrdersModel ordersModel = new OrdersModel(firebaseId, order_id, name, customer_id, whatsapp_number, alternet_mobilenumber,
                    order_weight, cart, String.valueOf(db.getCartCount()), current_date_time, String.valueOf(db.getTotalDiscountAmount()),
                    address, landmark, area_spinner.getSelectedItem().toString(), agent_spinner.getSelectedItem().toString(), "");

            dbref_orders.child(firebaseId).setValue(ordersModel);
            progressDialog.dismiss();

            dbref_notify.child("key").setValue("1234");
            dbref_notify.child("msg").setValue("New Order Recived from " + name + " , Area is " + area_spinner.getSelectedItem().toString() + "(" + current_date_time + ")");
            total_liter_oneproduct = 0;
            total_gram_oneproduct = 0;
            total_kg_oneproduct = 0;

            btn_ok.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setAnimation(for_icon);

            black_linear.setAlpha(1);
            black_linear.setAnimation(fromnothing);

            popup_linear.setAlpha(1);
            popup_linear.setAnimation(fromsmall);

            cust_name = name;
            cust_address = address + " \n Landmark :- " + landmark;
            cust_orderid = current_date + "_" + no;
            cust_mobile = whatsapp_number;

            if (!f.exists()) {
                f.createNewFile();
            }

            Document doc = new Document(PageSize.A4, 36, 36, 90, 36);
            PdfWriter.getInstance(doc, new FileOutputStream(f));
            doc.open();
            addTitlePage(doc);
            doc.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        total_gram_oneproduct = 0;
        total_kg_oneproduct = 0;
        total_liter_oneproduct = 0;
    }

    public void orderWeightCalulation() {
        if (total_liter_oneproduct >= 1) {
            if (total_gram_oneproduct >= 1000) {
                Double doublegram = Double.valueOf(total_gram_oneproduct);
                Double kgGram = Double.valueOf(((doublegram) / 1000));
                String strKGGRAM = "@" + (kgGram);
                String kg = strKGGRAM.substring(strKGGRAM.indexOf("@") + 1, strKGGRAM.indexOf("."));
                String gram = strKGGRAM.substring(strKGGRAM.indexOf(".") + 1);
                int kgInt = Integer.parseInt(kg);
                int newKG = (total_kg_oneproduct + kgInt);
                int newGRAM = Integer.parseInt(gram);
                order_weight = "" + newKG + " KG & " + newGRAM + " GRAM #" + total_liter_oneproduct + " Liter";
                Log.d("5432jk", "order if liter if gram" + order_weight);
            } else {
                order_weight = "" + total_kg_oneproduct + " KG & " + total_gram_oneproduct + " GRAM #" + total_liter_oneproduct + " Liter";
                Log.d("5432jk", "order if liter else gram" + order_weight);
            }
        } else {
            if (total_gram_oneproduct >= 1000) {
                Double doublegram = Double.valueOf(total_gram_oneproduct);
                Double kgGram = Double.valueOf(((doublegram) / 1000));
                String strKGGRAM = "@" + (kgGram);
                String kg = strKGGRAM.substring(strKGGRAM.indexOf("@") + 1, strKGGRAM.indexOf("."));
                String gram = strKGGRAM.substring(strKGGRAM.indexOf(".") + 1);
                int kgInt = Integer.parseInt(kg);
                int newKG = (total_kg_oneproduct + kgInt);
                int newGRAM = Integer.parseInt(gram);
                order_weight = "" + newKG + " KG & " + newGRAM + " GRAM";
                Log.d("5432jk", "order else liter if gram" + order_weight);
            } else {
                order_weight = "" + total_kg_oneproduct + " KG & " + total_gram_oneproduct + " GRAM";
                Log.d("5432jk", "order else liter else gram" + order_weight);
            }
        }
    }

    private static void addTitlePage(Document document)
            throws DocumentException {

        //for business name
        Paragraph shop_name = new Paragraph("Manjiri Home Food Products", catFont);
        shop_name.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(shop_name, 1);
        document.add(shop_name);
        //for business name end

        //for business contact
        Paragraph shop_contact = new Paragraph("Calling / WhatsApp No :- 9769373763", subFont);
        shop_contact.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(shop_contact, 1);
        document.add(shop_contact);
        //for business contact end

        //for business add
        Paragraph shop_add = new Paragraph("Shop Address : Bhaji Market, Tirupati Nagar Ph1, VIRAR(WEST).", subFont);
        shop_add.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(shop_add, 2);
        document.add(shop_add);
        //for business add end

        // for to
        Paragraph add_to = new Paragraph("To,");
        add_to.setAlignment(Element.ALIGN_LEFT);
        document.add(add_to);
        //for to end

        //for customer_name
        Paragraph c_name = new Paragraph(cust_name);
        c_name.setAlignment(Element.ALIGN_LEFT);
        document.add(c_name);
        //for customer_name end

        //for customer_orderid
        Paragraph c_oid = new Paragraph(cust_orderid);
        c_oid.setAlignment(Element.ALIGN_LEFT);
        document.add(c_oid);
        //for customer_orderid end

        //for customer_address
        Paragraph c_address = new Paragraph(cust_address);
        c_address.setAlignment(Element.ALIGN_LEFT);
        document.add(c_address);
        //for customer_address end

        //for customer_contactno
        Paragraph c_mobile = new Paragraph(cust_mobile);
        c_mobile.setAlignment(Element.ALIGN_LEFT);
        document.add(c_mobile);
        //for customer_mobile end

        //for bill date
        Paragraph date = new Paragraph("Order Date & Time :- " + current_date_time);
        date.setAlignment(Element.ALIGN_LEFT);
        addEmptyLine(date, 2);
        document.add(date);
        //for bill date end

        //for cart_data
        PdfPTable table = new PdfPTable(6);

        PdfPCell c1 = new PdfPCell(new Phrase("Sr. No."));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Product Name"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Product unit"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Rate"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Quantity"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Total"));
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        //table.setHeaderRows(1);

        for (int i = 0; i < db.getCartAll().size(); i++) {
            HashMap hashMap = (HashMap) DeliveryDetailsActivity.viewcart1.get(i);
            PricesModel pricesModel = (PricesModel) new GsonBuilder().create().fromJson((String) hashMap.get(DatabaseHandler.COLUMN_PRICES_SELECTED),
                    new TypeToken<PricesModel>() {
                    }.getType());
            int price = Integer.parseInt(pricesModel.getPrice());

            String p_name = String.valueOf(hashMap.get(DatabaseHandler.COLUMN_NAME));
            String p_price = String.valueOf(price);
            String p_qty = String.valueOf(hashMap.get(DatabaseHandler.COLUMN_QTY));
            String p_qty_unit = pricesModel.getQty() + " " + pricesModel.getUnit();
            String product_unit = pricesModel.getUnit();
            int total = Integer.parseInt(p_price) * Integer.parseInt(p_qty);

            int srno = i + 1;
            pro_name = p_name;
            pro_price = p_price;
            pro_quantity = p_qty;
            pro_qty_unit = p_qty_unit;

            table.addCell(srno + "");
            table.addCell(pro_name + " - " + pro_qty_unit);
            table.addCell(p_qty_unit);
            table.addCell(pro_price);
            table.addCell(pro_quantity);
            table.addCell(total + "");

            table.setHorizontalAlignment(Element.ALIGN_CENTER);
        }

        document.add(table);

        //for grand total
        Paragraph grand_total = new Paragraph("Grand Total:- " + String.valueOf(db.getTotalDiscountAmount()));
        grand_total.setAlignment(Element.ALIGN_RIGHT);
        grand_total.setIndentationRight(50);
        document.add(grand_total);

        //for grand total
        Paragraph weight_total = new Paragraph("Total Weight :- " + total_kg_oneproduct + " KG & " + total_gram_oneproduct + " GRAM");
        weight_total.setAlignment(Element.ALIGN_RIGHT);
        weight_total.setIndentationRight(50);
        addEmptyLine(weight_total, 3);
        document.add(weight_total);

        if (total_liter_oneproduct >= 1) {
            //for grand total
            Paragraph weight_total_liter = new Paragraph("Total Liter Weight :- " + total_liter_oneproduct);
            weight_total_liter.setAlignment(Element.ALIGN_RIGHT);
            weight_total_liter.setIndentationRight(50);
            addEmptyLine(weight_total_liter, 3);
            document.add(weight_total_liter);
        }

        //for grand total
        Paragraph thanks_para = new Paragraph("For Manjiri Home Food Products");
        thanks_para.setAlignment(Element.ALIGN_RIGHT);
        thanks_para.setIndentationRight(50);
        addEmptyLine(thanks_para, 2);
        document.add(thanks_para);

        //for grand total
        Paragraph signatory = new Paragraph("Authorised Signatory");
        signatory.setAlignment(Element.ALIGN_RIGHT);
        signatory.setIndentationRight(50);
        addEmptyLine(signatory, 1);
        document.add(signatory);
        document.newPage();
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public boolean isStoragePermissionGranted1() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                Toast.makeText(this, "Please give the permission.", Toast.LENGTH_SHORT).show();
                if (indialogNo) {
                    ActivityCompat.requestPermissions(DeliveryDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}