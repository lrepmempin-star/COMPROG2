import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

// ─────────────────────────────────────────────
//  Domain Classes
// ─────────────────────────────────────────────

class UserAccount {
    private int accountID;
    private String username;
    private String password;
    private String accountRole;
    private String accountStatus;

    public UserAccount(int accountID, String username, String password,
                       String accountRole, String accountStatus) {
        this.accountID     = accountID;
        this.username      = username;
        this.password      = password;
        this.accountRole   = accountRole;
        this.accountStatus = accountStatus;
    }

    public boolean login(String u, String p) {
        if (u == null || p == null || u.isEmpty() || p.isEmpty())
            throw new IllegalArgumentException("Username and password must not be empty.");
        return this.username.equals(u) && this.password.equals(p)
               && "active".equalsIgnoreCase(this.accountStatus);
    }

    public void logout() {}

    public boolean resetPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        this.password = newPassword;
        return true;
    }

    public boolean verifyUser(String u, String p) {
        if (u == null || p == null)
            throw new IllegalArgumentException("Credentials cannot be null.");
        return this.username.equals(u) && this.password.equals(p);
    }

    public String getUsername()    { return username; }
    public String getAccountRole() { return accountRole; }
    public int    getAccountID()   { return accountID; }
}

class Payslip {
    private int    payslipID;
    private double salary;
    private double deduction;
    private Date   paydate;

    public Payslip(int payslipID, double salary, double deduction, Date paydate) {
        if (salary < 0 || deduction < 0)
            throw new IllegalArgumentException("Salary and deduction must be non-negative.");
        this.payslipID = payslipID;
        this.salary    = salary;
        this.deduction = deduction;
        this.paydate   = paydate;
    }

    public String downloadPayslip()  { return "payslip_" + payslipID + ".pdf"; }
    public void   generatePayslip()  {}
    public int    getPayslipID()     { return payslipID; }
    public double getSalary()        { return salary; }
    public double getDeduction()     { return deduction; }
    public double getNetPay()        { return salary - deduction; }
    public Date   getPaydate()       { return paydate; }
}

class TaxDocument {
    private int    documentID;
    private int    taxYear;
    private double taxAmount;

    public TaxDocument(int documentID, int taxYear, double taxAmount) {
        if (taxYear < 2000 || taxYear > 2100)
            throw new IllegalArgumentException("Invalid tax year.");
        if (taxAmount < 0)
            throw new IllegalArgumentException("Tax amount cannot be negative.");
        this.documentID = documentID;
        this.taxYear    = taxYear;
        this.taxAmount  = taxAmount;
    }

    public void   viewTaxDocument()     {}
    public String downloadTaxDocument() { return "tax_doc_" + documentID + "_" + taxYear + ".pdf"; }
    public int    getDocumentID()       { return documentID; }
    public int    getTaxYear()          { return taxYear; }
    public double getTaxAmount()        { return taxAmount; }
}

class LeaveRequest {
    private int    requestID;
    private String leaveType;
    private Date   startDate;
    private Date   endDate;
    private String status;

    public LeaveRequest(int requestID, String leaveType, Date startDate, Date endDate) {
        if (leaveType == null || leaveType.isEmpty())
            throw new IllegalArgumentException("Leave type cannot be empty.");
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("Start and end dates cannot be null.");
        if (endDate.before(startDate))
            throw new IllegalArgumentException("End date cannot be before start date.");
        this.requestID = requestID;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate   = endDate;
        this.status    = "Pending";
    }

    public boolean submitRequest() { this.status = "Submitted"; return true; }

    public boolean approveRequest() {
        if (!"Submitted".equals(status))
            throw new IllegalStateException("Only submitted requests can be approved.");
        this.status = "Approved"; return true;
    }

    public boolean rejectRequest() {
        if (!"Submitted".equals(status))
            throw new IllegalStateException("Only submitted requests can be rejected.");
        this.status = "Rejected"; return true;
    }

    public int    getRequestID() { return requestID; }
    public String getLeaveType() { return leaveType; }
    public Date   getStartDate() { return startDate; }
    public Date   getEndDate()   { return endDate; }
    public String getStatus()    { return status; }
}

class LeaveBalance {
    private int vacationLeave;
    private int sickLeave;
    private int remainingBalance;

    public LeaveBalance(int vacationLeave, int sickLeave) {
        if (vacationLeave < 0 || sickLeave < 0)
            throw new IllegalArgumentException("Leave credits cannot be negative.");
        this.vacationLeave    = vacationLeave;
        this.sickLeave        = sickLeave;
        this.remainingBalance = vacationLeave + sickLeave;
    }

    public int viewBalance() { return remainingBalance; }

    public boolean updateBalance(int days) {
        if (days <= 0)
            throw new IllegalArgumentException("Days must be a positive number.");
        if (days > remainingBalance)
            throw new IllegalStateException("Insufficient leave balance.");
        remainingBalance -= days;
        return true;
    }

    public int getVacationLeave()    { return vacationLeave; }
    public int getSickLeave()        { return sickLeave; }
    public int getRemainingBalance() { return remainingBalance; }
    public int getTotalBalance()     { return vacationLeave + sickLeave; }
}

class PersonalDetails {
    private String address;
    private String contactNumber;
    private String bankDetails;

    public PersonalDetails(String address, String contactNumber, String bankDetails) {
        this.address       = address;
        this.contactNumber = contactNumber;
        this.bankDetails   = bankDetails;
    }

    public boolean updateAddress(String v) {
        if (v == null || v.isEmpty()) throw new IllegalArgumentException("Address cannot be empty.");
        this.address = v; return true;
    }

