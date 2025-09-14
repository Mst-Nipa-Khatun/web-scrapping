package com.webscrappingpractice.webscrappingpractice.services;

import com.webscrappingpractice.webscrappingpractice.dto.JobNewsDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobNewsService {

    private static final String BASE_URL = "https://www.prothomalo.com";
    private static final String JOBS_SECTION = BASE_URL + "/chakri";


    public List<JobNewsDto> getJobNews() throws IOException {
        List<JobNewsDto> jobNewsList = new ArrayList<>();

        Document doc = Jsoup.connect(JOBS_SECTION).get();

        // Select all article cards directly - both featured and regular articles
        Elements articles = doc.select(".news_with_item");

        System.out.println("Found " + articles.size() + " articles");

        for (Element article : articles) {
            try {
                // Extract image URL
                String imageUrl = extractImageUrl(article);

                // Extract title
                String title = extractTitle(article);

                // Extract description
                String description = extractDescription(article);

                // Extract publication time
                String publishedTime = extractPublishedTime(article);

                // Only add to list if we have at least a title
                if (!title.isEmpty()) {
                    JobNewsDto jobNews = new JobNewsDto(imageUrl, title, description);
                    jobNewsList.add(jobNews);
                    System.out.println("Added article: " + title);
                }
            } catch (Exception e) {
                // Log error and continue with next article
                System.err.println("Error parsing article: " + e.getMessage());
            }
        }

        return jobNewsList;
    }

    private String extractImageUrl(Element article) {
        // Try to find the image in img tag within picture element
        Element pictureElement = article.select("picture").first();
        if (pictureElement != null) {
            Element imgElement = pictureElement.select("img").first();
            if (imgElement != null) {
                // Check various attributes where the image URL might be stored
                String[] srcAttributes = {"src", "data-src", "data-srcset", "srcset"};
                for (String attr : srcAttributes) {
                    String src = imgElement.attr(attr);
                    if (!src.isEmpty() && !src.equals("/media-placeholder.svg")) {
                        // If srcset contains multiple URLs, take the first one
                        if (attr.equals("srcset") || attr.equals("data-srcset")) {
                            String[] urls = src.split(",");
                            if (urls.length > 0) {
                                String[] urlParts = urls[0].trim().split("\\s+");
                                if (urlParts.length > 0) {
                                    return urlParts[0];
                                }
                            }
                        } else {
                            return src;
                        }
                    }
                }
            }
        }

        // Check background-image style
        Element imageWrapper = article.select("div.card-image-wrapper").first();
        if (imageWrapper != null) {
            String style = imageWrapper.attr("style");
            if (style.contains("background-image")) {
                int start = style.indexOf("url(\"") + 5;
                int end = style.indexOf("\")");
                if (start > 4 && end > start) {
                    return style.substring(start, end);
                }
            }
        }

        // If no image found, return empty string
        return "";
    }

    private String extractTitle(Element article) {
        Element titleElement = article.select("span.tilte-no-link-parent").first();
        if (titleElement != null) {
            return titleElement.text().trim();
        }

        // Alternative selector if the above doesn't work
        titleElement = article.select("h3.headline-title").first();
        if (titleElement != null) {
            return titleElement.text().trim();
        }

        return "";
    }

    private String extractDescription(Element article) {
        Element descElement = article.select("a.excerpt").first();
        if (descElement != null) {
            return descElement.text().trim();
        }
        return "";
    }

    private String extractPublishedTime(Element article) {
        Element timeElement = article.select("time.published-at, time.published-time").first();
        if (timeElement != null) {
            return timeElement.text().trim();
        }
        return "";
    }
}