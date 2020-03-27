package com.linktag.linkapp.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.ARM_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class AlarmListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ARM_VO> mList;
    private LayoutInflater mInflater;
    protected InterfaceUser mUser;

    public AlarmListAdapter(Context context, ArrayList<ARM_VO> list) {
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
            convertView = mInflater.inflate(R.layout.listitem_alarm_record, parent, false);

            viewHolder = new ViewHolder();
          //  viewHolder.imgUserPhoto = convertView.findViewById(R.id.imgUserPhoto);
            if (Build.VERSION.SDK_INT >= 21) {
            //    viewHolder.imgUserPhoto.setClipToOutline(true);
            }

            viewHolder.tv_calenType = convertView.findViewById(R.id.tv_calenType);
            viewHolder.tv_calenTime = convertView.findViewById(R.id.tv_calenTime);
            viewHolder.btnMove = convertView.findViewById(R.id.btnMove);
            viewHolder.tv_calenTitle = convertView.findViewById(R.id.tv_calenTitle);
            viewHolder.tv_calenSubject = convertView.findViewById(R.id.tv_calenSubject);
            viewHolder.tvSvcl_04 = convertView.findViewById(R.id.tvSvcl_04);
            viewHolder.tvArm_01 = convertView.findViewById(R.id.tvArm_01);
            viewHolder.tvCtn_02 = convertView.findViewById(R.id.tvCtn_02);
            viewHolder.tvCtm_19 = convertView.findViewById(R.id.tvCtm_19);
            viewHolder.tvCtm_04 = convertView.findViewById(R.id.tvCtm_04);
            viewHolder.btnMove.setOnClickListener(v -> test(viewHolder.tvArm_01.getText().toString(), viewHolder.tvSvcl_04.getText().toString(), viewHolder.tvCtn_02.getText().toString(), viewHolder.tvCtm_19.getText().toString(), viewHolder.tvCtm_04.getText().toString()));

        //    viewHolder.btnMove.setOnClickListener(v -> test(viewHolder.hidd_gubun.getText().toString()));


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

     //   viewHolder.tvUserName.setText(mList.get(position).DSH_01);
        int resource = convertView.getResources().getIdentifier("service_" + mList.get(position).ARM_0101.toLowerCase() , "drawable", mContext.getPackageName());
        viewHolder.tv_calenType.setImageResource(resource);

//        if (mList.get(position).ARM_0101.substring(0,3).equals("POT")){         // 화분
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_POT1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("JDM")){   // 장독
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_JDM1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("CAR")){  // 차량
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_CAR1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("COS")){  // 화장품
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_COS1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("PCM")){  // 컴퓨터 관리
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_PCM1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("FRM")){  // 냉장고
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_RFM1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("TRP")){  //  복약
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_TRP1);
//        }else if (mList.get(position).ARM_0101.substring(0,3).equals("FRM")){  //  필터
//            viewHolder.tv_calenType.setImageResource(R.drawable.service_FRM1);
//        }

        String text1,text2,text3,text4;

        text1 = mList.get(position).ARM_92.substring(4,6)+"/"+mList.get(position).ARM_92.substring(6,8)+" ";
        if(Integer.parseInt(mList.get(position).ARM_92.substring(8,10)) >= 12){
            text2 = "pm ";
        }else {text2 = "am ";}

        if(Integer.parseInt(mList.get(position).ARM_92.substring(8,10)) > 12){
            text3 = String.valueOf(Integer.parseInt(mList.get(position).ARM_92.substring(8,10))-12);
        }else {text3 = mList.get(position).ARM_92.substring(8,10);}

        text4 = ":"+mList.get(position).ARM_92.substring(10,12);
        StringBuilder sb = new StringBuilder(text1);
        sb.append( text2 );
        sb.append( text3 );
        sb.append( text4 );


        //  viewHolder.tv_calenType.setText(mList.get(position).CTM_19);
        viewHolder.tv_calenTime.setText(sb);

        viewHolder.tv_calenTitle.setText(mList.get(position).ARM_90);
        viewHolder.tv_calenSubject.setText(mList.get(position).ARM_91);
        viewHolder.tvSvcl_04.setText(mList.get(position).SVCL_04);
        viewHolder.tvArm_01.setText(mList.get(position).ARM_01);
        viewHolder.tvCtn_02.setText(mList.get(position).ARM_ID);
        viewHolder.tvCtm_19.setText(mList.get(position).CTM_19);
        viewHolder.tvCtm_04.setText(mList.get(position).CTM_04);


        return convertView;
    }

    public void updateData(ArrayList<ARM_VO> list) {
        mList = list;
    }

    private void test(String scancode, String Svcl_04, String Ctn_02, String Ctm_19, String Ctm_04){


        if(!Svcl_04.equals("") && Svcl_04 != null)
        {
            String packageName = mContext.getPackageName();

            try{
                // List 액티비티 실행
                Class clsList = Class.forName(packageName + Svcl_04);

                CtdVO intentVO = new CtdVO();
                intentVO.CTN_02 = Ctn_02;
                intentVO.CTM_19 = Ctm_19;
                intentVO.CTM_04 = Ctm_04;


               Intent intent = new Intent(mContext, clsList);
                intent.putExtra("scancode", scancode); //scancode
                intent.putExtra("intentVO", intentVO);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                mContext.startActivity(intent);

            } catch (Exception e){
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(mContext, "해당 서비스의 경로를 찾을 수 없습니다.\n관리자에게 문의 바랍니다.", Toast.LENGTH_LONG).show();
        }

    }


    static class ViewHolder {
      //  ImageView imgUserPhoto;
        ImageView tv_calenType;
        TextView tv_calenTime;
        TextView tv_calenTitle;
        TextView tv_calenSubject;
        TextView tvSvcl_04;
        TextView tvArm_01;
        TextView tvCtn_02;
        TextView tvCtm_19;
        TextView tvCtm_04;
        LinearLayout btnMove;
    }
}
