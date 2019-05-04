package com.example.grocery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.grocery.Database.Database;
import com.example.grocery.Interface.ItemClickListener;
import com.example.grocery.Model.Grocery;
import com.example.grocery.ViewHolder.GroceryViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.Share;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class GroceryList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference groceryList;

    String categoryId="";

    FirebaseRecyclerAdapter<Grocery,GroceryViewHolder> adapter;

    //Favourites
    Database localDB;

    //Facebook Share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //Create Target from Picasso
    Target target= new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create Photo from Bitmap
            SharePhoto photo=new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content=new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        //Init Facebook
        callbackManager= CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);



        //Firebase

        database = FirebaseDatabase.getInstance();
        groceryList=database.getReference("Grocery");

        //Local DB
        localDB=new Database(this);

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
            protected void populateViewHolder(final GroceryViewHolder viewHolder, final Grocery model, final int position) {
                viewHolder.grocery_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.grocery_image);

                //Add favourites
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to change state of favorites
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(GroceryList.this,""+model.getName()+" was added to Favorites",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(GroceryList.this,""+model.getName()+" was removed from Favorites",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Click To Share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });





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
