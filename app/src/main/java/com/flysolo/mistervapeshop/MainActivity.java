package com.flysolo.mistervapeshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.adapters.ProductAdapter;
import com.flysolo.mistervapeshop.booking.ReserveProductFragment;
import com.flysolo.mistervapeshop.cart.CartActivity;
import com.flysolo.mistervapeshop.databinding.ActivityMainBinding;
import com.flysolo.mistervapeshop.login.LoginActivity;
import com.flysolo.mistervapeshop.models.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ProductAdapter adapter;
    public static List<Product> productList;
    private static final String BASE_URL = "https://blazeproject0033.000webhostapp.com/mobile/getProducts.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productList = new ArrayList<>();
        if (!LoginActivity.username.isEmpty()){
            binding.textUsername.setText(LoginActivity.username);
        }
        binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        getProducts();
        binding.textUsername.setOnClickListener(v -> {
            if (LoginActivity.username.isEmpty()){
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });
        binding.buttonMyCart.setOnClickListener(v->{
            if (LoginActivity.username.isEmpty()){
                Toast.makeText(this,"Login First",Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CartActivity.class));
            }

        });
       /* binding.buttonBooking.setOnClickListener(v->{
            if (LoginActivity.username.isEmpty()){
                Toast.makeText(this,"Login First",Toast.LENGTH_SHORT).show();
            } else {
                ReserveProductFragment fragment = new ReserveProductFragment();
                fragment.show(getSupportFragmentManager(),"Reserve Product");
            }
        });*/
    }

    private void getProducts (){
        binding.progressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL,
                response -> {
                    binding.progressbar.setVisibility(View.GONE);
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            String product_id = object.getString("product_id");
                            String product_name = object.getString("product_name");
                            double price = object.getDouble("price");
                            String gen_name = object.getString("gen_name");
                            int qty = object.getInt("qty");
                            String supplier = object.getString("supplier");
                            Product product = new Product(product_id,product_name,price, gen_name, qty,supplier);
                            productList.add(product);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    adapter = new ProductAdapter(this,productList);
                    binding.productsRecyclerView.setAdapter(adapter);
                }, error -> {
            binding.progressbar.setVisibility(View.GONE);
            Toast.makeText(this, error.toString(),Toast.LENGTH_LONG).show();

        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

}