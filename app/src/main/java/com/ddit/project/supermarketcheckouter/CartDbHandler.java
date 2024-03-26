package com.ddit.project.supermarketcheckouter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;

import java.util.ArrayList;

public class CartDbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cartManager";
    private static final String TABLE_PRODUCT = "cartproduct";
    private static final String KEY_ID = "id";
    private static final String KEY_PRODCUT_ID = "product_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRDCT_IMAGE = "image";
    private static final String KEY_PRDCT_CAT_ID = "category_id";
    private static final String KEY_PRDCT_CAT_NM = "category_name";
    private static final String KEY_PRDCT_PRICE = "price";
    private static final String KEY_PRDCT_CODE = "product_code";
    private static final String KEY_PRDCT_ITEM = "product_item";

    public CartDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CARTS_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PRODCUT_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_PRDCT_IMAGE + " TEXT,"
                + KEY_PRDCT_CAT_ID + " TEXT,"
                + KEY_PRDCT_CAT_NM + " TEXT,"
                + KEY_PRDCT_PRICE + " TEXT,"
                + KEY_PRDCT_CODE + " TEXT,"
                + KEY_PRDCT_ITEM + " TEXT" + ")";
        db.execSQL(CREATE_CARTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);

        // Create tables again
        onCreate(db);
    }


    void addItemTocart(CartItem_GetSet cartproduct) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRODCUT_ID, cartproduct.getProduct_id());
        values.put(KEY_NAME, cartproduct.getName());
        values.put(KEY_PRDCT_IMAGE, cartproduct.getImage());
        values.put(KEY_PRDCT_CAT_ID, cartproduct.getCategory_id());
        values.put(KEY_PRDCT_CAT_NM, cartproduct.getCategory_name());
        values.put(KEY_PRDCT_PRICE, cartproduct.getPrice());
        values.put(KEY_PRDCT_CODE, cartproduct.getProduct_code());
        values.put(KEY_PRDCT_ITEM, cartproduct.getProduct_items());

        db.insert(TABLE_PRODUCT, null, values);

        db.close();
    }

    CartItem_GetSet getOneCartItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{KEY_ID,
                        KEY_PRODCUT_ID, KEY_NAME, KEY_PRDCT_IMAGE, KEY_PRDCT_CAT_ID, KEY_PRDCT_CAT_NM, KEY_PRDCT_PRICE, KEY_PRDCT_CODE, KEY_PRDCT_ITEM}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        CartItem_GetSet cartproduct = new CartItem_GetSet(cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        // return cartproduct
        return cartproduct;
    }


    public ArrayList<CartItem_GetSet> getAllCartLists() {
        ArrayList<CartItem_GetSet> cartproductList = new ArrayList<CartItem_GetSet>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CartItem_GetSet cartproduct = new CartItem_GetSet(cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
                cartproductList.add(cartproduct);
            } while (cursor.moveToNext());
        }

        return cartproductList;
    }

    public int updateCartItem(CartItem_GetSet cartproduct) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRDCT_ITEM, cartproduct.getProduct_items());

        // updating row
        return db.update(TABLE_PRODUCT, values, KEY_PRODCUT_ID + " = ?", new String[]{String.valueOf(cartproduct.getProduct_id())});
    }


    public void deleteCartItem(CartItem_GetSet cartproduct) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_PRODCUT_ID + " = ?", new String[]{String.valueOf(cartproduct.getProduct_id())});
        db.close();
    }

    // Getting cartproducts Count
    public int getCartItemsCount() {
        int passcount = 0;
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        passcount = cursor.getCount();
        cursor.close();
        return passcount;
    }

    public boolean getCartItemsExist(String prodct_id) {
        boolean existval = false;
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCT + " WHERE " + KEY_PRODCUT_ID + "=" + prodct_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.getCount() > 0) {
            existval = true;
        } else {
            existval = false;
        }
        cursor.close();
        return existval;
    }

    public void removeAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, null, null);
        db.close();
    }
}
