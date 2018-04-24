package Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListFileServlet extends HttpServlet {

    public void doGet(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        //获取上传文件得目录
        String uploadFilePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        //要下载得文件名
        Map<String,String> fileNameMap = new HashMap<String,String>();
        //递归遍历
        listFile(new File(uploadFilePath),fileNameMap);
        request.setAttribute("fileNameMap",fileNameMap);
        request.getRequestDispatcher("/WEB-INF/jsp/listFile.jsp").forward(request,response);
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
    private void listFile(File file , Map<String,String> map){
        if(!file.isFile()){//如果不是文件
            File files [] = file.listFiles();
            for(File f : files){
                listFile(f,map);//递归
            }
        }else{
            //文件上传后是根据 uuid_文件名 命名
            String realName = file.getName().substring(file.getName().indexOf("_")+1);
            map.put(file.getName(),realName);
        }

    }
}
