package com.linktag.linkapp.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.board.BoardMain;
import com.linktag.linkapp.value_object.ARM_VO;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ARM_VO> mList;
    private LayoutInflater mInflater;
    protected InterfaceUser mUser;

    public CalendarAdapter(Context context, ArrayList<ARM_VO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_calendar_record, parent, false);

            viewHolder = new ViewHolder();
          //  viewHolder.imgUserPhoto = convertView.findViewById(R.id.imgUserPhoto);
            if (Build.VERSION.SDK_INT >= 21) {
            //    viewHolder.imgUserPhoto.setClipToOutline(true);
            }

            viewHolder.tv_calenType = convertView.findViewById(R.id.tv_calenType);
            viewHolder.tv_calenTime = convertView.findViewById(R.id.tv_calenTime);
            viewHolder.tv_calenTitle = convertView.findViewById(R.id.tv_calenTitle);
            viewHolder.tv_calenSubject = convertView.findViewById(R.id.tv_calenSubject);

        //    viewHolder.btnMove.setOnClickListener(v -> test(viewHolder.hidd_gubun.getText().toString()));


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

     //   viewHolder.tvUserName.setText(mList.get(position).DSH_01);

        if (mList.get(position).CTM_19.equals("P")){
            viewHolder.tv_calenType.setImageResource(R.drawable.solo);
        }else{
            viewHolder.tv_calenType.setImageResource(R.drawable.team);
        }
      //  viewHolder.tv_calenType.setText(mList.get(position).CTM_19);
        viewHolder.tv_calenTime.setText(mList.get(position).ARM_92.substring(8,10)+":"+mList.get(position).ARM_92.substring(10,12));
        viewHolder.tv_calenTitle.setText(mList.get(position).ARM_90);
        viewHolder.tv_calenSubject.setText(mList.get(position).ARM_91);


        return convertView;
    }

    public void updateData(ArrayList<ARM_VO> list) {
        mList = list;
    }

    private void test(String gubun){
        Intent intent = new Intent(mContext, BoardMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("DSH_GB", gubun);
        mContext.startActivity(intent);
//        Toast.makeText(mContext,gubun, Toast.LENGTH_SHORT).show();
    }


    static class ViewHolder {
      //  ImageView imgUserPhoto;
        ImageView tv_calenType;
        TextView tv_calenTime;
        TextView tv_calenTitle;
        TextView tv_calenSubject;
    }
}
