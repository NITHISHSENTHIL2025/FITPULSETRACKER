import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// [CO5] Inheritance: Extending JFrame to create the main application window
public class FitnessTrackerApp extends JFrame {

    // --- PROFESSIONAL COLOR PALETTE ---
    private static final Color SIDEBAR_BG = new Color(44, 62, 80);
    private static final Color MAIN_BG = new Color(236, 240, 241);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_HOVER = new Color(41, 128, 185);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);

    // --- FONTS ---
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- VARIABLES ---
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private User currentUser;
    private DatabaseHelper dbHelper;

    // Panel Identifiers
    private static final String LOGIN_PANEL = "LOGIN";
    private static final String HOME_PANEL = "HOME";
    private static final String MY_DATA_PANEL = "DATA";
    private static final String EXERCISE_CAT_PANEL = "EXERCISE_CAT";
    private static final String WORKOUT_PANEL = "WORKOUT";
    private static final String MANUAL_ENTRY_PANEL = "MANUAL_ENTRY";
    private static final String FOOD_PANEL = "FOOD";
    private static final String SETTINGS_PANEL = "SETTINGS";
    private static final String STATISTICS_PANEL = "STATISTICS";

    // Instances
    private LoginPanel loginPanel;
    private HomePanel homePanel;
    private MyDataPanel myDataPanel;
    private ExerciseCategoryPanel exerciseCategoryPanel;
    private WorkoutSessionPanel workoutSessionPanel;
    private ManualEntryPanel manualEntryPanel;
    private FoodPanel foodPanel;
    private SettingsPanel settingsPanel;
    private StatisticsPanel statisticsPanel;

    // [CO1] Application of fundamental programming constructs (Main Method entry point)
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
        } catch (Exception ignored) {}
        // [CO6] Functional Programming construct (Lambda Expression) for thread safety
        SwingUtilities.invokeLater(() -> new FitnessTrackerApp().setVisible(true));
    }

    // [CO4] Constructor: modular initialization of the application
    public FitnessTrackerApp() {
        setTitle("FitnessTracker - Professional Tracker");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(MAIN_BG);

        dbHelper = new DatabaseHelper();
        dbHelper.initTables();

        setLayout(new BorderLayout());

        // --- SIDEBAR MENU ---
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(250, 800));
        menuPanel.setBackground(SIDEBAR_BG);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 30));
        brandPanel.setBackground(SIDEBAR_BG);
        JLabel brand = new JLabel("FitnessTracker");
        brand.setForeground(Color.WHITE);
        brand.setFont(FONT_TITLE);
        brandPanel.add(brand);
        menuPanel.add(brandPanel);
        menuPanel.add(Box.createVerticalStrut(30));

        // --- MAIN CONTENT AREA ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(MAIN_BG);

        // Init Panels
        loginPanel = new LoginPanel();
        homePanel = new HomePanel();
        myDataPanel = new MyDataPanel();
        exerciseCategoryPanel = new ExerciseCategoryPanel();
        workoutSessionPanel = new WorkoutSessionPanel();
        manualEntryPanel = new ManualEntryPanel();
        foodPanel = new FoodPanel();
        settingsPanel = new SettingsPanel();
        statisticsPanel = new StatisticsPanel();

        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(homePanel, HOME_PANEL);
        mainPanel.add(myDataPanel, MY_DATA_PANEL);
        mainPanel.add(exerciseCategoryPanel, EXERCISE_CAT_PANEL);
        mainPanel.add(workoutSessionPanel, WORKOUT_PANEL);
        mainPanel.add(manualEntryPanel, MANUAL_ENTRY_PANEL);
        mainPanel.add(foodPanel, FOOD_PANEL);
        mainPanel.add(settingsPanel, SETTINGS_PANEL);
        mainPanel.add(statisticsPanel, STATISTICS_PANEL);

        // Add Menu Items
        addMenuButton("Dashboard", HOME_PANEL);
        addMenuButton("My Statistics", MY_DATA_PANEL);
        addMenuButton("Workout Timer", EXERCISE_CAT_PANEL);
        addMenuButton("Log Activity", MANUAL_ENTRY_PANEL);
        addMenuButton("Nutrition", FOOD_PANEL);
        addMenuButton("Analytics (History)", STATISTICS_PANEL);

        menuPanel.add(Box.createVerticalGlue());
        addMenuButton("Settings", SETTINGS_PANEL);
        menuPanel.add(Box.createVerticalStrut(30));

        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        menuPanel.setVisible(false);
        navigateTo(LOGIN_PANEL);
    }

    private void addMenuButton(String text, String panelName) {
        ModernButton btn = new ModernButton(text, SIDEBAR_BG, new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_NORMAL);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 30, 12, 10));
        btn.setMaximumSize(new Dimension(250, 55));
        // [CO6] Functional Interface (ActionListener) implemented via Lambda
        btn.addActionListener(e -> navigateTo(panelName));
        menuPanel.add(btn);
    }

    private void setNavigationVisible(boolean visible) {
        menuPanel.setVisible(visible);
        revalidate();
        repaint();
    }


    private void navigateTo(String panelName) {
        cardLayout.show(mainPanel, panelName);
        switch (panelName) {
            case HOME_PANEL -> homePanel.updateWelcome();
            // [CO5] Polymorphism: Calling refreshData() which behaves differently per panel
            case MY_DATA_PANEL -> myDataPanel.refreshData();
            case FOOD_PANEL -> foodPanel.refreshData();
            case SETTINGS_PANEL -> settingsPanel.refreshData();
            case STATISTICS_PANEL -> statisticsPanel.refreshData();
            case MANUAL_ENTRY_PANEL -> manualEntryPanel.resetView();
        }
    }

    // ================= CUSTOM POPUP LOGIC =================
    private void showMotivationalPopup(String title, String message, String emoji) {
        JDialog dialog = new JDialog(this, "Motivation", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0,0,0,0));

        // [CO5] Anonymous Inner Class extending JPanel for custom painting
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,50));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 20, 20);
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(30, 30, 30, 30));
        content.setBackground(new Color(0,0,0,0));

        JLabel iconLbl = new JLabel(emoji, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(TEXT_PRIMARY);

        JTextArea msgArea = new JTextArea(message);
        msgArea.setWrapStyleWord(true);
        msgArea.setLineWrap(true);
        msgArea.setEditable(false);
        msgArea.setOpaque(false);
        msgArea.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        msgArea.setForeground(TEXT_SECONDARY);
        msgArea.setBorder(new EmptyBorder(15, 10, 15, 10));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(iconLbl);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(titleLbl);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(msgArea);

        // Uses Overloaded Constructor (if we added it) or standard
        ModernButton closeBtn = new ModernButton("Let's Go!", ACCENT_COLOR, ACCENT_HOVER);
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(closeBtn);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ================= HELPER CLASSES (Demonstrating OOP) =================

    // [CO5] Inheritance: Creating a custom button component
    static class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        // [CO5] Method Overloading: Constructor 1 (Standard)
        public ModernButton(String text, Color normal, Color hover) {
            super(text);
            this.normalColor = normal;
            this.hoverColor = hover;
            initButton();
        }

        // [CO5] Method Overloading: Constructor 2 (Simplified, uses default blue)
        public ModernButton(String text) {
            super(text);
            this.normalColor = new Color(52, 152, 219); // Default Blue
            this.hoverColor = new Color(41, 128, 185);
            initButton();
        }

        private void initButton() {
            setBackground(normalColor);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setOpaque(true);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); }
                public void mouseExited(MouseEvent e) { setBackground(normalColor); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    static class ModernCard extends JPanel {
        public ModernCard() {
            setBackground(CARD_BG);
            setBorder(new CompoundBorder(new LineBorder(new Color(220, 224, 228), 1), new EmptyBorder(25, 25, 25, 25)));
        }
    }

    static class SmoothProgressBar extends JProgressBar {
        public SmoothProgressBar(int min, int max) {
            super(min, max);
            setUI(new BasicProgressBarUI() {
                protected void paintDeterminate(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = c.getWidth(); int h = c.getHeight();
                    int fillW = (int) (w * getPercentComplete());
                    g2.setColor(new Color(230, 230, 230));
                    g2.fillRoundRect(0, 0, w, h, 10, 10);
                    g2.setColor(ACCENT_COLOR);
                    g2.fillRoundRect(0, 0, fillW, h, 10, 10);
                }
            });
            setBorderPainted(false);
            setBackground(Color.WHITE);
        }
        public void setTargetValue(int target) {
            setValue(target);
        }
    }

    // [CO5] Abstract Class: Enforcing 'refreshData' implementation in all subclasses
    abstract static class BasePanel extends JPanel {
        abstract void refreshData();
    }

    // [CO4] Encapsulation: User entity with fields and methods
    static class User {
        int id; String username; String name; int age; double height; double weight; double goalWeight;
        public int getProteinGoal() { return (int) Math.round(weight * 1.8); }
    }

    // [CO4] Class and Object: Representing a food item
    static class FoodItem {
        double protein; double carbs; double fat; double calories;
        public FoodItem(double p, double c, double f, double cal) {
            this.protein = p; this.carbs = c; this.fat = f; this.calories = cal;
        }
    }

    static class DataUtils {
        // [CO3] Recursion: Method to calculate streak score recursively
        public static int recursiveStreakScore(int days) {
            if (days <= 0) return 0; // Base case
            return 10 + recursiveStreakScore(days - 1); // Recursive call
        }

        // [CO2] Algorithm: Sorting logic using 1D Arrays (Bubble Sort)
        public static int getLongestWorkoutDuration(ArrayList<Integer> durationsList) {
            int[] arr = new int[durationsList.size()];
            for (int i = 0; i < durationsList.size(); i++) {
                arr[i] = durationsList.get(i);
            }

            int n = arr.length;
            if (n == 0) return 0;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (arr[j] > arr[j + 1]) {
                        int temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            }
            return arr[n - 1];
        }

        // [CO6] Generics and Collections (Map)
        public static Map<String, String[]> getExerciseMap() {
            Map<String, String[]> map = new HashMap<>();
            map.put("Legs", new String[]{"Barbell Squats", "Leg Press", "Lunges", "Calf Raises", "Leg Extensions", "Romanian Deadlift"});
            map.put("Chest", new String[]{"Bench Press", "Incline Dumbbell Press", "Cable Flys", "Push-ups", "Dips", "Chest Press"});
            map.put("Back", new String[]{"Deadlift", "Pull-Ups", "Lat Pulldown", "Barbell Rows", "Face Pulls", "Seated Cable Row"});
            map.put("Arms", new String[]{"Bicep Curls", "Tricep Extensions", "Hammer Curls", "Skull Crushers", "Preacher Curls"});
            map.put("Abs", new String[]{"Crunches", "Plank", "Leg Raises", "Russian Twists", "Bicycle Crunches"});
            map.put("Shoulders", new String[]{"Overhead Press", "Lateral Raises", "Front Raises", "Shrugs", "Rear Delt Fly"});
            map.put("Cardio", new String[]{"Treadmill Run", "Outdoor Run", "Cycling", "Elliptical", "Jump Rope", "Rowing", "Swimming", "Stair Climber"});
            return map;
        }
    }

    class DatabaseHelper {
        private static final String DB_URL = "jdbc:sqlite:fitness_tracker.db";

        // [CO6] Robustness: Exception Handling (try-catch) and JDBC connection
        private Connection connect() {
            try {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection(DB_URL);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void initTables() {
            try (Connection c = connect(); Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, name TEXT, age INTEGER, height REAL, weight REAL, goal_weight REAL)");
                s.execute("CREATE TABLE IF NOT EXISTS workouts (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, date TEXT, exercise_type TEXT, duration_seconds INTEGER, details TEXT)");
                s.execute("CREATE TABLE IF NOT EXISTS meals (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, date TEXT, food_name TEXT, protein REAL, calories REAL)");
            } catch (SQLException e) { e.printStackTrace(); }
        }

        public boolean register(String u, String p, String n, int a, double h, double w, double gw) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("INSERT INTO users(username, password, name, age, height, weight, goal_weight) VALUES(?,?,?,?,?,?,?)")) {
                ps.setString(1, u); ps.setString(2, p); ps.setString(3, n); ps.setInt(4, a); ps.setDouble(5, h); ps.setDouble(6, w); ps.setDouble(7, gw);
                ps.executeUpdate(); return true;
            } catch (SQLException e) { return false; }
        }

        public User login(String u, String p) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
                ps.setString(1, u); ps.setString(2, p); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    User user = new User(); user.id = rs.getInt("id"); user.username = rs.getString("username"); user.name = rs.getString("name");
                    user.age = rs.getInt("age"); user.height = rs.getDouble("height"); user.weight = rs.getDouble("weight"); user.goalWeight = rs.getDouble("goal_weight");
                    return user;
                }
            } catch (SQLException e) { e.printStackTrace(); } return null;
        }

        public void updateUser(User u) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("UPDATE users SET name=?, age=?, height=?, weight=?, goal_weight=? WHERE id=?")) {
                ps.setString(1, u.name); ps.setInt(2, u.age); ps.setDouble(3, u.height); ps.setDouble(4, u.weight); ps.setDouble(5, u.goalWeight); ps.setInt(6, u.id); ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        // [CO5] Method Overloading 1: Full Parameters
        public void logWorkout(int uid, String t, int s, String d) {
            String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("INSERT INTO workouts(user_id, date, exercise_type, duration_seconds, details) VALUES(?, ?, ?, ?, ?)")) {
                ps.setInt(1, uid);
                ps.setString(2, dateNow);
                ps.setString(3, t);
                ps.setInt(4, s);
                ps.setString(5, d);
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        // [CO5] Method Overloading 2: Simplified (Auto-generates details)
        public void logWorkout(int uid, String t, int s) {
            logWorkout(uid, t, s, "Quick Log - No Details Provided");
        }

        public void logMeal(int uid, String f, double p, double cals) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("INSERT INTO meals(user_id, date, food_name, protein, calories) VALUES(?, date('now','localtime'), ?, ?, ?)")) {
                ps.setInt(1, uid); ps.setString(2, f); ps.setDouble(3, p); ps.setDouble(4, cals); ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        public void deleteMeal(int mealId) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM meals WHERE id=?")) {
                ps.setInt(1, mealId); ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        public int getStreakDays(int uid) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(DISTINCT date(date)) as streak FROM workouts WHERE user_id=?")) {
                ps.setInt(1, uid); ResultSet rs = ps.executeQuery(); if(rs.next()) return rs.getInt("streak");
            } catch (SQLException e) { e.printStackTrace(); } return 0;
        }

        public double getTodayProtein(int uid) {
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT SUM(protein) as total FROM meals WHERE user_id=? AND date=date('now','localtime')")) {
                ps.setInt(1, uid); ResultSet rs = ps.executeQuery(); if(rs.next()) return rs.getDouble("total");
            } catch (SQLException e) { e.printStackTrace(); } return 0.0;
        }

        public ArrayList<Object[]> getTodayMealsRaw(int uid) {
            ArrayList<Object[]> list = new ArrayList<>();
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT id, food_name, protein, calories FROM meals WHERE user_id=? AND date=date('now','localtime')")) {
                ps.setInt(1, uid); ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    list.add(new Object[]{rs.getInt("id"), rs.getString("food_name"), rs.getDouble("protein"), rs.getDouble("calories")});
                }
            } catch(SQLException e) { e.printStackTrace(); }
            return list;
        }

        public ArrayList<Object[]> getWorkoutHistory(int uid) {
            ArrayList<Object[]> list = new ArrayList<>();
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT date, exercise_type, duration_seconds, details FROM workouts WHERE user_id=? ORDER BY id DESC LIMIT 50")) {
                ps.setInt(1, uid); ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    int seconds = rs.getInt("duration_seconds");
                    String durStr = (seconds / 60) + "m " + (seconds % 60) + "s";
                    list.add(new Object[]{rs.getString("date"), rs.getString("exercise_type"), durStr, rs.getString("details")});
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
            return list;
        }

        public ArrayList<Integer> getWorkoutDurations(int uid) {
            ArrayList<Integer> list = new ArrayList<>();
            try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT duration_seconds FROM workouts WHERE user_id=?")) {
                ps.setInt(1, uid); ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    list.add(rs.getInt("duration_seconds"));
                }
            } catch(SQLException e) { e.printStackTrace(); }
            return list;
        }
    }

    // ================= UI PANELS =================

    class LoginPanel extends JPanel {
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        public LoginPanel() {
            setLayout(new GridBagLayout());
            setBackground(MAIN_BG);
            GridBagConstraints gbcMain = new GridBagConstraints();
            gbcMain.gridx = 0;
            gbcMain.insets = new Insets(10, 10, 5, 10);

            JLabel titleLabel = new JLabel("FitnessTracker Login");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            titleLabel.setForeground(TEXT_PRIMARY);
            gbcMain.gridy = 0;
            add(titleLabel, gbcMain);

            JPanel card = new ModernCard();
            card.setLayout(new GridBagLayout());
            card.setPreferredSize(new Dimension(450, 380));

            GridBagConstraints gbcCard = new GridBagConstraints();
            gbcCard.insets = new Insets(10, 10, 10, 10);

            JLabel subTitle = new JLabel("Welcome Back");
            subTitle.setFont(FONT_HEADER);
            subTitle.setForeground(TEXT_SECONDARY);

            gbcCard.gridx = 0; gbcCard.gridy = 0; gbcCard.gridwidth = 2;
            card.add(subTitle, gbcCard);

            gbcCard.gridwidth = 1; gbcCard.anchor = GridBagConstraints.WEST;
            gbcCard.gridy = 1; card.add(createLabel("Username"), gbcCard);
            gbcCard.gridx = 1; card.add(styleField(userField), gbcCard);

            gbcCard.gridx = 0; gbcCard.gridy = 2; card.add(createLabel("Password"), gbcCard);
            gbcCard.gridx = 1; card.add(styleField(passField), gbcCard);

            JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            btnPanel.setBackground(CARD_BG);

            ModernButton loginBtn = new ModernButton("Login", ACCENT_COLOR, ACCENT_HOVER);
            ModernButton regBtn = new ModernButton("Register", new Color(149, 165, 166), new Color(127, 140, 141));

            btnPanel.add(loginBtn); btnPanel.add(regBtn);
            gbcCard.gridx = 0; gbcCard.gridy = 3; gbcCard.gridwidth = 2; gbcCard.fill = GridBagConstraints.HORIZONTAL; gbcCard.insets = new Insets(25, 10, 10, 10);
            card.add(btnPanel, gbcCard);

            gbcMain.gridy = 1;
            gbcMain.insets = new Insets(20, 10, 10, 10);
            add(card, gbcMain);

            loginBtn.addActionListener(e -> {
                String u = userField.getText(); String p = new String(passField.getPassword());
                currentUser = dbHelper.login(u, p);
                if (currentUser != null) {
                    userField.setText(""); passField.setText("");
                    homePanel.updateWelcome();
                    setNavigationVisible(true);
                    navigateTo(HOME_PANEL);
                } else JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            });
            regBtn.addActionListener(e -> showRegDialog());
        }

        private JLabel createLabel(String text) {
            JLabel l = new JLabel(text); l.setFont(FONT_NORMAL); l.setForeground(TEXT_SECONDARY); return l;
        }
        private JTextField styleField(JTextField f) {
            f.setFont(FONT_NORMAL); f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200,200,200)), new EmptyBorder(8,8,8,8))); return f;
        }
        private void showRegDialog() {
            JTextField u = new JTextField(); JPasswordField p = new JPasswordField(); JTextField n = new JTextField();
            JTextField a = new JTextField(); JTextField h = new JTextField(); JTextField w = new JTextField(); JTextField gw = new JTextField();
            Object[] msg = {"Username:", u, "Password:", p, "Name:", n, "Age:", a, "Height (cm):", h, "Weight (kg):", w, "Goal (kg):", gw};
            if (JOptionPane.showConfirmDialog(null, msg, "Create Account", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    if(dbHelper.register(u.getText(), new String(p.getPassword()), n.getText(), Integer.parseInt(a.getText()), Double.parseDouble(h.getText()), Double.parseDouble(w.getText()), Double.parseDouble(gw.getText())))
                        JOptionPane.showMessageDialog(this, "Account created!");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid inputs"); }
            }
        }
    }

    class HomePanel extends JPanel {
        JLabel welcomeLabel;
        public HomePanel() {
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);
            setBorder(new EmptyBorder(40, 50, 40, 50));

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(MAIN_BG);
            header.setBorder(new EmptyBorder(0, 0, 40, 0));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(MAIN_BG);

            welcomeLabel = new JLabel("Welcome Back");
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            welcomeLabel.setForeground(TEXT_PRIMARY);

            JLabel sub = new JLabel("Here is your daily activity overview.");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            sub.setForeground(TEXT_SECONDARY);

            textPanel.add(welcomeLabel);
            textPanel.add(Box.createVerticalStrut(8));
            textPanel.add(sub);
            header.add(textPanel, BorderLayout.CENTER);
            add(header, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(2, 2, 30, 30));
            grid.setBackground(MAIN_BG);
            grid.setBorder(new EmptyBorder(10, 40, 10, 40));

            grid.add(createInteractiveCard("Start Workout", "Choose your muscle group", "💪",
                    "\"The only bad workout is the one that didn't happen.\"\n\nPush yourself today!"));

            grid.add(createInteractiveCard("Log Nutrition", "Track calories and macros", "🥗",
                    "\"You can't out-train a bad diet.\"\n\nFuel your body with the right nutrients."));

            grid.add(createInteractiveCard("View Analytics", "Check your progress", "📈",
                    "\"What gets measured, gets managed.\"\n\nConsistency is the key to success."));

            grid.add(createInteractiveCard("Update Goals", "Manage your profile", "⚙",
                    "\"Set your goals high, and don't stop till you get there.\"\n\nAdjust your path, not the goal."));

            add(grid, BorderLayout.CENTER);
        }

        private JPanel createInteractiveCard(String title, String desc, String icon, String motivation) {
            JPanel p = new ModernCard();
            p.setLayout(new BorderLayout());
            p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel i = new JLabel(icon);
            i.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            i.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel t = new JLabel(title);
            t.setFont(FONT_HEADER);
            t.setForeground(TEXT_PRIMARY);
            t.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel d = new JLabel(desc);
            d.setFont(FONT_NORMAL);
            d.setForeground(TEXT_SECONDARY);
            d.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel center = new JPanel(new GridLayout(2,1));
            center.setBackground(CARD_BG);
            center.add(t);
            center.add(d);

            p.add(i, BorderLayout.NORTH);
            p.add(center, BorderLayout.CENTER);

            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(title.contains("Analytics")) navigateTo(STATISTICS_PANEL);
                    else if(title.contains("Nutrition")) navigateTo(FOOD_PANEL);
                    else if(title.contains("Workout")) navigateTo(EXERCISE_CAT_PANEL);
                    else if(title.contains("Goals")) navigateTo(SETTINGS_PANEL);
                    else showMotivationalPopup(title, motivation, icon);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    p.setBorder(new CompoundBorder(new LineBorder(ACCENT_COLOR, 2), new EmptyBorder(24, 24, 24, 24)));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    p.setBorder(new CompoundBorder(new LineBorder(new Color(220, 224, 228), 1), new EmptyBorder(25, 25, 25, 25)));
                }
            });
            return p;
        }
        public void updateWelcome() { if(currentUser!=null) welcomeLabel.setText("Hello, " + currentUser.name); }
    }

    class FoodPanel extends BasePanel {
        SmoothProgressBar progressBar;
        JComboBox<String> foodCombo;
        JTextField gramsField, searchField;
        // [CO6] Use of TreeMap for sorted keys (robust collections)
        Map<String, FoodItem> foodData = new TreeMap<>();
        JLabel goalL, currL;
        JLabel infoProtein, infoCarbs, infoFats, infoCals;
        JTable historyTable;
        DefaultTableModel tableModel;

        public FoodPanel() {
            initFoodDatabase(); // Loads 100+ items
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);
            setBorder(new EmptyBorder(20, 20, 20, 20));

            // --- TOP: SUMMARY STATS ---
            JPanel statsPanel = new ModernCard();
            statsPanel.setLayout(new BorderLayout());
            statsPanel.setPreferredSize(new Dimension(1000, 100));

            JPanel textStats = new JPanel(new GridLayout(1, 2));
            textStats.setBackground(CARD_BG);
            goalL = new JLabel("Goal: 0g Protein");
            goalL.setFont(FONT_BOLD);
            currL = new JLabel("Current: 0g");
            currL.setFont(FONT_HEADER);
            currL.setForeground(ACCENT_COLOR);
            textStats.add(goalL);
            textStats.add(currL);

            progressBar = new SmoothProgressBar(0, 100);
            progressBar.setPreferredSize(new Dimension(100, 15));

            statsPanel.add(textStats, BorderLayout.CENTER);
            statsPanel.add(progressBar, BorderLayout.SOUTH);

            add(statsPanel, BorderLayout.NORTH);

            // --- CENTER: SPLIT PANE (INPUT LEFT, HISTORY RIGHT) ---
            JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            contentPanel.setBackground(MAIN_BG);
            contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            // 1. LEFT SIDE: INPUT FORM
            JPanel inputCard = new ModernCard();
            inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));

            JLabel inputTitle = new JLabel("Log Meal");
            inputTitle.setFont(FONT_HEADER);
            inputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Search Bar
            JLabel searchLbl = new JLabel("Search Food:");
            searchLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            searchField = new JTextField();
            searchField.setMaximumSize(new Dimension(2000, 35));
            searchField.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    filterFoodList(searchField.getText());
                }
            });

            // Dropdown
            JLabel selectLbl = new JLabel("Select Item:");
            selectLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            foodCombo = new JComboBox<>(foodData.keySet().toArray(new String[0]));
            foodCombo.setMaximumSize(new Dimension(2000, 35));
            foodCombo.addActionListener(e -> updateInfoLabels());

            // Grams
            JLabel gramsLbl = new JLabel("Quantity (grams/ml):");
            gramsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            gramsField = new JTextField();
            gramsField.setMaximumSize(new Dimension(2000, 35));

            // Info Box
            JPanel infoBox = new JPanel(new GridLayout(2, 2, 5, 5));
            infoBox.setBackground(new Color(245, 245, 245));
            infoBox.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10)));
            infoBox.setMaximumSize(new Dimension(2000, 80));
            infoProtein = new JLabel("P: -"); infoCarbs = new JLabel("C: -");
            infoFats = new JLabel("F: -"); infoCals = new JLabel("Kcal: -");
            infoBox.add(infoProtein); infoBox.add(infoCarbs);
            infoBox.add(infoFats); infoBox.add(infoCals);

            // Buttons
            ModernButton addBtn = new ModernButton("Add Entry", ACCENT_COLOR, ACCENT_HOVER);
            addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            addBtn.setMaximumSize(new Dimension(2000, 40));

            // Layout Adding
            inputCard.add(inputTitle); inputCard.add(Box.createVerticalStrut(20));
            inputCard.add(searchLbl); inputCard.add(searchField); inputCard.add(Box.createVerticalStrut(10));
            inputCard.add(selectLbl); inputCard.add(foodCombo); inputCard.add(Box.createVerticalStrut(10));
            inputCard.add(gramsLbl); inputCard.add(gramsField); inputCard.add(Box.createVerticalStrut(20));
            inputCard.add(infoBox); inputCard.add(Box.createVerticalStrut(20));
            inputCard.add(addBtn);

            // 2. RIGHT SIDE: HISTORY TABLE
            JPanel historyCard = new ModernCard();
            historyCard.setLayout(new BorderLayout());

            JLabel histTitle = new JLabel("Today's Intake");
            histTitle.setFont(FONT_HEADER);
            histTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

            tableModel = new DefaultTableModel(new String[]{"ID", "Food", "Prot(g)", "Kcal"}, 0);
            historyTable = new JTable(tableModel);
            historyTable.setRowHeight(25);
            historyTable.setShowGrid(false);
            historyTable.setIntercellSpacing(new Dimension(0, 0));
            historyTable.getTableHeader().setFont(FONT_BOLD);

            // Hide ID Column
            historyTable.getColumnModel().getColumn(0).setMinWidth(0);
            historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
            historyTable.getColumnModel().getColumn(0).setWidth(0);

            JScrollPane scroll = new JScrollPane(historyTable);
            scroll.getViewport().setBackground(Color.WHITE);
            scroll.setBorder(BorderFactory.createEmptyBorder());

            ModernButton delBtn = new ModernButton("Remove Selected", DANGER_COLOR, new Color(192, 57, 43));

            historyCard.add(histTitle, BorderLayout.NORTH);
            historyCard.add(scroll, BorderLayout.CENTER);
            historyCard.add(delBtn, BorderLayout.SOUTH);

            contentPanel.add(inputCard);
            contentPanel.add(historyCard);
            add(contentPanel, BorderLayout.CENTER);

            // --- ACTIONS ---
            addBtn.addActionListener(e -> {
                if (currentUser == null) return;
                try {
                    String f = (String) foodCombo.getSelectedItem();
                    if (f == null) return;
                    double g = Double.parseDouble(gramsField.getText());
                    FoodItem item = foodData.get(f);

                    double p = (item.protein / 100.0) * g;
                    double cals = (item.calories / 100.0) * g;

                    dbHelper.logMeal(currentUser.id, f, p, cals);
                    gramsField.setText("");
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Logged: " + f);
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Enter valid grams!"); }
            });

            delBtn.addActionListener(e -> {
                int row = historyTable.getSelectedRow();
                if(row != -1) {
                    int id = Integer.parseInt((String)tableModel.getValueAt(row, 0));
                    dbHelper.deleteMeal(id);
                    refreshData();
                }
            });

            updateInfoLabels();
        }

        // [CO3] String manipulation: Filtering list based on search query
        private void filterFoodList(String query) {
            foodCombo.removeAllItems();
            String q = query.toLowerCase();
            for (String food : foodData.keySet()) {
                if (food.toLowerCase().contains(q)) {
                    foodCombo.addItem(food);
                }
            }
            if (foodCombo.getItemCount() > 0) foodCombo.setSelectedIndex(0);
        }

        private void updateInfoLabels() {
            String f = (String) foodCombo.getSelectedItem();
            if(f != null) {
                FoodItem i = foodData.get(f);
                infoProtein.setText(String.format("P: %.1fg", i.protein));
                infoCarbs.setText(String.format("C: %.1fg", i.carbs));
                infoFats.setText(String.format("F: %.1fg", i.fat));
                infoCals.setText(String.format("Kcal: %.0f", i.calories));
            }
        }

        @Override
        public void refreshData() {
            if(currentUser==null) return;
            double g = currentUser.getProteinGoal();
            double c = dbHelper.getTodayProtein(currentUser.id);

            goalL.setText("Goal: " + (int)g + "g Protein");
            currL.setText("Today: " + (int)c + "g");

            // Calculate progress percentage carefully
            int percent = (g > 0) ? (int)((c/g)*100) : 0;
            progressBar.setTargetValue(Math.min(percent, 100)); // Cap at 100 for bar

            ArrayList<Object[]> rawList = dbHelper.getTodayMealsRaw(currentUser.id);
            tableModel.setRowCount(0);
            for (Object[] row : rawList) {
                tableModel.addRow(new String[]{
                        String.valueOf(row[0]),
                        (String)row[1],
                        String.format("%.1f", row[2]),
                        String.format("%.0f", row[3])
                });
            }
        }

        // --- MASSIVE DATABASE ---
        private void initFoodDatabase() {
            // Proteins
            addFood("Chicken Breast (Raw)", 23, 0, 1.2, 110);
            addFood("Chicken Breast (Cooked)", 31, 0, 3.6, 165);
            addFood("Chicken Thigh", 16, 0, 15, 209);
            addFood("Egg (Whole, Large)", 13, 1.1, 11, 155);
            addFood("Egg White", 11, 0.7, 0.2, 52);
            addFood("Salmon", 20, 0, 13, 208);
            addFood("Tuna (Canned in Water)", 25, 0, 1, 116);
            addFood("Beef (Ground, 85%)", 26, 0, 15, 250);
            addFood("Steak (Sirloin)", 27, 0, 10, 244);
            addFood("Pork Chop", 24, 0, 14, 231);
            addFood("Turkey Breast", 29, 0, 1, 135);
            addFood("Shrimp", 24, 0.2, 0.3, 99);
            addFood("Tofu (Firm)", 8, 2, 4, 76);
            addFood("Paneer", 18, 1.2, 20, 265);
            addFood("Soya Chunks", 52, 33, 0.5, 345);
            addFood("Whey Protein Powder", 80, 5, 2, 380);
            addFood("Greek Yogurt (Plain)", 10, 3.6, 0.4, 59);
            addFood("Cottage Cheese", 11, 3.4, 4.3, 98);
            addFood("Milk (Whole)", 3.2, 4.8, 3.3, 61);
            addFood("Milk (Skimmed)", 3.4, 5, 0.1, 35);
            addFood("Lentils (Cooked)", 9, 20, 0.4, 116);
            addFood("Chickpeas (Cooked)", 7, 27, 2.6, 164);
            addFood("Black Beans", 8.9, 23, 0.5, 132);
            addFood("Kidney Beans", 8.7, 22, 0.5, 127);

            // Carbs / Grains
            addFood("White Rice (Cooked)", 2.7, 28, 0.3, 130);
            addFood("Brown Rice (Cooked)", 2.6, 23, 0.9, 111);
            addFood("Oats (Raw)", 13, 68, 6.5, 389);
            addFood("Quinoa (Cooked)", 4.4, 21, 1.9, 120);
            addFood("Potato (Boiled)", 2, 17, 0.1, 77);
            addFood("Sweet Potato (Boiled)", 1.6, 20, 0.1, 86);
            addFood("Pasta (White, Cooked)", 5, 25, 1.1, 131);
            addFood("Whole Wheat Bread (1 slice)", 4, 12, 1, 80); // per slice approx
            addFood("White Bread (1 slice)", 2.7, 13, 0.8, 75);
            addFood("Chapati / Roti", 3, 15, 0.5, 85);

            // Fruits
            addFood("Apple", 0.3, 14, 0.2, 52);
            addFood("Banana", 1.1, 23, 0.3, 89);
            addFood("Orange", 0.9, 12, 0.1, 47);
            addFood("Grapes", 0.6, 17, 0.2, 67);
            addFood("Blueberries", 0.7, 14, 0.3, 57);
            addFood("Strawberries", 0.7, 8, 0.3, 32);
            addFood("Watermelon", 0.6, 8, 0.2, 30);
            addFood("Pineapple", 0.5, 13, 0.1, 50);
            addFood("Mango", 0.8, 15, 0.4, 60);
            addFood("Avocado", 2, 9, 15, 160);

            // Veggies
            addFood("Broccoli", 2.8, 7, 0.4, 34);
            addFood("Spinach", 2.9, 3.6, 0.4, 23);
            addFood("Carrot", 0.9, 10, 0.2, 41);
            addFood("Cucumber", 0.7, 3.6, 0.1, 15);
            addFood("Tomato", 0.9, 3.9, 0.2, 18);
            addFood("Bell Pepper", 1, 6, 0.3, 31);
            addFood("Onion", 1.1, 9, 0.1, 40);
            addFood("Green Peas", 5, 14, 0.4, 81);
            addFood("Corn", 3.2, 19, 1.2, 86);
            addFood("Mushroom", 3.1, 3.3, 0.3, 22);

            // Nuts & Seeds
            addFood("Almonds", 21, 22, 49, 575);
            addFood("Walnuts", 15, 14, 65, 654);
            addFood("Peanuts", 26, 16, 49, 567);
            addFood("Cashews", 18, 30, 44, 553);
            addFood("Peanut Butter", 25, 20, 50, 588);
            addFood("Chia Seeds", 17, 42, 31, 486);
            addFood("Flax Seeds", 18, 29, 42, 534);
            addFood("Pumpkin Seeds", 19, 54, 19, 446);

            // Junk / Snacks / Others
            addFood("Pizza (Slice)", 11, 30, 10, 266);
            addFood("Burger (Cheeseburger)", 15, 30, 14, 300);
            addFood("French Fries", 3.4, 41, 15, 312);
            addFood("Coke / Soda (330ml)", 0, 35, 0, 139);
            addFood("Chocolate (Milk)", 7.3, 59, 30, 535);
            addFood("Chocolate (Dark 70%)", 8, 46, 43, 600);
            addFood("Ice Cream (Vanilla)", 3.5, 24, 11, 207);
            addFood("Cookie (Choc Chip)", 5, 60, 24, 480);
            addFood("Popcorn (Plain)", 11, 74, 4, 370);
            addFood("Olive Oil", 0, 0, 100, 884);
            addFood("Butter", 0.9, 0.1, 81, 717);
            addFood("Mayonnaise", 1, 1, 75, 680);
            addFood("Honey", 0.3, 82, 0, 304);
            addFood("Sugar", 0, 100, 0, 387);
        }

        private void addFood(String n, double p, double c, double f, double cal) {
            foodData.put(n, new FoodItem(p, c, f, cal));
        }
    }

    class WorkoutSessionPanel extends JPanel {
        private JComboBox<String> exerciseCombo;
        private JSpinner setsSpinner, repsSpinner;
        private JLabel typeLbl, timerLbl;
        private ModernButton startButton, finishButton;
        private SmoothProgressBar progressBar;
        private String currentCategory;
        private Timer workoutTimer;
        private int totalTimeSeconds;
        private int remainingSeconds;
        private Map<String, String[]> exerciseMap;

        public WorkoutSessionPanel() {
            setLayout(new GridBagLayout());
            setBackground(MAIN_BG);

            exerciseMap = DataUtils.getExerciseMap();

            JPanel card = new ModernCard();
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(650, 600));

            typeLbl = new JLabel("Select Workout");
            typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
            typeLbl.setForeground(TEXT_PRIMARY);
            typeLbl.setHorizontalAlignment(SwingConstants.CENTER);
            typeLbl.setBorder(new EmptyBorder(0, 0, 30, 0));
            card.add(typeLbl, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(5, 1, 25, 25));
            formPanel.setBackground(CARD_BG);
            formPanel.setBorder(new EmptyBorder(10, 50, 20, 50));

            JPanel exPanel = new JPanel(new BorderLayout());
            exPanel.setBackground(CARD_BG);
            JLabel exLabel = new JLabel("Exercise");
            exLabel.setFont(FONT_BOLD);
            exLabel.setForeground(TEXT_SECONDARY);
            exerciseCombo = new JComboBox<>();
            exerciseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            exerciseCombo.setPreferredSize(new Dimension(200, 40));
            exPanel.add(exLabel, BorderLayout.NORTH);
            exPanel.add(exerciseCombo, BorderLayout.CENTER);
            formPanel.add(exPanel);

            JPanel setsRepsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
            setsRepsPanel.setBackground(CARD_BG);
            setsSpinner = createBigSpinner();
            repsSpinner = createBigSpinner();
            repsSpinner.setValue(12);
            setsRepsPanel.add(createInputGroup("Sets", setsSpinner));
            setsRepsPanel.add(createInputGroup("Reps", repsSpinner));
            formPanel.add(setsRepsPanel);

            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setBackground(CARD_BG);
            timerLbl = new JLabel("Ready to Start");
            timerLbl.setFont(FONT_BOLD);
            timerLbl.setHorizontalAlignment(SwingConstants.CENTER);
            progressBar = new SmoothProgressBar(0, 100);
            progressBar.setPreferredSize(new Dimension(100, 25));
            progressBar.setVisible(false);
            progressPanel.add(timerLbl, BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);
            formPanel.add(progressPanel);

            card.add(formPanel, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            btnPanel.setBackground(CARD_BG);
            btnPanel.setBorder(new EmptyBorder(20, 0, 10, 0));

            startButton = new ModernButton("Start Set", SUCCESS_COLOR, new Color(46, 204, 113));
            startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
            startButton.addActionListener(e -> startWorkoutTimer());

            finishButton = new ModernButton("Finish Early", DANGER_COLOR, new Color(231, 76, 60));
            finishButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
            finishButton.setEnabled(false);
            finishButton.addActionListener(e -> finishSet());

            btnPanel.add(startButton);
            btnPanel.add(finishButton);
            card.add(btnPanel, BorderLayout.SOUTH);

            add(card);
        }

        private JSpinner createBigSpinner() {
            JSpinner s = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            s.setFont(new Font("Segoe UI", Font.BOLD, 24));
            s.setBorder(new LineBorder(new Color(200,200,200), 1));
            JComponent editor = s.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor)editor).getTextField().setHorizontalAlignment(JTextField.CENTER);
            }
            return s;
        }

        private JPanel createInputGroup(String title, JSpinner spinner) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(CARD_BG);
            JLabel l = new JLabel(title);
            l.setFont(FONT_BOLD);
            l.setForeground(TEXT_SECONDARY);
            p.add(l, BorderLayout.NORTH);
            p.add(spinner, BorderLayout.CENTER);
            return p;
        }

        public void setExerciseType(String category) {
            this.currentCategory = category;
            typeLbl.setText(category + " Workout");
            exerciseCombo.removeAllItems();
            if (exerciseMap.containsKey(category)) {
                for (String ex : exerciseMap.get(category)) {
                    exerciseCombo.addItem(ex);
                }
            } else {
                exerciseCombo.addItem("Standard " + category);
            }
        }

        private void startWorkoutTimer() {
            if (workoutTimer != null && workoutTimer.isRunning()) return;
            int reps = (int) repsSpinner.getValue();
            totalTimeSeconds = reps * 6; // 6 seconds per rep estimation
            remainingSeconds = totalTimeSeconds;
            progressBar.setMaximum(totalTimeSeconds);
            progressBar.setValue(0);
            progressBar.setVisible(true);
            startButton.setEnabled(false);
            startButton.setBackground(Color.GRAY);
            finishButton.setEnabled(true);
            timerLbl.setText("Executing Set: " + remainingSeconds + "s remaining");

            workoutTimer = new Timer(1000, e -> {
                remainingSeconds--;
                progressBar.setValue(totalTimeSeconds - remainingSeconds);
                timerLbl.setText("Executing Set: " + remainingSeconds + "s remaining");
                if (remainingSeconds <= 0) {
                    finishSet();
                }
            });
            workoutTimer.start();
        }

        private void finishSet() {
            if (workoutTimer != null) workoutTimer.stop();

            int timeSpent = totalTimeSeconds - remainingSeconds;
            if (timeSpent < 0) timeSpent = 0;
            if (remainingSeconds == totalTimeSeconds) timeSpent = 1;

            startButton.setEnabled(true);
            startButton.setBackground(SUCCESS_COLOR);
            finishButton.setEnabled(false);
            progressBar.setVisible(false);
            timerLbl.setText("Set Complete!");

            if (currentUser != null) {
                String selectedExercise = (String) exerciseCombo.getSelectedItem();
                int sets = (int) setsSpinner.getValue();
                int reps = (int) repsSpinner.getValue();
                String details = "Timer Session - Sets: " + sets + ", Reps: " + reps;
                dbHelper.logWorkout(currentUser.id, currentCategory + ": " + selectedExercise, timeSpent, details);
            }
            JOptionPane.showMessageDialog(this, "Set Completed! Saved to History.");
        }
    }

    class ManualEntryPanel extends JPanel {
        CardLayout cl = new CardLayout();
        JPanel cont = new JPanel(cl);
        JTextField timeF, stepsF, repsF, setsF;
        JComboBox<String> exBox;
        String curType;
        JPanel formCard;
        JLabel setsLbl, repsLbl, stepsLbl;
        Map<String, String[]> allExMap;

        public ManualEntryPanel() {
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);
            cont.setBackground(MAIN_BG);
            allExMap = DataUtils.getExerciseMap();

            // -- SELECTION SCREEN --
            JPanel sel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 150));
            sel.setBackground(MAIN_BG);
            ModernButton g = new ModernButton("Strength Training", SIDEBAR_BG, new Color(52, 73, 94));
            g.setPreferredSize(new Dimension(250, 100));
            g.setFont(new Font("Segoe UI", Font.BOLD, 20));

            ModernButton c = new ModernButton("Cardio / Run", ACCENT_COLOR, ACCENT_HOVER);
            c.setPreferredSize(new Dimension(250, 100));
            c.setFont(new Font("Segoe UI", Font.BOLD, 20));

            sel.add(g); sel.add(c);

            // -- FORM SCREEN --
            JPanel formWrapper = new JPanel(new GridBagLayout());
            formWrapper.setBackground(MAIN_BG);
            formCard = new ModernCard();
            formCard.setLayout(new GridLayout(6, 2, 10, 20));
            formCard.setPreferredSize(new Dimension(550, 450));

            exBox = new JComboBox<>();
            timeF = new JTextField();
            stepsF = new JTextField();
            setsF = new JTextField();
            repsF = new JTextField();

            setsLbl = new JLabel("Sets:");
            repsLbl = new JLabel("Reps:");
            stepsLbl = new JLabel("Steps:");

            formCard.add(new JLabel("Exercise:")); formCard.add(exBox);
            formCard.add(new JLabel("Duration (mins):")); formCard.add(timeF);

            // Toggled Fields
            formCard.add(stepsLbl); formCard.add(stepsF);
            formCard.add(setsLbl); formCard.add(setsF);
            formCard.add(repsLbl); formCard.add(repsF);

            ModernButton save = new ModernButton("Save Log", SUCCESS_COLOR, new Color(46, 204, 113));
            ModernButton back = new ModernButton("Cancel", Color.GRAY, Color.DARK_GRAY);
            formCard.add(save); formCard.add(back);
            formWrapper.add(formCard);

            cont.add(sel, "SEL");
            cont.add(formWrapper, "FORM");
            add(cont);

            g.addActionListener(e -> {
                curType = "GYM";
                updateFormFields();
                loadEx("GYM");
                cl.show(cont, "FORM");
            });

            c.addActionListener(e -> {
                curType = "CARDIO";
                updateFormFields();
                loadEx("CARDIO");
                cl.show(cont, "FORM");
            });

            back.addActionListener(e -> cl.show(cont, "SEL"));

            save.addActionListener(e -> {
                try {
                    if(currentUser == null) return;
                    String ex = (String) exBox.getSelectedItem();
                    int dur = Integer.parseInt(timeF.getText()) * 60;
                    String details = "";
                    if(curType.equals("GYM")) {
                        details = "Sets: " + setsF.getText() + " Reps: " + repsF.getText();
                    } else {
                        details = "Steps: " + stepsF.getText();
                    }
                    dbHelper.logWorkout(currentUser.id, ex, dur, details);
                    JOptionPane.showMessageDialog(this, "Logged successfully!");
                    resetView();
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Check your inputs."); }
            });
        }

        private void updateFormFields() {
            boolean isGym = curType.equals("GYM");
            setsLbl.setVisible(isGym); setsF.setVisible(isGym);
            repsLbl.setVisible(isGym); repsF.setVisible(isGym);
            stepsLbl.setVisible(!isGym); stepsF.setVisible(!isGym);
            formCard.revalidate();
            formCard.repaint();
        }

        void loadEx(String type) {
            exBox.removeAllItems();
            if(type.equals("CARDIO")) {
                for(String s : allExMap.get("Cardio")) exBox.addItem(s);
            } else {
                for(String key : allExMap.keySet()) {
                    if(!key.equals("Cardio")) {
                        for(String s : allExMap.get(key)) exBox.addItem(s);
                    }
                }
            }
        }

        void resetView() {
            cl.show(cont, "SEL");
            timeF.setText(""); stepsF.setText(""); setsF.setText(""); repsF.setText("");
        }
    }

    class MyDataPanel extends BasePanel {
        private JLabel nameLbl, ageLbl, heightLbl, weightLbl, bmiLbl, goalLbl, streakLbl, proteinLbl;
        private JPanel card;

        public MyDataPanel() {
            setLayout(new GridBagLayout());
            setBackground(MAIN_BG);

            card = new ModernCard();
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(650, 450));

            JLabel header = new JLabel("User Statistics", SwingConstants.CENTER);
            header.setFont(new Font("Segoe UI", Font.BOLD, 22));
            header.setForeground(ACCENT_COLOR);
            header.setBorder(new EmptyBorder(0, 0, 25, 0));
            card.add(header, BorderLayout.NORTH);

            JPanel infoGrid = new JPanel(new GridLayout(4, 2, 20, 20));
            infoGrid.setBackground(CARD_BG);

            nameLbl = createValueLabel("-");
            ageLbl = createValueLabel("-");
            heightLbl = createValueLabel("-");
            weightLbl = createValueLabel("-");
            bmiLbl = createValueLabel("-");
            goalLbl = createValueLabel("-");
            streakLbl = createValueLabel("-");
            proteinLbl = createValueLabel("-");

            addPair(infoGrid, "Name", nameLbl);
            addPair(infoGrid, "Age", ageLbl);
            addPair(infoGrid, "Height", heightLbl);
            addPair(infoGrid, "Current Weight", weightLbl);
            addPair(infoGrid, "🔥 Activity Streak", streakLbl);
            addPair(infoGrid, "Daily Protein Goal", proteinLbl);
            addPair(infoGrid, "BMI Score", bmiLbl);
            addPair(infoGrid, "Target Weight", goalLbl);

            card.add(infoGrid, BorderLayout.CENTER);

            JLabel footerMsg = new JLabel("Consistent action creates consistent results.", SwingConstants.CENTER);
            footerMsg.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            footerMsg.setForeground(Color.GRAY);
            footerMsg.setBorder(new EmptyBorder(20,0,0,0));
            card.add(footerMsg, BorderLayout.SOUTH);

            add(card);
        }

        private JLabel createValueLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            l.setForeground(TEXT_PRIMARY);
            return l;
        }

        private void addPair(JPanel p, String title, JLabel valLbl) {
            JPanel pair = new JPanel(new BorderLayout());
            pair.setBackground(CARD_BG);
            JLabel t = new JLabel(title);
            t.setFont(new Font("Segoe UI", Font.BOLD, 12));
            t.setForeground(new Color(149, 165, 166));
            pair.add(t, BorderLayout.NORTH);
            pair.add(valLbl, BorderLayout.CENTER);
            p.add(pair);
        }

        @Override
        public void refreshData() {
            if(currentUser!=null) {
                nameLbl.setText(currentUser.name);
                ageLbl.setText(currentUser.age + " Years");
                heightLbl.setText(currentUser.height + " cm");
                weightLbl.setText(currentUser.weight + " kg");
                goalLbl.setText(currentUser.goalWeight + " kg");
                streakLbl.setText(dbHelper.getStreakDays(currentUser.id) + " Days");
                proteinLbl.setText(currentUser.getProteinGoal() + "g / day");
                double hM = currentUser.height / 100.0;
                double bmi = currentUser.weight / (hM * hM);
                String bmiText = String.format("%.1f", bmi);
                if (bmi < 18.5) {
                    bmiLbl.setText(bmiText + " (Underweight)"); bmiLbl.setForeground(ACCENT_COLOR);
                } else if (bmi < 24.9) {
                    bmiLbl.setText(bmiText + " (Healthy)"); bmiLbl.setForeground(SUCCESS_COLOR);
                } else {
                    bmiLbl.setText(bmiText + " (Overweight)"); bmiLbl.setForeground(DANGER_COLOR);
                }
            }
        }
    }

    class ExerciseCategoryPanel extends JPanel {
        public ExerciseCategoryPanel(){
            setLayout(new BorderLayout()); setBackground(MAIN_BG);
            JPanel grid = new JPanel(new GridLayout(3,2,20,20));
            grid.setBackground(MAIN_BG);
            grid.setBorder(new EmptyBorder(40,40,40,40));

            String[] m={"Legs","Chest","Back","Arms","Abs","Shoulders"};
            for(String s:m){
                ModernButton b=new ModernButton(s, ACCENT_COLOR, ACCENT_HOVER);
                b.setPreferredSize(new Dimension(200, 100));
                b.setFont(new Font("Segoe UI", Font.BOLD, 22));
                b.addActionListener(e->{
                    workoutSessionPanel.setExerciseType(s);
                    navigateTo(WORKOUT_PANEL);
                });
                grid.add(b);
            }
            add(grid);
        }
    }

    class SettingsPanel extends BasePanel {
        JTextField n,w,g;
        public SettingsPanel(){
            setLayout(new GridBagLayout()); setBackground(MAIN_BG);
            JPanel card = new ModernCard(); card.setLayout(new GridLayout(4,2,10,10)); card.setPreferredSize(new Dimension(450, 280));
            card.add(new JLabel("Name:")); n=new JTextField(); card.add(n);
            card.add(new JLabel("Weight (kg):")); w=new JTextField(); card.add(w);
            card.add(new JLabel("Goal Weight:")); g=new JTextField(); card.add(g);
            ModernButton s=new ModernButton("Update Profile", SUCCESS_COLOR, new Color(46, 204, 113));
            s.addActionListener(e->{currentUser.name=n.getText(); currentUser.weight=Double.parseDouble(w.getText()); currentUser.goalWeight=Double.parseDouble(g.getText()); dbHelper.updateUser(currentUser); JOptionPane.showMessageDialog(this,"Profile Updated");});
            card.add(s);
            ModernButton logout = new ModernButton("Logout", Color.GRAY, Color.DARK_GRAY);
            logout.addActionListener(e -> {
                currentUser=null; setNavigationVisible(false); navigateTo(LOGIN_PANEL);
            });
            card.add(logout);
            add(card);
        }
        @Override
        public void refreshData(){ if(currentUser!=null){ n.setText(currentUser.name); w.setText(""+currentUser.weight); g.setText(""+currentUser.goalWeight); } }
    }

    class StatisticsPanel extends BasePanel {
        JLabel streakLbl, scoreLbl, proteinLbl, longestWorkoutLbl;
        JTable historyTable;
        DefaultTableModel tableModel;

        public StatisticsPanel(){
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);
            setBorder(new EmptyBorder(20, 30, 20, 30));

            // -- Top Summary Card --
            JPanel summaryCard = new ModernCard();
            summaryCard.setLayout(new GridLayout(1, 4, 10, 0));
            summaryCard.setPreferredSize(new Dimension(800, 100));

            streakLbl = createStatLabel("Streak: 0");
            scoreLbl = createStatLabel("Score: 0");
            proteinLbl = createStatLabel("Protein: 0g");
            longestWorkoutLbl = createStatLabel("Max: 0s");

            summaryCard.add(createStatBox("Current Streak", streakLbl));
            summaryCard.add(createStatBox("Recursive Score", scoreLbl));
            summaryCard.add(createStatBox("Today's Protein", proteinLbl));
            summaryCard.add(createStatBox("Longest Workout", longestWorkoutLbl));

            add(summaryCard, BorderLayout.NORTH);

            // -- Bottom History Table --
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(CARD_BG);
            tablePanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JLabel tableTitle = new JLabel("Recent Workout History");
            tableTitle.setFont(FONT_HEADER);
            tableTitle.setBorder(new EmptyBorder(0,0,10,0));
            tablePanel.add(tableTitle, BorderLayout.NORTH);

            tableModel = new DefaultTableModel(new String[]{"Date", "Exercise", "Time", "Details"}, 0);
            historyTable = new JTable(tableModel);
            historyTable.setRowHeight(30);
            historyTable.setFont(FONT_NORMAL);
            historyTable.getTableHeader().setFont(FONT_BOLD);
            historyTable.getTableHeader().setBackground(new Color(236, 240, 241));

            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.getViewport().setBackground(Color.WHITE);
            tablePanel.add(scrollPane, BorderLayout.CENTER);

            add(tablePanel, BorderLayout.CENTER);
        }

        private JPanel createStatBox(String title, JLabel valLbl) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(CARD_BG);
            JLabel t = new JLabel(title, SwingConstants.CENTER);
            t.setForeground(TEXT_SECONDARY);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            p.add(t, BorderLayout.NORTH);
            p.add(valLbl, BorderLayout.CENTER);
            return p;
        }

        private JLabel createStatLabel(String txt) {
            JLabel l = new JLabel(txt, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 22));
            l.setForeground(TEXT_PRIMARY);
            return l;
        }

        @Override
        public void refreshData(){
            if(currentUser!=null) {
                // [CO3] Advanced Logic: Recursive calculation called here
                int days = dbHelper.getStreakDays(currentUser.id);
                int score = DataUtils.recursiveStreakScore(days);

                // [CO2] Algorithms: Retrieving data into Arrays and Sorting manually
                ArrayList<Integer> rawDurations = dbHelper.getWorkoutDurations(currentUser.id);
                int maxDuration = DataUtils.getLongestWorkoutDuration(rawDurations);

                streakLbl.setText(days + " Days");
                scoreLbl.setText(score + " pts");
                proteinLbl.setText(dbHelper.getTodayProtein(currentUser.id) + "g");
                longestWorkoutLbl.setText(maxDuration + "s");

                // Populate Table (Clear and Reload)
                ArrayList<Object[]> history = dbHelper.getWorkoutHistory(currentUser.id);
                tableModel.setRowCount(0);
                for(Object[] row : history) {
                    tableModel.addRow(row);
                }
                tableModel.fireTableDataChanged();
            }
        }
    }
}
