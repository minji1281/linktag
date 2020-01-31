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
import com.linktag.linkapp.value_object.NOT_VO;

import java.util.ArrayList;

public class BoardNOTAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<NOT_VO> mList;
    private LayoutInflater mInflater;

    public BoardNOTAdapter(Context context, ArrayList<NOT_VO> list){
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
            convertView = mInflater.inflate(R.layout.listitem_brd_record, parent, false);

            vh = new ViewHolder();
            vh.tvUserName = convertView.findViewById(R.id.tvUserName);
            vh.tvWorkType = convertView.findViewById(R.id.tvWorkType);
            vh.tvWorkDate = convertView.findViewById(R.id.tvWorkDate);
            vh.tvWorkTime = convertView.findViewById(R.id.tvWorkTime);
            vh.tvWorkState = convertView.findViewById(R.id.tvWorkState);
            vh.btnMove = convertView.findViewById(R.id.btnMove);
         //   vh.btnMove.setOnClickListener(v -> test(vh.hidd_gubun.getText().toString()));

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tvUserName.setText(mList.get(position).NOT_04); //제목
        vh.tvWorkType.setText(mList.get(position).NOT_05); // 내용

        vh.tvWorkTime.setText(mList.get(position).NOT_97);
        vh.tvWorkDate.setText(mList.get(position).NOT_06.substring(0,4)+"-"+mList.get(position).NOT_06.substring(4,6)+"-"+mList.get(position).NOT_06.substring(6,8));

        vh.tvWorkState.setText(mList.get(position).NOT_09); // 내용



        return convertView;
    }

    public void updateData(ArrayList<NOT_VO> list){ mList = list;}

    private void test(String gubun){

        Toast.makeText(mContext,gubun, Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder{
        TextView tvUserName;
        TextView tvWorkDate;
        TextView tvWorkType;
        TextView tvWorkTime;
        TextView tvWorkState;
    //    TextView hidd_gubun;
        ImageButton btnMove;

    }
}
