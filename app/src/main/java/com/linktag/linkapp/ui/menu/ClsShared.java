package com.linktag.linkapp.ui.menu;

public class ClsShared {
    private String name;
    private String contract;
    private String service;

    /**
     *
     * @param name      공유명(서비스 + 디렉터리명)
     * @param contract  계약번호 (CTM_01 = CTD_01)
     * @param service   서비스코드 (CTD_02 = SVC_02)
     */
    public ClsShared(String name, String contract, String service){
        this.name = name;
        this.contract = contract;
        this.service = service;
    }

    public String getName(){ return name; }
    public String getContract(){ return contract; }
    public String getService(){ return service; }
}