    public boolean updateContactNumber(String v) {
        if (v == null || !v.matches("\\d{10,11}"))
            throw new IllegalArgumentException("Contact number must be 10-11 digits.");
        this.contactNumber = v; return true;
    }

    public boolean updateBankDetails(String v) {
        if (v == null || v.isEmpty()) throw new IllegalArgumentException("Bank details cannot be empty.");
        this.bankDetails = v; return true;
    }

    public String getAddress()       { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getBankDetails()   { return bankDetails; }
}

class Employee {
    private int            employeeID;
    private String         employeeName;
    private String         email;
    private String         department;
    private Payslip        payslip;
    private TaxDocument    taxDocument;
    private LeaveRequest   currentLeaveRequest;
    private LeaveBalance   leaveBalance;
    private PersonalDetails personalDetails;

    public Employee(int employeeID, String employeeName, String email, String department,
                    Payslip payslip, TaxDocument taxDocument,
                    LeaveBalance leaveBalance, PersonalDetails personalDetails) {
        this.employeeID      = employeeID;
        this.employeeName    = employeeName;
        this.email           = email;
        this.department      = department;
        this.payslip         = payslip;
        this.taxDocument     = taxDocument;
        this.leaveBalance    = leaveBalance;
        this.personalDetails = personalDetails;
    }

    public void viewPayslip() {}

    public boolean submitLeaveRequest(LeaveRequest req) {
        if (req == null) throw new IllegalArgumentException("Leave request cannot be null.");
        this.currentLeaveRequest = req;
        return req.submitRequest();
    }

    public boolean updateDetails(String name, String email, String dept) {
        if (name == null || name.isEmpty())        throw new IllegalArgumentException("Name cannot be empty.");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email address.");
        this.employeeName = name; this.email = email; this.department = dept;
        return true;
    }

    public void viewTaxDocument() {}

    public int             getEmployeeID()      { return employeeID; }
    public String          getEmployeeName()    { return employeeName; }
    public String          getEmail()           { return email; }
    public String          getDepartment()      { return department; }
    public Payslip         getPayslip()         { return payslip; }
    public TaxDocument     getTaxDocument()     { return taxDocument; }
    public LeaveBalance    getLeaveBalance()    { return leaveBalance; }
    public PersonalDetails getPersonalDetails() { return personalDetails; }
}

// ─────────────────────────────────────────────
//  Main Application
// ─────────────────────────────────────────────

public class EmployeeApp {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color BG       = new Color(244, 246, 249);
    static final Color SURFACE  = Color.WHITE;
    static final Color CHARCOAL = new Color(  5,   5,   5);
    static final Color RED      = new Color(139,   0,   0);
    static final Color ORANGE   = new Color(252, 101,   8);
    static final Color SLATE    = new Color( 67,  70,  75);
    static final Color MID_GRAY = new Color(136, 136, 136);
    static final Color RULE_CLR = new Color(224, 220, 212);
    static final Color ROW_ALT  = new Color(250, 250, 250);
    static final Color SIDEBAR_DIV = new Color( 30,  30,  30);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    static final Font SERIF_22 = new Font("Georgia", Font.BOLD,   22);
    static final Font SERIF_18 = new Font("Georgia", Font.BOLD,   18);
    static final Font SERIF_16 = new Font("Georgia", Font.BOLD,   16);
    static final Font SERIF_15 = new Font("Georgia", Font.BOLD,   15);
    static final Font SERIF_14 = new Font("Georgia", Font.BOLD,   14);
    static final Font SANS     = new Font("SansSerif", Font.PLAIN, 13);
    static final Font SANS_SM  = new Font("SansSerif", Font.PLAIN, 11);
    static final Font SANS_CAP = new Font("SansSerif", Font.BOLD,  10);
    static final Font SANS_B   = new Font("SansSerif", Font.BOLD,  13);

    // ── Sample Data ───────────────────────────────────────────────────────────
    static UserAccount account  = new UserAccount(1, "group17", "password123", "Employee", "active");
    static Employee    employee = new Employee(
        101, "Group 17", "group17@motorph.com", "Software Developer",
        new Payslip(1001, 55000.00, 7500.00, new Date()),
        new TaxDocument(2001, 2026, 12500.00),
        new LeaveBalance(15, 10),
        new PersonalDetails("123 Rizal St, Manila", "09171234567", "BDO SA 0012345678")
    );
    static List<LeaveRequest> leaveRequests = new ArrayList<>();
    static int nextLeaveId = 3001;

    // ── State ─────────────────────────────────────────────────────────────────
    static JFrame     frame;
    static CardLayout cardLayout;
    static JPanel     cardPanel;
    static JLabel     topbarTitleLabel;

    // Nav button refs for active state
    static JButton[] navBtns = new JButton[6];
    static int activeTab = 0;

    static String[] TAB_IDS     = {"overview", "payslip", "tax", "leavereq", "leavebal", "personal"};
    static String[] TAB_LABELS  = {"Dashboard", "Payslip", "Tax Document", "Leave Request", "Leave Balance", "Personal Details"};
    static String[] PAGE_TITLES = {"Dashboard Overview", "Payslip", "Tax Document", "Leave Request", "Leave Balance", "Personal Details"};
    static String[] NAV_ICONS   = {"#", "$", "%", "@", "=", "&"};

    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeApp::showLogin);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static String fmt(double v) {
        return "\u20B1" + String.format("%,.2f", v);
    }

