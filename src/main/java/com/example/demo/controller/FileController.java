package com.example.demo.controller;

import com.example.demo.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController {
    @SneakyThrows
    @GetMapping("/files/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse response) {
        File f = new File("uploads" + File.separator + filename);
        if (!f.exists()) {
            throw new NotFoundException("Files does not exist");
        }
        Files.copy(f.toPath(), response.getOutputStream());
    }
}
