package com.aran.my_show_flower.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aran.my_show_flower.controller.RestManager;
import com.aran.my_show_flower.model.adapter.FlowerAdapter;
import com.aran.my_show_flower.model.callback.FlowerFetchListener;
import com.aran.my_show_flower.model.database.Database;
import com.aran.my_show_flower.model.database.FlowerDatabase;
import com.aran.my_show_flower.model.helper.Utils;
import com.aran.my_show_flower.model.pojo.Flower;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FlowerAdapter.FlowerClickListener, FlowerFetchListener {

    private RecyclerView mRecyclerView;
    private RestManager mManager;
    private FlowerAdapter mFlowerAdapter;
    private FlowerDatabase mDatabase;
    private Button mReload;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.aran.my_show_flower.R.layout.activity_main);
        configViews();

        mManager = new RestManager();
        mDatabase = new FlowerDatabase(this);

        loadFlowerFeed();

        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFlowerFeed();
            }
        });
    }

    private void loadFlowerFeed() {
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.getProgress();
        mDialog.setMessage("Loading Flower Data...");
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
        mFlowerAdapter.reset();
        mDialog.show();

        if (getNetworkAvailability()) {
            getFeed();
        } else {
            getFeedFromDatabase();
        }
    }

    private void getFeedFromDatabase() {
        mDatabase.fetchFlowers(this);
    }

    private void configViews() {
        mReload = findViewById(com.aran.my_show_flower.R.id.reload);
        mRecyclerView = findViewById(com.aran.my_show_flower.R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mFlowerAdapter = new FlowerAdapter(this);

        mRecyclerView.setAdapter(mFlowerAdapter);
    }

    @Override
    public void onClick(int position) {
        Flower selectedFlower = mFlowerAdapter.getSelectedFlower(position);
        DetailActivity.openFlowerDetail(this, selectedFlower);
    }

    public void getFeed() {

        Call<List<Flower>> listCall = mManager.getFlowerService().getAllFlowers();
        listCall.enqueue(new Callback<List<Flower>>() {
            @Override
            public void onResponse(Call<List<Flower>> call, Response<List<Flower>> response) {

                if (response.isSuccessful()) {
                    List<Flower> flowerList = response.body();

                    for (int i = 0; i < flowerList.size(); i++) {
                        Flower flower = flowerList.get(i);

                        SaveIntoDatabase task = new SaveIntoDatabase();
                        task.execute(flower);

                        mFlowerAdapter.addFlower(flower);
                    }
                } else {
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                    }
                }
                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Flower>> call, Throwable t) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    @Override
    public void onDeliverAllFlowers(List<Flower> flowers) {

    }

    @Override
    public void onDeliverFlower(Flower flower) {
        mFlowerAdapter.addFlower(flower);
    }

    @Override
    public void onHideDialog() {
        mDialog.dismiss();
    }
    public class SaveIntoDatabase extends AsyncTask<Flower, Void, Void> {

        private final String TAG = SaveIntoDatabase.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Flower... params) {

            Flower flower = params[0];

            try {
                InputStream stream = new URL(Database.BASE_URL + "/photos/" + flower.getPhoto()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                flower.setPicture(bitmap);
                mDatabase.addFlower(flower);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }
    }
}
