package com.flysolo.mistervapeshop.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.flysolo.mistervapeshop.models.CartItem;

import java.util.List;


public class CartItemViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> itemSelected = new MutableLiveData<>();
    public void setItemSelected(List<CartItem> cartItemList) {
        itemSelected.setValue(cartItemList);
    }

    public LiveData<List<CartItem>> getItemSelected() {
        return itemSelected;
    }
}
