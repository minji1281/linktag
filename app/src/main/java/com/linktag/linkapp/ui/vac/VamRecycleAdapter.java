package com.linktag.linkapp.ui.vac;

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
import com.linktag.linkapp.model.VAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.pcm.PcmDetail;
import com.linktag.linkapp.value_object.PcdVO;
import com.linktag.linkapp.value_object.VadVO;
import com.linktag.linkapp.value_object.VamVO;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VamRecycleAdapter extends RecyclerView.Adapter<VamRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VamVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private VamRecycleAdapter mAdapter;

    private InterfaceUser mUser = InterfaceUser.getInstance();

    private String[] str_save_text;

    VamRecycleAdapter(Context context, ArrayList<VamVO> list) {
        mContext = context;
        mList = list;

    }

    public void setmAdapter(VamRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public VamRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_vam_list, parent, false);
        VamRecycleAdapter.ViewHolder viewHolder = new VamRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        viewHolder.tv_name.setText(mList.get(position).VAM_03);

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                requestVAM_CONTROL(mList.get(position));
            }
        });

    }


    public void requestVAM_CONTROL(VamVO vamVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<VAMModel> call = Http.vam(HttpBaseService.TYPE.POST).VAM_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                vamVO.VAM_ID,
                vamVO.VAM_01,
                vamVO.VAM_02,
                vamVO.VAM_03,
                vamVO.VAM_98
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
                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                            requestLOG_CONTROL(vamVO.VAM_ID, vamVO.VAM_01,"2","" +" " + vamVO.VAM_03 + " " + str_save_text[7]);

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
        private Button btn_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    public void updateData(ArrayList<VamVO> list) {
        mList = list;
    }


}
