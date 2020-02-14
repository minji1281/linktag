package com.linktag.linkapp.ui.pcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.PCDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PcdVO;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PcdRecycleAdapter extends RecyclerView.Adapter<PcdRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PcdVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private PcdRecycleAdapter mAdapter;

    private HashMap<String, String> map = new HashMap<String, String>();


    PcdRecycleAdapter(Context context, ArrayList<PcdVO> list) {
        mContext = context;
        mList = list;
        map.put("선택", "0");
        map.put("CPU", "1");
        map.put("메인보드", "2");
        map.put("그래픽카드", "3");
        map.put("RAM", "4");
        map.put("SSD", "5");
        map.put("HDD", "6");
        map.put("파워", "7");
        map.put("쿨러", "8");
        map.put("케이스", "9");
    }

    public void setmAdapter(PcdRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public PcdRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_pcd_hw_list, parent, false);
        PcdRecycleAdapter.ViewHolder viewHolder = new PcdRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_hw.setText(mList.get(position).PCD_05);
        viewHolder.sp_hw.setSelection(Integer.parseInt(mList.get(position).PCD_04));

        if (mList.get(position).PCD_04.equals("0")) {
            viewHolder.imageView.setImageResource(R.drawable.ic_plus_round);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.ic_minus_round);
        }

        viewHolder.sp_hw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                mList.get(position).setPCD_04(map.get(viewHolder.sp_hw.getSelectedItem()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {

                mList.get(position).setPCD_05(viewHolder.tv_hw.getText().toString());

                Drawable tempImg = viewHolder.imageView.getDrawable();
                Drawable tempRes = mContext.getResources().getDrawable(R.drawable.ic_plus_round);
                Bitmap tmpBitmap = ((BitmapDrawable) tempImg).getBitmap();
                Bitmap tmpBitmapRes = ((BitmapDrawable) tempRes).getBitmap();


                if (tmpBitmap.equals(tmpBitmapRes)) {
                    if (mList.get(position).getPCD_04().equals("0")) {
                        Toast.makeText(mContext, "선택 필요", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        viewHolder.imageView.setImageResource(R.drawable.ic_minus_round);
                        mList.get(position).setGUBUN("INSERT");
                    }
                } else {
                    mList.get(position).setGUBUN("DELETE");
                }

                requestPCD_CONTROL(mList.get(position));
            }
        });

    }


    public void requestPCD_CONTROL(PcdVO pcdVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<PCDModel> call = Http.pcd(HttpBaseService.TYPE.POST).PCD_CONTROL(
                BaseConst.URL_HOST,
                pcdVO.GUBUN,
                pcdVO.PCD_ID,
                pcdVO.PCD_01,
                pcdVO.PCD_02,
                pcdVO.PCD_03,
                pcdVO.PCD_04,
                pcdVO.PCD_05,
                pcdVO.PCD_98
        );


        call.enqueue(new Callback<PCDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCDModel> call, Response<PCDModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<PCDModel> response = (Response<PCDModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<PCDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private Spinner sp_hw;
        private TextView tv_hw;
        private ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sp_hw = itemView.findViewById(R.id.sp_hw);
            tv_hw = itemView.findViewById(R.id.tv_hw);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }

    public void updateData(ArrayList<PcdVO> list) {
        mList = list;
    }


}
