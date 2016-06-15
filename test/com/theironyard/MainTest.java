package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Ben on 6/15/16.
 */
public class MainTest {
    public Connection startConnection () throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }


    //Tests functionality of insertUser and selectUser methods
    @Test
    public void testInsertSelect () throws SQLException {
        Connection conn = startConnection();
        User temp = new User(1, "Alice",  "123 fake street", "email@email.com");
        Main.insertUser(conn, temp);
        ArrayList<User> users = Main.selectUser(conn);
        conn.close();
        assertTrue(users !=null);
    }

    //Tests functionality of editUser method
    @Test
    public void testEdit () throws SQLException {
        Connection conn = startConnection();
        User temp = new User(1, "Alice",  "123 fake street", "email@email.com");
        Main.insertUser(conn, temp);
        User update = new User(1, "Bob", "124 fake street", "email@email.email");
        Main.editUser(conn, 1, update);
        ArrayList<User> users = Main.selectUser(conn);
        User user = users.get(0);
        conn.close();
        assertTrue(user.username == "Bob");
    }


    //Tests functionality of deleteUser method
    @Test
    public void testDelete () throws SQLException {
        Connection conn = startConnection();
        User user = new User("Alice",  "123 fake street", "email@email.com");
        Main.insertUser(conn, user);
        ArrayList<User> users = Main.selectUser(conn);
        user = users.get(0);
        Main.deleteUser(conn, user.id);
        users = Main.selectUser(conn);
        conn.close();
        assertTrue(users.size() == 0);
    }


}