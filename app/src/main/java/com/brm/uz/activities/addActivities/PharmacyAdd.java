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
import com.brm.uz.helper.MapSearch;
import com.brm.uz.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PharmacyAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Spinner  statusSpinner;
    EditText  nameEdit, ownerEdit, employeeEdit, phoneEdit, addonsEdit, addressEdit;
    String   statusText, current_user, userName, mapString, townUrl, regionUrl;
    Button btnSaveTask, btnCancel, btnMap;
    DatabaseReference reference;
    String[]  words, townArray;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_add);

        findView();
        reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                words = dataSnapshot.child("region").getValue().toString().split(" ");
                townUrl = dataSnapshot.child("town_pharmacy").getValue().toString();
                regionUrl = dataSnapshot.child("region").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mapString = "Пусто";

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.statusOfMission, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        statusSpinner.setOnItemSelectedListener(this);


        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert data to database
                if (nameEdit.length() == 0){
                    nameEdit.setError("Обязательное поле");
                }
                else if (phoneEdit.length() == 0){
                    phoneEdit.setError("Обязательное поле");
                }
                else if (employeeEdit.length() == 0){
                    employeeEdit.setError("Обязательное поле");
                }
                else if (addressEdit.length() == 0){
                    addressEdit.setError("Обязательное поле");
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
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyAdd.this, MapSearch.class);
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

        statusSpinner = findViewById(R.id.activity_pharmacy_status);

        nameEdit = findViewById(R.id.activity_pharmacy_name);
        ownerEdit = findViewById(R.id.activity_pharmacy_owner_name);
        phoneEdit = findViewById(R.id.activity_pharmacy_phone);
        employeeEdit = findViewById(R.id.activity_pharmacy_employees_name);
        addonsEdit = findViewById(R.id.activity_pharmacy_addons);
        addressEdit = findViewById(R.id.activity_pharmacy_address);

        btnSaveTask = findViewById(R.id.activity_pharmacy_btnSaveTask);
        btnCancel = findViewById(R.id.activity_pharmacy_btnCancel);
        btnMap = findViewById(R.id.activity_pharmacy_btnMap);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

    }

    private void addItemToSheet() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, townUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Аптека добавлена", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("action", regionUrl);

                params.put("name", nameEdit.getText().toString().trim());
                params.put("address", addressEdit.getText().toString().trim());
                params.put("owner", ownerEdit.getText().toString().trim());
                params.put("employee", employeeEdit.getText().toString().trim());
                params.put("phone", phoneEdit.getText().toString().trim());
                params.put("addons", addonsEdit.getText().toString().trim());
                params.put("map", mapString);
                params.put("userName", userName);

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
            case R.id.activity_pharmacy_status:
                statusText = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void categoryAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PharmacyAdd.this);
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

    private void townCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PharmacyAdd.this);
        builder.setTitle("Выберите область");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(townArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String town = townArray[which];
                if (town.equals("Ташкентская область")){
                    townUrl = "https://script.google.com/macros/s/AKfycbynNT5CbnJ5RZtqzzI4oKPUT5pOnRjX8BdAFCcB-stGaXsl3t3v/exec";
                    categoryAlert();
                }
                if (town.equals("Навоинская область")){
                    townUrl = "https://script.google.com/macros/s/AKfycbzou4oz1yOd7jMPrdi_v_dYN46Uc-lXDnPfKCGPbd3_g9CaHVti/exec";
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
                    townUrl = "https://script.google.com/macros/s/AKfycbzZlIgT1k6k3kxfXPOg0KdzMfh24dc8pIalXmOi8sdinbn-MWAl/exec";

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
                    townUrl = "https://script.google.com/macros/s/AKfycbyJbc-gskLz5_9CzwoNAVexD3OPyKDGI9IGe_EUI3wPJRWlTm0/exec";

                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
