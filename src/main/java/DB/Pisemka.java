package DB;

import java.sql.*;
import java.util.Scanner;

public class Pisemka {
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String URL_STRING = "jdbc:sqlite:data.dat";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement statement = con.createStatement();) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS zak (jmeno TEXT not null on conflict abort, znamka INTEGER not null on conflict abort );");
        } catch (Exception e) {
            System.out.println("Něco se pokazilo při vytváření tabulek! Zkuste spustit program znovu...");
            return;
        }

        try (Scanner scanner = new Scanner(System.in);) {
            String input = "";
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Program nemůže vyhodnotit tento vstup! Zkuste to znovu...");
            }

            String[] words = input.split(" ");
            if (words.length < 2) {
                System.out.println("Zřejmě jste zadali málo údajů, zkuste to prosím znovu...");
                return;
            }

            words[0] = correctString(words[0]);
            words[1] = correctString(words[1]);

            if (!isNumber(words[1])) {
                System.out.println("Druhý údaj není číslo! Zkuste ho zadat znovu...");
                return;
            }

            String addZakQuery = "INSERT INTO zak VALUES ('" + words[0] + "', " + words[1] + ");";
            String getJmenaZaku = "SELECT DISTINCT jmeno FROM zak;";
            try (Connection con = DriverManager.getConnection(URL_STRING, USER, PASSWORD); Statement mainStatement = con.createStatement(); Statement secondaryStatement = con.createStatement();) {
                try {
                    mainStatement.execute(addZakQuery);
                } catch (SQLException e) {
                    System.out.println("Při ukládání žáka se vyskytla chyba, zkuste to prosím znovu...");
                    return;
                }

                try {
                    long prumer = 0;
                    ResultSet jmena = mainStatement.executeQuery(getJmenaZaku);
                    while (jmena.next()) {
                        double pocetZnamek;
                        String jmenoReal = jmena.getString("jmeno");
                        String jmeno = jmenoReal.replaceAll("\'", "\'\'");
                        try {
                            pocetZnamek = 0.0;
                            String getZnamky = "SELECT znamka FROM zak WHERE jmeno='" + jmeno + "';";
                            ResultSet znamky = secondaryStatement.executeQuery(getZnamky);
                            while (znamky.next()) {
                                pocetZnamek++;
                                prumer += Integer.parseInt(znamky.getString("znamka"));
                            }

                            prumer = Math.round(prumer / pocetZnamek);
                        } catch (SQLException e) {
                            System.out.println("Při zjišťování známek žáka se vyskytla chyba, zkuste to prosím znovu...");
                            return;
                        }
                        System.out.println(jmenoReal + " " + prumer);
                        prumer = 0;
                    }
                } catch (SQLException e) {
                    System.out.println("Při zjišťování jmen žáků se vyskytla chyba, zkuste to prosím znovu...");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Něco se pokazilo při přístupu k databázi...");
                return;
            }
        } catch (Exception e) {
            System.out.println("Chyba programu, pokuste se ho znovu spustit!");
            return;
        }
    }

    private static String correctString(String s) {
        StringBuilder newStr = new StringBuilder();
        char invalid1 = '\'';
        char invalid2 = '-';
        char invalid3 = ';';
        char invalid4 = '(';
        char invalid5 = ')';
        for (char ch : s.toCharArray()) {
            if (ch == invalid2 || ch == invalid3 || ch == invalid4 || ch == invalid5) {
                continue;
            } else if (ch == invalid1) {
                newStr.append(invalid1).append(invalid1);
            } else {
                newStr.append(ch);
            }
        }


        return newStr.toString();
    }

    private static boolean isNumber(String s) {
        return s != null && s.length() == 1 && s.matches("[0-9]+");
    }
}
