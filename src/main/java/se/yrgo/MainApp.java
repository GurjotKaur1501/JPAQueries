package com.example;

import javax.persistence.*;
import java.util.List;

public class MainApp {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("BookStorePU");
        em = emf.createEntityManager();

        try {
            // Task 1: Create and store data
            createAndStoreData();

            // Task 2: Get all books by a specific author
            getBooksByAuthor("Author 1");

            // Task 3: Get all readers who read a specific book
            getReadersByBook("Book 1");

            // Task 4: Get authors whose books have been read by at least one reader
            getAuthorsWithReaders();

            // Task 5: Count books per author
            countBooksPerAuthor();

            // Task 6: Named Query - Get books by genre
            getBooksByGenre("Fiction");

        } finally {
            em.close();
            emf.close();
        }
    }

    // Task 1: Create and store data
    private static void createAndStoreData() {
        em.getTransaction().begin();

        // Create authors
        Author author1 = new Author("Author 1", "Swedish");
        Author author2 = new Author("Author 2", "American");
        Author author3 = new Author("Author 3", "British");

        // Create books
        Book book1 = new Book("Book 1", "Fiction", 2020);
        Book book2 = new Book("Book 2", "Non-Fiction", 2019);
        Book book3 = new Book("Book 3", "Fiction", 2018);
        Book book4 = new Book("Book 4", "Science", 2021);
        Book book5 = new Book("Book 5", "Fantasy", 2017);

        // Associate books with authors
        author1.addBook(book1);
        author1.addBook(book2);
        author2.addBook(book3);
        author3.addBook(book4);
        author3.addBook(book5);

        // Create readers
        Reader reader1 = new Reader("Reader 1", "reader1@example.com");
        Reader reader2 = new Reader("Reader 2", "reader2@example.com");
        Reader reader3 = new Reader("Reader 3", "reader3@example.com");

        // Associate books with readers
        reader1.addBook(book1);
        reader1.addBook(book3);
        reader2.addBook(book1);
        reader2.addBook(book4);
        reader3.addBook(book2);
        reader3.addBook(book5);

        // Persist all
        em.persist(author1);
        em.persist(author2);
        em.persist(author3);
        em.persist(reader1);
        em.persist(reader2);
        em.persist(reader3);

        em.getTransaction().commit();
    }

    // Task 2: Get all books by a specific author
    private static void getBooksByAuthor(String authorName) {
        System.out.println("\n=== Books by " + authorName + " ===");

        String jpql = "SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.name = :name";
        Author author = em.createQuery(jpql, Author.class)
                .setParameter("name", authorName)
                .getSingleResult();

        for (Book book : author.getBooks()) {
            System.out.println(book.getTitle() + " (" + book.getGenre() + ")");
        }
    }

    // Task 3: Get all readers who read a specific book
    private static void getReadersByBook(String bookTitle) {
        System.out.println("\n=== Readers who read " + bookTitle + " ===");

        String jpql = "SELECT r FROM Reader r WHERE :book MEMBER OF r.books";
        Book book = em.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class)
                .setParameter("title", bookTitle)
                .getSingleResult();

        List<Reader> readers = em.createQuery(jpql, Reader.class)
                .setParameter("book", book)
                .getResultList();

        for (Reader reader : readers) {
            System.out.println(reader.getName() + " (" + reader.getEmail() + ")");
        }
    }

    // Task 4: Get authors whose books have been read by at least one reader
    private static void getAuthorsWithReaders() {
        System.out.println("\n=== Authors with books read by readers ===");

        String jpql = "SELECT DISTINCT a FROM Author a JOIN a.books b JOIN b.readers r";
        List<Author> authors = em.createQuery(jpql, Author.class).getResultList();

        for (Author author : authors) {
            System.out.println(author.getName() + " (" + author.getNationality() + ")");
        }
    }

    // Task 5: Count books per author
    private static void countBooksPerAuthor() {
        System.out.println("\n=== Book count per author ===");

        String jpql = "SELECT a.name, COUNT(b) FROM Author a LEFT JOIN a.books b GROUP BY a.name";
        List<Object[]> results = em.createQuery(jpql).getResultList();

        for (Object[] result : results) {
            System.out.println(result[0] + ": " + result[1] + " books");
        }
    }

    // Task 6: Named Query - Get books by genre
    private static void getBooksByGenre(String genre) {
        System.out.println("\n=== Books in genre: " + genre + " ===");

        List<Book> books = em.createNamedQuery("Book.findByGenre", Book.class)
                .setParameter("genre", genre)
                .getResultList();

        for (Book book : books) {
            System.out.println(book.getTitle() + " by " +
                    getAuthorNameForBook(book) + " (" + book.getPublicationYear() + ")");
        }
    }

    private static String getAuthorNameForBook(Book book) {
        String jpql = "SELECT a.name FROM Author a WHERE :book MEMBER OF a.books";
        try {
            return em.createQuery(jpql, String.class)
                    .setParameter("book", book)
                    .getSingleResult();
        } catch (NoResultException e) {
            return "Unknown author";
        }
    }
}