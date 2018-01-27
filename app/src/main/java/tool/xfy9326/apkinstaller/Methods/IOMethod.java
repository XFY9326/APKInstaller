package tool.xfy9326.apkinstaller.Methods;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

class IOMethod {
    static final String FAILED = "FAILED";

    private static final String NEW_APK_NAME = "base.apk";

    static String prepareApk(Context context, String path) {
        try {
            int byte_read;
            File file = new File(path);
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                File cacheDir = context.getExternalCacheDir();
                String new_Path;
                if (cacheDir == null) {
                    new_Path = Environment.getExternalStorageDirectory() + File.separator + NEW_APK_NAME;
                } else {
                    new_Path = cacheDir.getAbsolutePath() + File.separator + NEW_APK_NAME;
                }
                File new_File = new File(new_Path);
                if (new_File.exists()) {
                    if (!new_File.delete()) {
                        return FAILED;
                    }
                }
                FileOutputStream fileOutputStream = new FileOutputStream(new_File);
                byte[] buffer = new byte[1024 * 5];
                while ((byte_read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byte_read);
                }
                inputStream.close();
                return new_Path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FAILED;
    }

    static void cleanInstallTemp(Context context) {
        File temp1 = new File(Environment.getExternalStorageDirectory() + File.separator + NEW_APK_NAME);
        if (temp1.exists()) {
            //noinspection ResultOfMethodCallIgnored
            temp1.delete();
        }
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File temp2 = new File(cacheDir.getAbsolutePath() + File.separator + NEW_APK_NAME);
            if (temp2.exists()) {
                //noinspection ResultOfMethodCallIgnored
                temp2.delete();
            }
        }
    }
}
