package com.miituo.miituolibrary.activities.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class TextWatcherCustom implements TextWatcher {

    TextInputLayout layout;
    public TextWatcherCustom(TextInputLayout layout){
        this.layout = layout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        this.layout.setHelperText(null);
    }

    @Override
    public void afterTextChanged(Editable editable) { }
}
