package com.linktag.linkapp.ui.vac;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.model.VADModel;
import com.linktag.linkapp.model.VAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.VadVO;
import com.linktag.linkapp.value_object.VamVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.ui.vac.VacDetail.mSpinnerList;
import static com.linktag.linkapp.ui.vac.VacDetail.tv_vamCnt;
import static com.linktag.linkapp.ui.vac.VacDetail.tv_vam_nodata;
import static com.linktag.linkapp.ui.vac.VacDetail.vadSpinner;

public class VamRecycleAdapter extends RecyclerView.Adapter<VamRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VamVO> mList;
    private ArrayList<VadVO> mList_vad;
    private LayoutInflater mInflater;
    private View view;
    private VamRecycleAdapter mAdapter;
    private String[] str;

    private InterfaceUser mUser = InterfaceUser.getInstance();


    private int lastSelectedPosition = -1;

    public static String VAM_03 = "";

    VamRecycleAdapter(Context context, ArrayList<VamVO> list) {
        mContext = context;
        mList = list;

    }

    public void setmAdapter(VamRecycleAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public VamRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_vam_list, parent, false);

        VamRecycleAdapter.ViewHolder viewHolder = new VamRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        viewHolder.tv_name.setText(mList.get(position).VAM_03);

        if (lastSelectedPosition == position) {
            viewHolder.radioButton.setChecked(true);
            viewHolder.tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
            requestVAD_SELECT(mList.get(position));
            VAM_03 = mList.get(position).VAM_03;

        } else {
            viewHolder.radioButton.setChecked(false);
            viewHolder.tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.path_black));
        }

        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = position;
                notifyDataSetChanged();
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if (mList.get(position).VAM_01.equals("")){
                    mList.remove(position);
                    if (mList == null)
                        mList = new ArrayList<>();
                    tv_vamCnt.setText("("+mList.size()+")");

                    if (mList.size() == 0) {
                        tv_vam_nodata.setVisibility(View.VISIBLE);
                        VacDetail.recyclerView.setVisibility(View.GONE);
                        VacDetail.alarmState = false;

                        VacDetail.imageView.setImageResource(R.drawable.alarm_state_off);
                        VacDetail.vacVO.setARM_03("N");

                    } else {
                        VacDetail.recyclerView.setVisibility(View.VISIBLE);
                        tv_vam_nodata.setVisibility(View.GONE);
                        VacDetail.alarmState = true;
                    }

                    mAdapter.updateData(mList);
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    requestVAM_CONTROL(mList.get(position));
                }

            }
        });

    }


    public void requestVAM_CONTROL(VamVO vamVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<VAMModel> call = Http.vam(HttpBaseService.TYPE.POST).VAM_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                vamVO.VAM_ID,
                vamVO.VAM_01,
                vamVO.VAM_02,
                vamVO.VAM_03,
                vamVO.VAM_98
        );


        call.enqueue(new Callback<VAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VAMModel> call, Response<VAMModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VAMModel> response = (Response<VAMModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();
                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                            requestLOG_CONTROL(vamVO.VAM_ID, vamVO.VAM_01, "2", "" + " " + vamVO.VAM_03 + " " + "제거");

                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<VAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }

    public void requestVAD_SELECT(VamVO vamVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                vamVO.VAM_ID,
                vamVO.VAM_01,
                vamVO.VAM_02
        );


        call.enqueue(new Callback<VADModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VADModel> call, Response<VADModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VADModel> response = (Response<VADModel>) msg.obj;

                            mList_vad = response.body().Data;
                            if (mList_vad == null)
                                mList_vad = new ArrayList<>();


                            if (response.body().Total > 0) {
                                str = new String[response.body().Total];
                            } else {
                                str = new String[1];
                            }

                            mSpinnerList.clear();
                            if (response.body().Total > 0) {
                                for (int i = 0; i < response.body().Total; i++) {
                                    str[i] =  stringTodateFormat(response.body().Data.get(i).VAD_96) +" "+ response.body().Data.get(i).VAD_04;
                                    mSpinnerList.add(new VacDetail.SpinnerList(response.body().Data.get(i).VAD_02,
                                            response.body().Data.get(i).VAD_03,
                                            "["+VAM_03+"]" +"\n"+ response.body().Data.get(i).VAD_04));
                                }
                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                                vadSpinner.setAdapter(adapter);
                            } else {
                                str[0] = "정보 없음";
                                mSpinnerList.add(new VacDetail.SpinnerList("","",""));
                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                                vadSpinner.setAdapter(adapter);
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VADModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

    private void requestLOG_CONTROL(String LOG_ID, String LOG_01, String LOG_03, String LOG_04) {
        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            Toast.makeText(mContext, mContext.getString(R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                LOG_ID,
                LOG_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_VACL_CONTROL"
        );

        call.enqueue(new Callback<LOG_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response) {

            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t) {
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private Button btn_delete;
        private RadioButton radioButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }

    public void updateData(ArrayList<VamVO> list) {
        mList = list;
    }


    public String stringTodateFormat(String str) {
        String retStr ="";
        //yyyy.MM.dd
        retStr = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8);
        return retStr;
    }
    public String stringTotimeFormat(String str) {
        String retStr ="";
        //hh:mm

        retStr = str.substring(8,10) + " : " + str.substring(10,12);
        return retStr;
    }

}
