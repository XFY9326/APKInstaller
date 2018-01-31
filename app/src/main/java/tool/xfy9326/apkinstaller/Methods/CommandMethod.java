package tool.xfy9326.apkinstaller.Methods;

import android.util.Base64;

import com.tananaev.adblib.AdbBase64;
import com.tananaev.adblib.AdbConnection;
import com.tananaev.adblib.AdbCrypto;
import com.tananaev.adblib.AdbStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

class CommandMethod {

    static String runADBCommand(String cmd) throws Exception {
        int port = getADBPort();
        if (port != -1 && isPortUsed(port)) {
            Socket socket = new Socket("127.0.0.1", port);
            AdbCrypto crypto = AdbCrypto.generateAdbKeyPair(new AdbBase64() {
                @Override
                public String encodeToString(byte[] data) {
                    return Base64.encodeToString(data, Base64.NO_CLOSE);
                }
            });
            AdbConnection connection = AdbConnection.create(socket, crypto);
            connection.connect();
            AdbStream stream = connection.open("shell:" + cmd);
            StringBuilder builder = new StringBuilder();
            try {
                while (!stream.isClosed()) {
                    builder.append(new String(stream.read(), "UTF-8")).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.close();
            return builder.toString();
        }
        return null;
    }

    static String runCommand(String[] cmd) throws Exception {
        Process process = Runtime.getRuntime().exec("sh");
        BufferedWriter mOutputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader mInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader mErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        for (String str : cmd) {
            mOutputWriter.write(str + "\n");
            mOutputWriter.flush();
        }
        mOutputWriter.write("exit\n");
        mOutputWriter.flush();
        process.waitFor();
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = mInputReader.readLine()) != null) {
            result.append(line).append("\n");
        }
        while ((line = mErrorReader.readLine()) != null) {
            result.append(line).append("\n");
        }
        mOutputWriter.close();
        mInputReader.close();
        process.destroy();
        return result.toString();
    }

    private static int getADBPort() throws Exception {
        String result = runCommand(new String[]{"getprop service.adb.tcp.port"});
        if (result.trim().isEmpty()) {
            return -1;
        }
        return Integer.parseInt(result.trim());
    }

    private static boolean isPortUsed(int port) {
        Process process = null;
        try {
            String command = "netstat -tln";
            process = Runtime.getRuntime().exec(command);
            InputStream in = process.getInputStream();
            Scanner scanner = new Scanner(in, "UTF-8");
            String[] result = scanner.useDelimiter("\\A").next().split("\n");
            boolean isUsed = false;
            for (int i = 1; i < result.length; i++) {
                String[] detail = result[i].split("\\s+");
                if (detail[3].contains(port + "") || detail[4].contains(port + "")) {
                    isUsed = true;
                    break;
                }
            }
            return isUsed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

}