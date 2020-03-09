package com.linktag.linkapp.ui.car;

public class CarSpinnerList {
    private String code;
    private String carnum;
    private String caryear;
    private String memo;

    /**
     *
     * @param code (코드)
     * @param carnum (차량번호)
     * @param caryear (연식)
     * @param memo (메모)
     *
     */

    public CarSpinnerList(String code, String carnum, String caryear, String memo){
        this.code = code;
        this.carnum = carnum;
        this.caryear = caryear;
        this.memo = memo;
    }

    public String getCode(){
        return code;
    }
    public String getCarNum(){
        return carnum;
    }
    public String getCarYear(){
        return caryear;
    }
    public String getMemo() {
        return memo;
    }
}