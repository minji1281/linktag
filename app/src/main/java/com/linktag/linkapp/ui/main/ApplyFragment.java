package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.apply_class.ApplyStateAdapter;
import com.linktag.linkapp.ui.apply_class.ApplyUpdateActivity;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyFragment extends BaseFragment{
    //===================================
    // Layout//===================================
    private BaseHeader header;
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    //===================================
    // Variable
    //===================================
    private ApplyStateAdapter mAdapter;
    private ArrayList<LED_VO> mList;

    public ApplyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_apply, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initLayout() {

    }

    protected void initialize(){

    }
}
