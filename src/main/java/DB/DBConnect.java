package DB;

import java.sql.*;
import java.util.Scanner;

public class DBConnect {
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String URL_STRING = "jdbc:sqlite::database.db";

    public static void main(String[] args) throws SQLException {
        mainOps();
    }

    private static void mainOps() {
        try(Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement statement = con.createStatement();) {
            // stmt.execute("INSERT INTO people(first_name, last_name) VALUES ('Jakub', 'Sveda')");

            Scanner sc = new Scanner(System.in);
            System.out.println("Jméno:");
            String name = sc.nextLine().replaceAll("\'", "''");
            System.out.println("Příjmení:");
            String surname = sc.nextLine().replaceAll("\'", "''");
            System.out.println("Věk");
            int age = sc.nextInt();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS clovek2 (jmeno VARCHAR, prijmeni VARCHAR, vek INT);");
            String sql = "INSERT INTO clovek2 VALUES ('"+name+"', '"+surname+"', "+age+");";
            System.out.println(sql);
            //statement.executeUpdate(sql);


            ResultSet rs = statement.executeQuery("SELECT * FROM clovek2;");

            while (rs.next()) {
                System.out.println(rs.getString("jmeno") + "\t" + rs.getString("prijmeni") + "\t" + rs.getInt("vek"));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void clearTable(String tableName) {
        
    }
}
