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
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import tool.xfy9326.apkinstaller.R;

public class BaseMethod {

    public static boolean checkHasADB() {
        try {
            Process process = Runtime.getRuntime().exec("sh");
            BufferedWriter mOutputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader mInputReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            mOutputWriter.write("adb\n");
            mOutputWriter.flush();
            mOutputWriter.write("exit\n");
            mOutputWriter.flush();
            process.waitFor();
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = mInputReader.readLine()) != null) {
                result.append(line).append("\n");
            }
            mInputReader.close();
            process.destroy();
            return !result.toString().contains("adb: not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkRun(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("RUN_ONCE", false);
    }

    public static void setRunOnce(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("RUN_ONCE", true).apply();
    }

    public static boolean hasADB(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("HAS_ADB", false);
    }

    public static void setHasADB(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("HAS_ADB", true).apply();
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
