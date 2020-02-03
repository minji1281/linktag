package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.SvcVO;

import java.util.ArrayList;

public class AddServiceAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SvcVO> mList;
    private LayoutInflater mInflater;

    public AddServiceAdapter(Context context, ArrayList<SvcVO> list) {
        this.mContext = context;
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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.griditem_add_service, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.ivServiceIcon = convertView.findViewById(R.id.ivServiceIcon);
            viewHolder.tvServiceNM = convertView.findViewById(R.id.tvServiceNM);
            viewHolder.ivUseCHK = convertView.findViewById(R.id.ivUseCHK);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvServiceNM.setText(mList.get(position).SVC_03);


        if(mList.get(position).SVC_CHK.equals("Y"))
            viewHolder.ivUseCHK.setBackground(ContextCompat.getDrawable(mContext, R.drawable.base_checkbox_checked));
        else
            viewHolder.ivUseCHK.setBackground(ContextCompat.getDrawable(mContext, R.drawable.base_checkbox_unchecked));


        return convertView;
    }

    public void updateData(ArrayList<SvcVO> list) {
        mList = list;
    }

    static class ViewHolder {
        ImageView ivServiceIcon;
        TextView tvServiceNM;
        ImageView ivUseCHK;
    }
}
