
package com.aran.my_show_flower.model.callback;

import com.aran.my_show_flower.model.pojo.Flower;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FlowerService {

    @GET("/feeds/flowers.json")
    Call<List<Flower>> getAllFlowers();
}
