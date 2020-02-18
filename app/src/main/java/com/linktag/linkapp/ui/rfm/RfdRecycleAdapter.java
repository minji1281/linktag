package com.linktag.linkapp.ui.rfm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm_service.AlarmHATT;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.RfdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RfdRecycleAdapter extends RecyclerView.Adapter<RfdRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RfdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    RfdRecycleAdapter(Context context, ArrayList<RfdVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public RfdRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_rfd_list, parent, false);
        RfdRecycleAdapter.ViewHolder viewHolder = new RfdRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String Format = mList.get(position).RFD_96;
        String dateFormat = Format.substring(2, 4) + "." + Format.substring(4, 6) + "." + Format.substring(6, 8);
        String timeFormat = Format.substring(8, 10) + ":" + Format.substring(10);

        viewHolder.tv_name.setText(mList.get(position).RFD_03);
        viewHolder.tv_memo.setText(mList.get(position).RFD_04);
        viewHolder.tv_date.setText(dateFormat);
        viewHolder.tv_time.setText(timeFormat);

        if (mList.get(position).ARM_03.equals("Y")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
        } else if (mList.get(position).ARM_03.equals("N")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
        }

        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArmVO armVO = new ArmVO();

                armVO.setARM_ID(mList.get(position).RFD_ID);
                armVO.setARM_01(mList.get(position).RFD_01);
                armVO.setARM_02(mUser.Value.OCM_01);
                armVO.setARM_03(mList.get(position).ARM_03);
                armVO.setARM_95(mList.get(position).RFD_02);
                armVO.setARM_98(mUser.Value.OCM_01);

                requestARM_CONTROL(armVO, position);

                if (mList.get(position).ARM_03.equals("Y")) {
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
                    Toast.makeText(mContext, "[" + mList.get(position).RFD_02 + "]- 알림 OFF", Toast.LENGTH_SHORT).show();
                } else {
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                    Toast.makeText(mContext, "[" + mList.get(position).RFD_02 + "]- 알림 ON", Toast.LENGTH_SHORT).show();


                }
            }
        });

    }

    public void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm_Receiver.class);
        intent.putExtra("notify_id", alarmId);
        intent.putExtra("ContentTitle", "");
        intent.putExtra("contentText", "");


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_memo;
        TextView tv_date;
        TextView tv_time;
        Boolean bool;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_memo = itemView.findViewById(R.id.tv_memo);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            bool = false;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    RfdVO rfdvo = new RfdVO();
                    rfdvo.setRFD_ID(mList.get(position).RFD_ID);
                    rfdvo.setRFD_01(mList.get(position).RFD_01);
                    rfdvo.setRFD_02(mList.get(position).RFD_02);
                    rfdvo.setRFD_03(tv_name.getText().toString());
                    rfdvo.setRFD_04(tv_memo.getText().toString());
                    rfdvo.setRFD_05(mList.get(position).RFD_05);
                    rfdvo.setRFD_06(mList.get(position).RFD_06);
                    rfdvo.setRFD_96(mList.get(position).RFD_96);
                    rfdvo.setARM_03(mList.get(position).ARM_03);
                    rfdvo.setARM_04(mList.get(position).ARM_04);

                    Intent intent = new Intent(mContext, DetailRfd.class);
                    intent.putExtra("RfdVO", rfdvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<RfdVO> list) {
        mList = list;
    }


    public void requestARM_CONTROL(ArmVO armVO, int position) {

        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            //BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<ARMModel> call = Http.arm(HttpBaseService.TYPE.POST).ARM_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_2",
                armVO.ARM_ID,
                armVO.ARM_01,
                armVO.ARM_02,
                armVO.ARM_03,
                armVO.ARM_95,
                armVO.ARM_98
        );

        call.enqueue(new Callback<ARMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ARMModel> call, Response<ARMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<ARMModel> response = (Response<ARMModel>) msg.obj;

                            ArrayList<ArmVO> responseData = response.body().Data;

                            mList.get(position).setARM_04(responseData.get(0).ARM_04);

                            if (mList.get(position).ARM_03.equals("Y")) {
                                mList.get(position).setARM_03("N");

                               // cancelAlarm(mContext, mList.get(position).ARM_04);

                            } else {
                                mList.get(position).setARM_03("Y");

//                                Intent intent = new Intent(mContext, Alarm_Receiver.class);
//                                intent.putExtra("notify_id", responseData.get(0).ARM_04);
//                                intent.putExtra("calDateTime", mList.get(position).RFD_96);
//                                intent.putExtra("contentTitle", "장독관리" + mList.get(position).RFD_02);
//                                intent.putExtra("contentText", mList.get(position).RFD_03);
//                                intent.putExtra("className", ".ui.intro.Intro");
//                                intent.putExtra("gotoActivity", ".ui.rfd.RFDMain");
//                                intent.putExtra("gotoLogin", ".ui.login.Login");
//                                intent.putExtra("gotoMain", ".ui.main.Main");
//
//
//                                RfdVO rfdvo = new RfdVO();
//                                rfdvo.setRFD_01( mList.get(position).getRFD_01());
//                                rfdvo.setRFD_02( mList.get(position).getRFD_02());
//                                rfdvo.setRFD_03( mList.get(position).getRFD_03());
//                                rfdvo.setRFD_04( mList.get(position).getRFD_04());
//                                rfdvo.setRFD_96( mList.get(position).getRFD_96());
//                                rfdvo.setARM_03( mList.get(position).getARM_03());
//
//                                intent.putExtra("mList",mList);
//
//                                intent.putExtra("RfdVO", rfdvo);
//
//
//                                new AlarmHATT(mContext).Alarm(intent);

                            }

                        }

                    }
                }.sendMessage(msg);
            }

            public void onFailure(Call<ARMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });
    }

}
