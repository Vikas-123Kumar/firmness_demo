package com.example.infyULabs;

import android.graphics.Color;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.dataAnalysis.MainActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

class CommentCode {

    //for connect bluetooth device in uv and triad

//    public boolean BTconnect() {
//        // making the connection of device to your mobile
//        if (device != null) {
//            boolean connected = false;
//            try {
//                Log.e("device name e", device + "");
//
//                socket = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
//                final GlobalClass globalClass = (GlobalClass) getApplicationContext();
//                if (!socket.isConnected()) {
//                    globalClass.setSocket(device);
//                    socket.connect();
//                    itemForNavigation.setEnabled(false);
//                    Log.e("device name", socket.isConnected() + "");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("error", e.getMessage() + "");
//
//                connected = true;
//            }
//            return connected;
//        }
//        return false;
//    }

// after alertdialog dismiss in main activity.

    //                        BTconnect();

//                        if (socket.isConnected()) {
//                            try {
//                                boolean status = false;
//                                status = true;
//                                if (status) {
//                                    GlobalClass.showToast("Device Connected");
//                                    itemForNavigation.setTitle("Connected");
//                                    pbar.setVisibility(View.GONE);
////                                    socket.close();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Toast toast1 = Toast.makeText(getApplicationContext(), "Device not connected", Toast.LENGTH_LONG);
//                            toast1.show();
//                            pbar.setVisibility(View.GONE);
//
//                        }

// write and read data from bluetooth device


//        if (socket != null && !socket.isConnected()) {
//            try {
//                final GlobalClass globalClass = (GlobalClass) getApplicationContext();
//                BluetoothDevice device = globalClass.getSocket();
//                socket = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
//                Log.e("socket value", device.toString() + "");
//                socket.connect();
//                Log.e("item name", itemForNavigation.getItemId() + "");
//                Log.e("socket value", socket.isConnected() + "");
//                scanConnected = true;
//            } catch (Exception e) {
//                pbar.setVisibility(View.GONE);
//                Log.e("error", e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        if (socket != null) {
//            if (socket.isConnected()) {
//                try {
//                    outputStream = socket.getOutputStream();
//                    Log.e("device  after connected", "" + socket.isConnected());
//                } catch (IOException e) {
//                    Log.e("exception1", e.getMessage());
//                    e.printStackTrace();
//                }
//                try {
//                    inputStream = socket.getInputStream();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("exception2", e.getMessage());
//                }
//                outputStream.write(string.getBytes());
////                beginListenForData();
//                itemForNavigation.setEnabled(false);
//            }
//        } else {
//            GlobalClass.showToast("Please Connect To UV Nano Device");
//        }


    // for piechart with color shorter


//    public void openUrlForYData(String receivedData) {
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("spectrum", receivedData.trim());
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AppleUrl, jsonObject,
//                    new Response.Listener<JSONObject>() {
//
//                        @Override
//                        public void onResponse(JSONObject jsonObject) {
//                            try {
//                                int r = Integer.parseInt(jsonObject.getString("r"));
//                                int g = Integer.parseInt(jsonObject.getString("g"));
//                                int b = Integer.parseInt(jsonObject.getString("b"));
//
//                                Log.e("Response from server", r + "");
//                                entriesData = new ArrayList<>();
//                                entriesData.add(new SliceValue((float) 2.0, Color.parseColor(jsonObject.getString("hex"))).setLabel(jsonObject.getString("hex")));
//                                PieChartData pieChartData = new PieChartData(entriesData);
//                                if (pieChartView != null) {
//                                    pieChartData.setHasLabels(true);
//                                    pieChartData.setHasCenterCircle(false);
//                                    pieChartView.setPieChartData(pieChartData);
//                                }
//                            } catch (Exception ex) {
//                                Log.e("Value of jsonObject", ex.getMessage() + "");
//                                ex.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    Log.e("error", volleyError.toString());
//
//                }
//            });
//            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
//                @Override
//                public int getCurrentTimeout() {
//                    return 50000;
//                }
//
//                @Override
//                public int getCurrentRetryCount() {
//                    return 50000;
//                }
//
//                @Override
//                public void retry(VolleyError error) throws VolleyError {
//
//                }
//            });
//            rQueue = Volley.newRequestQueue(MainActivity.this);
//            rQueue.add(jsonObjectRequest);
//        } catch (Exception e) {
//            Log.e("JSONObject Here", e.toString());
//        }
//    }


    // read data from socket using inputstream.

    //                        Log.e("scan", "1");
//                        int byteCount = inputStream.read(buffer);
//                        Log.e("scan", "11");
//                        String string = new String(buffer, 0, byteCount, "UTF-8");
//                        Log.e("byte", byteCount + "");


    // uuid for connecting bluetooth device

//    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
//    boolean scanConnected = false;

}




