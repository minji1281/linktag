package com.linktag.base.user_interface;

public class InterfaceUser {
    public static InterfaceUser instance;

    public UserValue Value;


    public InterfaceUser() {
        Value = new UserValue();
    }

    public static synchronized InterfaceUser getInstance() {
        if (null == instance) {
            instance = new InterfaceUser();
        }

        return instance;
    }
}
