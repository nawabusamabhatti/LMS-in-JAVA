import java.util.*;
import java.io.*;

class Book {
    private String title;
    private String author;
    private String ISBN;

    public Book(String title, String author, String ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getISBN() {
        return this.ISBN;
    }

    public String toFileString() {
        return this.title + "," + this.author + "," + this.ISBN;
    }

    public static Book fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        return new Book(parts[0], parts[1], parts[2]);
    }
}

class Administrator {
    List<Book> catalog = new LinkedList<>();
    Map<String, String> borrowedBooks = new HashMap<>();

    public Administrator() {
        loadBooks();
        loadBorrowedBooks();
    }

    public void addBook(Book book) {
        catalog.add(book);
        saveBooks();
    }

    public void recordBorrowedBook(String ISBN, String user) {
        borrowedBooks.put(ISBN, user);
        saveBorrowedBooks();
    }

    // Other methods...

    private void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                catalog.add(Book.fromFileString(line));
            }
        } catch (IOException e) {
            System.err.println("Failed to load books: " + e.getMessage());
        }
    }

    private void saveBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("books.txt", false))) {
            for (Book book : catalog) {
                writer.println(book.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Failed to save books: " + e.getMessage());
        }
    }

    private void loadBorrowedBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                borrowedBooks.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.err.println("Failed to load borrowed books: " + e.getMessage());
        }
    }

    private void saveBorrowedBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("borrowed.txt", false))) {
            for (Map.Entry<String, String> entry : borrowedBooks.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Failed to save borrowed books: " + e.getMessage());
        }
    }
    public void deleteBook(String ISBN) {
        catalog.removeIf(book -> book.getISBN().equals(ISBN));
        saveBooks();
    }
    public Book inquire(String ISBN) {
        for (Book book : catalog) {
            if (book.getISBN().equals(ISBN)) {
                return book;
            }
        }
        return null;
    }

    public int getAvailableBooks() {
        return catalog.size();
    }

    public List<Book> generateReport(String input) {
        List<Book> report = new ArrayList<>();
        for (Book book : catalog) {
            if (book.getTitle().equals(input) || book.getAuthor().equals(input) || book.getISBN().equals(input)) {
                report.add(book);
            }
        }
        return report;
    }
}

class User {
    public void inquire(Administrator admin, String ISBN) {
        Book book = admin.inquire(ISBN);
        if (book != null) {
            System.out.println("Book Found: " + book.getTitle() + ", " + book.getAuthor());
        } else {
            System.out.println("Book not found.");
        }
    }

    public void requestNewBook(Administrator admin, String title, String author, String ISBN) {
        Book newBook = new Book(title, author, ISBN);
        admin.addBook(newBook);
    }

    public void complain(String complaint) {
        System.out.println("User complaint: " + complaint);
    }
}

public class Main {
    public static void main(String[] args) {
        Administrator admin = new Administrator();
        User user = new User();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please select the user:\n1. Administrator\n2. User");
            String userType = scanner.nextLine().trim();

            if (userType.equals("1")) {
                System.out.println("Administrator menu:\na) Add book\nb) Add borrower\nc) Delete book\nd) Inquire\ne) Check availability\nf) Generate report");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "a":
                        System.out.println("Enter book title, author, and ISBN:");
                        String title = scanner.nextLine().trim();
                        String author = scanner.nextLine().trim();
                        String ISBN = scanner.nextLine().trim();
                        Book book = new Book(title, author, ISBN);
                        admin.addBook(book);
                        break;
                    case "b":
                        System.out.println("Enter book ISBN and borrower name:");
                        String borrowedISBN = scanner.nextLine().trim();
                        String borrowerName = scanner.nextLine().trim();
                        admin.recordBorrowedBook(borrowedISBN, borrowerName);
                        break;
                    case "c":
                        System.out.println("Enter book ISBN to delete:");
                        String deleteISBN = scanner.nextLine().trim();
                        admin.deleteBook(deleteISBN);
                        break;
                    case "d":
                        System.out.println("Enter book ISBN to inquire:");
                        String inquireISBN = scanner.nextLine().trim();
                        Book inquiredBook = admin.inquire(inquireISBN);
                        if (inquiredBook != null) {
                            System.out.println("Book Found: " + inquiredBook.getTitle() + ", " + inquiredBook.getAuthor());
                        } else {
                            System.out.println("Book not found.");
                        }
                        break;
                    case "e":
                        System.out.println("Number of available books: " + admin.getAvailableBooks());
                        break;
                    case "f":
                        System.out.println("Enter book title, author, or ISBN to generate report:");
                        String reportInput = scanner.nextLine().trim();
                        List<Book> report = admin.generateReport(reportInput);
                        if (report.isEmpty()) {
                            System.out.println("No books found for this input.");
                        } else {
                            for (Book reportBook : report) {
                                System.out.println("Report: " + reportBook.getTitle() + ", " + reportBook.getAuthor());
                            }
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } else if (userType.equals("2")) {
                System.out.println("User menu:\na) Inquire\nb) Request new book\nc) Complain");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "a":
                        System.out.println("Enter book ISBN to inquire:");
                        String inquireISBN = scanner.nextLine().trim();
                        user.inquire(admin, inquireISBN);
                        break;
                    case "b":
                        System.out.println("Enter book title, author, and ISBN for new book:");
                        String title = scanner.nextLine().trim();
                        String author = scanner.nextLine().trim();
                        String ISBN = scanner.nextLine().trim();
                        user.requestNewBook(admin, title, author, ISBN);
                        break;
                    case "c":
                        System.out.println("Enter your complaint:");
                        String complaint = scanner.nextLine().trim();
                        user.complain(complaint);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("Invalid user type.");
            }
        }
    }
}
