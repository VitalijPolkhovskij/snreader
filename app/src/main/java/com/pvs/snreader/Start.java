package com.pvs.snreader;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static android.app.role.RoleManager.ROLE_ASSISTANT;
// import static androidx.core.role.RoleManagerCompat.ROLE_ASSISTANT;

public class Start extends AppCompatActivity
   implements ActivityCompat.OnRequestPermissionsResultCallback {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;
    private final String[] nmPerm = {

            Manifest.permission.CAMERA,
    };

    private final int[] stringPerm = {
            R.string.perm_camera,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        if (ifAllPermisssionGranted())
            startAndFinish();
        else
           findViewById(R.id.btnStart).setVisibility(View.VISIBLE);
    }

    public void doGo(View v) {
        if (insertDummyContactWrapper())
            startAndFinish();
    }

// -=======================

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                return shouldShowRequestPermissionRationale(permission);
            }
        }
        return true;
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Start.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    private void showMessageOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Start.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
//                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                        AlertPermission(getString(R.string.badpermission));
                        return;
                }
            }
            startAndFinish();
        }
    }

    private void startAndFinish() {

        ProgressBar v_Bar = (ProgressBar) findViewById(R.id.progressBar1);
        v_Bar.setVisibility(View.VISIBLE);
        Intent newintent = new Intent(getApplicationContext(), MainActivity.class);
        newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newintent);
        finish();

    }


// https://stackoverflow.com/questions/59051867/permission-to-display-pop-up-windows-while-running-in-the-background



    private void AlertPermission(String Soo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Start.this);
        builder.setTitle(getString(R.string.error))
                .setMessage(Soo)
                .setIcon(R.drawable.important)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok),
                        (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean ifAllPermisssionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : nmPerm) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                   return false;
            }
        }
    return true;
    }

    private boolean insertDummyContactWrapper() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<>();
            final List<String> permissionsList = new ArrayList<String>();

            for (int i = 0; i < nmPerm.length; i++) {
                if (!addPermission(permissionsList, nmPerm[i]))
                    permissionsNeeded.add(getString(stringPerm[i]));
            }
            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    StringBuilder message = new StringBuilder(getString(R.string.perm_grant_access) + permissionsNeeded.get(0));
                    for (int i = 1; i < permissionsNeeded.size(); i++) {
                        String txtP = permissionsNeeded.get(i);
                        if (i > 1 && txtP.length() != 0)
                            message.append(", ");
                        message.append(txtP);
                    }
//                    message.append(".");
                    showMessageOKCancel(message.toString(),
                            (dialog, which) -> {


                                String[] s_Arr = new String[permissionsList.size()];
                                requestPermissions(permissionsList.toArray(s_Arr),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            });
                    return false;
                }
                String[] s_Arr = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(s_Arr),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return false;
            }
        }
        return true;
    }
}

