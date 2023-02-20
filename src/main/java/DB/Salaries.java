package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Salaries {
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String URL_STRING = "jdbc:sqlite:monkeys.dat";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement mainStatement = con.createStatement(); Statement secondaryStatement = con.createStatement();) {
            mainStatement.executeUpdate("DROP TABLE IF EXISTS monkey;");
            mainStatement.executeUpdate("CREATE TABLE monkey (name VARCHAR NOT NULL, month INT NOT NULL CHECK (month >= 1 AND month <= 12), year INT NOT NULL, salary FLOAT NOT NULL CHECK (salary > 0), CONSTRAINT my_constraint UNIQUE(name, month, year));");
            mainStatement.executeUpdate("INSERT INTO monkey VALUES ('jan',1,2023,10000), ('jan', 2, 2023, 20000), ('marie',1,2023,10000), ('marie',2,2023,10001);");


            ResultSet salaries = mainStatement.executeQuery("SELECT month, SUM(salary) FROM monkey GROUP BY month;");
            while (salaries.next()) {
                System.out.println("----------------------");
                System.out.println(salaries.getString(1));
                System.out.println(salaries.getFloat(2));
                System.out.println("----------------------");
            }

            System.out.println("===============================");

            ResultSet sal2 = mainStatement.executeQuery("SELECT DISTINCT name FROM monkey;");
            while (sal2.next()) {
                long count = 0;
                double salary = 0.0;
                String name = sal2.getString(1);
                ResultSet monkeySalaries = secondaryStatement.executeQuery("SELECT salary FROM monkey WHERE name='" + name + "';");
                while (monkeySalaries.next()) {
                    salary += monkeySalaries.getFloat(1);
                    count++;
                }

                salary = salary / count;

                System.out.println("----------------------");
                System.out.println(name);
                System.out.println(salary);
                System.out.println("----------------------");
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
