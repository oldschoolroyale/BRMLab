package com.brm.uz.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.brm.uz.R;
import com.brm.uz.activities.addActivities.DoctorAdd;
import com.brm.uz.activities.addActivities.PharmacyAdd;
import com.brm.uz.activities.startActivity.UpdateHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int GALLERY_PICK = 1;
    DatabaseReference reference;
    String town, region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.activity_main_fragment);
        NavigationUI .setupWithNavController(navigationView, navController);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String current_user = user.getUid();
        DatabaseReference version = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        version.child("version").setValue("1.35");
        reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);

        findViewById(R.id.app_bar_menu_button).setOnClickListener(this);
        findViewById(R.id.doctor_fab_btn).setOnClickListener(this);
        findViewById(R.id.pharmacy_fab_btn).setOnClickListener(this);


        updateNav();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_bar_menu_button:
                final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.doctor_fab_btn:
                if (!town.equals("null") && !region.equals("null")){
                    Intent intentDoctor = new Intent(this, DoctorAdd.class);
                    startActivity(intentDoctor);
                }
                else {Toast.makeText(getApplicationContext(), "Администратор пока не назначал вам область работы", Toast.LENGTH_LONG).show();}
                break;
            case R.id.pharmacy_fab_btn:
                if (!town.equals("null") && !region.equals("null")) {
                    Intent intentPharmacy = new Intent(this, PharmacyAdd.class);
                    startActivity(intentPharmacy);
                }
                else {Toast.makeText(getApplicationContext(), "Администратор пока не назначал вам область работы", Toast.LENGTH_LONG).show();}
                break;

        }
    }

    public void updateNav(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        final TextView textView = headerView.findViewById(R.id.nav_header_name);
        final CircleImageView circleImageView = headerView.findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1, "SELECT_IMAGE"), GALLERY_PICK);
            }
        });


         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 town = dataSnapshot.child("town_doctor").getValue().toString();
                 region = dataSnapshot.child("region").getValue().toString();
                     textView.setText(dataSnapshot.child("name").getValue().toString());
                     if (!dataSnapshot.child("image").getValue().toString().equals("default"))
                     {
                         Picasso.with(MainActivity.this).load(dataSnapshot.child("image").getValue().toString()).into(circleImageView);
                     }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

         headerView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 alertDialog();
             }
         });
     }

     public void alertDialog(){
         AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.nav_alert_dialog, null);
          final EditText editText = view.findViewById(R.id.nav_alert_dialog_name_edit_text);

         builder.setMessage("Профиль")
                 .setView(view)
                 .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         String name = editText.getText().toString();
                         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                         String userUid = user.getUid();
                         DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Account").child(userUid);
                         reference.child("name").setValue(name);
                     }
                 }).setNegativeButton("Отменить", null)
                 .setCancelable(false);
         AlertDialog dialog = builder.create();
         dialog.show();

     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
                Uri imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(this);
            }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(random() +".jpg");
                storageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_url = uri.toString();
                                reference.child("image").setValue(download_url);
                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(15);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }




}
