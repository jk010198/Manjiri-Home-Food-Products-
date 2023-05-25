package com.ithub.groceryshop.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ithub.groceryshop.Models.PricesModel;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String CART_TABLE = "cart";
    public static final String COLUMN_CAT_ID = "category_id";
    public static final String COLUMN_DISCOUNT = "discount";
    public static final String COLUMN_ID = "product_id";
    public static final String COLUMN_IMAGE = "product_image";
    public static final String COLUMN_INCREAMENT = "increament";
    public static final String COLUMN_IN_STOCK = "in_stock";
    public static final String COLUMN_NAME = "product_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_PRICES = "prices";
    public static final String COLUMN_PRICES_SELECTED = "prices_selected";
    public static final String COLUMN_PRICES_SELECTED_ID = "prices_selected_id";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_STOCK = "stock";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_UNIT_VALUE = "unit_value";
    public static String DB_NAME = "grocery";
    private static int DB_VERSION = 3;
    private Context context;

    /* renamed from: db */
    private SQLiteDatabase f102db;

    public DatabaseHandler(Context context2) {
        super(context2, DB_NAME, null, DB_VERSION);
        this.context = context2;
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        this.f102db = sQLiteDatabase;
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS cart(prices_selected_id integer primary key, qty DOUBLE NOT NULL,product_image TEXT NOT NULL, category_id TEXT NOT NULL, product_name TEXT NOT NULL, price DOUBLE NOT NULL, unit_value DOUBLE NOT NULL, unit TEXT NOT NULL, increament DOUBLE NOT NULL, stock DOUBLE NOT NULL, in_stock TEXT NOT NULL, discount DOUBLE NOT NULL, prices TEXT NOT NULL, prices_selected TEXT NOT NULL, product_id INTEGER NOT NULL, title TEXT NOT NULL )");
    }

    public boolean setCart(HashMap<String, String> hashMap, Float f) {
        this.f102db = getWritableDatabase();
        String str = COLUMN_PRICES_SELECTED_ID;
        if (isInCartPrice((String) hashMap.get(str))) {
            SQLiteDatabase sQLiteDatabase = this.f102db;
            StringBuilder sb = new StringBuilder();
            sb.append("update cart set qty = '");
            sb.append(f);
            sb.append("' where ");
            sb.append(str);
            sb.append(" = ");
            sb.append((String) hashMap.get(str));
            sQLiteDatabase.execSQL(sb.toString());
            return false;
        }
        ContentValues contentValues = new ContentValues();
        String str2 = COLUMN_ID;
        contentValues.put(str2, (String) hashMap.get(str2));
        contentValues.put(COLUMN_QTY, f);
        String str3 = COLUMN_CAT_ID;
        contentValues.put(str3, (String) hashMap.get(str3));
        String str4 = COLUMN_IMAGE;
        contentValues.put(str4, (String) hashMap.get(str4));
        String str5 = COLUMN_INCREAMENT;
        contentValues.put(str5, (String) hashMap.get(str5));
        String str6 = COLUMN_NAME;
        contentValues.put(str6, (String) hashMap.get(str6));
        String str7 = "price";
        contentValues.put(str7, (String) hashMap.get(str7));
        String str8 = COLUMN_STOCK;
        contentValues.put(str8, (String) hashMap.get(str8));
        String str9 = COLUMN_TITLE;
        contentValues.put(str9, (String) hashMap.get(str9));
        String str10 = COLUMN_UNIT;
        contentValues.put(str10, (String) hashMap.get(str10));
        String str11 = COLUMN_UNIT_VALUE;
        contentValues.put(str11, (String) hashMap.get(str11));
        String str12 = COLUMN_IN_STOCK;
        contentValues.put(str12, (String) hashMap.get(str12));
        String str13 = COLUMN_DISCOUNT;
        contentValues.put(str13, (String) hashMap.get(str13));
        String str14 = COLUMN_PRICES;
        contentValues.put(str14, (String) hashMap.get(str14));
        String str15 = COLUMN_PRICES_SELECTED;
        contentValues.put(str15, (String) hashMap.get(str15));
        contentValues.put(str, (String) hashMap.get(str));
        this.f102db.insert(CART_TABLE, null, contentValues);
        return true;
    }

    public boolean isInCart(String str) {
        this.f102db = getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("Select *  from cart where product_id = ");
        sb.append(str);
        Cursor rawQuery = this.f102db.rawQuery(sb.toString(), null);
        rawQuery.moveToFirst();
        return rawQuery.getCount() > 0;
    }

    public boolean isInCartPrice(String str) {
        this.f102db = getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("Select *  from cart where prices_selected_id = ");
        sb.append(str);
        Cursor rawQuery = this.f102db.rawQuery(sb.toString(), null);
        rawQuery.moveToFirst();
        return rawQuery.getCount() > 0;
    }

    public String getCartItemQty(String str) {
        this.f102db = getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("Select *  from cart where product_id = ");
        sb.append(str);
        Cursor rawQuery = this.f102db.rawQuery(sb.toString(), null);
        rawQuery.moveToFirst();
        return rawQuery.getString(rawQuery.getColumnIndex(COLUMN_QTY));
    }

    public String getInCartItemQty(String str) {
        if (!isInCart(str)) {
            return "0.0";
        }
        this.f102db = getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("Select *  from cart where product_id = ");
        sb.append(str);
        Cursor rawQuery = this.f102db.rawQuery(sb.toString(), null);
        rawQuery.moveToFirst();
        return rawQuery.getString(rawQuery.getColumnIndex(COLUMN_QTY));
    }

    public String getInCartItemQtyPrice(String str) {
        if (!isInCartPrice(str)) {
            return "0.0";
        }
        this.f102db = getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("Select *  from cart where prices_selected_id = ");
        sb.append(str);
        Cursor rawQuery = this.f102db.rawQuery(sb.toString(), null);
        rawQuery.moveToFirst();
        return rawQuery.getString(rawQuery.getColumnIndex(COLUMN_QTY));
    }

    public int getCartCount() {
        this.f102db = getReadableDatabase();
        return this.f102db.rawQuery("Select *  from cart", null).getCount();
    }

    public String getTotalAmount() {
        this.f102db = getReadableDatabase();
        Cursor rawQuery = this.f102db.rawQuery("Select SUM(qty * price) as total_amount  from cart", null);
        rawQuery.moveToFirst();
        String string = rawQuery.getString(rawQuery.getColumnIndex("total_amount"));
        return string != null ? string : "0";
    }

    public String getTotalDiscountAmount() {
        this.f102db = getReadableDatabase();
        Cursor rawQuery = this.f102db.rawQuery("Select *  from cart", null);
        rawQuery.moveToFirst();
        Double valueOf = Double.valueOf(0.0d);
        for (int i = 0; i < rawQuery.getCount(); i++) {
            HashMap hashMap = new HashMap();
            String str = COLUMN_QTY;
            hashMap.put(str, rawQuery.getString(rawQuery.getColumnIndex(str)));
            String str2 = COLUMN_PRICES_SELECTED;
            hashMap.put(str2, rawQuery.getString(rawQuery.getColumnIndex(str2)));
            String str3 = COLUMN_PRICES_SELECTED_ID;
            hashMap.put(str3, rawQuery.getString(rawQuery.getColumnIndex(str3)));
            PricesModel pricesModel = (PricesModel) new GsonBuilder().create().fromJson(rawQuery.getString(rawQuery.getColumnIndex(str2)), new TypeToken<PricesModel>() {
            }.getType());
            valueOf = Double.valueOf(valueOf.doubleValue() + Double.valueOf(Double.valueOf(Double.valueOf(pricesModel.getPrice()).doubleValue()).doubleValue() * Double.valueOf(Double.parseDouble(rawQuery.getString(rawQuery.getColumnIndex(str)))).doubleValue()).doubleValue());
            rawQuery.moveToNext();
        }
        this.f102db.close();
        return String.format("%.2f", new Object[]{valueOf});
    }

    public ArrayList<HashMap<String, String>> getCartAll() {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        this.f102db = getReadableDatabase();
        Cursor rawQuery = this.f102db.rawQuery("Select *  from cart", null);
        rawQuery.moveToFirst();
        for (int i = 0; i < rawQuery.getCount(); i++) {
            HashMap hashMap = new HashMap();
            String str = COLUMN_ID;
            hashMap.put(str, rawQuery.getString(rawQuery.getColumnIndex(str)));
            String str2 = COLUMN_QTY;
            hashMap.put(str2, rawQuery.getString(rawQuery.getColumnIndex(str2)));
            String str3 = COLUMN_IMAGE;
            hashMap.put(str3, rawQuery.getString(rawQuery.getColumnIndex(str3)));
            String str4 = COLUMN_CAT_ID;
            hashMap.put(str4, rawQuery.getString(rawQuery.getColumnIndex(str4)));
            String str5 = COLUMN_NAME;
            hashMap.put(str5, rawQuery.getString(rawQuery.getColumnIndex(str5)));
            String str6 = "price";
            hashMap.put(str6, rawQuery.getString(rawQuery.getColumnIndex(str6)));
            String str7 = COLUMN_UNIT_VALUE;
            hashMap.put(str7, rawQuery.getString(rawQuery.getColumnIndex(str7)));
            String str8 = COLUMN_UNIT;
            hashMap.put(str8, rawQuery.getString(rawQuery.getColumnIndex(str8)));
            String str9 = COLUMN_INCREAMENT;
            hashMap.put(str9, rawQuery.getString(rawQuery.getColumnIndex(str9)));
            String str10 = COLUMN_STOCK;
            hashMap.put(str10, rawQuery.getString(rawQuery.getColumnIndex(str10)));
            String str11 = COLUMN_TITLE;
            hashMap.put(str11, rawQuery.getString(rawQuery.getColumnIndex(str11)));
            String str12 = COLUMN_DISCOUNT;
            hashMap.put(str12, rawQuery.getString(rawQuery.getColumnIndex(str12)));
            String str13 = COLUMN_IN_STOCK;
            hashMap.put(str13, rawQuery.getString(rawQuery.getColumnIndex(str13)));
            String str14 = COLUMN_PRICES;
            hashMap.put(str14, rawQuery.getString(rawQuery.getColumnIndex(str14)));
            String str15 = COLUMN_PRICES_SELECTED;
            hashMap.put(str15, rawQuery.getString(rawQuery.getColumnIndex(str15)));
            hashMap.put(COLUMN_PRICES_SELECTED_ID, rawQuery.getString(rawQuery.getColumnIndex(COLUMN_PRICES_SELECTED_ID)));
            arrayList.add(hashMap);
            rawQuery.moveToNext();
        }
        this.f102db.close();
        return arrayList;
    }

    public String getFavConcatString() {
        this.f102db = getReadableDatabase();
        Cursor rawQuery = this.f102db.rawQuery("Select *  from cart", null);
        rawQuery.moveToFirst();
        String str = "";
        String str2 = str;
        for (int i = 0; i < rawQuery.getCount(); i++) {
            boolean equalsIgnoreCase = str2.equalsIgnoreCase(str);
            String str3 = COLUMN_ID;
            if (equalsIgnoreCase) {
                str2 = rawQuery.getString(rawQuery.getColumnIndex(str3));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append("_");
                sb.append(rawQuery.getString(rawQuery.getColumnIndex(str3)));
                str2 = sb.toString();
            }
            rawQuery.moveToNext();
        }
        return str2;
    }

    public void clearCart() {
        this.f102db = getReadableDatabase();
        this.f102db.execSQL("delete from cart");
    }

    public void removeItemFromCart(String str) {
        this.f102db = getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = this.f102db;
        StringBuilder sb = new StringBuilder();
        sb.append("delete from cart where product_id = ");
        sb.append(str);
        sQLiteDatabase.execSQL(sb.toString());
    }

    public void removeItemFromCartPrice(String str) {
        this.f102db = getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = this.f102db;
        StringBuilder sb = new StringBuilder();
        sb.append("delete from cart where prices_selected_id = ");
        sb.append(str);
        sQLiteDatabase.execSQL(sb.toString());
        this.f102db.close();
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i2 == 2) {
            sQLiteDatabase.execSQL("ALTER TABLE cart ADD COLUMN in_stock TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE cart ADD COLUMN discount DOUBLE NOT NULL Default 0.0");
        } else if (i2 == 3) {
            this.context.deleteDatabase(DB_NAME);
        }
    }

    public int getCurrentVersion() {
        this.f102db = getReadableDatabase();
        try {
            return this.f102db.getVersion();
        } finally {
            this.f102db.close();
        }
    }
}
