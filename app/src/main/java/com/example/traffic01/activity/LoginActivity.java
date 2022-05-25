package com.example.traffic01.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.example.traffic01.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * @author admin
 */
public class LoginActivity extends BaseActivity {
    private CheckBox checkBoxRemember;
    private CheckBox checkBoxAutoLogin;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(this.getString(R.string.login));

        initView();
        login();
        buttonRegister.setOnClickListener(v -> Snackbar.make(buttonRegister, "敬请期待", Snackbar.LENGTH_SHORT).show());
    }

    private void checks() {
        if(checkBoxRemember.isChecked()) {
            Snackbar.make(checkBoxRemember,getResources().getString(R.string.login_check1),Snackbar.LENGTH_SHORT).show();
        }
        if(checkBoxAutoLogin.isChecked()) {
            Snackbar.make(checkBoxAutoLogin,getResources().getString(R.string.login_check2),Snackbar.LENGTH_SHORT).show();
        }

    }

    private void login() {
        String userName = "admin";
        String password = "123456";
        buttonLogin.setOnClickListener(v -> {
            if (!editTextUserName.getText().toString().equals(userName)) {
                Snackbar.make(buttonLogin, "用户名错误", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!editTextPassword.getText().toString().equals(password)) {
                Snackbar.make(buttonLogin, "密码错误", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Snackbar.make(buttonLogin, "登录成功", Snackbar.LENGTH_SHORT).show();
            checks();
        });
    }

    private void initView() {
        checkBoxRemember = findViewById(R.id.check_remember);
        checkBoxAutoLogin = findViewById(R.id.check_automatic);
        editTextUserName = findViewById(R.id.edit_name);
        editTextPassword = findViewById(R.id.edit_pass);
        buttonLogin = findViewById(R.id.button_login);
        buttonRegister = findViewById(R.id.button_register);
    }
}