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
import com.linktag.linkapp.model.CMTModel;
import com.linktag.linkapp.model.DSHModel;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.board.BoardMain;
import com.linktag.linkapp.ui.dash_state.CommentStateAdapter;
import com.linktag.linkapp.ui.work_record.WorkRecordActivity;
import com.linktag.linkapp.ui.dash_state.DashStateAdapter;
import com.linktag.linkapp.value_object.CMT_VO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkFragment extends BaseFragment {
    //===================================
    // Layout//===================================
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    //===================================
    // Variable
    //===================================
    private CommentStateAdapter mAdapter;
    private ArrayList<CMT_VO> mList;


    public WorkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_work, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestCMT_SELECT();
    }

    private void initLayout() {
        listView = view.findViewById(R.id.listView);
       // listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestCMT_SELECT());
    }

    private void goWorkRecord(int position) {
        Intent intent = new Intent(mContext, BoardMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("CMT_GB", mList.get(position).CMT_GB);
//        intent.putExtra(BoardMain.WORK_STATE, mList.get(position));
        mContext.startActivity(intent);
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new CommentStateAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }


    public void requestCMT_SELECT() {
System.out.println("$$$$$$$$$$$$$$$됐다 됐다 잘 됐다.");
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

    //    openLoadingBar();



        Call<CMTModel> call = Http.commute(HttpBaseService.TYPE.POST).CMT_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mUser.Value.CTM_01,
                "",
                "",
                "",
                ""
        );

        call.enqueue(new Callback<CMTModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CMTModel> call, Response<CMTModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CMTModel> response = (Response<CMTModel>) msg.obj;

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
            public void onFailure(Call<CMTModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


}
