import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

// ==========================================
// 1. ACCOUNT CLASS (holds all account data)
// ==========================================
class Account {
    private String accountNumber;
    private String holderName;
    private String password;
    private double balance;
    private List<String> transactionHistory;

    public Account(String accountNumber, String holderName, String password) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.password = password;
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with balance $0.00");
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public boolean checkPassword(String password) { return this.password.equals(password); }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposited $" + amount);
        } else {
            System.out.println("❌ Deposit amount must be positive.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Withdrawal amount must be positive.");
            return false;
        }
        if (amount > balance) {
            System.out.println("❌ Insufficient balance. Available: $" + balance);
            return false;
        }
        balance -= amount;
        addTransaction("Withdrew $" + amount);
        return true;
    }

    public void transfer(Account target, double amount) {
        if (target == null) {
            System.out.println("❌ Target account not found.");
            return;
        }
        if (this == target) {
            System.out.println("❌ Cannot transfer to yourself.");
            return;
        }
        if (withdraw(amount)) { // withdraw from this account first
            target.deposit(amount);
            addTransaction("Transferred $" + amount + " to " + target.getAccountNumber());
            target.addTransaction("Received $" + amount + " from " + this.accountNumber);
            System.out.println("✅ Transfer successful.");
        }
    }

    private void addTransaction(String detail) {
        transactionHistory.add(detail);
    }

    public void showTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            System.out.println("\n--- Transaction History ---");
            for (String t : transactionHistory) {
                System.out.println("  • " + t);
            }
        }
    }

    @Override
    public String toString() {
        return "Account: " + accountNumber + " | Holder: " + holderName + " | Balance: $" + balance;
    }
}

// ==========================================
// 2. MAIN APPLICATION CLASS
// ==========================================
public class BankApplication {
    // In‑memory database: accountNumber → Account
    private static HashMap<String, Account> accounts = new HashMap<>();
    private static Account loggedInAccount = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Pre‑load some accounts for testing
        accounts.put("A1001", new Account("A1001", "Alice", "alice123"));
        accounts.put("A1002", new Account("A1002", "Bob", "bob456"));
        accounts.put("A1003", new Account("A1003", "Charlie", "charlie789"));

        System.out.println("🏦 Welcome to the Java Bank System!");
        System.out.println("(Test accounts: A1001/alice123, A1002/bob456, A1003/charlie789)");

        while (true) {
            displayMainMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> login();
                case 3 -> {
                    System.out.println("👋 Thank you for using our bank. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
            System.out.println("\n-----------------------------------");
        }
    }

    // ---------- MAIN MENU ----------
    private static void displayMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        if (loggedInAccount != null) {
            System.out.println("👤 Logged in as: " + loggedInAccount.getHolderName() 
                               + " (" + loggedInAccount.getAccountNumber() + ")");
        } else {
            System.out.println("👤 Status: Not logged in");
        }
        System.out.println("1. Create Account");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    // ---------- SAFE INPUT ----------
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("❌ Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("❌ Please enter a valid amount.");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    // ---------- 1. CREATE ACCOUNT ----------
    private static void createAccount() {
        System.out.print("Enter desired account number: ");
        String accNo = scanner.next();
        if (accounts.containsKey(accNo)) {
            System.out.println("❌ Account number already exists. Choose another.");
            return;
        }
        System.out.print("Enter full name: ");
        scanner.nextLine(); // consume newline
        String name = scanner.nextLine();
        System.out.print("Set a password: ");
        String pwd = scanner.next();

        Account newAcc = new Account(accNo, name, pwd);
        accounts.put(accNo, newAcc);
        System.out.println("✅ Account created successfully! Account number: " + accNo);
        System.out.println("You can now log in.");
    }

    // ---------- 2. LOGIN ----------
    private static void login() {
        if (loggedInAccount != null) {
            System.out.println("⚠️ You are already logged in as " + loggedInAccount.getHolderName());
            return;
        }
        System.out.print("Enter account number: ");
        String accNo = scanner.next();
        System.out.print("Enter password: ");
        String pwd = scanner.next();

        Account acc = accounts.get(accNo);
        if (acc != null && acc.checkPassword(pwd)) {
            loggedInAccount = acc;
            System.out.println("✅ Login successful! Welcome, " + acc.getHolderName() + ".");
            // After login, show the banking menu
            bankingMenu();
        } else {
            System.out.println("❌ Invalid account number or password.");
        }
    }

    // ---------- BANKING MENU (after login) ----------
    private static void bankingMenu() {
        while (true) {
            System.out.println("\n===== BANKING MENU =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Check Balance");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = getIntInput();

            switch (choice) {
                case 1 -> deposit();
                case 2 -> withdraw();
                case 3 -> transfer();
                case 4 -> checkBalance();
                case 5 -> transactionHistory();
                case 6 -> {
                    logout();
                    return; // exit banking menu back to main menu
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    // ---------- DEPOSIT ----------
    private static void deposit() {
        System.out.print("Enter amount to deposit: $");
        double amount = getDoubleInput();
        loggedInAccount.deposit(amount);
        System.out.println("✅ Deposit successful. New balance: $" + loggedInAccount.getBalance());
    }

    // ---------- WITHDRAW ----------
    private static void withdraw() {
        System.out.print("Enter amount to withdraw: $");
        double amount = getDoubleInput();
        boolean success = loggedInAccount.withdraw(amount);
        if (success) {
            System.out.println("✅ Withdrawal successful. New balance: $" + loggedInAccount.getBalance());
        }
    }

    // ---------- TRANSFER ----------
    private static void transfer() {
        System.out.print("Enter target account number: ");
        String targetAccNo = scanner.next();
        Account target = accounts.get(targetAccNo);
        if (target == null) {
            System.out.println("❌ Target account does not exist.");
            return;
        }
        System.out.print("Enter amount to transfer: $");
        double amount = getDoubleInput();
        loggedInAccount.transfer(target, amount);
    }

    // ---------- CHECK BALANCE ----------
    private static void checkBalance() {
        System.out.println("💰 Current balance: $" + loggedInAccount.getBalance());
    }

    // ---------- TRANSACTION HISTORY ----------
    private static void transactionHistory() {
        loggedInAccount.showTransactionHistory();
    }

    // ---------- LOGOUT ----------
    private static void logout() {
        System.out.println("👋 Logged out successfully.");
        loggedInAccount = null;
    }
}