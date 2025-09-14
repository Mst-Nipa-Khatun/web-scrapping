package com.webscrappingpractice.webscrappingpractice.controller;


import com.webscrappingpractice.webscrappingpractice.dto.JobNewsDto;
import com.webscrappingpractice.webscrappingpractice.services.JobNewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class JobNewsController {
    private final JobNewsService jobNewsService;

    public JobNewsController(JobNewsService jobNewsService) {
        this.jobNewsService = jobNewsService;
    }
    @GetMapping("/jobNews")
    public List<JobNewsDto> fetchJobsNews() {
        try {
            return jobNewsService.getJobNews();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
