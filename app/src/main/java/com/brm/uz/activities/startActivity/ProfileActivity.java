package com.brm.uz.activities.startActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brm.uz.R;
import com.brm.uz.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_PICK = 1;

    DatabaseReference reference;
    String current_user, download_url;
    EditText nameEdit, surNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        download_url = "https://firebasestorage.googleapis.com/v0/b/crm-project-e1298.appspot.com/o/profile_images%2F%26d_CO%2C).jpg?alt=media&token=adef1e0d-0d7c-46d3-b1f8-905461bded22";

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);

        nameEdit = findViewById(R.id.activity_profile_name_edit);
        surNameEdit = findViewById(R.id.activity_profile_surname_edit);

        findViewById(R.id.activity_profile_circle_image).setOnClickListener(this);
        findViewById(R.id.activity_profile_button_save).setOnClickListener(this);

        reference.child("check").setValue("true");
        reference.child("token").setValue(current_user);
        reference.child("medications").setValue("null");
        reference.child("town_doctor").setValue("null");
        reference.child("town_pharmacy").setValue("null");
        reference.child("region").setValue("null");
        reference.child("manager").setValue("null");
        reference.child("bonus").setValue(0);
        reference.child("name").setValue(nameEdit.getText().toString() + " " + surNameEdit.getText().toString());
        reference.child("image").setValue(download_url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_profile_circle_image:
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1, "SELECT_IMAGE"), GALLERY_PICK);
                break;
            case R.id.activity_profile_button_save:
                if (nameEdit.getText().toString().isEmpty() && surNameEdit.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Имя и фамилия являются обьязательными пунктами", Toast.LENGTH_SHORT).show();
                }
                else {

                    reference.child("name").setValue(nameEdit.getText().toString() + " " + surNameEdit.getText().toString());
                    reference.child("image").setValue(download_url);

                    Intent intent = new Intent(ProfileActivity.this, PermissionActivity.class);
                    startActivity(intent);
                    finish();
                }
        }
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
                                download_url = uri.toString();
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
