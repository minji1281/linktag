package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class RMM_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String RMM_ID;       // 컨테이너
    public String RMM_01;       // 일련번호(자동생성)
    public String RMM_02;       // 시간간격구분(1:30분 2:1시간)
    public String RMM_03;       // 예약시작시간(HHMM)
    public String RMM_04;       // 예약종료시간(HHMM)
    public String RMM_05;       // 공지
    public String RMM_98;       // 최종수정자
    //public Date RMM_99;       // 최종수정일시

}
