package com.miituo.miituolibrary.activities.utils;

import com.miituo.miituolibrary.activities.data.FacturasResponse;

public interface FacturaCallback {
    public void call(FacturasResponse response, int pos);
}
