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
import com.flysolo.mistervapeshop.databinding.ActivityCreateAccountBinding;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {
    private ActivityCreateAccountBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonCreateAccount.setOnClickListener(v->{
            String fullname, username, password, address, contact;
            fullname = binding.inputFullname.getEditText().getText().toString();
            address = binding.inputAddress.getEditText().getText().toString();
            username = binding.inputUsername.getEditText().getText().toString();
            password = binding.inputPassword.getEditText().getText().toString();
            contact = binding.inputContact.getEditText().getText().toString();
            if (fullname.isEmpty()){
                binding.inputFullname.setError("Please enter your fullname");
            }  else if (address.isEmpty()){
                binding.inputAddress.setError("Please enter your address");
            } else if (username.isEmpty()){
                binding.inputUsername.setError("Please enter your username");
            }else if (password.isEmpty()) {
                binding.inputPassword.setError("Please enter your password");
            }else if (contact.isEmpty()){
                    binding.inputContact.setError("Please enter your contact number");
            } else {
                createAccount(fullname,address,username,password,contact);
            }
        });
        binding.buttonBackToLogin.setOnClickListener(view -> {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        });

    }

    private void createAccount(String fullname, String address, String username, String password, String contact) {
        // add checkout items to the database
        binding.progressbar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/signup.php",
                response -> {
                    if (response.equals("Register Success")){
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    }
                    binding.progressbar.setVisibility(View.GONE);
                },
                error ->{
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(this,error.getMessage(),Toast.LENGTH_SHORT).show();
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map= new HashMap<>();
                map.put("fullname",fullname);
                map.put("username",username);
                map.put("password",password);
                map.put("address",address);
                map.put("contact",contact);
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }
}