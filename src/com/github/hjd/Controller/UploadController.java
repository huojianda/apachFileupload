package Controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class UploadController extends HttpServlet {


    public void doGet(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        //上传文件的保存地址
        String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        //临时目录
        String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
        File file = new File(tempPath);
        //判断目录是否存在
        if(!file.exists() && !file.isDirectory()){//目录不存在
            file.mkdir();//创建临时目录
        }
        //返回的信息提示
        String message = "保存失败";
        try{
            //使用apache fileupload and io
            //one 创建工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024*100);//设置缓冲区大小100Kb,默认10kb
            factory.setRepository(file);//设置上传时得临时文件保存地址
            //two 创建解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setProgressListener(new ProgressListener() {
                @Override
                public void update(long nowbyteSize, long fileSize, int i) {
                    System.out.println("文件大小为:"+fileSize+",当前处理进度为:"+nowbyteSize);
                }
            });
            upload.setHeaderEncoding("UTF-8");//防止中文乱码
            //three  提交上来的数据 是否是file类型
            if(!ServletFileUpload.isMultipartContent(request)){
                //普通表单
                return ;
            }
            upload.setFileSizeMax(1024*1024*10);//设置上传文件最大值   和
            //four 使用解析器 解析上传文件
            List<FileItem> list = upload.parseRequest(request);
            for(FileItem item : list){
                if(item.isFormField()){//普通数据
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    System.out.println("普通数据为:key="+name+",value="+value);
                }else{//文件数据
                    String fileName = item.getName();//文件名
                    System.out.println("文件名:"+fileName);
                    if(fileName == null || fileName.trim().equals("")){
                        continue;
                    }
                    fileName = fileName.substring(fileName.lastIndexOf("\\")+1);//文件名处理，有可能是全路径
                    String fileExName = fileName.substring(fileName.lastIndexOf(".")+1);//扩展名
                    //获取输入流
                    InputStream in = item.getInputStream();
                    String saveFileName = makeFileName(fileName);//得到文件保存名称
                    String saveFilePath = makeFilePath(saveFileName,savePath);//得到保存目录
                    //创建输出流
                    FileOutputStream out = new FileOutputStream(saveFilePath+"\\"+saveFileName);
                    //创建缓冲
                    byte [] buffer = new byte[1024];
                    //读取表示
                    int length = 0;
                    while((length = in.read(buffer))>0){
                        //写出
                        out.write(buffer,0,length);
                    }
                    out.flush();//刷出
                    in.close();//关闭输入输出
                    out.close();
                    item.delete();//删除临时文件
                    message = "保存成功";
                }
            }
        }catch (FileUploadBase.FileSizeLimitExceededException e){
            e.printStackTrace();
            request.setAttribute("message","单个文件超出最大值");
            request.getRequestDispatcher("/WEB-INF/jsp/message.jsp").forward(request,response);
        }catch (FileUploadBase.SizeLimitExceededException e){
            e.printStackTrace();
            request.setAttribute("message","上传文件总大小，超出限制");
            request.getRequestDispatcher("/WEB-INF/jsp/message.jsp").forward(request,response);
        }catch (Exception e){
            e.printStackTrace();
        }
        request.setAttribute("message",message);//设置信息
        request.getRequestDispatcher("/WEB-INF/jsp/message.jsp").forward(request,response);
    }
    public void doPost(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
    private String makeFileName(String fileName){

        return UUID.randomUUID().toString()+ "_" + fileName;
    }
    private String makeFilePath(String fileName , String savePath){
        //获取文件名得hashcode
        int hashCode = fileName.hashCode();
        int dir1 = hashCode&0xf;//0-15
        int dir2 = (hashCode&0xf0)>>4;//0-15
        //创建新的保存目录
        String dir  = savePath + "\\" +dir1+ "\\" +dir2;  // upload / n / n
        File newFile = new File(dir);
        if(! newFile.exists()){
            newFile.mkdirs();
        }
        return dir;
    }
}
