package org.example.sberdocparser.model;

import java.util.Date;

public class LogEntry {
    private Date dateTime;
    private String command;
    private Double amount;
    private String paymentStatus;
    private String cardEnding;

    public LogEntry(){

    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCardEnding() {
        return cardEnding;
    }

    public void setCardEnding(String cardEnding) {
        this.cardEnding = cardEnding;
    }

    @Override
    public String toString() {
        return "Итог парсинга{" +
                "Время = " + dateTime +
                ", Команда = '" + command + '\'' +
                ", Сумма = " + amount +
                ", Статус оплаты = '" + paymentStatus + '\''+
                ", Карта транзакции = " +cardEnding + '\'' +
                "}";
    }
}
