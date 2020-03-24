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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LOG_Model;
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

public class PcdHwRecycleAdapter extends RecyclerView.Adapter<PcdHwRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PcdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private PcdHwRecycleAdapter mAdapter;

    private InterfaceUser mUser = InterfaceUser.getInstance();
    private HashMap<String, String> map = new HashMap<String, String>();



    PcdHwRecycleAdapter(Context context, ArrayList<PcdVO> list) {
        mContext = context;
        mList = list;

        String[] str = mContext.getResources().getStringArray(R.array.hw);

        map.put("0", str[0]);
        map.put("1", str[1]);
        map.put("2", str[2]);
        map.put("3", str[3]);
        map.put("4", str[4]);
        map.put("5", str[5]);
        map.put("6", str[6]);
        map.put("7", str[7]);
        map.put("8", str[8]);
        map.put("9", str[9]);
        map.put("10",str[10]);

    }

    public void setmAdapter(PcdHwRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public PcdHwRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_pcd_list, parent, false);
        PcdHwRecycleAdapter.ViewHolder viewHolder = new PcdHwRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        viewHolder.tv_name.setText(map.get(mList.get(position).PCD_04));
        viewHolder.tv_detail.setText(mList.get(position).PCD_05);

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
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
                            PcmDetail.tv_hwCnt.setText("("+mList.size()+")");
                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                            requestLOG_CONTROL(pcdVO.PCD_ID, pcdVO.PCD_01,"2",mContext.getString(R.string.pcm_text5) +" " + pcdVO.PCD_05 + " " + mContext.getString(R.string.pcm_text8));

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

    private void requestLOG_CONTROL(String LOG_ID, String LOG_01, String LOG_03, String LOG_04){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mContext, mContext.getString(R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                LOG_ID,
                LOG_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_PCML_CONTROL"
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


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_detail;
        private Button btn_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_detail = itemView.findViewById(R.id.tv_detail);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    public void updateData(ArrayList<PcdVO> list) {
        mList = list;
    }


}
