package com.example.grocery;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.Common.Common;
import com.example.grocery.Database.Database;
import com.example.grocery.Model.Order;
import com.example.grocery.Model.Request;
import com.example.grocery.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart =new ArrayList<>();

    CartAdapter adapter;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_cart,container,false);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //FireBAse
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        //Init
        recyclerView =(RecyclerView)v.findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice=(TextView)v.findViewById(R.id.total);
        btnPlace=(FButton)v.findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(getActivity(),"Your Cart Is Empty !!",Toast.LENGTH_SHORT).show();
            }
        });

        loadListGrocery();

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address");

        LayoutInflater inflator = this.getLayoutInflater();
        View order_address_comment = inflator.inflate(R.layout.order_address_comment,null);
        final MaterialEditText edtAddress = (MaterialEditText) order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment = (MaterialEditText) order_address_comment.findViewById(R.id.edtComment);
        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create new request
                Request request= new Request(
                        Common.currentUser.getName(),
                        Common.currentUser.getPhone(),
                        edtAddress.getText().toString(),

                        txtTotalPrice.getText().toString(),
                        "0", // status
                        edtComment.getText().toString(),
                        cart
                );

                //Submit to FireBase
                //We Will be using System.CurrentMilli to key
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                //Delete Cart

                new Database(getActivity()).cleanCart();
                Toast.makeText(getActivity(),"Thank You, Order Placed", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void loadListGrocery() {
        cart=new Database(getActivity()).getCarts();
        adapter=new CartAdapter(cart, getActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculating total price
        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))* (Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("en", "US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }
    //press Ctrl+O

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position)
    {
        //we will remove item at List<Order> by position
        cart.remove(position);
      // After that we'll delete all data from SQlite
        new Database(getActivity()).cleanCart();
        // and final we will update all data from List<Order> to SQlite
        for(Order item:cart)
           new Database(getActivity()).addToCart(item);
          // Refresh
        loadListGrocery();
    }
}
