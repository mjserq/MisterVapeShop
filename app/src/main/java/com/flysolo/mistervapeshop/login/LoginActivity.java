package com.flysolo.mistervapeshop.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.MainActivity;
import com.flysolo.mistervapeshop.R;
import com.flysolo.mistervapeshop.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    public static String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonLogin.setOnClickListener(v->{
            String username = binding.inputUsername.getEditText().getText().toString();
            String password = binding.inputPassword.getEditText().getText().toString();
            if (username.isEmpty()){
                binding.inputUsername.setError("Please enter your username");
            } else if (password.isEmpty()){
                binding.inputPassword.setError("Please enter your password");
            } else {
                login(username,password);
            }
        });
        binding.buttonCreateAccount.setOnClickListener(v->{
            startActivity(new Intent(this,CreateAccount.class));
        });

    }

    private void login(String username,String password){
        // add checkout items to the database
        binding.progressCircular.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/login.php",
        response -> {
            if (response.equals("Login Success")){
                LoginActivity.username = username;
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class).putExtra("username",username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            }
            binding.progressCircular.setVisibility(View.GONE);
        },
        error ->{
            binding.progressCircular.setVisibility(View.GONE);
            Toast.makeText(this,error.getMessage(),Toast.LENGTH_SHORT).show();
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map= new HashMap<>();
                map.put("username",username);
                map.put("password",password);
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}