package com.flysolo.mistervapeshop.booking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flysolo.mistervapeshop.R;
import com.flysolo.mistervapeshop.databinding.FragmentReserveProductBinding;

import java.util.HashMap;
import java.util.Map;


public class ReserveProductFragment extends DialogFragment {


    private FragmentReserveProductBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReserveProductBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(v->{
            dismiss();
        });
        binding.buttonReserve.setOnClickListener(v->{
            String fullname, address, contact, productname, expectedDate, note;
            fullname = binding.inputFullname.getEditText().getText().toString();
            address = binding.inputAddress.getEditText().getText().toString();
            contact = binding.inputContact.getEditText().getText().toString();
            productname = binding.inputProducts.getEditText().getText().toString();
            expectedDate = binding.inputExpectedDate.getEditText().getText().toString();
            note = binding.inputNote.getEditText().getText().toString();
            if (fullname.isEmpty()){
                binding.inputFullname.setError("Please enter your fullname");
            }
            else if (address.isEmpty()){
                binding.inputAddress.setError("Please enter your address");
            }
            else if (contact.isEmpty()){
                binding.inputContact.setError("Please enter your contact");
            }
            else if (productname.isEmpty()){
                binding.inputProducts.setError("Please enter product name");
            }
            else if (expectedDate.isEmpty()){
                binding.inputExpectedDate.setError("Please enter expected date");
            }
            else if (note.isEmpty()){
                binding.inputAddress.setError("Please enter your address");
            }
            else {
                reserveProduct(fullname,address,contact,productname,expectedDate,note);
            }
        });
    }
    private void reserveProduct(String fullname, String address, String contact,String productname,String expectedDate,String note){
        binding.progress.setVisibility(View.VISIBLE);
        binding.textProgress.setText("Loading.........");
        StringRequest request = new StringRequest(Request.Method.POST, "https://blazeproject0033.000webhostapp.com/mobile/reserve.php",
                response -> {
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                    binding.progress.setVisibility(View.GONE);
                    binding.textProgress.setText(response);
                    dismiss();
                },
                error ->{
                    Toast.makeText(requireContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    binding.textProgress.setText(error.getMessage());
                    binding.progress.setVisibility(View.GONE);
                    dismiss();
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map= new HashMap<>();
                map.put("fullname",fullname);
                map.put("address",address);
                map.put("contact",contact);
                map.put("prod_name",productname);
                map.put("expected_date",expectedDate);
                map.put("note",note);
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}