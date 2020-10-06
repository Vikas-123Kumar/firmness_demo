package com.example.infyULabs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.setting.SettingOfApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class RiceData extends AppCompatActivity  {
    Button senddata;
    TextView textView, textView1;
    ImageView imageView;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 10;
    private Bitmap bitmap;
    JSONObject jsonObject;
    PieChartView pieChartView;
    RequestQueue rQueue;
    private String filePath;
    String url = "http://10.6.9.106:5000/api/post_some_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_data);
        senddata = (Button) findViewById(R.id.buttonUploadImage);
        textView = (TextView) findViewById(R.id.textview);
        pieChartView = (PieChartView) findViewById(R.id.piechart);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView1 = (TextView) findViewById(R.id.text2);
        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(RiceData.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(RiceData.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(RiceData.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });
        setupToolbar();
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        ActionBar action_Bar = getSupportActionBar();
        if (action_Bar != null) {
            action_Bar.setDisplayHomeAsUpEnabled(true);
            action_Bar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            if (filePath != null) {
                try {

                    textView.setText("File Selected");
                    Log.e("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    //  uploadBitmap(bitmap);
                    uploadImage(bitmap);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(
                        RiceData.this, "no image selected",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadImage(final Bitmap bitmap) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                try {
                    jsonObject = new JSONObject();
                    long imagename = System.currentTimeMillis();
                    jsonObject.put("name", imagename);
                    jsonObject.put("image", encodedImage);
                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.e("image", jsonObject.toString());
                                rQueue.getCache().clear();
                                Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                //  textView.setText(" Response : "+ jsonObject);
                                String str = jsonObject.toString();
                                List pieData = new ArrayList<>();
                                try {
                                    pieData.add(new SliceValue(Integer.parseInt(jsonObject.getString("medium")), Color.BLUE).setLabel("Medium = " + jsonObject.getString("medium")));
                                    Log.e("value of count", Integer.parseInt(jsonObject.getString("count")) + "");

                                    pieData.add(new SliceValue(Integer.parseInt(jsonObject.getString("bold")), Color.GRAY).setLabel("Bold = " + jsonObject.getString("bold")));
                                    pieData.add(new SliceValue(Integer.parseInt(jsonObject.getString("slender")), Color.RED).setLabel("Slender = \n" + jsonObject.getString("slender")));
                                    pieData.add(new SliceValue(Integer.parseInt(jsonObject.getString("round")), Color.MAGENTA).setLabel("Round = " + jsonObject.getString("round")));

                                    PieChartData pieChartData = new PieChartData(pieData);
                                    pieChartData.setHasLabels(true).setValueLabelTextSize(14);
                                    pieChartData.setHasCenterCircle(true).setCenterText1("Total: \n" + (jsonObject.getString("count"))).setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
                                    pieChartView.setPieChartData(pieChartData);
                                    textView1.setText("Quality:" + jsonObject.getString("quality"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("aaaaaaa", volleyError.toString());

                    }
                });

                rQueue = Volley.newRequestQueue(RiceData.this);
                rQueue.add(jsonObjectRequest);

            }
        });

    }

}
