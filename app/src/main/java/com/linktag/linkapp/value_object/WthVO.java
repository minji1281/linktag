package com.linktag.linkapp.value_object;

import java.io.Serializable;
import java.util.Date;

public class WthVO implements Serializable {
    private static final long serialVersionUID = 3034498942556282281L;

    public String WTH_ID;       // 컨테이너
    public String WTH_01;       // 매장코드
    public String WTH_02;       // 일자 yyyyMMdd
    public String WTH_03;       // 시간 HH
    public String WTH_04;       // 날씨 1:맑음, 2:흐림, 3:비/눈
    public float WTH_05;        // 기온
    public float WTH_06;        // 바람
    public float WTH_07;        // 습도
    public float WTH_08;        // 강수
    public float WTH_09;        // 미세먼지

    public boolean Validation;
    public String ErrorCode;
}
