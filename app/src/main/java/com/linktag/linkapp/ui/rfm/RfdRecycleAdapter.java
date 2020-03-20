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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm_service.AlarmHATT;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.ui.jdm.JdmRecycleAdapter;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.RfdVO;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RfdRecycleAdapter extends RecyclerView.Adapter<RfdRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<RfdVO> mList;
    private ArrayList<RfdVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private Calendar calendar = Calendar.getInstance();

    private String[] str_label;
    Filter listFilter;

    RfdRecycleAdapter(Context context, ArrayList<RfdVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        filteredmlist = list;
        str_label = mContext.getResources().getStringArray(R.array.rfd_label);
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
                ArrayList<RfdVO> itemList = new ArrayList<>();
                for (RfdVO item : mList) {
                    if (item.RFD_03.toLowerCase().contains(constraint.toString().toLowerCase())) {
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

            filteredmlist = (ArrayList<RfdVO>) results.values;

            notifyDataSetChanged();
        }
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

        viewHolder.tv_name.setText(filteredmlist.get(position).RFD_03);
        if (!filteredmlist.get(position).RFD_07.equals("")) {
            viewHolder.tv_label1.setText(str_label[3]);
            viewHolder.tv_D_day.setText(filteredmlist.get(position).RFD_07.substring(0, 4) + "." + filteredmlist.get(position).RFD_07.substring(4, 6) + "." + filteredmlist.get(position).RFD_07.substring(6, 8));
            viewHolder.btn_label.setVisibility(View.GONE);
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.imageview.setVisibility(View.GONE);
            viewHolder.root_linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape_gray));

        } else {
            viewHolder.tv_label1.setText(str_label[4]);
            viewHolder.btn_label.setVisibility(View.VISIBLE);
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.imageview.setVisibility(View.VISIBLE);

            viewHolder.root_linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape));
            viewHolder.tv_D_day.setText(filteredmlist.get(position).RFD_96.substring(0, 4) + "." + filteredmlist.get(position).RFD_96.substring(4, 6) + "." + filteredmlist.get(position).RFD_96.substring(6, 8));

            calendar.clear(Calendar.HOUR);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

            Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(Calendar.YEAR, Integer.parseInt(filteredmlist.get(position).RFD_96.substring(0, 4)));
            dCalendar.set(Calendar.MONTH, Integer.parseInt(filteredmlist.get(position).RFD_96.substring(4, 6)) - 1);
            dCalendar.set(Calendar.DATE, Integer.parseInt(filteredmlist.get(position).RFD_96.substring(6, 8)));

            dCalendar.clear(Calendar.HOUR);
            dCalendar.clear(Calendar.MINUTE);
            dCalendar.clear(Calendar.SECOND);
            dCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

            Calendar sCalendar = Calendar.getInstance();
            sCalendar.set(Calendar.YEAR, Integer.parseInt(filteredmlist.get(position).RFD_05.substring(0, 4)));
            sCalendar.set(Calendar.MONTH, Integer.parseInt(filteredmlist.get(position).RFD_05.substring(4, 6)) - 1);
            sCalendar.set(Calendar.DATE, Integer.parseInt(filteredmlist.get(position).RFD_05.substring(6, 8)));

            sCalendar.clear(Calendar.HOUR);
            sCalendar.clear(Calendar.MINUTE);
            sCalendar.clear(Calendar.SECOND);
            sCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화


            long dDayDiff = calendar.getTimeInMillis() - sCalendar.getTimeInMillis();
            int dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));


            long dDayDiff2 = dCalendar.getTimeInMillis() - sCalendar.getTimeInMillis();
            int totalProgress = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff2, TimeUnit.MILLISECONDS) / 24f));


            long dDayDiff3 = dCalendar.getTimeInMillis() - calendar.getTimeInMillis();
            int count = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff3, TimeUnit.MILLISECONDS) / 24f));


            if (count < 0) {
                viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_red_8dp);
                viewHolder.btn_label.setText(str_label[0]);
            } else if (count == 0) {
                viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_shallowgray_8dp);
                viewHolder.btn_label.setText(str_label[1]);
            } else {
                viewHolder.btn_label.setBackgroundResource(R.drawable.btn_round_shallowgray_8dp);
                viewHolder.btn_label.setText(count + " " +str_label[2]);
            }

            if (count == 0) {
                viewHolder.progressBar.getProgressDrawable().setColorFilter(null);
                viewHolder.progressBar.setMax(1);
                viewHolder.progressBar.setProgress(1);

            } else if (count < 0) {
                viewHolder.progressBar.setMax(1);
                viewHolder.progressBar.setProgress(1);
                viewHolder.progressBar.getProgressDrawable().setColorFilter(0xFFE97D6C, PorterDuff.Mode.SRC_IN);
            } else {
                viewHolder.progressBar.setMax(totalProgress);
                viewHolder.progressBar.setProgress(dcount);
                viewHolder.progressBar.getProgressDrawable().setColorFilter(null);
            }

            if (filteredmlist.get(position).ARM_03.equals("Y")) {
                viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
            } else if (filteredmlist.get(position).ARM_03.equals("N")) {
                viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
            }

            viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (filteredmlist.get(position).ARM_03.equals("Y")) {
                        viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
                        Toast.makeText(mContext, "[" + filteredmlist.get(position).RFD_03 + "]-" +mContext.getResources().getString(R.string.common_alarm_off), Toast.LENGTH_SHORT).show();
                    } else if (filteredmlist.get(position).ARM_03.equals("N")) {
                        viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                        Toast.makeText(mContext, "[" + filteredmlist.get(position).RFD_03 + "]-" +mContext.getResources().getString(R.string.common_alarm_on), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.common_no_alarm_toast), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArmVO armVO = new ArmVO();

                    armVO.setARM_ID(filteredmlist.get(position).RFD_ID);
                    armVO.setARM_01(filteredmlist.get(position).RFD_02);
                    armVO.setARM_02(mUser.Value.OCM_01);
                    armVO.setARM_03(filteredmlist.get(position).ARM_03);
                    armVO.setARM_95(filteredmlist.get(position).RFD_01);
                    armVO.setARM_90(filteredmlist.get(position).RFD_03);
                    armVO.setARM_91(filteredmlist.get(position).RFD_04);
                    armVO.setARM_92(filteredmlist.get(position).RFD_96);
                    armVO.setARM_93("");
                    armVO.setARM_94("N");
                    armVO.setARM_98(mUser.Value.OCM_01);

                    requestARM_CONTROL(armVO, position);
                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_D_day;
        TextView tv_label1;
        ProgressBar progressBar;
        Button btn_label;
        LinearLayout root_linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_D_day = itemView.findViewById(R.id.tv_D_day);
            btn_label = itemView.findViewById(R.id.btn_label);
            tv_label1 = itemView.findViewById(R.id.tv_label1);
            root_linearLayout = itemView.findViewById(R.id.root_linearLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    RfdVO rfdvo = new RfdVO();
                    rfdvo.setRFD_ID(filteredmlist.get(position).RFD_ID);
                    rfdvo.setRFD_01(filteredmlist.get(position).RFD_01);
                    rfdvo.setRFD_02(filteredmlist.get(position).RFD_02);
                    rfdvo.setRFD_03(filteredmlist.get(position).RFD_03);
                    rfdvo.setRFD_04(filteredmlist.get(position).RFD_04);
                    rfdvo.setRFD_05(filteredmlist.get(position).RFD_05);
                    rfdvo.setRFD_06(filteredmlist.get(position).RFD_06);
                    rfdvo.setRFD_07(filteredmlist.get(position).RFD_07);
                    rfdvo.setRFD_96(filteredmlist.get(position).RFD_96);
                    rfdvo.setARM_03(filteredmlist.get(position).ARM_03);
                    rfdvo.setARM_04(filteredmlist.get(position).ARM_04);

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
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<ARMModel> call = Http.arm(HttpBaseService.TYPE.POST).ARM_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_2",
                armVO.ARM_ID,
                armVO.ARM_01,
                "RFM1",
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

                if (filteredmlist.get(position).ARM_03.equals("Y")) {
                    filteredmlist.get(position).setARM_03("N");

                } else {
                    filteredmlist.get(position).setARM_03("Y");

                }
            }

            public void onFailure(Call<ARMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });
    }

}
