package com.flysolo.mistervapeshop.checkout;

import android.app.MediaRouteButton;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.MainActivity;
import com.flysolo.mistervapeshop.R;
import com.flysolo.mistervapeshop.databinding.FragmentCheckOutBinding;
import com.flysolo.mistervapeshop.login.LoginActivity;
import com.flysolo.mistervapeshop.models.CartItem;
import com.flysolo.mistervapeshop.models.Product;
import com.flysolo.mistervapeshop.viewmodels.CartItemViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CheckOutFragment extends DialogFragment {

    private FragmentCheckOutBinding binding;
    private ActivityResultLauncher<Intent> galleryLuancher;
    private Uri originalURI = null;
    private Bitmap bitmap;
    private List<CartItem> cartItemList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCheckOutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartItemList = new ArrayList<>();
        CartItemViewModel cartItemViewModel = new ViewModelProvider(requireActivity()).get(CartItemViewModel.class);
        cartItemViewModel.getItemSelected().observe(getViewLifecycleOwner(), cartItemList -> {
            for (CartItem cartItem : cartItemList){
                addView(cartItem);
            }
            this.cartItemList = cartItemList;
            binding.textCheckoutTotal.setText(String.valueOf(computeTotalCheckedItems(cartItemList)));
            binding.textItemCount.setText(String.valueOf(computeQuantity(cartItemList)));
        });
        galleryLuancher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null) {
                originalURI = result.getData().getData();
            }
            try {
                InputStream inputStream=requireActivity().getContentResolver().openInputStream(originalURI);
                bitmap= BitmapFactory.decodeStream(inputStream);
                binding.imageReciept.setImageBitmap(bitmap);
            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        binding.buttonBack.setOnClickListener(v -> dismiss());

        binding.attachPaymentReciept.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLuancher.launch(intent);
        });
        binding.buttonConfirm.setOnClickListener(v -> {


            String refNumber = binding.textReferenceNumber.getEditText().getText().toString();
            if (refNumber.isEmpty()){
                binding.textReferenceNumber.setError("Enter reference number");
            } else if (originalURI == null){
                Toast.makeText(requireContext(),"Please attach Gcash Reciept",Toast.LENGTH_SHORT).show();
            } else {
                binding.progressCircular.setVisibility(View.VISIBLE);
                String checkoutID = UUID.randomUUID().toString();
                String timestamp = String.valueOf(System.currentTimeMillis());
                checkout(checkoutID, LoginActivity.username,refNumber,timestamp);
            }

        });
    }
    private void addView(CartItem cartItem){
        //TODO add view to the linearlayout
        View view = getLayoutInflater().inflate(R.layout.layout_checkout_items,null);
        TextView textSupplier = view.findViewById(R.id.textSupplier);
        TextView textProductName = view.findViewById(R.id.textProductName);
        TextView textQuantity = view.findViewById(R.id.textQuantity);
        TextView textGenName = view.findViewById(R.id.textGenName);
        TextView textProductPrice = view.findViewById(R.id.textProductPrice);
        TextView textTotal = view.findViewById(R.id.textProductTotal);
        Product product = getProduct(cartItem.getProductID());
        textSupplier.setText(product.getSupplier());
        textProductName.setText(product.getProduct_name());
        textQuantity.setText(String.valueOf(cartItem.getCartItemQuantity()));
        textGenName.setText(product.getGen_name());
        textProductPrice.setText(String.valueOf(product.getPrice()));
        textTotal.setText(String.valueOf(cartItem.getCartItemQuantity() * product.getPrice()));
        binding.layoutItems.addView(view);
    }
    private void addCheckOutItems(JSONArray jsonArray){
        // add checkout items to the database
        StringRequest request = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/checkOutItems.php",
                response -> showDialog("Success", String.valueOf(response)),
                error -> showDialog("Error",error.toString()))
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map= new HashMap<>();
                map.put("params",jsonArray.toString());
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(requireContext());
        queue.add(request);
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

    private String encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage=byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }
    private void checkout(String checkoutID,String userID,String referenceNumber,String checkoutTimestamp) {
        StringRequest request=new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/checkout.php",
            response ->{
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < cartItemList.size(); i++) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("checkOutID",checkoutID);
                        jsonObject.put("productID",cartItemList.get(i).getProductID());
                        jsonObject.put("quantity",cartItemList.get(i).getCartItemQuantity());
                        jsonObject.put("itemStatus","paid");
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                addCheckOutItems(jsonArray);
            }
            }, error ->{
            showDialog("Error",error.toString());
            Toast.makeText(requireContext(),error.toString(),Toast.LENGTH_LONG).show();
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map= new HashMap<>();
                map.put("checkOutID",checkoutID);
                map.put("userID",userID);
                map.put("referenceNumber",referenceNumber);
                map.put("checkOutTimestamp",checkoutTimestamp);
                map.put("image",encodeBitmapImage(bitmap));
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
    public void showDialog(String title,String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            dismiss();
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private Product getProduct(String productID){
        Product newProduct = new Product();
        for (Product product : MainActivity.productList){
            if (product.getProduct_id().equals(productID)){
                newProduct = product;
            }
        }
        return newProduct;
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
}