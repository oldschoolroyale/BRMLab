package com.brm.uz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.brm.uz.R;
import com.brm.uz.adapter.OrderAdapter;
import com.brm.uz.models.PharmacyNewOrdersPOJO;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private CircularProgressView loading;
    private OrderAdapter newAdapter;
    private String[] words;
    private double finalPrice;
    private RecyclerView recyclerView;
    private ArrayList<PharmacyNewOrdersPOJO> secondList;
    private RadioGroup radioGroup;
    private TextView finalPriceTextView;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        startElements();
        cursor();
    }

    private void startElements() {
        loading = findViewById(R.id.activity_order_circle_progress_bar);
        loading.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.activity_order_new_order_recycler_view);
        radioGroup = findViewById(R.id.activity_order_radio_group);
        finalPriceTextView = findViewById(R.id.activity_order_final_price);

        secondList = new ArrayList<>();

        words = getIntent().getStringExtra("start").split(" ");

        findViewById(R.id.activity_order_button_add).setOnClickListener(this);
    }

    void cursor(){
        for (int i = 0; i < words.length; i++) {
            newOrder(words[i]);
        }
    }

    private void newOrder(String type){
        reference = FirebaseDatabase.getInstance().getReference().child("Medications").child(type);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    PharmacyNewOrdersPOJO p = dataSnapshot1.getValue(PharmacyNewOrdersPOJO.class);
                    secondList.add(p);
                }
                newAdapter = new OrderAdapter(getApplicationContext(), secondList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(newAdapter);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void radioButton(View view) {
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.activity_order_radio_100:
                finalPrice = 0;
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getFull() * secondList.get(i).getQuantity());
                    }
                }
                finalPriceTextView.setText("Общая стоимость при предоплате 100% : "+finalPrice);
                break;
            case R.id.activity_order_radio_50:
                finalPrice = 0;
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getHalf() * secondList.get(i).getQuantity());
                    }
                }
                finalPriceTextView.setText("Общая стоимость при предоплате 50% : "+finalPrice);
                break;
            case R.id.activity_order_radio_25:
                finalPrice = 0;
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getSemi() * secondList.get(i).getQuantity());
                    }
                }
                finalPriceTextView.setText("Общая стоимость при предоплате 25% : "+finalPrice);

        }
    }

    void newOrderSend(){
        reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(getIntent().getStringExtra("year"))
        .child(getIntent().getStringExtra("month")).child(getIntent().getStringExtra("day")).child(getIntent().getStringExtra("note"));

        for (int i = 0; i < secondList.size(); i++) {
            if (secondList.get(i).getQuantity() != 0){
                reference.child("items").child("item" + i).child("name").setValue(secondList.get(i).getName());
                reference.child("items").child("item" + i).child("quantity").setValue(secondList.get(i).getQuantity());
                reference.child("price").setValue(finalPrice);
                reference.child("title").setValue(getIntent().getStringExtra("title"));
                reference.child("address").setValue(getIntent().getStringExtra("address"));
            }
        }
        finish();
    }

    void updateReference(){
        reference = FirebaseDatabase.getInstance().getReference().child("Medications");
        for (int j = 0; j < secondList.size(); j++) {
            if (secondList.get(j).getQuantity() != 0){
                reference.child(secondList.get(j).getParent()).child(secondList.get(j).getId()).child("stock").setValue(secondList.get(j).getStock() - secondList.get(j).getQuantity());
            }
        }
        newOrderSend();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_order_button_add:
                updateReference();
        }
    }
}
