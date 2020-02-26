package com.linktag.linkapp.value_object;

import java.io.Serializable;
import java.util.Date;

public class OcmVO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String CTM_01;       // 코드
    public String RUTC_01;       // 매장 코드

    public String OCM_01;       // 코드
    public String OCM_02;       // 이름
    public String OCM_03;       // 비밀번호
    public String OCM_03_CHK;    // 체크
    public String OCM_04;

    public String OCM_21;       // 이메일
    public String OCM_22;
    public String OCM_23;
    public String OCM_24;

    public String OCM_25;       // 시작 회사 코드
    public String OCM_28;       // 입사일자
    public String OCM_29;       // 퇴사일자
    public String OCM_31;       // 부서
    public String OCM_32;       // 직급
    public String OCM_51;       // 전화번호
    public String OCM_52;
    public String OCM_53;       // 전화번호
    public String OCM_54;       // 전화번호

    public String OCP_01;

    public boolean Validation;
    public String ErrorCode;
}
