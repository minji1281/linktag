package com.linktag.linkapp.ui.trp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.TrdVO;
import com.linktag.linkapp.value_object.TrpVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrpRecycleAdapter extends RecyclerView.Adapter<TrpRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<TrpVO> mList;
    private ArrayList<TrdVO> mList_trd;
    private ArrayList<TrpVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private String[] array_pattern;
    private String[] str_weeks_text;

    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    Filter listFilter;

    TrpRecycleAdapter(Context context, ArrayList<TrpVO> list) {
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
                ArrayList<TrpVO> itemList = new ArrayList<>();
                for (TrpVO item : mList) {
                    if (item.TRP_02.toLowerCase().contains(constraint.toString().toLowerCase())) {
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

            filteredmlist = (ArrayList<TrpVO>) results.values;

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public TrpRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_trp_list
                , parent, false);
        TrpRecycleAdapter.ViewHolder viewHolder = new TrpRecycleAdapter.ViewHolder(view);


        str_weeks_text = mContext.getResources().getStringArray(R.array.trp3);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(filteredmlist.get(position).TRP_02);
        viewHolder.tv_week.setText(patternToWeeks(filteredmlist.get(position).TRP_04));


        requestTRD_SELECT(viewHolder, filteredmlist, position);

        viewHolder.filteredmlist_trd = new ArrayList<>();
        viewHolder.linearLayoutManager_TRD = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.mAdapter_trd = new TrdRecycleAdapter_horizontal(mContext, viewHolder.filteredmlist_trd);

        viewHolder.recyclerView_TRD.setLayoutManager(viewHolder.linearLayoutManager_TRD);
        viewHolder.recyclerView_TRD.setAdapter(viewHolder.mAdapter_trd);


        if (filteredmlist.get(position).ARM_03.equals("Y")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
        } else if (filteredmlist.get(position).ARM_03.equals("N")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
        }

        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.filteredmlist_trd.size() == 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.common_no_alarm_toast), Toast.LENGTH_LONG).show();
                    return;
                }

                if (filteredmlist.get(position).ARM_03.equals("Y")) {
                    filteredmlist.get(position).setARM_03("N");
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
                    Toast.makeText(mContext, "[" + filteredmlist.get(position).TRP_02 + "]- " + mContext.getString(R.string.common_alarm_off), Toast.LENGTH_SHORT).show();
                } else {
                    filteredmlist.get(position).setARM_03("Y");
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                    checkDayOfWeek(filteredmlist.get(position).TRP_04, viewHolder.filteredmlist_trd);
//                    Toast.makeText(mContext, "[" + filteredmlist.get(position).TRP_02 + "]- " + mContext.getString(R.string.common_alarm_on), Toast.LENGTH_SHORT).show();
                }

