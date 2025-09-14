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

public class Main {
    public static void main(String[] args) throws IOException {
        String baseUrl = "https://quotes.toscrape.com";
        List<Quote> quotes = new ArrayList<>();

        Document doc = Jsoup.connect(baseUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .get();

        quotes.addAll(extractQuotes(doc));

        Elements nextElements = doc.select(".next");
        while (!nextElements.isEmpty()) {
            String relativeUrl = nextElements.first().select("a").attr("href");
            String completeUrl = baseUrl + relativeUrl;

            doc = Jsoup.connect(completeUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();

            quotes.addAll(extractQuotes(doc));

            nextElements = doc.select(".next");
        }
//File csvFile = new File("output_" + System.currentTimeMillis() + ".csv");

        File csvFile = new File("output.csv");

        try (PrintWriter printWriter = new PrintWriter(csvFile, StandardCharsets.UTF_8)) {
            printWriter.write('\ufeff');
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

    private static List<Quote> extractQuotes(Document doc) {

        List<Quote> quotes = new ArrayList<>();
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

        return quotes;
    }
}
