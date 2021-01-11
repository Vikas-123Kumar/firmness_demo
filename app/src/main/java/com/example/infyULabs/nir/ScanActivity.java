package com.example.infyULabs.nir;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.infyULabs.R;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;
import com.kstechnologies.nirscannanolibrary.SettingsManager;

import java.util.ArrayList;

/**
 * Activity for scanning for advertising Nano devices over BLE
 * This allows the user to specify a preferred Nano to use in the future.
 * The preferred Nano will be connected to first in environments with more than one Nano
 */
public class ScanActivity extends AppCompatActivity {
    public static final long SCAN_PERIOD = 6000;

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private static final String DEVICE_NAME = "NIR";
    private ArrayList<KSTNanoSDK.NanoDevice> nanoDeviceList = new ArrayList<>();
    private NanoScanAdapter nanoScanAdapter;
    private static Context mContext;
    private AlertDialog alertDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mContext = this;

        //Set up action bar title and enable back button
        ListView lv_nanoDevices = (ListView) findViewById(R.id.lv_nanoDevices);

        //Start scanning for devices that match DEVICE_NAME
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        //Create adapter for the NanoDevice objects returned from a BLE scan
        nanoScanAdapter = new NanoScanAdapter(this, nanoDeviceList);
        Log.e("scan activity", nanoDeviceList.toString());
        lv_nanoDevices.setAdapter(nanoScanAdapter);
        lv_nanoDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                confirmationDialog(nanoDeviceList.get(i).getNanoMac());
            }
        });

        mHandler = new Handler();
        scanLeDevice(true);
        Log.e("scan activity", nanoDeviceList.toString());
        setupToolbar();

    }

    /**
     * Provide user with a dialog that asks if they are sure they want to use the Nano with the
     * specified mac as their preferred device
     *
     * @param mac MAC address of Nano
     */
    public void confirmationDialog(String mac) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        final String deviceMac = mac;
        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.nano_confirmation_title));
        alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.nano_confirmation_msg, mac));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog.dismiss();
                SettingsManager.storeStringPref(mContext, SettingsManager.SharedPreferencesKeys.preferredDevice, deviceMac);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Callback function for Bluetooth scanning. This function provides the instance of the
     * Bluetooth device {@link BluetoothDevice} that was found, it's rssi, and advertisement
     * data (scanRecord).
     * <p>
     * When a Bluetooth device with the advertised name matching the
     * string DEVICE_NAME {@link ScanActivity#DEVICE_NAME} is found, a call is made to connect
     * to the device. Also, the Bluetooth should stop scanning, even if
     * the {@link #SCAN_PERIOD} has not expired
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            if (name != null && name.contains(DEVICE_NAME) && result.getScanRecord() != null) {
                Log.e("device name ", name);

                Boolean isDeviceInList = false;
                KSTNanoSDK.NanoDevice nanoDevice = new KSTNanoSDK.NanoDevice(device, result.getRssi(), result.getScanRecord().getBytes());
                for (KSTNanoSDK.NanoDevice d : nanoDeviceList) {
                    if (d.getNanoMac().equals(device.getAddress())) {
                        isDeviceInList = true;
                        d.setRssi(result.getRssi());
                        nanoScanAdapter.notifyDataSetChanged();
                    }
                }
                if (!isDeviceInList) {
                    nanoDeviceList.add(nanoDevice);
                    nanoScanAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            System.out.println("BLE// onScanFailed");
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }

    };

    /*
     * Handle the selection of a menu item.
     * In this case, there is only the up indicator. If selected, this activity should finish.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Scans for Bluetooth devices on the specified interval {@link #SCAN_PERIOD}.
     * This function uses the handler {@link ScanActivity#mHandler} to delay call to stop
     * scanning until after the interval has expired. The start and stop functions take an
     * LeScanCallback parameter that specifies the callback function when a Bluetooth device
     * has been found
     *
     * @param enable Tells the Bluetooth adapter {@link ScanActivity#mBluetoothAdapter} if
     *               it should start or stop scanning
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDevice(final boolean enable) {
        if (mBluetoothLeScanner == null) {
            Toast.makeText(ScanActivity.this, "Could not open LE scanner", Toast.LENGTH_SHORT).show();
        } else {
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        mBluetoothLeScanner.stopScan(mLeScanCallback);
                    }
                }, SCAN_PERIOD);
                mBluetoothLeScanner.startScan(mLeScanCallback);
            } else {
                mBluetoothLeScanner.stopScan(mLeScanCallback);
            }
        }
    }

    /**
     * Custom adapter that holds {@link KSTNanoSDK.NanoDevice} objects to be used in a listview.
     * This adapter contains device name, MAC, and RSSI
     */
    private class NanoScanAdapter extends ArrayAdapter<KSTNanoSDK.NanoDevice> {
        private final ArrayList<KSTNanoSDK.NanoDevice> nanoDevices;


        public NanoScanAdapter(Context context, ArrayList<KSTNanoSDK.NanoDevice> values) {
            super(context, -1, values);
            this.nanoDevices = values;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.row_nano_scan_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.nanoName = (TextView) convertView.findViewById(R.id.tv_nano_name);
                viewHolder.nanoMac = (TextView) convertView.findViewById(R.id.tv_nano_mac);
                viewHolder.nanoRssi = (TextView) convertView.findViewById(R.id.tv_rssi);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final KSTNanoSDK.NanoDevice device = getItem(position);
            if (device != null) {
                viewHolder.nanoName.setText(device.getNanoName());
                viewHolder.nanoMac.setText(device.getNanoMac());
                viewHolder.nanoRssi.setText(device.getRssiString());
            }
            return convertView;
        }
    }

    /**
     * View holder for {@link KSTNanoSDK.NanoDevice} objects
     */
    private class ViewHolder {
        private TextView nanoName;
        private TextView nanoMac;
        private TextView nanoRssi;
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        androidx.appcompat.app.ActionBar action_Bar = getSupportActionBar();
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
