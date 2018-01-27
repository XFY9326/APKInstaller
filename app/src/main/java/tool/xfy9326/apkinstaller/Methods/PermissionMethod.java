package tool.xfy9326.apkinstaller.Methods;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionMethod {
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String PERMISSIONS_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static boolean verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                int permission = activity.checkSelfPermission(PERMISSIONS_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{PERMISSIONS_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
