package com.example.patrycja.filmbase.template;

import com.example.patrycja.filmbase.request.AddFilmRequest;
import com.example.patrycja.filmbase.service.RequestGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.List;

public class FillBaseTemplate {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected List<AddFilmRequest> createdRequests;
    protected RequestGenerator requestGenerator;

    protected void initFilms() {
        createdRequests = new ArrayList<>();
        requestGenerator = new RequestGenerator();
        for(int i=0; i<requestGenerator.getCount(); i++) {
            createdRequests.add(requestGenerator.getRequest(i));
        }
    }

    protected void postFilms() {
        createdRequests
                .forEach(filmRequest -> restTemplate.postForObject("/films", filmRequest, Object.class));
    }
}
