package org.example.sberdocparser.service;

import org.example.sberdocparser.model.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(LogParserService.class);

    public List<LogEntry> parseLogFile(String fileName) {
        List<LogEntry> logEntries = new ArrayList<LogEntry>();
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Pattern logPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{3}) SBKRNL: Command = (\\d+), Amount = (\\d+\\.\\d+)");

            Pattern resultPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) SBKRNL: Result  = (\\d+), GUID=[^,]+, Amount = (\\d+\\.\\d+)(?:, Card = '' and '([\\*]+(\\d{4}))')?");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss.SSS");

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
                        logger.debug("Найдено Command: " + logEntry);

                    } catch (ParseException e) {
                        logger.error("Ошибка парсинга даты: "+e.getMessage(),e);
                    }
                } else if (resultMatcher.find()) {
                        if (!logEntries.isEmpty()){
                            LogEntry lastEntry = logEntries.get(logEntries.size()-1);
                            lastEntry.setPaymentStatus("Успешно");
                            lastEntry.setCardEnding(resultMatcher.group(2));
                            logger.debug("Найдено Result: " + lastEntry);
                        }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        return logEntries;
    }
}
