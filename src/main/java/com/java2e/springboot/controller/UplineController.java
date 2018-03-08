package com.java2e.springboot.controller;

import com.java2e.springboot.bean.Constant;
import com.java2e.springboot.bean.FileNode;
import com.java2e.springboot.bean.UplinePathProperties;
import com.java2e.springboot.utils.ExecLinuxCMD;
import com.java2e.springboot.utils.UploadUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-14 14:29
 */
@Controller
@RequestMapping("/")
public class UplineController {

    @Autowired
    private UplinePathProperties uplinePathProperties;

    @RequestMapping("/upline")
    public String uplineIndex() {
        String path = uplinePathProperties.getParent();
        return "upline";
    }

    @RequestMapping("/history")
    public String uplineHistory() {
        String path = uplinePathProperties.getParent();
        return "history";
    }

    @RequestMapping("/login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/restart")
    public String restart() {
        String cmd = uplinePathProperties.getTomcat();
        System.out.println("cmd = " + cmd);
        StringBuilder sb = new StringBuilder();
        ExecLinuxCMD.exec(cmd, sb);
        sb.append(" [info]开始监控tomcat...[2017-12-06 20:26:29]                ");
        sb.append(" tomcat没有启动                                              ");
        sb.append(" 准备重启------                                              ");
        sb.append(" starting ,please waite! ------                              ");
        sb.append(" http://jxjycj.suda.edu.cn                                   ");
        sb.append(" [info]页面返回码为200,tomcat启动成功,测试页面正常......     ");
        sb.append(" 重启SUCCESS------                                           ");
        sb.append(" sb = tomcat的pid为                                          ");
        sb.append(" [info]开始监控tomcat...[2017-12-06 20:26:29]                ");
        sb.append(" tomcat没有启动                                              ");
        sb.append(" 准备重启------                                              ");
        sb.append(" starting ,please waite! ------                              ");
        sb.append(" http://jxjycj.suda.edu.cn                                   ");
        sb.append(" [info]页面返回码为200,tomcat启动成功,测试页面正常......     ");
        sb.append(" 重启SUCCESS------											");
        System.out.println("sb = " + sb);
        return "history";
    }

    @RequestMapping(value = "/reply", method = RequestMethod.GET)
    public void writeStream(HttpServletResponse response) throws IOException, InterruptedException {
        response.setContentType("text/html;charset=utf-8");
        write(response, "正在重启，请稍后。。。");
        Thread.sleep(1000*2);
        String cmd = uplinePathProperties.getTomcat();
        System.out.println("cmd = " + cmd);
        StringBuilder sb = new StringBuilder();
        ExecLinuxCMD.exec(cmd, sb);
        if (sb.length() == 0) {
            write(response, "重启失败。。。");
        } else {
            write(response, sb.toString().replaceAll("(\r\n|\r|\n|\n\r)", "<br/>"));
        }

        response.getWriter().close();
    }

    private void write(HttpServletResponse response, String content) throws IOException {
        response.getWriter().write(content + "<br/>");
        response.flushBuffer();
        response.getWriter().flush();
    }


    @RequestMapping("/index")
    public String hello() {
        return "index";
    }


    /**
     * 获取历史上传记录
     *
     * @return
     */
    @RequestMapping("/tree2")
    @ResponseBody
    public List<FileNode> getTree2() {
        String path = uplinePathProperties.getParent();
        File file = new File(path);
        List<FileNode> list = new ArrayList<>();
        try {
            int n = 3;
            generateNodesFromFile(n, file, list);
        } catch (Exception e) {
            e.printStackTrace();


        }
        return list;
    }

