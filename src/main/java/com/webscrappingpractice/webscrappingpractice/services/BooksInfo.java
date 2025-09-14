package com.webscrappingpractice.webscrappingpractice.services;

import com.webscrappingpractice.webscrappingpractice.dto.Book;
import com.webscrappingpractice.webscrappingpractice.dto.Quote;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BooksInfo {
    public static void main(String[] args) throws IOException {
        String baseUrl = "http://books.toscrape.com/catalogue/page-1.html";
        List<Book> books = new ArrayList<>();

        Document doc = Jsoup.connect(baseUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .get();

        books.addAll(extractBooks(doc));

        Elements nextElements = doc.select(".next");
        while (!nextElements.isEmpty()) {
            String relativeUrl = nextElements.first().select("a").attr("href");
            String fullUrl = baseUrl.replace("page-1.html", "") + relativeUrl;

            doc = Jsoup.connect(fullUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();

            books.addAll(extractBooks(doc));
            nextElements = doc.select(".next");
        }
        File csvFile = new File("D:/SEBPO/webScrappingPractice/bookOutput_new.csv"); // Absolute path
        csvFile.getParentFile().mkdirs();

        try (PrintWriter printWriter = new PrintWriter(csvFile, StandardCharsets.UTF_8)) {
            printWriter.write('\ufeff');
            printWriter.println("\"Title\",\"Price\",\"Rating\"");

            for (Book book : books) {
                List<String> row = new ArrayList<>();
                row.add("\"" + book.getTitle() + "\"");
                row.add("\"" + book.getPrice() + "\"");
                row.add("\"" + book.getRating() + "\"");
                printWriter.println(String.join(",", row));
            }
        }

        System.out.println("Scraping completed. " + books.size() + " Books saved to " + csvFile.getAbsolutePath());

    }

    private static List<Book> extractBooks(Document doc) {
        List<Book> books = new ArrayList<>();
        Elements bookElements = doc.select("article.product_pod");

        for (Element bookElement : bookElements) {
            Book book = new Book();

            String title = bookElement.select("h3 > a").attr("title");

            String price = bookElement.select(".price_color").text();

            String ratingClass = bookElement.select(".star-rating").first().className(); // e.g., "star-rating Three"
            String rating = ratingClass.replace("star-rating", "").trim();

            book.setTitle(title);
            book.setPrice(price);
            book.setRating(rating);

            books.add(book);
        }

        return books;
    }
}
