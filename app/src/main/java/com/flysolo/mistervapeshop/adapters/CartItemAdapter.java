package com.flysolo.mistervapeshop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.mistervapeshop.MainActivity;
import com.flysolo.mistervapeshop.R;
import com.flysolo.mistervapeshop.models.CartItem;
import com.flysolo.mistervapeshop.models.Product;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    Context context;
    private final List<CartItem> cartItemList;
    public interface CartItemEvents {
        void changeQuantity(int position,Boolean result);
        void itemIsChecked(int position);
    }
    private final CartItemEvents cartItemEvents;

    public CartItemAdapter(Context context, List<CartItem> cartItemList, CartItemEvents cartItemEvents) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.cartItemEvents = cartItemEvents;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item,parent,false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        int orderQuantity = cartItemList.get(position).getCartItemQuantity();
        holder.textProductName.setText(cartItemList.get(position).getProductID());
        holder.textQuantity.setText(String.valueOf(orderQuantity));
        holder.bindViews(MainActivity.productList,cartItemList.get(position).getProductID(), orderQuantity);
        if (cartItemList.get(position).isChecked()){
            holder.buttonCheckOutThisItem.setChecked(true);
        }
        holder.buttonCheckOutThisItem.setOnClickListener(v -> {
            cartItemList.get(position).setChecked(holder.buttonCheckOutThisItem.isChecked());
            cartItemEvents.itemIsChecked(position);

        });
        holder.buttonIncrement.setOnClickListener(v -> {
            if (holder.productStocks > cartItemList.get(position).getCartItemQuantity()){
                cartItemEvents.changeQuantity(position,true);
            }

        });

        holder.buttonDecrement.setOnClickListener(v -> cartItemEvents.changeQuantity(position,false));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView textProductSupplier;
        private final TextView textProductGenName;
        private final TextView textProductName;
        private final TextView textQuantity;
        private final TextView textProductPrice;
        private final TextView textProductQuantity;
        private final ImageButton buttonIncrement;
        private final ImageButton buttonDecrement;
        private final CheckBox buttonCheckOutThisItem;
        private int productStocks = 0;
        private final TextView textTotal;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductSupplier = itemView.findViewById(R.id.textSuplier);
            textProductGenName = itemView.findViewById(R.id.textGenName);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
            textProductName = itemView.findViewById(R.id.textProductName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textProductQuantity = itemView.findViewById(R.id.textProductQuantity);
            buttonIncrement = itemView.findViewById(R.id.buttonIncrement);
            buttonDecrement = itemView.findViewById(R.id.buttonDecrement);
            buttonCheckOutThisItem = itemView.findViewById(R.id.buttonCheckOutThisItem);

            textTotal = itemView.findViewById(R.id.textCartTotal);
        }
        @SuppressLint("SetTextI18n")
        private void bindViews(List<Product> productList, String productID, int cartQuantity){
                for (Product product : productList){
                    if (product.getProduct_id().equals(productID)){
                        textProductSupplier.setText(product.getSupplier());
                        textProductName.setText(product.getTitle());
                        textProductGenName.setText(product.getGen_name());
                        textProductPrice.setText(String.valueOf(product.getPrice()));
                        textProductQuantity.setText(product.getQty() + " items left");
                        productStocks = product.getQty();
                        textTotal.setText(String.valueOf(cartQuantity * product.getPrice() ));
                    }
                }
        }
    }


}
