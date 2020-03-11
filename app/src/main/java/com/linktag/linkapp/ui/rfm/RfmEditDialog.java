package com.linktag.linkapp.ui.rfm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linktag.linkapp.R;

public class RfmEditDialog extends AlertDialog {

    private CustomDialogListener customDialogListener;
    private Context context;

    private EditText ed_name;
    private EditText ed_memo;
    private Button okButton;
    private Button cancelButton;

    private String name;
    private String memo;


    public RfmEditDialog(@NonNull Context context, String name, String memo) {
        super(context);
        this.name = name;
        this.memo = memo;
        this.context = context;
    }

    public interface CustomDialogListener {
        void onPositiveClicked(String name, String memo);

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_rfm_edit);

        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);

        ed_name = findViewById(R.id.ed_name);
        ed_memo = findViewById(R.id.ed_memo);

        ed_name.setText(name);
        ed_memo.setText(memo);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                customDialogListener.onPositiveClicked(ed_name.getText().toString(), ed_memo.getText().toString());
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dismiss();
            }
        });

    }

}
