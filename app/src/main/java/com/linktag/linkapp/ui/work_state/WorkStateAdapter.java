package com.linktag.linkapp.ui.work_state;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.util.ClsDateTime;

import java.util.ArrayList;

public class WorkStateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<LED_VO> mList;
    private LayoutInflater mInflater;

    public WorkStateAdapter(Context context, ArrayList<LED_VO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_work_record, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgUserPhoto = convertView.findViewById(R.id.imgUserPhoto);
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.imgUserPhoto.setClipToOutline(true);
            }

            viewHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
            viewHolder.tvWorkType = convertView.findViewById(R.id.tvWorkType);
            viewHolder.tvWorkTime = convertView.findViewById(R.id.tvWorkTime);
            viewHolder.tvWorkState = convertView.findViewById(R.id.tvWorkState);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 사용자 이미지 가져올때
//        Glide.with(mContext).load(mList.get(position).UserPhoto)
//                .placeholder(R.drawable.main_profile_no_image)
//                .error(R.drawable.main_profile_no_image)
//                .into(viewHolder.imgUserPhoto);

        viewHolder.tvUserName.setText(mList.get(position).LED_04_NM);
        viewHolder.tvWorkType.setText(mList.get(position).CDO_06_NM + "  " + mList.get(position).CDO_07_NM);
        viewHolder.tvWorkTime.setText(ClsDateTime.ConvertStringBuffer(mList.get(position).LED_07, ":", 2) + " - " +
                                      ClsDateTime.ConvertStringBuffer(mList.get(position).LED_08, ":", 2));
        viewHolder.tvWorkState.setText(mList.get(position).STAT);

        /*
        String strWorkType = "시급직";
        if (mList.get(position).WorkType.equals("FULL_TYPE"))
            strWorkType = "월급직";

        viewHolder.tvWorkType.setText(strWorkType);
        viewHolder.tvWorkTime.setText(mList.get(position).WorkTime);

        String strWorkState = "연차";
        if (mList.get(position).WorkState.equals("WORKING"))
            strWorkState = "근무중";
        viewHolder.tvWorkState.setText(strWorkState);
*/
        return convertView;
    }

    public void updateData(ArrayList<LED_VO> list) {
        mList = list;
    }

    static class ViewHolder {
        ImageView imgUserPhoto;
        TextView tvUserName;
        TextView tvWorkType;
        TextView tvWorkTime;
        TextView tvWorkState;
    }
}
