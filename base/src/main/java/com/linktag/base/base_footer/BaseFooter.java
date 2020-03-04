package com.linktag.base.base_footer;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.linktag.linkapp.ui.menu.Menu;

import com.linktag.base.R;

public class BaseFooter extends LinearLayout {
    /*************************** Control ***************************/
    private LinearLayout layoutFooter;

    public LinearLayout btnFooterHome;
    public LinearLayout btnFooterCalendar;
    public LinearLayout btnFooterMenu;
    public LinearLayout btnFooterMember;
    public LinearLayout btnFooterSetting;

    public TextView tvFooterHome;
    public TextView tvFooterCalendar;
    public ImageButton btnFooterScan;
    public TextView tvFooterMenu;
    public TextView tvFooterMember;
    public TextView tvFooterSetting;

    /*************************** Variable ***************************/
    // Context
    private Context mContext;

    private OnClick mClickHome;
    private OnClick mClickCalendar;
    private OnClick mClickScan;
    private OnClick mClickMenu;
    private OnClick mClickMember;
    private OnClick mClickSetting;


    public BaseFooter(Context context){
        super(context);

        mContext = context;
    }

    public BaseFooter(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initControl();

        // 속성 설정
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BaseFooter, 0, 0);



        // 홈 버튼 visibility 설정
        btnFooterHome.setVisibility(typedArray.getInt(R.styleable.BaseFooter_btn_home_visibility, View.VISIBLE));
        // 보드 버튼 visibility 설정
        btnFooterCalendar.setVisibility(typedArray.getInt(R.styleable.BaseFooter_btn_calendar_visibility, View.VISIBLE));
        // 스캔 버튼 visibility 설정
        btnFooterScan.setVisibility(typedArray.getInt(R.styleable.BaseFooter_btn_scan_visibility, View.VISIBLE));
        // 메뉴 버튼 visibility 설정
        btnFooterMenu.setVisibility(typedArray.getInt(R.styleable.BaseFooter_btn_menu_visibility, View.VISIBLE));
        // 멤버 버튼 visibility 설정
        btnFooterMember.setVisibility(typedArray.getInt(R.styleable.BaseFooter_btn_member_visibility, View.VISIBLE));

        // 세팅 버튼 visibility 설정
        btnFooterSetting.setVisibility(typedArray.getInt(R.styleable.BaseFooter_btn_setting_visibility, View.GONE));

