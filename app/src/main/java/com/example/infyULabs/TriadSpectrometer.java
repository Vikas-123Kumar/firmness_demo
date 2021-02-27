package com.example.infyULabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.dataAnalysis.MainActivity;
import com.example.infyULabs.socket.SerialListener;
import com.example.infyULabs.socket.SerialService;
import com.example.infyULabs.socket.SerialSocket;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class TriadSpectrometer extends AppCompatActivity implements View.OnClickListener, ServiceConnection, SerialListener {
    private Context context = this;
    private LineChart line_graph;
    byte buffer[];
    private android.app.AlertDialog alertDialog;
    Button uv_light, nir_light, white_light, uv_nir_light, white_nir, white_uv, all_light;
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static final String AppleUrl = "https://colorsorter.herokuapp.com/api/color";
    RequestQueue rQueue;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    String word = "";
    String wordData = "";
    String number = "";
    LineData lineData;
    String colorName, led, fruitNumber, fruitName, token, deviceAddress;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    LineGraphSeries<DataPoint> series;
    int dataNo = 0;
    int scanNo = 0;
    ArrayList list;
    BluetoothSocket socket;
    private ListView listViewOFpairdevice;
    Handler handler = new Handler();
    private ArrayAdapter arrayAdapterForList;
    Set<BluetoothDevice> bondedDevices;
    double[] lam1;
    File dir;
    TextView resulr_text;
    private SerialService service;
    private boolean initialStart = true;
    private boolean savePermission = true;
    private ProgressBar pbar;
    private BluetoothDevice device;
    boolean stopThread;
    private ToggleButton btn_os;
    private ToggleButton btn_continuous;
    MenuItem itemForNavigation;
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
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_triad);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        GlobalClass globalClass = new GlobalClass();
        lam1 = globalClass.getX();
        Intent intent = getIntent();
        onBluetooth();
        fruitName = getIntent().getStringExtra("item");
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
        bindService(new Intent(getApplicationContext(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        startService(new Intent(getApplicationContext(), SerialService.class));
        setupToolbar();
//        findFolder();
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
                    Toast.makeText(getApplicationContext(), "Triad is disconnected", Toast.LENGTH_LONG).show();
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
                //Device has disconnected
                Log.e("in not connected", "connection lose");
//                GlobalClass.showToast("Device Connection Closed");
                itemForNavigation.setEnabled(true);
//                finish();
                if (socket.isConnected() != true) {
                    try {
                        socket.close();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void init() {
        uv_light = findViewById(R.id.uv_light);
        nir_light = findViewById(R.id.nir_light);
//        white_light = findViewById(R.id.white_light);
//        white_nir = findViewById(R.id.white_nir);
//        white_uv = findViewById(R.id.white_uv);
//        uv_nir_light = findViewById(R.id.uv_nir);
        all_light = findViewById(R.id.all_light);
        btn_os = (ToggleButton) findViewById(R.id.btn_saveOS);
        uv_light.setOnClickListener(this);
        nir_light.setOnClickListener(this);
        resulr_text = findViewById(R.id.result_text);
//        white_uv.setOnClickListener(this);
//        white_nir.setOnClickListener(this);
//        white_light.setOnClickListener(this);
        all_light.setOnClickListener(this);
//        uv_nir_light.setOnClickListener(this);
//        uv_nir_light.setEnabled(false);
        btn_os.setChecked(false);
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
//                case R.id.navigation_add_to_report:
////                    startActivity(new Intent(context, Addtoreport.class));
////                    if (!wordData.equals("")) {
////                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
////                    }
//                    return true;
            }

            return false;
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            startService(new Intent(getApplicationContext(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    protected void onStop() {
        if (service != null && !isChangingConfigurations())
            service.detach();
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
        if (connected != Connected.False)
            disconnect();
        stopService(new Intent(getApplicationContext(), SerialService.class));
//        this.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
            runOnUiThread(this::connect);
        } else {
            Log.e("service", service + "");
        }
    }

    public void ScanData(String led) throws IOException {       //this method is used for making connection to device and get data from device.
        String string = led;//to give textview.getText().toString(); to send data to arduino
        string.concat("\n");
        Log.e("connection", socket.isConnected() + "");
        if (!socket.isConnected())
            throw new IOException("not connected");
        if (connected == Connected.True) {
            socket.getOutputStream().write(string.getBytes());
            uv_light.setEnabled(false);
            nir_light.setEnabled(false);
//            white_light.setEnabled(false);
//            white_nir.setEnabled(false);
//            white_uv.setEnabled(false);
//            uv_nir_light.setEnabled(false);
            all_light.setEnabled(false);
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

    public void beginListenForData(String string) {
        //there are formate of the data i.e.  taken from the device.
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
//                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                try {
//                        int byteCount = socket.getInputStream().read(buffer);
//                        String string = new String(buffer, 0, byteCount, "UTF-8");
                    wordData += string;
                    Log.e("word length", wordData.split(" ").length + "");
                    if (string.contains("\n")) {
                        if (string.endsWith("\n")) {
                            word += string.replace("\n", "");
                            dataForChart = word;
                            dataToServer(word);
                            dataNo++;
                            Log.e("word in contain", word + " length" + word.split(" ").length);
                            word = "";
                        } else if (string.startsWith("\n")) {
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
                            dataToServer(word);
                            Log.e("left string", word + " length" + word.split(" ").length);
                            word = leftRightStrings[1];
                            Log.e("right string", word + " length" + word.split(" ").length);
                        }
                    } else {
                        word += string;
                    }
                    if (dataNo == 4) {
                        stopThread = true;
                        dataNo = 0;
                        Log.e("closing input stream", dataNo + "");
                        final boolean saveOS = btn_os.isChecked();
                        Log.e("continue ", led);
                        Log.e("led ", led);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Scan Complete", Toast.LENGTH_LONG).show();
                                if (saveOS == true) {
                                    scanNo++;
                                    if (!wordData.equals("")) {
                                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                                    }
                                    if (scanNo == 5) {
                                        GlobalClass.showToast("5 Scan Complete");
                                        scanNo = 0;
                                    }
                                }
//                                    findFolder();
                                uv_light.setEnabled(true);
                                nir_light.setEnabled(true);
//                                white_light.setEnabled(true);
//                                white_nir.setEnabled(true);
//                                white_uv.setEnabled(true);
//                                uv_nir_light.setEnabled(true);
                                all_light.setEnabled(true);
                                word = "";
                                wordData = "";
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
                                lineDataSet.setDrawValues(true);
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
                } catch (Exception ex) {
                    Log.e("exception", ex.getMessage());
                    stopThread = true;
                }
                //}
            }

        });
        thread.start();

    }

    public void dataToServer(String receivedData) {
        JSONObject jsonObject = new JSONObject();
        String url_str = fruitName.toLowerCase() + "-prediction-triad";
        try {
            jsonObject.put("spectrum", receivedData.trim());
            Log.e("json", jsonObject + "");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL.url + url_str, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e("response", jsonObject + "");
                            try {
                                String Brix = jsonObject.optString("brix");
                                String acidity = jsonObject.optString("ph");
                                String firmness = jsonObject.optString("firmness");
                                if (acidity.equals("") && firmness.equals("")) {
                                    resulr_text.setText("Brix : " + Brix);
                                } else if (firmness.equals("")) {
                                    resulr_text.setText("Brix : " + Brix + "\n\npH   : " + acidity);
                                } else if (acidity.equals("")) {
                                    resulr_text.setText("Brix : " + Brix + "\n\nFirmness : " + firmness);
                                } else {
                                    resulr_text.setText("Brix : " + Brix + "\n\npH   : " + acidity
                                            + "\n\nFirmness : " + firmness);
                                }
                            } catch (Exception e) {
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
        try {
            int i = 0;
            for (String field : recieveData.split(",")) {
                integerList[i] = Double.parseDouble(field);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                                deviceAddress = device.getAddress();
                                connect();
                                break;
                            }
                        }
                        alertdailog.dismiss();
                    }
                });
            }
        });
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
        Log.e("fruit name", fruitName);
        if (fruitNumber.equals("0")) {
            fruitNumber = "1";
        }
        String fileName = fruitName + System.currentTimeMillis() + ".txt";
        try {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TriadSpectrometer/" + fruitName + "/" + colorName + "/" + fruitNumber);
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

    public void findFolder() {
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TriadSpectrometer/" + fruitName + "/" + colorName);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("TAG", "could not create the directories");
                }
            }
            int file = -1;
            File[] files = dir.listFiles();
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                int file1 = Integer.parseInt(files[i].getName());
                if (file1 > file) {
                    file = file1;
                }
            }
            Log.e("folder name", file + "");
            fruitNumber = String.valueOf(file + 1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    1. White Light 2. NIR LED 3. UV 4. White+NIR 5. White+UV 6. UV+NIR 7. All Three

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uv_light) {
            if (connected == Connected.True) {
                led = "3";
                colorName = "uv";
                try {
                    if (scanNo == 0) {
                        findFolder();
                    }
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
            if (connected == Connected.True) {
                led = "2";
                colorName = "nir";
                try {
                    if (scanNo == 0) {
                        findFolder();
                    }
                    ScanData(led);
                    Log.e("in scan", "scan click");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
            }
        }
//        if (v.getId() == R.id.white_light) {
//            if (connected == Connected.True) {
//                led = "1";
//                colorName = "white";
//                try {
//                    if (scanNo == 0) {
//                        findFolder();
//                    }
//                    ScanData(led);
//                    Log.e("in scan", "scan click");
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            } else {
//                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
//            }
//        }
//        if (v.getId() == R.id.white_nir) {
//            if (connected == Connected.True) {
//                led = "4";
//                colorName = "white_nir";
//                try {
//                    if (scanNo == 0) {
//                        findFolder();
//                    }
//                    ScanData(led);
//                    Log.e("in scan", "scan click");
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
//            }
//        }
//        if (v.getId() == R.id.white_uv) {
//            if (connected == Connected.True) {
//                led = "5";
//                colorName = "white_uv";
//                try {
//                    if (scanNo == 0) {
//                        findFolder();
//                    }
//                    ScanData(led);
//                    Log.e("in scan", "scan click");
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Device Not Conected", Toast.LENGTH_LONG).show();
//            }
//        }
//        if (v.getId() == R.id.uv_nir) {
//            if (connected == Connected.True) {
//                led = "8";
//                colorName = "uv_nir";
//                try {
//                    if (scanNo == 0) {
//                        findFolder();
//                    }
//                    ScanData(led);
//                    Log.e("in scan", "scan click");
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Device Not Connected", Toast.LENGTH_LONG).show();
//            }
//        }
        if (v.getId() == R.id.all_light) {
            if (connected == Connected.True) {
                led = "7";
                colorName = "all_light";
                try {
                    if (scanNo == 0) {
                        findFolder();
                    }
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

    public void findPrevFolder() {
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TriadSpectrometer/" + fruitName + "/" + colorName);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("TAG", "could not create the directories");
                }
            }
            int file = -1;
            File[] files = dir.listFiles();
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                int file1 = Integer.parseInt(files[i].getName());
                if (file1 > file) {
                    file = file1;
                }
            }
            Log.e("folder name", file + "");
            if (String.valueOf(file).equals("-1")) {
                Toast.makeText(getApplicationContext(), "No Previous available", Toast.LENGTH_LONG).show();
            } else {
                fruitNumber = String.valueOf(file);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void askForSaveDialog() {
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Save Scan");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Do you want to save?");
        alertDialogBuilder.setPositiveButton("Prev", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                savePermission = false;
                findFolder();
            }
        });
        alertDialogBuilder.setNegativeButton("New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                savePermission = true;
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if (initialStart) {
            initialStart = false;
            runOnUiThread(this::connect);
            Log.e("service if", service + "");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public void onSerialConnect() {
        connected = Connected.True;
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
        pbar.setVisibility(View.GONE);
        itemForNavigation.setEnabled(false);
//        askForSaveDialog();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        String msg = new String(data);
        Log.e("msg", msg);
        beginListenForData(msg);
    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnect();
    }

    private enum Connected {False, Pending, True}

    private Connected connected = Connected.False;

    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//            status("connecting...");
            connected = Connected.Pending;
            Log.e("cvfxbfdgfdgfd", device + "");
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
//            Log.e("connection socket", socket + "");
            SerialSocket socket1 = new SerialSocket(getApplicationContext(), socket);
            service.connect(socket1);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage() + "");
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        try {
            Log.e("detach", "in detach");
            unbindService(this);
        } catch (Exception ignored) {
        }
    }
}