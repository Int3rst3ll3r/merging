package com.example.merging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.widget.ImageView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.text.Html;
import android.widget.TextView;

public class secondpage extends AppCompatActivity {
    ImageView imageView;
    Button button;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    int REQUEST_CODE = 12345;
    boolean isPermissionGranted =false;
    //Initialize variable
    Button btSelect;
    TextView tvUri,tvPath;
    ActivityResultLauncher<Intent> resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondpage);
        imageView =findViewById(R.id.imagePick);
        button =findViewById(R.id.btnPick);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        //Assign variable
        btSelect=findViewById(R.id.bt_select);
        tvUri = findViewById(R.id.tv_uri);
        tvPath = findViewById(R.id.tv_path);

        //Initialize result launcher
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //Initialize result data
                        Intent data = result.getData();
                        //check condition
                        if (data !=null){
                            //when data is not equal to empty
                            //Get PDF uri
                            Uri sUri = data.getData();
                            //Set uri on text view
                            tvUri.setText(Html.fromHtml(
                                    "<big><b>PDF Uri</b></big><br>" + sUri
                            ));
                            //Get PDF Path
                            String sPath = sUri.getPath();
                            //Set path on text view
                            tvPath.setText(Html.fromHtml(
                                    "<big><b>PDF Path</b></big><br>" + sPath
                            ));
                        }
                    }
                }

        );

        //Set click listener on button
        btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check condition
                if (ActivityCompat.checkSelfPermission(secondpage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //When permission is not granted
                    //Request permission
                    ActivityCompat.requestPermissions(secondpage.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }else{
                    //When permission is granted
                    //Create method
                    selectPDF();
                }
            }
        });

    }
    private void selectPDF(){
        //Initialize intent
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        //Set type
        intent.setType("application/pdf");
        //Launch intent
        resultLauncher.launch(intent);

    }
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            isPermissionGranted=true;
            Intent intent= new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Complete Action using"),REQUEST_CODE);

        }
        else{
            ActivityCompat.requestPermissions(secondpage.this,permissions,REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
        else{
            Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    }
}