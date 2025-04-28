package org.example.sberdocparser.controller;

import org.example.sberdocparser.model.LogEntry;
import org.example.sberdocparser.service.LogParserService;
import org.example.sberdocparser.service.LogWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(LogParserController.class);

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

        File tempFile = null;
        try {
            tempFile = File.createTempFile("log", ".log");
            logger.info("Временный файл создан: " + tempFile.getAbsolutePath());

            file.transferTo(tempFile);
            logger.info("Размер временного файла: " + tempFile.length() + " байт");

            String outputFilePath = "output.txt";
            List<LogEntry> logEntries = logParserService.parseLogFile(tempFile.getAbsolutePath());
            logger.info("Найдено log entries: " + logEntries.size());

            logWriterService.writeLogToFile(logEntries, outputFilePath);
            logger.info("Файл записан: " + outputFilePath);

            model.addAttribute("message", "Файл распарсен в output.txt");
            return "status";

        } catch (IOException e) {
            logger.error("Ошибка парсинга файла: " + e.getMessage(), e);
            model.addAttribute("message", "Ошибка парсинга файла: " + e.getMessage());
            return "status";

        } finally {
            if (tempFile != null) {
                if(tempFile.delete()) {
                    logger.info("Временный файл удален: " + tempFile.getAbsolutePath());
                } else {
                    logger.error("Не удалось удалить временный файл: " + tempFile.getAbsolutePath());
                }
            }
        }
    }
}
