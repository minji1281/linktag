package com.linktag.linkapp.ui.dam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
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
import com.linktag.base.util.ClsBitmap;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.DamVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DamRecycleAdapter extends RecyclerView.Adapter<DamRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<DamVO> mList;
    private ArrayList<DamVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    private Calendar nextDay = Calendar.getInstance();

    Filter listFilter;

    DamRecycleAdapter(Context context, ArrayList<DamVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        filteredmlist = list;
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
                ArrayList<DamVO> itemList = new ArrayList<>();
                for (DamVO item : mList) {
                    if (item.DAM_02.toLowerCase().contains(constraint.toString().toLowerCase())) {
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

            filteredmlist = (ArrayList<DamVO>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public DamRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_dam_list, parent, false);
        DamRecycleAdapter.ViewHolder viewHolder = new DamRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (filteredmlist.get(position).DAM_03.length()<20){
            int resource = mContext.getResources().getIdentifier(filteredmlist.get(position).DAM_03, "drawable", mContext.getPackageName());
            viewHolder.img_icon.setImageResource(resource);
            viewHolder.img_icon.setBackground(null);

        }else{
            viewHolder.img_icon.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21)
                viewHolder.img_icon.setClipToOutline(true);
            ClsBitmap.setSharedDamIcon(mContext, viewHolder.img_icon, mList.get(position).DAM_ID, mList.get(position).DAM_01, mList.get(position).DAM_03, "", R.drawable.btn_add);
        }


        viewHolder.tv_name.setText(filteredmlist.get(position).DAM_02);
        viewHolder.tv_date.setText(stringTodateFormat(filteredmlist.get(position).DAM_96));
        if (filteredmlist.get(position).DAM_04.equals("3")) {
            viewHolder.tv_label.setText(mContext.getString(R.string.dam_detail_text8));
        } else {
            viewHolder.tv_label.setText("");
        }

        Calendar sCalendar = Calendar.getInstance();
        sCalendar.set(Calendar.YEAR, Integer.parseInt(filteredmlist.get(position).DAM_96.substring(0, 4)));
        sCalendar.set(Calendar.MONTH, Integer.parseInt(filteredmlist.get(position).DAM_96.substring(4, 6)) - 1);
        sCalendar.set(Calendar.DATE, Integer.parseInt(filteredmlist.get(position).DAM_96.substring(6, 8)));

        sCalendar.clear(Calendar.HOUR);
        sCalendar.clear(Calendar.MINUTE);
        sCalendar.clear(Calendar.SECOND);
        sCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화


        long dDayDiff;
        int dcount;
        switch (filteredmlist.get(position).DAM_04) {
            case "1":
                dDayDiff = calendar.getTimeInMillis() - sCalendar.getTimeInMillis();
                dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                viewHolder.tv_count.setText(dcount + 1 + " " +mContext.getString(R.string.dam_detail_text15));
                viewHolder.imageview.setVisibility(View.GONE);
                break;
            case "2":
                dDayDiff = sCalendar.getTimeInMillis() - calendar.getTimeInMillis();
                dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                if (dcount == 0) {
                    viewHolder.tv_count.setText("D-DAY");
                } else {
                    viewHolder.tv_count.setText("D-" + dcount);
                }
                break;
            case "3":
                dDayDiff = sCalendar.getTimeInMillis() - calendar.getTimeInMillis();
                dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));

                while (dcount < 0){
                    sCalendar.add(Calendar.YEAR, 1);
                    dDayDiff = sCalendar.getTimeInMillis() - calendar.getTimeInMillis();
                    dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                }

                if (dcount == 0) {
                    viewHolder.tv_count.setText("D-DAY");
                } else {
                    viewHolder.tv_count.setText("D-" + dcount);
                }

                break;
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
                    Toast.makeText(mContext, "[" + filteredmlist.get(position).DAM_02 + "]-" + mContext.getResources().getString(R.string.common_alarm_off), Toast.LENGTH_SHORT).show();
                } else if (filteredmlist.get(position).ARM_03.equals("N")) {
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);

                    if (!filteredmlist.get(position).DAM_04.equals("1")) {
                        Toast.makeText(mContext, "[" + filteredmlist.get(position).DAM_02 + "]" + "\n" +
                                mContext.getString(R.string.dam_text2) + " " + filteredmlist.get(position).DAM_96.substring(0, 4) + "-" + filteredmlist.get(position).DAM_96.substring(4, 6) + "-" + filteredmlist.get(position).DAM_96.substring(6, 8) + " " +
                                filteredmlist.get(position).DAM_96.substring(8, 10) + ":" + filteredmlist.get(position).DAM_96.substring(10, 12) + " " + mContext.getString(R.string.dam_text3), Toast.LENGTH_LONG).show();
                    }
                }

                ArmVO armVO = new ArmVO();

                armVO.setARM_ID(filteredmlist.get(position).DAM_ID);
                armVO.setARM_01(filteredmlist.get(position).DAM_01);
                armVO.setARM_02(mUser.Value.OCM_01);
                armVO.setARM_03(filteredmlist.get(position).ARM_03);
                armVO.setARM_95("");
                armVO.setARM_90(filteredmlist.get(position).DAM_02);
                armVO.setARM_91(mContext.getString(R.string.dam_text12));
                armVO.setARM_92(filteredmlist.get(position).DAM_96);
                armVO.setARM_93("");
                armVO.setARM_94("N");
                armVO.setARM_98(mUser.Value.OCM_01);

                requestARM_CONTROL(armVO, position);

            }
        });


    }


    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_date;
        TextView tv_label;
        TextView tv_count;
        ImageView img_icon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_label = itemView.findViewById(R.id.tv_label);
            tv_count = itemView.findViewById(R.id.tv_count);
            img_icon = itemView.findViewById(R.id.img_icon);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    DamVO damvo = new DamVO();
                    damvo.setDAM_ID(filteredmlist.get(position).DAM_ID);
                    damvo.setDAM_01(filteredmlist.get(position).DAM_01);
                    damvo.setDAM_02(filteredmlist.get(position).DAM_02);
                    damvo.setDAM_03(filteredmlist.get(position).DAM_03);
                    damvo.setDAM_04(filteredmlist.get(position).DAM_04);
                    damvo.setDAM_96(filteredmlist.get(position).DAM_96);
                    damvo.setDAM_97(filteredmlist.get(position).DAM_97);
                    damvo.setARM_03(filteredmlist.get(position).ARM_03);

                    Intent intent = new Intent(mContext, DamDetail.class);
                    intent.putExtra("DamVO", damvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<DamVO> list) {
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
                "DAM1",
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

    public String stringTodateFormat(String str) {
        String retStr = "";
        //yyyy.MM.dd
        retStr = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8);
        return retStr;
    }


}
