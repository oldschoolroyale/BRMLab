package com.example.crm_project.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crm_project.DoctorInformation;
import com.example.crm_project.R;
import com.example.crm_project.dataBase.DoctorList;
import com.example.crm_project.fragments.RecyclerViewAdapter.Product;
import com.example.crm_project.fragments.RecyclerViewAdapter.ProductAdapter;
import com.example.crm_project.fragments.RecyclerViewAdapter.RecyclerViewClickInterface;
import com.example.crm_project.startActivity.UpdateHelper;
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


public class MissionFragment extends Fragment implements DatePickerDialog.OnDateSetListener, RecyclerViewClickInterface  {

    //view
    private View view;
    //final ints
    final int REQUEST_PERMISSION_CODE = 1000;
    //var
    String timeStamp, current_user, strDate, medications, managerUrl;
    Double lonDBL, latDBL;
    int bonusInt;
    //recycler
    RecyclerView recyclerView;
    ProductAdapter adapter;
    ArrayList<Product> productList;
    ProgressDialog loading;
    DatabaseReference reference, bonus;
    //other
    TextView dateText;
    LinearLayout linearLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
    String[]  words;


    public MissionFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_missions, container, false);
        timeStamp = new SimpleDateFormat("ddMyyyy").format(Calendar.getInstance().getTime());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();

        bonus = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        words = getResources().getStringArray(R.array.managerArray);

        linearLayout = view.findViewById(R.id.fragment_mission_ll1);

        dateText = view.findViewById(R.id.fragment_mission_date_text);
        dateText.setText( new SimpleDateFormat("dd.M.yyyy").format(Calendar.getInstance().getTime()));

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

        if (!checkPermissionFromDevice())
            requestPermissions();
        productList = new ArrayList<Product>();
        recyclerView = view.findViewById(R.id.fragment_missions_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));



        dataRetrieve();
        medicationsCheck();
        checkVerification();
        return view;
    }

    private void checkVerification() {
        reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("manager").getValue().toString().equals("null")){
                    categoryAlert();
                }
                else if (!dataSnapshot.child("check").getValue().toString().equals("true")){
                    verificationAlert();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void dataRetrieve() {
        loading = ProgressDialog.show(getActivity(),"Загрузка","Пожалуйста подождите",false,true);
        reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child("Date" + timeStamp);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                productList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Product p = dataSnapshot1.getValue(Product.class);
                    p.setTime(dataSnapshot1.child("add_time").getValue().toString());
                    p.setTimeStart(dataSnapshot1.child("time_start").getValue().toString());
                    p.setTimeEnd(dataSnapshot1.child("time_end").getValue().toString());
                    productList.add(p);

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

                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        bonus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bonusInt = Integer.parseInt(dataSnapshot.child("bonus").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void medicationsCheck(){
        DatabaseReference medication = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        medication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medications = dataSnapshot.child("medications").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int write_internal_storage_result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int access_fine_location = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int internet_connection = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET);
        int gps_access = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return write_internal_storage_result == PackageManager.PERMISSION_GRANTED
                && access_fine_location == PackageManager.PERMISSION_GRANTED
                && internet_connection == PackageManager.PERMISSION_GRANTED
                && gps_access == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
            {if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getActivity(), "PERMISSION GRANTED", Toast.LENGTH_LONG).show();
            else Toast.makeText(getActivity(), "PERMISSION DENIED", Toast.LENGTH_LONG).show();
            } break;

        }
    }

    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){

                    lonDBL = location.getLongitude();
                    latDBL = location.getLatitude();
                }

            }
        });
    }

    public void getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        strDate = mdformat.format(calendar.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        timeStamp = "" + dayOfMonth + (monthOfYear + 1) + year;
        String dateShow = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        dateText.setText(dateShow);
        dataRetrieve();
    }

    @Override
    public void onDeleteClick(int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child("Date" + timeStamp).child(productList.get(position).getId());
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

    @Override
    public void onPlayClick(int position) {
        if (checkPermissionFromDevice()){
            if (timeStamp.equals(new SimpleDateFormat("ddMyyyy").format(Calendar.getInstance().getTime()))){
                DialogInterface.OnClickListener playClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                fusedLocationProviderClient = new FusedLocationProviderClient(getActivity());
                                getLocation();
                                getCurrentTime();

                                final String id = productList.get(position).getId();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child("Date" + timeStamp).child(id);
                                reference.child("time_start").setValue(strDate);
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
                if (!getActivity().isFinishing()){
                    builder.show();
                }
            }
            else {Toast.makeText(getActivity(), "Визит можно выполнить только в запланированный день", Toast.LENGTH_LONG).show();}

        }
        else {
            requestPermissions();
        }
    }

    @Override
    public void onStopClick(int position) {
        final String id = productList.get(position).getId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(current_user).child("Date" + timeStamp).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("time_start").getValue().toString().equals("null")){
                    DialogInterface.OnClickListener stopClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //mediaRecorder.stop();
                                    getCurrentTime();

                                    reference.child("lon").setValue(lonDBL);
                                    reference.child("lat").setValue(latDBL);
                                    reference.child("time_end").setValue(strDate);
                                    reference.child("visit").setValue("Визит окончен");
                                    if (productList.get(position).getType().equals("Визит к врачу")){
                                        if (!medications.equals("null")){
                                            if (productList.get(position).getVisit().equals("Визит не окончен")){
                                                Intent intent = new Intent(getActivity(), DoctorInformation.class);
                                                intent.putExtra("id", current_user);
                                                intent.putExtra("note", productList.get(position).getId());
                                                intent.putExtra("time", timeStamp);
                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(getActivity(), "Изменения после визита ввести нельзя", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else {Toast.makeText(getActivity(), "Администратор еще не выдал вам категорию", Toast.LENGTH_LONG).show();}


                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Окончить визит?").setPositiveButton("Да", stopClickListener).setNegativeButton("Нет", stopClickListener);
                    if (!getActivity().isFinishing()){
                        builder.show();
                    }
                }
                else {Toast.makeText(getActivity(), "Этот визит еще не начат!", Toast.LENGTH_LONG).show();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bonusInt = bonusInt + 1;
        bonus.child("bonus").setValue(bonusInt);
    }

    private void categoryAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите администратора");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                managerUrl = words[which];
                if (managerUrl.equals("Ботиров Жамшид")){
                    reference.child("manager").setValue("vMjkEZ1FIBRhPcOYQUQ1wj4yzE92");
                }
                if (managerUrl.equals("Салих Касимович")){
                    reference.child("manager").setValue("R4DcAQ34BSgJ9K8AJ34ncZ0jlo82");
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void verificationAlert(){
        final DialogInterface.OnClickListener dialogVerification = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        checkVerification();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.verification).setPositiveButton("Обновить", dialogVerification).setCancelable(false).show();
    }

}

