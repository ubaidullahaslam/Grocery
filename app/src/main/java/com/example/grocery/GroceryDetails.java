package com.example.grocery;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.grocery.Model.Grocery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GroceryDetails extends AppCompatActivity {

    TextView grocery_name,grocery_price,grocery_description;
    ImageView grocery_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String groceryId="";

    FirebaseDatabase database;
    DatabaseReference grocery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_details);


        //Firebase
        database = FirebaseDatabase.getInstance();
        grocery= database.getReference("Grocery");

        //Init view
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);

        grocery_description=(TextView)findViewById(R.id.grocery_description);
        grocery_name=(TextView)findViewById(R.id.grocery_name);
        grocery_price=(TextView)findViewById(R.id.grocery_price);
        grocery_image=(ImageView)findViewById(R.id.img_grocery);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get Food Id from intent

        if(getIntent()!= null)
            groceryId=getIntent().getStringExtra("GroceryId");
        if(!groceryId.isEmpty())
        {
            getDetailGrocery(groceryId);
        }



    }

    private void getDetailGrocery(final String groceryId) {
        grocery.child(groceryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Grocery grocery=dataSnapshot.getValue(Grocery.class);

                //Set Image
                Picasso.with(getBaseContext()).load(grocery.getImage())
                        .into(grocery_image);

                collapsingToolbarLayout.setTitle(grocery.getName());

                grocery_price.setText(grocery.getPrice());

                grocery_name.setText(grocery.getName());

                grocery_description.setText(grocery.getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
