package com.linktag.linkapp.ui.iam;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.model.IAMModel;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.iam.IamDetail;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.IamVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IamRecycleAdapter extends RecyclerView.Adapter<IamRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<IamVO> mList;
    private ArrayList<IamVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");


    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Intent alarmIntent;


    private CtdVO intentVO;

    private String[] array_pattern;

    Filter listFilter;

    IamRecycleAdapter(Context context, ArrayList<IamVO> list, CtdVO intentVO) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        filteredmlist = list;
        this.intentVO = intentVO;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null)
            listFilter = new ListFilter();

        return listFilter;
    }


    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String charString = constraint.toString();
            if (charString.isEmpty()) {
                results.values = mList;
                results.count = mList.size();
            } else {
                ArrayList<IamVO> itemList = new ArrayList<>();
                for (IamVO item : mList) {
                    if (item.IAM_03.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredmlist = (ArrayList<IamVO>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public IamRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_iam_list, parent, false);
        IamRecycleAdapter.ViewHolder viewHolder = new IamRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        viewHolder.tv_name.setText(filteredmlist.get(position).IAM_03);

        viewHolder.tv_week.setText(patternToWeeks(filteredmlist.get(position).IAM_04));


        Calendar nextNotifyTime = Calendar.getInstance();
        nextNotifyTime.set(Calendar.YEAR, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(0, 4)));
        nextNotifyTime.set(Calendar.MONTH, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(4, 6)) - 1);
        nextNotifyTime.set(Calendar.DATE, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(6, 8)));
        nextNotifyTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(8, 10)));
        nextNotifyTime.set(Calendar.MINUTE, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(10, 12)));

        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy.MM.dd (EE) a hh : mm ", Locale.getDefault()).format(nextDate);

        viewHolder.tv_alarm.setText(date_text);

        if (filteredmlist.get(position).IAM_05.equals("Y")) {
            viewHolder.imageView.setImageResource(R.drawable.alarm_state_on);
        } else if (filteredmlist.get(position).IAM_05.equals("N")) {
            viewHolder.imageView.setImageResource(R.drawable.alarm_state_off);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filteredmlist.get(position).IAM_05.equals("Y")) {
                    viewHolder.imageView.setImageResource(R.drawable.alarm_state_off);
                    filteredmlist.get(position).setIAM_05("N");
                    cancelAlarmManager(filteredmlist.get(position).IAM_02);

                } else if (filteredmlist.get(position).IAM_05.equals("N")) {
                    viewHolder.imageView.setImageResource(R.drawable.alarm_state_on);
                    filteredmlist.get(position).setIAM_05("Y");


                    array_pattern = filteredmlist.get(position).IAM_04.split("");
                    // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                    Calendar nextNotifyTime = Calendar.getInstance();
                    nextNotifyTime.set(Calendar.YEAR, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(0, 4)));
                    nextNotifyTime.set(Calendar.MONTH, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(4, 6)) - 1);
                    nextNotifyTime.set(Calendar.DATE, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(6, 8)));
                    nextNotifyTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(8, 10)));
                    nextNotifyTime.set(Calendar.MINUTE, Integer.parseInt(filteredmlist.get(position).IAM_96.substring(10, 12)));

                    if (nextNotifyTime.before(Calendar.getInstance())) {

                        //요일별 더하기
                        int nowWeek = nextNotifyTime.get(Calendar.DAY_OF_WEEK);
                        int dateAdd = 1;
                        for (int i = 1; i < array_pattern.length; i++) {
                            if (nowWeek + i >= 8) {
                                nowWeek = 0;
                                i = 1;
                            }
                            if (array_pattern[nowWeek + i].equals("Y")) {
                                nextNotifyTime.add(Calendar.DATE, dateAdd);
                                break;
                            }
                            dateAdd++;
                        }
                    }

                    filteredmlist.get(position).setIAM_96(formatDate.format(nextNotifyTime.getTime())+formatTime.format(nextNotifyTime.getTime()));

                    Date nextDate = nextNotifyTime.getTime();
                    String date_text = new SimpleDateFormat("yyyy.MM.dd (EE) a hh : mm ", Locale.getDefault()).format(nextDate);
                    Toast.makeText(mContext, "[" + filteredmlist.get(position).IAM_03 + "]" +"\n"+ date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
                }

                requestIAM_CONTROL(filteredmlist.get(position));

            }
        });

    }

    public void requestIAM_CONTROL(IamVO iamVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<IAMModel> call = Http.iam(HttpBaseService.TYPE.POST).IAM_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_TIME",
                iamVO.IAM_ID,
                iamVO.IAM_01,
                iamVO.IAM_02,
                iamVO.IAM_03,
                iamVO.IAM_04,
                iamVO.IAM_05,
                iamVO.IAM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<IAMModel>() {
            @Override
            public void onResponse(Call<IAMModel> call, Response<IAMModel> response) {

                notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<IAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

    public void cancelAlarmManager(int IAM_02){
        alarmIntent = new Intent(mContext, AlarmReceiver.class);
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(mContext, IAM_02, alarmIntent, 0);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        alarmManager = null;
    }



    public String patternToWeeks(String pattern) {

        String[] array_pattern;
        String result_Weeks = "";
        array_pattern = pattern.split("");
        if (pattern.equals("YYYYYYY")) {
            result_Weeks = mContext.getString(R.string.trp_Everyday);
            return result_Weeks;
        } else {
            for (int i = 0; i < array_pattern.length; i++) {
                if (array_pattern[i].equals("Y") && i == 1) {
                    result_Weeks += mContext.getString(R.string.trp_Sun) + " ";
                } else if (array_pattern[i].equals("Y") && i == 2) {
                    result_Weeks += mContext.getString(R.string.trp_Mon) + " ";
                } else if (array_pattern[i].equals("Y") && i == 3) {
                    result_Weeks += mContext.getString(R.string.trp_Tue) + " ";
                } else if (array_pattern[i].equals("Y") && i == 4) {
                    result_Weeks += mContext.getString(R.string.trp_Wed) + " ";
                } else if (array_pattern[i].equals("Y") && i == 5) {
                    result_Weeks += mContext.getString(R.string.trp_Thu) + " ";
                } else if (array_pattern[i].equals("Y") && i == 6) {
                    result_Weeks += mContext.getString(R.string.trp_Fri) + " ";
                } else if (array_pattern[i].equals("Y") && i == 7) {
                    result_Weeks += mContext.getString(R.string.trp_Sat) + " ";
                }
            }
        }
        return result_Weeks;
    }



    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_alarm;
        ImageView imageView;
        TextView tv_week;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_alarm = itemView.findViewById(R.id.tv_alarm);
            imageView = itemView.findViewById(R.id.imageView);
            tv_week = itemView.findViewById(R.id.tv_week);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    IamVO iamvo = new IamVO();
                    iamvo.setIAM_ID(filteredmlist.get(position).IAM_ID);
                    iamvo.setIAM_01(filteredmlist.get(position).IAM_01);
                    iamvo.setIAM_02(filteredmlist.get(position).IAM_02);
                    iamvo.setIAM_03(filteredmlist.get(position).IAM_03);
                    iamvo.setIAM_04(filteredmlist.get(position).IAM_04);
                    iamvo.setIAM_05(filteredmlist.get(position).IAM_05);
                    iamvo.setIAM_96(filteredmlist.get(position).IAM_96);
                    iamvo.setIAM_97(filteredmlist.get(position).IAM_97);
                    Intent intent = new Intent(mContext, IamDetail.class);
                    intent.putExtra("intentVO", intentVO);
                    intent.putExtra("IamVO", iamvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<IamVO> list) {
        mList = list;
    }


}
