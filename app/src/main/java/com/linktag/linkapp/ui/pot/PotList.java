package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PotList extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private GridView gridView;
    private TextView emptyText;
    private EditText etName;
    private ImageView imgSearch;

    //======================
    // Variable
    //======================
    private PotAdapter mAdapter;
    private ArrayList<PotVO> mList;
    private String scancode = "";
    private String CTM_01;
    private String CTD_02;
    private String CTN_02;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pot_list);

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTD_02 = getIntent().getStringExtra("CTD_02");
        CTN_02 = getIntent().getStringExtra("CTN_02");
        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
            requestPOT_SELECT("DETAIL", scancode);
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility((View.GONE));

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PotVO POT = mList.get(position);

                Intent intent = new Intent(mContext, PotDetail.class);
                intent.putExtra("POT", POT);
                intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
                intent.putExtra("CTN_02", CTN_02);

                mContext.startActivity(intent);
            }
        });

        emptyText = findViewById(R.id.empty);
        gridView.setEmptyView(emptyText);
        etName = findViewById(R.id.etName);
        imgSearch = findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(v -> requestPOT_SELECT("LIST", ""));
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new PotAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume(){
        super.onResume();

        requestPOT_SELECT("LIST", "");
    }

    private void requestPOT_SELECT(String GUBUN, String POT_01){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String POT_ID = CTN_02; //컨테이너
        String POT_02 = etName.getText().toString();
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                POT_02,
                OCM_01
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            mList = response.body().Data;

                            if(GUBUN.equals("LIST")){
                                if(mList == null)
                                    mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                            }
                            else{ //DETAIL (스캔했을때)
                                if(mList.size() == 0){ //등록된 정보가 없을때
//                                    goPotNew();
                                }
                                else{ //등록된 정보가 있을때
                                    PotVO POT = mList.get(0);

                                    Intent intent = new Intent(mContext, PotDetail.class);
                                    intent.putExtra("POT", POT);
                                    intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                                    intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
                                    intent.putExtra("CTN_02", CTN_02);

                                    mContext.startActivity(intent);
                                }
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

//    private void goPotNew(){
//        Intent intent = new Intent(mContext, PotNew.class);
//        intent.putExtra("POT_01", scancode);
//        intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
//        intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
//        intent.putExtra("CTN_02", CTN_02);
//        mContext.startActivity(intent);
//    }

}
