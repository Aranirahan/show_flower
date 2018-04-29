package com.aran.my_show_flower.controller;

import com.aran.my_show_flower.model.callback.FlowerService;
import com.aran.my_show_flower.model.database.Database;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestManager {

    private FlowerService mFlowerService;

    public FlowerService getFlowerService() {
        if (mFlowerService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Database.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mFlowerService = retrofit.create(FlowerService.class);
        }
        return mFlowerService;
    }
}
