package com.ddit.project.supermarketcheckouter;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefStorageManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;
    public static String GLOB_PREF_NAME = "SuperMarket_checkout_Pref";

    public PrefStorageManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(GLOB_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setStringprefrence(String variablename, String value) {
        editor.putString(variablename, value);
        editor.commit();
    }

    public String getStringvalue(String variablename) {
        return pref.getString(variablename, "");
    }


    public String getStringvaluedef(String variablename,String def) {
        return pref.getString(variablename, def);
    }

    public void setIntprefrence(String variablename, int value) {
        editor.putInt(variablename, value);
        editor.commit();
    }

    public int getIntvalue(String variablename) {
        return pref.getInt(variablename, 0);
    }

    public void setBoolprefrence(String variablename, Boolean value) {
        editor.putBoolean(variablename, value);
        editor.commit();
    }

    public Boolean geBoolvalue(String variablename) {
        return pref.getBoolean(variablename, false);
    }


    public Boolean geBoolvaluedeftrue(String variablename) {
        return pref.getBoolean(variablename, true);
    }


    public void Logoutpref() {
        editor.clear();
        editor.commit();
    }


}
