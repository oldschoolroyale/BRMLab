package com.example.crm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.crm_project.dataBase.DoctorList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class DoctorInformation extends AppCompatActivity implements View.OnClickListener {
    Button okButton, pickButton, categoryButton;
    boolean checkedItems[];
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ProgressDialog loading;
    String[] listItem, words;
    String medications, category, chooseString;
    LinearLayout linearLayout;
    EditText commentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_information);

        findView();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String current_user = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medications = dataSnapshot.child("medications").getValue().toString();
                words = medications.split(" ");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void categoryAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorInformation.this);
        builder.setTitle("Выберите категорию");
        builder.setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                category = words[which];
                getItems();
                linearLayout.setVisibility(View.VISIBLE);
                categoryButton.setClickable(false);
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

    private void chooseAlert(){
        checkedItems = new boolean[listItem.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DoctorInformation.this);
        mBuilder.setTitle(R.string.preparat);
        mBuilder.setMultiChoiceItems(listItem, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.add_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mUserItems.size(); i++) {
                    item = item + listItem[mUserItems.get(i)];
                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                chooseString = item;
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void findView() {
        okButton = findViewById(R.id.doctor_information_button_add);
        pickButton = findViewById(R.id.doctor_information_choose_button);
        categoryButton = findViewById(R.id.doctor_information_category_button);
        linearLayout = findViewById(R.id.doctor_information_ll2);
        commentEdit = findViewById(R.id.doctor_information_comment_edit_text);

        categoryButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        pickButton.setOnClickListener(this);
    }

    private void getItems() {

         loading = ProgressDialog.show(this, "Загрузка", "Пожалуйста подождите", false, true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwir1_uwxA5fn7MSPqvfVoAIEQpqIIIf4S5YSrWLUHnYNaH_MxZ/exec?action="+category,
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
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            listItem = new String[jarray.length()];
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

               String name = jo.getString("name");
               listItem[i] = name;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("MyLog", Arrays.toString(listItem));

        loading.dismiss();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doctor_information_choose_button:
              chooseAlert();
                break;
            case R.id.doctor_information_button_add:
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(getIntent().getStringExtra("id")).child("Date"+getIntent().getStringExtra("time")).child(getIntent().getStringExtra("note"));
                reference.child("comment").setValue(commentEdit.getText().toString());
                reference.child("medications").setValue(chooseString);
                finish();
                break;
            case R.id.doctor_information_category_button:
                categoryAlert();

        }

        }
    }

