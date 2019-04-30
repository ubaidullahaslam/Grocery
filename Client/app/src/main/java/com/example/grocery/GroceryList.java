package com.example.grocery;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.grocery.Common.Common;
import com.example.grocery.Interface.ItemClickListener;
import com.example.grocery.Model.Grocery;
import com.example.grocery.ViewHolder.GroceryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class GroceryList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference groceryList;

    String categoryId="";
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Grocery,GroceryViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);


        //Firebase

        database = FirebaseDatabase.getInstance();
        groceryList=database.getReference("Grocery");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
                );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get Intent here
                if(getIntent()!=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId!=null)
                {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListGrocery(categoryId);
                    else
                    {
                        Toast.makeText(GroceryList.this,"Please Check Your Connection !!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get Intent here
                if(getIntent()!=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId!=null)
                {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListGrocery(categoryId);
                    else
                    {
                        Toast.makeText(GroceryList.this,"Please Check Your Connection !!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        recyclerView=(RecyclerView) findViewById(R.id.recycler_grocery);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



    }

    private void loadListGrocery(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Grocery, GroceryViewHolder>(Grocery.class,
                R.layout.grocery_item,GroceryViewHolder.class,
                groceryList.orderByChild("MenuId").equalTo(categoryId) //like Select * from Grocery where MenuId=
        ) {
            @Override
            protected void populateViewHolder(GroceryViewHolder viewHolder, Grocery model, int position) {
                viewHolder.grocery_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.grocery_image);

                final Grocery local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       Toast.makeText(GroceryList.this,""+local.getName(),Toast.LENGTH_SHORT).show();
                        //Start New Activity

                        Intent groceryDetail=new Intent(GroceryList.this,GroceryDetails.class);
                        groceryDetail.putExtra("GroceryId",adapter.getRef(position).getKey());  //send grocery id to new activity
                        startActivity(groceryDetail);
                    }
                });


                }
        };
        //set Adapter

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }
}
