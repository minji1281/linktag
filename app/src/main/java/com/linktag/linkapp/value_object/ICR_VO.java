package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class ICR_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String ICR_ID;       // 컨테이너
    public String ICR_01;       // Master일련번호(ICM_01)
    public String ICR_02;       // 일련번호(자동생성)
    public String ICR_03;       // 일자(YYYYMMDD)
    public String ICR_04;       // 시작시간(HHMM)
    public String ICR_05;       // 종료시간(HHMM)
    public String ICR_06;       // 항목(ICI_01)
    public String ICR_07;       // 상세
    public String ICR_08;       // 메모
    public String ICR_97;       // 작성자
    public String ICR_98;       // 최종수정자
    //public Date ICR_99;       // 최종수정일시

    public boolean Validation;
    public int MM_CNT;
}
