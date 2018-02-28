package com.example.patrycja.filmbase.service;

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
    private long count;

    public RequestGenerator() {
        requestList = new ArrayList<>();
        fileLinesReader = new FileLinesReader(filePath);

        for(String lineDivider : fileLinesReader.getLines()) {
            String[] content = lineDivider
                    .split("; ");
            List<String> typeList = Arrays
                    .asList(
                            content[2]
                                    .split(", "));

            requestList.add(new AddFilmRequest(
                    content[0],
                    content[1],
                    typeList,
                    Integer.parseInt(content[3])
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
}
