package com.example.infyULabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.bean.ScanBean;
import com.example.infyULabs.dataAnalysis.MainActivity;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.example.infyULabs.setting.SettingOfApp;
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
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class TriadSpectrometer extends AppCompatActivity implements View.OnClickListener {
    private Context context = this;
    private LineChart line_graph;
    byte buffer[];
    Button uv_light, nir_light, white_light, uv_nir_light, white_nir, white_uv, all_light;
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static final String AppleUrl = "https://colorsorter.herokuapp.com/api/color";
    RequestQueue rQueue;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    boolean scanConnected = false;
    String word = "";
    String wordData = "";
    LineData lineData;
    String colorName;
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
    private BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triad_spectrometer);
        context = this;
        init();
        BA = BluetoothAdapter.getDefaultAdapter();
        pbar = findViewById(R.id.progresBar);
        listViewOFpairdevice = (ListView) findViewById(R.id.item);
        line_graph = findViewById(R.id.viewpager);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        GlobalClass globalClass = new GlobalClass();
        lam1 = globalClass.getX();
        Intent intent = getIntent();
        onBluetooth();
        lineEntries = new ArrayList();
        series = new LineGraphSeries<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        BA.startDiscovery();
        this.registerReceiver(mReceiver, filter);
        setupToolbar();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("data ", "in broadcast");
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (BA.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    // The user bluetooth is turning off yet, but it is not disabled yet.
                    Log.e("off1", "off1");
                    finish();
                    return;
                }
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("device in found", device.getName() + "");
                Log.e("connected", "connected");
                //Device found
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.e("connected in ", "connected");
                //Device is now connected
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e("connected in discover", "connected");

                //Done searching
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                if (socket.isConnected()) {
                    try {
                        Log.e("connected closed", "close");
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Device has disconnected
            }
        }
    };

    public void init() {
        uv_light = findViewById(R.id.uv_light);
        nir_light = findViewById(R.id.nir_light);
        white_light = findViewById(R.id.white_light);
        white_nir = findViewById(R.id.white_nir);
        white_uv = findViewById(R.id.white_uv);
        uv_nir_light = findViewById(R.id.uv_nir);
        all_light = findViewById(R.id.all_light);
        uv_light.setOnClickListener(this);
        nir_light.setOnClickListener(this);
        white_uv.setOnClickListener(this);
        white_nir.setOnClickListener(this);
        white_light.setOnClickListener(this);
        all_light.setOnClickListener(this);
        uv_nir_light.setOnClickListener(this);
    }

    public void onBluetooth() {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener   //Used for the bottom item
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            itemForNavigation = item;
            switch (item.getItemId()) {
//                case R.id.navigation_scan:
////                    try {
////                        ScanData();
////                        Log.e("in scan", "scan click");
////                    } catch (IOException ex) {
////                        ex.printStackTrace();
////                    }
//                    return true;
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
        this.unregisterReceiver(mReceiver);
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
            rQueue = Volley.newRequestQueue(TriadSpectrometer.this);
            rQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("JSONObject Here", e.toString());
        }
    }

    public void ScanData(String led) throws IOException {       //this method is used for making connection to device and get data from device.
        String string = led;//to give textview.getText().toString(); to send data to arduino
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
//            GlobalClass.showToast("Please Connect To UV Nano Device");
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
                uv_light.setEnabled(false);
                nir_light.setEnabled(false);
                white_light.setEnabled(false);
                white_nir.setEnabled(false);
                white_uv.setEnabled(false);
                uv_nir_light.setEnabled(false);
                all_light.setEnabled(false);
                beginListenForData();
            }
        } else {
            GlobalClass.showToast("Please Connect To UV Nano Device");
        }
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
                        Log.e("scan", "1");
                        Log.e("scan", byteCount + "");
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
//                                dataToServer(word);
                                dataNo++;
                                Log.e("word in contain", word + " length" + word.split(" ").length);
                                word = "";
                            } else if (string.startsWith("\n")) {
//                                openUrlForYData(word);
//                                dataToServer(word);
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
//                                dataToServer(word);
                                Log.e("left string", word + " length" + word.split(" ").length);
                                word = leftRightStrings[1];
                                Log.e("right string", word + " length" + word.split(" ").length);
                            }
                        } else {
                            word += string;
                        }
                        if (dataNo == 15) {
                            Log.e("closing input stream", dataNo + "");
                            inputStream.close();
                            dataNo = 0;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, "Scan Complete", Toast.LENGTH_LONG).show();
                                    uv_light.setEnabled(true);
                                    nir_light.setEnabled(true);
                                    white_light.setEnabled(true);
                                    white_nir.setEnabled(true);
                                    white_uv.setEnabled(true);
                                    uv_nir_light.setEnabled(true);
                                    all_light.setEnabled(true);
                                }
                            });
                        }
                        //an Object of the PointsGraphSeries for plotting scatter graphs
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!dataForChart.isEmpty() && lam1.length == 18) {
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
                        Log.e("exception", ex.getMessage());

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
            rQueue = Volley.newRequestQueue(TriadSpectrometer.this);
            rQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("JSONObject Here", e.toString());
        }
    }

    public double[] stringIntoArray(String recieveData) {
        double[] integerList = new double[18];
        int i = 0;
        for (String field : recieveData.split(",")) {
            integerList[i] = Double.parseDouble(field);
            i++;
        }
        return integerList;
    }

    private void getEntries() {
        double[] valueOfScandata = stringIntoArray(dataForChart);
        Log.e("length of scan", valueOfScandata.length + "");
        lineEntries.clear();
        for (int i = 0; i < valueOfScandata.length; i++) {
            lineEntries.add(new Entry((float) lam1[i], (float) valueOfScandata[i]));

        }
    }

    /**
     * Custom receiver for returning the event that a scan has been initiated from the button
     */

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
                final AlertDialog alertdailog = new AlertDialog.Builder(TriadSpectrometer.this).create();
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
        if (ContextCompat.checkSelfPermission(TriadSpectrometer.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(TriadSpectrometer.this,
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
                Toast.makeText(TriadSpectrometer.this,
                        "File Saved!!",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(TriadSpectrometer.this,
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
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/INfyuLabs/" + fruitName + "/" + colorName);
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
//    1. White Light 2. NIR LED 3. UV 4. White+NIR 5. White+UV 6. UV+NIR 7. All Three

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uv_light) {
            if (socket != null) {
                String led = "3";
                colorName = "uv";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.nir_light) {
            if (socket != null) {
                String led = "2";
                colorName = "nir";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.white_light) {
            if (socket != null) {
                String led = "1";
                colorName = "white";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.white_nir) {
            if (socket != null) {
                String led = "4";
                colorName = "white_nir";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.white_uv) {
            if (socket != null) {
                String led = "5";
                colorName = "white_uv";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Device Not Conected", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.uv_nir) {
            if (socket != null) {
                String led = "6";
                colorName = "uv_nir";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.all_light) {
            if (socket != null) {
                String led = "7";
                colorName = "all_light";
                try {
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
    }
}