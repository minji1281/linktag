package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class AIR_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String AIR_ID;       // 컨테이너
    public String AIR_01;       // 코드번호
    public String AIR_02;       // 명칭
    public String AIR_03;       // 구매일자
    public String AIR_04;       // 최근 필터교체일자
    public int AIR_05;       // 주기
    public String AIR_06;       // 주기구분
    public String AIR_07;       // 메모
    public String AIR_96;       // 알림시각
    public String AIR_97;       // 작성자
    public String AIR_98;       // 최종수정자
    //public Date AIR_99;       // 최종수정일시

    public String ARM_03;       //알람여부

    public boolean Validation;

}
