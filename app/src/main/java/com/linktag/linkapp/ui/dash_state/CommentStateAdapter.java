package com.linktag.linkapp.ui.dash_state;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.board.BoardMain;
import com.linktag.linkapp.ui.board.BoardPopup;
import com.linktag.linkapp.value_object.CMT_VO;

import java.util.ArrayList;

public class CommentStateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CMT_VO> mList;
    private LayoutInflater mInflater;
    protected InterfaceUser mUser;

    public CommentStateAdapter(Context context, ArrayList<CMT_VO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_cmt_record, parent, false);

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
            viewHolder.hidd_CMT_01= convertView.findViewById(R.id.hidd_CMT_01);
            viewHolder.btnMove = convertView.findViewById(R.id.btnMove);
            //viewHolder.btnMove.setOnClickListener(v -> test(viewHolder.hidd_gubun.getText().toString(), viewHolder.hidd_CMT_01.getText().toString()));
            viewHolder.btnMove.setVisibility(View.GONE);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 사용자 이미지 가져올때
//        Glide.with(mContext).load(mList.get(position).UserPhoto)
//                .placeholder(R.drawable.main_profile_no_image)
//                .error(R.drawable.main_profile_no_image)
//                .into(viewHolder.imgUserPhoto);
//        if(mList.get(position).CMT_GB.equals("BRC") ){
//            String info = "[" ;
//            info += R.string.dash_01;
//            info +=  "]";
//            viewHolder.tvUserName.setText(info);
//        }else if(mList.get(position).CMT_GB.equals("NOC") ){
//            String info = "[" ;
//            info += R.string.dash_02;
//            info +=  "]";
//            viewHolder.tvUserName.setText(info);
//        }

        if(mList.get(position).CMT_GB.equals("BRC") ){viewHolder.tvUserName.setText(R.string.dash_13); viewHolder.hidd_gubun.setText("BRC"); }
        else if(mList.get(position).CMT_GB.equals("NOC") ){viewHolder.tvUserName.setText(R.string.dash_14); viewHolder.hidd_gubun.setText("NOC"); }


        if(mList.get(position).CMT_03.equals("") ){viewHolder.tvWorkType.setText("");}
        else {viewHolder.tvWorkType.setText(mList.get(position).CMT_03);}

        if(mList.get(position).CMT_01.equals("") ){viewHolder.hidd_CMT_01.setText("");}
        else {viewHolder.hidd_CMT_01.setText(mList.get(position).CMT_01);}

        if(mList.get(position).CMT_98NM.equals("") ){viewHolder.tvWorkDate.setText("");}
        else {viewHolder.tvWorkDate.setText(mList.get(position).CMT_98NM);}

//        if(mList.get(position).CMT_06.equals("") ){viewHolder.tvWorkDate.setText("");}
//        else {viewHolder.tvWorkDate.setText(mList.get(position).CMT_06.substring(0,4)+"-"+mList.get(position).CMT_06.substring(4,6)+"-"+mList.get(position).CMT_06.substring(6,8));}

        if(mList.get(position).CMT_97.equals("") ){viewHolder.tvWorkTime.setText("");}
        else {viewHolder.tvWorkTime.setText(mList.get(position).CMT_97);}

        if(mList.get(position).CMT_99.equals("") ){viewHolder.tvWorkState.setText("");}
        else {viewHolder.tvWorkState.setText(mList.get(position).CMT_99.substring(0,4)+"-"+mList.get(position).CMT_99.substring(4,6)+"-"+mList.get(position).CMT_99.substring(6,8));}
        return convertView;
    }

    public void updateData(ArrayList<CMT_VO> list) {
        mList = list;
    }

    private void test(String gubun, String CMT_01){
//        Intent intent = new Intent(mContext, BoardMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra("CMT_GB", gubun);
//        mContext.startActivity(intent);
//        Toast.makeText(mContext,gubun, Toast.LENGTH_SHORT).show();
        ((BoardPopup)BoardPopup.mContext5).Onsubmit(gubun,CMT_01);

    }

    static class ViewHolder {
      //  ImageView imgUserPhoto;
        TextView tvUserName;
        TextView tvWorkDate;
        TextView tvWorkType;
        TextView tvWorkTime;
        TextView tvWorkState;
        TextView hidd_gubun;
        TextView hidd_CMT_01;
        ImageButton btnMove;
    }


}
