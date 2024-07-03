package com.alura.literatura.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Author> authors;

    String languages;

    Integer downloadCount;

    public Book() {
    }

    public Book(DataBook dataBook) {
        this.title = dataBook.title();
        this.authors = dataBook.authors().stream()
                .map(Author::new)
                .peek(a -> a.setBook(this))
                .collect(Collectors.toList());
        this.languages = String.join("", dataBook.languages());
        this.downloadCount = dataBook.downloadCount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        authors.forEach(a -> a.setBook(this));
        this.authors = authors;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors.toString() +
                ", languages=" + languages +
                ", downloadCount=" + downloadCount;
    }

}
