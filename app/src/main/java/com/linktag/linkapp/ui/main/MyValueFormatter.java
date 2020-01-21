package com.linktag.linkapp.ui.main;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyValueFormatter extends ValueFormatter {
    private String suffix;

    public MyValueFormatter(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String getFormattedValue(float value) {
        return value + suffix;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (axis instanceof XAxis) {
            return String.format("%.0f",  value) + suffix;
        } else if (value > 0) {
            return String.format("%.0f",  value) + suffix;
        } else {
            return String.format("%.0f",  value);
        }
    }
}
