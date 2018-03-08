package com.java2e.springboot.utils;

import com.java2e.springboot.bean.Constant;
import org.apache.commons.io.FileUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-14 14:27
 */
public class UploadUtil {
    private static String revertPath;
    /**
     * 解析上传的压缩文件
     *
     * @param request 请求
     * @param file    上传文件
     * @param millis
     * @return 解压后的路径
     * @throws Exception
     */
    public static String resolveCompressUploadFile(HttpServletRequest request, MultipartFile file, String path, long millis) throws Exception {


          /* 截取后缀名 */
        if (file.isEmpty()) {
            throw new Exception("文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        int pos = fileName.lastIndexOf(".");
        //获取后缀
        String extName = fileName.substring(pos + 1).toLowerCase();
        //判断上传文件必须是zip或者是rar否则不允许上传
        if (!extName.equals("zip") && !extName.equals("rar")) {
            throw new Exception("上传文件格式错误，请重新上传");
        }
        // 时间加后缀名保存
        String saveName = millis + "." + extName;

        // 根据服务器的文件保存地址和原文件名创建目录文件全路径
        File pushFile = new File(path + File.separator + File.separator + saveName);

        File descFile = new File(path + File.separator);
        if (!descFile.exists()) {
            descFile.mkdirs();
        }
        //解压目的文件,解压到档次上传的update目录下
        String descDir = path + File.separator + Constant.UPDATE_KEY + File.separator;

        file.transferTo(pushFile);

        //开始解压zip
        if (extName.equals("zip")) {
            CompressFileUtils.unZipFiles(pushFile, descDir);
        } else if (extName.equals("rar")) {
            //开始解压rar
            CompressFileUtils.unRarFile(pushFile.getAbsolutePath(), descDir);
        } else {
            throw new Exception("文件格式不正确不能解压");
        }
        return descDir;
    }

    public static void upline(String src, String rootPath, List list) throws Exception {
        try {
            //获取所有待更新文件
            getAllFiles(src, list);
            //备份更新文件
            copyRunFileToBack(src, rootPath, list);
            //开始更新
            copyNewFileToLine(src, rootPath, list);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("上线失败");
        }
    }

    public static void revert(String src, String rootPath, List list) throws Exception {
        try {
            //获取所有待回滚文件
            getAllFiles(src, list);
            //开始回滚
            copyNewFileToLine(src, rootPath, list);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("回滚失败");
        }
    }





    public static List<String> getAllFiles(String dir, List<String> filelist) {
        File file = new File(dir);
        return getAllFiles(file, filelist);
    }

    public static List<String> getAllFiles(File dir, List<String> filelist) {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
            //若为文件夹，就调用getAllFiles方法
            if (fs[i].isDirectory()) {
                try {
                    getAllFiles(fs[i], filelist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //返回非目录file
                filelist.add(fs[i].getAbsolutePath());
            }
        }
        return filelist;
    }

    public static void copyRunFileToBack(String path, String backPath, List<String> list) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            throw new Exception("没有要备份的文件");
        }
        for (String s : list) {
            System.out.println("s = " + s);
            int i = 0;
            if(path.endsWith(File.separator)){
                i=path.length() - 1;
            }else {
                i = path.length();
            }
            File srcFile = new File(backPath + s.substring(i));
            File destFile = new File(path);
            destFile = new File(destFile.getParent() + File.separator + Constant.BACK_KEY + s.substring(i));
            FileUtils.copyFile(srcFile, destFile);
        }
    }

    public static void copyNewFileToLine(String path, String backPath, List<String> list) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            throw new Exception("没有要上线的文件");
        }
        for (String s : list) {
            int i = 0;
            if (path.endsWith(File.separator)) {
                i = path.length() - 1;
            } else {
                i = path.length();
            }
            File srcFile = new File(s);
            File destFile = new File(backPath + s.substring(i));
            FileUtils.copyFile(srcFile, destFile);
        }
    }

    public static String string2Date(String s){
        long t = Long.parseLong(s);
        Date date = new Date(t);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("t = " + sdf.format(date));
        return sdf.format(date);
    }

    public static String getRevertPath(String project, final String version) throws IOException {
         Files.walkFileTree(Paths.get(project), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("正在访问：" + dir + "目录");
                if(dir.toFile().getName().equals(version)){
                    System.out.println("******找到目标文件夹******");
                    revertPath =  dir.toString();
                    return FileVisitResult.TERMINATE; // 找到了就终止
                }
                return FileVisitResult.CONTINUE; // 没找到继续找
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("\t正在访问" + file + "文件");
                if (file.toFile().getName().equals(version)) {
                    System.out.println("******找到目标文件******");
                    return FileVisitResult.TERMINATE; // 找到了就终止
                }
                return FileVisitResult.CONTINUE; // 没找到继续找
            }

        });
        return revertPath;
    }

    public static void main(String[] args) throws IOException {
        String  s = getRevertPath("D:\\333\\wechat","1511596444616");
        System.out.println("s = " + s);
    }
}
