package com.flysolo.mistervapeshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.MainActivity;
import com.flysolo.mistervapeshop.R;
import com.flysolo.mistervapeshop.login.LoginActivity;
import com.flysolo.mistervapeshop.models.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context mContext;
    private List<Product> products;
    private int orderQuantity = 1;

    public ProductAdapter(Context context, List<Product> products){
        this.mContext = context;
        this.products = products;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_products,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Product product = products.get(position);
        holder.mPrice.setText("Php "+product.getPrice()+"0");
        holder.mCat.setText(product.getString());
        holder.mProduct.setText(product.getTitle());
        holder.mQty.setText("Stocks Left:  " + product.getQty());
        if (!LoginActivity.username.isEmpty()){
            holder.mContainer.setVisibility(View.VISIBLE);
        }
        holder.buttonAddToCart.setOnClickListener(v -> {
            String uniqueID = UUID.randomUUID().toString();
            String userID = LoginActivity.username;
            String productID = products.get(position).getProduct_id();
            int quantity = orderQuantity;
            long timestamp = System.currentTimeMillis();
            addToCart(uniqueID,userID,productID,quantity,timestamp);
        });
        holder.buttonIncrement.setOnClickListener(v -> {
            holder.textQuantity.setText(String.valueOf(incrementQuantity()));
        });
        holder.buttonDecrement.setOnClickListener(v -> {
            holder.textQuantity.setText(String.valueOf(decrementQuantity()));
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    private int incrementQuantity() {
        return orderQuantity = orderQuantity + 1;
    }
    private int decrementQuantity() {
        if (orderQuantity > 1) {
            orderQuantity = orderQuantity - 1;
            return orderQuantity;
        } else Toast.makeText(mContext, "minimum order is 1", Toast.LENGTH_SHORT).show();
        return orderQuantity;
    }

    private void addToCart(String cartItemID, String userID, String productID, int cartItemQuantity, long timestamp){
        StringRequest request = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/addToCart.php",
                response -> {

                    Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();

                },
                error ->{

                    Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();
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
        RequestQueue queue= Volley.newRequestQueue(mContext);
        queue.add(request);
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mProduct;
        private final TextView mPrice;
        private final TextView mCat;
        private final TextView mQty;
        private LinearLayout mContainer;

        private final TextView textQuantity;
        private final Button buttonAddToCart;
        private final ImageButton buttonDecrement;
        private final ImageButton buttonIncrement;

        public MyViewHolder(View view) {
            super(view);
            mProduct = view.findViewById(R.id.product_title);
            mCat = view.findViewById(R.id.product_cat);
            mPrice = view.findViewById(R.id.product_price);
            mQty = view.findViewById(R.id.product_qty);
            mContainer = view.findViewById(R.id.addToCartContainer);

            textQuantity = view.findViewById(R.id.textQuantity);
            buttonAddToCart = view.findViewById(R.id.buttonAddToCart);
            buttonIncrement = view.findViewById(R.id.buttonIncrement);
            buttonDecrement = view.findViewById(R.id.buttonDecrement);


        }
    }

}
