package com.example.infyULabs.userProfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.API_URL;
import com.example.infyULabs.ChooseTheData;
import com.example.infyULabs.Connection;
import com.example.infyULabs.GlobalClass;
import com.example.infyULabs.R;
import com.example.infyULabs.SessionHandler;
import com.example.infyULabs.dataAnalysis.MainActivity;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.example.infyULabs.nir.NewScanActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_EMPTY = "";
    DataBaseHelper databaseHelper;
    SharedPreferences SM;
    private String username;
    private String password;
    private EditText etUsername, etPassword;
    private Button go;
    private ProgressDialog barProgressDialog;
    CheckBox checkBox;
    private TextView signup, forgetpassword, responsetext;
    Context context;

    //username=infyu1076;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        SM = getSharedPreferences("userrecord", 0);
        Boolean islogin = SM.getBoolean("userlogin", false);
//        signup = (TextView) findViewById(R.id.sign_up);
//        checkBox = (CheckBox) findViewById(R.id.check);
        forgetpassword = (TextView) findViewById(R.id.forgot_password);
        databaseHelper = new DataBaseHelper(this);
        barProgressDialog = new ProgressDialog(LoginActivity.this);

        go = (Button) findViewById(R.id.login);
        if (islogin) {
            startActivity(new Intent(getApplicationContext(), ChooseTheData.class));
            return;
        }
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ChooseTheData.class));
                if (validateInputs() && isConnected()) {
                    login();
//                    Log.e("name", username + "pass" + password);
//                    if (databaseHelper.checkUser(username, password)) {
//                        SharedPreferences.Editor edit = SM.edit();
//                        edit.putBoolean("userlogin", true);
//                        edit.commit();
//                        Intent intent = new Intent(LoginActivity.this, ChooseTheData.class);
//                        startActivity(intent);
                    //}
//                else {
//                        GlobalClass.showToast("User name and password don't match");
//                    }
                }
            }
        });
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked) {
//                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    checkBox.setText("Show password");
//                    checkBox.setTextColor(Color.WHITE);
//
//                } else {
//                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    checkBox.setText("Hide password");
//                    checkBox.setTextColor(Color.WHITE);
//
//                }
//            }
//        });
        setupToolbar();

    }

    private void login() {             //send data to server and get valid data from server.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barProgressDialog.show();
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                    Log.e("json value", jsonObject.toString() + "");

                    //this is the url where you want to send the request
                    //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
                    // Request a string response from the provided URL.
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, API_URL.login, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.e("response", response + "");
                                        JSONObject jsonResponse = new JSONObject(response.getString("user"));
                                        Log.e("token", response.getString("token"));
                                        String token = response.getString("token");
                                        //           responsetext.setText(responseStatus);
                                        Intent intent = new Intent(getApplicationContext(), ChooseTheData.class);
                                        if (jsonResponse.getString("username").equals(username)) {
                                            final GlobalClass globalClass = (GlobalClass) getApplicationContext();
                                            globalClass.setUserName(username);
                                            SharedPreferences.Editor edit = SM.edit();
                                            edit.putString("token", token);
                                            edit.putBoolean("userlogin", true);
                                            edit.commit();
                                            startActivity(intent);
                                            GlobalClass.showToast("Login Successfully");
                                            barProgressDialog.hide();
                                        }
                                    } catch (Exception e) {
                                        try {
                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                            barProgressDialog.hide();
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        barProgressDialog.hide();
                                        Log.e("error", e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("message1234", error.getMessage() + "");
                                    barProgressDialog.hide();
                                    Toast.makeText(getApplicationContext(), "Failed To Connect", Toast.LENGTH_SHORT).show();
                                }
                            });
                    // Add the request to the RequestQueue.
                    queue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    Log.e("error jsonexception", e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LoginActivity.super.onBackPressed();
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
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */

    private boolean validateInputs() {                   // check that edittext is empty or not.
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (KEY_EMPTY.equals(username)) {
            etUsername.setError("Username can not be empty");
            etUsername.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            etPassword.setError("Password can not be empty");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 5) {
            GlobalClass.showToast("Password should be of minimum 6 characters");
            return false;
        }

        return true;
    }

    public boolean isConnected() {
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {
            Toast.makeText(this, " No Internet connection ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}