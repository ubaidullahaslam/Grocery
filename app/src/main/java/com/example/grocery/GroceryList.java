package com.example.grocery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    FirebaseRecyclerAdapter<Grocery,GroceryViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);


        //Firebase

        database = FirebaseDatabase.getInstance();
        groceryList=database.getReference("Grocery");

        recyclerView=(RecyclerView) findViewById(R.id.recycler_grocery);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get Intent here
        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId!=null)
        {
            loadListGrocery(categoryId);
        }

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

    }
}
