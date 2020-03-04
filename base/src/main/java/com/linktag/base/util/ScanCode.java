package com.linktag.base.util;

import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;

public class ScanCode extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();

        initialize();
    }

    private void initLayout() {
    }

    private void initialize() {
    }
}
