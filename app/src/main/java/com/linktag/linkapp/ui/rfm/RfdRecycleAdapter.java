package com.linktag.linkapp.ui.rfm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ProgressBar;
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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RfdRecycleAdapter extends RecyclerView.Adapter<RfdRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RfdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private Calendar calendar = Calendar.getInstance();

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

        viewHolder.tv_name.setText(mList.get(position).RFD_03);
        viewHolder.tv_memo.setText(mList.get(position).RFD_04);
        viewHolder.tv_D_day.setText(mList.get(position).RFD_96.substring(0, 4) + "." + mList.get(position).RFD_96.substring(4, 6) + "." + mList.get(position).RFD_96.substring(6, 8));

        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(Calendar.YEAR, Integer.parseInt(mList.get(position).RFD_96.substring(0, 4)));
        dCalendar.set(Calendar.MONTH, Integer.parseInt(mList.get(position).RFD_96.substring(4, 6)) - 1);
        dCalendar.set(Calendar.DATE, Integer.parseInt(mList.get(position).RFD_96.substring(6, 8)));

        dCalendar.clear(Calendar.HOUR);
        dCalendar.clear(Calendar.MINUTE);
        dCalendar.clear(Calendar.SECOND);
        dCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

        Calendar sCalendar = Calendar.getInstance();
        sCalendar.set(Calendar.YEAR, Integer.parseInt(mList.get(position).RFD_05.substring(0, 4)));
        sCalendar.set(Calendar.MONTH, Integer.parseInt(mList.get(position).RFD_05.substring(4, 6)) - 1);
        sCalendar.set(Calendar.DATE, Integer.parseInt(mList.get(position).RFD_05.substring(6, 8)));

        sCalendar.clear(Calendar.HOUR);
        sCalendar.clear(Calendar.MINUTE);
        sCalendar.clear(Calendar.SECOND);
        sCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화


        long dDayDiff = calendar.getTimeInMillis() - sCalendar.getTimeInMillis();
        int dcount = (int)(Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));


        long dDayDiff2 = dCalendar.getTimeInMillis() - sCalendar.getTimeInMillis();
        int totalProgress = (int)(Math.floor(TimeUnit.HOURS.convert(dDayDiff2, TimeUnit.MILLISECONDS) / 24f));


        long dDayDiff3 = dCalendar.getTimeInMillis() - calendar.getTimeInMillis();
        int count = (int)(Math.floor(TimeUnit.HOURS.convert(dDayDiff3, TimeUnit.MILLISECONDS) / 24f));

//        int dcount = (int) ((calendar.getTimeInMillis() - sCalendar.getTimeInMillis()) / 86400000);
//        int totalProgress = (int) ((dCalendar.getTimeInMillis() - sCalendar.getTimeInMillis()) / 86400000);
//
//        int count = (int) ((dCalendar.getTimeInMillis() - calendar.getTimeInMillis()) /  86400000);


        if(count < 0){
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_red_8dp);
            viewHolder.btn_label.setText("유통기한 마감");
        }
        else if(count == 0){
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_yellow_8dp);
            viewHolder.btn_label.setText("오늘까지");
        }
        else if(count <= 7){
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_blue_8dp);
            viewHolder.btn_label.setText("7일 이하 남음");
        }
        else if(count >=15){
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_green_8dp);
            viewHolder.btn_label.setText("15일이상 남음");
        }
        else if(count < 15 &&  count >=7){
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_blue_8dp);
            viewHolder.btn_label.setText("15일 미만 남음");
        }
        else if(count < 0){
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_red_8dp);
            viewHolder.btn_label.setText("유통기한 마감");
        }
        else {
            viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_yellow_8dp);
            viewHolder.btn_label.setText( count+"일 남음");
        }

        if (count == 0) {
         //   viewHolder.tv_Deadline.setText("오늘까지 마감");
            viewHolder.progressBar.getProgressDrawable().setColorFilter(null);
            viewHolder.progressBar.setMax(1);
            viewHolder.progressBar.setProgress(1);

        } else if (count < 0) {
            //viewHolder.tv_Deadline.setText("유통기한 지남");
            viewHolder.progressBar.setMax(1);
            viewHolder.progressBar.setProgress(1);
            viewHolder.progressBar.getProgressDrawable().setColorFilter(0xFFE97D6C, PorterDuff.Mode.SRC_IN);
        } else {
           // viewHolder.tv_Deadline.setText(String.valueOf(count) + "일 남음");
            viewHolder.progressBar.setMax(totalProgress);
            viewHolder.progressBar.setProgress(dcount);
            viewHolder.progressBar.getProgressDrawable().setColorFilter(null);
        }

        if (mList.get(position).ARM_03.equals("Y")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
        } else if (mList.get(position).ARM_03.equals("N")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
        }

        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mList.get(position).ARM_03.equals("Y")) {
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
                    Toast.makeText(mContext, "[" + mList.get(position).RFD_03 + "]- 알림 OFF", Toast.LENGTH_SHORT).show();
                } else if (mList.get(position).ARM_03.equals("N")) {
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                    Toast.makeText(mContext, "[" + mList.get(position).RFD_03 + "]- 알림 ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "알림일자를 지정하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArmVO armVO = new ArmVO();

                armVO.setARM_ID(mList.get(position).RFD_ID);
                armVO.setARM_01(mList.get(position).RFD_02);
                armVO.setARM_02(mUser.Value.OCM_01);
                armVO.setARM_03(mList.get(position).ARM_03);
                armVO.setARM_95(mList.get(position).RFD_01);
                armVO.setARM_90(mList.get(position).RFD_03);
                armVO.setARM_91(mList.get(position).RFD_04);
                armVO.setARM_92(mList.get(position).RFD_96);
                armVO.setARM_93("");
                armVO.setARM_94("N");
                armVO.setARM_98(mUser.Value.OCM_01);

                requestARM_CONTROL(armVO, position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_memo;
        TextView tv_D_day;
        TextView tv_Deadline;
        ProgressBar progressBar;
        Button btn_label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_memo = itemView.findViewById(R.id.tv_memo);
            tv_D_day = itemView.findViewById(R.id.tv_D_day);
            //tv_Deadline = itemView.findViewById(R.id.tv_Deadline);
            btn_label =itemView.findViewById(R.id.btn_label);

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
                armVO.ARM_90,
                armVO.ARM_91,
                armVO.ARM_92,
                armVO.ARM_93,
                armVO.ARM_94,
                armVO.ARM_95,
                armVO.ARM_98
        );

        call.enqueue(new Callback<ARMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ARMModel> call, Response<ARMModel> response) {

                if (mList.get(position).ARM_03.equals("Y")) {
                    mList.get(position).setARM_03("N");

                } else {
                    mList.get(position).setARM_03("Y");

                }
            }

            public void onFailure(Call<ARMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });
    }

}
