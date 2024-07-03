package com.alura.literatura.main;

import com.alura.literatura.model.*;
import com.alura.literatura.repository.BookRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private final Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos converter = new ConvierteDatos();
    private List<Book> books = new ArrayList<>();
    private BookRepository repository;

    public Main(BookRepository repository) {
        this.repository = repository;
    }

    public void showMenu() {
        var option = -1;

        while (option != 0) {
            var menu = """
                    1.- Buscar libro por título
                    2.- Listar libros registrados
                    3.- Listar autores registrados
                    4.- Listar autores vivos en un determinado año
                    5.- Listar libros por idioma
                    
                    0.- Salir
                    """;

            System.out.println(menu);
            option  = teclado.nextInt();
            teclado.nextLine();

            switch (option) {
                case 1:
                    searchBookWeb();
                    break;
                case 2:
                    listAllBooks();
                    break;
                case 3:
                    listAllAuthors();
                    break;
                case 4:
                    findAuthorAliveInACentury();
                    break;
                case 5:
                    listBooksByLanguage();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DataGutendex getDataBook() {
        System.out.print("Por favor escribe el nombre del libro que deseas buscar: ");
        var bookName = teclado.nextLine();
        var json = consumoAPI.getData(URL_BASE+"?search="+bookName.replace(" ", "%20"));
        //System.out.println(json);
        DataGutendex dataGutendex = converter.obtenerDatos(json, DataGutendex.class);
        return dataGutendex;
    }

    private void searchBookWeb() {
        DataGutendex data = getDataBook();
        List<DataBook> results = data.results();
        if (!results.isEmpty()) {
            DataBook firstBook = results.get(0);
            //System.out.println(firstBook);
            Book book = new Book(firstBook);

            Optional<Book> existingBook = repository.findByTitle(book.getTitle());
            if (existingBook.isPresent()) {
                System.out.println("""
                        Este libro ya se encuentra registrado
                        --------------------------
                        """);
            } else {

                repository.save(book);
                //System.out.println(book);
                String authorsString = getAuthorsString(book.getAuthors());
                System.out.printf("""
                        ---------- LIBRO ---------
                        Título: %s
                        Autor: %s
                        Idioma: %s
                        Número de descargas: %s
                        --------------------------
                        
                        --------------------------
                        """, book.getTitle(), authorsString, book.getLanguages(), book.getDownloadCount());
            }
        } else System.out.println("""
                El libro que desea buscar no ha sido encontrado
                --------------------------
                """);
    }

    private void listAllBooks() {
        books = repository.findAll();

        books.forEach(b -> {
            String authorsString = getAuthorsString(b.getAuthors());
            System.out.printf("""
                Título: %s
                Autor: %s
                Idioma: %s
                Número de descargas: %s
                
                """, b.getTitle(), authorsString, b.getLanguages(), b.getDownloadCount());
        });
    }

    private String getAuthorsString(List<Author> authors) {
        StringBuilder authorsString = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                authorsString.append("; ");
            }
            authorsString.append(authors.get(i).getName());
        }
        return authorsString.toString();
    }

    private void listAllAuthors() {
        books = repository.findAll();
        books.forEach(b ->
                b.getAuthors().forEach(a -> {
                    System.out.printf("""
                            Name: %s
                            Birth Year: %s
                            Death Year: %s
                            Libros: %s
                            
                            """, a.getName(), a.getBithYear(), a.getDeathYear(), b.getTitle());
                }));
    }

    private void findAuthorAliveInACentury() {
        System.out.print("Ingrese el año que desea buscar: ");
        int year = teclado.nextInt();
        teclado.nextLine();
        books = repository.findAuthorAliveInACentury(year);

        if (!books.isEmpty()) {
            books.forEach(b -> {
                b.getAuthors().forEach(a -> {
                    System.out.printf("""
                            Name: %s
                            Birth Year: %s
                            Death Year: %s
                            Libros: %s
                            
                            """, a.getName(), a.getBithYear(), a.getDeathYear(), b.getTitle());
                });
            });
        } else {
            System.out.println("No se encontraron autores vivos en el siglo especificado.");
        }
    }

    private void listBooksByLanguage() {
        List<String> languages = repository.findDistinctLanguages();
        System.out.println("Lita de idiomas:");
        languages.forEach(System.out::println);

        System.out.print("Ingrese el idioma que desea buscar: ");
        var language = teclado.nextLine();
        books = repository.findByLanguages(language);

        if (!books.isEmpty()) {
            books.forEach(b -> {
                String authorsString = getAuthorsString(b.getAuthors());
                System.out.printf("""
                Título: %s
                Autor: %s
                Idioma: %s
                Número de descargas: %s
                
                """, b.getTitle(), authorsString, b.getLanguages(), b.getDownloadCount());
            });
        } else {
            System.out.println("No se encontraron libros con el idioma especificado.");
        }
    }
}