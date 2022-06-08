package com.flysolo.mistervapeshop.cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.MainActivity;
import com.flysolo.mistervapeshop.R;
import com.flysolo.mistervapeshop.adapters.CartItemAdapter;
import com.flysolo.mistervapeshop.adapters.ProductAdapter;
import com.flysolo.mistervapeshop.checkout.CheckOutFragment;
import com.flysolo.mistervapeshop.databinding.ActivityCartBinding;
import com.flysolo.mistervapeshop.login.LoginActivity;
import com.flysolo.mistervapeshop.models.CartItem;
import com.flysolo.mistervapeshop.models.Product;
import com.flysolo.mistervapeshop.viewmodels.CartItemViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.CartItemEvents {
    private ActivityCartBinding binding;
    private List<CartItem> cartItemList;
    private CartItemAdapter adapter;
    private static final String BASE_URL = "https://blazeproject0033.000webhostapp.com/mobile/getItemsInCart.php";
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private CartItemViewModel cartItemViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cartItemViewModel = new ViewModelProvider(this).get(CartItemViewModel.class);
        cartItemList = new ArrayList<>();
        binding.recyclerviewMyCart.setLayoutManager(new LinearLayoutManager(this));
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        getMyCart();
        swipeToDelete(binding.recyclerviewMyCart);


        binding.buttonBack.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        binding.buttonCheckOut.setOnClickListener(view -> {
            if (!getCartItemSelected(cartItemList).isEmpty()){
                cartItemViewModel.setItemSelected(getCartItemSelected(cartItemList));
                CheckOutFragment checkOutFragment = new CheckOutFragment();
                checkOutFragment.show(getSupportFragmentManager(),"checkout");

            }
        });
    }
    private void getMyCart(){
        binding.progressCircular.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL,
                response -> {
                    binding.progressCircular.setVisibility(View.GONE);
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String cartItemID = object.getString("cartItemID");
                            String userID = object.getString("userID");
                            String productID = object.getString("productID");
                            int cartItemQuantity = object.getInt("cartItemQuantity");
                            long timestamp = object.getLong("timestamp");
                            CartItem cartItem = new CartItem(cartItemID,userID,productID,cartItemQuantity,timestamp);
                            if (cartItem.getUserID().equals(LoginActivity.username)){
                                cartItemList.add(cartItem);
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    adapter = new CartItemAdapter(CartActivity.this,cartItemList,this);
                    binding.recyclerviewMyCart.setAdapter(adapter);
                }, error -> {
            binding.progressCircular.setVisibility(View.GONE);
            Toast.makeText(this, error.toString(),Toast.LENGTH_LONG).show();
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void changeQuantity(int position, Boolean result) {
        int quantity = cartItemList.get(position).getCartItemQuantity();
        if (result) {
            cartItemList.get(position).setCartItemQuantity(incrementQuantity(quantity));

        } else {
            cartItemList.get(position).setCartItemQuantity(decrementQuantity(quantity));
        }
        adapter.notifyItemChanged(position);
        updateQuantity(position);
        binding.textCheckOutItemQuantity.setText(String.valueOf(computeQuantity(cartItemList)));
        binding.textTotal.setText(decimalFormat.format(computeTotalCheckedItems(cartItemList)));
    }

    @Override
    public void itemIsChecked(int position) {
        binding.textCheckOutItemQuantity.setText(String.valueOf(computeQuantity(cartItemList)));
        binding.textTotal.setText(decimalFormat.format(computeTotalCheckedItems(cartItemList)));
    }

    private int incrementQuantity(int orderQuantity) {
        orderQuantity += 1;
        return orderQuantity;
    }

    private int decrementQuantity(int orderQuantity) {
        if (orderQuantity > 1) {
            orderQuantity = orderQuantity - 1;
            return orderQuantity;
        } else Toast.makeText(this, "minimum order is 1", Toast.LENGTH_SHORT).show();
        return orderQuantity;
    }
    private void updateQuantity(int postion) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/updateCartItemQuantity.php",
                response -> {
                    if (response.equals("Success")){
                        adapter.notifyItemChanged(postion);
                    }
                }, error -> {
            Toast.makeText(this, error.toString(),Toast.LENGTH_LONG).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("cartItemID",cartItemList.get(postion).getCartItemID());
                map.put("cartItemQuantity",String.valueOf(cartItemList.get(postion).getCartItemQuantity()));
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private int computeQuantity(List<CartItem> cartItemList){
        int quantity = 0;
        for (CartItem cartItem : cartItemList){
            if (cartItem.isChecked()){
                quantity += cartItem.getCartItemQuantity();
            }
        }
        return quantity;
    }
    private double computeTotalCheckedItems(List<CartItem> cartItemList){
        int total = 0;
        for (CartItem cartItem : cartItemList){
            if (cartItem.isChecked()){
                for (Product product : MainActivity.productList){
                    if (product.getProduct_id().equals(cartItem.getProductID())){
                        total += (cartItem.getCartItemQuantity() * product.getPrice());
                    }
                }
            }
        }
        return total;
    }

    private List<CartItem> getCartItemSelected(List<CartItem> cartItemList){
        List<CartItem> selected = new ArrayList<>();
        for (CartItem cartItem : cartItemList){
            if (cartItem.isChecked()){
                selected.add(cartItem);
            }
        }
        return selected;
    }

    private void deleteCartItem(int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/deleteCartItem.php",
                response -> {
                    if (response.equals("Removed From Cart!")) {
                        cartItemList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(this, "Removed From Cart!", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Toast.makeText(this, error.toString(),Toast.LENGTH_LONG).show();
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("cartItemID",cartItemList.get(position).getCartItemID());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void swipeToDelete(final RecyclerView recyclerView) {
        ItemTouchHelper callback = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                deleteCartItem(pos);
                binding.textCheckOutItemQuantity.setText(String.valueOf(computeQuantity(cartItemList)));
                binding.textTotal.setText(decimalFormat.format(computeTotalCheckedItems(cartItemList)));
            }
        });
        callback.attachToRecyclerView(recyclerView);
    }
}