package com.example.infyULabs.dataAnalysis;

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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.API_URL;
import com.example.infyULabs.GlobalClass;
import com.example.infyULabs.Profile;
import com.example.infyULabs.R;
import com.example.infyULabs.Support;
import com.example.infyULabs.Trend;
import com.example.infyULabs.bean.ScanBeanFromDevice;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.example.infyULabs.setting.SettingOfApp;
import com.example.infyULabs.socket.SerialListener;
import com.example.infyULabs.socket.SerialService;
import com.example.infyULabs.socket.SerialSocket;
import com.example.infyULabs.socket.TextUtil;
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
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ServiceConnection, SerialListener {
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
    ScanBeanFromDevice scanBeanFromDevice;
    ArrayList list;
    private ToggleButton btn_os;
    private ToggleButton btn_continuous;
    BluetoothSocket socket;
    private ListView listViewOFpairdevice;
    Handler handler = new Handler();
    private ArrayAdapter arrayAdapterForList;
    Set<BluetoothDevice> bondedDevices;
    String string, fruitName, token;
    double[] lam1;
    DataBaseHelper dataBaseHelper;
    private ProgressBar pbar;
    private BluetoothDevice device;
    Boolean switchForGraph = false;
    boolean stopThread;
    ArrayList<SliceValue> entriesData;
    MenuItem itemForNavigation;
    PieChartView pieChartView;
    TextView stringText;
    private BluetoothAdapter BA;
    String dataForChart = "";    //y=valueofscandata```
    boolean deviceConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        init();
        scanBeanFromDevice = new ScanBeanFromDevice();
        BA = BluetoothAdapter.getDefaultAdapter();
        GlobalClass globalClass = new GlobalClass();
        lam1 = globalClass.getLam1();
        lineEntries = new ArrayList();
        onBluetooth();
        series = new LineGraphSeries<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        BA.startDiscovery();
        this.registerReceiver(mReceiver, filter);
        setup_nav_toolbar();


//        hexWatcher = new TextUtil.HexWatcher(stringText);
//        hexWatcher.enable(hexEnabled);
        setting_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingOfApp.class);
                startActivity(intent);
            }
        });
        bindService(new Intent(getApplicationContext(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        startService(new Intent(getApplicationContext(), SerialService.class));
    }

    public void init() {
        setting_action = findViewById(R.id.setting_action);
        pbar = findViewById(R.id.progresBar);
        stringText = findViewById(R.id.receive_text);
        listViewOFpairdevice = (ListView) findViewById(R.id.item);
        line_graph = findViewById(R.id.viewpager);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        btn_os = (ToggleButton) findViewById(R.id.btn_saveOS);
        btn_os.setChecked(false);
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
                //Device found
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                Log.e("connected in ", "connected");
                //Device is now connected
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                Log.e("in not connected", socket.isConnected() + "");
//                GlobalClass.showToast("Device Connection Closed");
                itemForNavigation.setEnabled(true);
//                finish();

//                if (socket.isConnected() != true) {
//                    try {
//                        socket.close();
//                        finish();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener   //Used for the bottom item
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            itemForNavigation = item;
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    if (connected != Connected.True) {
                        Toast.makeText(getApplicationContext(), "not connected", Toast.LENGTH_SHORT).show();
                    }
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
                    itemForNavigation.setEnabled(false);
                    return true;
            }

            return false;
        }

    };

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
        super.onStop();
        if (service != null && !isChangingConfigurations())
            service.detach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
            runOnUiThread(this::connect);
        } else {
            Log.e("service", service + "");
        }
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
        super.onDestroy();
    }

    public void onBluetooth() {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        }
    }


    public void ScanData() throws IOException {
        //this method is used for making connection to device and get data from device.

        String string = "1";//to give textview.getText().toString(); to send data to arduino
        string.concat("\n");
        TextView txtTag = new TextView(this);
        txtTag.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        txtTag.setEnabled(false);
        txtTag.setText("1");
        send(txtTag.getText().toString());
        stringText.setText("");
//        if (!socket.isConnected())
//            throw new IOException("not connected");
        if (connected == Connected.True) {
            itemForNavigation.setEnabled(false);
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

    String scanData = "";

    private boolean isValidString(String phone) {
        String Mobile_STRING = "^[7-9][0-9]{9}$";
        return Pattern.compile(Mobile_STRING).matcher(phone).matches();
    }

    public void beginListenForData(String string) {
        //there are formate of the data i.e.  taken from the device.
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                int len;
                try {
//                        int byteCount = socket.getInputStream().read(buffer);
//                        String string = new String(buffer, 0, byteCount, "UTF-8");
//                        scanData += string;
//                        wordData += string;
                    if (string.contains("\n")) {
                        if (string.endsWith("\n")) {
                            word += string.replace("\n", " ");
                            dataForChart = word;
//                                dataToServer(word);
                            dataNo++;
                            Log.e("word in contain", word + " length" + word.split(" ").length);
                            word = "";
                        } else if (string.startsWith("\n")) {
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
//                                dataToServer(word);
                            Log.e("left string", word + " length" + word.split(" ").length);
                            word = leftRightStrings[1];
                            Log.e("right string", word + " length" + word.split(" ").length);
                        }
                    } else {
                        word += string;
                    }
                    if (dataNo == 5) {
                        Log.e("word data", "wordData");
                        dataNo = 0;
                        final boolean saveOS = btn_os.isChecked();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (saveOS == true) {
                                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                                }
                                Toast.makeText(getApplicationContext(), "Scan Complete", Toast.LENGTH_LONG).show();
                                itemForNavigation.setEnabled(true);
                                word = "";
                                wordData = "";
                                scanData = "";
                                stopThread = true;
//                                try {
//                                    socket.getInputStream().close();
//                                    itemForNavigation.setEnabled(true);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        });
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
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    Log.e("exception", ex.getMessage());
                    stopThread = true;
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
//        if (recieveData.split(" ").length == 288) {
        try {
            int i = 0;
            for (String field : recieveData.split(" ")) {
                integerList[i] = Integer.parseInt(field);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  }
        }
        return integerList;
    }

    private void getEntries() {
        int[] valueOfScandata = stringIntoArray(dataForChart);
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
                                Log.e("pair devices", device.getName() + "");
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
        Log.d("DATA", "" + wordData);
        fruitName = getIntent().getStringExtra("item");
        Log.e("fruit name", fruitName);
        String fileName = fruitName + System.currentTimeMillis() + ".txt";
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UV_Vis/" + fruitName);
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
            String writeData = stringText.getText().toString();
            writer.write(writeData);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String deviceAddress;
    private SerialService service;
    private boolean initialStart = true;

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
    }

    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
        String msg = new String(data);
//        beginListenForData(msg);
    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnect();
    }

    private TextUtil.HexWatcher hexWatcher;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    private TextView sendText;

    private void receive(byte[] data) {
        if (hexEnabled) {
            stringText.append(TextUtil.toHexString(data) + '\n');
        } else {
            String msg = new String(data);
            if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                beginListenForData(msg);
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg.charAt(0) == '\n') {
                    Editable edt = stringText.getEditableText();
                    if (edt != null && edt.length() > 1)
                        edt.replace(edt.length() - 2, edt.length(), "");
                }
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }
            stringText.append(TextUtil.toCaretString(msg, newline.length() != 0));
        }
    }

    private void send(String str) {
        if (connected != Connected.True) {
            Toast.makeText(getApplicationContext(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;
            if (hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringText.append(spn);
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }
}