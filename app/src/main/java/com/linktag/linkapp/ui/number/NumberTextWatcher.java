package com.linktag.linkapp.ui.number;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.ParseException;

public class NumberTextWatcher implements TextWatcher {
    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private EditText et;

    public NumberTextWatcher(EditText editText)
    {
        df = new DecimalFormat("###,###.####");
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("###,###");
        this.et = editText;
        hasFractionalPart = false;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    public void afterTextChanged(Editable editable)
    {
        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String numString = editable.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number num = df.parse(numString);
            int cp = et.getSelectionStart();
            if (hasFractionalPart) {
                et.setText(df.format(num));
            } else {
                et.setText(dfnd.format(num));
            }
            endlen = et.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            }
        } catch (NumberFormatException nfe) {

        } catch (ParseException e) {

        }

        et.addTextChangedListener(this);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())) && !(s.toString().substring(s.toString().length()-2).equals(".0")))
        {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }
}
