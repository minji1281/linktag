package com.linktag.linkapp.ui.frm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.FRMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.FRM_VO;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<FRM_VO> mList;
    private ArrayList<FRM_VO> filteredmlist;
    private LayoutInflater mInflater;
    private InterfaceUser mUser;

    Filter listFilter;

    Calendar TODAY = Calendar.getInstance();
    Calendar FRM_03_C = Calendar.getInstance();
    Calendar FRM_96_C = Calendar.getInstance();

    public FrmAdapter(Context context, ArrayList<FRM_VO> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUser = InterfaceUser.getInstance();
        filteredmlist = list;
    }

    @Override
    public int getCount() {
        return filteredmlist.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredmlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_find_frm, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvFrmName = convertView.findViewById(R.id.tvFrmName);
            viewHolder.tvNextDay = convertView.findViewById(R.id.tvNextDay);

            viewHolder.pbFilter = (ProgressBar) convertView.findViewById(R.id.pbFilter);

            viewHolder.imgAlarmIcon = (ImageView) convertView.findViewById(R.id.imgAlarmIcon);

            viewHolder.btnFilterChange = (Button) convertView.findViewById(R.id.btnFilterChange);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Text
        viewHolder.tvFrmName.setText(filteredmlist.get(position).FRM_02);
        viewHolder.tvNextDay.setText(filteredmlist.get(position).FRM_96.substring(0, 4) + "-" + filteredmlist.get(position).FRM_96.substring(4, 6) + "-" + filteredmlist.get(position).FRM_96.substring(6, 8));

        //ProgressBar
        FRM_03_C.set(Integer.parseInt(filteredmlist.get(position).FRM_03.substring(0,4)), Integer.parseInt(filteredmlist.get(position).FRM_03.substring(4,6)) - 1, Integer.parseInt(filteredmlist.get(position).FRM_03.substring(6)));
        FRM_96_C.set(Integer.parseInt(filteredmlist.get(position).FRM_96.substring(0,4)), Integer.parseInt(filteredmlist.get(position).FRM_96.substring(4,6)) - 1, Integer.parseInt(filteredmlist.get(position).FRM_96.substring(6,8)));
        int max = (int) ((FRM_96_C.getTimeInMillis() - FRM_03_C.getTimeInMillis()) / (24*60*60*1000));
        int value = (int) ((TODAY.getTimeInMillis() - FRM_03_C.getTimeInMillis()) / (24*60*60*1000));
        viewHolder.pbFilter.setMax(max);
        viewHolder.pbFilter.setProgress(value);
        if(max - value > 0){
            viewHolder.pbFilter.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_listitem));
        }
        else{
            viewHolder.pbFilter.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_full_listitem));
        }

        //Image
        if(filteredmlist.get(position).ARM_03.equals("Y")){
            viewHolder.imgAlarmIcon.setImageResource(R.drawable.alarm_state_on);
        }
        else{ //N
            viewHolder.imgAlarmIcon.setImageResource(R.drawable.alarm_state_off);
        }
        viewHolder.imgAlarmIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FRM_VO data = filteredmlist.get(position);
                requestFRM_CONTROL("ALARM_UPDATE", data, position);
            }
        });

        //Button
        viewHolder.btnFilterChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage("해당 필터의 교체일자를 업데이트 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FRM_VO data = filteredmlist.get(position);
                                requestFRM_CONTROL("FILTER", data, position);
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .show();
            }
        });

        viewHolder.imgAlarmIcon.setFocusable(false);
        viewHolder.btnFilterChange.setFocusable(false);

        return convertView;
    }

    public Filter getFilter() {
        if (listFilter == null)
            listFilter = new ListFilter();

        return listFilter;
    }

    private class ListFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String charString = constraint.toString();
            if(charString.isEmpty()){
                results.values = mList;
                results.count = mList.size();
            }else{
                ArrayList<FRM_VO> itemList = new ArrayList<>();
                for(FRM_VO item : mList){
                    if(item.FRM_02.toLowerCase().contains(constraint.toString().toLowerCase())){
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

            filteredmlist = (ArrayList<FRM_VO>)results.values;

            if(results.count>0){
                notifyDataSetChanged();
            }
            else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void updateData(ArrayList<FRM_VO> list){ mList = list;}

    private void requestFRM_CONTROL(String GUBUN, FRM_VO FRM, int position) {

        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        String FRM_ID = FRM.FRM_ID; //컨테이너
        String FRM_01 = FRM.FRM_01; //코드번호
        String FRM_02 = FRM.FRM_02; //명칭
        String FRM_03 = "";

        int FRM_04 = FRM.FRM_04; //주기
        String FRM_05 = FRM.FRM_05; //주기구분
        String FRM_06 = FRM.FRM_06; //메모
        String FRM_96 = FRM.FRM_96; //알림시각
        String FRM_98 = mUser.Value.OCM_01; //사용자코드

        String ARM_03 = FRM.ARM_03;

        Call<FRMModel> call = Http.frm(HttpBaseService.TYPE.POST).FRM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                FRM_ID,
                FRM_01,
                FRM_02,
                FRM_03,

                FRM_04,
                FRM_05,
                FRM_06,
                FRM_96,
                FRM_98,

                ARM_03
        );

        call.enqueue(new Callback<FRMModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<FRMModel> call, Response<FRMModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if (msg.what == 100){

                            Response<FRMModel> response = (Response<FRMModel>) msg.obj;

                            ArrayList<FRM_VO> responseData = response.body().Data;

                            if(responseData.get(0).Validation){
                                filteredmlist.get(position).FRM_03 = responseData.get(0).FRM_03;
                                filteredmlist.get(position).FRM_96 = responseData.get(0).FRM_96;
                                filteredmlist.get(position).ARM_03 = responseData.get(0).ARM_03;

                                if(responseData.get(0).ARM_03.equals("Y")){
                                    String NextDay = responseData.get(0).FRM_96;
                                    Toast.makeText(mContext,"다음알람 "+ NextDay.substring(0,4)+"년 " + NextDay.substring(4,6)+"월 "+ NextDay.substring(6,8)+"일 " +
                                            NextDay.substring(8,10)+"시 " + NextDay.substring(10,12)+"분 예정입니다.", Toast.LENGTH_LONG ).show();
                                }
                            }

                            updateData(filteredmlist);
                            notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<FRMModel> call, Throwable t){
                Log.d("FRM_CONTROL", t.getMessage());
            }
        });

    }

    static class ViewHolder{
        TextView tvFrmName;
        TextView tvNextDay;

        ProgressBar pbFilter;

        ImageView imgAlarmIcon;

        Button btnFilterChange;

    }
}
