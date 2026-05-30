import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public void   viewTaxDocument()   {}
    public String downloadTaxDocument() { return "tax_doc_" + documentID + "_" + taxYear + ".pdf"; }
    public int    getDocumentID()     { return documentID; }
    public int    getTaxYear()        { return taxYear; }
    public double getTaxAmount()      { return taxAmount; }
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

    public int getVacationLeave()   { return vacationLeave; }
    public int getSickLeave()       { return sickLeave; }
    public int getRemainingBalance(){ return remainingBalance; }
    public int getTotalBalance()    { return vacationLeave + sickLeave; }
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
        this.employeeID     = employeeID;
        this.employeeName   = employeeName;
        this.email          = email;
        this.department     = department;
        this.payslip        = payslip;
        this.taxDocument    = taxDocument;
        this.leaveBalance   = leaveBalance;
        this.personalDetails = personalDetails;
    }

    public void viewPayslip() {}

    public boolean submitLeaveRequest(LeaveRequest req) {
        if (req == null) throw new IllegalArgumentException("Leave request cannot be null.");
        this.currentLeaveRequest = req;
        return req.submitRequest();
    }

    public boolean updateDetails(String name, String email, String dept) {
        if (name == null || name.isEmpty())          throw new IllegalArgumentException("Name cannot be empty.");
        if (email == null || !email.contains("@"))   throw new IllegalArgumentException("Invalid email address.");
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
//  Main Application — Magazine Style
// ─────────────────────────────────────────────

public class EmployeeApp {

    // ── Palette ──────────────────────────────────────────────────────────
    static final Color BG        = new Color(249, 249, 249);   // #f9f9f9
    static final Color SURFACE   = new Color(255, 255, 255);   // #ffffff
    static final Color CHARCOAL  = new Color(  5,   5,   5);   // #050505
    static final Color RED_DEEP  = new Color(139,   0,   0);   // #8b0000
    static final Color ORANGE    = new Color(252, 101,   8);   // #fc6508
    static final Color SLATE     = new Color( 67,  70,  75);   // #43464b
    static final Color INK2      = new Color( 67,  70,  75);   // #43464b
    static final Color INK3      = new Color(136, 136, 136);   // mid-gray
    static final Color RULE      = new Color(224, 220, 212);   // warm rule
    static final Color ROW_ALT   = new Color(249, 249, 249);   // #f9f9f9
    static final Color GREEN_OK  = new Color( 67,  70,  75);   // use slate for success
    static final Color RED_ERR   = new Color(139,   0,   0);   // #8b0000

    // ── Fonts ─────────────────────────────────────────────────────────────
    static final Font  SERIF_H1   = new Font("Georgia", Font.BOLD,  22);
    static final Font  SERIF_H2   = new Font("Georgia", Font.BOLD,  16);
    static final Font  SERIF_H3   = new Font("Georgia", Font.BOLD,  13);
    static final Font  SANS       = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font  SANS_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font  SANS_BOLD  = new Font("Segoe UI", Font.BOLD,  12);
    static final Font  SANS_CAP   = new Font("Segoe UI", Font.BOLD,  10);

    // ── Sample Data ───────────────────────────────────────────────────────
    static UserAccount account  = new UserAccount(1, "group17", "password123", "Employee", "active");
    static Employee    employee = new Employee(
        101, "Group 17", "group17@motorph.com", "Software Developer",
        new Payslip(1001, 55000.00, 7500.00, new Date()),
        new TaxDocument(2001, 2026, 12500.00),
        new LeaveBalance(15, 10),
        new PersonalDetails("123 Rizal St, Manila", "09171234567", "BDO SA 0012345678")
    );

    // ─────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeApp::showLogin);
    }

    // ── Login ─────────────────────────────────────────────────────────────

    static void showLogin() {
        JFrame f = new JFrame("Motor PH — Employee Portal");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(380, 460);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(40, 40, 30, 40));

        // Masthead
        JLabel kicker = mkCap("EMPLOYEE PORTAL");
        kicker.setAlignmentX(Component.CENTER_ALIGNMENT);
        kicker.setForeground(ORANGE);

        JLabel title = new JLabel("Motor PH", SwingConstants.CENTER);
        title.setFont(SERIF_H1);
        title.setForeground(CHARCOAL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel rulePanel = mkRule(40, RED_DEEP);
        rulePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel portal = new JLabel("Employee's Portal", SwingConstants.CENTER);
        portal.setFont(SANS_SMALL);
        portal.setForeground(SLATE);
        portal.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(kicker);
        root.add(Box.createVerticalStrut(6));
        root.add(title);
        root.add(Box.createVerticalStrut(10));
        root.add(rulePanel);
        root.add(Box.createVerticalStrut(6));
        root.add(portal);
        root.add(Box.createVerticalStrut(28));

        // Card
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE, 1),
            new EmptyBorder(26, 28, 26, 28)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(320, 400));

        JTextField  userField = styledField();
        JPasswordField passField = new JPasswordField();
        styleField(passField);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(SANS_SMALL);
        errorLabel.setForeground(RED_ERR);

        card.add(mkCapLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(16));
        card.add(mkCapLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(14));

        JButton loginBtn = mkDarkBtn("SIGN IN");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(RULE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        JLabel hint = new JLabel("<html><center><font color='#888888'>username &nbsp;·&nbsp; <b>group17</b><br>password &nbsp;·&nbsp; <b>password123</b></font></center></html>", SwingConstants.CENTER);
        hint.setFont(SANS_SMALL);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(hint);

        root.add(card);

        ActionListener doLogin = e -> {
            try {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                boolean ok = account.login(u, p);
                if (ok) { f.dispose(); showDashboard(); }
                else errorLabel.setText("  Invalid username or password.");
            } catch (IllegalArgumentException ex) {
                errorLabel.setText("  " + ex.getMessage());
            }
        };
        loginBtn.addActionListener(doLogin);
        passField.addActionListener(doLogin);

        f.add(root);
        f.setVisible(true);
    }

    // ── Dashboard ─────────────────────────────────────────────────────────

    static void showDashboard() {
        JFrame f = new JFrame("Motor PH — Employee Portal");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(820, 580);
        f.setLocationRelativeTo(null);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CHARCOAL);
        topBar.setBorder(new EmptyBorder(0, 24, 0, 16));
        topBar.setPreferredSize(new Dimension(820, 48));

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topLeft.setOpaque(false);
        JLabel wordmark = new JLabel("Motor PH");
        wordmark.setFont(new Font("Georgia", Font.BOLD, 16));
        wordmark.setForeground(Color.WHITE);
        JLabel divider = new JLabel("  |  ");
        divider.setFont(SANS);
        divider.setForeground(new Color(80, 80, 80));
        JLabel sub = new JLabel("Employee Portal");
        sub.setFont(SANS_CAP);
        sub.setForeground(ORANGE);
        topLeft.add(wordmark); topLeft.add(divider); topLeft.add(sub);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 9));
        topRight.setOpaque(false);
        JLabel empName = new JLabel(employee.getEmployeeName());
        empName.setFont(SANS_SMALL);
        empName.setForeground(new Color(170, 170, 170));
        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setFont(SANS_CAP);
        logoutBtn.setBackground(CHARCOAL);
        logoutBtn.setForeground(ORANGE);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RED_DEEP, 1),
            new EmptyBorder(3, 10, 3, 10)
        ));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(f, "Are you sure you want to sign out?",
                "Sign Out", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r == JOptionPane.YES_OPTION) { account.logout(); f.dispose(); showLogin(); }
        });
        topRight.add(empName); topRight.add(logoutBtn);

        topBar.add(topLeft,  BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

        // Red accent rule
        JPanel rule = new JPanel();
        rule.setBackground(RED_DEEP);
        rule.setPreferredSize(new Dimension(820, 3));

        // Tabs
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Georgia", Font.PLAIN, 13));
        tabs.setBackground(SURFACE);
        tabs.setForeground(INK2);

        tabs.addTab("Overview",         buildOverview());
        tabs.addTab("Payslip",          buildPayslip());
        tabs.addTab("Tax Document",     buildTaxDoc());
        tabs.addTab("Leave Request",    buildLeaveRequest(f));
        tabs.addTab("Leave Balance",    buildLeaveBalance());
        tabs.addTab("Personal Details", buildPersonalDetails(f));

        JPanel body = new JPanel(new BorderLayout());
        body.add(rule,  BorderLayout.NORTH);
        body.add(tabs,  BorderLayout.CENTER);

        f.add(topBar, BorderLayout.NORTH);
        f.add(body,   BorderLayout.CENTER);
        f.setVisible(true);
    }

    // ── Overview ──────────────────────────────────────────────────────────

    static JPanel buildOverview() {
        JPanel p = tabPanel();
        p.add(sectionHeader("Overview", "Employee Profile"), BorderLayout.NORTH);

        Payslip ps = employee.getPayslip();

        // Name hero
        JPanel hero = new JPanel();
        hero.setOpaque(false);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(0, 0, 18, 0));
        JLabel nameLabel = new JLabel(employee.getEmployeeName());
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        nameLabel.setForeground(CHARCOAL);
        JLabel metaLabel = new JLabel(employee.getDepartment() + "  ·  " + employee.getEmail());
        metaLabel.setFont(SANS_SMALL);
        metaLabel.setForeground(INK3);
        hero.add(nameLabel);
        hero.add(Box.createVerticalStrut(4));
        hero.add(metaLabel);
        hero.add(Box.createVerticalStrut(14));
        hero.add(fullRule());

        String[][] rows = {
            { "EMPLOYEE ID",    "#" + employee.getEmployeeID() },
            { "GROSS SALARY",   fmt(ps.getSalary()) },
            { "DEDUCTIONS",     fmt(ps.getDeduction()) },
            { "NET PAY",        fmt(ps.getNetPay()) },
            { "LEAVE BALANCE",  employee.getLeaveBalance().getRemainingBalance() + " days remaining" },
            { "TAX YEAR",       String.valueOf(employee.getTaxDocument().getTaxYear()) },
        };

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(hero, BorderLayout.NORTH);
        center.add(buildDataRows(rows), BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── Payslip ───────────────────────────────────────────────────────────

    static JPanel buildPayslip() {
        JPanel p = tabPanel();
        p.add(sectionHeader("Payslip", "Compensation"), BorderLayout.NORTH);

        Payslip ps = employee.getPayslip();
        String[][] rows = {
            { "PAYSLIP ID",   "#" + ps.getPayslipID() },
            { "PAY DATE",     new SimpleDateFormat("MMMM dd, yyyy").format(ps.getPaydate()) },
            { "GROSS SALARY", fmt(ps.getSalary()) },
            { "DEDUCTIONS",   fmt(ps.getDeduction()) },
        };

        // Net pay callout
        JPanel callout = new JPanel(new BorderLayout());
        callout.setBackground(CHARCOAL);
        callout.setBorder(new EmptyBorder(14, 18, 14, 18));
        callout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        JLabel netCap = mkCap("NET PAY");
        netCap.setForeground(INK3);
        JLabel netAmt = new JLabel(fmt(ps.getNetPay()));
        netAmt.setFont(new Font("Georgia", Font.BOLD, 20));
        netAmt.setForeground(Color.WHITE);
        callout.add(netCap, BorderLayout.WEST);
        callout.add(netAmt, BorderLayout.EAST);

        JLabel msgLabel = statusLabel();
        JButton btn = mkDarkBtn("DOWNLOAD PAYSLIP");
        btn.addActionListener(e -> {
            try { ps.generatePayslip(); setOk(msgLabel, "Downloaded: " + ps.downloadPayslip() + " (simulated)"); }
            catch (Exception ex) { setErr(msgLabel, ex.getMessage()); }
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildDataRows(rows));
        center.add(Box.createVerticalStrut(16));
        center.add(callout);
        center.add(Box.createVerticalStrut(20));
        center.add(btn);
        center.add(Box.createVerticalStrut(8));
        center.add(msgLabel);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── Tax Document ──────────────────────────────────────────────────────

    static JPanel buildTaxDoc() {
        JPanel p = tabPanel();
        p.add(sectionHeader("Tax Document", "Tax Records"), BorderLayout.NORTH);

        TaxDocument td = employee.getTaxDocument();
        String[][] rows = {
            { "DOCUMENT ID", "#" + td.getDocumentID() },
            { "TAX YEAR",    String.valueOf(td.getTaxYear()) },
            { "TAX AMOUNT",  fmt(td.getTaxAmount()) },
        };

        JLabel previewLabel = new JLabel(" ");
        previewLabel.setFont(SANS);
        previewLabel.setForeground(INK2);

        JLabel msgLabel = statusLabel();
        JButton viewBtn = mkLightBtn("VIEW DOCUMENT");
        JButton dlBtn   = mkDarkBtn("DOWNLOAD");

        viewBtn.addActionListener(e -> {
            td.viewTaxDocument();
            previewLabel.setText("Document #" + td.getDocumentID()
                + "  ·  Year: " + td.getTaxYear()
                + "  ·  Amount: " + fmt(td.getTaxAmount()));
        });
        dlBtn.addActionListener(e -> {
            previewLabel.setText(" ");
            setOk(msgLabel, "Downloaded: " + td.downloadTaxDocument() + " (simulated)");
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(viewBtn); btnRow.add(dlBtn);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildDataRows(rows));
        center.add(Box.createVerticalStrut(14));
        center.add(previewWrap(previewLabel));
        center.add(Box.createVerticalStrut(16));
        center.add(btnRow);
        center.add(Box.createVerticalStrut(6));
        center.add(msgLabel);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── Leave Request ─────────────────────────────────────────────────────

    static JPanel buildLeaveRequest(JFrame parent) {
        JPanel p = tabPanel();
        p.add(sectionHeader("Leave Request", "Time Off"), BorderLayout.NORTH);

        String[] types = { "Vacation Leave", "Sick Leave", "Emergency Leave", "Maternity/Paternity Leave" };
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setFont(SANS);
        typeBox.setBackground(SURFACE);

        JTextField startField = styledField();
        JTextField endField   = styledField();
        JLabel startErr = errLabel(); JLabel endErr = errLabel();
        JLabel msgLabel = statusLabel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 0, 2, 12);
        g.fill   = GridBagConstraints.HORIZONTAL;

        addFormRow(form, g, 0, "LEAVE TYPE",            typeBox,    null);
        addFormRow(form, g, 1, "START DATE (yyyy-MM-dd)", startField, startErr);
        addFormRow(form, g, 2, "END DATE   (yyyy-MM-dd)", endField,   endErr);

        // History
        String[] cols = { "ID", "Type", "Start", "End", "Status" };
        DefaultTableModel histModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable histTable = mkTable(histModel);
        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBorder(BorderFactory.createLineBorder(RULE));
        scroll.setPreferredSize(new Dimension(560, 110));
        scroll.setBackground(SURFACE);

        JButton submitBtn = mkDarkBtn("SUBMIT REQUEST");
        submitBtn.addActionListener(e -> {
            startErr.setText(" "); endErr.setText(" ");
            msgLabel.setText(" "); msgLabel.setForeground(GREEN_OK);
            boolean valid = true;
            if (startField.getText().trim().isEmpty()) { startErr.setText("  Required"); valid = false; }
            if (endField.getText().trim().isEmpty())   { endErr.setText("  Required");   valid = false; }
            if (!valid) return;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); sdf.setLenient(false);
                Date start = sdf.parse(startField.getText().trim());
                Date end   = sdf.parse(endField.getText().trim());
                LeaveRequest req = new LeaveRequest(
                    (int)(Math.random() * 9000) + 1000,
                    (String) typeBox.getSelectedItem(), start, end
                );
                employee.submitLeaveRequest(req);
                histModel.addRow(new Object[]{
                    "#" + req.getRequestID(), req.getLeaveType(),
                    sdf.format(req.getStartDate()), sdf.format(req.getEndDate()), req.getStatus()
                });
                setOk(msgLabel, "Request #" + req.getRequestID() + " submitted.");
                startField.setText(""); endField.setText("");
            } catch (java.text.ParseException ex) {
                setErr(msgLabel, "Invalid date format. Use yyyy-MM-dd.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                setErr(msgLabel, ex.getMessage());
            }
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(form);
        center.add(Box.createVerticalStrut(12));
        center.add(submitBtn);
        center.add(Box.createVerticalStrut(6));
        center.add(msgLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(mkCap("SUBMITTED REQUESTS"));
        center.add(Box.createVerticalStrut(8));
        center.add(scroll);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── Leave Balance ─────────────────────────────────────────────────────

    static JPanel buildLeaveBalance() {
        JPanel p = tabPanel();
        p.add(sectionHeader("Leave Balance", "Time Off"), BorderLayout.NORTH);

        LeaveBalance lb = employee.getLeaveBalance();

        String[][] rows = {
            { "TOTAL ALLOCATED", lb.getTotalBalance() + " days" },
            { "VACATION LEAVE",  lb.getVacationLeave() + " days" },
            { "SICK LEAVE",      lb.getSickLeave() + " days" },
            { "REMAINING",       lb.getRemainingBalance() + " days" },
        };

        JTextField daysField = styledField(); daysField.setMaximumSize(new Dimension(140, 30));
        JLabel fieldErr = errLabel();
        JLabel msgLabel = statusLabel();

        // Live model for remaining row
        DefaultTableModel balModel = new DefaultTableModel(new Object[]{"FIELD", "VALUE"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : rows) balModel.addRow(new Object[]{ row[0], row[1] });
        JTable balTable = mkTable(balModel);
        JScrollPane scroll = new JScrollPane(balTable);
        scroll.setBorder(BorderFactory.createLineBorder(RULE));
        scroll.setPreferredSize(new Dimension(360, 102));

        JButton btn = mkDarkBtn("UPDATE");
        btn.addActionListener(e -> {
            fieldErr.setText(" "); msgLabel.setText(" ");
            String txt = daysField.getText().trim();
            if (txt.isEmpty()) { fieldErr.setText("  Enter a number."); return; }
            try {
                int n = Integer.parseInt(txt);
                lb.updateBalance(n);
                balModel.setValueAt(lb.getRemainingBalance() + " days", 3, 1);
                setOk(msgLabel, "Updated. Remaining: " + lb.getRemainingBalance() + " days.");
                daysField.setText("");
            } catch (NumberFormatException ex) { setErr(msgLabel, "Must be a valid number."); }
              catch (IllegalArgumentException | IllegalStateException ex) { setErr(msgLabel, ex.getMessage()); }
        });

        JPanel deductRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        deductRow.setOpaque(false);
        JLabel deductLbl = mkCap("DAYS TO DEDUCT");
        deductLbl.setForeground(INK3);
        deductRow.add(deductLbl);
        deductRow.add(daysField);
        deductRow.add(btn);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(scroll);
        center.add(Box.createVerticalStrut(20));
        center.add(fullRule());
        center.add(Box.createVerticalStrut(14));
        center.add(mkCap("UPDATE BALANCE"));
        center.add(Box.createVerticalStrut(8));
        center.add(deductRow);
        center.add(Box.createVerticalStrut(4));
        center.add(fieldErr);
        center.add(Box.createVerticalStrut(4));
        center.add(msgLabel);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── Personal Details ──────────────────────────────────────────────────

    static JPanel buildPersonalDetails(JFrame parent) {
        JPanel p = tabPanel();
        p.add(sectionHeader("Personal Details", "Profile"), BorderLayout.NORTH);

        PersonalDetails pd = employee.getPersonalDetails();

        JTextField addrField    = styledField(); addrField.setText(pd.getAddress());
        JTextField contactField = styledField(); contactField.setText(pd.getContactNumber());
        JTextField bankField    = styledField(); bankField.setText(pd.getBankDetails());

        JLabel addrErr = errLabel(); JLabel contactErr = errLabel(); JLabel bankErr = errLabel();
        JLabel msgLabel = statusLabel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 0, 2, 12);
        g.fill   = GridBagConstraints.HORIZONTAL;

        addFormRow(form, g, 0, "ADDRESS",        addrField,    addrErr);
        addFormRow(form, g, 1, "CONTACT NUMBER", contactField, contactErr);
        addFormRow(form, g, 2, "BANK DETAILS",   bankField,    bankErr);

        JButton saveBtn = mkDarkBtn("SAVE CHANGES");
        saveBtn.addActionListener(e -> {
            addrErr.setText(" "); contactErr.setText(" "); bankErr.setText(" ");
            msgLabel.setText(" "); msgLabel.setForeground(GREEN_OK);
            boolean valid = true;
            if (addrField.getText().trim().isEmpty()) { addrErr.setText("  Cannot be empty."); valid = false; }
            String c = contactField.getText().trim();
            if (c.isEmpty()) { contactErr.setText("  Required."); valid = false; }
            else if (!c.matches("\\d{10,11}")) { contactErr.setText("  Must be 10-11 digits."); valid = false; }
            if (bankField.getText().trim().isEmpty()) { bankErr.setText("  Cannot be empty."); valid = false; }
            if (!valid) { setErr(msgLabel, "Please fix the errors above."); return; }

            java.util.List<String> errs = new java.util.ArrayList<>();
            try { pd.updateAddress(addrField.getText().trim()); } catch (Exception ex) { errs.add(ex.getMessage()); }
            try { pd.updateContactNumber(contactField.getText().trim()); } catch (Exception ex) { errs.add(ex.getMessage()); }
            try { pd.updateBankDetails(bankField.getText().trim()); } catch (Exception ex) { errs.add(ex.getMessage()); }
            if (errs.isEmpty()) setOk(msgLabel, "Details saved successfully.");
            else setErr(msgLabel, String.join("  ·  ", errs));
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(form);
        center.add(Box.createVerticalStrut(18));
        center.add(saveBtn);
        center.add(Box.createVerticalStrut(8));
        center.add(msgLabel);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────────────────────────────
    //  UI Helpers
    // ─────────────────────────────────────────

    static JPanel tabPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(26, 30, 20, 30));
        return p;
    }

    static JPanel sectionHeader(String title, String kicker) {
        JPanel ph = new JPanel();
        ph.setOpaque(false);
        ph.setLayout(new BoxLayout(ph, BoxLayout.Y_AXIS));
        JLabel capLabel = mkCap(kicker.toUpperCase());
        capLabel.setForeground(ORANGE);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SERIF_H2);
        titleLabel.setForeground(CHARCOAL);
        JPanel rulePanel = new JPanel();
        rulePanel.setOpaque(false);
        rulePanel.setLayout(new BoxLayout(rulePanel, BoxLayout.X_AXIS));
        JPanel shortRule = new JPanel();
        shortRule.setBackground(RED_DEEP);
        shortRule.setMaximumSize(new Dimension(28, 3));
        shortRule.setPreferredSize(new Dimension(28, 3));
        rulePanel.add(shortRule);
        ph.add(capLabel);
        ph.add(Box.createVerticalStrut(6));
        ph.add(titleLabel);
        ph.add(Box.createVerticalStrut(10));
        ph.add(rulePanel);
        ph.add(Box.createVerticalStrut(18));
        return ph;
    }

    static JPanel buildDataRows(String[][] rows) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < rows.length; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(true);
            row.setBackground(i % 2 == 0 ? SURFACE : ROW_ALT);
            row.setBorder(new EmptyBorder(9, 14, 9, 14));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            JLabel key = new JLabel(rows[i][0]);
            key.setFont(SANS_CAP);
            key.setForeground(INK3);
            key.setPreferredSize(new Dimension(160, 20));
            JLabel val = new JLabel(rows[i][1]);
            val.setFont(new Font("Georgia", Font.PLAIN, 13));
            val.setForeground(CHARCOAL);
            row.add(key, BorderLayout.WEST);
            row.add(val, BorderLayout.CENTER);
            panel.add(row);
        }
        return panel;
    }

    static JTable mkTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(SANS);
        t.setForeground(INK2);
        t.setBackground(SURFACE);
        t.setRowHeight(26);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(SANS_CAP);
        t.getTableHeader().setBackground(CHARCOAL);
        t.getTableHeader().setForeground(ORANGE);
        t.getTableHeader().setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                lbl.setBackground(r % 2 == 0 ? SURFACE : ROW_ALT);
                lbl.setForeground(c == 4 ? GREEN_OK : INK2);
                lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
                lbl.setFont(SANS);
                return lbl;
            }
        });
        return t;
    }

    static JTextField styledField() {
        JTextField f = new JTextField();
        styleField(f); return f;
    }

    static void styleField(JComponent f) {
        f.setFont(SANS);
        f.setBackground(SURFACE);
        f.setForeground(CHARCOAL);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    static JButton mkDarkBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(SANS_CAP);
        b.setBackground(RED_DEEP);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        return b;
    }

    static JButton mkLightBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(SANS_CAP);
        b.setBackground(SURFACE);
        b.setForeground(CHARCOAL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE, 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        return b;
    }

    static JLabel mkCap(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SANS_CAP);
        l.setForeground(ORANGE);
        return l;
    }

    static JLabel mkCapLabel(String text) {
        JLabel l = mkCap(text);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static JLabel errLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(SANS_SMALL);
        l.setForeground(RED_ERR);
        return l;
    }

    static JLabel statusLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(SANS_SMALL);
        l.setForeground(GREEN_OK);
        return l;
    }

    static void setOk(JLabel l, String msg)  { l.setForeground(GREEN_OK); l.setText("  " + msg); }
    static void setErr(JLabel l, String msg) { l.setForeground(RED_ERR);  l.setText("  " + msg); }

    static JPanel fullRule() {
        JPanel rp = new JPanel();
        rp.setBackground(RULE);
        rp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        rp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        return rp;
    }

    static JPanel mkRule(int width) {
        return mkRule(width, RED_DEEP);
    }
    static JPanel mkRule(int width, Color c) {
        JPanel rp = new JPanel();
        rp.setBackground(c);
        rp.setMaximumSize(new Dimension(width, 3));
        rp.setPreferredSize(new Dimension(width, 3));
        return rp;
    }

    static JPanel previewWrap(JLabel label) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(ROW_ALT);
        wrap.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RULE, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        wrap.add(label, BorderLayout.WEST);
        return wrap;
    }

    static void addFormRow(JPanel form, GridBagConstraints g, int row,
                           String label, JComponent field, JLabel errLabel) {
        g.gridx = 0; g.gridy = row * 2; g.weightx = 0.28;
        form.add(mkCap(label), g);
        g.gridx = 1; g.weightx = 0.72;
        form.add(field, g);
        if (errLabel != null) {
            g.gridx = 1; g.gridy = row * 2 + 1;
            form.add(errLabel, g);
        }
    }

    static String fmt(double n) {
        return String.format("PHP %,.2f", n);
    }
}
