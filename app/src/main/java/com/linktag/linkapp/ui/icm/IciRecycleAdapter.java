package com.linktag.linkapp.ui.icm;

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
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ICRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IciRecycleAdapter extends RecyclerView.Adapter<IciRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private CtdVO intentVO;

    IciRecycleAdapter(Context context, ArrayList<String> list, CtdVO intentVO_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();

        intentVO = intentVO_tmp;
    }

    @NonNull
    @Override
    public IciRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_ici
                , parent, false);
        IciRecycleAdapter.ViewHolder viewHolder = new IciRecycleAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tvIcon.setText(mList.get(position));
        viewHolder.tvName.setText(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvName = itemView.findViewById(R.id.tvName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    requestICR_CONTROL("INSERT", position);
                }
            });
        }
    }

    public void updateData(ArrayList<String> list) {
        mList = list;
    }

    private void requestICR_CONTROL(String GUB, int position) {

        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String ICR_ID = intentVO.CTN_02; //컨테이너
        String ICR_01 = intentVO.CTD_01; //Master일련번호(ICM_01)
        String ICR_02 = ""; //일련번호
        String ICR_03 = ""; //일자(YYYYMMDD)

        String ICR_04 = ""; //시작시간(HHMM)
        String ICR_05 = ""; //종료시간(HHMM)
        String ICR_06 = String.valueOf(position); //항목(ici)
        String ICR_07 = ""; //상세
        String ICR_08 = ""; //메모

        String ICR_98 = mUser.Value.OCM_01; //최종수정자

        Call<ICRModel> call = Http.icr(HttpBaseService.TYPE.POST).ICR_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                ICR_ID,
                ICR_01,
                ICR_02,
                ICR_03,

                ICR_04,
                ICR_05,
                ICR_06,
                ICR_07,
                ICR_08,

                ICR_98
        );

        call.enqueue(new Callback<ICRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ICRModel> call, Response<ICRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<ICRModel> response = (Response<ICRModel>) msg.obj;

                            IcrMain.mList = response.body().Data;
                            if(IcrMain.mList == null)
                                IcrMain.mList = new ArrayList<>();

                            IcrMain.empty.setVisibility(View.GONE);

                            IcrMain.mAdapter.updateData(IcrMain.mList);
                            IcrMain.mAdapter.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ICRModel> call, Throwable t){
                Log.d("ICR_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

}
