package com.linktag.linkapp.ui.trp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.TrdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrdRecycleAdapter_horizontal extends RecyclerView.Adapter<TrdRecycleAdapter_horizontal.ViewHolder> {

    private Context mContext;
    private ArrayList<TrdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private TrdRecycleAdapter_horizontal mAdapter;

    TrdRecycleAdapter_horizontal(Context context, ArrayList<TrdVO> list) {
        mContext = context;
        mList = list;
    }

    public void setmAdapter(TrdRecycleAdapter_horizontal mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public TrdRecycleAdapter_horizontal.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_trd_list_horizontal
                , parent, false);
        TrdRecycleAdapter_horizontal.ViewHolder viewHolder = new TrdRecycleAdapter_horizontal.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String am_pm = "";
        int hourOfDay = Integer.parseInt(mList.get(position).TRD_96.substring(8, 10));
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            am_pm = mContext.getString(R.string.trp_Pm);
        } else {
            am_pm = mContext.getString(R.string.trp_Am);
        }
        String minute = mList.get(position).TRD_96.substring(10);

        viewHolder.tv_alarmItem.setText(am_pm + " " + hourOfDay + ":" + minute);


    }



    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_alarmItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_alarmItem = itemView.findViewById(R.id.tv_alarmItem);

        }
    }

    public void updateData(ArrayList<TrdVO> list) {
        mList = list;
    }


}
