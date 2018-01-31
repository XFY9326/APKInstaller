package tool.xfy9326.apkinstaller.Methods;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;

import tool.xfy9326.apkinstaller.R;

import static java.lang.Thread.sleep;

public class InstallMethod {

    public static void installApk(final Activity activity, final Dialog installDialog, final String apkPath, final String apkName, final Drawable apkIcon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = IOMethod.prepareApk(activity, apkPath);
                if (!path.equals(IOMethod.FAILED)) {
                    try {
                        String result = installCommand(activity, path.trim());
                        if (result != null) {
                            sleep(2000);
                            installDialog.cancel();
                            if (result.contains("\n")) {
                                String temp[] = result.split("\n");
                                result = temp[temp.length - 1];
                            }
                            String showText;
                            String showDetail = null;
                            if (result.contains("Success")) {
                                showText = activity.getString(R.string.install_success);
                            } else if (result.contains("Failure")) {
                                showText = activity.getString(R.string.install_failed);
                                showDetail = result.substring(result.indexOf("[") + 1, result.lastIndexOf("]"));
                            } else {
                                showText = activity.getString(R.string.install_failed);
                                showDetail = result;
                            }
                            showStatus(activity, installDialog, showText, showDetail, apkName, apkIcon);
                        } else {
                            if (BaseMethod.hasADB(activity)) {
                                showStatus(activity, installDialog, activity.getString(R.string.install_failed), activity.getString(R.string.adb_no_device), apkName, apkIcon);
                            } else {
                                showStatus(activity, installDialog, activity.getString(R.string.install_failed), activity.getString(R.string.adb_no_port), apkName, apkIcon);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            CommandMethod.runCommand(new String[]{"stop adbd", "start adbd"});
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                        showStatus(activity, installDialog, activity.getString(R.string.install_failed), ExceptionToString(e), apkName, apkIcon);
                    }
                    IOMethod.cleanInstallTemp(activity);
                }
            }
        }).start();
    }

    private static String installCommand(Context context, String path) throws Exception {
        String result = null;
        if (BaseMethod.hasADB(context)) {
            CommandMethod.runCommand(new String[]{"setprop ro.debuggable 1", "setprop persist.service.adb.enable 1"});
            CommandMethod.runCommand(new String[]{"stop adbd", "start adbd"});
            String device = getDevice(CommandMethod.runCommand(new String[]{"adb kill-server", "adb devices"}));
            if (device != null) {
                result = CommandMethod.runCommand(new String[]{"adb -s " + device + " install -r " + path});
            }
            CommandMethod.runCommand(new String[]{"stop adbd", "start adbd"});
        } else {
            String cmd;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                int user_id = android.os.Process.myUserHandle().describeContents();
                cmd = "pm install -r --user " + user_id + " ";
            } else {
                cmd = "pm install -r ";
            }
            result = CommandMethod.runADBCommand(cmd + path);
        }
        return result;
    }

    private static String getDevice(String result) {
        String device = null;
        if (result.contains("device")) {
            String[] line = result.split("\n");
            for (String str : line) {
                if (str.contains("device") && !str.contains("List")) {
                    device = str.substring(0, str.lastIndexOf("device")).trim();
                    break;
                }
            }
        }
        return device;
    }

    private static void showStatus(final Activity activity, Dialog install, final String text, final String detail, final String apkName, final Drawable apkIcon) {
        install.cancel();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseMethod.showStatusDialog(activity, text, detail, apkName, apkIcon);
            }
        });
    }

    private static String ExceptionToString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        e.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }
}