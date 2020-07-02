package com.brm.uz.activities.addActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brm.uz.models.MapSearch;
import com.brm.uz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DoctorAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner  categorySpinner, statusSpinner;
    EditText  addressEdit, nameEdit, phoneEdit, productEdit, specialityEdit, doctorTypeEdit;
    String  categoryText, statusText, userName, current_user, mapString,  townUrl, regionUrl;
    Button btnSaveTask, btnCancel;
    DatabaseReference reference;
    String[]  words, townArray;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add);

        findView();

        reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                townUrl = dataSnapshot.child("town_doctor").getValue().toString();
                words = dataSnapshot.child("region").getValue().toString().split(" ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mapString = "Пусто";
        //townRegionCheck


        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categoryOfMission, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.statusOfMission, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        categorySpinner.setOnItemSelectedListener(this);
        statusSpinner.setOnItemSelectedListener(this);


        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert data to database
                if (addressEdit.length() == 0){
                    addressEdit.setError("Обязательное поле");
                }
                else if (nameEdit.length() == 0){
                    nameEdit.setError("Обязательное поле");
                }
                else {
                    if (townUrl.equals("Ташкентская-Сырдарьинская")){
                        townArray = getResources().getStringArray(R.array.tashSirdarya);
                        townCategory();
                    }
                    else if (townUrl.equals("Навои-Бухара")){
                        townArray = getResources().getStringArray(R.array.navoyiBuhoro);
                        townCategory();
                    }
                    else {
                        categoryAlert();
                    }

                }


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.activity_doctor_btnMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorAdd.this, MapSearch.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){return;}
        mapString = "https://www.google.com/maps/?q=" + data.getStringExtra("lat")+","+data.getStringExtra("lon");
    }

    private void findView(){
        categorySpinner = findViewById(R.id.activity_new_task_category);
        statusSpinner = findViewById(R.id.activity_new_task_status);

        addressEdit = findViewById(R.id.activity_new_task_descdoes);
        nameEdit = findViewById(R.id.activity_new_task_datedoes);
        phoneEdit = findViewById(R.id.activity_new_task_phone);
        productEdit = findViewById(R.id.activity_new_task_product);
        specialityEdit = findViewById(R.id.activity_new_task_specialty);
        doctorTypeEdit = findViewById(R.id.activity_new_task_doctor_type);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

        btnSaveTask = findViewById(R.id.activity_new_task_btnSaveTask);
        btnCancel = findViewById(R.id.activity_new_task_btnCancel);
    }

    private void addItemToSheet() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, townUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Врач добавлен", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
                    }
                }
                )
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("action", regionUrl);

                params.put("itemName", nameEdit.getText().toString().trim());
                params.put("address", addressEdit.getText().toString().trim());
                params.put("phone", phoneEdit.getText().toString().trim());
                params.put("product", productEdit.getText().toString().trim());
                params.put("specialty", specialityEdit.getText().toString().trim());
                params.put("position", doctorTypeEdit.getText().toString().trim());
                params.put("userName", userName);
                params.put("map", mapString);

                params.put("type", "Визит к врачу");
                params.put("category", categoryText);
                params.put("status", statusText);
                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.activity_new_task_category:
                categoryText = parent.getItemAtPosition(position).toString();
                break;
            case R.id.activity_new_task_status:
                statusText = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void townCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorAdd.this);
        builder.setTitle("Выберите область");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(townArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String town = townArray[which];
                if (town.equals("Ташкентская область")){
                    townUrl = "https://script.google.com/macros/s/AKfycbz7i3CoqSSiJwC-49Ul19JJmbihWWASUcg6M3p5M71vg80cBlU/exec";
                    categoryAlert();
                }
                if (town.equals("Навоинская область")){
                    townUrl = "https://script.google.com/macros/s/AKfycbw9Sx8U91AnNYLHQ3g1zzjmkpF-LOiJcFe6fcg7BZxnGZZWf-I/exec";
                    categoryAlert();
                }
                if (town.equals("Бухарская область")){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            words = dataSnapshot.child("region2").getValue().toString().split(" ");
                            categoryAlert();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    townUrl = "https://script.google.com/macros/s/AKfycbwzEBxBhrxTVQ21Qq46upQTfmHYne9OZvP5WXUbwVDaApPofNg/exec";

                }
                if (town.equals("Сырдаринская область")){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            words = dataSnapshot.child("region2").getValue().toString().split(" ");
                            categoryAlert();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    townUrl = "https://script.google.com/macros/s/AKfycbw0lUC898Q1zFlk1DJad5ttYL7SP-3mFS7NrH4oGXavmH6mYXk/exec";

                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void categoryAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorAdd.this);
        builder.setTitle("Выберите район");
        builder.setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regionUrl = words[which];
                addItemToSheet();
                finish();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
