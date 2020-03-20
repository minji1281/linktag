package com.linktag.linkapp.ui.trp;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.pcm.DetailPcm;
import com.linktag.linkapp.value_object.TrdVO;
import com.linktag.linkapp.value_object.TrpVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.ui.trp.DetailTrp.tv_alarmLabel;
import static com.linktag.linkapp.ui.trp.DetailTrp.recyclerView;
import static com.linktag.linkapp.ui.trp.DetailTrp.alarmState;
import static com.linktag.linkapp.ui.trp.DetailTrp.imageView;
import static com.linktag.linkapp.ui.trp.DetailTrp.trpVO;

public class TrdRecycleAdapter extends RecyclerView.Adapter<TrdRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TrdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private TrdRecycleAdapter mAdapter;
    private InterfaceUser mUser;

    private String am_pm;
    private int hourOfDay;

    TrdRecycleAdapter(Context context, ArrayList<TrdVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    public void setmAdapter(TrdRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public TrdRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_trd_list
                , parent, false);
        TrdRecycleAdapter.ViewHolder viewHolder = new TrdRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        am_pm = "";
        hourOfDay = Integer.parseInt(mList.get(position).TRD_96.substring(8, 10));
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            am_pm = mContext.getString(R.string.trp_Pm);
        } else {
            am_pm = mContext.getString(R.string.trp_Am);
        }
        String minute = mList.get(position).TRD_96.substring(10);

        viewHolder.tv_dateTime.setText(am_pm + " " + hourOfDay + ":" + minute);

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrdVO trdVO = new TrdVO();

                trdVO.setTRD_ID(mList.get(position).TRD_ID);
                trdVO.setTRD_01(mList.get(position).TRD_01);
                trdVO.setTRD_02(mList.get(position).TRD_02);

                requestTRD_CONTROL(trdVO, position);

                requestLOG_CONTROL("2",mContext.getString(R.string.trp_Alarm_delete) + " " + am_pm+ hourOfDay +":" +minute);


            }
        });

    }


    private void requestLOG_CONTROL(String LOG_03, String LOG_04){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mContext, mContext.getString(R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                trpVO.TRP_ID,
                trpVO.TRP_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_TRPL_CONTROL"
        );

        call.enqueue(new Callback<LOG_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response){

            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t){
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }



    public void requestTRD_CONTROL(TrdVO trdVO, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<TRDModel> call = Http.trd(HttpBaseService.TYPE.POST).TRD_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                trdVO.TRD_ID,
                trdVO.TRD_01,
                trdVO.TRD_02,
                "",
                mUser.Value.OCM_01,

                "",
                "",
                "",
                ""
        );


        call.enqueue(new Callback<TRDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TRDModel> call, Response<TRDModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<TRDModel> response = (Response<TRDModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();
                            DetailTrp.tv_alarmCnt.setText("("+mList.size()+")");
                            if(mList.size()==0){
                                tv_alarmLabel.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                alarmState = false;

                                imageView.setImageResource(R.drawable.alarm_state_off);
                                requestTRP_CONTROL(trdVO);
                                trpVO.setTRP_08("N");

                            }
                            else{
                                recyclerView.setVisibility(View.VISIBLE);
                                tv_alarmLabel.setVisibility(View.GONE);
                                alarmState = true;
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<TRDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }



    public void requestTRP_CONTROL(TrdVO trdVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<TRPModel> call = Http.trp(HttpBaseService.TYPE.POST).TRP_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_2",
                trdVO.TRD_ID,
                trdVO.TRD_01,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                mUser.Value.OCM_01

        );


        call.enqueue(new Callback<TRPModel>() {
            @Override
            public void onResponse(Call<TRPModel> call, Response<TRPModel> response) {

            }

            @Override
            public void onFailure(Call<TRPModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_dateTime;
        Button btn_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_dateTime = itemView.findViewById(R.id.tv_dateTime);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    public void updateData(ArrayList<TrdVO> list) {
        mList = list;
    }


}
