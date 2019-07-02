package com.example.grocery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.Common.Common;
import com.example.grocery.Interface.ItemClickListener;
import com.example.grocery.Model.Store;
import com.example.grocery.Model.Store;
import com.example.grocery.ViewHolder.MenuViewHolder;
import com.example.grocery.ViewHolder.StoreViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Stores extends Fragment {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.stores,container,false);
        return v;
    }

    FirebaseDatabase database;
    DatabaseReference stores;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Store,StoreViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        stores = database.getReference("Stores");
        //load menu
        recycler_menu=v.findViewById(R.id.recycler_store);
        recycler_menu.setHasFixedSize(true);
        //layoutManager=new LinearLayoutManager(this);
        layoutManager=new GridLayoutManager(getActivity(),1);
        recycler_menu.setLayoutManager(layoutManager);
        //view
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getActivity()))
                    loadMenu();
                else {
                    Toast.makeText(getActivity(), "Please Check Your Connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        // default , load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getActivity()))
                    loadMenu();
                else {
                    Toast.makeText(getActivity(), "Please Check Your Connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    private void loadMenu() {
        adapter=new FirebaseRecyclerAdapter<Store,StoreViewHolder>(Store.class,R.layout.store_item,StoreViewHolder.class,stores) {
            @Override
            protected void populateViewHolder(StoreViewHolder viewHolder, Store model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getActivity()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Store clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getActivity(),""+clickItem.getName(),Toast.LENGTH_SHORT).show();

                        //Get storesId and send to new activity
                        Intent groceryList=new Intent(getActivity(),GroceryList.class);
                        //Because storesId is key,so we just get key of this item
                        groceryList.putExtra("StoreId",adapter.getRef(position).getKey());
                        Toast.makeText(getActivity(),adapter.getRef(position).getKey(),Toast.LENGTH_SHORT).show();
                        startActivity(groceryList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
