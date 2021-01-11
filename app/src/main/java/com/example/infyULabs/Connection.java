package com.example.infyULabs;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.infyULabs.dataAnalysis.MainActivity;
import com.example.infyULabs.nir.NirConnection;
import com.example.infyULabs.setting.SettingOfApp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Connection extends AppCompatActivity {
    private SwitchCompat uv_vis, nir, triad;
    private Context context = this;
    String intent_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        uv_vis = (SwitchCompat) findViewById(R.id.uv_vis);
        nir = (SwitchCompat) findViewById(R.id.nir);
        triad = (SwitchCompat) findViewById(R.id.triad_switch);
        intent_data = getIntent().getStringExtra("item");
        final GlobalClass globalClass = (GlobalClass) getApplicationContext();
        uv_vis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("item", intent_data);
                    GlobalClass.showToast("uv nano selected");
                    startActivity(intent);
                }
            }
        });
        nir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nir.isChecked()) {
                    startActivity(new Intent(getApplicationContext(), NirConnection.class).putExtra("item", intent_data));
                    GlobalClass.showToast("nir nano selected");
                }
            }
        });
        triad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (triad.isChecked()) {
                    startActivity(new Intent(getApplicationContext(), TriadSpectrometer.class).putExtra("item", intent_data));
                    GlobalClass.showToast("TriadSpectrometer nano selected");
                }
            }
        });
        setupToolbar();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        uv_vis.setChecked(false);
        nir.setChecked(false);
        triad.setChecked(false);
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
}