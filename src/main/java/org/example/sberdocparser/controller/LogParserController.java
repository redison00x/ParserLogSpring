package org.example.sberdocparser.controller;

import org.example.sberdocparser.model.LogEntry;
import org.example.sberdocparser.service.LogParserService;
import org.example.sberdocparser.service.LogWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class LogParserController {

    @Autowired
    private LogParserService logParserService;

    @Autowired
    private LogWriterService logWriterService;


    @GetMapping("/parse")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/parse")
    public String parseLog(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Выбери файл для загрузки");
            return "status";
        }
        try {
            File tempFile = File.createTempFile("log", ".log");
            file.transferTo(tempFile);
            String outputFilePath = "output.txt";
            List<LogEntry> logEntries = logParserService.parseLogFile(tempFile.getAbsolutePath());
            logWriterService.writeLogToFile(logEntries, outputFilePath);

            model.addAttribute("message", "Файл распарсен в output.txt");
            return "status";

        } catch (IOException e) {
            model.addAttribute("message", "Ошибка парсинга файла: " + e.getMessage());
            return "status";
        }
    }
}
