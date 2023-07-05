package org.example.Service;

import java.sql.*;

public class GeneratedLinkService {
    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE62_BASE = BASE62_ALPHABET.length();
    private static Connection con;

    static {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/test", "root", "1234");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void start(){
        if(!check()){
            addFreeRows();
        }else return;

    }
    private static void addFreeRows()  {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Long linksCount = 0L;
        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM links ");
            rs = stmt.executeQuery();
            rs.next();
            linksCount = rs.getLong(1);
            rs.close();
        }catch (SQLException e){
            System.out.println(e);
        }
        for (long i = linksCount; i < linksCount + 100; i++){
            try {
                stmt = con.prepareStatement("insert into links (`generated`) values(?)");
                stmt.setString(1, toBase62(i));
                stmt.executeUpdate();
                stmt.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        }

    }
    private static boolean check(){
        ResultSet rs = null;
        int freeLinksCount = 0;
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM links WHERE origin IS NULL ");
            rs = stmt.executeQuery();
            rs.next();
            freeLinksCount = rs.getInt(1);
            rs.close();
        }catch (SQLException e){
            System.out.println(e);
        }
        System.out.println(freeLinksCount);

        return freeLinksCount > 10;

    }
    private static String toBase62(Long number){
        StringBuilder result = new StringBuilder();
        while (number >= BASE62_BASE) {
            int i = (int)(number % BASE62_BASE);
            result.append(BASE62_ALPHABET.charAt(i));
            number = number / BASE62_BASE;
        }
        result.append(BASE62_ALPHABET.charAt(number.intValue()));
        return result.reverse().toString();
    }
}
