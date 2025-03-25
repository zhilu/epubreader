package com.ereader.service;

import com.ereader.config.Constants;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StorageService {

    private static final String DB_URL = "jdbc:sqlite:" + Constants.DB_PATH;

    public static StorageService INSTANCE = new StorageService();

    public void init() {
        File file = new File(Constants.DB_PATH);
        if (file.exists()) {
            createTableBookshelf();
            creatTableBook();
        }
    }

    //--表的初始化-------------------------------------------------------------
    public void createTableBookshelf() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createBookshelfTable = """
                CREATE TABLE IF NOT EXISTS bookshelf (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                );
                """;
            stmt.execute(createBookshelfTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void creatTableBook() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createBookTable = """
                CREATE TABLE IF NOT EXISTS book (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    bookshelf_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    file_path TEXT NOT NULL,
                    FOREIGN KEY (bookshelf_id) REFERENCES bookshelf (id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createBookTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
