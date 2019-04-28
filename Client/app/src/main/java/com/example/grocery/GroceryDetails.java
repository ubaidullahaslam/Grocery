package com.example.grocery;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.grocery.Database.Database;
import com.example.grocery.Model.Grocery;
import com.example.grocery.Model.Order;
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

    Grocery currentGrocery;

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

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        groceryId,
                        currentGrocery.getName(),
                        numberButton.getNumber(),
                        currentGrocery.getPrice(),
                        currentGrocery.getDiscount()
                        ));

                Toast.makeText(GroceryDetails.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

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
                currentGrocery=dataSnapshot.getValue(Grocery.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentGrocery.getImage())
                        .into(grocery_image);

                collapsingToolbarLayout.setTitle(currentGrocery.getName());

                grocery_price.setText(currentGrocery.getPrice());

                grocery_name.setText(currentGrocery.getName());

                grocery_description.setText(currentGrocery.getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
