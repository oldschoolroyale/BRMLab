package com.brm.uz.dataList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import com.brm.uz.activities.MainActivity;
import com.brm.uz.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class DoctorList extends AppCompatActivity implements AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener {

    ListView listView;
    SimpleAdapter adapter;
    CircularProgressView loading;
    EditText editText;
    ArrayList<HashMap<String, String>> list;
    String timeStampYear, timeStampMonth, timeStampDay, current_user, regionUrl, townUrl;
    TextView timeText;
    DatabaseReference reference;
    String[]  words, townArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        startElements();
        reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                townUrl = dataSnapshot.child("town_doctor").getValue().toString();
                words = dataSnapshot.child("region").getValue().toString().split(" ");
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


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void startElements() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

        timeStampYear = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
        timeStampMonth = new SimpleDateFormat("M").format(Calendar.getInstance().getTime());
        timeStampDay = new SimpleDateFormat("d").format(Calendar.getInstance().getTime());

        timeText = findViewById(R.id.list_item_date_text);
        timeText.setText("Сегодня: " + new SimpleDateFormat("dd/M/yyyy").format(Calendar.getInstance().getTime()));
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DoctorList.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });

        listView = findViewById(R.id.lv_items);
        editText = findViewById(R.id.list_item_et_text);
        loading = findViewById(R.id.doctor_list_progress_bar);
        listView.setOnItemClickListener(this);
    }

    private void getItems() {

        loading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, townUrl+ "?action="+regionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {

        list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String id = jo.getString("id");
                String itemName = jo.getString("itemName");
                String address = jo.getString("address");
                String type = jo.getString("type");
                String town = jo.getString("town");
                String region = jo.getString("region");


                HashMap<String, String> item = new HashMap<>();
                item.put("itemName", itemName);
                item.put("address", address);
                item.put("type", type);
                item.put("id", id);
                item.put("town", town);
                item.put("region", region);
                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(this, list, R.layout.doctor_item_view,
                new String[]{"itemName", "address", "type", "town", "region"}, new int[]{R.id.tv_item_name, R.id.tv_brand, R.id.tv_price, R.id.list_item_town, R.id.list_item_region});


        listView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DoctorList.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String current_user = user.getUid();

        final Integer increasingInt = new Random().nextInt();

        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        final String itemName = map.get("itemName");
        final String type = map.get("type");
        final String address = map.get("address");
        DateFormat df = new SimpleDateFormat("HH:mm"); // Format time
        final String time = df.format(Calendar.getInstance().getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child(timeStampYear).child(timeStampMonth).child(timeStampDay).child("Note" + increasingInt);

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", itemName);
        userMap.put("address", address);
        userMap.put("type", type);
        userMap.put("id", "Note" + increasingInt);
        userMap.put("time_start", "null");
        userMap.put("time_end", "null");
        userMap.put("sound", "null");
        userMap.put("lat", "null");
        userMap.put("lon", "null");
        userMap.put("visit", "Визит не окончен");
        userMap.put("add_time", time);
        userMap.put("medications", "null");
        userMap.put("comment", "null");

        reference.setValue(userMap);

        Intent a = new Intent(DoctorList.this, MainActivity.class);
        startActivity(a);
        finish();
        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Добавить в задачи?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

            timeStampYear = "" + year;
            timeStampDay = "" + dayOfMonth;
            timeStampMonth = "" + (monthOfYear + 1);


        String dateShow = "Выбрано: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        timeText.setText(dateShow);
    }

    private void categoryAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorList.this);
        builder.setTitle("Выберите район");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regionUrl = words[which];
                getItems();
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void townCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorList.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


