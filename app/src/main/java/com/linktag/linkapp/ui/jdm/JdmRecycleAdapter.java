package com.linktag.linkapp.ui.jdm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.JdmVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JdmRecycleAdapter extends RecyclerView.Adapter<JdmRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<JdmVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    private Calendar nextDay = Calendar.getInstance();


    JdmRecycleAdapter(Context context, ArrayList<JdmVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public JdmRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_jdm_list, parent, false);
        JdmRecycleAdapter.ViewHolder viewHolder = new JdmRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Calendar sCalendar = Calendar.getInstance();
        sCalendar.set(Calendar.YEAR, Integer.parseInt(mList.get(position).JDM_96.substring(0, 4)));
        sCalendar.set(Calendar.MONTH, Integer.parseInt(mList.get(position).JDM_96.substring(4, 6)) - 1);
        sCalendar.set(Calendar.DATE, Integer.parseInt(mList.get(position).JDM_96.substring(6, 8)));

        switch (mList.get(position).JDM_06) {
            case "0":
                sCalendar.add(Calendar.DATE, -3);
                break;
            case "1":
                sCalendar.add(Calendar.DATE, -5);
                break;
            case "2":
                sCalendar.add(Calendar.DATE, -7);
                break;
            case "3":
                sCalendar.add(Calendar.DATE, -15);
                break;
            case "4":
                sCalendar.add(Calendar.DATE, -30);
                break;
        }

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(Integer.parseInt(mList.get(position).JDM_96.substring(0, 4)),
                Integer.parseInt(mList.get(position).JDM_96.substring(4, 6)) - 1,
                Integer.parseInt(mList.get(position).JDM_96.substring(6, 8)));

        int dcount = (int) ((calendar.getTimeInMillis() - sCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000));

        if (dcount < 0) dcount = 0;

        int totalProgress = (int) ((dCalendar.getTimeInMillis() - sCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000));


        viewHolder.progressBar.setMax(totalProgress);
        viewHolder.progressBar.setProgress(dcount+1);
