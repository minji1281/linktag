package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.util.ClsBitmap;
import com.linktag.base.util.ClsDateTime;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.INV_VO;
import com.linktag.linkapp.value_object.OcmVO;

import java.util.ArrayList;

public class InviteListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<INV_VO> mList;
    private LayoutInflater mInflater;

    private InviteBtnClickListener inviteBtnClickListener;

    public interface InviteBtnClickListener{
        void onListBtnClick(String btnName, int position);
    }

    public InviteListAdapter(Context context, ArrayList<INV_VO> list, InviteBtnClickListener inviteBtnClickListener) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.inviteBtnClickListener = inviteBtnClickListener;
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
            convertView = mInflater.inflate(R.layout.listitem_invitelist, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.ivShared = convertView.findViewById(R.id.ivShared);

            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(mContext.getResources().getColor(R.color.transparent_color));

            viewHolder.ivShared.setBackground(drawable);

            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.ivShared.setClipToOutline(true);
            }

            viewHolder.tvSharedNM = convertView.findViewById(R.id.tvSharedNM);
            viewHolder.tvInviter = convertView.findViewById(R.id.tvInviter);
            viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
            viewHolder.btnAccept = convertView.findViewById(R.id.btnAccept);
            viewHolder.btnDeny = convertView.findViewById(R.id.btnDeny);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int resource = convertView.getResources().getIdentifier("service_" + mList.get(position).SVC_01.toLowerCase() , "drawable", mContext.getPackageName());
        ClsBitmap.setSharedPhoto(mContext, viewHolder.ivShared, mList.get(position).INV_01, mList.get(position).CTD_08, "", resource);

        viewHolder.tvSharedNM.setText(mList.get(position).INV_01_NM);
        viewHolder.tvInviter.setText(mContext.getString(R.string.invite) + " : " + mList.get(position).INV_04_NM);
        viewHolder.tvDate.setText(ClsDateTime.ConvertDateFormat("yyyyMMdd", "yyyy. MM. dd", mList.get(position).INV_05));

        viewHolder.btnAccept.setTag(position);
        viewHolder.btnAccept.setOnClickListener(this);
        viewHolder.btnDeny.setTag(position);
        viewHolder.btnDeny.setOnClickListener(this);

        return convertView;
    }

    public void updateData(ArrayList<INV_VO> list) {
        mList = list;
    }

    @Override
    public void onClick(View v){
        if(this.inviteBtnClickListener != null){
            String btnName = "";

            if(v.getId() == R.id.btnAccept)
                btnName = "ACCEPT";
            else if (v.getId() == R.id.btnDeny)
                btnName = "DENY";

            this.inviteBtnClickListener.onListBtnClick(btnName, (int) v.getTag());
        }
    }

    static class ViewHolder {
        ImageView ivShared;
        TextView tvSharedNM;
        TextView tvInviter;
        TextView tvDate;
        Button btnAccept;
        Button btnDeny;
    }
}
