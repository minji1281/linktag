package com.linktag.linkapp.ui.scanner;

import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.linktag.linkapp.R;

public class ScanBarcode extends CaptureActivity {


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
