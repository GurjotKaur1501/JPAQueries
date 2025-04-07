package se.yrgo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@NamedQuery(name = "Book.findByGenre", query = "SELECT b FROM Book b WHERE b.genre = :genre")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String genre;
    private int publicationYear;

    @ManyToMany(mappedBy = "books")
    private List<Reader> readers = new ArrayList<>();

    // Constructors, getters, setters
    public Book() {}

    public Book(String title, String genre, int publicationYear) {
        this.title = title;
        this.genre = genre;
        this.publicationYear = publicationYear;
    }

    // Getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public List<Reader> getReaders() { return readers; }
    public void setReaders(List<Reader> readers) { this.readers = readers; }
}