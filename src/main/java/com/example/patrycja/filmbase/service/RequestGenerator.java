package com.example.patrycja.filmbase.service;

import com.example.patrycja.filmbase.model.Actor;
import com.example.patrycja.filmbase.model.Director;
import com.example.patrycja.filmbase.request.AddActorRequest;
import com.example.patrycja.filmbase.request.AddFilmRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RequestGenerator {

    private final String filePath = "Films";
    private final FileLinesReader fileLinesReader;
    private List<AddFilmRequest> requestList;
    private List<AddActorRequest> actorList;
    private long count;

    public RequestGenerator() {
        requestList = new ArrayList<>();
        actorList = new ArrayList<>();
        fileLinesReader = new FileLinesReader(filePath);

        for(String lineDivider : fileLinesReader.getLines()) {
            String[] content = lineDivider
                    .split("; ");
            List<String> typeList = Arrays
                    .asList(
                            content[3]
                                    .split(", "));

            String[] fullNames = content[5].split(", ");
            List<AddActorRequest> cast = new ArrayList<>();
            for(String name : fullNames) {
                String[] separatedNames = name.split("_");
                cast.add(new AddActorRequest(separatedNames[0], separatedNames[1]));
                actorList.add(new AddActorRequest(separatedNames[0], separatedNames[1]));
            }

            requestList.add(new AddFilmRequest(
                    content[0],
                    typeList,
                    Integer.parseInt(content[4]),
                    content[1],
                    content[2],
                    cast
            ));
        }

        count = requestList.size();
    }

    public AddFilmRequest getRequest(int index) {
        return requestList.get(index);
    }

    public long getCount() {
        return count;
    }

    public AddActorRequest getActor(int index) { return actorList.get(index); }
}
