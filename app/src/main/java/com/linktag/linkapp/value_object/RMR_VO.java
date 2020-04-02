package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class RMR_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String RMR_ID;       // 컨테이너
    public String RMR_01;       // Master일련번호(RMM_01)
    public String RMR_02;       // 연습실일련번호(RMD_02)
    public String RMR_03;       // 일자(YYYYMMDD)
    public String RMR_04;       // 시간(HHMM)
    public String RMR_05;       // 사용자(OCM_01)
    public String RMR_98;       // 최종수정자
    //public Date RMR_99;       // 최종수정일시

    public boolean Validation;
    public String RMR_05_NM;       // 사용자명
    public boolean boolChange = false;       // 변경여부(RmrRecycleAdapter에서 사용)
}
