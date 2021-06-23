package com.example.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private int isLogon = 0;
    private String email;
    private String username;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        Button btLogon = findViewById(R.id.bt_logon);
        Button btLogin  = findViewById(R.id.bt_login);
        EditText emailTextView = findViewById(R.id.email);
        EditText usernameTextView = findViewById(R.id.username);
        EditText passwordTextView = findViewById(R.id.password);
        btLogon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (isLogon == 0) {
                    emailTextView.setVisibility(View.VISIBLE);
                    btLogon.setText("确认");
                    isLogon = 1;
                }
                else if (isLogon == 1){
                    email = emailTextView.getText().toString();
                    username = usernameTextView.getText().toString();
                    password = passwordTextView.getText().toString();
                    // send logon request to server
                    // receive "logon success" signal from server
                    Handler handler = new Handler(Looper.getMainLooper());
                    ServerReq.postJson("/signup", List.of(
                            new Pair<>("nickname", usernameTextView.getText().toString()),
                            new Pair<>("email", emailTextView.getText().toString()),
                            new Pair<>("password", passwordTextView.getText().toString())
                    ), (JSONObject obj) -> handler.post(() -> {
                        try {
                            int error = obj.getInt("error");
                            if (error == 0) {
                                emailTextView.setVisibility(View.GONE);
                                btLogon.setText("注册");
                                isLogon = 0;
                                Toast toast = Toast.makeText(getBaseContext(), "注册成功！", Toast.LENGTH_LONG);
                                toast.show();
                            } else if (error == 1) {
                                Toast toast = Toast.makeText(getBaseContext(), "用户名需为 3 至 16 个字符", Toast.LENGTH_LONG);
                                toast.show();
                            } else if (error == 2) {
                                Toast toast = Toast.makeText(getBaseContext(), "用户名已被注册", Toast.LENGTH_LONG);
                                toast.show();
                            } else if (error == 3) {
                                Toast toast = Toast.makeText(getBaseContext(), "邮箱已被注册", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        } catch (Exception e) {
                            Log.e("LoginActivity", e.toString());
                            return;
                        }
                    }));
                }
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // send login request to server; is password right?
                Handler handler = new Handler(Looper.getMainLooper());
                ServerReq.login(
                    usernameTextView.getText().toString(),
                    passwordTextView.getText().toString(),
                    (Boolean success) -> handler.post(() -> {
                        android.util.Log.d("MainActivity", success ? ServerReq.token : "T-T");
                        if (!success) {
                            Toast toast = Toast.makeText(getBaseContext(), "密码错误！", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            Toast toast = Toast.makeText(getBaseContext(), "登录成功！", Toast.LENGTH_LONG);
                            toast.show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("isLogin", "true");
                            LoginActivity.this.startActivity(intent);
                        }
                    }));
            }
        });

    }
}
