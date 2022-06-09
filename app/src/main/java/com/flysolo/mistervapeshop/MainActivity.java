package com.flysolo.mistervapeshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.adapters.ProductAdapter;
import com.flysolo.mistervapeshop.booking.ReserveProductFragment;
import com.flysolo.mistervapeshop.cart.CartActivity;
import com.flysolo.mistervapeshop.databinding.ActivityMainBinding;
import com.flysolo.mistervapeshop.login.LoginActivity;
import com.flysolo.mistervapeshop.models.Product;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private ActivityMainBinding binding;
    private ProductAdapter adapter;
    public static List<Product> productList;
    private static final String BASE_URL = "https://blazeproject0033.000webhostapp.com/mobile/getProducts.php";
    private int orderQuantity = 1;
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
        productList.clear();
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
                    adapter = new ProductAdapter(this,productList,this);
                    binding.productsRecyclerView.setAdapter(adapter);
                }, error -> {
            binding.progressbar.setVisibility(View.GONE);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Connection Error!")
                    .setPositiveButton("Refresh", (dialogInterface, i) -> {
                        getProducts();
                    }).show();;

        });
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void addToCart(String cartItemID, String userID, String productID, int cartItemQuantity, long timestamp){
        StringRequest request = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/addToCart.php",
                response -> {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                },
                error ->{
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setTitle("Connection Error!")
                            .setPositiveButton("Refresh", (dialogInterface, i) -> {
                                getProducts();
                            }).show();
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map= new HashMap<>();
                map.put("cartItemID",cartItemID);
                map.put("userID",userID);
                map.put("productID",productID);
                map.put("cartItemQuantity",String.valueOf(cartItemQuantity));
                map.put("timestamp",String.valueOf(timestamp));
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }


    @Override
    public void onProductClick(int position, int quantity) {
        String uniqueID = UUID.randomUUID().toString();
        String userID = LoginActivity.username;
        Product product = productList.get(position);
        addToCart(uniqueID,userID,product.getProduct_id(),quantity,System.currentTimeMillis());
    }
}