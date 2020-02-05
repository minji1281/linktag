package com.linktag.linkapp.ui.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.BRC_VO;

import java.util.ArrayList;

public class BoardBRCAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<BRC_VO> mList;
    private LayoutInflater mInflater;

    public BoardBRCAdapter(Context context, ArrayList<BRC_VO> list){
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
        ViewHolder vh;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_cmt_record, parent, false);

            vh = new ViewHolder();
            vh.tvUserName = convertView.findViewById(R.id.tvUserName);
            vh.tvWorkType = convertView.findViewById(R.id.tvWorkType);
            vh.tvWorkDate = convertView.findViewById(R.id.tvWorkDate);
            vh.tvWorkTime = convertView.findViewById(R.id.tvWorkTime);
            vh.tvWorkState = convertView.findViewById(R.id.tvWorkState);
            vh.btnMove = convertView.findViewById(R.id.btnMove);
            vh.hidd_CMT_01= convertView.findViewById(R.id.hidd_CMT_01);
            vh.hidd_gubun= convertView.findViewById(R.id.hidd_gubun);
            vh.btnMove.setOnClickListener(v -> test(vh.hidd_gubun.getText().toString(), vh.hidd_CMT_01.getText().toString()));
         //   vh.btnMove.setOnClickListener(v -> test(vh.hidd_gubun.getText().toString()));

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
//
//        vh.tvUserName.setText(mList.get(position).BRC_01); //제목
//        vh.tvWorkType.setText(mList.get(position).BRC_02); // 내용
//
//        vh.tvWorkTime.setText(mList.get(position).BRC_03);
//        vh.tvWorkDate.setText(mList.get(position).BRC_04);
//
//        vh.tvWorkState.setText(mList.get(position).BRC_99); // 내용
        vh.tvUserName.setText(mList.get(position).BRC_98+ " ("+ mList.get(position).BRC_98NM+ ")"); //작성자
        vh.tvWorkType.setText(mList.get(position).BRC_03); // 내용

        vh.hidd_gubun.setText("BRC");

        if(mList.get(position).BRC_01.equals("") ){vh.hidd_CMT_01.setText("");}
        else {vh.hidd_CMT_01.setText(mList.get(position).BRC_01);}


        return convertView;
    }

    public void updateData(ArrayList<BRC_VO> list){ mList = list;}

    private void test(String gubun, String CMT_01){
//        Intent intent = new Intent(mContext, BoardMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra("CMT_GB", gubun);
//        mContext.startActivity(intent);
//        Toast.makeText(mContext,gubun, Toast.LENGTH_SHORT).show();
        ((BoardPopup)BoardPopup.mContext5).Onsubmit(gubun,CMT_01);

    }

    static class ViewHolder{
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
