package semester.cn.servlets;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import semester.cn.services.PhotoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "initPhotoWallServlet")
public class initPhotoWallServlet extends HttpServlet {
    PhotoService photoService;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject res = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        photoService = new PhotoService();
        ArrayList<String> photosPath = photoService.getAllPhotoPath();
        if(photosPath.size()>0){
            res.put("message","getAllPhotoPath successfully");
            res.put("success",200);
            for (String path:photosPath) {
                jsonArray.add(path);
            }
        }else{
            res.put("message","fail to getAllPhotoPath ");
            res.put("success",000);
        }
        res.put("photoPath",jsonArray);
        out.write(res.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}