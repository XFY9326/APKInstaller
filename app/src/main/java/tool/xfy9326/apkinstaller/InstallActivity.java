package tool.xfy9326.apkinstaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import tool.xfy9326.apkinstaller.Methods.ApkMethod;
import tool.xfy9326.apkinstaller.Methods.BaseMethod;
import tool.xfy9326.apkinstaller.Methods.InstallMethod;
import tool.xfy9326.apkinstaller.Methods.PermissionMethod;

public class InstallActivity extends Activity {
    private String Apk_Path;
    private String Apk_Name;
    private Drawable Apk_Icon;
    private ApkMethod apkMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionMethod.verifyStoragePermissions(this)) {
            checkEnv();
        }
    }

    private void checkEnv() {
        if (BaseMethod.hasADB(this)) {
            startInstall(true);
        } else {
            if (BaseMethod.checkHasADB()) {
                BaseMethod.setHasADB(this);
            }
            startInstall(true);
        }
    }

    private void startInstall(boolean check) {
        if (check && !BaseMethod.checkRun(this)) {
            showAttentionDialog();
        } else {
            getInstallInfo();
            showBeforeInstallDialog();
        }
    }

    private void getInstallInfo() {
        Intent intent = getIntent();
        Uri packageUri = intent.getData();
        if (packageUri != null) {
            Apk_Path = packageUri.getPath();
            apkMethod = new ApkMethod(this, Apk_Path);
            Apk_Name = apkMethod.getApplicationName();
            Apk_Icon = apkMethod.getApplicationIcon();
        }
    }

    private void showAttentionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.adb_ask_title);
        builder.setMessage(R.string.adb_ask_content);
        builder.setPositiveButton(R.string.adb_ask_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseMethod.setRunOnce(InstallActivity.this);
                startInstall(false);
            }
        });
        builder.setNegativeButton(R.string.adb_ask_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void showBeforeInstallDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_content_before_install, (ViewGroup) findViewById(R.id.dialog_layout_before_install));
        String[] version = apkMethod.getApplicationVersion();
        String permission = apkMethod.getApplicationPermission();
        String pkgName = apkMethod.getApplicationPkgName();
        TextView textView_version_now = view.findViewById(R.id.textView_version_now);
        textView_version_now.setText(version[0]);
        TextView textView_pkgname = view.findViewById(R.id.textView_pkgname);
        textView_pkgname.setText(pkgName);
        if (version[1] != null) {
            TextView textView_version_installed_text = view.findViewById(R.id.textView_version_installed_text);
            textView_version_installed_text.setVisibility(View.VISIBLE);
            TextView textView_version_installed = view.findViewById(R.id.textView_version_installed);
            textView_version_installed.setText(version[1]);
        }
        if (permission != null && !permission.isEmpty()) {
            TextView textView_permission = view.findViewById(R.id.textView_permission);
            textView_permission.setText(permission);
        } else {
            TextView textView_permission_text = view.findViewById(R.id.textView_permission_text);
            textView_permission_text.setVisibility(View.INVISIBLE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Apk_Name);
        builder.setIcon(Apk_Icon);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InstallMethod.installApk(InstallActivity.this, showInstallDialog(), Apk_Path, Apk_Name, Apk_Icon);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private Dialog showInstallDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_content_install, (ViewGroup) findViewById(R.id.dialog_layout_install));
        AlertDialog.Builder builder = new AlertDialog.Builder(InstallActivity.this);
        builder.setTitle(Apk_Name);
        builder.setIcon(Apk_Icon);
        builder.setCancelable(false);
        builder.setView(view);
        return builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionMethod.REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkEnv();
            } else {
                Toast.makeText(this, R.string.permission_error, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}