    static JButton mkDarkBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(SANS_CAP);
        b.setBackground(CHARCOAL);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(RED); }
            public void mouseExited(MouseEvent e)  { b.setBackground(CHARCOAL); }
        });
        return b;
    }

    static JButton mkRedBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(SANS_CAP);
        b.setBackground(RED);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(9, 16, 9, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(100, 0, 0)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(RED); }
        });
        return b;
    }

    static JButton mkOutlineBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(SANS_CAP);
        b.setBackground(SURFACE);
        b.setForeground(CHARCOAL);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE_CLR, 1),
            new EmptyBorder(8, 18, 8, 18)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JTextField mkTextField() {
        JTextField f = new JTextField();
        f.setFont(SANS);
        f.setBackground(new Color(249, 249, 249));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE_CLR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return f;
    }

    static JLabel mkCapLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(SANS_CAP);
        l.setForeground(SLATE);
        return l;
    }

    static JLabel mkStatusLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(SANS_SM);
        return l;
    }

    static void setOk(JLabel l, String msg) {
        l.setText(msg);
        l.setForeground(SLATE);
    }

    static void setErr(JLabel l, String msg) {
        l.setText(msg);
        l.setForeground(RED);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    static void showLogin() {
        JFrame f = new JFrame("MotorPH \u2014 Employee Portal");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 480);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(new Color(249, 249, 249));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(40, 40, 32, 40));

        // Kicker
        JLabel kicker = new JLabel("EMPLOYEE PORTAL");
        kicker.setFont(SANS_CAP);
        kicker.setForeground(ORANGE);
        kicker.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("MotorPH");
        title.setFont(new Font("Georgia", Font.PLAIN, 28));
        title.setForeground(CHARCOAL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Red rule
        JPanel ruleBar = new JPanel();
        ruleBar.setBackground(RED);
        ruleBar.setPreferredSize(new Dimension(40, 2));
        ruleBar.setMaximumSize(new Dimension(40, 2));
        ruleBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sub
        JLabel sub = new JLabel("Employee's Portal");
        sub.setFont(SANS_SM);
        sub.setForeground(SLATE);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(kicker);
        root.add(Box.createVerticalStrut(6));
        root.add(title);
        root.add(Box.createVerticalStrut(10));
        root.add(ruleBar);
        root.add(Box.createVerticalStrut(6));
        root.add(sub);
        root.add(Box.createVerticalStrut(28));

        // Card
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE_CLR, 1),
            new EmptyBorder(26, 28, 26, 28)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(340, 999));

        JTextField   userField = mkTextField();
        JPasswordField passField = new JPasswordField();
        passField.setFont(SANS);
        passField.setBackground(new Color(249, 249, 249));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE_CLR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel errLabel = mkStatusLabel();

        card.add(mkCapLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(14));
        card.add(mkCapLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(10));
        card.add(errLabel);
        card.add(Box.createVerticalStrut(10));

        JButton loginBtn = mkDarkBtn("SIGN IN");
        loginBtn.setMaximumSize(new Dimension(999, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(18));

        JSeparator sep = new JSeparator();
        sep.setForeground(RULE_CLR);
        sep.setMaximumSize(new Dimension(999, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        JLabel hint = new JLabel("<html><center><font color='#888888'>username &nbsp;\u00B7&nbsp; <b>group17</b><br>password &nbsp;\u00B7&nbsp; <b>password123</b></font></center></html>");
        hint.setFont(SANS_SM);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(hint);

        root.add(card);

        ActionListener doLogin = e -> {
            try {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                if (account.login(u, p)) {
                    f.dispose();
                    showDashboard();
                } else {
                    setErr(errLabel, "  Invalid username or password.");
                }
            } catch (IllegalArgumentException ex) {
                setErr(errLabel, "  " + ex.getMessage());
            }
        };
        loginBtn.addActionListener(doLogin);
        passField.addActionListener(doLogin);

        f.add(root);
        f.setVisible(true);
    }

    // ── Dashboard Shell ───────────────────────────────────────────────────────

    static void showDashboard() {
        frame = new JFrame("MotorPH \u2014 Employee Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 660);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(buildSidebar(), BorderLayout.WEST);
        frame.add(buildMainArea(), BorderLayout.CENTER);

        frame.setVisible(true);
        switchTab(0);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    static JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(CHARCOAL);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Brand
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setBackground(CHARCOAL);
        brand.setBorder(new EmptyBorder(18, 16, 16, 16));
        brand.setMaximumSize(new Dimension(220, 70));

        // Brand icon
        JPanel icon = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED);
                g2.fillRoundRect(0, 0, 38, 38, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "MP";
                g2.drawString(txt, (38 - fm.stringWidth(txt)) / 2, (38 + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        icon.setPreferredSize(new Dimension(38, 38));
        icon.setOpaque(false);

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel brandName = new JLabel("MotorPH");
        brandName.setFont(new Font("Georgia", Font.PLAIN, 16));
        brandName.setForeground(Color.WHITE);
        JLabel brandSub = new JLabel("PORTAL");
        brandSub.setFont(new Font("SansSerif", Font.BOLD, 9));
        brandSub.setForeground(ORANGE);
        brandText.add(brandName);
        brandText.add(brandSub);

        brand.add(icon);
        brand.add(brandText);
        sidebar.add(brand);

        // Divider
        sidebar.add(mkSidebarDivider());

        // Section label
        sidebar.add(mkSidebarSectionLabel("MAIN MENU"));

        // Nav items
        String[] labels = {"Dashboard", "Payslip", "Tax Document", "Leave Request", "Leave Balance", "Personal Details"};
        String[] iconTxt = {"[ ]", "[$]", "[%]", "[@]", "[=]", "[&]"};

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JButton btn = buildNavButton(labels[i], iconTxt[i]);
            navBtns[i] = btn;
            btn.addActionListener(e -> switchTab(idx));
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(mkSidebarDivider());

        // Logout
        JButton logoutBtn = buildNavButton("Logout", "[<]");
        logoutBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to sign out?", "Sign Out",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r == JOptionPane.YES_OPTION) {
                account.logout();
                frame.dispose();
                showLogin();
            }
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(8));

        return sidebar;
    }

    static JButton buildNavButton(String label, String icon) {
        JButton btn = new JButton(label) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getClientProperty("active") == Boolean.TRUE) {
                    g2.setColor(RED);
                    g2.fillRect(0, 0, 4, getHeight());
                    g2.setColor(RED);
                    g2.fillRect(4, 0, getWidth() - 4, getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(20, 20, 20));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setColor(CHARCOAL);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                if (getClientProperty("active") == Boolean.TRUE) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(new Color(136, 136, 136));
                }
                g2.drawString(getText(), 40, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(SANS);
        btn.setForeground(MID_GRAY);
        btn.setBackground(CHARCOAL);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        return btn;
    }

    static void switchTab(int idx) {
        activeTab = idx;
        for (int i = 0; i < navBtns.length; i++) {
            if (navBtns[i] != null) {
                navBtns[i].putClientProperty("active", i == idx ? Boolean.TRUE : Boolean.FALSE);
                navBtns[i].repaint();
            }
        }
        cardLayout.show(cardPanel, TAB_IDS[idx]);
        if (topbarTitleLabel != null) topbarTitleLabel.setText(PAGE_TITLES[idx]);
    }

    static JSeparator mkSidebarDivider() {
        JSeparator s = new JSeparator();
        s.setForeground(SIDEBAR_DIV);
        s.setBackground(SIDEBAR_DIV);
        s.setMaximumSize(new Dimension(220, 1));
        return s;
    }

    static JLabel mkSidebarSectionLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("SansSerif", Font.BOLD, 9));
        l.setForeground(SLATE);
        l.setBorder(new EmptyBorder(14, 18, 6, 18));
        l.setMaximumSize(new Dimension(220, 32));
        return l;
    }

    // ── Main Area ─────────────────────────────────────────────────────────────

    static JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(SURFACE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, RULE_CLR),
            new EmptyBorder(0, 24, 0, 24)
        ));
        topbar.setPreferredSize(new Dimension(0, 56));

        topbarTitleLabel = new JLabel("Dashboard Overview");
        topbarTitleLabel.setFont(SERIF_18);
        topbarTitleLabel.setForeground(CHARCOAL);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        JLabel empName = new JLabel(employee.getEmployeeName());
        empName.setFont(SANS_SM);
        empName.setForeground(new Color(100, 100, 100));
        JLabel badge = new JLabel("  EMPLOYEE  ");
        badge.setFont(SANS_CAP);
        badge.setForeground(Color.WHITE);
        badge.setBackground(RED);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        topRight.add(empName);
        topRight.add(badge);

        topbar.add(topbarTitleLabel, BorderLayout.WEST);
        topbar.add(topRight, BorderLayout.EAST);

        // Content cards
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG);

        cardPanel.add(buildOverview(),      TAB_IDS[0]);
        cardPanel.add(buildPayslip(),       TAB_IDS[1]);
        cardPanel.add(buildTaxDoc(),        TAB_IDS[2]);
        cardPanel.add(buildLeaveReq(),      TAB_IDS[3]);
        cardPanel.add(buildLeaveBal(),      TAB_IDS[4]);
        cardPanel.add(buildPersonal(),      TAB_IDS[5]);

        main.add(topbar,    BorderLayout.NORTH);
        main.add(cardPanel, BorderLayout.CENTER);
        return main;
    }

    // ── Scroll wrapper ────────────────────────────────────────────────────────

    static JScrollPane scrollWrap(JPanel inner) {
        JScrollPane sp = new JScrollPane(inner);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        return sp;
    }

    // ── Content panel shell ───────────────────────────────────────────────────

    static JPanel contentShell() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        return p;
    }

    // ── White card panel ──────────────────────────────────────────────────────

    static JPanel cardPanel(int padV, int padH) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(padV, padH, padV, padH));
        return p;
    }

    // ── Page header ───────────────────────────────────────────────────────────

    static JPanel pageHeader(String title, String sub) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(0, 0, 18, 0));
        p.setMaximumSize(new Dimension(9999, 60));
        JLabel t = new JLabel(title);
        t.setFont(SERIF_22);
        t.setForeground(CHARCOAL);
        JLabel s = new JLabel(sub);
        s.setFont(SANS_SM);
        s.setForeground(MID_GRAY);
        p.add(t);
        p.add(Box.createVerticalStrut(3));
        p.add(s);
        return p;
    }

    // ── Red rule ──────────────────────────────────────────────────────────────

    static JPanel redRule() {
        JPanel r = new JPanel();
        r.setBackground(RED);
        r.setMaximumSize(new Dimension(36, 2));
        r.setPreferredSize(new Dimension(36, 2));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        return r;
    }

    // ── KV row ────────────────────────────────────────────────────────────────

    static JPanel kvRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        row.setMaximumSize(new Dimension(9999, 44));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(SANS_CAP);
        lbl.setForeground(MID_GRAY);
        lbl.setPreferredSize(new Dimension(180, 20));
        JLabel val = new JLabel(value);
        val.setFont(SANS);
        val.setForeground(CHARCOAL);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(row);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(245, 242, 238));
        sep.setMaximumSize(new Dimension(9999, 1));
        wrapper.add(sep);
        return wrapper;
    }

    // ── Overview ──────────────────────────────────────────────────────────────

    static JPanel buildOverview() {
        JPanel inner = contentShell();
        inner.setAlignmentX(Component.LEFT_ALIGNMENT);

        inner.add(pageHeader("Dashboard Overview", "Welcome to your employee portal \u2014 Motor PH"));

        // Stat cards row
        JPanel statRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statRow.setOpaque(false);
        statRow.setMaximumSize(new Dimension(9999, 110));
        statRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statRow.setBorder(new EmptyBorder(0, 0, 16, 0));

        Payslip ps = employee.getPayslip();
        LeaveBalance lb = employee.getLeaveBalance();
        TaxDocument td = employee.getTaxDocument();

        statRow.add(buildStatCard("Employee ID", "#" + employee.getEmployeeID(), "Active employee", RED));
        statRow.add(buildStatCard("Gross Salary", fmt(ps.getSalary()), "Current month", ORANGE));
        statRow.add(buildStatCard("Net Pay", fmt(ps.getNetPay()), "After deductions", SLATE));
        statRow.add(buildStatCard("Leave Balance", lb.getRemainingBalance() + " days", "Days remaining", CHARCOAL));

        inner.add(statRow);

        // Bottom row: chart + activity
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 14, 0));
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(9999, 9999));

        // Chart card
        JPanel chartCard = new ShadowCard();
        chartCard.setLayout(new BorderLayout(0, 10));
        chartCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel chartHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chartHeader.setOpaque(false);
        JLabel chartTitle = new JLabel("Monthly Salary Trend");
        chartTitle.setFont(SERIF_15);
        chartTitle.setForeground(CHARCOAL);
        chartHeader.add(chartTitle);

        SalaryChartPanel chartPanel = new SalaryChartPanel(ps.getNetPay());
        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);

        // Recent activity card
        JPanel actCard = new ShadowCard();
        actCard.setLayout(new BorderLayout(0, 10));
        actCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel actTitle = new JLabel("Recent Activity");
        actTitle.setFont(SERIF_15);
        actTitle.setForeground(CHARCOAL);

        JPanel txnList = new JPanel();
        txnList.setOpaque(false);
        txnList.setLayout(new BoxLayout(txnList, BoxLayout.Y_AXIS));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        txnList.add(buildTxnRow(
            employee.getEmployeeName().substring(0, 1),
            employee.getEmployeeName(),
            sdf.format(ps.getPaydate()) + " \u00B7 Payslip",
            fmt(ps.getNetPay()), RED
        ));
        txnList.add(buildTxnRow("T", "Tax Filed",
            "Tax Year " + td.getTaxYear(),
            fmt(td.getTaxAmount()), ORANGE));
        txnList.add(buildTxnRow("L", "Leave Balance",
            "Available credits",
            lb.getRemainingBalance() + " days", SLATE));

        actCard.add(actTitle, BorderLayout.NORTH);
        actCard.add(txnList, BorderLayout.CENTER);

        bottomRow.add(chartCard);
        bottomRow.add(actCard);

        inner.add(bottomRow);

        JScrollPane sp = scrollWrap(inner);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(sp);
        return wrapper;
    }

    static JPanel buildStatCard(String label, String value, String sub, Color accentColor) {
        JPanel card = new ShadowCard() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(accentColor);
                g2.fillRect(0, 0, getWidth(), 3);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(SANS_CAP);
        lbl.setForeground(MID_GRAY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Georgia", Font.BOLD, 20));
        val.setForeground(CHARCOAL);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLbl.setForeground(MID_GRAY);

        left.add(lbl);
        left.add(Box.createVerticalStrut(5));
        left.add(val);
        left.add(Box.createVerticalStrut(4));
        left.add(subLbl);

        // Icon circle
        JPanel iconCircle = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 25);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, 40, 40, 10, 10);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(40, 40));

        card.add(left, BorderLayout.CENTER);
        card.add(iconCircle, BorderLayout.EAST);
        return card;
    }

    static JPanel buildTxnRow(String initials, String name, String meta, String amount, Color avatarColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(9, 0, 9, 0));
        row.setMaximumSize(new Dimension(9999, 58));

        // Avatar
        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(avatarColor);
                g2.fillRoundRect(0, 0, 36, 36, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, (36 - fm.stringWidth(initials)) / 2,
                    (36 + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(SANS_B);
        nameLabel.setForeground(CHARCOAL);
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        metaLabel.setForeground(MID_GRAY);
        info.add(nameLabel);
        info.add(metaLabel);

        JLabel amtLabel = new JLabel(amount);
        amtLabel.setFont(SERIF_14);
        amtLabel.setForeground(CHARCOAL);

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);
        row.add(amtLabel, BorderLayout.EAST);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(row);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(245, 242, 238));
        sep.setMaximumSize(new Dimension(9999, 1));
        wrapper.add(sep);
        return wrapper;
    }

    // ── Payslip ───────────────────────────────────────────────────────────────

    static JPanel buildPayslip() {
        JPanel inner = contentShell();
        inner.add(pageHeader("Payslip", "Your compensation breakdown for the current period"));

        Payslip ps = employee.getPayslip();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

        JPanel card = cardPanel(22, 24);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(9999, 9999));

        card.add(redRule());
        card.add(Box.createVerticalStrut(14));
        card.add(kvRow("Payslip ID",   "#" + ps.getPayslipID()));
        card.add(kvRow("Pay Date",     sdf.format(ps.getPaydate())));
        card.add(kvRow("Gross Salary", fmt(ps.getSalary())));
        card.add(kvRow("Deductions",   fmt(ps.getDeduction())));

        // Callout
        JPanel callout = new JPanel(new BorderLayout());
        callout.setBackground(CHARCOAL);
        callout.setBorder(new EmptyBorder(14, 18, 14, 18));
        callout.setMaximumSize(new Dimension(9999, 52));
        JLabel netCap = new JLabel("NET PAY");
        netCap.setFont(SANS_CAP);
        netCap.setForeground(MID_GRAY);
        JLabel netAmt = new JLabel(fmt(ps.getNetPay()));
        netAmt.setFont(new Font("Georgia", Font.BOLD, 20));
        netAmt.setForeground(Color.WHITE);
        callout.add(netCap, BorderLayout.WEST);
        callout.add(netAmt, BorderLayout.EAST);
        card.add(Box.createVerticalStrut(16));
        card.add(callout);
        card.add(Box.createVerticalStrut(18));

        JLabel msg = mkStatusLabel();
        JButton dlBtn = mkDarkBtn("DOWNLOAD PAYSLIP");
        dlBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        dlBtn.addActionListener(e -> setOk(msg, "Downloaded: " + ps.downloadPayslip() + " (simulated)"));

        card.add(dlBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(msg);

        inner.add(card);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scrollWrap(inner));
        return wrapper;
    }

    // ── Tax Document ──────────────────────────────────────────────────────────

    static JPanel buildTaxDoc() {
        JPanel inner = contentShell();
        inner.add(pageHeader("Tax Document", "Your official tax records"));

        TaxDocument td = employee.getTaxDocument();

        JPanel card = cardPanel(22, 24);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(9999, 9999));

        card.add(redRule());
        card.add(Box.createVerticalStrut(14));
        card.add(kvRow("Document ID", "#" + td.getDocumentID()));
        card.add(kvRow("Tax Year",    String.valueOf(td.getTaxYear())));
        card.add(kvRow("Tax Amount",  fmt(td.getTaxAmount())));
        card.add(Box.createVerticalStrut(18));

        JLabel msg = mkStatusLabel();
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton viewBtn = mkOutlineBtn("VIEW DOCUMENT");
        JButton dlBtn   = mkDarkBtn("DOWNLOAD");
        btnRow.add(viewBtn);
        btnRow.add(dlBtn);

        viewBtn.addActionListener(e -> setOk(msg, "Viewing tax document for " + td.getTaxYear() + " (simulated)."));
        dlBtn.addActionListener(e -> setOk(msg, "Downloaded: " + td.downloadTaxDocument() + " (simulated)"));

        card.add(btnRow);
        card.add(Box.createVerticalStrut(8));
        card.add(msg);

        inner.add(card);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scrollWrap(inner));
        return wrapper;
    }

    // ── Leave Request ─────────────────────────────────────────────────────────

    static DefaultTableModel leaveTableModel;

    static JPanel buildLeaveReq() {
        JPanel inner = contentShell();
        inner.add(pageHeader("Leave Request", "Submit and track your leave requests"));

        // Form card
        JPanel formCard = cardPanel(22, 24);
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.setMaximumSize(new Dimension(9999, 9999));
        formCard.add(redRule());
        formCard.add(Box.createVerticalStrut(14));

        String[] leaveTypes = {"-- Select type --", "Vacation", "Sick", "Emergency", "Maternity", "Paternity"};
        JComboBox<String> typeBox = new JComboBox<>(leaveTypes);
        typeBox.setFont(SANS);
        typeBox.setBackground(new Color(249, 249, 249));
        typeBox.setMaximumSize(new Dimension(380, 36));

        JTextField startField = mkTextField();
        startField.setToolTipText("YYYY-MM-DD");
        startField.setMaximumSize(new Dimension(180, 36));

        JTextField endField = mkTextField();
        endField.setToolTipText("YYYY-MM-DD");
        endField.setMaximumSize(new Dimension(180, 36));

        JPanel formGrid = new JPanel(new GridLayout(3, 2, 14, 10));
        formGrid.setOpaque(false);
        formGrid.setMaximumSize(new Dimension(580, 120));
        formGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tLbl = mkCapLabel("Leave Type");
        JLabel sLbl = mkCapLabel("Start Date (YYYY-MM-DD)");
        JLabel eLbl = mkCapLabel("End Date (YYYY-MM-DD)");

        formGrid.add(tLbl);   formGrid.add(new JLabel(""));
        formGrid.add(sLbl);   formGrid.add(eLbl);
        formGrid.add(startField); formGrid.add(endField);

        formCard.add(mkCapLabel("Leave Type"));
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(typeBox);
        formCard.add(Box.createVerticalStrut(14));

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        dateRow.setOpaque(false);
        dateRow.setMaximumSize(new Dimension(9999, 50));
        JPanel startGrp = new JPanel(); startGrp.setOpaque(false); startGrp.setLayout(new BoxLayout(startGrp, BoxLayout.Y_AXIS));
        startGrp.add(mkCapLabel("Start Date (YYYY-MM-DD)")); startGrp.add(Box.createVerticalStrut(5)); startGrp.add(startField);
        JPanel endGrp = new JPanel();   endGrp.setOpaque(false);   endGrp.setLayout(new BoxLayout(endGrp, BoxLayout.Y_AXIS));
        endGrp.add(mkCapLabel("End Date (YYYY-MM-DD)"));   endGrp.add(Box.createVerticalStrut(5));   endGrp.add(endField);
        dateRow.add(startGrp);
        dateRow.add(endGrp);
        formCard.add(dateRow);
        formCard.add(Box.createVerticalStrut(14));

        JLabel errLabel = new JLabel(" ");
        errLabel.setFont(SANS_SM);
        errLabel.setForeground(RED);
        JLabel msgLabel = mkStatusLabel();

        JButton submitBtn = mkDarkBtn("SUBMIT REQUEST");
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(errLabel);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(submitBtn);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(msgLabel);

        inner.add(formCard);
        inner.add(Box.createVerticalStrut(16));

        // History card
        JPanel histCard = cardPanel(18, 24);
        histCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        histCard.setMaximumSize(new Dimension(9999, 9999));

        JLabel histTitle = new JLabel("Leave History");
        histTitle.setFont(SERIF_15);
        histTitle.setForeground(CHARCOAL);
        histCard.add(histTitle);
        histCard.add(Box.createVerticalStrut(12));

        String[] cols = {"ID", "Type", "Start", "End", "Status"};
        leaveTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(leaveTableModel);
        tbl.setFont(SANS);
        tbl.setRowHeight(34);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setBackground(SURFACE);
        tbl.setSelectionBackground(new Color(245, 242, 238));

        JTableHeader header = tbl.getTableHeader();
        header.setFont(SANS_CAP);
        header.setBackground(SURFACE);
        header.setForeground(MID_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, RULE_CLR));

        // Striping renderer
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? SURFACE : ROW_ALT);
                ((JLabel) comp).setBorder(new EmptyBorder(0, 10, 0, 10));
                return comp;
            }
        });

        refreshLeaveTable();

        JScrollPane tblScroll = new JScrollPane(tbl);
        tblScroll.setBorder(BorderFactory.createLineBorder(RULE_CLR, 1));
        tblScroll.setMaximumSize(new Dimension(9999, 200));

        histCard.add(tblScroll);
        inner.add(histCard);

        submitBtn.addActionListener(e -> {
            errLabel.setText(" ");
            msgLabel.setText(" ");
            int typeIdx = typeBox.getSelectedIndex();
            if (typeIdx == 0) { errLabel.setText("  Please select a leave type."); return; }
            String typeStr = (String) typeBox.getSelectedItem();
            String startStr = startField.getText().trim();
            String endStr   = endField.getText().trim();
            if (startStr.isEmpty()) { errLabel.setText("  Please enter a start date."); return; }
            if (endStr.isEmpty())   { errLabel.setText("  Please enter an end date."); return; }
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setLenient(false);
                Date start = df.parse(startStr);
                Date end   = df.parse(endStr);
                LeaveRequest req = new LeaveRequest(nextLeaveId++, typeStr, start, end);
                employee.submitLeaveRequest(req);
                leaveRequests.add(req);
                refreshLeaveTable();
                setOk(msgLabel, "  Leave request submitted successfully.");
                typeBox.setSelectedIndex(0);
                startField.setText("");
                endField.setText("");
            } catch (Exception ex) {
                setErr(errLabel, "  " + ex.getMessage());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scrollWrap(inner));
        return wrapper;
    }

    static void refreshLeaveTable() {
        if (leaveTableModel == null) return;
        leaveTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        for (LeaveRequest r : leaveRequests) {
            leaveTableModel.addRow(new Object[]{
                "#" + r.getRequestID(),
                r.getLeaveType() + " Leave",
                sdf.format(r.getStartDate()),
                sdf.format(r.getEndDate()),
                r.getStatus()
            });
        }
    }

    // ── Leave Balance ─────────────────────────────────────────────────────────

    static JPanel buildLeaveBal() {
        JPanel inner = contentShell();
        inner.add(pageHeader("Leave Balance", "Your available leave credits"));

        LeaveBalance lb = employee.getLeaveBalance();

        JPanel balRow = new JPanel(new GridLayout(1, 3, 12, 0));
        balRow.setOpaque(false);
        balRow.setMaximumSize(new Dimension(9999, 110));
        balRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        balRow.add(buildBalCard(lb.getVacationLeave()   + "", "Vacation Leave",    RED));
        balRow.add(buildBalCard(lb.getSickLeave()        + "", "Sick Leave",        ORANGE));
        balRow.add(buildBalCard(lb.getRemainingBalance() + "", "Remaining Balance", SLATE));

        inner.add(balRow);
        inner.add(Box.createVerticalStrut(16));

        JPanel card = cardPanel(20, 24);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(9999, 9999));
        card.add(kvRow("Total Credits",   lb.getTotalBalance() + " days"));
        card.add(kvRow("Remaining Days",  lb.getRemainingBalance() + " days"));
        inner.add(card);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scrollWrap(inner));
        return wrapper;
    }

    static JPanel buildBalCard(String num, String label, Color accent) {
        JPanel card = new ShadowCard() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(accent);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel numLabel = new JLabel(num);
        numLabel.setFont(new Font("Georgia", Font.BOLD, 36));
        numLabel.setForeground(CHARCOAL);
        numLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLabel = new JLabel(label.toUpperCase());
        lblLabel.setFont(SANS_CAP);
        lblLabel.setForeground(MID_GRAY);
        lblLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(numLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(lblLabel);
        return card;
    }

    // ── Personal Details ──────────────────────────────────────────────────────

    static JPanel buildPersonal() {
        JPanel inner = contentShell();
        inner.add(pageHeader("Personal Details", "Manage your personal information"));

        PersonalDetails pd = employee.getPersonalDetails();

        JPanel card = cardPanel(22, 24);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(9999, 9999));
        card.add(redRule());
        card.add(Box.createVerticalStrut(14));

        // Address
        card.add(mkSectionTitle("Address"));
        JTextField addrField = mkTextField();
        addrField.setText(pd.getAddress());
        addrField.setMaximumSize(new Dimension(420, 36));
        JLabel addrMsg = mkStatusLabel();
        JButton addrBtn = mkRedBtn("SAVE");
        JPanel addrRow = editRow(addrField, addrBtn);
        addrBtn.addActionListener(e -> {
            try { pd.updateAddress(addrField.getText().trim()); setOk(addrMsg, "Address updated successfully."); }
            catch (IllegalArgumentException ex) { setErr(addrMsg, ex.getMessage()); }
        });
        card.add(addrRow);
        card.add(addrMsg);
        card.add(Box.createVerticalStrut(20));

        // Contact
        card.add(mkSectionTitle("Contact Number"));
        JTextField contactField = mkTextField();
        contactField.setText(pd.getContactNumber());
        contactField.setMaximumSize(new Dimension(420, 36));
        JLabel contactMsg = mkStatusLabel();
        JButton contactBtn = mkRedBtn("SAVE");
        JPanel contactRow = editRow(contactField, contactBtn);
        contactBtn.addActionListener(e -> {
            try { pd.updateContactNumber(contactField.getText().trim()); setOk(contactMsg, "Contact number updated successfully."); }
            catch (IllegalArgumentException ex) { setErr(contactMsg, ex.getMessage()); }
        });
        card.add(contactRow);
        card.add(contactMsg);
        card.add(Box.createVerticalStrut(20));

        // Bank
        card.add(mkSectionTitle("Bank Details"));
        JTextField bankField = mkTextField();
        bankField.setText(pd.getBankDetails());
        bankField.setMaximumSize(new Dimension(420, 36));
        JLabel bankMsg = mkStatusLabel();
        JButton bankBtn = mkRedBtn("SAVE");
        JPanel bankRow = editRow(bankField, bankBtn);
        bankBtn.addActionListener(e -> {
            try { pd.updateBankDetails(bankField.getText().trim()); setOk(bankMsg, "Bank details updated successfully."); }
            catch (IllegalArgumentException ex) { setErr(bankMsg, ex.getMessage()); }
        });
        card.add(bankRow);
        card.add(bankMsg);

        inner.add(card);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scrollWrap(inner));
        return wrapper;
    }

    static JLabel mkSectionTitle(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(SERIF_14);
        l.setForeground(CHARCOAL);
        l.setBorder(new EmptyBorder(0, 0, 8, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static JPanel editRow(JTextField field, JButton btn) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(9999, 44));
        field.setPreferredSize(new Dimension(380, 34));
        btn.setPreferredSize(new Dimension(70, 34));
        row.add(field);
        row.add(btn);
        return row;
    }
}

