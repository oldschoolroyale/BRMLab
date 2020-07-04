package com.brm.uz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.brm.uz.adapter.OrderAdapter;
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
    private PharmacyOrdersAdapter adapter;

    private String[] words;
    private DatabaseReference reference;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_orders);

        startElements();
        cursor();
    }

    void startElements(){
        loading = findViewById(R.id.pharmacy_orders_progress_bar);
        loading.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.pharmacy_orders_medications_recycler_view);

        mainList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

        words = getIntent().getStringExtra("start").split(" ");

        findViewById(R.id.pharmacy_orders_button_add).setOnClickListener(this);
        findViewById(R.id.pharmacy_orders_button_new_order).setOnClickListener(this);
    }

    void cursor(){
        for (int i = 0; i < words.length; i++) {
                getItems(words[i]);
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


    private void informationSend(){
        reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user)
                .child(getIntent().getStringExtra("year"))
                .child(getIntent().getStringExtra("month")).child(getIntent().getStringExtra("day"))
                .child(getIntent().getStringExtra("id"));
            for (int i = 0; i < mainList.size(); i++) {
                if (mainList.get(i).getQuantity() != 0){
                    reference.child("stock").child("item" + i).child("name").setValue(mainList.get(i).getName());
                    reference.child("stock").child("item" + i).child("quantity").setValue(mainList.get(i).getQuantity());
                }
            }
        }

    @Override
    public void onClick(View v) {
       switch (v.getId()){

           case R.id.pharmacy_orders_button_add:
                   informationSend();
                   finish();
               break;

           case R.id.pharmacy_orders_button_new_order:
               informationSend();
               Intent intent = new Intent(PharmacyActivity.this, OrderActivity.class);
               intent.putExtra("start", getIntent().getStringExtra("start"));
               intent.putExtra("note", getIntent().getStringExtra("note"));
               intent.putExtra("day", getIntent().getStringExtra("day"));
               intent.putExtra("month", getIntent().getStringExtra("month"));
               intent.putExtra("year", getIntent().getStringExtra("year"));
               intent.putExtra("title", getIntent().getStringExtra("title"));
               intent.putExtra("address", getIntent().getStringExtra("address"));
               startActivity(intent);
               finish();
               break;
       }
    }


}
