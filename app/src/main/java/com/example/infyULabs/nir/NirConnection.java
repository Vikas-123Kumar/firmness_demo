package com.example.infyULabs.nir;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.infyULabs.R;
import com.example.infyULabs.SplashScreen;
import com.example.infyULabs.dataAnalysis.MainActivity;
import com.example.infyULabs.setting.SettingsActivity;

public class NirConnection extends AppCompatActivity implements View.OnClickListener {
    private Button nirConnection, nirScan;
    Context context = this;
    String intent_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nir_connection);
        context = this;
        initView();
        setupToolbar();
        intent_data = getIntent().getStringExtra("item");
    }

    public void initView() {
        nirConnection = findViewById(R.id.nirConnection);
        nirScan = findViewById(R.id.nirScan);
        nirConnection.setOnClickListener(this);
        nirScan.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nirConnection) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        if (v.getId() == R.id.nirScan) {
            Intent graphIntent = new Intent(getApplicationContext(), NewScanActivity.class);
            graphIntent.putExtra("fruit_name", intent_data);
            Log.e("scan button", intent_data);
            startActivity(graphIntent);

        }
    }
}