//        if (totalProgress <= dcount ) {
//            viewHolder.progressBar.getProgressDrawable().setColorFilter(0xFFE97D6C, PorterDuff.Mode.SRC_IN);
//        }

        if (mList.get(position).JDM_04.equals("")) {
            viewHolder.tv_D_day.setText("0");
        } else {
            String year = mList.get(position).JDM_04.substring(0, 4);
            String month = mList.get(position).JDM_04.substring(4, 6);
            String dayOfMonth = mList.get(position).JDM_04.substring(6, 8);
            Calendar aCalendar = Calendar.getInstance();
            aCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(dayOfMonth));

            int count = (int) ((calendar.getTimeInMillis() - aCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000));
            viewHolder.tv_D_day.setText(String.valueOf(count));

        }
        viewHolder.tv_name.setText(mList.get(position).JDM_02);
        viewHolder.tv_memo.setText(mList.get(position).JDM_03);
        viewHolder.tv_nextDay.setText(mList.get(position).JDM_96.substring(0,4)+"."+mList.get(position).JDM_96.substring(4,6)+"."+mList.get(position).JDM_96.substring(6,8));

        if (Integer.parseInt(mList.get(position).JDM_96.substring(0, 8)) <= Integer.parseInt(formatDate.format(calendar.getTime()))) {
            viewHolder.imageView_check.setImageResource(R.drawable.ic_check_off);
        } else {
            viewHolder.imageView_check.setImageResource(R.drawable.ic_check_on
            );
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
                    Toast.makeText(mContext, "[" + mList.get(position).JDM_02 + "]- 알림 OFF", Toast.LENGTH_SHORT).show();
                } else if (mList.get(position).ARM_03.equals("N")) {
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                    Toast.makeText(mContext, "[" + mList.get(position).JDM_02 + "]- 알림 ON", Toast.LENGTH_SHORT).show();
                }

                ArmVO armVO = new ArmVO();

                armVO.setARM_ID(mList.get(position).JDM_ID);
                armVO.setARM_01(mList.get(position).JDM_01);
                armVO.setARM_02(mUser.Value.OCM_01);
                armVO.setARM_03(mList.get(position).ARM_03);
                armVO.setARM_95("");
                armVO.setARM_90(mList.get(position).JDM_02);
                armVO.setARM_91(mList.get(position).JDM_03);
                armVO.setARM_92(mList.get(position).JDM_96);
                armVO.setARM_93("");
                armVO.setARM_94("N");
                armVO.setARM_98(mUser.Value.OCM_01);

                requestARM_CONTROL(armVO, position);

            }
        });

        viewHolder.imageView_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(mList.get(position).JDM_96.substring(0, 8)) <= Integer.parseInt(formatDate.format(calendar.getTime()))) {
                    requestJMD_CONTROL(viewHolder, mList.get(position));
                } else {

                    new AlertDialog.Builder(mContext)
                            .setMessage("이전 청소이력이 있습니다. 청소확인 처리하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestJMD_CONTROL(viewHolder, mList.get(position));
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                    return;
                }
            }
        });


    }

    public void requestJMD_CONTROL(ViewHolder viewHolder, JdmVO jdmVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        nextDay.set(Calendar.YEAR, Integer.parseInt(jdmVO.JDM_96.substring(0, 4)));
        nextDay.set(Calendar.MONTH, Integer.parseInt(jdmVO.JDM_96.substring(4, 6)) - 1);
        nextDay.set(Calendar.DATE, Integer.parseInt(jdmVO.JDM_96.substring(6, 8)));

        boolean date = true;
        while (date) {
            switch (jdmVO.JDM_06) {
                case "0":
                    nextDay.add(Calendar.DATE, 3);
                    break;
                case "1":
                    nextDay.add(Calendar.DATE, 5);
                    break;
                case "2":
                    nextDay.add(Calendar.DATE, 7);
                    break;
                case "3":
                    nextDay.add(Calendar.DATE, 15);
                    break;
                case "4":
                    nextDay.add(Calendar.DATE, 30);
                    break;
            }
            if (Integer.parseInt(formatDate.format(nextDay.getTime())) > Integer.parseInt(formatDate.format(calendar.getTime()))) {
                date = false;
            }
        }


        Call<JDMModel> call = Http.jdm(HttpBaseService.TYPE.POST).JDM_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_NEXT",
                jdmVO.JDM_ID,
                jdmVO.JDM_01,
                jdmVO.JDM_02,
                jdmVO.JDM_03,
                jdmVO.JDM_04,
                jdmVO.JDM_05,
                jdmVO.JDM_06,
                formatDate.format(nextDay.getTime())+jdmVO.JDM_96.substring(8,12),
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                jdmVO.ARM_03
        );


        call.enqueue(new Callback<JDMModel>() {
            @Override
            public void onResponse(Call<JDMModel> call, Response<JDMModel> response) {

                int dcount = (int) ((calendar.getTimeInMillis() - nextDay.getTimeInMillis()) / (24 * 60 * 60 * 1000));

                viewHolder.imageView_check.setImageResource(R.drawable.ic_check_on);
                viewHolder.tv_nextDay.setText(format.format(nextDay.getTime()));
                viewHolder.progressBar.setProgress(dcount);
            }

            @Override
            public void onFailure(Call<JDMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

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
        TextView tv_D_day;
        ProgressBar progressBar;
        ImageView imageView_check;
        TextView tv_nextDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_memo = itemView.findViewById(R.id.tv_memo);
            tv_D_day = itemView.findViewById(R.id.tv_D_day);
            imageView_check = itemView.findViewById(R.id.imageView_check);
            tv_nextDay = itemView.findViewById(R.id.tv_nextDay);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    JdmVO jdmvo = new JdmVO();
                    jdmvo.setJDM_ID(mList.get(position).JDM_ID);
                    jdmvo.setJDM_01(mList.get(position).JDM_01);
                    jdmvo.setJDM_02(mList.get(position).JDM_02);
                    jdmvo.setJDM_03(mList.get(position).JDM_03);
                    jdmvo.setJDM_04(mList.get(position).JDM_04);
                    jdmvo.setJDM_05(mList.get(position).JDM_05);
                    jdmvo.setJDM_06(mList.get(position).JDM_06);
                    jdmvo.setJDM_96(mList.get(position).JDM_96);
                    jdmvo.setJDM_97(mList.get(position).JDM_97);
                    jdmvo.setARM_03(mList.get(position).ARM_03);
                    jdmvo.setARM_04(mList.get(position).ARM_04);

                    Intent intent = new Intent(mContext, DetailJdm.class);
                    intent.putExtra("JdmVO", jdmvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<JdmVO> list) {
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