// ── Shadow Card ───────────────────────────────────────────────────────────────

class ShadowCard extends JPanel {
    ShadowCard() {
        setOpaque(false);
        setBackground(Color.WHITE);
    }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Shadow
        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 3, 8, 8);
        // Surface
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 8, 8);
        g2.dispose();
    }
}

// ── Salary Chart ──────────────────────────────────────────────────────────────

class SalaryChartPanel extends JPanel {
    private final double netPay;
    private final double[] values;
    private final String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    SalaryChartPanel(double netPay) {
        this.netPay = netPay;
        values = new double[]{46500, 47200, 47500, 47300, 47500, 47500, 47500, 47500, 47500, 47500, 47500, netPay};
        setOpaque(false);
        setPreferredSize(new Dimension(0, 200));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int padL = 58, padR = 16, padT = 12, padB = 30;
        int chartW = w - padL - padR;
        int chartH = h - padT - padB;

        double minV = 44000, maxV = 60000;
        int steps = 4;

        // Grid lines + Y labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(240, 237, 232));
        for (int i = 0; i <= steps; i++) {
            int y = padT + (int)(chartH * (1.0 - (double)i / steps));
            g2.drawLine(padL, y, padL + chartW, y);
            double val = minV + (maxV - minV) * i / steps;
            g2.setColor(new Color(136, 136, 136));
            String lbl = "\u20B1" + (int)(val / 1000) + "k";
            g2.drawString(lbl, padL - 54, y + 4);
            g2.setColor(new Color(240, 237, 232));
        }

        // X labels
        g2.setColor(new Color(136, 136, 136));
        double xStep = (double) chartW / (months.length - 1);
        for (int i = 0; i < months.length; i++) {
            int x = padL + (int)(xStep * i);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(months[i], x - fm.stringWidth(months[i]) / 2, h - padB + 14);
        }

        // Compute points
        int[] px = new int[values.length];
        int[] py = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            px[i] = padL + (int)(xStep * i);
            py[i] = padT + (int)(chartH * (1.0 - (values[i] - minV) / (maxV - minV)));
        }

