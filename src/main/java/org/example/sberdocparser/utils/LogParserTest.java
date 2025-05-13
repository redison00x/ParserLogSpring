package org.example.sberdocparser.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LogParserTest {
    private static final Logger logger = LoggerFactory.getLogger(LogParserTest.class);
    private static final String OUTPUT_FILE = "output.html";
    public static void main(String[] args) {
        String logFilePath = "C:\\Users\\CWSoft\\Desktop\\log.log";
        LogParser logParser = new LogParser(logFilePath);
        List<String> output = logParser.parseLogFile();
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE))) {
            // HTML шапка
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"ru\">");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>Log Parser Output</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Log Parser Output</h1>");
            // HTML вывод
            for (String line : output) {
                if (line.equals("---")) {
                    out.println("<hr>");
                } else {
                    out.println("<p>" + line + "</p>");
                }
            }
            // HTML подвал
            out.println("</body>");
            out.println("</html>");
            logger.info("Результаты записаны в " + OUTPUT_FILE);
        } catch (IOException e) {
            logger.error("Ошибка записи в файл: " + e.getMessage(), e);
        }
    }
}