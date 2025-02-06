import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class MyActionListener extends JFrame implements ActionListener {

    //I like to create a class for ease and structure
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            //add to cart
            if ("addToCartButton".equals(actionCommand)) {
               boolean loggedin = WebbShopSkor.checkIfLoggedIn();
                if (loggedin) {
                    List<String> selectedShoes = WebbShopSkor.checkShoeSelected();
                    for (int i = 0; i < selectedShoes.size(); i++) {
                        String selectedShoeName = selectedShoes.get(i);
                    try {
                        JDBC.addToCartDB(JDBC.getUserIDFromName(WebbShopSkor.checkUserName()), null, JDBC.getProduktIDByName(selectedShoeName));
                        WebbShopSkor.cartTextArea.setText("");
                        WebbShopSkor.userCartItems = JDBC.loadInUserCart(JDBC.getUserIDFromName(WebbShopSkor.checkUserName()));
                        for (String product : WebbShopSkor.userCartItems) {
                           WebbShopSkor.cartTextArea.append(product + "\n");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                        throw new RuntimeException(ex);
                    }
                }
               } else{
                   JOptionPane.showMessageDialog(this, "Please login first");
               }

                //Exits Program
            } else if ("exitButton".equals(actionCommand)) {
                System.exit(0);

                //Payment button
            } else if ("payButton".equals(actionCommand)) {
                try {
                    JDBC.payForOrder(JDBC.getUserIDFromName(WebbShopSkor.checkUserName()));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                //Login or create user
            } else if ("loginButton".equals(actionCommand)) {
                WebbShopSkor.username = JOptionPane.showInputDialog("Please enter your username: ");
                WebbShopSkor.password = JOptionPane.showInputDialog("Please enter your password: ");

                try {
                    JDBC.createOrLoginUser(WebbShopSkor.username,WebbShopSkor.password);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }