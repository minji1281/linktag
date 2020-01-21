package com.linktag.linkapp.ui.apply_class;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.ClsDateTime;

import java.util.ArrayList;

public class ApplyStateAdapter extends BaseAdapter implements View.OnClickListener {
    private InterfaceUser mUser;
    private Context mContext;
    private ArrayList<LED_VO> mList;
    private LayoutInflater mInflater;
    private String LED_05;

    private ApplyBtnClickListener applyBtnClickListener;

    public interface ApplyBtnClickListener{
        void onListBtnClick(String btnName, int position);
    }

    public ApplyStateAdapter(Context context, ArrayList<LED_VO> list, String LED_05, ApplyBtnClickListener applyBtnClickListener) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.LED_05 = LED_05;

        this.applyBtnClickListener = applyBtnClickListener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_apply_record, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tvLED05NM = convertView.findViewById(R.id.tvLED05NM);
            viewHolder.tvLED19NM = convertView.findViewById(R.id.tvLED19NM);
            viewHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
            viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
            viewHolder.tvTitleTime1 = convertView.findViewById(R.id.tvTitleTime1);
            viewHolder.tvValueTime1 = convertView.findViewById(R.id.tvValueTime1);
            viewHolder.tvTitleTime2 = convertView.findViewById(R.id.tvTitleTime2);
            viewHolder.tvValueTime2 = convertView.findViewById(R.id.tvValueTime2);

            viewHolder.tvTitleReason = convertView.findViewById(R.id.tvTitleReason);
            viewHolder.tvValueReason = convertView.findViewById(R.id.tvValueReason);
            viewHolder.tvMemo = convertView.findViewById(R.id.tvMemo);
            viewHolder.btnApprove = convertView.findViewById(R.id.btnApprove);
            viewHolder.btnReturn = convertView.findViewById(R.id.btnReturn);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(mList.get(position).LED_05.equals("1")){
            // 연차
            viewHolder.tvLED05NM.setText("연차");
            viewHolder.tvTitleTime1.setText("신청일수");
            viewHolder.tvTitleReason.setText("연차사유");
        } else if (mList.get(position).LED_05.equals("2")) {
            // 연장
            viewHolder.tvLED05NM.setText("연장");
            viewHolder.tvTitleTime1.setText("연장시간");
            viewHolder.tvTitleReason.setText("연장사유");
            viewHolder.tvTitleTime2.setVisibility(View.GONE);
            viewHolder.tvValueTime2.setVisibility(View.GONE);
        }

        if(mList.get(position).STAT.equals("0")){
            viewHolder.btnApprove.setVisibility(View.VISIBLE);
            viewHolder.btnReturn.setVisibility(View.VISIBLE);
            viewHolder.btnApprove.setTag(position);
            viewHolder.btnApprove.setOnClickListener(this);
            viewHolder.btnReturn.setTag(position);
            viewHolder.btnReturn.setOnClickListener(this);
            viewHolder.tvLED19NM.setText("");
        } else {
            viewHolder.btnApprove.setVisibility(View.GONE);
            viewHolder.btnReturn.setVisibility(View.GONE);

            if(mList.get(position).LED_19.equals("Y")){
                viewHolder.tvLED19NM.setText("승인");
                viewHolder.tvLED19NM.setTextColor(Color.BLUE);
            } else if (mList.get(position).LED_19.equals("R")){
                viewHolder.tvLED19NM.setText("반려");
                viewHolder.tvLED19NM.setTextColor(Color.RED);
            } else {
                viewHolder.tvLED19NM.setText("승인대기중");
                viewHolder.tvLED19NM.setTextColor(Color.parseColor("#9dc8c8"));
            }
        }

        LED_05 = mList.get(position).LED_05;

        viewHolder.tvUserName.setText(mList.get(position).LED_04_NM);
        viewHolder.tvDate.setText(ClsDateTime.ConvertDateFormat("yyyyMMdd", "yyyy.MM.dd", mList.get(position).LED_24) + " " + mList.get(position).LED_24_NM);
        viewHolder.tvValueTime1.setText(String.valueOf(mList.get(position).LED_12));
        viewHolder.tvValueTime2.setText(String.valueOf(mList.get(position).LED_11));
        viewHolder.tvValueReason.setText(fnLED06(mList.get(position).LED_06));
        viewHolder.tvMemo.setText(mList.get(position).LED_97);

        return convertView;
    }

    public void updateData(ArrayList<LED_VO> list) {
        mList = list;
    }

    public String fnLED06(String LED_06){
        String ret = "";

        if(LED_05.equals("1")){
            switch(LED_06){
                case "0": ret = "기타"; break;
                case "1": ret = "개인사유"; break;
                case "2": ret = "병가"; break;
            }
        } else {
            switch(LED_06){
                case "0": ret = "기타"; break;
            }
        }

        return ret;
    }

    @Override
    public void onClick(View v) {
        if(this.applyBtnClickListener != null) {
            String btnName = "";

            if (v.getId() == R.id.btnApprove)
                btnName = "APPROVE";
            else if (v.getId() == R.id.btnReturn)
                btnName = "RETURN";

            this.applyBtnClickListener.onListBtnClick(btnName, (int) v.getTag());
        }
    }

    static class ViewHolder {
        TextView tvLED05NM, tvLED19NM;
        TextView tvUserName;
        TextView tvDate;
        TextView tvTitleTime1, tvValueTime1;
        TextView tvTitleTime2, tvValueTime2;
        TextView tvTitleReason, tvValueReason;
        TextView tvMemo;
        Button btnApprove;
        Button btnReturn;
    }
}
