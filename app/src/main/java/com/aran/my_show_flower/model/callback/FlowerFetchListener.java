package com.aran.my_show_flower.model.callback;

import com.aran.my_show_flower.model.pojo.Flower;

import java.util.List;

public interface FlowerFetchListener {

    void onDeliverAllFlowers(List<Flower> flowers);

    void onDeliverFlower(Flower flower);

    void onHideDialog();
}
