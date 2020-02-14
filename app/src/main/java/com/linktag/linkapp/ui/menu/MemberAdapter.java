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
import com.linktag.linkapp.value_object.OcmVO;
import com.linktag.linkapp.value_object.SvcVO;

import java.util.ArrayList;

public class MemberAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OcmVO> mList;
    private LayoutInflater mInflater;

    public MemberAdapter(Context context, ArrayList<OcmVO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_member, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.tvEmail = convertView.findViewById(R.id.tvEmail);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(mList.get(position).OCM_02);
        viewHolder.tvEmail.setText(mList.get(position).OCM_21);

        return convertView;
    }

    public void updateData(ArrayList<OcmVO> list) {
        mList = list;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvEmail;
    }
}
