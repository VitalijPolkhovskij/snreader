// В фильтрах - использовать SIN:
package com.pvs.snreader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.IOException;

import static com.pvs.snreader.MyConstants.DATA_SET;
import static com.pvs.snreader.MyConstants.autoSave;
import static com.pvs.snreader.MyConstants.countRate;
import static com.pvs.snreader.MyConstants.customCases;
import static com.pvs.snreader.MyConstants.flag;
import static com.pvs.snreader.MyConstants.getSwitches;
import static com.pvs.snreader.MyConstants.ifAutoFocus;
import static com.pvs.snreader.MyConstants.ifFlash;
import static com.pvs.snreader.MyConstants.intAnySym;
import static com.pvs.snreader.MyConstants.keyAnySym;
import static com.pvs.snreader.MyConstants.nameCustomCases;
import static com.pvs.snreader.MyConstants.nameswitchautoSave;
import static com.pvs.snreader.MyConstants.nameswitchauto_focus;
import static com.pvs.snreader.MyConstants.nameswitchcheck_SN;
import static com.pvs.snreader.MyConstants.nameswitchuse_flash;
import static com.pvs.snreader.MyConstants.onlySerialNumber;
import static com.pvs.snreader.MyConstants.rotatedBitmap;
import static com.pvs.snreader.MyConstants.setCases;
import static com.pvs.snreader.MyConstants.setCustomCases;
import static com.pvs.snreader.MyConstants.setRate;
import static com.pvs.snreader.MyConstants.siseCustomCases;
import static com.pvs.snreader.MyConstants.sizeCases;
import static com.pvs.snreader.MyConstants.titleText;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageButton starOkButton;
    private ImageButton star1,star2,star3,star4,star5;

    private boolean ifSetFive;
    private AlertDialog alert = null;
    private ProgressBar progressbar;

    private AssetManager mAssetManager;
    private SoundPool mSoundPool;
    private int mCatSound;

    private Animation animThree;
    private TextView statusMessage;
    private TextView textValue;
    private RelativeLayout layResult;
    private String resulText;
    private TextView text_title;
    private ImageView zoom;

    private Button readText;

    private CheckBox ch_auto_focus;
    private CheckBox ch_check_SN;
    private CheckBox ch_autoSave;
    private CheckBox ch_use_flash;


    private static final int RC_OCR_CAPTURE = 9003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!flag)
            return;


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
//            ActionBar aBar = getSupportActionBar();
        }


        progressbar = findViewById(R.id.progressbar);




// Admob:


        MobileAds.initialize(this, initializationStatus -> { });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }
            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.

            }
