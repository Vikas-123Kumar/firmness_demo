package com.example.infyULabs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.infyULabs.bean.FruitName;

import com.example.infyULabs.dataAnalysis.MainActivity;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.example.infyULabs.nir.NirConnection;
import com.example.infyULabs.userProfile.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChooseTheData extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView list_view_item;
    //    private SearchView searchView;
    ArrayList<String> listOfSearchingData;
    ArrayAdapter<String> adapter;
    FloatingActionButton addItem;
    DataBaseHelper dataBaseHelper;
    FruitName fruit_bean;
    List list;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final int REQUEST_WRITE_STORAGE = 112;
    Context context;
    String fruitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_the_data);
//        searchView = (SearchView) findViewById(R.id.searchView);
        list_view_item = (ListView) findViewById(R.id.list_view_item);
        addItem = findViewById(R.id.fab);
        context = this;
        fruit_bean = new FruitName();
        Date date = new Date();
        Log.e("date", date + "");
        listOfSearchingData = new ArrayList<String>();
//        listOfSearchingData.add("rice");
//        listOfSearchingData.add("chilli");
        listOfSearchingData.add("apple");
        dataBaseHelper = new DataBaseHelper(context);
        adapter();
        adapter.notifyDataSetChanged();
        list_view_item.setAdapter(adapter);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.e("item name", newText);
//
//                if (searchData(newText)) {
//                    adapter.getFilter().filter(newText.toLowerCase());
//                    adapter.notifyDataSetChanged();
//                    list_view_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            try {
//                                String itemName = list.get(position).toString();
//                                Log.e("item name", itemName);
//                                if (itemName.equals("rice")) {
//                                    startActivity(new Intent(context, RiceData.class));
//                                } else {
//                                    startActivity(new Intent(context, Connection.class));
//                                }
//                            } catch (Exception e) {
//
//                            }
//
//                        }
//                    });
//
//                } else {
//                    adapter.getFilter().filter("..,;,;,;,kk;;;kk");// some random string
//                    GlobalClass.showToast("No Data Found");
//                }
//
//                return true;
//            }
//        });
        list_view_item.setOnItemClickListener(this);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlert();

            }
        });
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            boolean hasPermission = (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            Log.e("permission", hasPermission + "");
            if (!hasPermission) {
                ActivityCompat.requestPermissions(ChooseTheData.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_WRITE_STORAGE);
                Log.e("permission......", hasPermission + "");

            }
        }

        setupToolbar();
    }


    public boolean searchData(String newtext) {
        for (String i : listOfSearchingData) {
            Log.e("item in for", i);
            if (i.contains(newtext.toLowerCase())) {
                Log.e("item", i);
                list = new ArrayList();
                list.add(i);
                return true;
            }
        }
        return false;
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
        List<FruitName> list = new ArrayList<>();
        list = dataBaseHelper.getFruit();
        Log.e("list value", list.toString());
        for (FruitName fruitName : list) {
            listOfSearchingData.add(fruitName.toString());
            Log.e("listof", listOfSearchingData.toString());

        }
        Collections.sort(listOfSearchingData);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfSearchingData);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    public void addAlert() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertitem, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.addItem);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChooseTheData.this);
        alertDialog.setTitle("Add New Fruit");
        if (editText.getParent() != null) {
            ((ViewGroup) editText.getParent()).removeView(editText);
            Log.e("error", alertDialog + "");
        }
        alertDialog.setView(editText);
        alertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fruitName = editText.getText().toString();
                        if (!fruitName.equals("")) {
                            if (!listOfSearchingData.contains(fruitName.toLowerCase())) {
                                addFruitToLocal();
                                listOfSearchingData.add(fruitName);
                                adapter.notifyDataSetChanged();
                                Log.e("string value", fruitName);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ChooseTheData.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        String itemName = listOfSearchingData.get(position);
        Log.e("position of item", position + "");
        Log.e("position", id + "");
        intent = new Intent(ChooseTheData.this, Connection.class);
        intent.putExtra("item", itemName);
        Log.e("position", listOfSearchingData.get(position));
        startActivity(intent);
    }


    public void addFruitToLocal() {

        if (!dataBaseHelper.checkFruit(fruitName)) {
            fruit_bean.setFruit_name(fruitName);
            dataBaseHelper.addFruit(fruit_bean);
        } else {
            Toast.makeText(getApplicationContext(), "Name already exist", Toast.LENGTH_SHORT).show();
        }
    }
}
