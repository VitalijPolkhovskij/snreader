package com.pvs.snreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.pvs.snreader.MyConstants.DATA_SET;
import static com.pvs.snreader.MyConstants.customCases;
import static com.pvs.snreader.MyConstants.getSwitches;
import static com.pvs.snreader.MyConstants.intAnySym;
import static com.pvs.snreader.MyConstants.keyAnySym;
import static com.pvs.snreader.MyConstants.nameCustomCases;
import static com.pvs.snreader.MyConstants.nameShowDefRules;
import static com.pvs.snreader.MyConstants.nameSwitchNumber;
import static com.pvs.snreader.MyConstants.nameSwitchSN;
import static com.pvs.snreader.MyConstants.nameSwitchSNN;
import static com.pvs.snreader.MyConstants.nameswitchFOR;
import static com.pvs.snreader.MyConstants.nameswitchFRU;
import static com.pvs.snreader.MyConstants.nameswitchSERNO;
import static com.pvs.snreader.MyConstants.nameswitchSIN;
import static com.pvs.snreader.MyConstants.nameswitchSNID;
import static com.pvs.snreader.MyConstants.nameswitchSerialN;
import static com.pvs.snreader.MyConstants.setCases;
import static com.pvs.snreader.MyConstants.setCustomCases;
import static com.pvs.snreader.MyConstants.siseCustomCases;
import static com.pvs.snreader.MyConstants.sizeCases;
import static com.pvs.snreader.MyConstants.valueSwitchNumber;
import static com.pvs.snreader.MyConstants.valueSwitchSN;
import static com.pvs.snreader.MyConstants.valueSwitchSNN;
import static com.pvs.snreader.MyConstants.valueshowDefRules;
import static com.pvs.snreader.MyConstants.valueswitchFOR;
import static com.pvs.snreader.MyConstants.valueswitchFRU;
import static com.pvs.snreader.MyConstants.valueswitchSERNO;
import static com.pvs.snreader.MyConstants.valueswitchSIN;
import static com.pvs.snreader.MyConstants.valueswitchSNID;
import static com.pvs.snreader.MyConstants.valueswitchSerialN;

