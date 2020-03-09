package com.linktag.linkapp.ui.work_state;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CMT_VO;
import com.linktag.base.util.ClsDateTime;


import java.util.ArrayList;

public class WorkStateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CMT_VO> mList;
    private LayoutInflater mInflater;

    public WorkStateAdapter(Context context, ArrayList<CMT_VO> list) {
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
          //  viewHolder.imgUserPhoto = convertView.findViewById(R.id.imgUserPhoto);
            if (Build.VERSION.SDK_INT >= 21) {
            //    viewHolder.imgUserPhoto.setClipToOutline(true);
            }

            viewHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
            viewHolder.tvWorkType = convertView.findViewById(R.id.tvWorkType);
            viewHolder.tvWorkTime = convertView.findViewById(R.id.tvWorkTime);
            viewHolder.tvWorkDate = convertView.findViewById(R.id.tvWorkDate);
            viewHolder.tvWorkState = convertView.findViewById(R.id.tvWorkState);
            viewHolder.hidd_gubun= convertView.findViewById(R.id.hidd_gubun);
            viewHolder.btnMove = convertView.findViewById(R.id.btnMove);
            viewHolder.btnMove.setOnClickListener(v -> test(viewHolder.hidd_gubun.getText().toString()));







            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 사용자 이미지 가져올때
//        Glide.with(mContext).load(mList.get(position).UserPhoto)
//                .placeholder(R.drawable.main_profile_no_image)
//                .error(R.drawable.main_profile_no_image)
//                .into(viewHolder.imgUserPhoto);

        if(mList.get(position).CMT_GB.equals("BRD") ){viewHolder.tvUserName.setText(R.string.dash_02); viewHolder.hidd_gubun.setText("BRD");}
        else if(mList.get(position).CMT_GB.equals("NOT") ){viewHolder.tvUserName.setText(R.string.dash_01); viewHolder.hidd_gubun.setText("NOT"); }
     //   viewHolder.tvUserName.setText(mList.get(position).DSH_01);

        if(mList.get(position).CMT_03.equals("") ){viewHolder.tvWorkType.setText("");}
        else {viewHolder.tvWorkType.setText(mList.get(position).CMT_03);}

        if(mList.get(position).CMT_02.equals("") ){viewHolder.tvWorkDate.setText("");}
        else {viewHolder.tvWorkDate.setText(mList.get(position).CMT_02);}

//        if(mList.get(position).DSH_06.equals("") ){viewHolder.tvWorkDate.setText("");}
//        else {viewHolder.tvWorkDate.setText(mList.get(position).DSH_06.substring(0,4)+"-"+mList.get(position).DSH_06.substring(4,6)+"-"+mList.get(position).DSH_06.substring(6,8));}


        if(mList.get(position).CMT_01.equals("") ){viewHolder.tvWorkTime.setText("0");}
        else {viewHolder.tvWorkTime.setText(mList.get(position).CMT_01);}
//        viewHolder.tvWorkType.setText(mList.get(position).DSH_04);
//        viewHolder.tvWorkTime.setText(mList.get(position).DSH_06);
//        viewHolder.tvWorkState.setText(mList.get(position).DSH_97);

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

    public void updateData(ArrayList<CMT_VO> list) {
        mList = list;
    }

    private void test(String gubun){

        Toast.makeText(mContext,gubun, Toast.LENGTH_SHORT).show();
    }


    static class ViewHolder {
      //  ImageView imgUserPhoto;
        TextView tvUserName;
        TextView tvWorkDate;
        TextView tvWorkType;
        TextView tvWorkTime;
        TextView tvWorkState;
        TextView hidd_gubun;
        ImageButton btnMove;
    }
}