/*
            @Override
            public void onAdFailedToLoad(int i) {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.

            }
*/

            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                // Gets the domain from which the error came.
                String errorDomain = error.getDomain();
                // Gets the error code. See
                // https://developers.google.com/android/reference/com/google/android/gms/ads/AdRequest#constant-summary
                // for a list of possible codes.
                int errorCode = error.getCode();
                // Gets an error message.
                // For example "Account not approved yet". See
                // https://support.google.com/admob/answer/9905175 for explanations of
                // common errors.
                String errorMessage = error.getMessage();
                // Gets additional response information about the request. See
                // https://developers.google.com/admob/android/response-info for more
                // information.
                ResponseInfo responseInfo = error.getResponseInfo();
                // Gets the cause of the error, if available.
                AdError cause = error.getCause();
                // All of this information is available via the error's toString() method.
                Log.d("Ads", error.toString());
            }


        });
        zoom=findViewById(R.id.zooPic);
        layResult = findViewById(R.id.layResult);

        animThree = AnimationUtils.loadAnimation(this, R.anim.animation_three);
        animThree.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
  //              zoom.setVisibility(View.GONE);
  //              zoom.setVisibility(View.INVISIBLE);
                zoom.setImageAlpha(0);

            }
        });

        SharedPreferences sF = getSharedPreferences(DATA_SET, Context.MODE_PRIVATE);

        getSwitches(sF);
        loadSettingsCustomSwitches();


        statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        text_title=findViewById(R.id.text_title);

        ch_auto_focus = findViewById(R.id.auto_focus);
        ch_use_flash = findViewById(R.id.use_flash);
        ch_check_SN = findViewById(R.id.check_SN);
        ch_autoSave = findViewById(R.id.autoSave);


        ifAutoFocus=sF.getBoolean(nameswitchauto_focus,true);
        ifFlash=sF.getBoolean(nameswitchuse_flash,true);
        onlySerialNumber=sF.getBoolean(nameswitchcheck_SN,true);
        autoSave=sF.getBoolean(nameswitchautoSave,true);

        ch_auto_focus.setChecked(ifAutoFocus);
        ch_use_flash.setChecked(ifFlash);
        ch_check_SN.setChecked(onlySerialNumber);
        ch_autoSave.setChecked(autoSave);





        readText=findViewById(R.id.read_text);
        readText.setOnClickListener(this);
        ch_auto_focus.setOnClickListener(this);
        ch_use_flash.setOnClickListener(this);
        ch_check_SN.setOnClickListener(this);
        ch_autoSave.setOnClickListener(this);

        findViewById(R.id.filters).setOnClickListener(this);

        mAssetManager = getAssets();

        mSoundPool = null;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            createOldSoundPool();
        } else {
            createNewSoundPool();
        }


        if (mSoundPool != null)
            mCatSound = loadSound();


    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        String nameArg=null;
        String id = v.getTag().toString();
        switch (id) {

            case "filters":


                setProgressBarIndeterminateVisibility(true);
                progressbar.setVisibility(View.VISIBLE);


                Intent newintent = new Intent(getApplicationContext(), Rules.class);
                newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newintent);
                break;
            case "read_text":
                setProgressBarIndeterminateVisibility(true);
                progressbar.setVisibility(View.VISIBLE);
                readText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.my_button_act, null));

                Intent intent = new Intent(this, OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, ch_auto_focus.isChecked());
                intent.putExtra(OcrCaptureActivity.UseFlash, ch_use_flash.isChecked());
                startActivityForResult(intent, RC_OCR_CAPTURE);


                break;
            case "check_SN":
                onlySerialNumber=ch_check_SN.isChecked();
                nameArg=nameswitchcheck_SN;
                break;
            case "autoSave":
                nameArg=nameswitchautoSave;
                autoSave=ch_autoSave.isChecked();
                break;
            case "use_flash":
                nameArg=nameswitchuse_flash;
                ifFlash=ch_use_flash.isChecked();
                break;
            case "auto_focus":
                nameArg=nameswitchauto_focus;
                ifAutoFocus=ch_auto_focus.isChecked();
                break;


            default:
                break;
        }

     if (nameArg != null) {

         SharedPreferences.Editor data_editor = getSharedPreferences(DATA_SET, Context.MODE_PRIVATE).edit();
         data_editor.putBoolean(nameArg,((CheckBox) v).isChecked());
         data_editor.apply();
     }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void showRate(SharedPreferences sF) {


        int count = sF.getInt(countRate,0);
        SharedPreferences.Editor edit = sF.edit();
        edit.putInt(countRate,count+1);

        if (count >= 3) {
            edit.putBoolean(setRate,true);
            showMyRate();
        }
        edit.apply();
    }


    private void doRateApppStore() {
        String link = "market://details?id=";
        try {
            getPackageManager().getPackageInfo("com.android.vending", 0);
        } catch (PackageManager.NameNotFoundException e) {
            link = "https://play.google.com/store/apps/details?id=";
        }
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(link + getPackageName())));
    }

    private void showMyRate() {

        ifSetFive = false;
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.rate_mes, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setView(promptsView);
        starOkButton = promptsView.findViewById(R.id.okButton);
        starOkButton.setOnClickListener(v -> {
            if (alert != null)
                alert.cancel();
            if (ifSetFive) {
                doRateApppStore();
            }

        });
        ImageButton closeButton = promptsView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            if (alert != null)
                alert.cancel();
        });

        star1 = promptsView.findViewById(R.id.star1);
        star2 = promptsView.findViewById(R.id.star2);
        star3 = promptsView.findViewById(R.id.star3);
        star4 = promptsView.findViewById(R.id.star4);
        star5 = promptsView.findViewById(R.id.star5);

        star1.setOnClickListener(starClickListener);
        star2.setOnClickListener(starClickListener);
        star3.setOnClickListener(starClickListener);
        star4.setOnClickListener(starClickListener);
        star5.setOnClickListener(starClickListener);

        builder.setCancelable(false);
        alert = builder.create();
        alert.show();

    }

    View.OnClickListener starClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            starOkButton.setAlpha(1.0f);
            ifSetFive = false;
            star1.setImageResource(R.drawable.star_gray);
            star2.setImageResource(R.drawable.star_gray);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
            String tag = v.getTag().toString();
            switch (tag) {
                case "star1": {
                    star1.setImageResource(R.drawable.star1);
                    break;
                }
                case "star2": {
                    star1.setImageResource(R.drawable.star1);
                    star2.setImageResource(R.drawable.star1);
                    break;
                }
                case "star3": {
                    star1.setImageResource(R.drawable.star1);
                    star2.setImageResource(R.drawable.star1);
                    star3.setImageResource(R.drawable.star1);
                    break;
                }
                case "star4": {
                    star1.setImageResource(R.drawable.star1);
                    star2.setImageResource(R.drawable.star1);
                    star3.setImageResource(R.drawable.star1);
                    star4.setImageResource(R.drawable.star1);
                    break;
                }
                case "star5": {
                    star1.setImageResource(R.drawable.star1);
                    star2.setImageResource(R.drawable.star1);
                    star3.setImageResource(R.drawable.star1);
                    star4.setImageResource(R.drawable.star1);
                    star5.setImageResource(R.drawable.star1);
                    ifSetFive = true;
                    break;
                }
            }

        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        readText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.my_button, null));
        findViewById(R.id.progressbar).setVisibility(View.GONE);
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    resulText = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(resulText);
                    if (titleText != null && titleText.length() != 0) {
                        layResult.setVisibility(View.VISIBLE);
                        text_title=findViewById(R.id.text_title);
                        text_title.setText(titleText);

                        if (mCatSound > 0 && mSoundPool != null) {
                            mSoundPool.play(mCatSound, 1, 1, 1, 0, 1);
                        }


                        SharedPreferences sF = getSharedPreferences(DATA_SET, Context.MODE_PRIVATE);
                        if (!sF.getBoolean(setRate,false)) {
                            showRate(sF);
                        }
                    }
                    else
                        layResult.setVisibility(View.GONE);




