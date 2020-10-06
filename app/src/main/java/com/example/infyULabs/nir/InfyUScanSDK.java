package com.example.infyULabs.nir;

/**
 * Created by iris.lin on 2017/12/12.
 */

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kstechnologies.nirscannanolibrary.SettingsManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class InfyUScanSDK {
    private static final String TAG = "InfyUScanSDK";
    public static BluetoothGatt mBluetoothGatt;
    public static BluetoothAdapter mBluetoothAdapter;
    public static final String ACTION_GATT_CONNECTED = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_NOTIFY_DONE = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_NOTIFY_DONE";
    public static final String EXTRA_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DATA";
    public static final String SEND_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SEND_DATA";
    public static final String SCAN_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SCAN_DATA";
    public static final String REF_CONF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.REF_CONF_DATA";
    public static final String SCAN_CONF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SCAN_CONF_DATA";
    public static final String STORED_SCAN_DATA = "com.isctechnologies.NanoScan.bluetooth.le.STORED_SCAN_DATA";
    public static final String EXTRA_REF_COEF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_COEF_DATA";
    public static final String EXTRA_REF_MATRIX_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_MATRIX_DATA";
    public static final String EXTRA_SCAN_NAME = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_NAME";
    public static final String EXTRA_SCAN_TYPE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_TYPE";
    public static final String EXTRA_SCAN_DATE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_DATE";
    public static final String EXTRA_SCAN_INDEX = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_INDEX";
    public static final String EXTRA_INDEX_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_INDEX_SIZE";
    public static final String EXTRA_CONF_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_CONF_SIZE";
    public static final String EXTRA_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_ACTIVE_CONF";
    public static final String EXTRA_SCAN_FMT_VER = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SCAN_FMT_VER";
    public static final String GET_INFO = "com.isctechnologies.NanoScan.bluetooth.le.GET_INFO";
    public static final String GET_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.GET_STATUS";
    public static final String GET_SCAN_CONF = "com.isctechnologies.NanoScan.bluetooth.le.GET_SCAN_CONF";
    public static final String GET_STORED_SCANS = "com.isctechnologies.NanoScan.bluetooth.le.GET_STORED_SCANS";
    public static final String SET_TIME = "com.isctechnologies.NanoScan.bluetooth.le.SET_TIME";
    public static final String START_SCAN = "com.isctechnologies.NanoScan.bluetooth.le.START_SCAN";
    public static final String DELETE_SCAN = "com.isctechnologies.NanoScan.bluetooth.le.DELETE_SCAN";
    public static final String SD_SCAN_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.SD_SCAN_SIZE";
    public static final String SCAN_CONF_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.SCAN_CONF_SIZE";
    public static final String GET_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.GET_ACTIVE_CONF";
    public static final String SET_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.SET_ACTIVE_CONF";
    public static final String SEND_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.SEND_ACTIVE_CONF";
    public static final String UPDATE_THRESHOLD = "com.isctechnologies.NanoScan.bluetooth.le.UPDATE_THRESHOLD";
    public static final String REQUEST_ACTIVE_CONF = "com.isctechnologies.NanoScan.bluetooth.le.REQUEST_ACTIVE_CONF";
    public static final String ACTION_INFO = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_INFO";
    public static final String ACTION_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_STATUS";
    public static final String EXTRA_MANUF_NAME = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_MANUF_NAME";
    public static final String EXTRA_MODEL_NUM = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_MODEL_NUM";
    public static final String EXTRA_SERIAL_NUM = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SERIAL_NUM";
    public static final String EXTRA_HW_REV = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_HW_REV";
    public static final String EXTRA_TIVA_REV = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_TIVA_REV";
    public static final String EXTRA_SPECTRUM_REV = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SPECTRUM_REV";
    public static final String EXTRA_BATT = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_BATT";
    public static final String EXTRA_TEMP = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_TEMP";
    public static final String EXTRA_HUMID = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_HUMID";
    public static final String EXTRA_DEV_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DEV_STATUS";
    public static final String EXTRA_DEV_STATUS_BYTE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DEV_STATUS_BYTE";
    public static final String EXTRA_ERR_STATUS = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_ERR_STATUS";
    public static final String EXTRA_ERR_BYTE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_ERR_BYTE";
    public static final String EXTRA_TEMP_THRESH = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_TEMP_THRESH";
    public static final String EXTRA_HUMID_THRESH = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_HUMID_THRESH";
    public static final String ACTION_REQ_CAL_COEFF = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_REQ_CAL_COEFF";
    public static final String ACTION_REQ_CAL_MATRIX = "com.isctechnologies.NanoScan.bluetooth.le.ACTION_REQ_CAL_MATRIX";
    public static final String EXTRA_REF_CAL_COEFF_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.REF_CAL_COEFF_SIZE";
    public static final String EXTRA_REF_CAL_MATRIX_SIZE = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_CAL_MATRIX_SIZE";
    public static final String EXTRA_REF_CAL_COEFF_SIZE_PACKET = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_CAL_COEFF_SIZE_PACKET";
    public static final String EXTRA_REF_CAL_MATRIX_SIZE_PACKET = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_REF_CAL_MATRIX_SIZE_PACKET";
    public static final String SCAN_MODE_ON_OFF = "scan.mode.on.off";
    public static final String LAMP_ON_OFF = "lamp.on.off";
    public static final String ACTION_LAMP = "action.lamp";
    public static final String PGA_SET = "pga.set";
    public static final String ACTION_PGA = "action.pga";
    public static final String REPEAT_SET = "repeat.set";
    public static final String ACTION_REPEAT = "action.repeat";
    public static final String LAMP_TIME = "lamp.time";
    public static final String ACTION_LAMP_TIME = "action.lamp.time";
    public static final String ACTION_SAVE_REFERENCE = "action.save.reference";
    public static final String EXTRA_SPEC_COEF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_SPEC_COEF_DATA";
    public static final String SPEC_CONF_DATA = "com.isctechnologies.NanoScan.bluetooth.le.SPEC_CONF_DATA";
    public static final String ACTION_ACTIVATE_STATE = "action.activate.state";
    public static final String ACTIVATE_STATE_KEY = "activate.state.key";
    public static final String ACTION_RETURN_ACTIVATE = "action.return.activate";
    public static final String RETURN_ACTIVATE_STATUS = "return.activate.status";
    public static final String ACTION_READ_ACTIVATE_STATE = "action.read.activate.state";
    public static final String ACTION_RETURN_READ_ACTIVATE_STATE = "action.return.read.activate.state";
    public static final String RETURN_READ_ACTIVATE_STATE = "return.read.activate.state";
    public static final String ACTION_READ_CONFIG = "action.read.config";
    public static final String READ_CONFIG_DATA = "read.cureent.config.data"; // read current config in device(quickse or default)
    public static final String RETURN_CURRENT_CONFIG_DATA = "active.return.current.config.data";
    public static final String EXTRA_CURRENT_CONFIG_DATA = "com.extra.current.config.data";
    public static final String ACTION_WRITE_SCAN_CONFIG = "action.write.scan.config";
    public static final String WRITE_SCAN_CONFIG_VALUE = "write.scan.config.value";
    public static final String ACTION_RETURN_WRITE_SCAN_CONFIG_STATUS = "action.return.write.scan.config.status";
    public static final String RETURN_WRITE_SCAN_CONFIG_STATUS = "return.write.scan.config.status";
    public static final String GET_UUID = "com.isctechnologies.NanoScan.bluetooth.le.GET_UUID";
    public static final String SEND_DEVICE_UUID = "com.isctechnologies.NanoScan.bluetooth.le.SEND_DEVICE_UUID";
    public static final String EXTRA_DEVICE_UUID = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_DEVICE_UUID";
    public static final String GET_BATTERY = "com.isctechnologies.NanoScan.bluetooth.le.GET_BATTERY";
    public static final String SEND_BATTERY = "com.isctechnologies.NanoScan.bluetooth.le.SEND_BATTERY";
    public static final String EXTRA_BATTERY = "com.isctechnologies.NanoScan.bluetooth.le.EXTRA_BATTERY";
    public static final String CLEAR_ERROR_STATUS = "action.clear.error.status";

    public InfyUScanSDK() {
    }

    public static native Object dlpSpecScanInterpReference(byte[] var0, byte[] var1, byte[] var2);

    public static native Object dlpSpecScanReadConfiguration(byte[] var0);

    public static InfyUScanSDK.ScanResults KSTNanoSDK_dlpSpecScanInterpReference(byte[] data, byte[] coeff, byte[] matrix) {
        return (InfyUScanSDK.ScanResults)dlpSpecScanInterpReference(data, coeff, matrix);
    }

    public static InfyUScanSDK.ScanConfiguration KSTNanoSDK_dlpSpecScanReadConfiguration(byte[] data) {
        return (InfyUScanSDK.ScanConfiguration)dlpSpecScanReadConfiguration(data);
    }

//    private static boolean characteristicError() {
//        if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISHardwareRev == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.DIS_HW_REV.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISManufName == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.DIS_MANUF_NAME.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISModelNumber == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.DIS_MODEL_NUMBER.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISSerialNumber == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.DIS_SERIAL_NUMBER.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISSpectrumCRev == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.DIS_SPECC_REV.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISTivaFirmwareRev == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.DIS_TIVA_FW_REV.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharBASBattLevel == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.BAS_BATT_LVL.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISBatteryRechargeCycles == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_NUM_BATT_RECHARGE.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISDevStatus == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_DEV_STATUS.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorStatus == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_ERR_STATUS.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHoursOfUse == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_HOURS_OF_USE.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidMeasurement == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_HUMID_MEASUREMENT.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidThreshold == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_HUMID_THRESH.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISLampHours == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_LAMP_HOURS.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempMeasurement == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_TEMP_MEASUREMENT.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempThreshold == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GGIS_TEMP_THRESH.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGDTSTime == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GDTS_TIME.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalCoefficients == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GCIS_REQ_REF_CAL_COEFF.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalMatrix == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GCIS_REQ_REF_CAL_MATRIX.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqSpecCalCoefficients == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GCIS_REQ_SPEC_CAL_COEFF.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GCIS_RET_REF_CAL_COEFF.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GCIS_RET_REF_CAL_MATRIX.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSCIS_ACTIVE_SCAN_CONF.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISNumberStoredConf == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSCIS_NUM_STORED_CONF.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqScanConfData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSCIS_REQ_SCAN_CONF_DATA.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqStoredConfList == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSCIS_REQ_STORED_CONF_LIST.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSCIS_RET_SCAN_CONF_DATA.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSCIS_RET_STORED_CONF_LIST.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISNumberSDStoredScans == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_NUM_SD_STORED_SCANS.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesList == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST_DATA.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSetScanNameStub == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_SET_SCAN_NAME_STUB.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanWrite == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_START_SCAN.toString() + " (Write)");
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_START_SCAN.toString() + " (Notify)");
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanWrite == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_CLEAR_SCAN.toString() + " (Write)");
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_CLEAR_SCAN.toString() + " (Notify)");
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanName == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_REQ_SCAN_NAME.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_RET_SCAN_NAME.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanType == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_REQ_SCAN_TYPE.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_RET_SCAN_TYPE.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanDate == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_REQ_SCAN_DATE.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_RET_SCAN_DATE.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqPacketFormatVersion == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_REQ_PKT_FMT_VER.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_RET_PKT_FMT_VER.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqSerialScanDataStruct == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_REQ_SER_SCAN_DATA_STRUCT.toString());
//            return true;
//        } else if (InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + InfyUScanSDK.NanoGATT.GSDIS_RET_SER_SCAN_DATA_STRUCT.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.GSDIS_ACTIVATE_STATE.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.GSDIS_RETURN_ACTIVATE_STATE.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharacteristicReadCurrentScanConfigurationData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.GSDIS_READ_CURRENT_SCANCONFIG_DATA.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.GSDIS_RETURN_CURRENT_SCANCONFIG_DATA.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharacteristicWriteScanConfigurationData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.GSDIS_WRITE_SCANCONFIG_DATA.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.GSDIS_RETURN_WRITE_SCANCONFIG_DATA.toString());
//            return true;
//        } else if (NanoGattCharacteristic.mBleGattCharUUID == null) {
//            Log.e("InfyUScanSDK", "Failed to enumerate UUID:" + NanoGATT.DEVICE_UUID.toString());
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static void initialize() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean enumerateServices(BluetoothGatt gatt) {
        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
        Iterator var2 = gattServices.iterator();

        while (var2.hasNext()) {
            BluetoothGattService gattService = (BluetoothGattService) var2.next();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            Iterator var5 = gattCharacteristics.iterator();

            while (var5.hasNext()) {
                BluetoothGattCharacteristic gattCharacteristic = (BluetoothGattCharacteristic) var5.next();
                if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.DIS_MANUF_NAME) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISManufName = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.DIS_MODEL_NUMBER) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISModelNumber = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.DIS_SERIAL_NUMBER) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISSerialNumber = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.DIS_HW_REV) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISHardwareRev = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.DIS_TIVA_FW_REV) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISTivaFirmwareRev = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.DIS_SPECC_REV) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISSpectrumCRev = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.BAS_BATT_LVL) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharBASBattLevel = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_TEMP_MEASUREMENT) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempMeasurement = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_HUMID_MEASUREMENT) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidMeasurement = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_DEV_STATUS) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISDevStatus = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_ERR_STATUS) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorStatus = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_TEMP_THRESH) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempThreshold = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_HUMID_THRESH) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidThreshold = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_HOURS_OF_USE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHoursOfUse = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_NUM_BATT_RECHARGE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISBatteryRechargeCycles = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_LAMP_HOURS) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISLampHours = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GGIS_ERR_LOG) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorLog = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GDTS_TIME) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGDTSTime = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GCIS_REQ_SPEC_CAL_COEFF) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqSpecCalCoefficients = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GCIS_RET_SPEC_CAL_COEFF) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetSpecCalCoefficients = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetSpecCalCoefficients, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GCIS_REQ_REF_CAL_COEFF) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalCoefficients = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GCIS_RET_REF_CAL_COEFF) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalCoefficients, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GCIS_REQ_REF_CAL_MATRIX) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalMatrix = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GCIS_RET_REF_CAL_MATRIX) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISRetRefCalMatrix, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSCIS_NUM_STORED_CONF) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISNumberStoredConf = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSCIS_REQ_STORED_CONF_LIST) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqStoredConfList = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSCIS_RET_STORED_CONF_LIST) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetStoredConfList, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSCIS_REQ_SCAN_CONF_DATA) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqScanConfData = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSCIS_RET_SCAN_CONF_DATA) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISRetScanConfData, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSCIS_ACTIVE_SCAN_CONF) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_NUM_SD_STORED_SCANS) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISNumberSDStoredScans = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesList = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_SD_STORED_SCAN_IND_LIST_DATA) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesListData, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_SET_SCAN_NAME_STUB) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSetScanNameStub = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_START_SCAN) == 0) {
                    if (gattCharacteristic.getProperties() == 8) {
                        InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanWrite = gattCharacteristic;
                    } else if (gattCharacteristic.getProperties() == 16) {
                        InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify = gattCharacteristic;
                        gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanNotify, true);
                    }
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_CLEAR_SCAN) == 0) {
                    if (gattCharacteristic.getProperties() == 8) {
                        InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanWrite = gattCharacteristic;
                    } else if (gattCharacteristic.getProperties() == 16) {
                        InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify = gattCharacteristic;
                        // gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanNotify, true);
                    }
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_REQ_SCAN_NAME) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanName = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_RET_SCAN_NAME) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanName, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_REQ_SCAN_TYPE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanType = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_RET_SCAN_TYPE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanType, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_REQ_SCAN_DATE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanDate = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_RET_SCAN_DATE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetScanDate, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_REQ_PKT_FMT_VER) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqPacketFormatVersion = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_RET_PKT_FMT_VER) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetPacketFormatVersion, true);
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_REQ_SER_SCAN_DATA_STRUCT) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqSerialScanDataStruct = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(InfyUScanSDK.NanoGATT.GSDIS_RET_SER_SCAN_DATA_STRUCT) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISRetSerialScanDataStruct, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_ACTIVATE_STATE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_RETURN_ACTIVATE_STATE) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateStateNotify, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_READ_CURRENT_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicReadCurrentScanConfigurationData = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_RETURN_CURRENT_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnCurrentScanConfigurationData, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_WRITE_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicWriteScanConfigurationData = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_RETURN_WRITE_SCANCONFIG_DATA) == 0) {
                    NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData = gattCharacteristic;
                    gatt.setCharacteristicNotification(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData, true);
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.DEVICE_UUID) == 0) {
                    NanoGattCharacteristic.mBleGattCharUUID = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_LAMP_MODE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicLampMode = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_LAMP_DELAY_TIME) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicLampDelayTime = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_SET_PGA) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicSetPGA = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_SET_SCAN_AVERAGE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicSetScanAverage = gattCharacteristic;
                } else if (gattCharacteristic.getUuid().compareTo(NanoGATT.GSDIS_SAVE_REFERENCE) == 0) {
                    InfyUScanSDK.NanoGattCharacteristic.mBleGattCharacteristicSaveReference = gattCharacteristic;
                }
            }
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setStub(byte[] data) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSetScanNameStub, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void startScan(byte[] saveToSD) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISStartScanWrite, saveToSD);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setTime() {
        Calendar currentTime = Calendar.getInstance();
        @SuppressLint("WrongConstant") byte[] dateBytes = new byte[]{(byte) (currentTime.get(1) - 2000), (byte) (currentTime.get(2) + 1), (byte) currentTime.get(5), (byte) (currentTime.get(7) - 1), (byte) currentTime.get(11), (byte) currentTime.get(12), (byte) currentTime.get(13)};
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGDTSTime, dateBytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setTemperatureThreshold(byte[] threshold) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempThreshold, threshold);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setHumidityThreshold(byte[] threshold) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidThreshold, threshold);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setActiveConf(byte[] index) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf, index);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void getActiveConf() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISActiveScanConf);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void deleteScan(byte[] index) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISClearScanWrite, index);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void getModelNumber() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISModelNumber);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void getSerialNumber() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISSerialNumber);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void getHardwareRev() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISHardwareRev);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getFirmwareRev() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISTivaFirmwareRev);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getSpectrumCRev() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISSpectrumCRev);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getTemp() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISTempMeasurement);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getHumidity() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISHumidMeasurement);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void getManufacturerName() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharDISManufName);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getUUID() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharUUID);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getBatteryLevel() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharBASBattLevel);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getNumberStoredConfigurations() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISNumberStoredConf);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void getNumberStoredScans() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISNumberSDStoredScans);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getDeviceStatus() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISDevStatus);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void getErrorStatus() {
        readCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGGISErrorStatus);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestStoredConfigurationList() {
        byte[] writeData = new byte[]{0};
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqStoredConfList, writeData);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestScanName(byte[] index) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanName, index);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestScanConfiguration(byte[] index) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSCISReqScanConfData, index);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void requestRefCalCoefficients() {
        byte[] data = new byte[]{0};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            writeCharacteristic(NanoGattCharacteristic.mBleGattCharGCISReqRefCalCoefficients, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void requestRefCalMatrix() {
        byte[] data = new byte[]{0};
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqRefCalMatrix, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void requestSpectrumCalCoefficients() {
        byte[] data = new byte[]{0};
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGCISReqSpecCalCoefficients, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestScanIndicesList() {
        byte[] writeData = new byte[]{0};
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISSDStoredScanIndicesList, writeData);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestScanDate(byte[] scanIndex) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanDate, scanIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestScanType(byte[] scanIndex) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqScanType, scanIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestPacketFormatVersion(byte[] scanIndex) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqPacketFormatVersion, scanIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void requestSerializedScanDataStruct(byte[] scanIndex) {
        writeCharacteristic(InfyUScanSDK.NanoGattCharacteristic.mBleGattCharGSDISReqSerialScanDataStruct, scanIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setPGA(byte[] index) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }
        ;
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicSetPGA, index);
        System.out.println("__BT_SERVICE setPGA");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setLampTime(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicLampDelayTime, index);
        System.out.println("__BT_SERVICE setLampTime");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setLampMode(byte[] index) {

        try {
            Thread.sleep(200);
        } catch (Exception e) {

        }
        ;
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicLampMode, index);
        System.out.println("__BT_SERVICE setLampMode");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void setScanAverage(byte[] index) {

        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }
        ;
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicSetScanAverage, index);
        System.out.println("__BT_SERVICE setScanAverage");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void SaveReference(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicSaveReference, index);
        System.out.println("__BT_SERVICE SaveReference");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void writeScanConfig(byte[] index) {

        try {
            Thread.sleep(200);
        } catch (Exception e) {

        }
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicWriteScanConfigurationData, index);
        System.out.println("__BT_SERVICE writeScanConfig");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void SetActiveStateKey(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState, index);
        System.out.println("__BT_SERVICE: Set ActivateState key");
        if (index.length == 12) {
            Boolean unactivate = true;
            for (int i = 0; i < 12; i++) {
                if (index[i] != 0) {
                    unactivate = false;
                }
            }
            if (unactivate) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {

                }
                ReadActiveState();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void ClearErrorStatus() {
        byte[] writeData = new byte[]{0};
        writeCharacteristic(NanoGattCharacteristic.mBleGattCharGGISErrorStatus, writeData);
        System.out.println("__BT_SERVICE: Clear Error Status");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void ReadCurrentConfig(byte[] index) {

        writeCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicReadCurrentScanConfigurationData, index);
        System.out.println("__BT_SERVICE: ReadCurrentConfig");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void ReadScanConfigDataStatus() {
        readCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicReturnWriteScanConfigurationData);
        System.out.println("__BT_SERVICE: ReadScanConfigDataStatus");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void ReadActiveState() {
        readCharacteristic(NanoGattCharacteristic.mBleGattCharacteristicCharacteristicActivateState);
        System.out.println("__BT_SERVICE: ReadActiveState");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (characteristic == null) {
            Log.e("InfyUScanSDK", "Error writing NULL characteristic");
        } else {
            characteristic.setValue(data);
            if (mBluetoothAdapter != null && mBluetoothGatt != null) {
                mBluetoothGatt.writeCharacteristic(characteristic);
            } else {
                Log.e("InfyUScanSDK", "ERROR: mBluetoothAdapter is null");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter != null && mBluetoothGatt != null) {
            if (characteristic != null) {
                mBluetoothGatt.readCharacteristic(characteristic);
            } else {
                Log.e("InfyUScanSDK", "ERROR: Reading NULL characteristic");
            }

        }
    }

    public static class SlewScanSection implements Serializable {
        byte sectionScanType;
        byte widthPx;
        int wavelengthStartNm;
        int wavelengthEndNm;
        int numPatterns;
        int numRepeats;
        int exposureTime;

        public SlewScanSection(byte sectionScanType, byte widthPx, int wavelengthStartNm, int wavelengthEndNm, int numPatterns, int numRepeats, int exposureTime) {
            this.sectionScanType = sectionScanType;
            this.widthPx = widthPx;
            this.wavelengthStartNm = wavelengthStartNm;
            this.wavelengthEndNm = wavelengthEndNm;
            this.numPatterns = numPatterns;
            this.numRepeats = numRepeats;
            this.exposureTime = exposureTime;
        }

        public int getExposureTime() {
            return this.exposureTime;
        }

        public void setExposureTime(int exposureTime) {
            this.exposureTime = exposureTime;
        }

        public int getNumPatterns() {
            return this.numPatterns;
        }

        public void setNumPatterns(int numPatterns) {
            this.numPatterns = numPatterns;
        }

        public int getNumRepeats() {
            return this.numRepeats;
        }

        public void setNumRepeats(int numRepeats) {
            this.numRepeats = numRepeats;
        }

        public byte getSectionScanType() {
            return this.sectionScanType;
        }

        public void setSectionScanType(byte sectionScanType) {
            this.sectionScanType = sectionScanType;
        }

        public int getWavelengthEndNm() {
            return this.wavelengthEndNm;
        }

        public void setWavelengthEndNm(int wavelengthEndNm) {
            this.wavelengthEndNm = wavelengthEndNm;
        }

        public int getWavelengthStartNm() {
            return this.wavelengthStartNm;
        }

        public void setWavelengthStartNm(int wavelengthStartNm) {
            this.wavelengthStartNm = wavelengthStartNm;
        }

        public byte getWidthPx() {
            return this.widthPx;
        }

        public void setWidthPx(byte widthPx) {
            this.widthPx = widthPx;
        }
    }

    public static class ScanListManager {
        private String infoTitle;
        private String infoBody;

        public ScanListManager(String infoTitle, String infoBody) {
            this.infoTitle = infoTitle;
            this.infoBody = infoBody;
        }

        public String getInfoTitle() {
            return this.infoTitle;
        }

        public String getInfoBody() {
            return this.infoBody;
        }
    }

    public static class ScanConfiguration implements Serializable {
        private static final int SCAN_CFG_FILENAME_LEN = 8;
        int scanType;
        int scanConfigIndex;
        byte[] scanConfigSerialNumber;
        byte[] configName;
        int wavelengthStartNm;
        int wavelengthEndNm;
        int widthPx;
        int numPatterns;
        int numRepeats;
        boolean active;
        byte[] sectionScanType;
        byte[] sectionWidthPx;
        int[] sectionWavelengthStartNm;
        int[] sectionWavelengthEndNm;
        int[] sectionNumPatterns;
        int[] sectionNumRepeats;
        int[] sectionExposureTime;
        byte numSections;

        public ScanConfiguration(int scanType, int scanConfigIndex, byte[] scanConfigSerialNumber, byte[] configName, int wavelengthStartNm, int wavelengthEndNm, int widthPx, int numPatterns, int numRepeats) {
            this.scanType = scanType;
            this.scanConfigIndex = scanConfigIndex;
            this.scanConfigSerialNumber = scanConfigSerialNumber;
            this.configName = configName;
            this.wavelengthStartNm = wavelengthStartNm;
            this.wavelengthEndNm = wavelengthEndNm;
            this.widthPx = widthPx;
            this.numPatterns = numPatterns;
            this.numRepeats = numRepeats;
        }

        public ScanConfiguration(byte[] scanConfigSerialNumber) {
            this.scanType = scanType;
            this.scanConfigIndex = scanConfigIndex;
            this.scanConfigSerialNumber = scanConfigSerialNumber;
            this.configName = configName;
            this.sectionScanType = sectionScanType;
            this.sectionWidthPx = sectionWidthPx;
            this.sectionWavelengthStartNm = sectionWavelengthStartNm;
            this.sectionWavelengthEndNm = sectionWavelengthEndNm;
            this.sectionNumPatterns = sectionNumPatterns;
            this.sectionNumRepeats = sectionNumRepeats;
            this.sectionExposureTime = sectionExposureTime;
            this.numSections = numSections;
        }

        public String getScanType() {
            return this.scanType == 1 ? "Hadamard" : (this.scanType == 2 ? "Slew" : "Column");
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setScanType(int scanType) {
            this.scanType = scanType;
        }

        public int getScanConfigIndex() {
            return this.scanConfigIndex;
        }

        public void setScanConfigIndex(int scanConfigIndex) {
            this.scanConfigIndex = scanConfigIndex;
        }

        public String getScanConfigSerialNumber() {
            String s = null;

            try {
                s = new String(this.scanConfigSerialNumber, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
            }

            return s;
        }

        public void setScanConfigSerialNumber(byte[] scanConfigSerialNumber) {
            this.scanConfigSerialNumber = scanConfigSerialNumber;
        }

        public String getConfigName() {
            byte[] byteChars = new byte[40];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] var3 = byteChars;
            int i = byteChars.length;

            for (int var5 = 0; var5 < i; ++var5) {
                byte b = var3[var5];
                byteChars[b] = 0;
            }

            String s = null;

            for (i = 0; i < this.configName.length; ++i) {
                byteChars[i] = this.configName[i];
                if (this.configName[i] == 0) {
                    break;
                }

                os.write(this.configName[i]);
            }

            try {
                s = new String(os.toByteArray(), "UTF-8");
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
            }

            return s;
        }

        public void setConfigName(byte[] configName) {
            this.configName = configName;
        }

        public int getWavelengthStartNm() {
            return this.wavelengthStartNm;
        }

        public void setWavelengthStartNm(int wavelengthStartNm) {
            this.wavelengthStartNm = wavelengthStartNm;
        }

        public int getWavelengthEndNm() {
            return this.wavelengthEndNm;
        }

        public void setWavelengthEndNm(int wavelengthEndNm) {
            this.wavelengthEndNm = wavelengthEndNm;
        }

        public int getWidthPx() {
            return this.widthPx;
        }

        public void setWidthPx(int widthPx) {
            this.widthPx = widthPx;
        }

        public int getNumPatterns() {
            return this.numPatterns;
        }

        public void setNumPatterns(int numPatterns) {
            this.numPatterns = numPatterns;
        }

        public int getNumRepeats() {
            return this.numRepeats;
        }

        public void setNumRepeats(int numRepeats) {
            this.numRepeats = numRepeats;
        }

        public int[] getSectionExposureTime() {
            return this.sectionExposureTime;
        }

        public void setSectionExposureTime(int[] sectionExposureTime) {
            this.sectionExposureTime = sectionExposureTime;
        }

        public int[] getSectionNumPatterns() {
            return this.sectionNumPatterns;
        }

        public void setSectionNumPatterns(int[] sectionNumPatterns) {
            this.sectionNumPatterns = sectionNumPatterns;
        }

        public int[] getSectionNumRepeats() {
            return this.sectionNumRepeats;
        }

        public void setSectionNumRepeats(int[] sectionNumRepeats) {
            this.sectionNumRepeats = sectionNumRepeats;
        }

        public byte[] getSectionScanType() {
            return this.sectionScanType;
        }

        public void setSectionScanType(byte[] sectionScanType) {
            this.sectionScanType = sectionScanType;
        }

        public int[] getSectionWavelengthEndNm() {
            return this.sectionWavelengthEndNm;
        }

        public void setSectionWavelengthEndNm(int[] sectionWavelengthEndNm) {
            this.sectionWavelengthEndNm = sectionWavelengthEndNm;
        }

        public int[] getSectionWavelengthStartNm() {
            return this.sectionWavelengthStartNm;
        }

        public void setSectionWavelengthStartNm(int[] sectionWavelengthStartNm) {
            this.sectionWavelengthStartNm = sectionWavelengthStartNm;
        }

        public byte[] getSectionWidthPx() {
            return this.sectionWidthPx;
        }

        public void setSectionWidthPx(byte[] sectionWidthPx) {
            this.sectionWidthPx = sectionWidthPx;
        }

        public byte getSlewNumSections() {
            return this.numSections;
        }

        public void setSlewNumSections(byte numSections) {
            this.numSections = numSections;
        }
    }

    public static class ScanResults {
        double[] wavelength;

        int[] intensity;
        int[] uncalibratedIntensity;
        int length;

        public double[] getWavelength() {
            return this.wavelength;
        }

        public void setWavelength(double[] wavelength) {
            this.wavelength = wavelength;
        }

        public int[] getIntensity() {
            return this.intensity;
        }

        public void setIntensity(int[] intensity) {
            this.intensity = intensity;
        }

        public int getLength() {
            return this.length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int[] getUncalibratedIntensity() {
            return this.uncalibratedIntensity;
        }

        public void setUncalibratedIntensity(int[] uncalibratedIntensity) {
            this.uncalibratedIntensity = uncalibratedIntensity;
        }

        public ScanResults(double[] wavelength, int[] intensity, int[] uncalibratedIntensity, int length) {
            this.wavelength = wavelength;
            this.intensity = intensity;
            this.uncalibratedIntensity = uncalibratedIntensity;
            this.length = length;
        }

        public static double getSpatialFreq(Context ctx, double wavelength) {
            return SettingsManager.getBooleanPref(ctx, "spatialFreq", true) ? wavelength : 1.0E7D / wavelength;
        }
    }

    public static class ReferenceCalibration implements Serializable {
        public static final String REF_FILENAME = "refcals";
        public static ArrayList<ReferenceCalibration> currentCalibration;
        private byte[] refCalCoefficients;
        private byte[] refCalMatrix;

        public ReferenceCalibration(byte[] refCalCoefficients, byte[] refCalMatrix) {
            this.refCalCoefficients = refCalCoefficients;
            this.refCalMatrix = refCalMatrix;
        }

        public static boolean refCalFileExists(Context context) {
            ObjectInputStream in;
            try {
                FileInputStream fis = context.openFileInput("refcals");
                in = new ObjectInputStream(fis);
            } catch (IOException var5) {
                var5.printStackTrace();
                return false;
            }

            try {
                in.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return true;
        }

        public byte[] getRefCalCoefficients() {
            return this.refCalCoefficients;
        }

        public byte[] getRefCalMatrix() {
            return this.refCalMatrix;
        }

        public static boolean writeRefCalFile(Context context, ArrayList<ReferenceCalibration> list) {
            currentCalibration = list;
            ObjectOutputStream out = null;

            try {
                FileOutputStream fos = context.openFileOutput("refcals", 0);
                out = new ObjectOutputStream(fos);
                Iterator var4 = list.iterator();

                while (var4.hasNext()) {
                    InfyUScanSDK.ReferenceCalibration tempObject = (InfyUScanSDK.ReferenceCalibration) var4.next();
                    out.writeObject(tempObject);
                }

                out.close();
                return true;
            } catch (IOException var7) {
                var7.printStackTrace();
                Log.e("__REFS", "IO exception when writing groups file: " + var7);

                try {
                    assert out != null;

                    out.close();
                } catch (IOException var6) {
                    var6.printStackTrace();
                    Log.e("__REFS", "IO exception when closing groups file: " + var6);
                }

                return false;
            }
        }
    }

    public static class NanoDevice {
        private BluetoothDevice device;
        private int rssi;
        byte[] scanRecord;
        private String nanoName;
        private String nanoMac;

        public NanoDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.device = device;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
            this.nanoName = device.getName();
            this.nanoMac = device.getAddress();
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public String getRssiString() {
            return String.valueOf(this.rssi);
        }

        public String getNanoName() {
            return this.nanoName;
        }

        public String getNanoMac() {
            return this.nanoMac;
        }
    }

    public static class NanoGattCharacteristic {
        public static BluetoothGattCharacteristic mBleGattCharDISManufName;
        public static BluetoothGattCharacteristic mBleGattCharDISModelNumber;
        public static BluetoothGattCharacteristic mBleGattCharDISSerialNumber;
        public static BluetoothGattCharacteristic mBleGattCharDISHardwareRev;
        public static BluetoothGattCharacteristic mBleGattCharDISTivaFirmwareRev;
        public static BluetoothGattCharacteristic mBleGattCharDISSpectrumCRev;
        public static BluetoothGattCharacteristic mBleGattCharBASBattLevel;
        public static BluetoothGattCharacteristic mBleGattCharGGISTempMeasurement;
        public static BluetoothGattCharacteristic mBleGattCharGGISHumidMeasurement;
        public static BluetoothGattCharacteristic mBleGattCharGGISDevStatus;
        public static BluetoothGattCharacteristic mBleGattCharGGISErrorStatus;
        public static BluetoothGattCharacteristic mBleGattCharGGISTempThreshold;
        public static BluetoothGattCharacteristic mBleGattCharGGISHumidThreshold;
        public static BluetoothGattCharacteristic mBleGattCharGGISHoursOfUse;
        public static BluetoothGattCharacteristic mBleGattCharGGISBatteryRechargeCycles;
        public static BluetoothGattCharacteristic mBleGattCharGGISLampHours;
        public static BluetoothGattCharacteristic mBleGattCharGGISErrorLog;
        public static BluetoothGattCharacteristic mBleGattCharGDTSTime;
        public static BluetoothGattCharacteristic mBleGattCharGCISReqSpecCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISRetSpecCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISReqRefCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISRetRefCalCoefficients;
        public static BluetoothGattCharacteristic mBleGattCharGCISReqRefCalMatrix;
        public static BluetoothGattCharacteristic mBleGattCharGCISRetRefCalMatrix;
        public static BluetoothGattCharacteristic mBleGattCharGSCISNumberStoredConf;
        public static BluetoothGattCharacteristic mBleGattCharGSCISReqStoredConfList;
        public static BluetoothGattCharacteristic mBleGattCharGSCISRetStoredConfList;
        public static BluetoothGattCharacteristic mBleGattCharGSCISReqScanConfData;
        public static BluetoothGattCharacteristic mBleGattCharGSCISRetScanConfData;
        public static BluetoothGattCharacteristic mBleGattCharGSCISActiveScanConf;
        public static BluetoothGattCharacteristic mBleGattCharGSDISNumberSDStoredScans;
        public static BluetoothGattCharacteristic mBleGattCharGSDISSDStoredScanIndicesList;
        public static BluetoothGattCharacteristic mBleGattCharGSDISSDStoredScanIndicesListData;
        public static BluetoothGattCharacteristic mBleGattCharGSDISSetScanNameStub;
        public static BluetoothGattCharacteristic mBleGattCharGSDISStartScanWrite;
        public static BluetoothGattCharacteristic mBleGattCharGSDISStartScanNotify;
        public static BluetoothGattCharacteristic mBleGattCharGSDISClearScanWrite;
        public static BluetoothGattCharacteristic mBleGattCharGSDISClearScanNotify;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqScanName;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetScanName;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqScanType;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetScanType;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqScanDate;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetScanDate;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqPacketFormatVersion;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetPacketFormatVersion;
        public static BluetoothGattCharacteristic mBleGattCharGSDISReqSerialScanDataStruct;
        public static BluetoothGattCharacteristic mBleGattCharGSDISRetSerialScanDataStruct;
        public static BluetoothGattCharacteristic mBleGattCharacteristicCharacteristicActivateState;
        public static BluetoothGattCharacteristic mBleGattCharacteristicCharacteristicActivateStateNotify;
        public static BluetoothGattCharacteristic mBleGattCharacteristicReadCurrentScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharacteristicReturnCurrentScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharacteristicWriteScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharacteristicReturnWriteScanConfigurationData;
        public static BluetoothGattCharacteristic mBleGattCharUUID;
        public static BluetoothGattCharacteristic mBleGattCharacteristicLampMode;
        public static BluetoothGattCharacteristic mBleGattCharacteristicLampDelayTime;
        public static BluetoothGattCharacteristic mBleGattCharacteristicSetPGA;
        public static BluetoothGattCharacteristic mBleGattCharacteristicSetScanAverage;
        public static BluetoothGattCharacteristic mBleGattCharacteristicSaveReference;

        public NanoGattCharacteristic() {
        }
    }

    public static class NanoGATT {
        public static final UUID DIS_MANUF_NAME = UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_MODEL_NUMBER = UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_HW_REV = UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_TIVA_FW_REV = UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB");
        public static final UUID DIS_SPECC_REV = UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB");
        public static final UUID BAS_BATT_LVL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
        public static final UUID GGIS_TEMP_MEASUREMENT = UUID.fromString("43484101-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_HUMID_MEASUREMENT = UUID.fromString("43484102-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_DEV_STATUS = UUID.fromString("43484103-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_ERR_STATUS = UUID.fromString("43484104-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_TEMP_THRESH = UUID.fromString("43484105-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_HUMID_THRESH = UUID.fromString("43484106-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_HOURS_OF_USE = UUID.fromString("43484107-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_NUM_BATT_RECHARGE = UUID.fromString("43484108-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_LAMP_HOURS = UUID.fromString("43484109-444C-5020-4E49-52204E616E6F");
        public static final UUID GGIS_ERR_LOG = UUID.fromString("4348410A-444C-5020-4E49-52204E616E6F");
        public static final UUID GDTS_TIME = UUID.fromString("4348410C-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_REQ_SPEC_CAL_COEFF = UUID.fromString("4348410D-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_RET_SPEC_CAL_COEFF = UUID.fromString("4348410E-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_REQ_REF_CAL_COEFF = UUID.fromString("4348410F-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_RET_REF_CAL_COEFF = UUID.fromString("43484110-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_REQ_REF_CAL_MATRIX = UUID.fromString("43484111-444C-5020-4E49-52204E616E6F");
        public static final UUID GCIS_RET_REF_CAL_MATRIX = UUID.fromString("43484112-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_NUM_STORED_CONF = UUID.fromString("43484113-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_REQ_STORED_CONF_LIST = UUID.fromString("43484114-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_RET_STORED_CONF_LIST = UUID.fromString("43484115-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_REQ_SCAN_CONF_DATA = UUID.fromString("43484116-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_RET_SCAN_CONF_DATA = UUID.fromString("43484117-444C-5020-4E49-52204E616E6F");
        public static final UUID GSCIS_ACTIVE_SCAN_CONF = UUID.fromString("43484118-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_NUM_SD_STORED_SCANS = UUID.fromString("43484119-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SD_STORED_SCAN_IND_LIST = UUID.fromString("4348411A-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SD_STORED_SCAN_IND_LIST_DATA = UUID.fromString("4348411B-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SET_SCAN_NAME_STUB = UUID.fromString("4348411C-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_START_SCAN = UUID.fromString("4348411D-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_CLEAR_SCAN = UUID.fromString("4348411E-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SCAN_NAME = UUID.fromString("4348411F-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SCAN_NAME = UUID.fromString("43484120-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SCAN_TYPE = UUID.fromString("43484121-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SCAN_TYPE = UUID.fromString("43484122-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SCAN_DATE = UUID.fromString("43484123-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SCAN_DATE = UUID.fromString("43484124-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_PKT_FMT_VER = UUID.fromString("43484125-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_PKT_FMT_VER = UUID.fromString("43484126-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_REQ_SER_SCAN_DATA_STRUCT = UUID.fromString("43484127-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RET_SER_SCAN_DATA_STRUCT = UUID.fromString("43484128-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_ACTIVATE_STATE = UUID.fromString("43484130-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RETURN_ACTIVATE_STATE = UUID.fromString("43484131-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_READ_CURRENT_SCANCONFIG_DATA = UUID.fromString("43484140-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RETURN_CURRENT_SCANCONFIG_DATA = UUID.fromString("43484141-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_WRITE_SCANCONFIG_DATA = UUID.fromString("43484142-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_RETURN_WRITE_SCANCONFIG_DATA = UUID.fromString("43484143-444C-5020-4E49-52204E616E6F");
        public static final UUID DEVICE_UUID = UUID.fromString("00002A23-0000-1000-8000-00805F9B34FB");
        public static final UUID GSDIS_LAMP_MODE = UUID.fromString("43484144-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_LAMP_DELAY_TIME = UUID.fromString("43484145-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SET_PGA = UUID.fromString("43484146-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SET_SCAN_AVERAGE = UUID.fromString("43484147-444C-5020-4E49-52204E616E6F");
        public static final UUID GSDIS_SAVE_REFERENCE = UUID.fromString("43484132-444C-5020-4E49-52204E616E6F");

        public NanoGATT() {
        }
    }
}

