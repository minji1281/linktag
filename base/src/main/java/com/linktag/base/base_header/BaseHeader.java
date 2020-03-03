package com.linktag.base.base_header;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.R;
import com.linktag.base.base_activity.BaseActivity;

public class BaseHeader extends LinearLayout {

    /*************** 정리 예정 ***************/
    private final String TEXT_COLOR = "textColor";
    private final String TEXT_SIZE = "textSize";
    private final String LEFT_BTN_SRC = "btn_left_src";

    private String m_strTextColor = "";
    private String m_strTextSize = "";
    private String m_strLeftBtnSrc = "";

    /*************************** Control ***************************/
    // 헤더 전체 레이아웃
    public LinearLayout layoutHeader;
    // 텍스트 타이틀(기본)
    public TextView tvHeaderTitle;
    public TextView tvHeaderTitle2;
    // 왼쪽 버튼 (Back)
    public ImageButton btnHeaderLeft;
    // 오른쪽1 버튼
    public ImageButton btnHeaderRight1;
    // 오른쪽2 버튼
    public ImageButton btnHeaderRight2;
    // 오른쪽3 버튼
    //public ImageButton btnHeaderRight3;
    // 텍스트 버튼
    public Button btnHeaderText;
    // 왼쪽 텍스트 버튼
    public Button btnHeaderLeftText;
    // 오른쪽 스피너
    public Spinner spHeaderRight;

    public TextView headerSpacer;

    /*************************** Variable ***************************/
    // Context
    private Context mContext;
    // Left 버튼 클릭 이벤트
    private OnClick mClickLeft;
    // Right1 버튼 클릭 이벤트
    private OnClick mClickRight1;
    // Right2 버튼 클릭 이벤트
    private OnClick mClickRight2;
    // Right3 버튼 클릭 이벤트
    private OnClick mClickRight3;
    // Text 버튼 클릭 이벤트
    private OnClick mClickText;
    // Left Text 버튼 클릭 이벤트
    private OnClick mClickLeftText;

    /*************************** initialize ***************************/

    // 기본생성자
    public BaseHeader(Context context) {
        super(context);

        mContext = context;
    }

    // 속성을 읽는 생성자
    public BaseHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initControl();

        // 속성 설정
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BaseHeader, 0, 0);

        // 타이틀 text 설정
        tvHeaderTitle.setText(typedArray.getString(R.styleable.BaseHeader_title_text));
        tvHeaderTitle.setVisibility(typedArray.getInt(R.styleable.BaseHeader_title_text_visibility, View.VISIBLE));

        // 타이틀 text2 설정
        tvHeaderTitle2.setText(typedArray.getString(R.styleable.BaseHeader_title_text2));
        tvHeaderTitle2.setVisibility(typedArray.getInt(R.styleable.BaseHeader_title_text2_visibility, View.GONE));

        // 왼쪽 버튼 visibility 설정
        btnHeaderLeft.setVisibility(typedArray.getInt(R.styleable.BaseHeader_btn_left_visibility, View.VISIBLE));

        // 오른쪽1 버튼 visibility 설정
        btnHeaderRight1.setVisibility(typedArray.getInt(R.styleable.BaseHeader_btn_right1_visibility, View.GONE));

        // 오른쪽2 버튼 visibility 설정
        btnHeaderRight2.setVisibility(typedArray.getInt(R.styleable.BaseHeader_btn_right2_visibility, View.GONE));

        // 오른쪽3 버튼 visibility 설정
        //btnHeaderRight3.setVisibility(typedArray.getInt(R.styleable.BaseHeader_btn_right3_visibility, View.GONE));

        // 오른쪽 텍스트 버튼
        btnHeaderText.setVisibility(typedArray.getInt(R.styleable.BaseHeader_btn_text_visibility, View.GONE));

        // 오른쪽 스피너 visibility 설정
        spHeaderRight.setVisibility(typedArray.getInt(R.styleable.BaseHeader_sp_visibility, View.GONE));

        headerSpacer.setVisibility(typedArray.getInt(R.styleable.BaseHeader_spacer_visibility , View.VISIBLE));

        // 텍스트 버튼 text 설정
        String strText = typedArray.getString(R.styleable.BaseHeader_btn_text_text);
        if (strText != null && !strText.isEmpty())
            btnHeaderText.setText(strText);

        // 왼쪽 텍스트 버튼
        btnHeaderLeftText.setVisibility(typedArray.getInt(R.styleable.BaseHeader_btn_left_text_visibility, View.GONE));

        // 왼쪽텍스트 버튼 text 설정
        String strLeftText = typedArray.getString(R.styleable.BaseHeader_btn_left_text_text);
        if (strLeftText != null && !strLeftText.isEmpty())
            btnHeaderLeftText.setText(strLeftText);

        // 이미지 설정
        int nBtnLeftSrc = typedArray.getResourceId(R.styleable.BaseHeader_btn_left_src, -1);
        if (nBtnLeftSrc != -1)
            btnHeaderLeft.setImageResource(nBtnLeftSrc);

        // 이미지 설정
        int nBtnRight1Src = typedArray.getResourceId(R.styleable.BaseHeader_btn_right1_src, -1);
        if (nBtnRight1Src != -1)
            btnHeaderRight1.setImageResource(nBtnRight1Src);

        // 이미지 설정
        int nBtnRight2Src = typedArray.getResourceId(R.styleable.BaseHeader_btn_right2_src, -1);
        if (nBtnRight2Src != -1)
            btnHeaderRight2.setImageResource(nBtnRight2Src);
