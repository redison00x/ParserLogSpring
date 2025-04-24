package org.example.sberdocparser.service;

import org.example.sberdocparser.model.LogEntry;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogParserService {

    public List<LogEntry> parseLogFile(String fileName) {
        List<LogEntry> logEntries = new ArrayList<LogEntry>();
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Pattern logPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{3}) SBKRNL: Command = (\\d+), Amount = (\\d+\\.\\d+)");
            Pattern resultPattern = Pattern.compile("Result = 0, Amount = (\\d+\\.\\d+), Card = '\\*\\*\\*\\*\\*\\*\\*\\*\\*(\\d{4})'");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm:ss");

            while((line = br.readLine())!= null){
                Matcher matcher = logPattern.matcher(line);
                Matcher resultMatcher = resultPattern.matcher(line);

                if(matcher.find()){
                    LogEntry logEntry = new LogEntry();
                    try {
                        Date dateTime = dateFormat.parse(matcher.group(1));
                        logEntry.setDateTime(dateTime);
                        logEntry.setCommand(matcher.group(2));
                        logEntry.setAmount(Double.parseDouble(matcher.group(3)));
                        logEntries.add(logEntry);
                    } catch (ParseException e) {
                        System.out.println("Ошибка парсинга: " + e.getMessage());
                    } }
                else if (resultMatcher.find()) {
                        if (!logEntries.isEmpty()){
                            LogEntry lastEntry = logEntries.get(logEntries.size()-1);
                            lastEntry.setPaymentStatus("Успешно");
                            lastEntry.setCardEnding(resultMatcher.group(2));
                        }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        return logEntries;
    }
}
