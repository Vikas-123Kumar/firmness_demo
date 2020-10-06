package com.example.infyULabs.dataAnalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.API_URL;
import com.example.infyULabs.ChooseTheData;
import com.example.infyULabs.Connection;
import com.example.infyULabs.GlobalClass;
import com.example.infyULabs.Profile;
import com.example.infyULabs.R;
import com.example.infyULabs.Support;
import com.example.infyULabs.bean.ScanBean;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.example.infyULabs.setting.SettingOfApp;
import com.example.infyULabs.Trend;
import com.example.infyULabs.userProfile.LoginActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.lang.String;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Button setting_action;
    private Context context = this;
    private LineChart line_graph;
    byte buffer[];
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static final String AppleUrl = "https://colorsorter.herokuapp.com/api/color";
    RequestQueue rQueue;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    boolean scanConnected = false;
    String word = "";
    String wordData = "";
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    private OutputStream outputStream;
    private InputStream inputStream;
    LineGraphSeries<DataPoint> series;
    int dataNo = 0;
    ArrayList list;
    BluetoothSocket socket;
    private ListView listViewOFpairdevice;
    Handler handler = new Handler();
    private ArrayAdapter arrayAdapterForList;
    Set<BluetoothDevice> bondedDevices;
    String string, fruit, fruitName, token;
    double[] lam1;
    DataBaseHelper dataBaseHelper;
    private ProgressBar pbar;
    private BluetoothDevice device;
    Boolean switchForGraph = false;
    boolean stopThread;
    ArrayList<SliceValue> entriesData;
    int j = 0;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    MenuItem itemForNavigation;
    PieChartView pieChartView;
    String[] receivedData = {""};
    int dataJ = 0;
    String dataForChart = "";    //y=valueofscandata```
    boolean deviceConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setting_action = findViewById(R.id.setting_action);
        context = this;
        pbar = findViewById(R.id.progresBar);
        listViewOFpairdevice = (ListView) findViewById(R.id.item);
        line_graph = findViewById(R.id.viewpager);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        GlobalClass globalClass = new GlobalClass();
        lam1 = globalClass.getLam1();

        Intent intent = getIntent();
        lineEntries = new ArrayList();
        series = new LineGraphSeries<>();
        setup_nav_toolbar();
        setting_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingOfApp.class);
                startActivity(intent);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener   //Used for the bottom item
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            itemForNavigation = item;
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    try {
                        ScanData();
                        Log.e("in scan", "scan click");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return true;
                case R.id.connection_device:
                    if (item.getItemId() == R.id.connection_device) {
                        pbar.setVisibility(View.VISIBLE);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (BTinit()) {
                                    if (deviceConnected = true) {

                                    }
                                } else {
                                    GlobalClass.showToast("Device not connected");
                                }
                            }
                        });
                    }
                    return true;
                case R.id.navigation_add_to_report:
