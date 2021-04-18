package com.pvs.snreader;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class MyConstants {


    static final String setRate = "setRate";
    static final String countRate = "countRate";

    static boolean onlySerialNumber=true;
    static boolean autoSave=true;
    static boolean flag = true;
    static Bitmap rotatedBitmap;
    static String titleText="";

    static List<String> customCases = new ArrayList<String>();
    static List<Integer> sizeCases = new ArrayList<Integer>();
    static List<Boolean> setCases = new ArrayList<Boolean>();

    static final String DATA_SET = "myData";
    static final String nameCustomCases = "myCases";
    static final String siseCustomCases = "sizeCases";
    static final String setCustomCases = "setCases";




    static boolean valueshowDefRules=true;
    static final String nameShowDefRules="showDefRules";

    static final String nameSwitchNumber="switchNumber";
    static boolean valueSwitchNumber=false;
    static final String keyAnySym="keyAnySym";

    static int intAnySym=12;


    static final String nameSwitchSN="switchSN";
    static boolean valueSwitchSN=true;

    static final String nameSwitchSNN="switchSNN";
    static boolean valueSwitchSNN=true;

    static final String nameswitchSerialN="switchSerialN";
    static boolean valueswitchSerialN=true;

    static final String nameswitchSERNO="switchSERNO";
    static boolean valueswitchSERNO=true;

    static final String nameswitchSIN="switchSIN";
    static boolean valueswitchSIN=true;

    static final String nameswitchSNID="switchSNID";
    static boolean valueswitchSNID=true;

    static final String nameswitchFOR="switchFOR";
    static boolean valueswitchFOR=true;

    static final String nameswitchFRU="switchFRU";
    static boolean valueswitchFRU=true;

    static final String nameswitchauto_focus="auto_focus";
    static final String nameswitchcheck_SN="check_SN";
    static final String nameswitchautoSave="autoSave";
    static final String nameswitchuse_flash="use_flash";

    static boolean ifAutoFocus=true;
    static boolean ifFlash=true;

    static void getSwitches(SharedPreferences sF) {

        valueSwitchNumber=sF.getBoolean(nameSwitchNumber,false);
        valueSwitchSN=sF.getBoolean(nameSwitchSN,true);
        valueswitchSerialN=sF.getBoolean(nameswitchSerialN,true);
        valueswitchSERNO=sF.getBoolean(nameswitchSERNO,true);
        valueswitchSIN=sF.getBoolean(nameswitchSIN,true);
        valueswitchSNID=sF.getBoolean(nameswitchSNID,true);
        valueswitchFOR=sF.getBoolean(nameswitchFOR,true);
        valueswitchFRU=sF.getBoolean(nameswitchFRU,true);
        valueshowDefRules = sF.getBoolean(nameShowDefRules,true);
    }




}
