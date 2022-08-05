package com.example.wifiapiapp;

import com.example.wifiapiapp.data.WifiInfoData;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DataBase {
    private final String DRIVER = "org.sqlite.JDBC";
    private final String DB = "jdbc:sqlite:C:/Users/kimse/OneDrive/Desktop/WifiApiApp/src/main/database/wifiproject.sqlite";
    public Connection conn = null;
    private Statement statement;
    public ResultSet loadResult = null;
    public int historySize = 0;

    public DataBase() {
        connet();
    }

    public boolean connet() {
        try {
            Class.forName(this.DRIVER);
            if (conn == null) {
                conn = DriverManager.getConnection(this.DB);
                statement = conn.createStatement();

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open database.");
            return false;
        }
        System.out.println("Opened database successfully.");
        return true;
    }

    public void closeConnection() {
        try {
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to close database.");
        }
        System.out.println("Closed database successfully.");
    }

    public void dropTable(String table) {
        String sql = "DROP TABLE " + table;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createWifiInfoTable() {
        String sql = "create table WifiData\n" +
                "      (\n" +
                "          MGR_NO      TEXT\n" +
                "              constraint WifiData_pk\n" +
                "                  primary key,\n" +
                "          WRDOFC      TEXT,\n" +
                "          MAIN_NM     TEXT,\n" +
                "          ADRES1      TEXT,\n" +
                "          ADRES2      TEXT,\n" +
                "          INSTL_FLOOR TEXT,\n" +
                "          INSTL_TY    TEXT,\n" +
                "          INSTL_MBY   TEXT,\n" +
                "          SVC_SE      TEXT,\n" +
                "          CMCWR       TEXT,\n" +
                "          CNSTC_YEAR  TEXT,\n" +
                "          INOUT_DOOR  TEXT,\n" +
                "          REMARS3     TEXT,\n" +
                "          LAT         REAL,\n" +
                "          LNT         REAL,\n" +
                "          WORK_DTTM   TEXT\n" +
                "      )";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (Exception e) {
            dropTable("WifiData");
            createWifiInfoTable();
        }
    }
    public void loadHistory() {
        String sql = "select * from History";
        try {
            loadResult = null;
            loadResult = statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveWifiInfo(ArrayList<WifiInfoData> wifiInfoDataList) {
        String sql = "INSERT INTO WifiData Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < wifiInfoDataList.size(); i++) {
            WifiInfoData wifiInfoData = wifiInfoDataList.get(i);
            try {
                pstmt.setString(1, wifiInfoData.getMGR_NO().replace("'", ""));
                pstmt.setString(2, wifiInfoData.getWRDOFC().replace("'", ""));
                pstmt.setString(3, wifiInfoData.getMAIN_NM().replace("'", ""));
                pstmt.setString(4, wifiInfoData.getADRES1().replace("'", ""));
                pstmt.setString(5, wifiInfoData.getADRES2().replace("'", ""));
                pstmt.setString(6, wifiInfoData.getINSTL_FLOOR().replace("'", ""));
                pstmt.setString(7, wifiInfoData.getINSTL_TY().replace("'", ""));
                pstmt.setString(8, wifiInfoData.getINSTL_MBY().replace("'", ""));
                pstmt.setString(9, wifiInfoData.getSVC_SE().replace("'", ""));
                pstmt.setString(10, wifiInfoData.getCMCWR().replace("'", ""));
                pstmt.setString(11, wifiInfoData.getCNSTC_YEAR().replace("'", ""));
                pstmt.setString(12, wifiInfoData.getINOUT_DOOR().replace("'", ""));
                pstmt.setString(13, wifiInfoData.getREMARS3().replace("'", ""));
                pstmt.setString(14, wifiInfoData.getLAT().replace("'", ""));
                pstmt.setString(15, wifiInfoData.getLNT().replace("'", ""));
                pstmt.setString(16, wifiInfoData.getWORK_DTTM().replace("'", ""));

                pstmt.addBatch();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            conn.setAutoCommit(false);
            pstmt.executeBatch();
            //conn.setAutoCommit(true);
            pstmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadWifiInfo(String lat, String lnt) {

        String query = "round(6371*acos(cos(radians(" + lat + "))*cos(radians(LAT))*cos(radians(LNT)-radians(" + lnt + "))+sin(radians(" + lat + "))*sin(radians(LAT))),4) as DISTANCE,*";
        String table = "WifiData";
        String subQuery = "order by DISTANCE limit 20";

        String sql = "select " + query + " from " + table + " " + subQuery;
        try {
            loadResult = null;
            loadResult = statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getHistory() {
        String[] result = new String[17];
        try {
            this.loadResult.next();
            result[0] = loadResult.getString("ID");
            result[1] = loadResult.getString("LAT");
            result[2] = loadResult.getString("LNT");
            result[3] = loadResult.getString("DATE");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void createHistoryTable() {
        String sql = "create table History\n" +
                "(\n" +
                "    ID   integer\n" +
                "        constraint History_pk\n" +
                "            primary key autoincrement,\n" +
                "    LAT  REAL,\n" +
                "    LNT  REAL,\n" +
                "    DATE TEXT\n" +
                ")";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (Exception e) {
            dropTable("History");
            createHistoryTable();
        }
    }

    public void insertHistory(String lat, String lnt) {
        String date = "'" +
                LocalDate.now().toString() +
                "T" +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "'";
        String sql = "INSERT INTO History (LAT, LNT, DATE) Values('" + lat + "', '" + lnt + "', "+ date + ")";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            createHistoryTable();
            insertHistory(lat,lnt);
        }
    }
    public int getHistorySize() {

        int size = 0;
        String sql = "SELECT count(*) from History";
        try {
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            size = rs.getInt("count(*)");
            rs = null;
        } catch (Exception e) {
            return 0;
        }

        return size;
    }
    public String[] getLoadedWifiInfo() {
        String[] result = new String[17];
        try {
            this.loadResult.next();
            result[0] = loadResult.getString("DISTANCE");
            result[1] = loadResult.getString("MGR_NO");
            result[2] = loadResult.getString("WRDOFC");
            result[3] = loadResult.getString("MAIN_NM");
            result[4] = loadResult.getString("ADRES1");
            result[5] = loadResult.getString("ADRES2");
            result[6] = loadResult.getString("INSTL_FLOOR");
            result[7] = loadResult.getString("INSTL_TY");
            result[8] = loadResult.getString("INSTL_MBY");
            result[9] = loadResult.getString("SVC_SE");
            result[10] = loadResult.getString("CMCWR");
            result[11] = loadResult.getString("CNSTC_YEAR");
            result[12] = loadResult.getString("INOUT_DOOR");
            result[13] = loadResult.getString("REMARS3");
            result[14] = loadResult.getString("LAT");
            result[15] = loadResult.getString("LNT");
            result[16] = loadResult.getString("WORK_DTTM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public void deleteHistory(String id){
        String sql = "delete from History where ID = " + id;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
