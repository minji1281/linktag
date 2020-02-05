package com.linktag.linkapp.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.linkapp.R;

public class MemberFragment extends BaseFragment {

    private View view;

    public MemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_member, container, false);

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
