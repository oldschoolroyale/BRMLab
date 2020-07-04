package com.brm.uz.activities.startActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.brm.uz.BuildConfig;
import com.brm.uz.R;
import com.brm.uz.activities.MainActivity;
import com.brm.uz.helper.UpdateHelper;
import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class UpdateActivityCheck extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener {
    CircularProgressView loading;
    private ProgressDialog pDialog;
    private GifImageView gifImageView;
    private TextView mainText;

    public static final int progress_bar_type = 0;
    private static String file_url;
    private static String gifUrl = "https://firebasestorage.googleapis.com/v0/b/crm-project-e1298.appspot.com/o/giphy%20(3).gif?alt=media&token=a3455d53-fd1e-4b3f-9efb-d5120848e152";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_check);



        loading = findViewById(R.id.activity_update_check_circular_progress_bar);
        gifImageView = findViewById(R.id.activity_update_check_gif);
        mainText = findViewById(R.id.activity_update_check_text_view);

        mainText.setText(randomText(getResources().getStringArray(R.array.startText)));
        loading.setVisibility(View.VISIBLE);

        Glide.with(getApplicationContext())
                .load(gifUrl)
                .into(gifImageView);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        UpdateHelper.with(UpdateActivityCheck.this)
                                .onUpdateCheck(UpdateActivityCheck.this)
                                .check();
                    }
                },
                5000);
    }
    @Override
    public void OnUpdateCheckListener(String urlApp, String updateText, boolean check) {
        if (check){
            loading.setVisibility(View.GONE);
            file_url = urlApp;
            findViewById(R.id.activity_update_check_text_view).setVisibility(View.GONE);
            new DownloadFileFromURL().execute(file_url);

        }
        else {
            goHome();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id){
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Загружаем новую версию, ожидайте...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected  String doInBackground(String... f_url){
            int count;
            try{
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "/BRMLab.apk";
                File imageFile = new File(storageDir+fileName);
                OutputStream output = new FileOutputStream(imageFile);

                byte data[] = new byte[1024];
                long total = 0;

                while((count = input.read(data)) != -1){
                    total += count;

                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    output.write(data, 0, count);
                }
                output.flush();

                output.close();
                input.close();
            }catch (Exception e){
                Log.e("Error: ", e.getMessage());
            }

            return null;

        }

        protected void onProgressUpdate(String... progress){
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url){
            dismissDialog(progress_bar_type);
            goHome();

            File toInstall = new File("/storage/emulated/0/", "BRMLab" + ".apk");
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(UpdateActivityCheck.this, BuildConfig.APPLICATION_ID + ".fileprovider", toInstall);
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Uri apkUri = Uri.fromFile(toInstall);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        }
    }

    void goHome(){
        loading.setVisibility(View.GONE);
        findViewById(R.id.activity_update_check_text_view).setVisibility(View.GONE);
        Intent intent = new Intent(UpdateActivityCheck.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String randomText(String[] text){
        int rnd = new Random().nextInt(text.length);
        return text[rnd];
    }
}
