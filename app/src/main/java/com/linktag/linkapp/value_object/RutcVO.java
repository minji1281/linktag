package com.linktag.linkapp.value_object;

import java.io.Serializable;
import java.util.Date;

public class RutcVO implements Serializable {
    private static final long serialVersionUID = -1784847693878661308L;

    public String RUTC_ID;       // 회사코드
    public String RUTC_01;       // 일련번호
    public String RUTC_02;       // 매장명
    public String RUTC_03;       // 점주명
    public String RUTC_04;       // 사업자번호
    public String RUTC_05;       // 매장전화번호
    public String RUTC_06;       // 우편번호
    public String RUTC_07;       // 주소1
    public String RUTC_08;       // 주소2
    public String RUTC_09;       // 오픈일
    public String RUTC_98;

    public boolean Validation;
    public String ErrorCode;
}
