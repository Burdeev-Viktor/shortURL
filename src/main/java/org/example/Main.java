package org.example;


import org.hashids.Hashids;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Main {
    private static String str = "https://proglib.io/p/a-mozhno-pokoroche-kak-rabotayut-sokrashchateli-ssylok-2020-02-24";
    private static Hashids hashids = new Hashids(str);
    private static Connection con;

    static {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/test", "root", "1234");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Main() throws SQLException {
    }

    public static void main(String[] args) throws SQLException {
        String str = "uLz`qks^A\\JBpgPEUfIgt\\eG_FqMKnbbqobTp^SnkVethwUnfiVjrqiIWMjbH^`RsPXfnpdAgTDHqhssH";
        String str1 = "_``EzWWT^yJEMoADyXxPD^rI]PCpG^ESlleyKXjMEvxQOejFeybLaCrcHRR";

        System.out.println(str.hashCode());
        System.out.println(str1.hashCode());
        String id = Hashing.adler32().hashString(str, StandardCharsets.UTF_8).toString();
        System.out.println(id);


        String str2 = "https://poezdato.net/raspisanie-poezdov/minsk-pass--molodechno/24.06.2023/";

//        String or = getOriginLincksById(1L);
//        System.out.println(or);
//        Encoder encoder = new Encoder();
//        System.out.println(encoder.encode(or));



        createLinks();
    }
    private static String getOriginLincksById(Long id) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        stmt = con.prepareStatement("SELECT origin FROM links WHERE idlinks = ?");
        stmt.setLong(1,id);

        rs =  stmt.executeQuery();
        rs.next();
        return rs.getString(1);
    }

    private static void createLinks() throws SQLException {

        Random random = new Random();
        int leftLimit = 65;
        int rightLimit = 122;


        for (long count = 0; count < 3000000; count++) {
            int targetStringLength = random.nextInt(50,100);
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            String generatedString = buffer.toString();
            System.out.println("# -> " + count);
            String str4  = Hashing.adler32().hashString(generatedString, StandardCharsets.UTF_8).toString();
            PreparedStatement stmt = con.prepareStatement("insert into links(origin,denerate) values(?,?)");
            stmt.setString(1, generatedString);
            stmt.setString(2, str4);
            stmt.executeUpdate();
            stmt.close();
        }

    }
}