    private List<FileNode> generateNodesFromFile(int n, File file, List<FileNode> list) throws Exception {
        if (n-- == 0) {
            return list;
        }

        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File innerFile : listFiles) {
                String revertUrl = file.getParentFile().getName() + File.separator;
                String fileName = innerFile.getName();
                FileNode fileNode = new FileNode();
                fileNode.setName(fileName);
                fileNode.setCode(fileName);
                //设置回滚路径
                if (n == 0) {
                    revertUrl += fileName;
                    fileNode.setRevert(revertUrl);
                    fileNode.setTime(UploadUtil.string2Date(fileName));
                }
                List<FileNode> innerFileNodes = new ArrayList<>();
                innerFileNodes = generateNodesFromFile(n, innerFile, innerFileNodes);
                fileNode.setChildren(innerFileNodes);
                System.out.println("revertUrl = " + revertUrl);
                list.add(fileNode);

            }
        }
        return list;
    }


    /**
     * 上传压缩文件
     */
    @RequestMapping("/upline.do")
    @ResponseBody
    public Object upline(@RequestParam("file") MultipartFile pushContent, HttpSession session, HttpServletRequest request) {

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String project = request.getParameter("inlineRadioOptions");
        jsonMap.put(Constant.SUCCESS, false);

        if (pushContent == null) {
            jsonMap.put(Constant.ERROR_MSG, "上传文件不能为空");
        } else {
            if (StringUtils.isBlank(project)) {
                jsonMap.put(Constant.ERROR_MSG, "未选择要上线的项目");
            } else {
                try {
                    if (request.getCharacterEncoding() == null) {
                        request.setCharacterEncoding("UTF-8");
                    }
                    long millis = System.currentTimeMillis();
                    //生成上传路径
                    String path = this.getPath(project, millis);
                    List<String> list = new ArrayList<>();
                    //获取项目根路径
                    String rootPath = getRootPath(project);
                    //解压
                    String saveFileName = UploadUtil.resolveCompressUploadFile(request, pushContent, path, millis);
                    //开始上传
                    UploadUtil.upline(saveFileName, rootPath, list);
                    System.out.println("urlFile" + saveFileName);
                    jsonMap.put(Constant.SUCCESS, true);
                    jsonMap.put("url", saveFileName);
                } catch (Exception e) {
                    jsonMap.put(Constant.ERROR_MSG, e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return jsonMap;
    }

    private String getRootPath(String project) {
        String rootPath = "";
        if("manager".equals(project)){
            rootPath=uplinePathProperties.getManager();
        }else if("center".equals(project)){
            rootPath=uplinePathProperties.getCenter();
        }else if("learning".equals(project)){
            rootPath=uplinePathProperties.getLearning();
        }else if("learnspace".equals(project)){
            rootPath=uplinePathProperties.getLearnspace();
        }else if("workspace".equals(project)){
            rootPath=uplinePathProperties.getWorkspace();
        }else if("wechat".equals(project)){
            rootPath = uplinePathProperties.getWechat();
        }
        return rootPath;
    }

    /**
     * 回滚
     *
     * @return
     */
    @RequestMapping(value = "/revert/{project}/{version}", method = RequestMethod.GET)
    @ResponseBody
    public Object revert(@PathVariable("project") String project, @PathVariable("version") String version) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Constant.SUCCESS, false);
        try {
            String path = UploadUtil.getRevertPath(uplinePathProperties.getParent() + File.separator + project, version) + File.separator + Constant.BACK_KEY;
            String rootPath = getRootPath(project);
            List<String> list = new ArrayList<>();
            UploadUtil.revert(path, rootPath, list);
            jsonMap.put(Constant.SUCCESS, true);
            jsonMap.put("url", path);
        } catch (Exception e) {
            jsonMap.put(Constant.ERROR_MSG, e.getMessage());
            e.printStackTrace();
        }

        return jsonMap;
    }


    private String getPath(String project, long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT);
        String dateStr = sdf.format(new Date());
        String datePath = uplinePathProperties.getParent() + File.separator + project + File.separator + dateStr + File.separator + millis;
        File dateFile = new File(datePath);
        if (!dateFile.exists()) {
            dateFile.mkdirs();
        }
        return datePath;
    }

    public static void main(String[] args) throws Exception {
//        String path = "D:\\1111\\wechat";
//        File file = new File(path);
//        List<FileNode> list = new ArrayList<>();
//        int count = hanoi(3,"A","B","C");
//        System.out.println("count = " + count);
        testZip();
    }

    private static void testZip() throws IOException {

        File zipFile = new File("D:\\333\\wechat\\2017-11-25\\1511601124309\\1511601124309.zip");
        ZipFile zip = new ZipFile(zipFile, "GBK");
        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String descDir = "D:\\333\\wechat\\2017-11-25\\1511601124309\\1511601124309";
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
            ;
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            //输出文件路径信息
            System.out.println(outPath);


            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private static int hanoi(int i, String x, String y, String z) {
        int count = 0;
        System.out.println("count1 = " + count);
        if (i == 0) {
            return 0;
        } else {
            count += hanoi(i - 1, x, z, y);
            System.out.println("count2 = " + count);
            count++;
            System.out.println("count3 = " + count);
            count += hanoi(i - 1, z, x, y);
            System.out.println("count4 = " + count);
        }
        return count;
    }


    public static List<Map<String, Object>> getNodesInFile(File file, List<Map<String, Object>> list) throws Exception {
        checkFile(file);
        File[] files = file.listFiles();

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            File file1 = files[i];
            if (file1.isDirectory() && file1.listFiles().length > 0) {
                List<Map<String, Object>> list1 = new ArrayList<>();
                map.put(file1.getName(), getNodesInFile(file1, list1));
            }
        }
        list.add(map);
        return list;
    }

    private static void checkFile(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("文件不存在");
        }
    }
}
