import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ATMWithdrawal {
    private static final double BALANCE = 5000.00;

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Enter account number: ");
            String accountNumber = input.nextLine().trim();

            if (accountNumber.isEmpty()) {
                throw new IllegalArgumentException("Account number cannot be empty.");
            }

            String cardStatus = Files.readString(Path.of("card-reader.txt")).trim();
            if (!cardStatus.equalsIgnoreCase("ACTIVE")) {
                throw new IllegalStateException("Card is not active.");
            }

            System.out.print("Enter withdrawal amount: ");
            double amount;
            try {
                amount = input.nextDouble();
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Withdrawal amount must be numeric.", e);
            }

            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive.");
            }
            if (amount > BALANCE) {
                throw new IllegalArgumentException("Insufficient account balance.");
            }
            if (amount % 100 != 0) {
                throw new IllegalArgumentException("Enter an amount in multiples of 100.");
            }

            System.out.printf("Withdrawal successful. Account: %s, Amount: Rs. %.2f%n",
                    accountNumber, amount);
            System.out.printf("Remaining balance: Rs. %.2f%n", BALANCE - amount);
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("Card reader error: card-reader.txt is missing.");
            e.printStackTrace(System.out);
        } catch (IOException e) {
            System.out.println("Card reader error: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Transaction rejected: " + e.getMessage());
        } finally {
            System.out.println("ATM session closed. Finally block executed.");
        }
    }
}