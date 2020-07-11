package com.brm.uz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brm.uz.R;
import com.brm.uz.activities.startActivity.UpdateActivityCheck;
import com.brm.uz.adapter.OrderAdapter;
import com.brm.uz.helper.UpdateHelper;
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
    private RadioButton radioButton;
    private DatabaseReference reference;
    private int radioId;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        startElements();
        cursor();
    }

    private void startElements() {
        loading = findViewById(R.id.activity_order_circle_progress_bar);
        linearLayout = findViewById(R.id.activity_order_main_linear_layout);
        inVisible();
        recyclerView = findViewById(R.id.activity_order_new_order_recycler_view);
        radioGroup = findViewById(R.id.activity_order_radio_group);

        secondList = new ArrayList<>();

        words = getIntent().getStringExtra("start").split(" ");

        findViewById(R.id.activity_order_button_add).setOnClickListener(this);
    }

    void cursor(){
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < words.length; i++) {
                    newOrder(words[i]);
                }
                visible();
            }
        }, 1000);
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
                radioId = radioGroup.getCheckedRadioButtonId();
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getFull() * secondList.get(i).getQuantity());
                        secondList.get(i).setPrice(secondList.get(i).getFull());
                    }
                }
                break;
            case R.id.activity_order_radio_50:
                finalPrice = 0;
                radioId = radioGroup.getCheckedRadioButtonId();
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getHalf() * secondList.get(i).getQuantity());
                        secondList.get(i).setPrice(secondList.get(i).getHalf());
                    }
                }
                finalPrice = finalPrice / 2;
                break;
            case R.id.activity_order_radio_25:
                finalPrice = 0;
                radioId = radioGroup.getCheckedRadioButtonId();
                for (int i = 0; i < secondList.size(); i++) {
                    if (secondList.get(i).getQuantity() > 0){
                        finalPrice = finalPrice + (secondList.get(i).getSemi() * secondList.get(i).getQuantity());
                        secondList.get(i).setPrice(secondList.get(i).getSemi());
                    }
                }
                finalPrice = finalPrice * 0.25;

        }
    }

    void newOrderSend(){
        reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(getIntent().getStringExtra("year"))
        .child(getIntent().getStringExtra("month")).child(getIntent().getStringExtra("day")).child(getIntent().getStringExtra("note"));

        for (int i = 0; i < secondList.size(); i++) {
            if (secondList.get(i).getQuantity() != 0){
                reference.child("items").child("item" + i).child("name").setValue(secondList.get(i).getName());
                reference.child("items").child("item" + i).child("quantity").setValue(secondList.get(i).getQuantity());
                reference.child("items").child("item" + i).child("price").setValue(secondList.get(i).getPrice());
                reference.child("final_price").setValue(finalPrice);
                reference.child("title").setValue(getIntent().getStringExtra("title"));
                reference.child("address").setValue(getIntent().getStringExtra("address"));
                reference.child("type").setValue(radioButton.getText());
            }
        }
        Toast.makeText(getApplicationContext(), "Заказ успешно отправлен!", Toast.LENGTH_LONG).show();
        finish();
    }

    void updateReference(){
            inVisible();
            boolean find = false;
            for (int i = 0; i < secondList.size(); i++) {
                if (secondList.get(i).getQuantity() != 0 && secondList.get(i).getQuantity() > secondList.get(i).getStock()){
                    find = true;
                    break;
                }
            }
            if (find){
                Toast.makeText(getApplicationContext(), "В заказе есть пунк превышающий количество из склада!", Toast.LENGTH_LONG).show();
                visible();
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

    void sendDialog(){
        visible();
        DialogInterface.OnClickListener sendDialog = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        updateReference();
                        break;
                        case DialogInterface.BUTTON_NEGATIVE:
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setMessage("Финальная цена : " + finalPrice + "\n Осуществить заказ?").setPositiveButton("Да", sendDialog)
                .setNegativeButton("Нет", sendDialog)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_order_button_add:
                if (radioId == 0){
                    Toast.makeText(getApplicationContext(), "Не выбрана категория предоплаты!", Toast.LENGTH_LONG).show();
                }
                else {
                    inVisible();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    radioButton = findViewById(radioId);
                                    radioButton.callOnClick();
                                    sendDialog();
                                }
                            },
                            2000);
                }

        }
    }

    void visible(){
        loading.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    void inVisible(){
        loading.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
    }
}
