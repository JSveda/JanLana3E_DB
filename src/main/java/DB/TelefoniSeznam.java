package DB;

import java.sql.*;
import java.util.Scanner;

public class TelefoniSeznam {

    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String URL_STRING = "jdbc:sqlite:data.dat";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement statement = con.createStatement();) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (name TEXT not null on conflict abort);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS numbers (id INTEGER not null on conflict abort, tel INTEGER not null on conflict abort);");
        } catch (Exception e) {
            System.out.println("Něco se pokazilo při vytváření tabulek! Zkuste spustit program znovu...");
            return;
        }

        try (Scanner scanner = new Scanner(System.in);) {
            String command = "";
            try {
                command = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Program nemůže vyhodnotit tento vstup! Zkuste to znovu...");
            }

            String[] cmd = command.split(" ");
            if (cmd.length < 1) {
                System.out.println("Zřejmě jste zadali málo údajů, zkuste to prosím znovu...");
            }

            String output = "Program nemůže vyhodnotit tento vstup! Zkuste to znovu...";
            if (cmd[0].equals("add")) {
                if (cmd.length < 3) {
                    System.out.println("Zřejmě jste zadali málo údajů, zkuste to prosím znovu...");
                }
                output = add(cmd[1], cmd[2]);
            } else if (cmd[0].equals("find")) {
                if (cmd.length < 2) {
                    System.out.println("Zřejmě jste zadali málo údajů, zkuste to prosím znovu...");
                }
                output = find(cmd[1]);
            }

            System.out.println(output);
        } catch (Exception e) {
            System.out.println("Chyba programu, pokuste se ho znovu spustit!");
        }
    }

    private static String find(String searchInput) {
        if (searchInput.length() < 1 || searchInput.length() > 9 || !searchInput.matches("[0-9]+"))
            return "Vstupní data jsou ve špatném formátu! Zkuste je zadat znovu...";

        String allUsersWithNumber = "SELECT DISTINCT name FROM users JOIN numbers ON numbers.id=users.ROWID WHERE CAST(numbers.tel AS TEXT) LIKE '%" + searchInput + "%';";

        try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement statement = con.createStatement();) {
            StringBuilder names = new StringBuilder();
            try {
                ResultSet rs = statement.executeQuery(allUsersWithNumber);
                while (rs.next()) {
                    names.append(rs.getString("name")).append(" ");
                }
            } catch (SQLException e) {
                System.out.println(e);
                return "Chyba při hledání lidí podle hledaného vstupu! Zkuste to znovu...";
            }

            return (names.toString().isEmpty()) ? "Program nenašel pro tento vstup žádné výsledky..." : names.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            return "Chyba při hledání člověka! Zkuste to znovu...";
        }
    }

    private static String add(String name, String tel) {
        if (name.length() < 1 || tel.length() < 1 || tel.length() > 9 || !tel.matches("[0-9]+"))
            return "Vstupní data jsou ve špatném formátu! Zkuste je zadat znovu...";

        String addUserQuery = "INSERT INTO users VALUES ('" + name + "');";
        String telsInDBQuery = "SELECT n.tel FROM numbers AS n, users AS u WHERE (SELECT ROWID FROM users WHERE name='" + name + "')=n.id AND u.name='" + name + "';";
        String telIsInDBQuery = "SELECT n.tel FROM numbers AS n, users AS u WHERE (SELECT ROWID FROM users WHERE name='" + name + "')=n.id AND n.tel=" + tel + ";";
        String addTelQuery = "INSERT INTO numbers VALUES ((SELECT ROWID FROM users WHERE name='" + name + "'), " + tel + ");";

        String output = "Něco se pokazilo! Zkuste data zadat znovu...";
        try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement statement = con.createStatement();) {
            int telsNumberInDb = 0;
            try {
                ResultSet rs = statement.executeQuery(telsInDBQuery);
                while (rs.next()) {
                    telsNumberInDb++;
                }
            } catch (SQLException e) {
                System.out.println(e);
                return "Chyba při ověření počtu lidí v databázi! Zkuste to znovu...";
            }

            if (telsNumberInDb == 0) {
                try {
                    statement.execute(addUserQuery);
                } catch (SQLException e) {
                    System.out.println(e);
                    return "Chyba při ukládání člověka do databáze! Zkuste to znovu...";
                }
            }

            boolean telIsInDb = false;
            try {
                ResultSet rs = statement.executeQuery(telIsInDBQuery);
                while (rs.next()) {
                    String s = rs.getString("tel");
                    telIsInDb = true;
                }
            } catch (SQLException e) {
                System.out.println(e);
                return "Chyba při ověření existence telefoního čísla v databázi! Zkuste to znovu...";
            }

            if (!telIsInDb) {
                try {
                    statement.execute(addTelQuery);
                } catch (SQLException e) {
                    System.out.println(e);
                    return "Chyba při ukládání telefoního čísla! Zkuste to znovu...";
                }
            }

            if (telsNumberInDb != 0) {
                if (telIsInDb) output = telsNumberInDb + "";
                else output = (telsNumberInDb + 1) + "";
            } else output = "1";

        } catch (Exception e) {
            System.out.println(e.toString());
            return "Chyba při ukládání člověka do databáze! Zkuste to znovu...";
        }

        return output;
    }
}
