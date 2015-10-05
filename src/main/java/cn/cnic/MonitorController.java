package cn.cnic;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

@Controller
public class MonitorController {
    private static final String IP = "TODO205"+":3306";
    private static final String CONN_STRING = "jdbc:mysql://"+IP+"/platform2?" +
            "user=root&password=root";
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

    /**
     *
     * @return monitor data of provinces
     */
    @RequestMapping("provdata.do")
    public @ResponseBody String getData(){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://www2.csdb.cn/getServiceInfo.jsp?serviceName=cloud");
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            try {
                HttpEntity entity1 = response1.getEntity();
                String value = getStringFromInputStream(entity1.getContent());
                // do something useful with the response body
                // and ensure it is fully consumed
                System.out.println(value);
                EntityUtils.consume(entity1);
                return value;
            } finally {
                response1.close();
            }
        }catch(Exception e ){
            e.printStackTrace();
        }
        return "";

    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}