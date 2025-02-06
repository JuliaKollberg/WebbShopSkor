import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBC {

    static List<String> listOfShoes = new ArrayList<>();
    static String url ;
    static String user;
    static String password;

    //Add file in src with login for safety
    public static void getConfigForDB() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("src/ConfIgDONTSHARE"));
        url = reader.readLine().trim();
        user = reader.readLine().trim();
        password = reader.readLine().trim();
        reader.close();
    }

    //Connects and load in information
    public static void connectToDatabase() {
        String getShoesString = "select * from produkter ";
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(getShoesString)) {

            while (rs.next()) {
                listOfShoes.add(rs.getString("namn"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Loads in cart if there is one
    public static List<String> loadInUserCart(int kundID) throws SQLException {
        List<String> userCartProducts = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT produkter.namn " +
                             "FROM beställningar " +
                             "JOIN beställningsprodukter ON beställningar.beställningID = beställningsprodukter.beställningID " +
                             "JOIN produkter ON beställningsprodukter.produktID = produkter.produktID " +
                             "WHERE beställningar.kundID = ?")) {

            stmt.setInt(1, kundID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userCartProducts.add(rs.getString("namn"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading user cart", e);
        }
        return userCartProducts;
    }


    //Creates a new user or logs in one
    public static void createOrLoginUser(String andvändarNamn, String lösenord) throws SQLException {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "{CALL CreateOrLoginUser(?, ?, ?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setString(1, andvändarNamn);
                stmt.setString(2, lösenord);
                stmt.registerOutParameter(3, Types.INTEGER);
                stmt.registerOutParameter(4, Types.VARCHAR);

                stmt.execute();
                int kundID = stmt.getInt(3);
                String message = stmt.getString(4);
                if (kundID != 0) {
                    JOptionPane.showMessageDialog(null, message + "\nWelcome " + andvändarNamn);

                    WebbShopSkor.loginButton.setBackground(Color.darkGray);
                    WebbShopSkor.addToCartButton.setBackground(Color.lightGray);

                    WebbShopSkor.updateLoginTRUE();

                    WebbShopSkor.cartTextArea.setText("");
                    WebbShopSkor.userCartItems = JDBC.loadInUserCart(JDBC.getUserIDFromName(WebbShopSkor.checkUserName()));
                    for (String product : WebbShopSkor.userCartItems) {
                        WebbShopSkor.cartTextArea.append(product + "\n");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong password or Username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error occurred.");
        }
    }

    //Adds the pruducts to the cart, Takes in the customerID, order ID and what pruduct, then adds all the information to the SP
    public static void addToCartDB(int kundID, Integer beställningID, int produktID) throws SQLException {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "{CALL AddToCart(?, ?, ?)}";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, kundID);
                if (beställningID == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, beställningID);
                }
                stmt.setInt(3, produktID);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fel vid att lägga till produkt i varukorg.");
            }
        }
    }

    //Inputs the users input String and gets the ID
    public static int getUserIDFromName(String andvändarNamn) throws SQLException{
        int kundID = -1;
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT kundID FROM kunder WHERE andvändarNamn = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, andvändarNamn);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        kundID = rs.getInt("kundID");
                    } else {
                        System.out.println("User not found!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching kundID from database.");
        }
        return kundID;
    }

    //Inputs the products name and finds the ID
    public static int getProduktIDByName(String namn) throws SQLException {
        int produktID = -1;
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT produktID FROM produkter WHERE namn = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, namn);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        produktID = rs.getInt("produktID");
                    } else {
                        System.out.println("Product not found!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching produktID from database.");
        }
        return produktID;
    }

    //With the customerID if finds a matching order and sets it as paid
    public static void payForOrder(int kundID) throws SQLException {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "UPDATE beställningar SET betald = TRUE WHERE kundID = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, kundID);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Betalning Godkänd");
                    WebbShopSkor.cartTextArea.setText("");
                } else {
                    System.out.println("No order");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}