        /*
        int nBtnHome = typedArray.getResourceId(R.styleable.BaseFooter_btn_home_src, -1);
        if (nBtnHome != -1)
            btnFooterHome. setImageResource(nBtnHome);
        int nBtnBoard = typedArray.getResourceId(R.styleable.BaseFooter_btn_board_src, -1);
        if (nBtnBoard != -1)
            btnFooterBoard.setImageResource(nBtnBoard);
        int nBtnScan = typedArray.getResourceId(R.styleable.BaseFooter_btn_scan_src, -1);
        if (nBtnScan != -1)
            btnFooterScan.setImageResource(nBtnScan);
        int nBtnMenu = typedArray.getResourceId(R.styleable.BaseFooter_btn_menu_src, -1);
        if (nBtnMenu != -1)
            btnFooterMenu.setImageResource(nBtnMenu);
        int nBtnMember = typedArray.getResourceId(R.styleable.BaseFooter_btn_member_src, -1);
        if (nBtnMember != -1)
            btnFooterMember.setImageResource(nBtnMember);
        int nBtnSetting = typedArray.getResourceId(R.styleable.BaseFooter_btn_setting_src, -1);
        if (nBtnSetting != -1)
            btnFooterSetting.setImageResource(nBtnSetting);


         */
        initAttr();
    }

    /**
     * 레이아웃 초기화
     */
    private void initControl() {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.layout_base_footer, this, false);

        layoutFooter = (LinearLayout) v.findViewById(R.id.layoutFooter);

        btnFooterHome = (LinearLayout) v.findViewById(R.id.btnFooterHome);
        btnFooterCalendar = (LinearLayout) v.findViewById(R.id.btnFooterCalendar);
        btnFooterScan = (ImageButton) v.findViewById(R.id.btnFooterScan);
        btnFooterMenu = (LinearLayout) v.findViewById(R.id.btnFooterMenu);
        btnFooterMember = (LinearLayout) v.findViewById(R.id.btnFooterMember);
        btnFooterSetting = (LinearLayout) v.findViewById(R.id.btnFooterSetting);

        addView(v);
    }

    private void initAttr() {
        setFooterHomeButton();

        setFooterCalendarButton();

        setFooterScanButton();

        setFooterMenuButton();

        setFooterMemberButton();

        setFooterSettingButton();
    }


    private void setFooterHomeButton(){
        // 버튼 이벤트
        btnFooterHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickHome != null) {
                    mClickHome.onClick(v);
                } else {
                    try{
                        Class clsList = Class.forName("com.linktag.linkapp.ui.main.Main");
                        Intent intent = new Intent(mContext, clsList);
                        intent.putExtra("startFragment", "home");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        mContext.startActivity(intent);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFooterCalendarButton(){
        // 버튼 이벤트
        btnFooterCalendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickCalendar != null) {
                    mClickCalendar.onClick(v);
                } else {
                    try{
                        Class clsList = Class.forName("com.linktag.linkapp.ui.main.Main");
                        Intent intent = new Intent(mContext, clsList);
                        intent.putExtra("startFragment", "calendar");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        mContext.startActivity(intent);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFooterScanButton(){
        // 버튼 이벤트
        btnFooterScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickScan != null) {
                    mClickScan.onClick(v);
                } else {
                    //Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFooterMenuButton(){
        // 버튼 이벤트
        btnFooterMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickMenu != null) {
                    mClickMenu.onClick(v);
                } else {
                    try{
                        Class clsList = Class.forName("com.linktag.linkapp.ui.menu.Menu");
                        Intent intent = new Intent(mContext, clsList);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mContext.startActivity(intent);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFooterMemberButton(){
        // 버튼 이벤트
        btnFooterMember.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickMember != null) {
                    mClickMember.onClick(v);
                } else {

                    //Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFooterSettingButton(){
        // 버튼 이벤트
        btnFooterSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickSetting != null) {
                    mClickSetting.onClick(v);
                } else {
                    try{
                        Class clsList = Class.forName("com.linktag.linkapp.ui.settings_main.SettingMain");
                        Intent intent = new Intent(mContext, clsList);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mContext.startActivity(intent);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // onClick interface
    public interface OnClick {
        void onClick(View view);
    }

    public void SetCallbackBtnHome(View view) {
        if (mClickHome != null) {
            mClickHome.onClick(view);
        }
    }
    public void SetCallbackBtnCalendar(View view) {
        if (mClickCalendar!= null) {
            mClickCalendar.onClick(view);
        }
    }
    public void SetCallbackBtnScan(View view) {
        if (mClickScan != null) {
            mClickScan.onClick(view);
        }
    }
    public void SetCallbackBtnMenu(View view) {
        if (mClickMenu!= null) {
            mClickMenu.onClick(view);
        }
    }
    public void SetCallbackBtnMember(View view) {
        if (mClickMember!= null) {
            mClickMember.onClick(view);
        }
    }
    public void SetCallbackBtnSetting(View view) {
        if (mClickSetting!= null) {
            mClickSetting.onClick(view);
        }
    }

    public void SetOnClickHome(OnClick callback) {
        mClickHome = callback;
    }
    public void SetOnClickCalendar(OnClick callback) {
        mClickCalendar = callback;
    }
    public void SetOnClickScan(OnClick callback) {
        mClickScan = callback;
    }
    public void SetOnClickMenu(OnClick callback) {
        mClickMenu = callback;
    }
    public void SetOnClickMember(OnClick callback) {
        mClickMember = callback;
    }
    public void SetOnClickSetting(OnClick callback) {
        mClickSetting = callback;
    }

}
