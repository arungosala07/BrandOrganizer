package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BrandOrganizer {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private HashMap<String, ArrayList<String>> brandDrops;  // Store brand -> drop dates
    private HashMap<String, String> brandWebsites;  // Store brand -> website link
    private ArrayList<User> users;
    private User currentUser;
    private Calendar calendar;

    public BrandOrganizer() {
        frame = new JFrame("Brand Organizer");
        tabbedPane = new JTabbedPane();
        brandDrops = new HashMap<>();
        brandWebsites = new HashMap<>();
        users = new ArrayList<>();
        calendar = Calendar.getInstance();  // Initialize calendar instance

        // Create UI Components
        createLoginPanel();
        createBrandInputPanel();
        createDropDisplayPanel();
        createCalendarPanel();
        createEditBrandPanel();
        createBrandListPanel();  // New button to show list of brands

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // Success Criterion: Login and multiple user accounts with separate registration page
    private void createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 150, 20)); // Orange background
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            currentUser = authenticateUser(username, password);
            if (currentUser != null) {
                JOptionPane.showMessageDialog(frame, "Logged in as: " + username);
                brandDrops = currentUser.getBrandDrops();  // Load the user's saved brands and drops
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials.");
            }
        });

        // Open a new registration page
        registerButton.addActionListener(e -> {
            createRegistrationPage();
        });

        panel.add(new JLabel("Username: "));
        panel.add(usernameField);
        panel.add(new JLabel("Password: "));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        tabbedPane.addTab("Login", panel);
    }

    // Separate registration page
    private void createRegistrationPage() {
        JFrame registerFrame = new JFrame("Register");
        JPanel panel = new JPanel();
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton createAccountButton = new JButton("Create Account");

        createAccountButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (!username.isEmpty() && !password.isEmpty()) {
                currentUser = new User(username, password);
                users.add(currentUser);
                JOptionPane.showMessageDialog(registerFrame, "Account created for: " + username);
                registerFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Please fill in all fields.");
            }
        });

        panel.add(new JLabel("Username: "));
        panel.add(usernameField);
        panel.add(new JLabel("Password: "));
        panel.add(passwordField);
        panel.add(createAccountButton);
        registerFrame.add(panel);
        registerFrame.setSize(300, 150);
        registerFrame.setVisible(true);
    }

    private User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Success Criterion: Add brands, show drop dates, and link to brand websites
    private void createBrandInputPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 150, 20)); // Orange background
        JTextField brandInput = new JTextField(15);
        JTextField websiteInput = new JTextField(15);
        JButton addBrandButton = new JButton("Add Brand");

        addBrandButton.addActionListener(e -> {
            String brand = brandInput.getText();
            String website = websiteInput.getText();
            if (!brand.isEmpty() && !website.isEmpty()) {
                currentUser.addBrand(brand);
                brandWebsites.put(brand, website);  // Save website to the brandWebsites HashMap
                brandDrops.put(brand, new ArrayList<>());
                brandInput.setText("");
                websiteInput.setText("");
                JOptionPane.showMessageDialog(frame, brand + " added with website link!");
            }

        });

        panel.add(new JLabel("Brand: "));
        panel.add(brandInput);
        panel.add(new JLabel("Website: "));
        panel.add(websiteInput);
        panel.add(addBrandButton);
        tabbedPane.addTab("Input Brands", panel);
    }

    // Success Criterion: Display upcoming drops and allow users to view them
    private void createDropDisplayPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 150, 20)); // Orange background
        JTextArea dropDisplay = new JTextArea(10, 40);
        dropDisplay.setEditable(false);
        JButton showDropsButton = new JButton("Show Upcoming Drops");
        JButton visitWebsiteButton = new JButton("Show Brand Website");

        showDropsButton.addActionListener(e -> {
            StringBuilder drops = new StringBuilder();
            for (String brand : currentUser.getBrands()) {
                if (!brandDrops.get(brand).isEmpty()) {  // Only show if drop dates exist for the brand
                    drops.append(brand).append(": ").append(brandDrops.get(brand)).append("\n");
                }
            }
            dropDisplay.setText(drops.toString());
        });

        visitWebsiteButton.addActionListener(e -> {
            String brand = JOptionPane.showInputDialog(frame, "Enter Brand Name:");
            if (brandWebsites.containsKey(brand)) {
                // Simply show the saved website link in a message dialog
                JOptionPane.showMessageDialog(frame, "Website for " + brand + ": " + brandWebsites.get(brand));
            } else {
                JOptionPane.showMessageDialog(frame, "Brand not found.");
            }
        });

        panel.add(new JScrollPane(dropDisplay));
        panel.add(showDropsButton);
        panel.add(visitWebsiteButton);
        tabbedPane.addTab("View Drops", panel);
    }

    // Success Criterion: Calendar and Notification for upcoming drops
    private void createCalendarPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 150, 20)); // Orange background
        JTextField notificationField = new JTextField(15);
        JButton notifyButton = new JButton("Set Notification");
        JLabel monthYearLabel = new JLabel();

        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(0, 7)); // 7 columns for the days of the week

        updateCalendar(calendarPanel, monthYearLabel); // Initial calendar update

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar(calendarPanel, monthYearLabel);
        });

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar(calendarPanel, monthYearLabel);
        });

        notifyButton.addActionListener(e -> {
            String dropDate = notificationField.getText();
            String brand = JOptionPane.showInputDialog(frame, "Enter Brand Name:");
            if (!dropDate.isEmpty() && brandDrops.containsKey(brand)) {
                brandDrops.get(brand).add(dropDate);
                JOptionPane.showMessageDialog(frame, "Notification set for: " + dropDate);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid date or brand.");
            }
        });

        panel.add(prevButton);
        panel.add(monthYearLabel);
        panel.add(nextButton);
        panel.add(new JLabel("Notify me about drop on (date): "));
        panel.add(notificationField);
        panel.add(notifyButton);
        panel.add(calendarPanel);
        tabbedPane.addTab("Calendar", panel);
    }

    private void updateCalendar(JPanel calendarPanel, JLabel monthYearLabel) {
        if (currentUser == null) {
            return;  // Only update the calendar when the user is logged in
        }

        calendarPanel.removeAll();

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        monthYearLabel.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.ENGLISH) + " " + year);

        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            calendarPanel.add(dayLabel);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel("")); // Empty labels for days not in the month
        }

        for (int day = 1; day <= daysInMonth; day++) {
            final int dayToShow = day;  // Final or effectively final variable
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.addActionListener(e -> {
                String date = String.format("%02d-%02d-%02d", dayToShow, month + 1, year % 100);  // Format: MM-DD-YY
                if (brandDrops.values().stream().anyMatch(dates -> dates.contains(date))) {
                    StringBuilder brandsWithDrops = new StringBuilder("Drops for the following brands on " + date + ":\n");
                    for (String brand : brandDrops.keySet()) {
                        if (brandDrops.get(brand).contains(date)) {
                            brandsWithDrops.append(brand).append("\n");
                        }
                    }
                    JOptionPane.showMessageDialog(frame, brandsWithDrops.toString());
                } else {
                    JOptionPane.showMessageDialog(frame, "No drops on " + date);
                }
            });
            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // Success Criterion: Edit brand information
    private void createEditBrandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 150, 20)); // Orange background
        JTextField brandNameField = new JTextField(15);
        JButton editBrandButton = new JButton("Edit Brand");

        editBrandButton.addActionListener(e -> {
            String brand = brandNameField.getText();
            if (brandDrops.containsKey(brand)) {
                String newBrandName = JOptionPane.showInputDialog(frame, "Enter new name for brand:");
                if (newBrandName != null && !newBrandName.isEmpty()) {
                    ArrayList<String> drops = brandDrops.remove(brand); // Remove the old brand and its drops
                    brandDrops.put(newBrandName, drops); // Add the new brand with the same drops
                    brandNameField.setText("");
                    JOptionPane.showMessageDialog(frame, "Brand updated to: " + newBrandName);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Brand not found.");
            }
        });

        panel.add(new JLabel("Brand Name: "));
        panel.add(brandNameField);
        panel.add(editBrandButton);
        tabbedPane.addTab("Edit Brand", panel);
    }

    // Success Criterion: List brands
    private void createBrandListPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 150, 20)); // Orange background
        JTextArea brandListDisplay = new JTextArea(10, 40);
        brandListDisplay.setEditable(false);
        JButton showBrandListButton = new JButton("Show Brands");

        showBrandListButton.addActionListener(e -> {
            StringBuilder brands = new StringBuilder("Brands:\n");
            for (String brand : brandDrops.keySet()) {
                brands.append(brand).append("\n");
            }
            brandListDisplay.setText(brands.toString());
        });

        panel.add(new JScrollPane(brandListDisplay));
        panel.add(showBrandListButton);
        tabbedPane.addTab("Brand List", panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BrandOrganizer::new);
    }

    class User {
        private String username;
        private String password;
        private ArrayList<String> brands;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.brands = new ArrayList<>();
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void addBrand(String brand) {
            brands.add(brand);
        }

        public ArrayList<String> getBrands() {
            return brands;
        }

        public HashMap<String, ArrayList<String>> getBrandDrops() {
            HashMap<String, ArrayList<String>> userBrandDrops = new HashMap<>();
            for (String brand : brands) {
                userBrandDrops.put(brand, brandDrops.get(brand));
            }
            return userBrandDrops;
        }
    }
}
