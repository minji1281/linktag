package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class RtscVO implements Serializable {
    private static final long serialVersionUID = -1784847693878661308L;

    public String RTSC_03;       // 회사코드
    public String RTSC_04;       // 일련번호
    public String RTSC_05;       // 매장명
    public String RTSC_06;       // 점주명
    public float RTSC_07;       // 사업자번호
    public float RTSC_08;       // 매장전화번호
    public int BHM_CNT;       // 우편번호

    public boolean Validation;
    public String ErrorCode;
}
