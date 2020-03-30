package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class CtdVO implements Serializable {
    private static final long serialVersionUID = 6369701948166998823L;

    public String CTD_01;       // 계약번호
    public String CTD_02;       // 서비스코드(SVC_02)

    public String CTD_03;       //
    public String CTD_04;       //
    public String CTD_05;       //
    public double CTD_06;        //

    public String CTD_07;        // 해당서비스 관리자 아이디(OCM_01)
    public String CTD_08;        // 공유 이미지
    public String CTD_09;        // 유형(P:개인, S:공유)
    public String CTD_10;        // 공유명

    public String CTD_97;        //
    public String CTD_98;        //

    public String CTDS_03;        // 스캔코드

    public String CTD_02_NM;        // 서비스명(SVC_03)
    public String CTD_07_NM;        //

    public String CTM_01;       // 계약번호(CTD_01)
    public String CTM_04;       // 계약자 아이디
    public String CTM_17;       // 공유명
    public String CTM_19;       // 계약구분(P: 개인, S: 공유)

    public String CTN_02;       // 컨테이너 번호

    public String SVC_01;        // 시스템코드
    public String SVC_16;        // 아이콘 url
    public String SVC_18;        // 서비스 카테고리명
    public String SVC_90;        // 서비스 사용여부

    public String SVCL_03;       // 테이블
    public String SVCL_04;       // 서비스 LIST 페이지 경로
    public String SVCL_05;       // 서비스 DETAIL 페이지 경로


    public boolean Validation;
    public String ErrorCode;
}