        // Fill area
        Path2D.Float area = new Path2D.Float();
        area.moveTo(px[0], padT + chartH);
        area.lineTo(px[0], py[0]);
        for (int i = 1; i < values.length; i++) {
            double cx1 = px[i-1] + xStep * 0.4;
            double cx2 = px[i]   - xStep * 0.4;
            area.curveTo(cx1, py[i-1], cx2, py[i], px[i], py[i]);
        }
        area.lineTo(px[values.length - 1], padT + chartH);
        area.closePath();
        g2.setColor(new Color(139, 0, 0, 30));
        g2.fill(area);

        // Line
        g2.setColor(new Color(139, 0, 0));
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D.Float line = new Path2D.Float();
        line.moveTo(px[0], py[0]);
        for (int i = 1; i < values.length; i++) {
            double cx1 = px[i-1] + xStep * 0.4;
            double cx2 = px[i]   - xStep * 0.4;
            line.curveTo(cx1, py[i-1], cx2, py[i], px[i], py[i]);
        }
        g2.draw(line);

        // Dots
        for (int i = 0; i < values.length; i++) {
            g2.setColor(new Color(139, 0, 0));
            g2.fillOval(px[i] - 4, py[i] - 4, 8, 8);
            g2.setColor(Color.WHITE);
            g2.fillOval(px[i] - 2, py[i] - 2, 4, 4);
        }

        g2.dispose();
    }
}