// ZOOM image - https://stackoverflow.com/questions/2537238/how-can-i-get-zoom-functionality-for-images

// ZOOM - https://github.com/MikeOrtiz/TouchImageView

 //                   ImageView vRes=findViewById(R.id.captImage);
                    com.ortiz.touchview.TouchImageView vRes=findViewById(R.id.captImage);
                    if (vRes != null) {
                        if (rotatedBitmap != null) {
                            vRes.setImageBitmap(rotatedBitmap);
                            ImageView zoom=findViewById(R.id.zooPic);
                            zoom.setVisibility(View.VISIBLE);
                            zoom.setImageAlpha(255);

// OnTouchImageViewListener  Если воспользовпался - отключать картинку zoom.
                            zoom.startAnimation(animThree);


                        } else {
                            vRes.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tech_support, null));
                            layResult.setVisibility(View.GONE);
                        }
                    }
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                }
            } else {
                statusMessage.setText(getString(R.string.ocr_error));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // TODO Auto-generated method stub
        flag = false;
        super.onConfigurationChanged(newConfig);
    }

    private void createOldSoundPool() {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        if (attributes != null)
            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
    }



    private int loadSound() {
        AssetFileDescriptor afd;
        if (mAssetManager == null)
            return -1;
        try {
            afd = mAssetManager.openFd("plucky.mp3");
        } catch (IOException e) {
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

/*
private void textToClipBoard(String text) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)getApplicationContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clipData = android.content.ClipData
                .newPlainText(titleText, text);

        if (clipboardManager != null)
             clipboardManager.setPrimaryClip(clipData);
    } else {
        android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getApplicationContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null)
             clipboardManager.setText(text);
    }


}


 */




public void copyPaste(View v) {
    if (resulText != null && resulText.length() != 0) {
        String textLabel = getString(R.string.copypast);
            final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            final android.content.ClipData clipData = android.content.ClipData
                    .newPlainText(titleText, resulText);
            clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), titleText + " " +textLabel, Toast.LENGTH_SHORT).show();
    }
}

    public void sendNumber(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, resulText);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, resulText));
    }



    private void loadSettingsCustomSwitches() {
        customCases.clear();
        sizeCases.clear();
        setCases.clear();
        SharedPreferences sFName = getSharedPreferences(nameCustomCases, Context.MODE_PRIVATE);

        intAnySym=sFName.getInt(keyAnySym,12);

        int num=sFName.getInt("Count",0);
        for (int i=0; i < num; i++) {
            String name=String.valueOf(i);
            String swName=sFName.getString(name,null);
            if (swName != null) {
                SharedPreferences sSet = getSharedPreferences(setCustomCases, Context.MODE_PRIVATE);
                boolean ifSet=sSet.getBoolean(name,false);
                SharedPreferences sFaze = getSharedPreferences(siseCustomCases, Context.MODE_PRIVATE);
                int iSize=sFaze.getInt(name,5);
                customCases.add(swName);
                sizeCases.add(iSize);
                setCases.add(ifSet);
            }
        }
    }

    @Override
    protected void onResume() {
        progressbar.setVisibility(View.GONE);
        super.onResume();
    }