public class Rules extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private SharedPreferences.Editor data_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar aBar = getSupportActionBar();
            assert aBar != null;
            aBar.setDisplayShowHomeEnabled(true);
            aBar.setDisplayHomeAsUpEnabled(true);
        }


        SharedPreferences sF = getSharedPreferences(DATA_SET, Context.MODE_PRIVATE);
        data_editor = sF.edit();
        data_editor.apply();
        getSwitches(sF);

        androidx.appcompat.widget.SwitchCompat
                vSwitch=findViewById(R.id.switchNumber);
        vSwitch.setChecked(valueSwitchNumber);
        vSwitch.setOnCheckedChangeListener(this);

        vSwitch=findViewById(R.id.switchSN);
        vSwitch.setChecked(valueSwitchSN);
        vSwitch.setOnCheckedChangeListener(this);

        vSwitch=findViewById(R.id.switchSNN);
        vSwitch.setChecked(valueSwitchSNN);
        vSwitch.setOnCheckedChangeListener(this);


        vSwitch=findViewById(R.id.showDefRules);
        vSwitch.setChecked(valueshowDefRules);
        vSwitch.setOnCheckedChangeListener(this);


        showDefRuls();


        vSwitch=findViewById(R.id.switchSerialN);
        vSwitch.setChecked(valueswitchSerialN);
        vSwitch.setOnCheckedChangeListener(this);


        vSwitch=findViewById(R.id.switchSERNO);
        vSwitch.setChecked(valueswitchSERNO);
        vSwitch.setOnCheckedChangeListener(this);

        vSwitch=findViewById(R.id.switchSIN);
        vSwitch.setChecked(valueswitchSIN);
        vSwitch.setOnCheckedChangeListener(this);

        vSwitch=findViewById(R.id.switchSNID);
        vSwitch.setChecked(valueswitchSNID);
        vSwitch.setOnCheckedChangeListener(this);

        vSwitch=findViewById(R.id.switchFOR);
        vSwitch.setChecked(valueswitchFOR);
        vSwitch.setOnCheckedChangeListener(this);

        vSwitch=findViewById(R.id.switchFRU);
        vSwitch.setChecked(valueswitchFRU);
        vSwitch.setOnCheckedChangeListener(this);
        loadCustomSwitches();
    }


    @Override
    public void onBackPressed() {
    EditText vE=findViewById(R.id.inputSize);
    if (vE != null) {
        int val = GetInt(vE.getText().toString());
        if (val != 0) {
            intAnySym=val;
            SharedPreferences sFName = getSharedPreferences(nameCustomCases, Context.MODE_PRIVATE);
            SharedPreferences.Editor edName = sFName.edit();
            edName.putInt(keyAnySym,val);
            edName.apply();
            edName.commit();
        }
    }


    super.onBackPressed();
    }


    private void showDefRuls() {
        LinearLayout layStandartSwitch = findViewById(R.id.layStandartSwitch);
        if (valueshowDefRules) {
            layStandartSwitch.setVisibility(View.VISIBLE);
        }
        else  {
            layStandartSwitch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String nameSwitch=null;
        String id = buttonView.getTag().toString();
        switch (id) {
            case "showDefRules":
                valueshowDefRules=isChecked;
                nameSwitch=nameShowDefRules;
                showDefRuls();
                break;
            case "switchNumber":
                valueSwitchNumber=isChecked;
                nameSwitch=nameSwitchNumber;
                break;
            case "switchSN":
                valueSwitchSN=isChecked;
                nameSwitch=nameSwitchSN;
                break;
            case "switchSNN":
                valueSwitchSNN=isChecked;
                nameSwitch=nameSwitchSNN;
                break;
            case "switchSerialN":
                valueswitchSerialN=isChecked;
                nameSwitch=nameswitchSerialN;
                break;
            case "switchSERNO":
                valueswitchSERNO=isChecked;
                nameSwitch=nameswitchSERNO;
                break;
            case "switchSIN":
                valueswitchSIN=isChecked;
                nameSwitch=nameswitchSIN;
                break;

            case "switchSNID":
                valueswitchSNID=isChecked;
                nameSwitch=nameswitchSNID;
                break;

            case "switchFOR":
                valueswitchFOR=isChecked;
                nameSwitch=nameswitchFOR;
                break;

            case "switchFRU":
                valueswitchFRU=isChecked;
                nameSwitch=nameswitchFRU;
                break;

            default:
                break;
        }
        if (nameSwitch != null) {
            data_editor.putBoolean(nameSwitch,isChecked);
            data_editor.apply();
        }
    }


    private void showMes(String mess) {
        LayoutInflater li = LayoutInflater.from(Rules.this);
        View promptsView = li.inflate(R.layout.prompt_mes, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.messStyle);
        builder.setView(promptsView);

        TextView tTitle = promptsView.findViewById(R.id.textMess);
        tTitle.setText(mess);

        builder.setCancelable(false);

        final AlertDialog alert  = builder.create();


        ImageButton okButton = promptsView.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> {
            alert.cancel();
        });
        alert.show();
    }




    public void infoSIN(View view) {
       showMes(getString(R.string.isnos));
    }
    public void infoSerialN(View view) {
        showMes(getString(R.string.idnums));
    }

    public void infoFRU(View view) {
        showMes(getString(R.string.svlastsym));
    }

    private int GetInt(String s_P) {
        if (s_P == null)
            return 0;
        if (s_P.length() == 0)
            return 0;
        int res = 0;
        try {
            res = Integer.parseInt(s_P);
        } catch (Exception e) {

//            ErParse(Fcontext,e.getMessage(),"GetInt!!!");
        }
        return res;
    }


    private boolean createSwitch(String name, boolean ifSet) {
        LinearLayout linearLayout = findViewById(R.id.laySwitch);
        if (linearLayout != null) {
            final androidx.appcompat.widget.SwitchCompat
                    sw = new androidx.appcompat.widget.SwitchCompat
                    (this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 20, 90, 0);
            sw.setLayoutParams(layoutParams);
            sw.setText(name);
            sw.setChecked(ifSet);
            sw.setTextColor(Color.WHITE);
            linearLayout.addView(sw);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    androidx.appcompat.widget.SwitchCompat
                            cSw = (androidx.appcompat.widget.SwitchCompat
                            ) buttonView;
                    String name = cSw.getText().toString();


                    int numCase=0;
                    for (numCase=0; numCase < customCases.size(); numCase++) {
                        String nm=customCases.get(numCase);
                        if (nm.equals(name))
                            break;

                    }
                    setCases.set(numCase,isChecked);

                    String nV=String.valueOf(numCase);

                    SharedPreferences sSet = getSharedPreferences(setCustomCases, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edSet = sSet.edit();
                    edSet.putBoolean(nV, isChecked);
                    edSet.apply();
                }
            });

            return true;
        }
    return false;
    }


    private void addNewSwitch(String name, String size) {

        if (createSwitch(name, false)) {

            SharedPreferences sFName = getSharedPreferences(nameCustomCases, Context.MODE_PRIVATE);
            SharedPreferences.Editor edName = sFName.edit();

            int num=sFName.getInt("Count",0);

            String nV=String.valueOf(num);
            edName.putString(nV,name);

            SharedPreferences sFsize = getSharedPreferences(siseCustomCases, Context.MODE_PRIVATE);
            SharedPreferences.Editor edSize = sFsize.edit();
            int isize=GetInt(size);
            if (isize == 0)
                isize=5;
            edSize.putInt(nV,isize);

            SharedPreferences sSet = getSharedPreferences(setCustomCases, Context.MODE_PRIVATE);
            SharedPreferences.Editor edSet = sSet.edit();
            edSet.putBoolean(nV,false);


            num=num+1;
            edName.putInt("Count",num);


            edName.apply();
            edSize.apply();
            edSet.apply();

            customCases.add(name);
            sizeCases.add(isize);
            setCases.add(false);
        }
    }

    private void loadCustomSwitches() {

        customCases.clear();
        sizeCases.clear();
        setCases.clear();

        SharedPreferences sFName = getSharedPreferences(nameCustomCases, Context.MODE_PRIVATE);
        intAnySym=sFName.getInt(keyAnySym,12);
        EditText vE=findViewById(R.id.inputSize);
        vE.setText(String.valueOf(intAnySym));

        int num=sFName.getInt("Count",0);
        for (int i=0; i < num; i++) {
            String name=String.valueOf(i);
            String swName=sFName.getString(name,null);
            if (swName != null) {
                SharedPreferences sSet = getSharedPreferences(setCustomCases, Context.MODE_PRIVATE);
                boolean ifSet=sSet.getBoolean(name,false);
                SharedPreferences sFsize = getSharedPreferences(siseCustomCases, Context.MODE_PRIVATE);
                int iSize=sFsize.getInt(name,5);
                customCases.add(swName);
                sizeCases.add(iSize);
                setCases.add(ifSet);
                if (!createSwitch(swName, ifSet)) {
                     break;
                }
            }
        }
    }

    public void hideKeyBoard() {
              getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void addSwitch(View v) {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(Rules.this, R.style.AlertDialogStyle);
        mDialogBuilder.setView(promptsView);

        final EditText nameIdent = (EditText) promptsView.findViewById(R.id.nameIdent);
        final EditText sizeIdent = (EditText) promptsView.findViewById(R.id.sizeIdent);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),null)
                .setNegativeButton(getString(R.string.cancel),
                        (dialog, id) -> {
                            hideKeyBoard();
                            dialog.cancel();
                        });
        final AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
        Button pos=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neg=alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (neg != null)
            neg.setTextColor(Color.WHITE);
        if (pos != null) {
            pos.setTextColor(Color.WHITE);

            pos.setOnClickListener(v1 -> {
                String textIdent = nameIdent.getText().toString();
                String sizeI = sizeIdent.getText().toString().trim();
                if(textIdent.isEmpty()){
                    nameIdent.setError(getString(R.string.emptyid));
                    nameIdent.requestFocus();
                }
               else {
                    hideKeyBoard();
                    addNewSwitch(textIdent,sizeI);
                    alertDialog.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
