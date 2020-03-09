package com.linktag.linkapp.ui.dash_state;

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
import com.linktag.linkapp.value_object.DSH_VO;
import com.linktag.base.util.ClsDateTime;


import java.util.ArrayList;

public class DashStateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<DSH_VO> mList;
    private LayoutInflater mInflater;
    protected InterfaceUser mUser;

    public DashStateAdapter(Context context, ArrayList<DSH_VO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_dsh_record, parent, false);

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

        if(mList.get(position).DSH_GB.equals("BRD") ){viewHolder.tvUserName.setText(R.string.dash_02); viewHolder.hidd_gubun.setText("BRD"); }
        else if(mList.get(position).DSH_GB.equals("NOT") ){viewHolder.tvUserName.setText(R.string.dash_01); viewHolder.hidd_gubun.setText("NOT"); }
     //   viewHolder.tvUserName.setText(mList.get(position).DSH_01);

        if(mList.get(position).DSH_97.equals("") ){viewHolder.tvWorkTime.setText("");}
        else {viewHolder.tvWorkTime.setText(mList.get(position).DSH_97);}

        if(mList.get(position).DSH_04.equals("") ){viewHolder.tvWorkType.setText("");}
        else {viewHolder.tvWorkType.setText(mList.get(position).DSH_04);}

        if(mList.get(position).DSH_06.equals("") ){viewHolder.tvWorkDate.setText("");}
        else {viewHolder.tvWorkDate.setText(mList.get(position).DSH_06.substring(0,4)+"-"+mList.get(position).DSH_06.substring(4,6)+"-"+mList.get(position).DSH_06.substring(6,8));}


//        if(mList.get(position).DSH_09.equals("") ){viewHolder.tvWorkTime.setText("0");}
//        else {viewHolder.tvWorkTime.setText(mList.get(position).DSH_09);}




        return convertView;
    }

    public void updateData(ArrayList<DSH_VO> list) {
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
        TextView tvUserName;
        TextView tvWorkDate;
        TextView tvWorkType;
        TextView tvWorkTime;
        TextView tvWorkState;
        TextView hidd_gubun;
        ImageButton btnMove;
    }
}