//

    private void exitApp() {
        Process.killProcess(Process.myPid());
        System.exit(1);
    }


    private String getGooglePlayStoreUrl() {
        String id = getApplicationInfo().packageName; // current google play is   using package name as id
        return "https://play.google.com/store/apps/details?id=" + id;
    }

    private void shareApp() {
        String myUrl = getGooglePlayStoreUrl();
        String sendText = getString(R.string.app_name) + "  " + myUrl;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sendText);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, sendText));
    }


    private void showAbout() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.alert_about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.messStyle);
        builder.setView(promptsView);


        TextView textTitle = promptsView.findViewById(R.id.textTitle);
        String title = getString(R.string.app_name) + " (" + BuildConfig.VERSION_NAME + ")";
        textTitle.setText(title);


        ImageButton okButton = promptsView.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> {
            if (alert != null)
                alert.cancel();
        });
        builder.setCancelable(false);

        alert = builder.create();
        alert.show();
    }

    private void showHelp(String mes) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.alert_help, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.messStyle);
        builder.setView(promptsView);
        TextView textMess = promptsView.findViewById(R.id.textMess);
        textMess.setText(mes);


        ImageButton youTb = promptsView.findViewById(R.id.youTb);
        youTb.setOnClickListener(v -> {

            String resUrl = "https://www.youtube.com/watch?v=nt2Ayu093eY&feature=youtu.be";
            Uri uri = Uri.parse(resUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));


        });
        ImageButton okButton = promptsView.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> {
            if (alert != null)
                alert.cancel();
        });
        builder.setTitle("")
//                .setIcon(R.drawable.logo3)
                .setCancelable(false);

        alert = builder.create();
        alert.show();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            exitApp();
        }
        else if (id == R.id.action_share) {
            shareApp();
        }
        else if (id == R.id.action_help) {
            showHelp(getString(R.string.about_text));
        }
        else if (id == R.id.action_about) {
            showAbout();
        }
        return super.onOptionsItemSelected(item);
    }
}
