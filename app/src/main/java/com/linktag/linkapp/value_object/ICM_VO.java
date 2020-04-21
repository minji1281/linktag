package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class ICM_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String ICM_ID;       // 컨테이너
    public String ICM_01;       // 일련번호(CTD_01)
    public String ICM_02;       // 이름
    public String ICM_03;       // 생년월일(YYYYMMDD)
    public String ICM_98;       // 최종수정자
    //public Date ICM_99;       // 최종수정일시

    public boolean Validation;
    public int MM_CNT;
    public int DD_CNT;
    public int ALL_DD_CNT;
    public int MINUTE1;
    public int MINUTE2;
    public int MINUTE3;
}
