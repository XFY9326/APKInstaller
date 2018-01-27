package tool.xfy9326.apkinstaller.Methods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import tool.xfy9326.apkinstaller.R;

public class BaseMethod {

    public static boolean checkADB() {
        if (hasADB()) {
            return true;
        } else {
            if (hasADB_old()) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasADB_old() {
        try {
            Process process = Runtime.getRuntime().exec("adb");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            int i = process.waitFor();
            if (i == 0) {
                Runtime.getRuntime().exec("adb");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static boolean hasADB() {
        String binPath = "/system/bin/adb";
        String xBinPath = "/system/xbin/adb";
        return new File(binPath).exists() && isExecutable(binPath) || new File(xBinPath).exists() && isExecutable(xBinPath);
    }

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

    public static boolean checkRun(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("Run_Once", false);
    }

    public static void setRunOnce(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("Run_Once", true).apply();
    }

    public static boolean hasADB(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("Has_ADB", false);
    }

    public static void setHasADB(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("Has_ADB", true).apply();
    }

    static void showStatusDialog(final Activity activity, String text, String detail, String apkName, Drawable apkIcon) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_content_install_status, (ViewGroup) activity.findViewById(R.id.dialog_layout_install_status));
        TextView textView_status = view.findViewById(R.id.textView_install_status);
        textView_status.setText(text);
        if (detail != null) {
            TextView textView_detail = view.findViewById(R.id.textView_install_detail);
            textView_detail.setText(detail);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(apkName);
        builder.setIcon(apkIcon);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        builder.setView(view);
        builder.show();
    }

}
