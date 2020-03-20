package com.linktag.linkapp.ui.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CAD_VO;

import java.text.NumberFormat;
import java.util.ArrayList;

public class CadAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CAD_VO> mList;
    private ArrayList<CAD_VO> filteredmlist;
    private LayoutInflater mInflater;
    private InterfaceUser mUser;

    Filter listFilter;

    public CadAdapter(Context context, ArrayList<CAD_VO> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUser = InterfaceUser.getInstance();
        filteredmlist = list;
    }

    @Override
    public int getCount() {
        return filteredmlist.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredmlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_find_cad, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgGubunIcon = (ImageView) convertView.findViewById(R.id.imgGubunIcon);
            viewHolder.tvDay = convertView.findViewById(R.id.tvDay);
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.tvMoney = convertView.findViewById(R.id.tvMoney);
            viewHolder.tvKm =  convertView.findViewById(R.id.tvKm);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Image
        if(filteredmlist.get(position).CAD_05.equals("1")){ //1:교체
            viewHolder.imgGubunIcon.setImageResource(R.drawable.ic_cad_change);
        }
        else{ //1:점검
            viewHolder.imgGubunIcon.setImageResource(R.drawable.ic_cad_check);
        }

        //Text
        viewHolder.tvDay.setText(mContext.getString(R.string.cad_list_day) + " " + filteredmlist.get(position).CAD_03.substring(0, 4) + "." + filteredmlist.get(position).CAD_03.substring(4, 6) + "." + filteredmlist.get(position).CAD_03.substring(6, 8));
        viewHolder.tvName.setText(filteredmlist.get(position).CAD_04);
        viewHolder.tvMoney.setText(mContext.getString(R.string.price_unit) + NumberFormat.getInstance().format(filteredmlist.get(position).CAD_07));
        viewHolder.tvKm.setText(NumberFormat.getInstance().format(filteredmlist.get(position).CAD_08) + "km");

        return convertView;
    }

    public Filter getFilter() {
        if (listFilter == null)
            listFilter = new ListFilter();

        return listFilter;
    }

    private class ListFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String charString = constraint.toString();
            if(charString.isEmpty()){
                results.values = mList;
                results.count = mList.size();
            }else{
                ArrayList<CAD_VO> itemList = new ArrayList<>();
                for(CAD_VO item : mList){
                    if(item.CAD_04.toLowerCase().contains(constraint.toString().toLowerCase())){
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

            filteredmlist = (ArrayList<CAD_VO>)results.values;

            if(results.count>0){
                notifyDataSetChanged();
            }
            else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void updateData(ArrayList<CAD_VO> list){ mList = list;}

    static class ViewHolder{
        ImageView imgGubunIcon;

        TextView tvDay;
        TextView tvName;
        TextView tvMoney;
        TextView tvKm;

    }
}
