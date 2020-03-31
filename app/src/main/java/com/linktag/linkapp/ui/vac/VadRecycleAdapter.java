package com.linktag.linkapp.ui.vac;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VADModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.TrdVO;
import com.linktag.linkapp.value_object.VadVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.ui.vac.VadEditDetail.recyclerView_vad;
import static com.linktag.linkapp.ui.vac.VadEditDetail.tv_vadCnt;
import static com.linktag.linkapp.ui.vac.VadEditDetail.tv_vad_nodata;

public class VadRecycleAdapter extends RecyclerView.Adapter<VadRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VadVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private VadRecycleAdapter mAdapter;

    VadRecycleAdapter(Context context, ArrayList<VadVO> list) {
        mContext = context;
        mList = list;
    }

    public void setmAdapter(VadRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public VadRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_vad_list, parent, false);
        VadRecycleAdapter.ViewHolder viewHolder = new VadRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(mList.get(position).VAD_04);
        viewHolder.tv_date.setText(stringTodateFormat(mList.get(position).VAD_96));
        viewHolder.tv_time.setText(stringTotimeFormat(mList.get(position).VAD_96));

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VadVO vadVO = new VadVO();

                vadVO.setVAD_ID(mList.get(position).VAD_ID);
                vadVO.setVAD_01(mList.get(position).VAD_01);
                vadVO.setVAD_02(mList.get(position).VAD_02);
                vadVO.setVAD_03(mList.get(position).VAD_03);

                requestVAD_CONTROL(vadVO);

//                requestLOG_CONTROL("2",mContext.getString(R.string.trp_Alarm_delete) + " " + am_pm+ hourOfDay +":" +minute);


            }
        });

    }

    public void requestVAD_CONTROL(VadVO vadVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                vadVO.VAD_ID,
                vadVO.VAD_01,
                vadVO.VAD_02,
                vadVO.VAD_03,
                "",
                "",
                "",
                ""
        );


        call.enqueue(new Callback<VADModel>() {
            @Override
            public void onResponse(Call<VADModel> call, Response<VADModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VADModel> response = (Response<VADModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();
                            tv_vadCnt.setText("(" + mList.size() + ")");
                            if (mList.size() == 0) {
                                tv_vad_nodata.setVisibility(View.VISIBLE);
                                recyclerView_vad.setVisibility(View.GONE);
                            } else {
                                recyclerView_vad.setVisibility(View.VISIBLE);
                                tv_vad_nodata.setVisibility(View.GONE);
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VADModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_name;
        TextView tv_date;
        TextView tv_time;

        Button btn_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    public void updateData(ArrayList<VadVO> list) {
        mList = list;
    }

    public String stringTodateFormat(String str) {
        String retStr ="";
        //yyyy.MM.dd
        retStr = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8);
        return retStr;
    }
    public String stringTotimeFormat(String str) {
        String retStr ="";
        //hh:mm

        retStr = str.substring(8,10) + " : " + str.substring(10,12);
        return retStr;
    }

}
