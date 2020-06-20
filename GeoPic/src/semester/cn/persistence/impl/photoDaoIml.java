package semester.cn.persistence.impl;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import semester.cn.domain.PhotoInfo;
import semester.cn.persistence.PhotoDao;
import semester.cn.persistence.UtilDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class photoDaoIml  implements PhotoDao {
//    private static String insertPhotoInfoSql = "insert into photoinfo " +
//        " (takenplace,takentime,geo,photopath) " +
//        " values (?,?,?,?)";
    private static String getPhotoGPSAndPath = "select st_astext(geo) as geo,photopath " +
        "from photoinfo";
    private static String getAllPhotoPath = "select photopath from photoinfo order by takentime desc";

    @Override
    public PhotoInfo getPhotoByPath(PhotoInfo photoInfo) {

        return null;
    }

    @Override
    public boolean insertPhotoInfoToDB(PhotoInfo photoInfo) {
        boolean insertResult = false;
        Connection connection = null;
        try {
            String insertPhotoInfoSql = "";
            System.out.println(photoInfo.getGeo()+"hellll");
            if(photoInfo.getGeo().equals("no GPSInfo")||photoInfo.getGeo().equals("")){
                System.out.println("我进来了嘛"+photoInfo.getGeo());
               insertPhotoInfoSql = "insert into photoinfo " +
                        " (takenplace,takentime,photopath,photolabels,facesid) " +
                        " values (?,?,?,?,?)";
            }else{
                insertPhotoInfoSql = "insert into photoinfo " +
                        " (takenplace,takentime,geo,photopath,photolabels,facesid) " +
                        " values (?,?,st_geomfromtext('"+photoInfo.getGeo()+"',3857),?,?,?)";
            }

            connection = UtilDao.getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement(insertPhotoInfoSql);
            preparedStatement.setString(1,photoInfo.getTakenPlace());
            System.out.println(photoInfo.getTakenTime());
            preparedStatement.setTimestamp(2,photoInfo.getTakenTime());
            preparedStatement.setString(3,photoInfo.getPhotoPath());
            preparedStatement.setArray(4, (Array) photoInfo.getPhotoLabels());
            preparedStatement.setArray(5, (Array) photoInfo.getFacesId());
            int num = preparedStatement.executeUpdate();
            if(num>0){
                 insertResult= true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  insertResult;
    }

    @Override
    public HashMap<String, String> getPhotoGPSAndPath() {
        HashMap <String,String> photoGPSAndPath = new HashMap<String, String>();
        Connection connection = null;
        try {
            connection = UtilDao.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getPhotoGPSAndPath);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String GPS = resultSet.getString(1);
                String Path = resultSet.getString(2);

                photoGPSAndPath.put(Path, GPS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  photoGPSAndPath;
    }

    @Override
    public ArrayList<String > getTimeQueryPhotoPath(String startTime, String endTime) {
        Connection conn = null;
        ArrayList<String> timeQueryRes = new ArrayList<>();
        try{
            String getTimeQueryPhotoPathSql = "select photopath from photoinfo " +
                    " where takentime between '"+startTime+"' and '"+endTime+"'";
            conn = UtilDao.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(getTimeQueryPhotoPathSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String path = resultSet.getString(1);
                timeQueryRes.add(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeQueryRes;
    }

    @Override
    public ArrayList<String> getPlaceQueryPhotoPath(String geo, String address) {
        Connection conn = null;
        ArrayList<String> placeQueryRes = new ArrayList<>();
        try{
            String getPlaceQueryPhotoPathSql = "select photopath from photoinfo " +
                    "  where ST_Distance(st_transform(st_setsrid(geo,4326),3857), st_transform(st_setsrid('"+
                    geo+"'::geometry,4326),3857))<2000";

            //select st_astext(geo) from photoinfo where ST_Distance(st_transform(st_setsrid(geo,4326),3857),
            //	st_transform(st_setsrid('POINT(112.926388 28.164166)'::geometry,4326),3857)) <10
            conn = UtilDao.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(getPlaceQueryPhotoPathSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String path = resultSet.getString(1);
                placeQueryRes.add(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return placeQueryRes;
    }

    @Override
    public ArrayList<String> getAllPhotoPath() {
        ArrayList<String> res = new ArrayList<>();
        Connection conn;
        try{
            conn = UtilDao.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(getAllPhotoPath);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String path = resultSet.getString(1);
                res.add(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean getThumbs() {
//        System.out.println("Welcome to OpenCV " + Core.VERSION);
//        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
//        System.out.println("OpenCV Mat: " + m);
        return false;
    }

    @Override
    public int getPhotoIdAcoordintPhotoPath(PhotoInfo photoInfo) {
        int photo_id = 0;
        Connection conn = null;
        try{
            System.out.println("photo Pathinget "+photoInfo.getPhotoId());
            String getPhotoIdAccordingPhotoPathSql = "select photo_id from photoinfo " +
                    "where photopath = '"+photoInfo.getPhotoPath()+"'";
            conn = UtilDao.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(getPhotoIdAccordingPhotoPathSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                photo_id = resultSet.getInt("photo_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photo_id;
    }

    @Override
    public boolean insertPhotoFaceId(PhotoInfo photoInfo) {
        boolean insertPhotoFaceIdRes = false;
        Connection conn= null;
        ArrayList<Integer> arrayList = photoInfo.getFacesId();
        String facesId = "";
        for(int i = 0;i<arrayList.size();i++){
            facesId +=arrayList.get(i);
            if(i<arrayList.size()-1)
            {
                facesId+=",";
            }
        }
        try{
            conn = UtilDao.getConnection();
            String insertPhotoFaceIdSql = "update photoinfo set facesid = '{"+facesId+"}' where photo_id = "+photoInfo.getPhotoId();
            PreparedStatement preparedStatement = conn.prepareStatement(insertPhotoFaceIdSql);
            int num = preparedStatement.executeUpdate();
            System.out.println("人脸id"+num);
            while (num>0){
                insertPhotoFaceIdRes = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertPhotoFaceIdRes;
    }

    @Override
    public boolean insertPhotoLabel(PhotoInfo photoInfo) {
        boolean insertPhotoLabelRes = false;
        Connection conn = null;
        ArrayList<String> arrayList = photoInfo.getPhotoLabels();
        String photoLabels = "";
        for(int i = 0;i<arrayList.size();i++){
            photoLabels +=arrayList.get(i);
            if(i<arrayList.size()-1)
            {
                photoLabels+=",";
            }
        }
        try{
            conn = UtilDao.getConnection();
            String insertPhotoFaceIdSql = "update photoinfo set  photolabels= '{"+photoLabels+"}' where photo_id = "+photoInfo.getPhotoId();
            PreparedStatement preparedStatement = conn.prepareStatement(insertPhotoFaceIdSql);
            int num = preparedStatement.executeUpdate();
            while (num>0){
                insertPhotoLabelRes = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertPhotoLabelRes;
    }


}
