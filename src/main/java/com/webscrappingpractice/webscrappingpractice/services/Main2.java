package com.webscrappingpractice.webscrappingpractice.services;

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

public class Main2 {
    public static void main(String[] args) throws IOException {
        // Base URL
        String baseUrl = "https://quotes.toscrape.com";

        // প্রথম পেজ লোড
        Document doc = Jsoup
                .connect(baseUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .get();

        List<Quote> quotes = new ArrayList<>();

        // প্রথম পেজ + pagination লুপ
        Elements nextElements = doc.select(".next");
        while (true) {

            Elements quoteElements = doc.select(".quote");

            for (Element quoteElement : quoteElements) {
                Quote quote = new Quote();

                String text = quoteElement.select(".text").first().text()
                        .replace("“", "")
                        .replace("”", "");
                String author = quoteElement.select(".author").first().text();

                List<String> tags = new ArrayList<>();
                for (Element tag : quoteElement.select(".tag")) {
                    tags.add(tag.text());
                }

                quote.setText(text);
                quote.setAuthor(author);
                quote.setTags(String.join(", ", tags));

                quotes.add(quote);
            }

            // পরের পেজ আছে কিনা চেক করা
            nextElements = doc.select(".next");
            if (nextElements.isEmpty()) {
                break; // লুপ থেকে বের হওয়া
            }

            // পরের পেজ লোড করা
            String relativeUrl = nextElements.first().select("a").attr("href");
            String completeUrl = baseUrl + relativeUrl;

            doc = Jsoup
                    .connect(completeUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .get();
        }

        // সব ডেটা আনার পরে CSV তে লেখা

       // File csvFile = new File("output_" + System.currentTimeMillis() + ".csv");

        File csvFile = new File("output.csv");
        try (PrintWriter printWriter = new PrintWriter(csvFile, StandardCharsets.UTF_8)) {
            // BOM
            printWriter.write('\ufeff');

            // Header
            printWriter.println("\"Quote\",\"Author\",\"Tags\"");

            for (Quote quote : quotes) {
                List<String> row = new ArrayList<>();
                row.add("\"" + quote.getText() + "\"");
                row.add("\"" + quote.getAuthor() + "\"");
                row.add("\"" + quote.getTags() + "\"");

                printWriter.println(String.join(",", row));
            }
        }

        System.out.println("Scraping completed. " + quotes.size() + " quotes saved to output.csv");
    }
}
