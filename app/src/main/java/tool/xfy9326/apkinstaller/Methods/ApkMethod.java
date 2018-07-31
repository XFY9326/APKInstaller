package tool.xfy9326.apkinstaller.Methods;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class ApkMethod {
    private final String applicationPath;
    private final PackageManager packageManager;

    public ApkMethod(Context context, String applicationPath) {
        this.applicationPath = applicationPath;
        this.packageManager = context.getPackageManager();
    }

    public String getApplicationPkgName() {
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(applicationPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.packageName;
        }
        return null;
    }

    public String getApplicationName() {
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(applicationPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            packageInfo.applicationInfo.sourceDir = applicationPath;
            packageInfo.applicationInfo.publicSourceDir = applicationPath;
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        }
        return null;
    }

    public String[] getApplicationVersion() {
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(applicationPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            String[] result = new String[2];
            result[0] = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
            try {
                PackageInfo sysPackageInfo = packageManager.getPackageInfo(packageInfo.packageName, PackageManager.GET_ACTIVITIES);
                result[1] = sysPackageInfo.versionName + " (" + sysPackageInfo.versionCode + ")";
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            return result;
        }
        return null;
    }

    private static Drawable zoomImg(Drawable drawable, int newWidth, int newHeight) {
        Bitmap bm = drawableToBitmap(drawable);
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        if (!bm.isRecycled()) {
            bm.recycle();
        }
        return new BitmapDrawable(newbmp);
    }

    public String[] getApplicationPermission() {
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(applicationPath, PackageManager.GET_PERMISSIONS);
        if (packageInfo != null) {
            ArrayList<String> result_a = new ArrayList<>();
            ArrayList<String> result_b = new ArrayList<>();
            String[] allPermission = packageInfo.requestedPermissions;
            if (allPermission != null) {
                if (allPermission.length != 0) {
                    PermissionInfo permissionInfo;
                    for (String permissionName : allPermission) {
                        try {
                            permissionInfo = packageManager.getPermissionInfo(permissionName, 0);
                            result_a.add(permissionInfo.loadLabel(packageManager).toString());
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            result_b.add(permissionName);
                        }
                    }
                }
                result_a.addAll(result_b);
                return result_a.toArray(new String[result_a.size()]);
            }
        }
        return null;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public Drawable getApplicationIcon(Context context) {
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(applicationPath, PackageManager.GET_ACTIVITIES);
        packageInfo.applicationInfo.sourceDir = applicationPath;
        packageInfo.applicationInfo.publicSourceDir = applicationPath;
        Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
        return zoomImg(icon, dip2px(context, 25), dip2px(context, 25));
    }

}
