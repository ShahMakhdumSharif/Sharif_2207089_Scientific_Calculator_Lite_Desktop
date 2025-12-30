package com.example.calculator.model;

public class CalculationHistory {
    private final int id;
    private final int userId;
    private final String expression;
    private final String result;
    private final String timestamp;

    public CalculationHistory(int id, int userId, String expression, String result, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.expression = expression;
        this.result = result;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
