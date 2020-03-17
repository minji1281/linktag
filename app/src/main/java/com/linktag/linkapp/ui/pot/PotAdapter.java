package com.linktag.linkapp.ui.pot;

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
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PotVO> mList;
    private ArrayList<PotVO> filteredmlist;
    private LayoutInflater mInflater;
    private InterfaceUser mUser;

    Filter listFilter;

    public PotAdapter(Context context, ArrayList<PotVO> list){
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
            convertView = mInflater.inflate(R.layout.griditem_find_pot, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.tvDDAY = convertView.findViewById(R.id.tvDDAY);

            viewHolder.btnWater = (Button) convertView.findViewById(R.id.btnWater);

            viewHolder.PotIcon = (ImageView) convertView.findViewById(R.id.PotIcon);
            viewHolder.AlarmIcon = (ImageView) convertView.findViewById(R.id.AlarmIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Text
        viewHolder.tvName.setText(filteredmlist.get(position).POT_02);
        String DDAY = "";
        if(Integer.parseInt(filteredmlist.get(position).DDAY) > 0) {
            DDAY = "D-" + filteredmlist.get(position).DDAY;
        }
        else if(Integer.parseInt(filteredmlist.get(position).DDAY) == 0){
            DDAY = "D-Day";
        }
        else{
            DDAY = "D+" + (Integer.parseInt(filteredmlist.get(position).DDAY) * -1);
        }
        viewHolder.tvDDAY.setText(DDAY);

        //Image
        if(filteredmlist.get(position).POT_05.equals("F")){
            viewHolder.PotIcon.setImageResource(R.drawable.ic_pot2_test);
        }
        else{
            if(Integer.parseInt(filteredmlist.get(position).DDAY) <= 0){
                viewHolder.PotIcon.setImageResource(R.drawable.ic_pot1_test);
            }
            else{
                viewHolder.PotIcon.setImageResource(R.drawable.ic_pot3_test);
            }
        }

        if(filteredmlist.get(position).ARM_03.equals("Y")){
            viewHolder.AlarmIcon.setImageResource(R.drawable.alarm_state_on);
        }
        else{ //N
            viewHolder.AlarmIcon.setImageResource(R.drawable.alarm_state_off);
        }
        viewHolder.AlarmIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PotVO data = filteredmlist.get(position);
                requestPOT_CONTROL("ALARM_UPDATE", data, position);
            }
        });

        //button
        viewHolder.btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage("해당 화분의 물주기 정보를 업데이트 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PotVO data = filteredmlist.get(position);
                                requestPOT_CONTROL("WATER", data, position);
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

        viewHolder.AlarmIcon.setFocusable(false);
        viewHolder.btnWater.setFocusable(false);

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
                ArrayList<PotVO> itemList = new ArrayList<>();
                for(PotVO item : mList){
                    if(item.POT_02.toLowerCase().contains(constraint.toString().toLowerCase())){
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

            filteredmlist = (ArrayList<PotVO>)results.values;

            if(results.count>0){
                notifyDataSetChanged();
            }
            else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void updateData(ArrayList<PotVO> list){ mList = list;}

    private void requestPOT_CONTROL(String GUB, PotVO pot, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        String GUBUN = GUB;
        String POT_ID = pot.POT_ID; //컨테이너
        String POT_01 = pot.POT_01; //코드번호
        String POT_02 = pot.POT_02; //명칭
        String POT_03 = "";

        int POT_04 = 0;
        String POT_05 = "";
        String POT_06 = "";
        String POT_81 = "";
        String POT_96 = "";
        String POT_98 = mUser.Value.OCM_01; //사용자 아이디

        String ARM_03 = pot.ARM_03; //알림여부

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                POT_02,
                POT_03,

                POT_04,
                POT_05,
                POT_06,
                POT_96,
                POT_98,

                ARM_03
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if (msg.what == 100){

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            ArrayList<PotVO> responseData = response.body().Data;

                            if(responseData.get(0).Validation){
                                filteredmlist.get(position).DDAY = responseData.get(0).DDAY;
                                filteredmlist.get(position).POT_03 = responseData.get(0).POT_03;
                                filteredmlist.get(position).POT_96 = responseData.get(0).POT_96;
                                filteredmlist.get(position).ARM_03 = responseData.get(0).ARM_03;

                                if(responseData.get(0).ARM_03.equals("Y")){
                                    String NextDay = responseData.get(0).POT_96;
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
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("Test", t.getMessage());

            }
        });

    }

    static class ViewHolder{
        TextView tvName;
        TextView tvDDAY;

        Button btnWater;

        ImageView PotIcon;
        ImageView AlarmIcon;

    }
}
