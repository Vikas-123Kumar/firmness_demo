package com.example.infyULabs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.infyULabs.bean.FruitName;
import com.example.infyULabs.bean.WareHouseBean;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class second extends AppCompatActivity {
    Context context;
    ListView wareHouse;
    FloatingActionButton addWareHouse;
    DataBaseHelper dataBaseHelper;
    String wareHouseName, wareHouseAddress;
    ArrayList<String> listOfSearchingData;
    ArrayAdapter<String> adapter;

    WareHouseBean wareHouseBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        context = this;
        init();
        dataBaseHelper = new DataBaseHelper(context);
        listOfSearchingData = new ArrayList<String>();
        listOfSearchingData.add("Name : InfyU Labs \nAddress : GandhiNagar");
        dataBaseHelper = new DataBaseHelper(context);
        adapter();
        adapter.notifyDataSetChanged();
        wareHouseBean = new WareHouseBean();
        wareHouse.setAdapter(adapter);
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

    public void adapter() {
        List<WareHouseBean> list = new ArrayList<>();
        list = dataBaseHelper.getWareHouseName();
        if (list.isEmpty() == false) {
            Log.e("list value", list.toString());
            for (WareHouseBean wareHouseBean : list) {
                listOfSearchingData.add(wareHouseBean.getWareHouseName());
                Log.e("listof", listOfSearchingData.toString());
            }
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfSearchingData);
        adapter.notifyDataSetChanged();
    }

    public void addAlert() {

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        View dialogView = inflater.inflate(R.layout.alertitem, null);
        final EditText name = (EditText) dialogView.findViewById(R.id.addItem);
        name.setHint("Name");
        final EditText address = (EditText) dialogView.findViewById(R.id.address);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(second.this);
        alertDialog.setTitle("Add WareHouse");
        if (name.getParent() != null) {
            ((ViewGroup) name.getParent()).removeView(name);
        }
        if (address.getParent() != null) {
            ((ViewGroup) address.getParent()).removeView(address);
        }
        layout.addView(name);
        layout.addView(address);
        alertDialog.setView(layout);
        alertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        wareHouseName = name.getText().toString();
                        wareHouseAddress = address.getText().toString();
                        if (!wareHouseName.equals("")) {
                            if (!listOfSearchingData.contains(wareHouseName.toLowerCase())) {
                                addFruitToLocal();
                                listOfSearchingData.add("Name : " + wareHouseName + "\n" + "Address : " + wareHouseAddress);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Name already exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter Fruit name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void init() {
        addWareHouse = findViewById(R.id.addNewWareHouse);
        wareHouse = findViewById(R.id.wareHouse_list);
        addWareHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlert();
            }
        });
    }

    public void addFruitToLocal() {

        if (!dataBaseHelper.checkWareHouse(wareHouseName)) {
            wareHouseBean.setWareHouseName(wareHouseName + "\n" + wareHouseAddress);
            dataBaseHelper.addWareHouse(wareHouseBean);
        } else {
            Toast.makeText(getApplicationContext(), "Name already exist", Toast.LENGTH_SHORT).show();
        }
    }
}