package com.brm.uz.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.activities.PharmacyActivity;
import com.brm.uz.activities.DoctorInformation;
import com.brm.uz.R;
import com.brm.uz.models.ProductPOJO;
import com.brm.uz.adapter.ProductAdapter;
import com.brm.uz.view.RecyclerViewClickInterface;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;


public class MissionFragment extends Fragment implements DatePickerDialog.OnDateSetListener, RecyclerViewClickInterface  {


    private String timeCheck, timeStampYear, timeStampMonth, timeStampDay, current_user, strDate, medicationsIntent, orderTypeText, orderPriceText;
    private Double lonDBL, latDBL;
    private int bonusInt;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ArrayList<ProductPOJO> productList;
    private CircularProgressView loading;
    private DatabaseReference reference, bonus;

    private TextView dateText;
    private LinearLayout linearLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions, container, false);

        timeStampYear = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
        timeStampMonth = new SimpleDateFormat("M").format(Calendar.getInstance().getTime());
        timeStampDay = new SimpleDateFormat("d").format(Calendar.getInstance().getTime());
        timeCheck = timeStampDay+timeStampMonth+timeStampYear;

        loading = view.findViewById(R.id.mission_progress_bar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

        bonus = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        bonusInt = 0;

        linearLayout = view.findViewById(R.id.fragment_mission_ll1);

        dateText = view.findViewById(R.id.fragment_mission_date_text);
        dateText.setText( new SimpleDateFormat("d.M.yyyy").format(Calendar.getInstance().getTime()));

       ImageView imageView = view.findViewById(R.id.fragment_mission_calendar);
       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Calendar now = Calendar.getInstance();
               DatePickerDialog dpd = DatePickerDialog.newInstance(
                       MissionFragment.this,
                       now.get(Calendar.YEAR), // Initial year selection
                       now.get(Calendar.MONTH), // Initial month selection
                       now.get(Calendar.DAY_OF_MONTH) // Inital day selection
               );
               dpd.show(getFragmentManager(), "Datepickerdialog");
           }
       });

        productList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.fragment_missions_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));


        dataRetrieve();
        return view;
    }

    private void dataRetrieve() {
        loading.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child(timeStampYear).child(timeStampMonth).child(timeStampDay);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                productList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    ProductPOJO p = dataSnapshot1.getValue(ProductPOJO.class);
                    p.setTime(dataSnapshot1.child("add_time").getValue().toString());
                    p.setTimeStart(dataSnapshot1.child("time_start").getValue().toString());
                    p.setTimeEnd(dataSnapshot1.child("time_end").getValue().toString());
                    productList.add(p);
                    Collections.sort(productList, ProductPOJO.visitTime);

                }
                adapter = new ProductAdapter(getActivity(), productList, MissionFragment.this);
                if (adapter.getItemCount() == 0){
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    linearLayout.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        bonus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String test = "" + dataSnapshot.child("bonus").getValue();
                if (!test.equals("null"))
                {
                    bonusInt = Integer.parseInt("" + dataSnapshot.child("bonus").getValue());
                }

                medicationsIntent = "" + dataSnapshot.child("medications").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getLocationAndTime() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    lonDBL = location.getLongitude();
                    latDBL = location.getLatitude();
                }

            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        strDate = mdformat.format(calendar.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        timeStampYear = "" + year;
        timeStampDay = "" + dayOfMonth;
        timeStampMonth = "" + (monthOfYear + 1);
        timeCheck = timeStampDay+timeStampMonth+timeStampYear;

        String dateShow = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        dateText.setText(dateShow);
        dataRetrieve();
    }

    @Override
    public void onDeleteClick(int position) {
        if (timeCheck.equals(new SimpleDateFormat("dMyyyy").format(Calendar.getInstance().getTime()))) {
            if (productList.get(position).getVisit().equals("Визит окончен")) {
                extraInformation(position);
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).
                                        child(timeStampYear).child(timeStampMonth).child(timeStampDay).child(productList.get(position).getId());
                                reference.removeValue();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.delete_note).setPositiveButton("Да", dialogClickListener)
                        .setNegativeButton("Нет", dialogClickListener).show();
            }
        }
        else {
            Toast.makeText(getActivity(), "Изменения возможно осуществить только в назначенный день!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (timeCheck.equals(new SimpleDateFormat("dMyyyy").format(Calendar.getInstance().getTime()))){
            if (productList.get(position).getTimeStart().equals("null") && productList.get(position).getTimeStart().equals("null")){
                doStart(position);
            }
            if (!productList.get(position).getTimeStart().equals("null") && productList.get(position).getTimeEnd().equals("null")){
                doStop(position);
            }
            if (productList.get(position).getVisit().equals("Визит окончен")){
                loading.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (productList.get(position).getType().equals("Визит к врачу")){
                            commentDialog(position,  true);
                        }
                        if (productList.get(position).getType().equals("Визит в аптеку")){
                            getOrderText(position);
                        }
                    }
                }, 1500);

            }
        }
        else {
            Toast.makeText(getActivity(), "Визит возможно осуществить только в назначенный день!", Toast.LENGTH_SHORT).show();
        }
    }

    private void doStart(int position){
        DialogInterface.OnClickListener playClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        fusedLocationProviderClient = new FusedLocationProviderClient(getActivity());
                        getLocationAndTime();
                        productList.get(position).setTimeStart(strDate);
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_dialog_start_message).setPositiveButton("Да", playClickListener)
                .setNegativeButton("Нет", playClickListener);
        builder.show();
    }

    private void doStop(int position){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child(timeStampYear).child(timeStampMonth).child(timeStampDay).child(productList.get(position).getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DialogInterface.OnClickListener stopClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                getLocationAndTime();
                                reference.child("lon").setValue(lonDBL);
                                reference.child("lat").setValue(latDBL);
                                reference.child("time_start").setValue(productList.get(position).getTimeStart());
                                reference.child("time_end").setValue(strDate);
                                reference.child("visit").setValue("Визит окончен");
                                extraInformation(position);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Окончить визит?").setPositiveButton("Да", stopClickListener).setNegativeButton("Нет", stopClickListener);
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bonusInt = bonusInt + 1;
        bonus.child("bonus").setValue(bonusInt);
    }

    private void commentDialog(int position,  boolean check){
        View view;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        if (check) {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.nav_alert_doctor, null);
            ListView simpleList = view.findViewById(R.id.nav_alert_comment_medications_list_view);
            TextView commentText = view.findViewById(R.id.nav_alert_comment_text_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, productList.get(position).getMedications().split(", "));
            commentText.setText(productList.get(position).getComment());
            simpleList.setAdapter(adapter);
        }
        else {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.nav_alert_pharmacy, null);
            ListView simpleList = view.findViewById(R.id.nav_alert_pharmacy_list_view);
            ListView orderList = view.findViewById(R.id.nav_alert_pharmacy_order_list_view);
            TextView typeText = view.findViewById(R.id.nav_alert_pharmacy_type);
            TextView finalPriceText = view.findViewById(R.id.nav_alert_pharmacy_final_price);

            typeText.setText(orderTypeText);
            finalPriceText.setText(orderPriceText);

            SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), getPharmacy(position),
                    R.layout.item_stock, new String[]{"name", "quantity"}, new int[]{R.id.item_stock_text_view_1, R.id.item_stock_text_view_2});
            simpleList.setAdapter(simpleAdapter);
            SimpleAdapter orderAdapter = new SimpleAdapter(getActivity(), getOrder(position), R.layout.item_order, new String[]{"name", "quantity", "price"},
                    new int[]{R.id.item_order_text_view1, R.id.item_order_text_view2, R.id.item_order_text_view3});
            orderList.setAdapter(orderAdapter);
        }
        builder
                .setView(view)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(true);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        recyclerView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    private void extraInformation(int position) {
        if (!medicationsIntent.equals("null")){
            if (productList.get(position).getType().equals("Визит к врачу")){
                Intent intent = new Intent(getActivity(), DoctorInformation.class);
                intent.putExtra("id", current_user);
                intent.putExtra("note", productList.get(position).getId());
                intent.putExtra("day", timeStampDay);
                intent.putExtra("month", timeStampMonth);
                intent.putExtra("year", timeStampYear);
                startActivity(intent);
            }
            if (productList.get(position).getType().equals("Визит в аптеку")){
                Intent intent = new Intent(getActivity(), PharmacyActivity.class);
                intent.putExtra("note", productList.get(position).getId());
                intent.putExtra("id", productList.get(position).getId());
                intent.putExtra("start", medicationsIntent);
                intent.putExtra("day", timeStampDay);
                intent.putExtra("month", timeStampMonth);
                intent.putExtra("year", timeStampYear);
                intent.putExtra("title", productList.get(position).getName());
                intent.putExtra("address", productList.get(position).getAddress());
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(getActivity(), "Администратор Вам не выдал категорию", Toast.LENGTH_SHORT).show();
        }


    }

    private ArrayList<HashMap<String, String>> getPharmacy(int position){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user)
                .child(timeStampYear).child(timeStampMonth).child(timeStampDay).child(productList.get(position).getId()).child("stock");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> map;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    map = new HashMap<>();
                    map.put("name", "" + dataSnapshot1.child("name").getValue());
                    map.put("quantity", "Количество : " + dataSnapshot1.child("quantity").getValue());
                    arrayList.add(map);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return arrayList;
    }

    private ArrayList<HashMap<String, String>> getOrder(int position){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(timeStampYear)
                .child(timeStampMonth).child(timeStampDay).child(productList.get(position).getId()).child("items");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> map;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    map = new HashMap<>();
                    map.put("name", "" + dataSnapshot1.child("name").getValue());
                    map.put("quantity", "Количество : " + dataSnapshot1.child("quantity").getValue());
                    map.put("price", "Цена препарата : " + dataSnapshot1.child("price").getValue());
                    arrayList.add(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return arrayList;
    }

    private void getOrderText(int position){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(timeStampYear)
                .child(timeStampMonth).child(timeStampDay).child(productList.get(position).getId());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 orderTypeText = "" + dataSnapshot.child("type").getValue();
                 orderPriceText = "" + dataSnapshot.child("final_price").getValue();
                 commentDialog(position,  false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

