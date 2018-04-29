package com.aran.my_show_flower.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aran.my_show_flower.model.callback.FlowerFetchListener;
import com.aran.my_show_flower.model.helper.Utils;
import com.aran.my_show_flower.model.pojo.Flower;

import java.util.ArrayList;
import java.util.List;

public class FlowerDatabase extends SQLiteOpenHelper {

    private static final String TAG = FlowerDatabase.class.getSimpleName();

    public FlowerDatabase(Context context) {
        super(context, Database.DB_NAME, null, Database.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Database.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Database.DROP_QUERY);
        this.onCreate(db);
    }

    public void addFlower(Flower flower) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Database.CATEGORY, flower.getCategory());
        values.put(Database.PRICE, Double.toString(flower.getPrice()));
        values.put(Database.INSTRUCTIONS, flower.getInstructions());
        values.put(Database.NAME, flower.getName());
        values.put(Database.PHOTO_URL, flower.getPhoto());
        values.put(Database.PHOTO, Utils.getPictureByteOfArray(flower.getPicture()));

        try {
            db.insert(Database.TABLE_NAME, null, values);
        } catch (Exception e) {
            //do nothing
        }
        db.close();
    }

    public void fetchFlowers(FlowerFetchListener listener) {
        FlowerFetcher fetcher = new FlowerFetcher(listener, this.getWritableDatabase());
        fetcher.start();
    }

    public class FlowerFetcher extends Thread {

        private final FlowerFetchListener mListener;
        private final SQLiteDatabase mDb;

        private FlowerFetcher(FlowerFetchListener listener, SQLiteDatabase db) {
            mListener = listener;
            mDb = db;
        }

        @Override
        public void run() {
            Cursor cursor = mDb.rawQuery(Database.GET_FLOWERS_QUERY, null);

            final List<Flower> flowerList = new ArrayList<>();

            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Flower flower = new Flower();
                        flower.setFromDatabase(true);
                        flower.setName(cursor.getString(cursor
                                .getColumnIndex(Database.NAME)));
                        flower.setPrice(Double.parseDouble(cursor
                                .getString(cursor.getColumnIndex(Database.PRICE))));
                        flower.setInstructions(cursor.getString(cursor
                                .getColumnIndex(Database.INSTRUCTIONS)));
                        flower.setCategory(cursor.getString(cursor
                                .getColumnIndex(Database.CATEGORY)));
                        flower.setPicture(Utils.getBitmapFromByte(cursor
                                .getBlob(cursor.getColumnIndex(Database.PHOTO))));
                        flower.setProductId(Integer.parseInt(cursor
                                .getString(cursor.getColumnIndex(Database.PRODUCT_ID))));
                        flower.setPhoto(cursor.getString(cursor
                                .getColumnIndex(Database.PHOTO_URL)));

                        flowerList.add(flower);
                        publishFlower(flower);

                    } while (cursor.moveToNext());
                }
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverAllFlowers(flowerList);
                    mListener.onHideDialog();
                }
            });
        }

        public void publishFlower(final Flower flower) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverFlower(flower);
                }
            });
        }
    }
}
