package Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UploadView extends HttpServlet {

    public void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException, ServletException {

        request.getRequestDispatcher("/WEB-INF/jsp/upload.jsp").forward(request,response);
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException, ServletException {
        doGet(request,response);
    }
}
