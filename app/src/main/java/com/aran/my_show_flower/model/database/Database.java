package com.aran.my_show_flower.model.database;

public class Database {

        public static final String BASE_URL = "http://services.hanselandpetal.com";

        static final String DB_NAME = "flowers";
        static final int DB_VERSION = 1;
        static final String TABLE_NAME = "flower";

        static final String DROP_QUERY = "DROP TABLE IF EXIST " + TABLE_NAME;
        static final String GET_FLOWERS_QUERY = "SELECT * FROM " + TABLE_NAME;

        static final String PRODUCT_ID = "productId";
        static final String CATEGORY = "category";
        static final String PRICE = "price";
        static final String INSTRUCTIONS = "instructions";
        static final String NAME = "name";
        static final String PHOTO_URL = "photo_url";
        static final String PHOTO = "photo";

        static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "" +
                "(" + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CATEGORY + " TEXT not null," +
                PRICE + " TEXT not null," +
                INSTRUCTIONS + " TEXT not null," +
                NAME + " TEXT not null," +
                PHOTO_URL + " TEXT not null," +
                PHOTO + " blob not null)";
}
