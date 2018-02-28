package com.example.patrycja.filmbase.service;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileLinesReader {

    private List<String> lines;

    public FileLinesReader(String filePath) {
        lines = new ArrayList<>();
        try {
            lines = Files.lines(
                    Paths.get(
                            new ClassPathResource(
                                    filePath)
                                    .getURI()))
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public List<String> getLines() {
        return lines;
    }

}
