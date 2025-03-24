package com.ereader.service;

import com.ereader.model.MyBook;
import com.ereader.model.Bookshelf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookshelfService {


    private static final String DB_PATH = System.getProperty("user.home")+"/epubReader.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    public static BookshelfService INSTANCE = new BookshelfService();

    public void init(){
        createTableBookshelf();
        creatTableBook();
    }

    public List<Bookshelf> listBookshelf() {
        List<Bookshelf> bookshelves = new ArrayList<>();
        String sql = "select id,name from bookshelf;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Bookshelf bookshelf = new Bookshelf();
                bookshelf.setId(rs.getInt("id"));
                bookshelf.setName(rs.getString("name"));
                bookshelves.add(bookshelf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookshelves;
    }

    public void addBookshelf(Bookshelf bookshelf) {
        String sql = "INSERT INTO bookshelf (name) VALUES (?);";
        int id = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, bookshelf.getName());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                        bookshelf.setId(id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBookshelf(Bookshelf bookshelf) {
        String sql = "UPDATE bookshelf SET name = ? WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookshelf.getName());
            stmt.setInt(2, bookshelf.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBookshelf(int bookshelfId) {
        deleteAllBook(bookshelfId);
        String sql = "DELETE FROM bookshelf WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookshelfId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<MyBook> getBooksByBookshelf(int bookshelfId) {
        List<MyBook> books = new ArrayList<>();
        String sql = "SELECT id, name, file_path FROM book WHERE bookshelf_id = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookshelfId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MyBook book = new MyBook();
                book.setId(rs.getInt("id"));
                book.setFileName(rs.getString("name"));
                book.setFilePath(rs.getString("file_path"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public void deleteAllBook(int bookshelfId) {
        String sql = "DELETE FROM book WHERE bookshelf_id = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookshelfId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAllBook(int bookshelfId, List<MyBook> books) {
        String sql = "INSERT INTO book (bookshelf_id, name, file_path) VALUES (?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (MyBook book : books) {
                pstmt.setInt(1, bookshelfId);
                pstmt.setString(2, book.getFileName());
                pstmt.setString(3, book.getFilePath());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(int bookshelfId, MyBook myBook) {
        String sql = "INSERT INTO book (bookshelf_id, name, file_path) VALUES (?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, bookshelfId);
            pstmt.setString(2, myBook.getFileName());
            pstmt.setString(3, myBook.getFilePath());
            int row = pstmt.executeUpdate();
            if(row > 0){
                ResultSet rs = pstmt.getGeneratedKeys();
                if(rs.next()){
                    myBook.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // 更新书籍名称
    public void updateBook(int bookId, String newName, String newFilePath) {
        String sql = "UPDATE book SET name = ?, file_path = ? WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newFilePath);
            pstmt.setInt(3, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除书籍
    public void deleteBook(int bookId) {
        String sql = "DELETE FROM book WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
