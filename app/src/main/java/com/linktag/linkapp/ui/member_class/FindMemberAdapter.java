package com.linktag.linkapp.ui.member_class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CDO_VO;

import java.util.ArrayList;

public class FindMemberAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CDO_VO> mList;
    private LayoutInflater mInflater;

    public FindMemberAdapter(Context context, ArrayList<CDO_VO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_find_member, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
            viewHolder.tvUserNum = convertView.findViewById(R.id.tvUserNum);
            viewHolder.tvWorkType = convertView.findViewById(R.id.tvWorkType);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(mList.get(position).CDO_04);
        viewHolder.tvUserNum.setText(mList.get(position).CDO_03);
        viewHolder.tvWorkType.setText(mList.get(position).CDO_06_NM + " " + mList.get(position).CDO_07_NM);

        return convertView;
    }

    public void updateData(ArrayList<CDO_VO> list) {
        mList = list;
    }

    static class ViewHolder {
        TextView tvUserName;
        TextView tvUserNum;
        TextView tvWorkType;
    }
}
