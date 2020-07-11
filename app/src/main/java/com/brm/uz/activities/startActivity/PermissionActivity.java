package com.brm.uz.activities.startActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.brm.uz.R;
import com.brm.uz.activities.MainActivity;

public class PermissionActivity extends AppCompatActivity {


    private final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        findViewById(R.id.activity_permission_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

            requestPermissions();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int write_internal_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int access_fine_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int internet_connection = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int gps_access = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
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
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
            } break;

        }
    }

    private void goHome(){
        if (checkPermissionFromDevice()){
            Intent sentHome = new Intent(PermissionActivity.this, UpdateActivityCheck.class);
            sentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            sentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(sentHome);
            finish();
        }
        else {
            requestPermissions();
        }
    }
}
