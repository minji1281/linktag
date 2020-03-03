package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class FRM_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String FRM_ID;       // 컨테이너
    public String FRM_01;       // 코드번호
    public String FRM_02;       // 명칭
    public String FRM_03;       // 최근 필터교체일자
    public int FRM_04;          // 주기
    public String FRM_05;       // 주기구분
    public String FRM_06;       // 메모
    public String FRM_96;       // 알림시각
    public String FRM_97;       // 작성자
    public String FRM_98;       // 최종수정자
    //public Date FRM_99;         // 최종수정일시

    public String ARM_03;       //알람여부

    public boolean Validation;

}
