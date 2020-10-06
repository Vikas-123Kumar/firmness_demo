package com.example.infyULabs.userProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.infyULabs.GlobalClass;
import com.example.infyULabs.R;
import com.example.infyULabs.setting.SettingOfApp;

import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    private Button submit_password, settingAction;
    private EditText edit_Email;
    private TextView back_login;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        submit_password = (Button) findViewById(R.id.submit);
        settingAction = findViewById(R.id.setting_action);
        back_login = findViewById(R.id.back_login);
        edit_Email = findViewById(R.id.email_edit);
        back_login.setOnClickListener(this);
        settingAction.setOnClickListener(this);
        submit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonTask();
                Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        setupToolbar();
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

    public void submitButtonTask() {
        String getEmailId = edit_Email.getText().toString();
        if (getEmailId.equals("") || getEmailId.length() == 0) {
            GlobalClass.showToast("Please Enter Your Email Id");
        } else if (!getEmailId.trim().matches(emailPattern)) {
            GlobalClass.showToast("Invalid email address");

        } else {
            GlobalClass.showToast("Get Forget Password");

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_action) {
            startActivity(new Intent(this, SettingOfApp.class));
        } else if (v.getId() == R.id.back_login) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
