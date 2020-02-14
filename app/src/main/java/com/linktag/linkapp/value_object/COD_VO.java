package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class COD_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String COD_ID;       // 컨테이너
    public String COD_01;       // 일련번호
    public String COD_02;       // 명칭
    public String COD_03;       // 브랜드명
    public double COD_04;       // 가격
    public String COD_05;       // 개봉일자
    public String COD_06;       // 유통기한일자
    public String COD_07;       // 사용종료일자
    public String COD_08;       // 메모
    public String COD_95;       // 화장대코드
    public String COD_96;       // 알림시각
    public String COD_97;       // 작성자
    public String COD_98;       // 최종수정자
    //public Date COD_99;       // 최종수정일시

    public String ARM_03;       //알람여부
    public String COS_02;       //화장대

    public boolean Validation;

}
