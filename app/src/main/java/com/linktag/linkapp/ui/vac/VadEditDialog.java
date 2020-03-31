package com.linktag.linkapp.ui.vac;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.model.VAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.VadVO;
import com.linktag.linkapp.value_object.VamVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VadEditDialog extends AlertDialog {

    private CustomDialogListener customDialogListener;
    private Context mContext;

    private EditText ed_vadInfo;

    private Button okButton;
    private Button cancelButton;

    private String VAC_ID;
    private String VAC_01;

    private InterfaceUser mUser = InterfaceUser.getInstance();

    private RecyclerView recyclerView_vam;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<VamVO> mList;
    private VamRecycleAdapter2 mAdapter;

    private RecyclerView recyclerView_vad;
    private LinearLayoutManager linearLayoutManager_vad;
    private ArrayList<VadVO> mList_vad;
    private VadRecycleAdapter mAdapter_vad;


    public static TextView tv_vam_nodata;
    public static TextView tv_vamCnt;

    public static TextView tv_vad_nodata;
    public static TextView tv_vadCnt;

    public VadEditDialog(@NonNull Context context, String VAC_ID, String VAC_01) {
        super(context);
        this.mContext = context;
        this.VAC_ID = VAC_ID;
        this.VAC_01 = VAC_01;
    }

    public interface CustomDialogListener {
        void onPositiveClicked();

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_vad_edit);

//        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        imm.showSoftInput(ed_vadInfo, 0);


        recyclerView_vad = findViewById(R.id.recyclerView_vad);
        tv_vadCnt = findViewById(R.id.tv_vadCnt);
        tv_vad_nodata = findViewById(R.id.tv_vad_nodata);


        mList_vad = new ArrayList<>();
        linearLayoutManager_vad = new LinearLayoutManager(mContext);
        recyclerView_vad.setLayoutManager(linearLayoutManager_vad);
        mAdapter_vad = new VadRecycleAdapter(mContext, mList_vad);
        mAdapter_vad.setmAdapter(mAdapter_vad);
        recyclerView_vad.setAdapter(mAdapter_vad);


        recyclerView_vam = findViewById(R.id.recyclerView_vam);
        tv_vamCnt = findViewById(R.id.tv_vamCnt);
        tv_vam_nodata = findViewById(R.id.tv_vam_nodata);

        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView_vam.setLayoutManager(linearLayoutManager);
        mAdapter = new VamRecycleAdapter2(mContext, mList);
        mAdapter.setmAdapter(mAdapter_vad);
        recyclerView_vam.setAdapter(mAdapter);

        requestVAM_SELECT();


        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);

        ed_vadInfo = findViewById(R.id.ed_vadInfo);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                customDialogListener.onPositiveClicked();
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }



    public void requestVAM_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<VAMModel> call = Http.vam(HttpBaseService.TYPE.POST).VAM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                VAC_ID,
                VAC_01
        );


        call.enqueue(new Callback<VAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VAMModel> call, Response<VAMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VAMModel> response = (Response<VAMModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            tv_vamCnt.setText("(" + mList.size() + ")");
                            if (mList.size() == 0) {
                                tv_vam_nodata.setVisibility(View.VISIBLE);
                                recyclerView_vam.setVisibility(View.GONE);

                            } else {
                                recyclerView_vam.setVisibility(View.VISIBLE);
                                tv_vam_nodata.setVisibility(View.GONE);
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


}
