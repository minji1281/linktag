package com.linktag.linkapp.ui.iam;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.model.IAMModel;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.iam.IamDetail;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.IamVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IamRecycleAdapter extends RecyclerView.Adapter<IamRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<IamVO> mList;
    private ArrayList<IamVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    private CtdVO intentVO;


    Filter listFilter;

    IamRecycleAdapter(Context context, ArrayList<IamVO> list, CtdVO intentVO) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        filteredmlist = list;
        this.intentVO = intentVO;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null)
            listFilter = new ListFilter();

        return listFilter;
    }


    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String charString = constraint.toString();
            if (charString.isEmpty()) {
                results.values = mList;
                results.count = mList.size();
            } else {
                ArrayList<IamVO> itemList = new ArrayList<>();
                for (IamVO item : mList) {
                    if (item.IAM_03.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredmlist = (ArrayList<IamVO>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public IamRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_iam_list, parent, false);
        IamRecycleAdapter.ViewHolder viewHolder = new IamRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        viewHolder.tv_name.setText(filteredmlist.get(position).IAM_03);

    }



    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    IamVO iamvo = new IamVO();
                    iamvo.setIAM_ID(filteredmlist.get(position).IAM_ID);
                    iamvo.setIAM_01(filteredmlist.get(position).IAM_01);
                    iamvo.setIAM_02(filteredmlist.get(position).IAM_02);
                    iamvo.setIAM_03(filteredmlist.get(position).IAM_03);
                    iamvo.setIAM_96(filteredmlist.get(position).IAM_96);
                    iamvo.setIAM_97(filteredmlist.get(position).IAM_97);
                    Intent intent = new Intent(mContext, IamDetail.class);
                    intent.putExtra("intentVO", intentVO);
                    intent.putExtra("IamVO", iamvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<IamVO> list) {
        mList = list;
    }


}
