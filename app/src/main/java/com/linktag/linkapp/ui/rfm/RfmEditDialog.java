package com.linktag.linkapp.ui.rfm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RfmEditDialog extends Dialog {

    private CustomDialogListener customDialogListener;
    private Context mContext;

    private EditText ed_name;
    private EditText ed_memo;
    private Button okButton;
    private Button deleteButton;

    private String RFM_ID;
    private String RFM_01;
    private String name;
    private String memo;

    private InterfaceUser mUser = InterfaceUser.getInstance();

    public RfmEditDialog(@NonNull Context context, String RFM_ID, String RFM_01, String name, String memo) {
        super(context);
        this.name = name;
        this.memo = memo;
        this.mContext = context;
        this.RFM_ID = RFM_ID;
        this.RFM_01 = RFM_01;
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

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(ed_memo, 0);


        okButton = findViewById(R.id.okButton);
        deleteButton = findViewById(R.id.deleteButton);

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
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog();
            }
        });

    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        AlertDialog dialog = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etDeleteName.getText().toString().equals(ed_name.getText().toString())) {
                    dialog.dismiss();
                    requestRFM_CONTROL("DELETE");
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.common_confirm_delete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void requestRFM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getResources().getString(R.string.common_network_error));
            return;
        }


        Call<RFMModel> call = Http.rfm(HttpBaseService.TYPE.POST).RFM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                RFM_ID,
                RFM_01,
                name,
                memo,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<RFMModel>() {
            @Override
            public void onResponse(Call<RFMModel> call, Response<RFMModel> response) {
            }

            @Override
            public void onFailure(Call<RFMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }



}
