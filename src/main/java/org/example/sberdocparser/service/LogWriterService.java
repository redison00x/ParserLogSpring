package org.example.sberdocparser.service;

import org.example.sberdocparser.model.LogEntry;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class LogWriterService {
    public void writeLogToFile(List<LogEntry> logEntries, String outputFileName) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))){
            for (LogEntry logEntry : logEntries) {
                writer.write(logEntry.toString());
                writer.newLine();
            }

        }catch ( IOException e ){
            System.out.println("Ошибка записи в файл: "+ e.getMessage());
        }
    }
}
