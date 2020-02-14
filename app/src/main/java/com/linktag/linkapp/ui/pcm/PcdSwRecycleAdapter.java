package com.linktag.linkapp.ui.pcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.PCDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PcdVO;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PcdSwRecycleAdapter extends RecyclerView.Adapter<PcdSwRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PcdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private PcdSwRecycleAdapter mAdapter;

    private HashMap<String, String> map = new HashMap<String, String>();


    PcdSwRecycleAdapter(Context context, ArrayList<PcdVO> list) {
        mContext = context;
        mList = list;

        map.put("0", "선택");
        map.put("1", "운영체제");
        map.put("2", "그래픽드라");
        map.put("3", "어도비");
        map.put("4", "백신");
        map.put("5", "기타");
    }


    public void setmAdapter(PcdSwRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public PcdSwRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_pcd_list, parent, false);
        PcdSwRecycleAdapter.ViewHolder viewHolder = new PcdSwRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        viewHolder.tv_name.setText(map.get(mList.get(position).PCD_04));
        viewHolder.tv_detail.setText(mList.get(position).PCD_05);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                requestPCD_CONTROL(mList.get(position));
            }
        });

    }


    public void requestPCD_CONTROL(PcdVO pcdVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        String GUBUN = pcdVO.GUBUN;

        Call<PCDModel> call = Http.pcd(HttpBaseService.TYPE.POST).PCD_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                pcdVO.PCD_ID,
                pcdVO.PCD_01,
                pcdVO.PCD_02,
                pcdVO.PCD_03,
                pcdVO.PCD_04,
                pcdVO.PCD_05,
                pcdVO.PCD_98
        );


        call.enqueue(new Callback<PCDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCDModel> call, Response<PCDModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<PCDModel> response = (Response<PCDModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<PCDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_detail;
        private ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_detail = itemView.findViewById(R.id.tv_detail);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }

    public void updateData(ArrayList<PcdVO> list) {
        mList = list;
    }


}
