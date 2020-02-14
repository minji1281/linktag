package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.OcmVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberFragment extends BaseFragment {

    private View view;

    private TextView tvInvite;
    private ImageView ivInvite;

    private TextView tvTitleMem;
    private EditText etSearch;
    private ImageView btnSearch;
    private Spinner spinnerShared;
    private ListView listview;
    private TextView emptyText;

    private ArrayList<ClsShared> sharedList;

    private ArrayList<OcmVO> mList;
    private MemberAdapter mAdapter;
    private String[] ar;

    public MemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_member, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestCTD_SELECT();
    }

    private void initLayout() {
        tvInvite = view.findViewById(R.id.tvInvite);
        ivInvite = view.findViewById(R.id.ivInvite);
        tvInvite.setOnClickListener(v -> goInvite());
        ivInvite.setOnClickListener(v -> goInvite());

        tvTitleMem = view.findViewById(R.id.tvTitleMem);

        etSearch = view.findViewById(R.id.etSearch);
        etSearch.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    requestOCM_SELECT();
                    return true;
                }
                return false;
            }
        });
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> requestOCM_SELECT());

        spinnerShared = view.findViewById(R.id.spinnerShared);

        listview = view.findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        emptyText = view.findViewById(R.id.empty);
        listview.setEmptyView(emptyText);
    }

    protected void initialize(){
        sharedList = new ArrayList<>();

        mList = new ArrayList<>();
        mAdapter = new MemberAdapter(mContext, mList);
        listview.setAdapter(mAdapter);

    }

    public void requestCTD_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext )){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "LIST_SHARED",
                "",
                "",
                mUser.Value.OCM_01
        );

        call.enqueue(new Callback<CTD_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTD_Model> call, Response<CTD_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            setSpinner(response.body());
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    private void goInvite(){
        Intent intent = new Intent(mContext, AddService.class);
        mContext.startActivity(intent);
    }

    private void setSpinner(CTD_Model model){
        sharedList.clear();

        ar = new String[model.Total + 1];
        ar[0] = "공유 선택";

        sharedList.add(new ClsShared("공유 선택", "", ""));

        if(model.Total != 0){
            if(model.Total > 0){
                for (int i=1; i<model.Total + 1; i++){
                    sharedList.add(new ClsShared(model.Data.get(i - 1).CTD_02_NM, model.Data.get(i - 1).CTD_01, model.Data.get(i - 1).CTD_02));

                    ar[i] = model.Data.get(i - 1).CTD_02_NM + "[" +  model.Data.get(i - 1).CTM_17 + "]";
                }
            }
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, ar);

        //adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerShared.setAdapter(adapter);

        spinnerShared.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println("######### : " + sharedList.get(position).getContract());
//                if(position != 0)
                    requestOCM_SELECT();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void requestOCM_SELECT(){
        int position = spinnerShared.getSelectedItemPosition();
        String CTM_01 = sharedList.get(position).getContract();
        String OCM_02 = etSearch.getText().toString();
/*
        if(CTM_01.equals("") || CTM_01 == null){
            Toast.makeText(mActivity, "조회 할 공유를 선택해 주세요.", Toast.LENGTH_LONG).show();
            return;
        }
*/
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_SELECT(
                BaseConst.URL_HOST,
                "LIST_SHARED",
                mUser.Value.OCM_01,
                OCM_02,
                "",
                 CTM_01
        );

        call.enqueue(new Callback<OCM_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                            setTitleMem(position);
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<OCM_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });
    }

    private void setTitleMem(int position){
        String str = "멤버";

        if(position != 0){
            //str += " - " + sharedList.get(position).getName();
            str += " - " + ar[position];

            SpannableString sb = new SpannableString(str);
            sb.setSpan(new ForegroundColorSpan(Color.BLUE), 5, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //sb.setSpan(new RelativeSizeSpan(0.8f), 5, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTitleMem.setText(sb);

        }
        else
            tvTitleMem.setText(str);
    }

}
