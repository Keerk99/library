package com.alura.literatura.main;

import com.alura.literatura.model.DataAuthor;
import com.alura.literatura.model.DataBook;
import com.alura.literatura.model.DataGutendex;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

public class mainTest {
    public static void main(String[] args) {
        ConvierteDatos conversor = new ConvierteDatos();
        ConsumoAPI consumoAPI = new ConsumoAPI();

        String apiUrl = "https://gutendex.com/books/";
        String jsonData = consumoAPI.getData(apiUrl);

        DataGutendex response = conversor.obtenerDatos(jsonData, DataGutendex.class);
        for (DataBook book : response.results()) {
            System.out.println("Book title: " + book.title());
            System.out.print("Author: ");
            for (DataAuthor author : book.authors()) {
                System.out.println(author.name());
            }
            System.out.println("Languages: " + book.languages());
            System.out.println("Download Count: " + book.downloadCount());
            System.out.println();
        }
    }
}
