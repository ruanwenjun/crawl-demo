package com.ruanwenjun.utils;

import com.ruanwenjun.entity.ScrawEntity;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 *  将爬取的数据插入mysql
 * @Author RUANWENJUN
 * @Creat 2018-09-07 17:35
 */

public class JDBCUtil {
    private static String DB_URL = "jdbc:mysql://localhost:3306/scraw?serverTimezone=UTC&useSSL=false";
    private static String USER = "root";
    private static String PASSWORD = "root";
    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insert(List<ScrawEntity> list) throws SQLException {
        String date = DateTime.now().toString("yyyyMMdd");
        String hour = DateTime.now().hourOfDay().get() + "";
        String oridate = DateTime.now().toString("yyyyMMddmm");
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO hotwords(date,hour,oridate,source,oridata,cutted,count) values (?,?,?,?,?,?,?)");
        for (int i = 0; i < list.size(); i++) {
            ScrawEntity entity = list.get(i);
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, hour);
            preparedStatement.setString(3, oridate);
            preparedStatement.setString(4, entity.getSite());
            preparedStatement.setString(5, entity.getHot());
            preparedStatement.setString(6, entity.getWords().toString().replace("[", "").replace("]", "").replace(",", "@>"));
            preparedStatement.setString(7, entity.getCount());
            preparedStatement.addBatch();
            if (i % 200 == 0 || i == list.size() - 1) {
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            }
        }
        preparedStatement.close();
    }
}
