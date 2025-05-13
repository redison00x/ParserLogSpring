package org.example.sberdocparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserTest {

    private static final int MAX_COMMAND_AGE = 10; // Максимальное количество строк между Command и Result
    private static final boolean DEBUG = false; // Включить/выключить отладочный вывод

    public static void main(String[] args) {

        String logFilePath = "C:\\Users\\CWSoft\\Desktop/log.log";

        // Регулярное выражение для строк с "Command"
        Pattern commandPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) SBKRNL: Command = \\d+, Amount = (\\d+\\.\\d+)");

        // Регулярное выражение для строк с "Result" (добавили группу для Result)
        Pattern resultPattern = Pattern.compile("(\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) SBKRNL: Result  = (\\d+), GUID=[^,]+, Amount = (\\d+\\.\\d+)(?:, Card = '' and '([\\*]+(\\d{4}))')?");

        // Список для хранения последних значений Command
        List<CommandInfo> commandInfoList = new ArrayList<>();

        int lineNumber = 0; // Номер текущей строки

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Удаляем старые Command (перед обработкой текущей строки)
                Iterator<CommandInfo> cleanupIterator = commandInfoList.iterator();
                while (cleanupIterator.hasNext()) {
                    CommandInfo commandInfo = cleanupIterator.next();
                    if (lineNumber - commandInfo.lineNumber > MAX_COMMAND_AGE) {
                        if (DEBUG) {
                            System.out.println("  Удаляем старый Command от " + commandInfo.dateTime + " (возраст = " + (lineNumber - commandInfo.lineNumber) + ")");
                        }
                        cleanupIterator.remove();
                    }
                }

                // Попробуем сопоставить с resultPattern (Сначала обрабатываем Result)
                Matcher resultMatcher = resultPattern.matcher(line);
                if (resultMatcher.find()) {
                    String dateTime = resultMatcher.group(1);
                    String resultValue = resultMatcher.group(2); // Получаем значение Result
                    String amount = resultMatcher.group(3);
                    String cardEnding = null;
                    // Проверяем, есть ли группа 4 (номер карты)
                    if (resultMatcher.groupCount() >= 4) {
                        cardEnding = resultMatcher.group(4);
                    }

                    // Ищем соответствующую строку Command в списке
                    Iterator<CommandInfo> iterator = commandInfoList.iterator();
                    while (iterator.hasNext()) {
                        CommandInfo commandInfo = iterator.next();
                        if (commandInfo.amount.equals(amount)) {
                            // Выводим информацию в нужном формате
                            System.out.println("Date Time: " + commandInfo.dateTime);
                            System.out.println("Result = " + resultValue); // Выводим "Result = " + значение
                            System.out.println("Amount: " + amount);
                            System.out.println("Card Ending: " + cardEnding);
                            System.out.println();

                            // Удаляем информацию из списка, чтобы не было повторной обработки
                            iterator.remove();
                            break;
                        } else {
                            if (DEBUG) {
                                System.out.println("  Суммы НЕ совпадают с Command от " + commandInfo.dateTime + " (amount = " + amount + ", commandAmount = " + commandInfo.amount + ")");
                            }
                        }
                    }
                } else { //Если это не Result, то проверяем Command
                    // Попробуем сопоставить с commandPattern
                    Matcher commandMatcher = commandPattern.matcher(line);
                    if (commandMatcher.find()) {
                        String dateTime = commandMatcher.group(1);
                        String amount = commandMatcher.group(2);
                        if (DEBUG) {
                            System.out.println("Найдена строка Command: " + line);
                            System.out.println("  Запомнили: Date Time = " + dateTime + ", Amount = " + amount);
                        }

                        // Добавляем информацию в список
                        commandInfoList.add(new CommandInfo(dateTime, amount, lineNumber));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
    }

    // Класс для хранения информации о Command
    static class CommandInfo {
        String dateTime;
        String amount;
        int lineNumber; // Номер строки, в которой был найден Command

        public CommandInfo(String dateTime, String amount, int lineNumber) {
            this.dateTime = dateTime;
            this.amount = amount;
            this.lineNumber = lineNumber;
        }
    }
}