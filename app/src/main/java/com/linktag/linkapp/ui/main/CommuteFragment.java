package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.linkapp.R;
import com.linktag.base.util.ClsImage;
import com.linktag.linkapp.model.DSHModel;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.board.BoardMain;
import com.linktag.linkapp.ui.work_record.WorkRecordActivity;
import com.linktag.linkapp.ui.dash_state.DashStateAdapter;
import com.linktag.linkapp.value_object.DSH_VO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommuteFragment extends BaseFragment {
    //===================================
    // Layout//===================================
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    //===================================
    // Variable
    //===================================
    private DashStateAdapter mAdapter;
    private ArrayList<DSH_VO> mList;


    public CommuteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_commute, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestDSH_SELECT();
    }

    private void initLayout() {
        listView = view.findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestDSH_SELECT());
    }

    private void goWorkRecord(int position) {
        Intent intent = new Intent(mContext, BoardMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("DSH_GB", mList.get(position).DSH_GB);
//        intent.putExtra(BoardMain.WORK_STATE, mList.get(position));
        mContext.startActivity(intent);
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new DashStateAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }


    public void requestDSH_SELECT() {

        System.out.println("$$$$$$$$$$$$$$$됐다 됐다 잘 됐다.");
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        openLoadingBar();

        String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<DSHModel> call = Http.commute(HttpBaseService.TYPE.POST).DSH_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mUser.Value.CTM_01,
                "",
                "",
                "",
                ""
        );

        call.enqueue(new Callback<DSHModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<DSHModel> call, Response<DSHModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<DSHModel> response = (Response<DSHModel>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<DSHModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


}
