package com.linktag.linkapp.ui.work_place_search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.linktag.linkapp.R;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;

import java.util.ArrayList;

public class FindWorkPlace extends BaseActivity {
    public static final int REQUEST_CODE = 1553;

    //===================================
    // Layout
    //===================================
    private BaseHeader header;
    private Spinner spinnerCity;
    private Spinner spinnerStreet;
    private EditText etSearch;
    private ListView listView;

    //===================================
    // Variable
    //===================================
    private FindWorkPlaceAdapter mAdapter;
    private ArrayList<WorkPlaceVO> mList;

    private static int nSearchCount;
    @SuppressLint("HandlerLeak")
    private Handler mHandlerSearch = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            nSearchCount++;
            mHandlerSearch.sendEmptyMessageDelayed(0, 100);
            if (nSearchCount > 7) {

                requestFindWorkPlace();
                mHandlerSearch.removeMessages(0);
            }
        }
    };


    //===================================
    // Initialize
    //===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_work_place);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        spinnerCity = findViewById(R.id.spinnerCity);
        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                nSearchCount = 0;
                mHandlerSearch.removeMessages(0);
                mHandlerSearch.sendEmptyMessage(0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView = findViewById(R.id.listView);

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();

        mList.add(new WorkPlaceVO("(주)푸른밤 445", "김진용", "서울특별시 서초구 강남대로525, 15층", "164-433-32"));
        mList.add(new WorkPlaceVO("푸른밤테스트", "조예라", "서울특별시 강남구 테헤란로39길 40", "025564660"));

        mAdapter = new FindWorkPlaceAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }


    /**
     * 근무지 리스트
     */
    private void requestFindWorkPlace() {
        mList = new ArrayList<>();

        mList.add(new WorkPlaceVO("(주)푸른밤 445", "김진용", "서울특별시 서초구 강남대로525, 15층", "164-433-32"));
        mList.add(new WorkPlaceVO("푸른밤테스트", "조예라", "서울특별시 강남구 테헤란로39길 40", "025564660"));

        mAdapter.updateData(mList);
        mAdapter.notifyDataSetChanged();
    }
}
