package DB;

import java.sql.*;

public class Library {
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String URL_STRING = "jdbc:sqlite:library.dat";

    public static void main(String[] args) {
        //Book.writeIntoDB(new Book("Proces"));
        //System.out.println(Book.readFromDB(1) + "\n" + Book.readFromDB(2));
        Book.deleteFromDB("Proces");
        System.out.println(Book.readFromDB(1) + "\n" + Book.readFromDB(2));
    }

    static class Book {
        int bookId = 0;
        String name;
        int authorId = 0;

        public Book(String name) {
            this.name = name;
        }

        private Book(int bookId, String name, int authorId) {
            this.bookId = bookId;
            this.name = name;
            this.authorId = authorId;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "bookId=" + bookId +
                    ", name='" + name + '\'' +
                    ", authorId=" + authorId +
                    '}';
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAuthorId(int authorId) {
            this.authorId = authorId;
        }

        public int getBookId() {
            return bookId;
        }

        public String getName() {
            return name;
        }

        public int getAuthorId() {
            return authorId;
        }

        public static Book readFromDB(int id) {
            try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement mainStatement = con.createStatement()) {
                initializeDatabase(mainStatement);
                ResultSet book = mainStatement.executeQuery("SELECT * FROM library WHERE bookId=" + id + ";");
                while (book.next()) {
                    return new Book(book.getInt(1), book.getString(2), book.getInt(3));
                }

                return null;
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return new Book("Bílá nemoc");
        }

        public static void writeIntoDB(Book book) {
            try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement mainStatement = con.createStatement()) {
                initializeDatabase(mainStatement);
                int bookIdToSave = book.bookId == 0 ? getLastBookId(mainStatement) + 1 : book.bookId;
                mainStatement.executeUpdate("INSERT INTO library VALUES (" + bookIdToSave + ", '" + book.name + "', " + (book.authorId == 0 ? "NULL" : book.authorId) + ")");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        public static void deleteFromDB(String name) {
            try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement mainStatement = con.createStatement()) {
                initializeDatabase(mainStatement);
                mainStatement.executeUpdate("DELETE FROM library WHERE name='"+name+"';");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        private static void initializeDatabase(Statement statement) throws SQLException {
            //statement.executeUpdate("DROP TABLE IF EXISTS library;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS library (bookID INT PRIMARY KEY , name VARCHAR UNIQUE NOT NULL, authorId INT);");
        }

        private static int getLastBookId(Statement statement) throws SQLException {
            ResultSet rs = statement.executeQuery("SELECT MAX(bookId) FROM library;");
            while (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }
}
