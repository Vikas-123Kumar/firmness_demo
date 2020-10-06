package com.example.infyULabs.userProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.infyULabs.GlobalClass;
import com.example.infyULabs.R;
import com.example.infyULabs.loginregister.model.User;
import com.example.infyULabs.loginregister.sqlite.DataBaseHelper;
import com.example.infyULabs.setting.SettingOfApp;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup extends AppCompatActivity implements View.OnClickListener {
    private Button submitbutton, settingAction;
    ImageView show_hide_icon, show_hide_icon_confirm;
    Context context;
    EditText user_name, _pass, _confirm, _mobile, _email;
    String Username, Password, Conf_Pass, Mobile, Email;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private DataBaseHelper databaseHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
        submitbutton = (Button) findViewById(R.id.submit);
        user_name = findViewById(R.id.user);
        show_hide_icon = findViewById(R.id.show_hide_icon_pass);
        show_hide_icon_confirm = findViewById(R.id.show_hide_icon_confirm);
        _pass = findViewById(R.id.pass);
        _confirm = findViewById(R.id.confirmpass);
        _mobile = findViewById(R.id.mobile);
        _email = findViewById(R.id.email);
        settingAction = (Button) findViewById(R.id.setting_action);
        show_hide_icon.setOnClickListener(this);
        settingAction.setOnClickListener(this);
        show_hide_icon_confirm.setOnClickListener(this);
        submitbutton.setOnClickListener(this);
        databaseHelper = new DataBaseHelper(context);
        user=new User();
        setupToolbar();
    }

    private void signUp() {                   //send data to server for a new user and make an account.
        RequestQueue queue = Volley.newRequestQueue(Signup.this);
        JSONObject jsonObject = new JSONObject();
        final JSONObject jsonObject2 = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put("username", Username);
            jsonObject.put("password", Password);
            jsonObject.put("confirm password", Conf_Pass);
            jsonObject.put("mobile no.", Mobile);
            jsonObject.put("email", Email);

            jsonObject2.put("camb2", jsonObject);

            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = "http://192.168.0.106:5000/api/post_some_data";
            // Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonObject2, new com.android.volley.Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean checkstatus() {                 // check edittext is fill or not and valid data.
        Username = user_name.getText().toString();
        Password = _pass.getText().toString();
        Conf_Pass = _confirm.getText().toString();
        Mobile = _mobile.getText().toString();
        Email = _email.getText().toString();


        if (Username.length() < 5) {
            GlobalClass.showToast("Username should not be minimum 5 characters");
            return false;
        }
        if (Password.length() < 4) {
            GlobalClass.showToast("password should be minimum 8 characters");
            return false;
        } else if (!Password.equals(Conf_Pass)) {

            GlobalClass.showToast("Password and Confirm Password does'nt match");
            return false;
        }
        if (Mobile.length() < 10) {
            GlobalClass.showToast("Mobile no. must be 10 digit");
            return false;
        }
        if (!Email.trim().matches(emailPattern)) {
            GlobalClass.showToast("Invalid email address");
            return false;
        }
        return true;
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
    public void onClick(View v) {
        if (v.getId() == R.id.setting_action) {
            startActivity(new Intent(this, SettingOfApp.class));
        } else if (v.getId() == R.id.submit) {
            if (checkstatus()) {
                Log.e("checkstatus", checkstatus() + "");
                if (databaseHelper != null && !databaseHelper.checkUser(Email)) {
                    user.setName(Username);
                    user.setEmail(Email);
                    user.setPassword(Password);
                    user.setMobile_no(Mobile);
                    databaseHelper.addUser(user);
                    Log.e("databasehelper", databaseHelper + "");
                    // Snack Bar to show success message that record saved successfully
                    Snackbar.make(v, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
                    // signUp();
                    Intent intent = new Intent(Signup.this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    // Snack Bar to show error message that record already exists
                    Snackbar.make(v, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
                }
            }
        } else if (v.getId() == R.id.show_hide_icon_pass) {

            if (_pass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                show_hide_icon.setImageResource(R.drawable.show_password);

                //Show Password
                _pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                show_hide_icon.setImageResource(R.drawable.hide_password);

                //Hide Password
                _pass.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        } else if (v.getId() == R.id.show_hide_icon_confirm) {
            if (_confirm.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                show_hide_icon_confirm.setImageResource(R.drawable.show_password);

                //Show Password
                _confirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                show_hide_icon_confirm.setImageResource(R.drawable.hide_password);

                //Hide Password
                _confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }
}