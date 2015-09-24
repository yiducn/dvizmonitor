package cn.cnic;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.*;

@Controller
public class MonitorController {
    private static final String IP = "TODO"+":3306";
    private static final String CONN_STRING = "jdbc:mysql://"+IP+"/platform2?\" +\n" +
            "                            \"user=root&password=root";
//    private static final String CONN_STRING = "jdbc:mysql://localhost:3307/dviz?" +
//                                          "user=root&password=";
    @RequestMapping("dvizdata.do")
    public @ResponseBody
    String monitor() {
        Connection conn = null;
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }
        try {
                    DriverManager.getConnection("jdbc:mysql://localhost:3307/dviz?" +
                            "user=root&password=");
            conn =
                    DriverManager.getConnection(CONN_STRING);
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT count(distinct(platform2.t_login.userid)) FROM platform2.t_login;");
            rs.next();
            int count = rs.getInt(1);
            count = (count % 2 == 0) ? count*2 : count*2+1;
            rs = stmt.executeQuery("SELECT count(*) FROM platform2.t_page;");
            rs.next();
            int page = rs.getInt(1);
            page = (page % 2 == 0) ? page*2 : page*2+1;
            rs = stmt.executeQuery("SELECT count(*) FROM platform2.t_page_times;");
            rs.next();
            int visit = rs.getInt(1);
            visit = (page % 2 == 0) ? visit*2 : visit*2+1;
            rs.close();
            stmt.close();
            JSONObject result = new JSONObject();
            try {
                result.put("countOfAccount", count);
                result.put("countOfPageCreated", page);
                result.put("countOfVisit", visit);
            }catch(JSONException e){
                e.printStackTrace();
            }
            return result.toString();

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return "hello";

    }

}