//                    startActivity(new Intent(context, Addtoreport.class));
                    if (!wordData.equals("")) {
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                    }
                    return true;
            }

            return false;
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
                GlobalClass.showToast("Device Connection Closed");
                Log.e("socket value", socket.isConnected() + "");
            }
        } catch (IOException ex) {
            Log.e("exception", ex.getMessage());
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    public void openUrlForYData(String receivedData) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("spectrum", receivedData.trim());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AppleUrl, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                int r = Integer.parseInt(jsonObject.getString("r"));
                                int g = Integer.parseInt(jsonObject.getString("g"));
                                int b = Integer.parseInt(jsonObject.getString("b"));

                                Log.e("Response from server", r + "");
                                entriesData = new ArrayList<>();
                                entriesData.add(new SliceValue((float) 2.0, Color.parseColor(jsonObject.getString("hex"))).setLabel(jsonObject.getString("hex")));
                                PieChartData pieChartData = new PieChartData(entriesData);
                                if (pieChartView != null) {
                                    pieChartData.setHasLabels(true);
                                    pieChartData.setHasCenterCircle(false);
                                    pieChartView.setPieChartData(pieChartData);
                                }
                            } catch (Exception ex) {
                                Log.e("Value of jsonObject", ex.getMessage() + "");
                                ex.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("error", volleyError.toString());

                }
            });
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            rQueue = Volley.newRequestQueue(MainActivity.this);
            rQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("JSONObject Here", e.toString());
        }
    }

    public void ScanData() throws IOException {       //this method is used for making connection to device and get data from device.
        String string = "1";//to give textview.getText().toString(); to send data to arduino
        string.concat("\n");
        try {
            final GlobalClass globalClass = (GlobalClass) getApplicationContext();
            BluetoothDevice device = globalClass.getSocket();
            if (socket != null) {
                socket = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
                Log.e("socket value", device.toString() + "");
                socket.connect();

                Log.e("item name", itemForNavigation.getItemId() + "");
                Log.e("socket value", socket.isConnected() + "");
                scanConnected = true;
            } else {
                pbar.setVisibility(View.GONE);
                GlobalClass.showToast("Please Connect To UV Nano Device");
            }
        } catch (Exception e) {
            pbar.setVisibility(View.GONE);
            GlobalClass.showToast("Please Connect To UV Nano Device");
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    outputStream = socket.getOutputStream();
                    Log.e("device  after connected", "" + socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputStream.write(string.getBytes());
                beginListenForData();
            }
        } else {
            GlobalClass.showToast("Please Connect To UV Nano Device");
        }
    }

    public void setup_nav_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_36dp);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(context, Profile.class));
        } else if (id == R.id.nav_logs) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setMessage("Do you want to logout?");
            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences SM = getSharedPreferences("userrecord", 0);
                    SharedPreferences.Editor edit = SM.edit();
                    edit.putBoolean("userlogin", false);
                    edit.commit();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        } else if (id == R.id.nav_trends) {
            startActivity(new Intent(context, Trend.class));

        } else if (id == R.id.nav_support) {
            startActivity(new Intent(context, Support.class));

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void beginListenForData() {
        word = "";
        wordData = "";//there are formate of the data i.e.  taken from the device.
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                switchForGraph = true;
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        int byteCount = inputStream.read(buffer);
                        String string = new String(buffer, 0, byteCount, "UTF-8");
                        Log.e("byte", byteCount + "");
                        wordData += string;
//                        System.out.println(wordData);
                        Log.e("word length", wordData.split(" ").length + "");
                        //  Log.e("stringValue::", receivedData.length + "");
                        if (string.contains("\n")) {
                            if (string.endsWith("\n")) {
                                word += string.replace("\n", "");
                                dataForChart = word;
//                                openUrlForYData(word);
                                dataToServer(word);
                                dataNo++;
                                Log.e("word in contain", word + " length" + word.split(" ").length);
                                word = "";
                            } else if (string.startsWith("\n")) {
//                                openUrlForYData(word);
                                dataToServer(word);
                                dataNo++;
                                dataForChart = word;
                                Log.e("word in start", word + " length" + word.split(" ").length);
                                word = string.replace("\n", "");

                                Log.e("start string", word);
                            } else {
                                String[] leftRightStrings = string.split("\n");
                                word += leftRightStrings[0];
                                dataNo++;
                                Log.e("word length", word.length() + "");
                                dataForChart = word;
//                                openUrlForYData(word);
                                dataToServer(word);
                                Log.e("left string", word + " length" + word.split(" ").length);
                                word = leftRightStrings[1];
                                Log.e("right string", word + " length" + word.split(" ").length);
                            }
                        } else {
                            word += string;
                        }
                        if (dataNo == 7) {
                            Log.e("closing input stream", dataNo + "");
                            inputStream.close();
                            dataNo = 0;
                        }
                        //an Object of the PointsGraphSeries for plotting scatter graphs
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!dataForChart.isEmpty() && lam1.length == 288) {
                                    getEntries();
                                    lineDataSet = new LineDataSet(lineEntries, "");

                                    lineDataSet.setDrawCircles(true);
                                    lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                                    lineDataSet.setColor(Color.BLUE);
                                    lineDataSet.setCircleColor(Color.BLUE);
                                    lineDataSet.setCircleSize(2f);
                                    lineDataSet.setDrawCircleHole(false);
                                    lineDataSet.setFillAlpha(65);
                                    lineDataSet.setFillColor(Color.BLUE);
                                    lineDataSet.setDrawFilled(true);
                                    lineData = new LineData(lineDataSet);
                                    line_graph.setData(lineData);
                                    line_graph.setTouchEnabled(true);
                                    line_graph.invalidate();
                                    line_graph.getAxisRight().setDrawGridLines(false);
                                    line_graph.getAxisLeft().setDrawGridLines(false);
                                    line_graph.getXAxis().setDrawGridLines(false);
//                                    line_graph.setDragEnabled(true);
                                    lineDataSet.setDrawValues(false);
                                    line_graph.getAxisLeft().setEnabled(false);
                                    line_graph.getAxisRight().setEnabled(false);
                                    line_graph.getDescription().setEnabled(false);
                                    line_graph.getLegend().setEnabled(false);
                                }
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {

                        }
                    } catch (IOException ex) {
                        stopThread = true;
                    }

                }
            }

        });
        thread.start();

    }

    public void dataToServer(String receivedData) {
        SharedPreferences prefs = getSharedPreferences("userrecord", 0);
        token = prefs.getString("token", null);//"No n
        JSONObject jsonObject = new JSONObject();
        fruitName = getIntent().getStringExtra("item");
        Log.e("token", token);
        try {
            jsonObject.put("token", token);
            jsonObject.put("name", fruitName.toLowerCase());
            jsonObject.put("reading", receivedData.trim());
            Log.e("json", jsonObject + "");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL.uv_url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e("response", jsonObject + "");
                            try {
                                JSONObject response = new JSONObject(jsonObject.getString("uv"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("error", volleyError.toString());

                }
            });
            rQueue = Volley.newRequestQueue(MainActivity.this);
            rQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("JSONObject Here", e.toString());
        }
    }

    public int[] stringIntoArray(String recieveData) {
        int[] integerList = new int[288];
        int i = 0;
        for (String field : recieveData.split(" ")) {
            integerList[i] = Integer.parseInt(field);
            i++;
        }
        return integerList;
    }

    private void getEntries() {
        int[] valueOfScandata = stringIntoArray(dataForChart);
        Log.e("length of scan", valueOfScandata.length + "");
        lineEntries.clear();
        for (int i = 0; i < valueOfScandata.length; i++) {
            if (i % 4 == 0) {
                lineEntries.add(new Entry((float) lam1[i], (float) valueOfScandata[i]));
            }
        }
    }

    /**
     * Custom receiver for returning the event that a scan has been initiated from the button
     */
    @Override
    public void onClick(View v) {
    }

    public void dataToSqlite() {
        ScanBean scanBean = new ScanBean();
        Date date = new Date();
        dataBaseHelper = new DataBaseHelper(this);
        if (dataBaseHelper != null) {
            scanBean.setFruitName(fruitName);
            scanBean.setScanData(string);
            scanBean.setScanTime(date);
            dataBaseHelper.addScanData(scanBean);
            Log.e("scan value", scanBean + "");
            GlobalClass.showToast("Data Save");
        }
    }

    public boolean BTinit() {                      //check that mobile bluetooth is on or not if not then on .
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
        }
        bondedDevices = bluetoothAdapter.getBondedDevices();
        list = new ArrayList();

        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator != null) {
                    Log.e("Iterator Devices ", " " + iterator);
                }
                if (iterator.getName() != null) {
                    list.add(iterator.getName() + "\n" + iterator.getAddress());
                    if (iterator.getName().contains("Infinity")) {
                        device = iterator;
                        Log.e("pair device", list + "");
                        found = true;
                    }
                }
            }
            alertDialog();
        }
        return true;
    }

    private void alertDialog() {        //this method is for show the paired device in your mobile on dialog box.
        handler.post(new Runnable() {
            @Override
            public void run() {
                final AlertDialog alertdailog = new AlertDialog.Builder(MainActivity.this).create();
                arrayAdapterForList = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                listViewOFpairdevice.setAdapter(arrayAdapterForList);
                arrayAdapterForList.notifyDataSetChanged();
                alertdailog.setMessage("");
                if (listViewOFpairdevice.getParent() != null) {
                    ((ViewGroup) listViewOFpairdevice.getParent()).removeView(listViewOFpairdevice);
                }
                alertdailog.setView(listViewOFpairdevice);
                alertdailog.setTitle("List Of Bluetooth Pair Devices");
                alertdailog.show();
                listViewOFpairdevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        for (BluetoothDevice iterator : bondedDevices) {

                            if (iterator.getAddress().equals(list.get(position).toString().split("\n")[1])) {
                                device = iterator;
                                Log.e("pair device", device.getName() + "");
                                break;
                            }
                        }
                        alertdailog.dismiss();
                        BTconnect();

                        if (socket.isConnected()) {
                            try {
                                boolean status = false;
                                status = true;
                                if (status) {
                                    GlobalClass.showToast("Device Connected");
                                    itemForNavigation.setTitle("Connected");
                                    pbar.setVisibility(View.GONE);
                                    socket.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast1 = Toast.makeText(getApplicationContext(), "Device not connected", Toast.LENGTH_LONG);
                            toast1.show();
                            pbar.setVisibility(View.GONE);

                        }

                    }
                });
            }
        });
    }

    public boolean BTconnect() {
        // making the connection of device to your mobile
        if (device != null) {
            boolean connected = false;
            try {
                Log.e("device name e", device + "");

                socket = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
                final GlobalClass globalClass = (GlobalClass) getApplicationContext();
                if (!socket.isConnected()) {
                    globalClass.setSocket(device);

                    socket.connect();
                    Log.e("device name", socket.isConnected() + "");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error", e.getMessage() + "");

                connected = true;
            }
            return connected;
        }
        return false;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission},
                    requestCode);
            writeOnFile();

        } else {
            writeOnFile();

        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                writeOnFile();
                Toast.makeText(MainActivity.this,
                        "File Saved!!",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void writeOnFile() {
        //String directoryDownload = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("DATA", "" + wordData);
        fruitName = getIntent().getStringExtra("item");
        Log.e("fruit name", fruitName);

        String fileName = fruitName + System.currentTimeMillis() + ".txt";
        try {

            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/INfyuLabs/" + fruitName);

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("TAG", "could not create the directories");
                }
            }

            final File myFile = new File(dir, fileName);
            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            FileWriter writer = new FileWriter(myFile);
            writer.write(wordData);

            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}