package Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class DownLoadServlet extends HttpServlet {

    public void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException, ServletException {
        String fileName = request.getParameter("filename");
        fileName = new String(fileName.getBytes("iso8859-1"),"utf-8");
        //文件保存目录
        String rootPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        //通过文件名 找到文件目录
        String path = findFileSavePathByFileName(fileName,rootPath);
        File file = new File(path + "\\" + fileName);
        if(!file.exists()){
            request.setAttribute("message","资源文件已删除");
            request.getRequestDispatcher("/WEB-INF/jsp/message.jsp").forward(request,response);
            return ;
        }
        //处理文件名
        String realName = fileName.substring(fileName.indexOf("_")+1);
        //设置响应头
        response.setHeader("content-disposition","attachment;filename=" + URLEncoder.encode(realName, "UTF-8"));
        //读取要下载得文件
        FileInputStream in = new FileInputStream( path +"\\" + fileName);
        //输出流
        OutputStream out = response.getOutputStream();
        //缓冲
        byte [] buffer = new byte[1024];
        int length =0;
        while((length = in.read(buffer)) > 0){
            out.write(buffer,0,length);
        }
        in.close();
        out.close();
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException, ServletException {
        doGet(request,response);
    }
    private String findFileSavePathByFileName(String fileName , String path){
        int hashCode = fileName.hashCode();
        int dir1 = hashCode&0xf;
        int dir2 = (hashCode&0xf0)>>4;
        String dir = path + "\\" + dir1 + "\\" + dir2;
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }

        return dir;
    }
}
