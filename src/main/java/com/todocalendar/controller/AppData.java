package com.todocalendar.controller;

import java.util.List;

public class AppData {
    private String monthlyGoal;
    private List<MainController.TodoItem> weeklyTodos;
    private List<MainController.TodoItem> todayTodos;

    public AppData() {
    }

    public AppData(String monthlyGoal, List<MainController.TodoItem> weeklyTodos,
            List<MainController.TodoItem> todayTodos) {
        this.monthlyGoal = monthlyGoal;
        this.weeklyTodos = weeklyTodos;
        this.todayTodos = todayTodos;
    }

    public String getMonthlyGoal() {
        return monthlyGoal;
    }

    public void setMonthlyGoal(String monthlyGoal) {
        this.monthlyGoal = monthlyGoal;
    }

    public List<MainController.TodoItem> getWeeklyTodos() {
        return weeklyTodos;
    }

    public void setWeeklyTodos(List<MainController.TodoItem> weeklyTodos) {
        this.weeklyTodos = weeklyTodos;
    }

    public List<MainController.TodoItem> getTodayTodos() {
        return todayTodos;
    }

    public void setTodayTodos(List<MainController.TodoItem> todayTodos) {
        this.todayTodos = todayTodos;
    }
}