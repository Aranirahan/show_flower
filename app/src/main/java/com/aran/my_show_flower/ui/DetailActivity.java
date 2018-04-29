package com.aran.my_show_flower.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aran.my_show_flower.R;
import com.aran.my_show_flower.model.database.Database;
import com.aran.my_show_flower.model.pojo.Flower;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    public static final String PACKAGE_NAME = "com.aran.show_flower";
    public static final String FLOWER = PACKAGE_NAME + "flower";
    private ImageView mPhoto;
    private TextView mName, mId, mCategory, mInstruction, mPrice;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Flower flower = (Flower) intent.getSerializableExtra(FLOWER);
        configViews();

        mId.setText(String.format("%d", flower.getProductId()));
        mName.setText(flower.getName());
        mCategory.setText(flower.getCategory());
        mInstruction.setText(flower.getInstructions());
        mPrice.setText(String.format("$%.2f", flower.getPrice()));

        if (flower.isFromDatabase()) {
            mPhoto.setImageBitmap(flower.getPicture());
        } else {
            Picasso.get().load(Database.BASE_URL + "/photos/" + flower.getPhoto())
                    .into(mPhoto);
        }
    }

    private void configViews() {
        mPhoto =  findViewById(R.id.flowerPhoto);
        mName = findViewById(R.id.flowerName);
        mId = findViewById(R.id.flowerId);
        mCategory = findViewById(R.id.flowerCategory);
        mInstruction = findViewById(R.id.flowerInstruction);
        mPrice =  findViewById(R.id.flowerPrice);
    }

    public static void openFlowerDetail(Context context, Flower flower){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(FLOWER, flower);
        try{
            context.startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context, "Please Wait, Still Reloading", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
