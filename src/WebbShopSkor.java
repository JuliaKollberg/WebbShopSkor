import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class WebbShopSkor extends JFrame {

    //Needed for other classes
    public static boolean loggedInBool = false;
    public static String username;
    public static String password;
    static List<String> userCartItems;

    //Main Panels
    static JPanel mainPanel = new JPanel();

    //Starting menu
    JPanel titlePanel = new JPanel();
    JPanel selectionPanel = new JPanel();
    JPanel productHolderPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel cartPanel = new JPanel();

    //title
    JLabel titleLabel = new JLabel("Webb Shop Skor");

    //Selection buttons
    public static Map<String, JCheckBox> checkBoxMap = new HashMap<>();
    JScrollPane productScroller = new JScrollPane(productHolderPanel);

    //buttons
    static JButton addToCartButton = new JButton("Add To Cart");
    JButton exitButton = new JButton("Exit");
    static JButton loginButton = new JButton("Login");

    //CartPanel
    JLabel cartLabel = new JLabel("    Your Cart    ");
    JButton payButton = new JButton("Pay");
    static JTextArea cartTextArea = new JTextArea("Log in to see");

    public WebbShopSkor() {

        //mainPanel
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        //titlePanel
        titlePanel.setLayout(new FlowLayout());
        titlePanel.setBackground(Color.gray);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 25));

        //selectionPanel
        selectionPanel.setLayout(new BorderLayout ());
        selectionPanel.setBackground(Color.LIGHT_GRAY);
        productHolderPanel.setLayout(new BoxLayout(productHolderPanel, BoxLayout.Y_AXIS));
        productScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        productHolderPanel.setBackground(Color.LIGHT_GRAY);

        for (String option : JDBC.listOfShoes) {
            JCheckBox optionCheckBox = new JCheckBox(option);
            optionCheckBox.setFont(new Font("Monospaced", Font.BOLD, 22));
            optionCheckBox.setSelected(false);
            optionCheckBox.setBackground(Color.LIGHT_GRAY);
            productHolderPanel.add(optionCheckBox);
            checkBoxMap.put(option, optionCheckBox);

            optionCheckBox.addItemListener(e -> {
                if (optionCheckBox.isSelected()) {
                    optionCheckBox.setBackground(Color.gray);
                } else {
                    optionCheckBox.setBackground(Color.LIGHT_GRAY);
                }
            });
        }

        //buttonPanel
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.darkGray);

        addToCartButton.setFont(new Font("Monospaced", Font.BOLD, 22));
        addToCartButton.setBackground(Color.darkGray);
        exitButton.setFont(new Font("Monospaced", Font.BOLD, 22));
        exitButton.setBackground(Color.lightGray);
        loginButton.setFont(new Font("Monospaced", Font.BOLD, 22));
        loginButton.setBackground(Color.lightGray);


        addToCartButton.addActionListener(new MyActionListener());
        addToCartButton.setActionCommand("addToCartButton");
        exitButton.addActionListener(new MyActionListener());
        exitButton.setActionCommand("exitButton");
        loginButton.addActionListener(new MyActionListener());
        loginButton.setActionCommand("loginButton");

        //CartPanel
        cartPanel.setLayout(new BorderLayout());
        cartPanel.setBackground(Color.darkGray);
        cartLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        cartLabel.setForeground(Color.WHITE);

        payButton.setFont(new Font("Monospaced", Font.BOLD, 22));
        payButton.setBackground(Color.lightGray);
        payButton.addActionListener(new MyActionListener());
        payButton.setActionCommand("payButton");

        cartTextArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        cartTextArea.setForeground(Color.BLACK);
        cartTextArea.setBackground(Color.lightGray);
        cartTextArea.setEditable(false);

        add(mainPanel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        selectionPanel.add(BorderLayout.CENTER,productScroller);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(cartPanel, BorderLayout.EAST);

        titlePanel.add(titleLabel);

        buttonPanel.add(loginButton);
        buttonPanel.add(addToCartButton);
        buttonPanel.add(exitButton);

        cartPanel.add(BorderLayout.NORTH, cartLabel);
        cartPanel.add(BorderLayout.SOUTH, payButton);
        JScrollPane scrollPane = new JScrollPane(cartTextArea);
        cartPanel.add(BorderLayout.CENTER, scrollPane);

        //End
        setTitle("WebbShopSkor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 600);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    //Better checker for if they can use addToCart
    public static boolean checkIfLoggedIn() {
        if (loggedInBool) {
            return true;
        } else {
            return false;
        }
    }

    public static String checkUserName(){
        return username;
    }

    public static void updateLoginTRUE() {
        loggedInBool = true;
    }

    //Checks which shoes the user has selected and forwards it to a List
    public static List<String> checkShoeSelected() {
        List<String> selectedShoes = new ArrayList<>();
        selectedShoes.clear();

        for (String option : JDBC.listOfShoes) {
            if (checkBoxMap.get(option).isSelected()) {
                selectedShoes.add(option);
            }
        }
        return selectedShoes;
    }

    //first loads in the data needed and then runs the visuals
    public static void main(String[] args) throws IOException {
        JDBC.getConfigForDB();
        JDBC.connectToDatabase();
        SwingUtilities.invokeLater(WebbShopSkor::new);
    }
}
