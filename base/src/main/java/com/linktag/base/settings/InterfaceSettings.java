package com.linktag.base.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class InterfaceSettings {
    public static InterfaceSettings instance;

    public SettingsValue Value;

    //=====================================
    // Variable
    //=====================================

    private Context mContext;

    /**
     * SharedPreferences 데이터를 수정하고 싶을 때 사용
     */
    private static SharedPreferences.Editor editor;

    /**
     * Android 내부저장소
     */
    private static SharedPreferences preferences;


    public InterfaceSettings(Context context) {
        if (context == null)
            return;

        mContext = context;
        preferences = context.getSharedPreferences("LinkTag", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        Value = new SettingsValue();
        loadSettings();
    }

    private void loadSettings() {
        Value.LoginID = getStringItem(SettingsKey.LoginID, "");
        Value.LoginPW = getStringItem(SettingsKey.LoginPW, "");
        Value.AutoLogin = getBooleanItem(SettingsKey.AutoLogin, false);
//        Value.isOnline = getBooleanItem(SettingsKey.isOnline, true);
//        Value.isNotice = getBooleanItem(SettingsKey.isNotice, true);

        String loc;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            loc = mContext.getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            loc = mContext.getResources().getSystem().getConfiguration().locale.getLanguage();
        }

        Value.myLocale = getStringItem(SettingsKey.myLocale, loc);
    }

    public static synchronized InterfaceSettings getInstance(Context context) {
        if (null == instance
                || null == preferences
                || null == editor) {
            instance = new InterfaceSettings(context);
        }

        return instance;
    }

    /************************ Method ***********************/
    /**
     * 내부 저장소에 해당 key에 value를 저장한다.
     *
     * @param key
     * @param value
     */
    public void putStringItem(String key, String value) {
        //editor가 Null인 경우가 발생하므로 null인경우는 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (editor == null)
            return;

        editor.putString(key, value);
        editor.commit();
    }

    /**
     * String 값을 넣는다.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getStringItem(String key, String defaultValue) {
        //preferences가 Null인 경우가 발생하므로 null인경우는 defaultValue를 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (preferences == null)
            return defaultValue;

        return preferences.getString(key, defaultValue);
    }

    /**
     * boolean 값을 넣는다.
     *
     * @param key
     * @param value
     */
    public void putBooleanItem(String key, boolean value) {
        //editor가 Null인 경우가 발생하므로 null인경우는 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (editor == null)
            return;

        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * boolean 값을 가져온다.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBooleanItem(String key, Boolean defaultValue) {

        //preferences가 Null인 경우가 발생하므로 null인경우는 defaultValue를 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (preferences == null)
            return defaultValue;

        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * float값을 저장한다.
     *
     * @param key
     * @param value
     */
    public void putFloatItem(String key, float value) {

        //editor가 Null인 경우가 발생하므로 null인경우는 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (editor == null)
            return;

        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * float형 값을 반환한다.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloatItem(String key, float defaultValue) {

        //preferences가 Null인 경우가 발생하므로 null인경우는 defaultValue를 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (preferences == null)
            return defaultValue;

        return preferences.getFloat(key, defaultValue);
    }

    public void putIntItem(String key, int value) {
        //editor가 Null인 경우가 발생하므로 null인경우는 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (editor == null)
            return;

        editor.putInt(key, value);
        editor.commit();
    }


    public int getIntItem(String key, int defaultValue) {

        //preferences가 Null인 경우가 발생하므로 null인경우는 defaultValue를 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (preferences == null)
            return defaultValue;

        return preferences.getInt(key, defaultValue);
    }

    public void putLongItem(String key, long value) {
        if (editor == null)
            return;

        editor.putLong(key, value);
        editor.commit();
    }


    public long getIntItem(String key, long defaultValue) {
        if (preferences == null)
            return defaultValue;

        return preferences.getLong(key, defaultValue);
    }
}