//                ArmVO armVO = new ArmVO();
//
//                armVO.setARM_ID(filteredmlist.get(position).TRP_ID);
//                armVO.setARM_01(filteredmlist.get(position).TRP_01);
//                armVO.setARM_02(mUser.Value.OCM_01);
//                armVO.setARM_03(filteredmlist.get(position).ARM_03);
//                armVO.setARM_95("");
//                armVO.setARM_90(filteredmlist.get(position).JDM_02);
//                armVO.setARM_91(filteredmlist.get(position).JDM_03);
//                armVO.setARM_92(filteredmlist.get(position).JDM_96);
//                armVO.setARM_93("");
//                armVO.setARM_94("N");
//                armVO.setARM_98(mUser.Value.OCM_01);
//
//                requestARM_CONTROL(armVO, position);

                TrpVO trpvo = new TrpVO();
                trpvo.setTRP_ID(filteredmlist.get(position).TRP_ID);
                trpvo.setTRP_01(filteredmlist.get(position).TRP_01);
                trpvo.setTRP_02(filteredmlist.get(position).TRP_02);
                trpvo.setTRP_03(filteredmlist.get(position).TRP_03);
                trpvo.setTRP_04(filteredmlist.get(position).TRP_04);
                trpvo.setTRP_05(filteredmlist.get(position).TRP_05);
                trpvo.setTRP_06(filteredmlist.get(position).TRP_06);
                trpvo.setTRP_07(filteredmlist.get(position).TRP_07);
                trpvo.setTRP_97(filteredmlist.get(position).TRP_97);
                trpvo.setARM_03(filteredmlist.get(position).ARM_03);

                requestTRP_CONTROL(trpvo, position);

            }
        });

    }

    public void requestTRD_SELECT(ViewHolder viewHolder, ArrayList<TrpVO> filteredmlist, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<TRDModel> call = Http.trd(HttpBaseService.TYPE.POST).TRD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                filteredmlist.get(position).TRP_ID,
                filteredmlist.get(position).TRP_01,
                ""
        );


        call.enqueue(new Callback<TRDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TRDModel> call, Response<TRDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<TRDModel> response = (Response<TRDModel>) msg.obj;

                            viewHolder.filteredmlist_trd = response.body().Data;
                            if (viewHolder.filteredmlist_trd == null)
                                viewHolder.filteredmlist_trd = new ArrayList<>();

                            if (viewHolder.filteredmlist_trd.size() == 0) {
                                viewHolder.tv_alarmNone.setVisibility(View.VISIBLE);
                                viewHolder.recyclerView_TRD.setVisibility(View.GONE);
                            } else {
                                viewHolder.tv_alarmNone.setVisibility(View.GONE);
                                viewHolder.recyclerView_TRD.setVisibility(View.VISIBLE);
                            }
                            viewHolder.mAdapter_trd.updateData(viewHolder.filteredmlist_trd);
                            viewHolder.mAdapter_trd.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<TRDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

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
        ImageView imageview;
        TextView tv_name;
        TextView tv_week;

        private TrdRecycleAdapter_horizontal mAdapter_trd;
        private LinearLayoutManager linearLayoutManager_TRD;
        private ArrayList<TrdVO> filteredmlist_trd;

        RecyclerView recyclerView_TRD;

        TextView tv_alarmNone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_week = itemView.findViewById(R.id.tv_week);

            recyclerView_TRD = itemView.findViewById(R.id.recyclerView_TRD);

            tv_alarmNone = itemView.findViewById(R.id.tv_alarmNone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    TrpVO trpvo = new TrpVO();
                    trpvo.setTRP_ID(filteredmlist.get(position).TRP_ID);
                    trpvo.setTRP_01(filteredmlist.get(position).TRP_01);
                    trpvo.setTRP_02(filteredmlist.get(position).TRP_02);
                    trpvo.setTRP_03(filteredmlist.get(position).TRP_03);
                    trpvo.setTRP_04(filteredmlist.get(position).TRP_04);
                    trpvo.setTRP_05(filteredmlist.get(position).TRP_05);
                    trpvo.setTRP_06(filteredmlist.get(position).TRP_06);
                    trpvo.setTRP_07(filteredmlist.get(position).TRP_07);
                    trpvo.setTRP_97(filteredmlist.get(position).TRP_97);
                    trpvo.setARM_03(filteredmlist.get(position).ARM_03);

                    Intent intent = new Intent(mContext, TrpDetail.class);
                    intent.putExtra("TrpVO", trpvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<TrpVO> list) {
        mList = list;
    }


    public void requestTRP_CONTROL(TrpVO trpVO, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<TRPModel> call = Http.trp(HttpBaseService.TYPE.POST).TRP_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_2",
                trpVO.TRP_ID,
                trpVO.TRP_01,
                trpVO.TRP_02,
                trpVO.TRP_03,
                trpVO.TRP_04,
                trpVO.TRP_05,
                trpVO.TRP_06,
                trpVO.TRP_07,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                trpVO.ARM_03
        );


        call.enqueue(new Callback<TRPModel>() {
            @Override
            public void onResponse(Call<TRPModel> call, Response<TRPModel> response) {

            }

            @Override
            public void onFailure(Call<TRPModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


    public void checkDayOfWeek(String TRP_04, ArrayList<TrdVO> mList_trd) {


        array_pattern = TRP_04.split("");


        Calendar calendar = Calendar.getInstance();

        int nowWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int nowTime = Integer.parseInt(formatTime.format(calendar.getTime()));
        String time = "";
        int count = 0;
        for (TrdVO trdVO : mList_trd) {
            int setTime = Integer.parseInt(trdVO.TRD_96.substring(8, 12));
            time = trdVO.TRD_96.substring(8, 12);
            if (nowTime < setTime) {
                count++;
                break;
            }
        }
        if (count == 0) {  // 예정알림시간이 이미 다 지난경우 다음요일로 넘어감
            for (int i = 1; i < array_pattern.length; i++) {
                if (array_pattern[nowWeek + i].equals("Y")) {
                    nowWeek = nowWeek + i;
                    break;
                }
            }
        }

        String ToastMessage = time.substring(0, 2) + ":" + time.substring(2, 4);
        if (nowWeek == 1 && array_pattern[1].equals("Y")) { //일요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[0] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        } else if (nowWeek == 2 && array_pattern[2].equals("Y")) { //월요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[1] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        } else if (nowWeek == 3 && array_pattern[3].equals("Y")) { //화요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[2] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        } else if (nowWeek == 4 && array_pattern[4].equals("Y")) { //수요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[3] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        } else if (nowWeek == 5 && array_pattern[5].equals("Y")) { //목요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[4] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        } else if (nowWeek == 6 && array_pattern[6].equals("Y")) { //금요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[5] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        } else if (nowWeek == 7 && array_pattern[7].equals("Y")) { //토요일
            Toast.makeText(mContext, mContext.getString(R.string.trp_text6) + " " + str_weeks_text[6] + " " + ToastMessage + " " + mContext.getString(R.string.trp_text7), Toast.LENGTH_LONG).show();
        }
    }


}
