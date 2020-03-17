package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.COD_VO;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<COD_VO> mList;
    private ArrayList<COD_VO> filteredmlist;
    private LayoutInflater mInflater;
    private InterfaceUser mUser;

    Filter listFilter;

    Calendar TODAY = Calendar.getInstance();
    Calendar COD_05_C = Calendar.getInstance();
    Calendar COD_06_C = Calendar.getInstance();

    public CodAdapter(Context context, ArrayList<COD_VO> list){
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
            convertView = mInflater.inflate(R.layout.listitem_find_cod, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.layoutCod = (LinearLayout) convertView.findViewById(R.id.layoutCod);

            viewHolder.tvCodName = convertView.findViewById(R.id.tvCodName);
            viewHolder.tvEndDay = convertView.findViewById(R.id.tvEndDay);
            viewHolder.tvDayLabel = convertView.findViewById(R.id.tvDayLabel);

            viewHolder.pbUse = (ProgressBar) convertView.findViewById(R.id.pbUse);

            viewHolder.imgAlarmIcon = (ImageView) convertView.findViewById(R.id.imgAlarmIcon);

            viewHolder.btnUseEnd = (Button) convertView.findViewById(R.id.btnUseEnd);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        clearCalTime(TODAY);
        clearCalTime(COD_05_C);
        clearCalTime(COD_06_C);

        viewHolder.tvCodName.setText(filteredmlist.get(position).COD_02);

        if(filteredmlist.get(position).COD_07.equals("")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.layoutCod.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape));
            } else {
                viewHolder.layoutCod.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape));
            }
            viewHolder.tvDayLabel.setText("유효기간");
            viewHolder.imgAlarmIcon.setVisibility(View.VISIBLE);
            viewHolder.btnUseEnd.setVisibility(View.VISIBLE);
            viewHolder.pbUse.setVisibility(View.VISIBLE);

            //Text
            viewHolder.tvEndDay.setText(filteredmlist.get(position).COD_06.substring(0, 4) + "-" + filteredmlist.get(position).COD_06.substring(4, 6) + "-" + filteredmlist.get(position).COD_06.substring(6, 8));

            //ProgressBar
            COD_05_C.set(Integer.parseInt(filteredmlist.get(position).COD_05.substring(0,4)), Integer.parseInt(filteredmlist.get(position).COD_05.substring(4,6)) - 1, Integer.parseInt(filteredmlist.get(position).COD_05.substring(6)));
            COD_06_C.set(Integer.parseInt(filteredmlist.get(position).COD_06.substring(0,4)), Integer.parseInt(filteredmlist.get(position).COD_06.substring(4,6)) - 1, Integer.parseInt(filteredmlist.get(position).COD_06.substring(6)));
            int max = (int) ((COD_06_C.getTimeInMillis() - COD_05_C.getTimeInMillis()) / (24*60*60*1000));
            int value = (int) ((TODAY.getTimeInMillis() - COD_05_C.getTimeInMillis()) / (24*60*60*1000));
            viewHolder.pbUse.setMax(max);
            viewHolder.pbUse.setProgress(value);
            if(max - value > 0){
                viewHolder.pbUse.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_listitem));
            }
            else{
                viewHolder.pbUse.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_full_listitem));
            }
        }
        else{ //사용종료일때!!
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.layoutCod.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape_gray));
            } else {
                viewHolder.layoutCod.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape_gray));
            }
            viewHolder.tvDayLabel.setText("사용종료");
            viewHolder.imgAlarmIcon.setVisibility(View.GONE);
            viewHolder.btnUseEnd.setVisibility(View.GONE);
            viewHolder.pbUse.setVisibility(View.GONE);

            //Text
            viewHolder.tvEndDay.setText(filteredmlist.get(position).COD_07.substring(0, 4) + "-" + filteredmlist.get(position).COD_07.substring(4, 6) + "-" + filteredmlist.get(position).COD_07.substring(6, 8));
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
                COD_VO data = filteredmlist.get(position);
                requestCOD_CONTROL("ALARM_UPDATE", data, position);
            }
        });

        //Button
        viewHolder.btnUseEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage("해당 화장품을 사용종료 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                COD_VO data = filteredmlist.get(position);
                                requestCOD_CONTROL("USEEND", data, position);
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
        viewHolder.btnUseEnd.setFocusable(false);

        return convertView;
    }

    private void requestCOD_CONTROL(String GUBUN, COD_VO COD, int position) {

        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        String COD_ID = COD.COD_ID; //컨테이너
        String COD_01 = COD.COD_01; //코드번호
        String COD_02 = "";
        String COD_03 = "";
        double COD_04 = 0;
        String COD_05 = "";
        String COD_06 = "";
        String COD_07 = "";
        String COD_08 = "";
        String COD_95 = "";
        String COD_96 = "";
        String COD_98 = mUser.Value.OCM_01; //최종수정자
        String ARM_03 = "";

        Call<CODModel> call = Http.cod(HttpBaseService.TYPE.POST).COD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                COD_ID,
                COD_01,
                COD_02,
                COD_03,

                COD_04,
                COD_05,
                COD_06,
                COD_07,
                COD_08,

                COD_95,
                COD_96,
                COD_98,
                ARM_03
        );

        call.enqueue(new Callback<CODModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CODModel> call, Response<CODModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if (msg.what == 100){

                            Response<CODModel> response = (Response<CODModel>) msg.obj;

                            ArrayList<COD_VO> responseData = response.body().Data;

                            if(responseData.get(0).Validation){
                                filteredmlist.get(position).COD_07 = responseData.get(0).COD_07;
                                filteredmlist.get(position).ARM_03 = responseData.get(0).ARM_03;

                                if(responseData.get(0).ARM_03.equals("Y") && responseData.get(0).COD_07.equals("")){
                                    String NextDay = responseData.get(0).COD_96;
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
            public void onFailure(Call<CODModel> call, Throwable t){
                Log.d("Test", t.getMessage());

            }
        });

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
                ArrayList<COD_VO> itemList = new ArrayList<>();
                for(COD_VO item : mList){
                    if(item.COD_02.toLowerCase().contains(constraint.toString().toLowerCase())){
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

            filteredmlist = (ArrayList<COD_VO>)results.values;

            if(results.count>0){
                notifyDataSetChanged();
            }
            else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    public void updateData(ArrayList<COD_VO> list){ mList = list;}

    static class ViewHolder{
        LinearLayout layoutCod;

        TextView tvCodName;
        TextView tvEndDay;
        TextView tvDayLabel;

        ProgressBar pbUse;

        ImageView imgAlarmIcon;

        Button btnUseEnd;

    }
}
