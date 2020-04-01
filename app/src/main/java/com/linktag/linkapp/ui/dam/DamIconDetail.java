package com.linktag.linkapp.ui.dam;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;

public class DamIconDetail extends BaseActivity {

    private BaseHeader header;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private DamIconRecycleAdapter mAdapter;

    private LinearLayoutManager linearLayoutManager2;
    private RecyclerView recyclerView2;
    private DamUserIconRecycleAdapter mAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_dam_icon);

        initLayout();
        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initLayout() {

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);

        linearLayoutManager = new GridLayoutManager(mContext, 4);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new DamIconRecycleAdapter(mContext, getIntent().getIntExtra("index",0));
        recyclerView.setAdapter(mAdapter);


        recyclerView2 = findViewById(R.id.recyclerView2);

        linearLayoutManager2 = new GridLayoutManager(mContext, 4);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        mAdapter2 = new DamUserIconRecycleAdapter(mContext, getIntent().getIntExtra("index",0));
        recyclerView2.setAdapter(mAdapter2);

    }

    @Override
    protected void initialize() {

    }
}
