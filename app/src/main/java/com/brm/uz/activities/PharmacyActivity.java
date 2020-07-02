package com.brm.uz.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.brm.uz.adapter.PharmacyNewOrdersAdapter;
import com.brm.uz.adapter.PharmacyOrdersAdapter;
import com.brm.uz.models.PharmacyNewOrdersPOJO;
import com.brm.uz.models.PharmacyOrdersPOJO;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PharmacyActivity extends AppCompatActivity implements View.OnClickListener {
    private CircularProgressView loading;
    private String current_user;
    private ArrayList<PharmacyOrdersPOJO> mainList;
    private ArrayList<PharmacyNewOrdersPOJO> secondList;
    private PharmacyOrdersAdapter adapter;
    private PharmacyNewOrdersAdapter newAdapter;
    private String[] words;
    private DatabaseReference reference;
    private RecyclerView recyclerView, newOrderRecycler;
    private LinearLayout linearLayout2, linearLayout1;
    private RadioGroup radioGroup;
    private TextView finalPriceTextView;
    private int finalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_orders);

        startElements();
        cursor(true);
    }

    void startElements(){
        loading = findViewById(R.id.pharmacy_orders_progress_bar);
        loading.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.pharmacy_orders_medications_recycler_view);
        newOrderRecycler = findViewById(R.id.pharmacy_orders_new_order_recycler_view);

        linearLayout2 = findViewById(R.id.pharmacy_orders_linearLayout3);
        linearLayout1 = findViewById(R.id.activity_pharmacy_orders_ll1);
        radioGroup = findViewById(R.id.activity_pharmacy_orders_radio_group);
        finalPriceTextView = findViewById(R.id.activity_pharmacy_orders_final_price);

        mainList = new ArrayList<>();
        secondList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

        words = getIntent().getStringExtra("start").split(" ");

        findViewById(R.id.pharmacy_orders_button_add).setOnClickListener(this);
        findViewById(R.id.pharmacy_orders_button_new_order).setOnClickListener(this);
    }

    void cursor(boolean check){
        for (int i = 0; i < words.length; i++) {
            if (check){
                getItems(words[i]);
            }
            else {
                newOrder(words[i]);
            }
        }
    }

    private void getItems(String type) {
        reference = FirebaseDatabase.getInstance().getReference().child("Medications").child(type);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        mainList.add(new PharmacyOrdersPOJO(dataSnapshot1.child("name").getValue().toString(), 0));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                adapter = new PharmacyOrdersAdapter(getApplicationContext(), mainList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                recyclerView.setAdapter(adapter);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void newOrder(String type){
        reference = FirebaseDatabase.getInstance().getReference().child("Medications").child(type);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    PharmacyNewOrdersPOJO p = dataSnapshot1.getValue(PharmacyNewOrdersPOJO.class);
                    p.setQuantity(0);
                    secondList.add(p);
                }
                newAdapter = new PharmacyNewOrdersAdapter(getApplicationContext(), secondList);
                newOrderRecycler.setHasFixedSize(true);
                newOrderRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                newOrderRecycler.setAdapter(newAdapter);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void informationSend(boolean check){
        reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user)
                .child(getIntent().getStringExtra("year"))
                .child(getIntent().getStringExtra("month")).child(getIntent().getStringExtra("day"))
                .child(getIntent().getStringExtra("id"));
        if (check){
            for (int i = 0; i < mainList.size(); i++) {
                if (mainList.get(i).getQuantity() != 0){
                    reference.child("stock").child("item" + i).child("name").setValue(mainList.get(i).getName());
                    reference.child("stock").child("item" + i).child("quantity").setValue(mainList.get(i).getQuantity());
                }
            }
        }
        else {
                reference = FirebaseDatabase.getInstance().getReference().child("Medications");
                for (int j = 0; j < secondList.size(); j++) {
                    if (secondList.get(j).getQuantity() != 0){
                        reference.child(secondList.get(j).getParent()).child(secondList.get(j).getId()).child("stock").setValue(secondList.get(j).getStock() - secondList.get(j).getQuantity());
                    }
                }
            newOrderSend();
            }
        }

    void newOrderSend(){
        reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(getIntent().getStringExtra("year"))
                .child(getIntent().getStringExtra("month")).child(getIntent().getStringExtra("day")).child(getIntent().getStringExtra("note"));
        reference.child("title").setValue(getIntent().getStringExtra("title"));
        reference.child("address").setValue(getIntent().getStringExtra("address"));
        for (int i = 0; i < secondList.size(); i++) {
            if (secondList.get(i).getQuantity() != 0){
                reference.child("items").child("item" + i).child("name").setValue(secondList.get(i).getName());
                reference.child("items").child("item" + i).child("quantity").setValue(secondList.get(i).getQuantity());
            }
        }
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){

           case R.id.pharmacy_orders_button_add:
               if (!secondList.isEmpty()){
                   informationSend(false);
                   finish();
               }
               else {
                   informationSend(true);
                   finish();
               }
               break;

           case R.id.pharmacy_orders_button_new_order:
               linearLayout2.setVisibility(View.VISIBLE);
               linearLayout1.setVisibility(View.GONE);
               cursor(false);
               informationSend(true);
               findViewById(R.id.pharmacy_orders_button_new_order).setVisibility(View.GONE);
               findViewById(R.id.pharmacy_orders_linearLayout4).setVisibility(View.VISIBLE);
               findViewById(R.id.pharmacy_orders_linearLayout1).setVisibility(View.GONE);
       }
    }

    public void radioButton(View view) {
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.activity_pharmacy_radio_100:
                finalPrice = 0;
                for (int i = 0; i < secondList.size(); i++) {
                    Log.d("MyLog", secondList.get(i).getName() + " " + i );
                    /*if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getFull() * secondList.get(i).getQuantity());
                    }*/
                }
                finalPriceTextView.setText("Общая стоимость при предоплате 100% : "+finalPrice);
                break;
            case R.id.activity_pharmacy_radio_50:
                finalPrice = 0;
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + secondList.get(i).getHalf();
                    }
                }
                finalPriceTextView.setText("Общая стоимость при предоплате 50% : "+finalPrice);
                break;
            case R.id.activity_pharmacy_radio_25:
                finalPrice = 0;
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + secondList.get(i).getSemi();
                    }
                }
                finalPriceTextView.setText("Общая стоимость при предоплате 25% : "+finalPrice);

        }
    }
}
