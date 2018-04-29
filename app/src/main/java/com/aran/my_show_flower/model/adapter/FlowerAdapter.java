package com.aran.my_show_flower.model.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aran.my_show_flower.R;
import com.aran.my_show_flower.model.database.Database;
import com.aran.my_show_flower.model.pojo.Flower;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.Holder> {

    private final FlowerClickListener mListener;
    private List<Flower> mFlowers;

    public FlowerAdapter(FlowerClickListener listener) {
        mFlowers = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, null, false);
        return new Holder(row);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Flower currFlower = mFlowers.get(position);

        holder.mName.setText(currFlower.getName());
        holder.mPrice.setText(String.format("$%.2f", currFlower.getPrice()));

        if (currFlower.isFromDatabase()) {
            holder.mPhoto.setImageBitmap(currFlower.getPicture());
        } else {
            Picasso.get().load(Database.BASE_URL + "/photos/" + currFlower
                    .getPhoto())
                    .into(holder.mPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mFlowers.size();
    }

    public void addFlower(Flower flower) {
        mFlowers.add(flower);
        notifyDataSetChanged();
    }

    public Flower getSelectedFlower(int position) {
        return mFlowers.get(position);
    }

    public void reset() {
        mFlowers.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPhoto;
        private TextView mName, mPrice;

        private Holder(View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.flowerPhoto);
            mName = itemView.findViewById(R.id.flowerName);
            mPrice = itemView.findViewById(R.id.flowerPrice);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getLayoutPosition());
        }
    }

    public interface FlowerClickListener {
        void onClick(int position);
    }
}
