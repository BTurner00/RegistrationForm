package com.theironyard;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, address VARCHAR, email VARCHAR)");
    }

    public static void insertUser(Connection conn, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, user.username);
        stmt.setString(2, user.address);
        stmt.setString(3, user.email);
        stmt.execute();
    }

    public static ArrayList<User> selectUser(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        ArrayList<User> users = new ArrayList<>();
        while(results.next()) {
            int id = results.getInt("id");
            String username = results.getString("username");
            String address = results.getString("address");
            String email = results.getString("email");
            User user = new User(id, username, address, email);
            users.add(user);
        }
        return users;
    }

    public static void editUser(Connection conn, int id, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET username = ?, address =?, email = ? WHERE id = ?");
        stmt.setString(1, user.username);
        stmt.setString(2, user.address);
        stmt.setString(3, user.email);
        stmt.setInt(4, id);
        stmt.execute();
    }

    public static void deleteUser(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.externalStaticFileLocation("public");
        Spark.init();

        JsonSerializer serializer = new JsonSerializer();

        Spark.get(
                "/user",
                (request, response) -> {
                    ArrayList<User> users = selectUser(conn);
                    return serializer.serialize(users);
                }

        );

        Spark.post(
                "/user",
                (request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    User user = p.parse(body, User.class);
                    insertUser(conn, user);
                    return "";
                }

        );

        Spark.put(
                "/user",
                (request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    User user = p.parse(body, User.class);
                    editUser(conn, user.id, user);
                    return "";
                }

        );

        Spark.delete(
                "/user/:id",
                (request, response) -> {
                    Integer id = Integer.valueOf(request.params(":id"));
                    deleteUser(conn, id);
                    return "";
                }

        );

    }
}
