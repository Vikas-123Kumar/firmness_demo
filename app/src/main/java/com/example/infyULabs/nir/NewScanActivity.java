package com.example.infyULabs.nir;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.infyULabs.API_URL;
import com.example.infyULabs.dataAnalysis.MainActivity;
import com.github.mikephil.chartingNIR.animationNIR.EasingNIR;
import com.github.mikephil.chartingNIR.chartsNIR.LineChartNIRNIR;
import com.github.mikephil.chartingNIR.componentsNIR.Legend;
import com.github.mikephil.chartingNIR.componentsNIR.LimitLine;
import com.github.mikephil.chartingNIR.componentsNIR.XAxis;
import com.github.mikephil.chartingNIR.componentsNIR.YAxis;
import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;
import com.github.mikephil.chartingNIR.dataNIR.LineDataNIR;
import com.github.mikephil.chartingNIR.dataNIR.LineDataSetNIR;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.infyULabs.R;
import com.example.infyULabs.service.NanoBLEService;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;
import com.kstechnologies.nirscannanolibrary.SettingsManager;
import com.opencsv.CSVWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity controlling the Nano once it is connected
 * This activity allows a user to initiate a scan, as well as access other "connection-only"
 * settings. When first launched, the app will scan for a preferred device
 * for {@link NanoBLEService#SCAN_PERIOD}, if it is not found, then it will start another "open"
 * scan for any Nano.
 * <p>
 * If a preferred Nano has not been set, it will start a single scan. If at the end of scanning, a
 * Nano has not been found, a message will be presented to the user indicating and error, and the
 * activity will finish
 * <p>
 * WARNING: This activity uses JNI function calls for communicating with the Spectrum C library, It
 * is important that the name and file structure of this activity remain unchanged, or the functions
 * will NOT work
 *
 * @author collinmast
 */
public class NewScanActivity extends AppCompatActivity {

    private static Context mContext;

    private ProgressDialog barProgressDialog;

    private ViewPager mViewPager;

    private String fileName;
    private ArrayList<String> mXValues;
    private ArrayList<EntryNIR> mIntensityFloat;
    private ArrayList<EntryNIR> mAbsorbanceFloat;
    private ArrayList<EntryNIR> mReflectanceFloat;
    private ArrayList<Float> mWavelengthFloat;

    private final BroadcastReceiver scanDataReadyReceiver = new scanDataReadyReceiver();
    private final BroadcastReceiver refReadyReceiver = new refReadyReceiver();
    private final BroadcastReceiver notifyCompleteReceiver = new notifyCompleteReceiver();
    private final BroadcastReceiver scanStartedReceiver = new ScanStartedReceiver();
    private final BroadcastReceiver requestCalCoeffReceiver = new requestCalCoeffReceiver();
    private final BroadcastReceiver requestCalMatrixReceiver = new requestCalMatrixReceiver();
    private final BroadcastReceiver disconnReceiver = new DisconnReceiver();

    private final IntentFilter scanDataReadyFilter = new IntentFilter(KSTNanoSDK.SCAN_DATA);
    private final IntentFilter refReadyFilter = new IntentFilter(KSTNanoSDK.REF_CONF_DATA);
    private final IntentFilter notifyCompleteFilter = new IntentFilter(KSTNanoSDK.ACTION_NOTIFY_DONE);
    private final IntentFilter requestCalCoeffFilter = new IntentFilter(KSTNanoSDK.ACTION_REQ_CAL_COEFF);
    private final IntentFilter requestCalMatrixFilter = new IntentFilter(KSTNanoSDK.ACTION_REQ_CAL_MATRIX);
    private final IntentFilter disconnFilter = new IntentFilter(KSTNanoSDK.ACTION_GATT_DISCONNECTED);
    private final IntentFilter scanStartedFilter = new IntentFilter(NanoBLEService.ACTION_SCAN_STARTED);

    private final BroadcastReceiver scanConfReceiver = new ScanConfReceiver();
    private final IntentFilter scanConfFilter = new IntentFilter(KSTNanoSDK.SCAN_CONF_DATA);
    String url;
    private ProgressBar calProgress;
    private KSTNanoSDK.ScanResults results;
    private ToggleButton btn_os;
    private ToggleButton btn_continuous;
    private Button btn_scan;
    RequestQueue rQueue;
    String fruitName;
    private NanoBLEService mNanoBLEService;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private static final String DEVICE_NAME = "NIRScanNano";
    private boolean connected;
    private AlertDialog alertDialog;
    private TextView tv_scan_conf;
    private String preferredDevice;
    private LinearLayout ll_conf;
    private KSTNanoSDK.ScanConfiguration activeConf;
    private TextView brixData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scan);

        mContext = this;

        calProgress = (ProgressBar) findViewById(R.id.calProgress);
        calProgress.setVisibility(View.VISIBLE);
        connected = false;
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //Set the filename from the intent
        Intent intent = getIntent();
        fileName = intent.getStringExtra("file_name");
        fruitName = getIntent().getStringExtra("fruit_name");
        brixData = findViewById(R.id.briText);
        //Set up action bar enable tab navigation
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.new_scan));
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            mViewPager.setOffscreenPageLimit(2);

            // Create a tab listener that is called when the user changes tabs.
            ActionBar.TabListener tl = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }
            };

            // Add 3 tabs, specifying the tab's text and TabListener
            for (int i = 0; i < 3; i++) {
                ab.addTab(
                        ab.newTab()
                                .setText(getResources().getStringArray(R.array.graph_tab_index)[i])
                                .setTabListener(tl));
            }
        }

        //Set up UI elements and event handlers
        btn_os = (ToggleButton) findViewById(R.id.btn_saveOS);
        btn_continuous = (ToggleButton) findViewById(R.id.btn_continuous);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_os.setChecked(SettingsManager.getBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.saveOS, false));
        btn_continuous.setChecked(SettingsManager.getBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.continuousScan, false));

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(KSTNanoSDK.START_SCAN));
                calProgress.setVisibility(View.VISIBLE);
                btn_scan.setText(getString(R.string.scanning));
            }
        });

        btn_scan.setClickable(false);

        //Bind to the service. This will start it, and call the start command function
        Intent gattServiceIntent = new Intent(this, NanoBLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //Register all needed broadcast receivers
        LocalBroadcastManager.getInstance(mContext).registerReceiver(scanDataReadyReceiver, scanDataReadyFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(refReadyReceiver, refReadyFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(notifyCompleteReceiver, notifyCompleteFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(requestCalCoeffReceiver, requestCalCoeffFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(requestCalMatrixReceiver, requestCalMatrixFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(disconnReceiver, disconnFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(scanConfReceiver, scanConfFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(scanStartedReceiver, scanStartedFilter);
        setupToolbar();
    }


    /*
     * When the activity is destroyed, unregister all broadcast receivers, remove handler callbacks,
     * and store all user preferences
     */

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

    @Override
    public void onResume() {
        super.onResume();

        //Initialize view pager
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.invalidate();

//        tv_scan_conf.setText(SettingsManager.getStringPref(mContext, SettingsManager.SharedPreferencesKeys.scanConfiguration, "Column 1"));

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        ActionBar ab = getActionBar();
                        if (ab != null) {
                            getActionBar().setSelectedNavigationItem(position);
                        }
                    }
                });

        mXValues = new ArrayList<>();
        mIntensityFloat = new ArrayList<>();
        mAbsorbanceFloat = new ArrayList<>();
        mReflectanceFloat = new ArrayList<>();
        mWavelengthFloat = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(scanDataReadyReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(refReadyReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(notifyCompleteReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(requestCalCoeffReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(requestCalMatrixReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(disconnReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(scanConfReceiver);

        mHandler.removeCallbacksAndMessages(null);

        SettingsManager.storeBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.saveOS, btn_os.isChecked());
        SettingsManager.storeBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.continuousScan, btn_continuous.isChecked());
    }
    /**
     * Pager enum to control tab tile and layout resource
     */

    /**
     * Custom pager adapter to handle changing chart data when pager tabs are changed
     */


    /**
     * Custom enum for chart type
     */
    public enum ChartType {
        INTENSITY,
        REFLECTANCE,
        ABSORBANCE
    }

    /**
     *
     * Custom receiver for handling scan data and setting up the graphs properly
     */

    /**
     * Custom receiver for returning the event that reference calibrations have been read
     */
    public class refReadyReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            byte[] refCoeff = intent.getByteArrayExtra(KSTNanoSDK.EXTRA_REF_COEF_DATA);
            byte[] refMatrix = intent.getByteArrayExtra(KSTNanoSDK.EXTRA_REF_MATRIX_DATA);
            Log.e("file name coeff", refCoeff[2] + "");
            Log.e("file name matrix", refMatrix.length + "");

            ArrayList<KSTNanoSDK.ReferenceCalibration> refCal = new ArrayList<>();

            refCal.add(new KSTNanoSDK.ReferenceCalibration(refCoeff, refMatrix));
            Log.e("file name", refCal.size() + "value" + refCal.toString());

            KSTNanoSDK.ReferenceCalibration.writeRefCalFile(mContext, refCal);
            calProgress.setVisibility(View.GONE);
        }
    }

    /**
     * Custom receiver for returning the event that a scan has been initiated from the button
     */
    public class ScanStartedReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            calProgress.setVisibility(View.VISIBLE);
            btn_scan.setText(getString(R.string.scanning));
        }
    }

    public enum CustomPagerEnum {
        INTENSITY(R.string.intensity, R.layout.page_graph_intensity),
        REFLECTANCE(R.string.reflectance, R.layout.page_graph_reflectance),
        ABSORBANCE(R.string.absorbance, R.layout.page_graph_absorbance);


        private final int mTitleResId;
        private final int mLayoutResId;

        CustomPagerEnum(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

    }

    /**
     * Custom pager adapter to handle changing chart data when pager tabs are changed
     */
    public class CustomPagerAdapter extends PagerAdapter {

        private final Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
            collection.addView(layout);

            if (customPagerEnum.getLayoutResId() == R.layout.page_graph_intensity) {
                LineChartNIRNIR mChart = (LineChartNIRNIR) layout.findViewById(R.id.lineChartInt);
                mChart.setDrawGridBackground(false);

                // no description text
                mChart.setDescription("");

                // enable touch gestures
                mChart.setTouchEnabled(true);

                // enable scaling and dragging
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);

                // if disabled, scaling can be done on x- and y-axis separately
                mChart.setPinchZoom(true);

                // x-axis limit line
                LimitLine llXAxis = new LimitLine(10f, "Index 10");
                llXAxis.setLineWidth(4f);
                llXAxis.enableDashedLine(10f, 10f, 0f);
                llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                llXAxis.setTextSize(10f);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setEnabled(false);
                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                leftAxis.setEnabled(false);
                mChart.setAutoScaleMinMaxEnabled(true);

                leftAxis.setStartAtZero(true);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);

                leftAxis.setDrawLimitLinesBehindData(true);

                mChart.getAxisRight().setEnabled(false);

                // add data
                setData(mChart, mXValues, mIntensityFloat, ChartType.INTENSITY);

                mChart.animateX(2500, EasingNIR.EasingOption.EaseInOutQuart);

                // get the legend (only possible after setting data)
                Legend l = mChart.getLegend();

                // modify the legend ...
                l.setForm(Legend.LegendForm.LINE);
                mChart.getLegend().setEnabled(false);
                return layout;
            } else if (customPagerEnum.getLayoutResId() == R.layout.page_graph_absorbance) {

                LineChartNIRNIR mChart = (LineChartNIRNIR) layout.findViewById(R.id.lineChartAbs);
                mChart.setDrawGridBackground(false);

                // no description text
                mChart.setDescription("");

                // enable touch gestures
                mChart.setTouchEnabled(true);

                // enable scaling and dragging
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);

                // if disabled, scaling can be done on x- and y-axis separately
                mChart.setPinchZoom(true);

                // x-axis limit line
                LimitLine llXAxis = new LimitLine(10f, "Index 10");
                llXAxis.setLineWidth(4f);
                llXAxis.enableDashedLine(10f, 10f, 0f);
                llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                llXAxis.setTextSize(10f);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

                mChart.setAutoScaleMinMaxEnabled(true);

                leftAxis.setStartAtZero(false);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);

                // limit lines are drawn behind data (and not on top)
                leftAxis.setDrawLimitLinesBehindData(true);

                mChart.getAxisRight().setEnabled(false);


                // add data
                setData(mChart, mXValues, mAbsorbanceFloat, ChartType.ABSORBANCE);

                mChart.animateX(2500, EasingNIR.EasingOption.EaseInOutQuart);

                // get the legend (only possible after setting data)
                Legend l = mChart.getLegend();

                // modify the legend ...
                l.setForm(Legend.LegendForm.LINE);
                mChart.getLegend().setEnabled(false);

                return layout;
            } else if (customPagerEnum.getLayoutResId() == R.layout.page_graph_reflectance) {

                LineChartNIRNIR mChart = (LineChartNIRNIR) layout.findViewById(R.id.lineChartRef);
                mChart.setDrawGridBackground(false);

                // no description text
                mChart.setDescription("");

                // enable touch gestures
                mChart.setTouchEnabled(true);

                // enable scaling and dragging
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);

                // if disabled, scaling can be done on x- and y-axis separately
                mChart.setPinchZoom(true);

                // x-axis limit line
                LimitLine llXAxis = new LimitLine(10f, "Index 10");
                llXAxis.setLineWidth(4f);
                llXAxis.enableDashedLine(10f, 10f, 0f);
                llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                llXAxis.setTextSize(10f);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

                mChart.setAutoScaleMinMaxEnabled(true);

                leftAxis.setStartAtZero(false);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);

                // limit lines are drawn behind data (and not on top)
                leftAxis.setDrawLimitLinesBehindData(true);

                mChart.getAxisRight().setEnabled(false);


                // add data
                setData(mChart, mXValues, mReflectanceFloat, ChartType.REFLECTANCE);

                mChart.animateX(2500, EasingNIR.EasingOption.EaseInOutQuart);

                // get the legend (only possible after setting data)
                Legend l = mChart.getLegend();

                // modify the legend ...
                l.setForm(Legend.LegendForm.LINE);
                mChart.getLegend().setEnabled(false);
                return layout;
            } else {
                return layout;
            }
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.intensity);
                case 1:
                    return getString(R.string.absorbance);
                case 2:
                    return getString(R.string.reflectance);
            }
            return null;
        }

    }

    private void setData(LineChartNIRNIR mChart, ArrayList<String> xValues, ArrayList<EntryNIR> yValues, ChartType type) {

        if (type == ChartType.REFLECTANCE) {
            // create a dataset and give it a type
            LineDataSetNIR set1 = new LineDataSetNIR(yValues, fileName);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.RED);
            set1.setLineWidth(1f);
            set1.setCircleSize(3f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.RED);
            set1.setDrawFilled(true);

            ArrayList<LineDataSetNIR> dataSets = new ArrayList<LineDataSetNIR>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineDataNIR data = new LineDataNIR(xValues, dataSets);

            // set data
            mChart.setData(data);

            mChart.setMaxVisibleValueCount(20);
        } else if (type == ChartType.ABSORBANCE) {
            // create a dataset and give it a type
            LineDataSetNIR set1 = new LineDataSetNIR(yValues, fileName);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.GREEN);
            set1.setLineWidth(1f);
            set1.setCircleSize(3f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.GREEN);
            set1.setDrawFilled(true);

            ArrayList<LineDataSetNIR> dataSets = new ArrayList<LineDataSetNIR>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineDataNIR data = new LineDataNIR(xValues, dataSets);

            // set data
            mChart.setData(data);

            mChart.setMaxVisibleValueCount(20);
        } else if (type == ChartType.INTENSITY) {
            // create a dataset and give it a type
            LineDataSetNIR set1 = new LineDataSetNIR(yValues, fileName);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLUE);
            set1.setLineWidth(1f);
            set1.setCircleSize(3f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.BLUE);
            set1.setDrawFilled(true);

            ArrayList<LineDataSetNIR> dataSets = new ArrayList<LineDataSetNIR>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineDataNIR data = new LineDataNIR(xValues, dataSets);

            // set data
            mChart.setData(data);

            mChart.setMaxVisibleValueCount(20);
        } else {
            // create a dataset and give it a type
            LineDataSetNIR set1 = new LineDataSetNIR(yValues, fileName);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleSize(3f);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.BLACK);
            set1.setDrawFilled(true);

            ArrayList<LineDataSetNIR> dataSets = new ArrayList<LineDataSetNIR>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineDataNIR data = new LineDataNIR(xValues, dataSets);

            // set data
            mChart.setData(data);

            mChart.setMaxVisibleValueCount(10);
        }
    }

    /**
     * Custom enum for chart type
     */

    public class scanDataReadyReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onReceive(Context context, Intent intent) {
            calProgress.setVisibility(View.GONE);
            btn_scan.setText(getString(R.string.scan));
            byte[] scanData = intent.getByteArrayExtra(KSTNanoSDK.EXTRA_DATA);
            String scanType = intent.getStringExtra(KSTNanoSDK.EXTRA_SCAN_TYPE);
            /*
             * 7 bytes representing the current data
             * byte0: uint8_t     year; //< years since 2000
             * byte1: uint8_t     month; /**< months since January [0-11]
             * byte2: uint8_t     day; /**< day of the month [1-31]
             * byte3: uint8_t     day_of_week; /**< days since Sunday [0-6]
             * byte3: uint8_t     hour; /**< hours since midnight [0-23]
             * byte5: uint8_t     minute; //< minutes after the hour [0-59]
             * byte6: uint8_t     second; //< seconds after the minute [0-60]
             */
            String scanDate = intent.getStringExtra(KSTNanoSDK.EXTRA_SCAN_DATE);

            KSTNanoSDK.ReferenceCalibration ref = KSTNanoSDK.ReferenceCalibration.currentCalibration.get(0);
            results = KSTNanoSDK.KSTNanoSDK_dlpSpecScanInterpReference(scanData, ref.getRefCalCoefficients(), ref.getRefCalMatrix());
            dataFormateForServer(results);
            mXValues.clear();
            mIntensityFloat.clear();
            mAbsorbanceFloat.clear();
            mReflectanceFloat.clear();
            mWavelengthFloat.clear();

            int index;
            for (index = 0; index < results.getLength(); index++) {
                mXValues.add(String.format("%.02f", KSTNanoSDK.ScanResults.getSpatialFreq(mContext, results.getWavelength()[index])));
                mIntensityFloat.add(new EntryNIR((float) results.getUncalibratedIntensity()[index], index));
                mAbsorbanceFloat.add(new EntryNIR((-1) * (float) Math.log10((double) results.getUncalibratedIntensity()[index] / (double) results.getIntensity()[index]), index));
                mReflectanceFloat.add(new EntryNIR((float) results.getUncalibratedIntensity()[index] / results.getIntensity()[index], index));
                mWavelengthFloat.add((float) results.getWavelength()[index]);
            }
            Log.e("mintensity", mIntensityFloat.size() + "");
            Log.e("mintensity", mIntensityFloat + "");

            float minWavelength = mWavelengthFloat.get(0);
            float maxWavelength = mWavelengthFloat.get(0);

            for (Float f : mWavelengthFloat) {
                if (f < minWavelength) minWavelength = f;
                if (f > maxWavelength) maxWavelength = f;
            }

            float minAbsorbance = mAbsorbanceFloat.get(0).getVal();
            float maxAbsorbance = mAbsorbanceFloat.get(0).getVal();

            for (EntryNIR e : mAbsorbanceFloat) {
                if (e.getVal() < minAbsorbance) minAbsorbance = e.getVal();
                if (e.getVal() > maxAbsorbance) maxAbsorbance = e.getVal();
            }

            float minReflectance = mReflectanceFloat.get(0).getVal();
            float maxReflectance = mReflectanceFloat.get(0).getVal();

            for (EntryNIR e : mReflectanceFloat) {
                if (e.getVal() < minReflectance) minReflectance = e.getVal();
                if (e.getVal() > maxReflectance) maxReflectance = e.getVal();
            }

            float minIntensity = mIntensityFloat.get(0).getVal();
            float maxIntensity = mIntensityFloat.get(0).getVal();

            for (EntryNIR e : mIntensityFloat) {
                if (e.getVal() < minIntensity) minIntensity = e.getVal();
                if (e.getVal() > maxIntensity) maxIntensity = e.getVal();
            }

            mViewPager.setAdapter(mViewPager.getAdapter());
            mViewPager.invalidate();

            if (scanType.equals("00")) {
                scanType = "Column 1";
            } else {
                scanType = "Hadamard";
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyhhmmss", java.util.Locale.getDefault());
            String ts = simpleDateFormat.format(new Date());

            boolean saveOS = btn_os.isChecked();
            boolean continuous = btn_continuous.isChecked();

            writeCSV(ts, results, saveOS);
//            writeCSVDict(ts, scanType, scanDate, String.valueOf(minWavelength), String.valueOf(maxWavelength), String.valueOf(results.getLength()), String.valueOf(results.getLength()), "1", "2.00", saveOS);
            if (continuous) {
                calProgress.setVisibility(View.VISIBLE);
                btn_scan.setText(getString(R.string.scanning));
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(KSTNanoSDK.SEND_DATA));
            }
        }
    }

    private void writeCSV(String currentTime, KSTNanoSDK.ScanResults scanResults, boolean saveOS) {
        Log.e("fruitname", fruitName);
        if (saveOS) {
            String csvOS = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fruitName + currentTime + ".csv";

            CSVWriter writer;
            try {
                writer = new CSVWriter(new FileWriter(csvOS), ',', CSVWriter.NO_QUOTE_CHARACTER);
                List<String[]> data = new ArrayList<String[]>();
                data.add(new String[]{"Wavelength,Intensity,Absorbance,Reflectance"});

                int csvIndex;
                for (csvIndex = 0; csvIndex < scanResults.getLength(); csvIndex++) {
                    double waves = scanResults.getWavelength()[csvIndex];
                    int intens = scanResults.getUncalibratedIntensity()[csvIndex];
                    float absorb = (-1) * (float) Math.log10((double) scanResults.getUncalibratedIntensity()[csvIndex] / (double) scanResults.getIntensity()[csvIndex]);
                    float reflect = (float) results.getUncalibratedIntensity()[csvIndex] / results.getIntensity()[csvIndex];
                    data.add(new String[]{String.valueOf(waves), String.valueOf(intens), String.valueOf(absorb), String.valueOf(reflect)});
                }

                writer.writeAll(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dataFormateForServer(KSTNanoSDK.ScanResults scanResults) {
        int csvIndex;
        List list = new ArrayList();
        for (csvIndex = 0; csvIndex < scanResults.getLength(); csvIndex++) {
            float absorb = (-1) * (float) Math.log10((double) scanResults.getUncalibratedIntensity()[csvIndex] / (double) scanResults.getIntensity()[csvIndex]);

            list.add(String.valueOf(absorb));
        }
        Log.e("list", list + "");
        String absorbance = String.join(" ", list);
        ;
        dataToServer(absorbance);

    }

    public void dataToServer(String receivedData) {
        SharedPreferences prefs = getSharedPreferences("userrecord", MODE_PRIVATE);
        String token = prefs.getString("token", "No name defined");//"No n
        JSONObject jsonObject = new JSONObject();
        Log.e("token", token);
        try {
            jsonObject.put("reading", receivedData.trim());
//            jsonObject.put("name", fruitName.toLowerCase());
//            jsonObject.put("token", token);
            Log.e("json", jsonObject + "");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL.nir_url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("response", response + "");
                            try {
                                String Brix = response.optString("brix");
                                String acidity = response.optString("ph");
                                String firmness_level = response.optString("firmness_level");
                                String firmness = response.optString("firmness");
                                String waterCore = response.optString("watercore");
                                String starch = response.optString("starch");
                                brixData.setText("Brix : " + Brix + "\n\npH : " + acidity + "\n\nFirmness Level : " + firmness_level
                                        + "\n\nFirmness : " + firmness + "\n\nStarch : " + starch
                                        + "\n\nWatercore : " + waterCore);
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
            rQueue = Volley.newRequestQueue(NewScanActivity.this);
            rQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("JSONObject Here", e.toString());
        }
    }

    /**
     * Custom receiver that will request the time once all of the GATT notifications have been
     * subscribed to
     */
    public class notifyCompleteReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(KSTNanoSDK.SET_TIME));
        }
    }

    /**
     * Write scan data to CSV file
     * @param currentTime the current time to save
     * @param scanResults the {@link KSTNanoSDK.ScanResults} structure to save
     * @param saveOS boolean indicating if the CSV file should be saved to the OS
     */

    /**
     * Write the dictionary for a CSV files
     *
     * @param currentTime the current time to be saved
     * @param scanType the scan type to be saved
     * @param timeStamp the timestamp to be saved
     * @param spectStart the spectral range start
     * @param spectEnd the spectral range end
     * @param numPoints the number of data points
     * @param resolution the scan resolution
     * @param numAverages the number of scans to average
     * @param measTime the total measurement time
     * @param saveOS boolean indicating if this file should be saved to the OS
     */


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            //Get a reference to the service from the service connection
            mNanoBLEService = ((NanoBLEService.LocalBinder) service).getService();

            //initialize bluetooth, if BLE is not available, then finish
            if (!mNanoBLEService.initialize()) {
                finish();
            }

            //Start scanning for devices that match DEVICE_NAME
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (mBluetoothLeScanner == null) {
                finish();
                Toast.makeText(NewScanActivity.this, "Please ensure Bluetooth is enabled and try again", Toast.LENGTH_SHORT).show();
            }
            mHandler = new Handler();
            if (SettingsManager.getStringPref(mContext, SettingsManager.SharedPreferencesKeys.preferredDevice, null) != null) {
                preferredDevice = SettingsManager.getStringPref(mContext, SettingsManager.SharedPreferencesKeys.preferredDevice, null);
                scanPreferredLeDevice(true);
            } else {
                scanLeDevice(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mNanoBLEService = null;
        }
    };

    /**
     * Callback function for Bluetooth scanning. This function provides the instance of the
     * Bluetooth device {@link BluetoothDevice} that was found, it's rssi, and advertisement
     * data (scanRecord).
     * <p>
     * When a Bluetooth device with the advertised name matching the
     * string DEVICE_NAME {@link NewScanActivity#DEVICE_NAME} is found, a call is made to connect
     * to the device. Also, the Bluetooth should stop scanning, even if
     * the {@link NanoBLEService#SCAN_PERIOD} has not expired
     */
    private final ScanCallback mLeScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            if (name != null) {
                if (device.getName().equals(DEVICE_NAME)) {
                    mNanoBLEService.connect(device.getAddress());
                    connected = true;
                    scanLeDevice(false);
                }
            }
        }
    };

    /**
     * Callback function for preferred Nano scanning. This function provides the instance of the
     * Bluetooth device {@link BluetoothDevice} that was found, it's rssi, and advertisement
     * data (scanRecord).
     * <p>
     * When a Bluetooth device with the advertised name matching the
     * string DEVICE_NAME {@link NewScanActivity#DEVICE_NAME} is found, a call is made to connect
     * to the device. Also, the Bluetooth should stop scanning, even if
     * the {@link NanoBLEService#SCAN_PERIOD} has not expired
     */
    private final ScanCallback mPreferredLeScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            if (name != null) {
                if (device.getName().equals(DEVICE_NAME)) {
                    if (device.getAddress().equals(preferredDevice)) {
                        mNanoBLEService.connect(device.getAddress());
                        connected = true;
                        scanPreferredLeDevice(false);
                    }
                }
            }
        }
    };

    /**
     * Scans for Bluetooth devices on the specified interval {@link NanoBLEService#SCAN_PERIOD}.
     * This function uses the handler {@link NewScanActivity#mHandler} to delay call to stop
     * scanning until after the interval has expired. The start and stop functions take an
     * LeScanCallback parameter that specifies the callback function when a Bluetooth device
     * has been found {@link NewScanActivity#mLeScanCallback}
     *
     * @param enable Tells the Bluetooth adapter {@link KSTNanoSDK#mBluetoothAdapter} if
     *               it should start or stop scanning
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    if (mBluetoothLeScanner != null) {
                        mBluetoothLeScanner.stopScan(mLeScanCallback);
                        if (!connected) {
                            notConnectedDialog();
                        }
                    }
                }
            }, NanoBLEService.SCAN_PERIOD);
            if (mBluetoothLeScanner != null) {
                mBluetoothLeScanner.startScan(mLeScanCallback);
            } else {
                finish();
                Toast.makeText(NewScanActivity.this, "Please ensure Bluetooth is enabled and try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    /**
     * Scans for preferred Nano devices on the specified interval {@link NanoBLEService#SCAN_PERIOD}.
     * This function uses the handler {@link NewScanActivity#mHandler} to delay call to stop
     * scanning until after the interval has expired. The start and stop functions take an
     * LeScanCallback parameter that specifies the callback function when a Bluetooth device
     * has been found {@link NewScanActivity#mPreferredLeScanCallback}
     *
     * @param enable Tells the Bluetooth adapter {@link KSTNanoSDK#mBluetoothAdapter} if
     *               it should start or stop scanning
     */
    private void scanPreferredLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(mPreferredLeScanCallback);
                    if (!connected) {

                        scanLeDevice(true);
                    }
                }
            }, NanoBLEService.SCAN_PERIOD);
            if (mBluetoothLeScanner != null) {
                mBluetoothLeScanner.startScan(mPreferredLeScanCallback);
            }
        } else {
            mBluetoothLeScanner.stopScan(mPreferredLeScanCallback);
        }
    }

    /**
     * Dialog that tells the user that a Nano is not connected. The activity will finish when the
     * user selects ok
     */
    private void notConnectedDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.not_connected_title));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.not_connected_message));

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    /**
     * Custom receiver for receiving calibration coefficient data.
     */
    public class requestCalCoeffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(KSTNanoSDK.EXTRA_REF_CAL_COEFF_SIZE, 0);
            Boolean size = intent.getBooleanExtra(KSTNanoSDK.EXTRA_REF_CAL_COEFF_SIZE_PACKET, false);
            if (size) {
                calProgress.setVisibility(View.INVISIBLE);
                barProgressDialog = new ProgressDialog(NewScanActivity.this);
                Log.e("data cresh", "in if");

                barProgressDialog.setTitle(getString(R.string.dl_ref_cal));
                barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                barProgressDialog.setProgress(0);
                barProgressDialog.setMax(intent.getIntExtra(KSTNanoSDK.EXTRA_REF_CAL_COEFF_SIZE, 0));
                barProgressDialog.setCancelable(false);
                barProgressDialog.show();
            } else {
                Log.e("data cresh", "not cresh");
                barProgressDialog.setProgress(barProgressDialog.getProgress() + intent.getIntExtra(KSTNanoSDK.EXTRA_REF_CAL_COEFF_SIZE, 0));
            }
        }
    }


    /**
     * Custom receiver for receiving calibration matrix data. When this receiver action complete, it
     * will request the active configuration so that it can be displayed in the listview
     */
    public class requestCalMatrixReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(KSTNanoSDK.EXTRA_REF_CAL_MATRIX_SIZE, 0);
            Boolean size = intent.getBooleanExtra(KSTNanoSDK.EXTRA_REF_CAL_MATRIX_SIZE_PACKET, false);
            if (size) {
                barProgressDialog.dismiss();
                barProgressDialog = new ProgressDialog(NewScanActivity.this);

                barProgressDialog.setTitle(getString(R.string.dl_cal_matrix));
                barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                barProgressDialog.setProgress(0);
                barProgressDialog.setMax(intent.getIntExtra(KSTNanoSDK.EXTRA_REF_CAL_MATRIX_SIZE, 0));
                barProgressDialog.setCancelable(false);
                barProgressDialog.show();
            } else {
                barProgressDialog.setProgress(barProgressDialog.getProgress() + intent.getIntExtra(KSTNanoSDK.EXTRA_REF_CAL_MATRIX_SIZE, 0));
            }
            if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(KSTNanoSDK.REQUEST_ACTIVE_CONF));
            }
        }
    }

    /**
     * Custom receiver for handling scan configurations
     */
    private class ScanConfReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] smallArray = intent.getByteArrayExtra(KSTNanoSDK.EXTRA_DATA);
            Log.e("small array size", smallArray.length + "");
            byte[] addArray = new byte[smallArray.length * 3];
            byte[] largeArray = new byte[smallArray.length + addArray.length];

            System.arraycopy(smallArray, 0, largeArray, 0, smallArray.length);
            System.arraycopy(addArray, 0, largeArray, smallArray.length, addArray.length);

            Log.w("_JNI", "largeArray Size: " + largeArray.length);
            KSTNanoSDK.ScanConfiguration scanConf = KSTNanoSDK.KSTNanoSDK_dlpSpecScanReadConfiguration(intent.getByteArrayExtra(KSTNanoSDK.EXTRA_DATA));
            //KSTNanoSDK.ScanConfiguration scanConf = KSTNanoSDK.KSTNanoSDK_dlpSpecScanReadConfiguration(largeArray);

//            activeConf = scanConf;

            barProgressDialog.dismiss();
            btn_scan.setClickable(true);

//            SettingsManager.storeStringPref(mContext, SettingsManager.SharedPreferencesKeys.scanConfiguration, scanConf.getConfigName());
//            tv_scan_conf.setText(scanConf.getConfigName());


        }
    }

    /**
     * Broadcast Receiver handling the disconnect event. If the Nano disconnects,
     * this activity should finish so that the user is taken back to the {@link com.example.infyULabs.nir.NirConnection}
     * and display a toast message
     */
    public class DisconnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(mContext, R.string.nano_disconnected, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
