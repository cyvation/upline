package com.java2e.springboot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-28 20:06
 */
public class ExecLinuxCMD {
    public static Object exec(String cmd, StringBuilder sb) {
        try {
            String[] cmdA = {"/bin/sh", "-c", cmd};
            Process process = Runtime.getRuntime().exec(cmdA);
            LineNumberReader br = new LineNumberReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                sb.append(line).append("<br/>");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    /*    String pwdString = exec("pwd").toString();
        String netsString = exec("netstat -nat|grep -i \"80\"|wc -l").toString();

        System.out.println("==========获得值=============");
        System.out.println(pwdString);
        System.out.println(netsString);*/

        String path = "C:\\dev-env\\apache-tomcat-6.0.33\\bin\\shutdown.bat";
        StringBuilder sb = new StringBuilder();
        execCMD(path,sb);
    }

    public static void execCMD(String cmd,StringBuilder sb) {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        boolean flag = false;
        int exitVal;
        try {
            try {
                p = rt.exec("cmd exe /c  " + cmd);
                String line = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line + "\n");
                    System.out.println(line);
                }
                exitVal = p.waitFor();
                // 进程的出口值。根据惯例，0 表示正常终止。
                if (exitVal == 0) {
                    flag = true;
                } else {
                    flag = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
