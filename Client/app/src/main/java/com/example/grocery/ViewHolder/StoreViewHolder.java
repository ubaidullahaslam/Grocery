package com.example.grocery.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grocery.Interface.ItemClickListener;
import com.example.grocery.R;

public class StoreViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public StoreViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMenuName=itemView.findViewById(R.id.store_name);
        imageView=itemView.findViewById(R.id.store_image);
        itemView.setOnClickListener((View.OnClickListener) this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}