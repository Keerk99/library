package com.alura.literatura.repository;
import com.alura.literatura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.birthYear BETWEEN :year AND (:year + 100)")
    List<Book> findAuthorAliveInACentury(int year);

    List<Book> findByLanguages(String language);

    @Query("SELECT DISTINCT b.languages FROM Book b")
    List<String> findDistinctLanguages();
}
