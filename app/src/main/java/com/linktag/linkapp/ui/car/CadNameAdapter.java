package com.linktag.linkapp.ui.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.linktag.linkapp.R;

import java.util.ArrayList;
import java.util.List;

public class CadNameAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;
    private Filter listFilter;

    private List<String> filteredItemList;

    public CadNameAdapter(Context context, List<String> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.filteredItemList = list;
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_find_cad_name, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvCadName = (TextView) convertView.findViewById(R.id.tvCadName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Text
        viewHolder.tvCadName.setText(filteredItemList.get(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = mList;
                results.count = mList.size();
            } else {
                List<String> itemList = new ArrayList<String>();

                for (String item : mList) {
                    if (item.toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
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
            filteredItemList = (List<String>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void updateData(List<String> list){ filteredItemList = list;}

    static class ViewHolder{
        TextView tvCadName;
    }
}
