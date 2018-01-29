package tool.xfy9326.apkinstaller.Methods;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class CommandMethod {

    static String runCommand(String[] cmd) throws Exception {
        Process process = Runtime.getRuntime().exec("sh");
        BufferedWriter mOutputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader mInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
        mOutputWriter.close();
        mInputReader.close();
        process.destroy();
        return result.toString();
    }

}
