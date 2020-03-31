package com.linktag.linkapp.ui.vac;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VADModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.VadVO;
import com.linktag.linkapp.value_object.VamVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.ui.vac.VadEditDetail.VAM_02;
import static com.linktag.linkapp.ui.vac.VadEditDetail.recyclerView_vad;
import static com.linktag.linkapp.ui.vac.VadEditDetail.tv_vadCnt;
import static com.linktag.linkapp.ui.vac.VadEditDetail.tv_vad_nodata;

public class VamRecycleAdapter2 extends RecyclerView.Adapter<VamRecycleAdapter2.ViewHolder> {

    private Context mContext;
    private ArrayList<VamVO> mList;
    private ArrayList<VadVO> mList_vad;
    private LayoutInflater mInflater;
    private View view;
    private VadRecycleAdapter mAdapter;

    private InterfaceUser mUser = InterfaceUser.getInstance();


    private int lastSelectedPosition = -1;


    VamRecycleAdapter2(Context context, ArrayList<VamVO> list) {
        mContext = context;
        mList = list;

    }

    public void setmAdapter(VadRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public VamRecycleAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = mInflater.inflate(R.layout.listitem_vam_list2, parent, false);

        VamRecycleAdapter2.ViewHolder viewHolder = new VamRecycleAdapter2.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(mList.get(position).VAM_03);

        if (lastSelectedPosition == position) {
            viewHolder.radioButton.setChecked(true);
            viewHolder.tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
            requestVAD_SELECT(mList.get(position));

        } else {
            viewHolder.radioButton.setChecked(false);
            viewHolder.tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.path_black));
        }

        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VAM_02 = mList.get(position).VAM_02;
                lastSelectedPosition = position;
                notifyDataSetChanged();
            }
        });

    }



    public void requestVAD_SELECT(VamVO vamVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                vamVO.VAM_ID,
                vamVO.VAM_01,
                vamVO.VAM_02
        );


        call.enqueue(new Callback<VADModel>() {
            @SuppressLint("HandlerLeak")
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

                            mList_vad = response.body().Data;
                            if (mList_vad == null)
                                mList_vad = new ArrayList<>();
                            mAdapter.updateData(mList_vad);
                            mAdapter.notifyDataSetChanged();

                            tv_vadCnt.setText("(" + mList_vad.size() + ")");
                            if (mList_vad.size() == 0) {
                                tv_vad_nodata.setVisibility(View.VISIBLE);
                                recyclerView_vad.setVisibility(View.GONE);

                            } else {
                                recyclerView_vad.setVisibility(View.VISIBLE);
                                tv_vad_nodata.setVisibility(View.GONE);
                            }


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

        private TextView tv_name;
        private RadioButton radioButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }

    public void updateData(ArrayList<VamVO> list) {
        mList = list;
    }


}
