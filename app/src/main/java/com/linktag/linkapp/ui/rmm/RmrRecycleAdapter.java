package com.linktag.linkapp.ui.rmm;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.RMR_VO;

import java.util.ArrayList;

public class RmrRecycleAdapter extends RecyclerView.Adapter<RmrRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RMR_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    RmrRecycleAdapter(Context context, ArrayList<RMR_VO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public RmrRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_rmr
                , parent, false);
        RmrRecycleAdapter.ViewHolder viewHolder = new RmrRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if(mList.get(position).boolChange){ //변경했을때
            if(mList.get(position).RMR_05.equals("")){ //예약
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.layoutRmr.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                } else {
                    viewHolder.layoutRmr.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                }

                viewHolder.tvTime.setTextColor(Color.WHITE);
                viewHolder.tvUser.setTextColor(Color.WHITE);
            }
            else{ //예약취소
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.layoutRmr.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                } else {
                    viewHolder.layoutRmr.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                }

                viewHolder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                viewHolder.tvUser.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            }
        }
        else{ //변경안됨
            if(mList.get(position).RMR_05.equals("")){ //예약안함
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.layoutRmr.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                } else {
                    viewHolder.layoutRmr.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                }

                viewHolder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                viewHolder.tvUser.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            }
            else{ //예약
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.layoutRmr.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                } else {
                    viewHolder.layoutRmr.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                }

                viewHolder.tvTime.setTextColor(Color.WHITE);
                viewHolder.tvUser.setTextColor(Color.WHITE);
            }
        }

        viewHolder.tvTime.setText(mList.get(position).RMR_04.substring(0,2) + ":" + mList.get(position).RMR_04.substring(2));
        viewHolder.tvUser.setText(mList.get(position).RMR_05_NM);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutRmr;
        TextView tvTime;
        TextView tvUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutRmr = itemView.findViewById(R.id.layoutRmr);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUser = itemView.findViewById(R.id.tvUser);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mList.get(position).RMR_05.equals(mUser.Value.OCM_01) || mList.get(position).RMR_05.equals("")){ //다른사람이 예약한 시간은 수정할 수 없다.
                        ReserveList tmp = new ReserveList(mList.get(position).RMR_02, mList.get(position).RMR_04); //예약
                        if(mList.get(position).boolChange){
                            RmdRecycleAdapter.RMR_04_list.remove(tmp);
                            mList.get(position).boolChange = false;
                        }
                        else{
                            RmdRecycleAdapter.RMR_04_list.add(tmp);
                            mList.get(position).boolChange = true;
                        }

                        setBackgroundChange(layoutRmr, tvTime, tvUser);
                    }
                }
            });
        }
    }

    public void updateData(ArrayList<RMR_VO> list) {
        mList = list;
    }

    private void setBackgroundChange(LinearLayout layoutRmr, TextView tvTime, TextView tvUser){
        if(layoutRmr.getBackground().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.btn_round_gray_8dp).getConstantState())) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layoutRmr.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
            } else {
                layoutRmr.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
            }
            tvTime.setTextColor(Color.WHITE);
            tvUser.setTextColor(Color.WHITE);
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layoutRmr.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
            } else {
                layoutRmr.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
            }
            tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            tvUser.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
        }

    }


}
