package com.linktag.linkapp.ui.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.GridView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class ChooseScan extends BaseActivity {
    //===================================
    // Layout
    //===================================
    private BaseHeader header;
    private GridView gridView;

    //===================================
    // Variable
    //===================================
    private ChooseScanAdapter mAdapter;
    private ArrayList<CtdVO> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choosescan);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> closePop(v));
        header.headerSpacer.setVisibility(View.INVISIBLE);
//        header.tvHeaderTitle.setTextColor(Color.WHITE);
//        header.btnHeaderLeft.setSelected(true);

        gridView = findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view, position, id) -> onSelect(position));
    }

    @Override
    protected void initialize() {
        if(getIntent().hasExtra("mList"))
            mList = (ArrayList<CtdVO>) getIntent().getSerializableExtra("mList");
        else
            mList = new ArrayList<>();
        mAdapter = new ChooseScanAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void onSelect(int position) {
        ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, mList.get(position));
        changeActivityCls.changeServiceWithScan(mList.get(position).CDS_03);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void closePop(View v){
        finish();
    }
}
