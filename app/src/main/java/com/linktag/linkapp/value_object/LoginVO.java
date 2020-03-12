package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class LoginVO implements Serializable {
    private static final long serialVersionUID = 3355003498579211313L;

    public String CTM_01;       // 계약코드
    public String CTM_17;       // 계약 디렉토리 명
    public String CTM_19;       // 계약구분(P : 개인, S : 공유)

    public String OCP_ID;       // 회사코드 = CTM_03
    public String OCP_01;       // 회사명

    public String OCM_01;       // 코드
    public String OCM_02;       // 이름
    public String OCM_03;       // 비밀번호
    public String OCM_03_CHK;    // 체크

    public String OCM_21;       // 이메일
    public String OCM_24;       // 알림 설정 여부
    public String OCM_28;       // 입사일자
    public String OCM_29;       // 퇴사일자
    public String OCM_31;       // 부서
    public String OCM_32;       // 직급
    public String OCM_51;       // 전화번호
    public String OCM_52;       // 프로필 이미지

    // 안쓸듯
    public String CTU_08;       // 시작 매장
    public String CTU_09;       // 시작 매장 알림 일자
    public String OCM_04;
    public String OCM_25;       // 시작 회사 코드
    public String OCP_2102;       // 회사명


    public boolean Validation;
    public String ErrorCode;
}