/*
        // 이미지 설정
        int nBtnRight3Src = typedArray.getResourceId(R.styleable.BaseHeader_btn_right3_src, -1);
        if (nBtnRight3Src != -1)
            btnHeaderRight3.setImageResource(nBtnRight3Src);


 */
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attributeName = attrs.getAttributeName(i);

            if (attributeName == null)
                continue;

            if (attributeName.equals(TEXT_COLOR)) {
                m_strTextColor = attrs.getAttributeValue(i);
            } else if (attributeName.equals(TEXT_SIZE)) {
                m_strTextSize = attrs.getAttributeValue(i);
            } else if (attributeName.equals(LEFT_BTN_SRC)) {
                m_strLeftBtnSrc = attrs.getAttributeValue(i);
            }
        }

        initAttr();
    }

    /**
     * 레이아웃 초기화
     */
    private void initControl() {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.layout_base_header, this, false);

        layoutHeader = (LinearLayout) v.findViewById(R.id.layoutHeader);
        tvHeaderTitle = (TextView) v.findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle2 = (TextView) v.findViewById(R.id.tvHeaderTitle2);

        btnHeaderLeft = (ImageButton) v.findViewById(R.id.btnHeaderLeft);
        btnHeaderRight1 = (ImageButton) v.findViewById(R.id.btnHeaderRight1);
        btnHeaderRight2 = (ImageButton) v.findViewById(R.id.btnHeaderRight2);
        //btnHeaderRight3 = (ImageButton) v.findViewById(R.id.btnHeaderRight3);

        btnHeaderText = (Button) v.findViewById(R.id.btnHeaderText);
        btnHeaderLeftText = (Button) v.findViewById(R.id.btnHeaderLeftText);

        spHeaderRight = (Spinner) v.findViewById(R.id.spHeaderRight);

        headerSpacer = (TextView) v.findViewById(R.id.headerSpacer);

        addView(v);
    }

    /**
     * 속성 초기화
     */
    private void initAttr() {
        setHeaderTitleTextView();

        setHeaderLeftButton();

        setHeaderRightButton1();

        setHeaderRightButton2();

        //setHeaderRightButton3();

        setHeaderText();

        setHeaderLeftText();
    }

    private void setHeaderRightButton2() {
        // 버튼 이벤트
        btnHeaderRight2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickRight2 != null) {
                    mClickRight2.onClick(v);
                } else {
                    Toast.makeText(mContext, "Click Right2 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setHeaderColor(int color) {
        layoutHeader.setBackgroundColor(color);
    }

    private void setHeaderText() {
        // 버튼 이벤트
        btnHeaderText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickText != null) {
                    mClickText.onClick(v);
                } else {
                    Toast.makeText(mContext, "Click Text Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setHeaderLeftText() {
        // 버튼 이벤트
        btnHeaderLeftText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickLeftText != null) {
                    mClickLeftText.onClick(v);
                } else {
                    Toast.makeText(mContext, "Click LeftText Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setHeaderRightButton1() {
        // 버튼 이벤트
        btnHeaderRight1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickRight1 != null) {
                    mClickRight1.onClick(v);
                } else {
                    Toast.makeText(mContext, "Click Right1 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
/*
    private void setHeaderRightButton3() {
        // 버튼 이벤트
        btnHeaderRight3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickRight3 != null) {
                    mClickRight3.onClick(v);
                } else {
                    Toast.makeText(mContext, "Click Right3 Button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
*/
    private void setHeaderLeftButton() {
        // 이미지 변경
        if (m_strLeftBtnSrc.split("/").length == 2 && m_strLeftBtnSrc.substring(0, 1).equals("@")) {
            String strResourceType = m_strLeftBtnSrc.split("/")[0];
            strResourceType = strResourceType.substring(1, strResourceType.length());

            int resId = getResources().getIdentifier(m_strLeftBtnSrc.split("/")[1], strResourceType,
                    getAndroidManifestPackageName());
            btnHeaderLeft.setImageResource(resId);
        }

        // 버튼 이벤트
        btnHeaderLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickLeft != null) {
                    mClickLeft.onClick(v);
                } else {
                    Toast.makeText(mContext, "Click Left Button", Toast.LENGTH_SHORT).show();  // #009

                }
            }
        });
    }


    private void setHeaderTitleTextView() {
        // 텍스트컬러
        if (m_strTextColor.split("/").length == 2 && m_strTextColor.substring(0, 1).equals("@"))
            tvHeaderTitle.setTextColor(Color.parseColor(getAttrString(m_strTextColor)));
        else if (!m_strTextColor.isEmpty() && m_strTextColor.substring(0, 1).equals("#"))
            tvHeaderTitle.setTextColor(Color.parseColor(m_strTextColor));

        // 텍스트크기
        if (m_strTextSize.split("/").length == 2 && m_strTextSize.substring(0, 1).equals("@"))
            tvHeaderTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAttrFloat(m_strTextSize));
        else if (!m_strTextSize.isEmpty())
            tvHeaderTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(m_strTextSize));
    }

    // 패키지네임
    public String getAndroidManifestPackageName() {
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getContext().getPackageName(), 0);

            return packageInfo.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;// 기본 값
    }

    // XML에서받아오는리소스아이디변환_String
    public String getAttrString(String attr) {
        try {
            String rType = attr.split("/")[0];// @color,@string,@dimens가들어갈수있음
            rType = rType.substring(1, rType.length());// color,string,dimens
            int rId = getResources().getIdentifier(attr.split("/")[1], rType, getAndroidManifestPackageName());
            String valueString = getResources().getString(rId);
            return valueString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // XML에서받아오는리소스아이디변환_float
    public float getAttrFloat(String attr) {
        try {
            String rType = attr.split("/")[0];// @color,@string,@dimens가들어갈수있음
            rType = rType.substring(1, rType.length());// color,string,dimens
            int rId = getResources().getIdentifier(attr.split("/")[1], rType, getAndroidManifestPackageName());

            String valueString = getResources().getString(rId);
            return Float.parseFloat(valueString.replaceAll("dip", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // onClick interface
    public interface OnClick {
        void onClick(View view);
    }

    public void SetCallbackBtnLeft(View view) {
        if (mClickLeft != null) {
            mClickLeft.onClick(view);
        }
    }

    public void SetCallbackBtnRight1(View view) {
        if (mClickRight1 != null) {
            mClickRight1.onClick(view);
        }
    }

    public void SetCallbackBtnRight2(View view) {
        if (mClickRight2 != null) {
            mClickRight2.onClick(view);
        }
    }

    public void SetCallbackBtnRight3(View view) {
        if (mClickRight3 != null) {
            mClickRight3.onClick(view);
        }
    }

    public void SetCallbackBtnText(View view) {
        if (mClickText != null) {
            mClickText.onClick(view);
        }
    }

    public void SetCallbackBtnLeftText(View view) {
        if (mClickLeftText != null) {
            mClickLeftText.onClick(view);
        }
    }

    public void SetOnClickLeft(OnClick callback) {
        mClickLeft = callback;
    }

    public void SetOnClickRight1(OnClick callback) {
        mClickRight1 = callback;
    }

    public void SetOnClickRight2(OnClick callback) {
        mClickRight2 = callback;
    }

    public void SetOnClickRight3(OnClick callback) {
        mClickRight3 = callback;
    }

    public void SetOnClickText(OnClick callback) {
        mClickText = callback;
    }

    public void SetOnClickLeftText(OnClick callback) {
        mClickLeftText = callback;
    }


}
