package com.linktag.base;

import android.app.Application;
import android.content.Context;

public class linkapp extends Application {
    private String mGlobalString;
    public static Context BaseContext;

    @Override
    public void onCreate() {
        super.onCreate();

        BaseContext = this;
    }

    public String getGlobalString()
    {
        return mGlobalString;
    }

    public void setGlobalString(String globalString)
    {
        this.mGlobalString = globalString;
    }
}
