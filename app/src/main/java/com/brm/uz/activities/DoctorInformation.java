package com.brm.uz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brm.uz.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
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

public class DoctorInformation extends AppCompatActivity implements View.OnClickListener {
    private Button okButton, pickButton, categoryButton;
    boolean checkedItems[];
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private CircularProgressView loading;
    private String[] listItem, words;
    private String medications, category, chooseString;
    private LinearLayout linearLayout, linearLayout1;
    private EditText commentEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_information);

        findView();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String current_user = user.getUid();

        chooseString = "null";

        DatabaseReference medReference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        medReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void findView() {
        okButton = findViewById(R.id.doctor_information_button_add);
        pickButton = findViewById(R.id.doctor_information_choose_button);
        categoryButton = findViewById(R.id.doctor_information_category_button);
        linearLayout = findViewById(R.id.doctor_information_ll2);
        commentEdit = findViewById(R.id.doctor_information_comment_edit_text);
        loading = findViewById(R.id.doctor_information_progress_bar);
        linearLayout1 = findViewById(R.id.activity_doctor_information_ll1);

        categoryButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        pickButton.setOnClickListener(this);
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

    private void getItems() {
        ArrayList<String> arrayList = new ArrayList<>();
         loading.setVisibility(View.VISIBLE);
         linearLayout1.setVisibility(View.GONE);
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Medications").child(category);
         reference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                     arrayList.add(dataSnapshot1.child("name").getValue().toString());
                 }
                 listItem = new String[arrayList.size()];
                 for (int i = 0; i < arrayList.size(); i++) {
                     listItem[i] = arrayList.get(i);
                 }
                 loading.setVisibility(View.GONE);
                 linearLayout1.setVisibility(View.VISIBLE);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doctor_information_choose_button:
              chooseAlert();
                break;
            case R.id.doctor_information_button_add:
               DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes")
                       .child(getIntent().getStringExtra("id")).child(getIntent().getStringExtra("year"))
                       .child(getIntent().getStringExtra("month")).child(getIntent().getStringExtra("day"))
                       .child(getIntent().getStringExtra("note"));
                reference.child("comment").setValue(commentEdit.getText().toString());
                reference.child("medications").setValue(chooseString);
                finish();
                break;
            case R.id.doctor_information_category_button:
                categoryAlert();

        }

        }
    }

