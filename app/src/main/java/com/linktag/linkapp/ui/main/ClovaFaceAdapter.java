package com.linktag.linkapp.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CfbVO;

import java.util.ArrayList;

public class ClovaFaceAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CfbVO> mList;
    private LayoutInflater mInflater;

    public ClovaFaceAdapter(Context context, ArrayList<CfbVO> list){
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_clovaface, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvText = convertView.findViewById(R.id.tvText);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String date1 = mList.get(position).CFB_05.substring(4, 6) + "/" + mList.get(position).CFB_05.substring(6, 8);
        String date2 = mList.get(position).CFB_06.substring(0, 2) + ":" + mList.get(position).CFB_06.substring(2, 4) + ":" + mList.get(position).CFB_06.substring(4, 6);
        String gender = mList.get(position).CFB_03.equals("M") ? "남성" :
                mList.get(position).CFB_03.equals("F") ? "여성" : "";
        int age = mList.get(position).CFB_04 / 10;

        String strText = date1 + " " + date2 + " " + gender + " " + age + "0대";

        viewHolder.tvText.setText(strText);

        return convertView;
    }

    public void updateData(ArrayList<CfbVO> list){ mList = list;}

    public void addData(ArrayList<CfbVO> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }

    static class ViewHolder{
        TextView tvText;
    }
}
