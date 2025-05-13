package org.example.sberdocparser.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogParser {

    private static final Logger logger = LoggerFactory.getLogger(LogParser.class);
    private static final int MAX_COMMAND_AGE = 10;
    private static final boolean DEBUG = false;

    private final String logFilePath;
    private final List<CommandInfo> commandInfoList = new ArrayList<>();
    private final Pattern commandPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) SBKRNL: Command = \\d+, Amount = (\\d+\\.\\d+)");
    private final Pattern resultPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) SBKRNL: Result  = (\\d+), GUID=[^,]+, Amount = (\\d+\\.\\d+)(?:, Card = '' and '([\\*]+(\\d{4}))')?");

    public LogParser(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public List<String> parseLogFile() {
        List<String> output = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;

                Iterator<CommandInfo> cleanupIterator = commandInfoList.iterator();
                while (cleanupIterator.hasNext()) {
                    CommandInfo commandInfo = cleanupIterator.next();
                    if (lineNumber - commandInfo.lineNumber > MAX_COMMAND_AGE) {
                        if (DEBUG) {
                            output.add("  Удаляем старый Command от " + commandInfo.dateTime + " (возраст = " + (lineNumber - commandInfo.lineNumber) + ")");
                        }
                        cleanupIterator.remove();
                    }
                }

                Matcher resultMatcher = resultPattern.matcher(line);
                if (resultMatcher.find()) {
                    String dateTime = resultMatcher.group(1);
                    String resultValue = resultMatcher.group(2);
                    String amount = resultMatcher.group(3);
                    String cardEnding = null;
                    if (resultMatcher.groupCount() >= 5) {
                        cardEnding = resultMatcher.group(5);
                    }

                    Iterator<CommandInfo> iterator = commandInfoList.iterator();
                    while (iterator.hasNext()) {
                        CommandInfo commandInfo = iterator.next();
                        if (commandInfo.amount.equals(amount)) {
                            output.add("Date Time: " + commandInfo.dateTime);
                            output.add("Result = " + resultValue);
                            output.add("Amount: " + amount);
                            output.add("Card Ending: " + cardEnding);
                            output.add("---");
                            iterator.remove();
                            break;
                        } else {
                            if (DEBUG) {
                                output.add("  Суммы НЕ совпадают с Command от " + commandInfo.dateTime + " (amount = " + amount + ", commandAmount = " + commandInfo.amount + ")");
                            }
                        }
                    }
                } else {
                    Matcher commandMatcher = commandPattern.matcher(line);
                    if (commandMatcher.find()) {
                        String dateTime = commandMatcher.group(1);
                        String amount = commandMatcher.group(2);
                        if (DEBUG) {
                            output.add("Найдена строка Command: " + line);
                            output.add("  Запомнили: Date Time = " + dateTime + ", Amount = " + amount);
                        }

                        commandInfoList.add(new CommandInfo(dateTime, amount, lineNumber));
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading log file: " + e.getMessage(), e);
        }

        return output;
    }

    static class CommandInfo {
        String dateTime;
        String amount;
        int lineNumber;

        public CommandInfo(String dateTime, String amount, int lineNumber) {
            this.dateTime = dateTime;
            this.amount = amount;
            this.lineNumber = lineNumber;
        }
    }
}