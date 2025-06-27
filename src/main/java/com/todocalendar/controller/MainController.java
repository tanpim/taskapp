package com.todocalendar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.HashMap;

public class MainController {

    @FXML
    private TextArea monthlyGoalTextArea;

    @FXML
    private GridPane weeklyScheduleGrid;
    @FXML
    private TextField dayTodoField;
    @FXML
    private ComboBox<String> daySelector;

    // 曜日別タスク表示用コンテナ
    @FXML
    private VBox mondayTodoContainer;
    @FXML
    private VBox tuesdayTodoContainer;
    @FXML
    private VBox wednesdayTodoContainer;
    @FXML
    private VBox thursdayTodoContainer;
    @FXML
    private VBox fridayTodoContainer;
    @FXML
    private VBox saturdayTodoContainer;
    @FXML
    private VBox sundayTodoContainer;

    @FXML
    private Label monthSummaryLabel;
    @FXML
    private Label weekSummaryLabel;

    @FXML
    private ComboBox<String> dayPrioritySelector;

    @FXML
    private Label weeklyCompletionRate;
    @FXML
    private Label highPriorityCount;
    @FXML
    private Label monthlyProgress;
    @FXML
    private Label totalTaskCount;

    // 本日タブ用の要素
    @FXML
    private Label todayLabel;
    @FXML
    private Label todayDateLabel;
    @FXML
    private VBox todayTodoContainer;
    @FXML
    private TextField newTodayTodoField;
    @FXML
    private ComboBox<String> todayPrioritySelector;
    @FXML
    private GridPane todayScheduleGrid;

    // サイドバー用の新しいフィールド
    @FXML
    private Button todayViewButton;
    @FXML
    private Button weeklyViewButton;
    @FXML
    private Button monthlyGoalViewButton;
    @FXML
    private Button statisticsViewButton;

    @FXML
    private VBox todayView;
    @FXML
    private VBox weeklyView;
    @FXML
    private VBox monthlyGoalView;
    @FXML
    private VBox statisticsView;

    @FXML
    private Label monthlyGoalLabel;

    private List<TodoItem> weeklyTodos = new ArrayList<>();
    private List<TodoItem> todayTodos = new ArrayList<>(); // 本日のタスク
    private Map<String, List<TodoItem>> dayTodos = new HashMap<>();
    private DayOfWeek selectedDay = DayOfWeek.MONDAY;
    private LocalDate currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    // 曜日とコンテナのマッピング
    private Map<DayOfWeek, VBox> dayContainers = new HashMap<>();

    @FXML
    public void initialize() {
        setupWeeklyScheduleGrid();
        setupDayContainers();
        // setupDaySelector(); // 一旦コメントアウト
        setupSummaryLabels();
        loadData();

        // 曜日選択の初期化
        daySelector.getItems().addAll("月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日", "日曜日");

        // 優先度選択の初期化
        dayPrioritySelector.getItems().addAll("高", "中", "低");
        dayPrioritySelector.setValue("中");
        todayPrioritySelector.getItems().addAll("高", "中", "低");
        todayPrioritySelector.setValue("中");

        // 本日の日付ラベル更新
        updateTodayLabels();

        // 初期化：各曜日のToDoリストを作成
        for (DayOfWeek day : DayOfWeek.values()) {
            dayTodos.put(day.toString(), new ArrayList<>());
        }

        // 初期表示を更新
        updateAllDayTodoDisplays();
        updateStatistics();

        // 月間目標ラベルの初期化
        setupMonthlyGoalLabel();

        // 初期ビューを本日に設定
        showTodayView();
    }

    @FXML
    private void saveData() {
        try {
            AppData data = new AppData();
            data.setMonthlyGoal(monthlyGoalTextArea.getText());
            data.setWeeklyTodos(weeklyTodos);
            // TODO: 曜日別ToDoの保存を実装
            DataStore.save(data);
            System.out.println("データを保存しました");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("保存に失敗しました");
        }
    }

    @FXML
    private void syncGoogleCalendar() {
        // TODO: Googleカレンダー同期機能を実装
        System.out.println("Googleカレンダーと同期しました");
    }

    @FXML
    private void sendToDiscord() {
        // TODO: Discord送信機能を実装
        System.out.println("Discordに送信しました");
    }

    @FXML
    private void previousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        setupWeeklyScheduleGrid();
        setupSummaryLabels();
    }

    @FXML
    private void currentWeek() {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        setupWeeklyScheduleGrid();
        setupSummaryLabels();
    }

    @FXML
    private void nextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        setupWeeklyScheduleGrid();
        setupSummaryLabels();
    }

    private void setupWeeklyScheduleGrid() {
        // 曜日ヘッダーを追加
        weeklyScheduleGrid.getChildren().clear();
        LocalDate today = LocalDate.now();

        // デバッグ用：現在の日付と曜日を確認
        System.out.println("今日の日付: " + today);
        System.out.println("今日の曜日: " + today.getDayOfWeek());
        System.out.println("表示中の週開始: " + currentWeekStart);

        // 表示中の週の月曜日を使用
        LocalDate monday = currentWeekStart;
        System.out.println("計算された月曜日: " + monday);

        String[] days = { "月", "火", "水", "木", "金", "土", "日" };
        for (int i = 0; i < days.length; i++) {
            LocalDate date = monday.plusDays(i);
            String dayLabelText = String.format("%s(%d/%d)", days[i], date.getMonthValue(), date.getDayOfMonth());
            System.out.println("曜日" + i + ": " + dayLabelText + " - " + date);
            Label dayLabel = new Label(dayLabelText);
            dayLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(100, 30);
            dayLabel.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc;");
            weeklyScheduleGrid.add(dayLabel, i + 1, 0);
        }

        // 時間軸を追加（7:00〜22:00）
        for (int hour = 7; hour <= 22; hour++) {
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setPrefSize(60, 40);
            timeLabel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc;");
            weeklyScheduleGrid.add(timeLabel, 0, hour - 6);

            // 各時間帯のセルを作成
            for (int day = 0; day < 7; day++) {
                Pane cell = new Pane();
                cell.setPrefSize(100, 40);
                cell.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
                weeklyScheduleGrid.add(cell, day + 1, hour - 6);
            }
        }
    }

    private void setupDayContainers() {
        // 曜日とコンテナのマッピングを設定
        dayContainers.put(DayOfWeek.MONDAY, mondayTodoContainer);
        dayContainers.put(DayOfWeek.TUESDAY, tuesdayTodoContainer);
        dayContainers.put(DayOfWeek.WEDNESDAY, wednesdayTodoContainer);
        dayContainers.put(DayOfWeek.THURSDAY, thursdayTodoContainer);
        dayContainers.put(DayOfWeek.FRIDAY, fridayTodoContainer);
        dayContainers.put(DayOfWeek.SATURDAY, saturdayTodoContainer);
        dayContainers.put(DayOfWeek.SUNDAY, sundayTodoContainer);
    }

    private void setupDaySelector() {
        // 曜日選択コンボボックスを設定
        daySelector.getItems().addAll("月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日", "日曜日");
        daySelector.setValue("月曜日");

        // 曜日が変更された時の処理
        daySelector.setOnAction(e -> {
            String selected = daySelector.getValue();
            selectedDay = getDayOfWeekFromString(selected);
            updateDayTodoDisplay();
        });

        // 初期化：各曜日のToDoリストを作成
        for (DayOfWeek day : DayOfWeek.values()) {
            dayTodos.put(day.toString(), new ArrayList<>());
        }

        updateDayTodoDisplay();
    }

    private DayOfWeek getDayOfWeekFromString(String dayString) {
        switch (dayString) {
            case "月曜日":
                return DayOfWeek.MONDAY;
            case "火曜日":
                return DayOfWeek.TUESDAY;
            case "水曜日":
                return DayOfWeek.WEDNESDAY;
            case "木曜日":
                return DayOfWeek.THURSDAY;
            case "金曜日":
                return DayOfWeek.FRIDAY;
            case "土曜日":
                return DayOfWeek.SATURDAY;
            case "日曜日":
                return DayOfWeek.SUNDAY;
            default:
                return DayOfWeek.MONDAY;
        }
    }

    private void updateDayTodoDisplay() {
        // 曜日別タスク一覧を更新（dayTodoContainerは削除）
        updateAllDayTodoDisplays();
    }

    private void updateAllDayTodoDisplays() {
        // 各曜日のタスク表示を更新
        LocalDate today = LocalDate.now();
        DayOfWeek todayOfWeek = today.getDayOfWeek();

        for (DayOfWeek day : DayOfWeek.values()) {
            VBox container = dayContainers.get(day);
            if (container != null) {
                container.getChildren().clear();

                // 今日の曜日かどうかでスタイルを変更
                String todayStyle = "";
                if (day == todayOfWeek) {
                    todayStyle = "-fx-border-color: #FF5722; -fx-border-width: 2; -fx-border-radius: 3; -fx-background-color: #FFF3E0;";
                }
                container.setStyle(todayStyle);

                List<TodoItem> todos = dayTodos.get(day.toString());
                if (todos != null) {
                    for (TodoItem todo : todos) {
                        addCompactTodoToContainer(todo, container, todos);
                    }
                }
            }
        }
    }

    private void addCompactTodoToContainer(TodoItem todo, VBox container, List<TodoItem> todoList) {
        HBox todoBox = new HBox(3);
        todoBox.setAlignment(Pos.CENTER_LEFT);
        todoBox.setPadding(new Insets(2, 3, 2, 3));
        todoBox.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0.5; -fx-border-radius: 2;");

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(todo.isCompleted());
        checkBox.setScaleX(0.8);
        checkBox.setScaleY(0.8);
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            todo.setCompleted(newVal);
            // 完了時は視覚的に薄く表示
            if (newVal) {
                todoBox.setStyle(
                        "-fx-background-color: #e9ecef; -fx-border-color: #adb5bd; -fx-border-width: 0.5; -fx-border-radius: 2; -fx-opacity: 0.7;");
            } else {
                todoBox.setStyle(
                        "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0.5; -fx-border-radius: 2; -fx-opacity: 1.0;");
            }
        });

        Label todoLabel = new Label(todo.getText());
        todoLabel.setWrapText(true);
        todoLabel.setMaxWidth(Double.MAX_VALUE);
        todoLabel.setStyle("-fx-font-size: 10px;");
        HBox.setHgrow(todoLabel, Priority.ALWAYS);

        Button deleteButton = new Button("×");
        deleteButton.setStyle(
                "-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 8px;");
        deleteButton.setPrefSize(15, 15);
        deleteButton.setOnAction(e -> {
            todoList.remove(todo);
            container.getChildren().remove(todoBox);
            updateDayTodoDisplay(); // 詳細表示も更新
        });

        todoBox.getChildren().addAll(checkBox, todoLabel, deleteButton);
        container.getChildren().add(todoBox);
    }

    @FXML
    private void addDayTodo() {
        String selectedDay = daySelector.getValue();
        String text = dayTodoField.getText().trim();

        if (selectedDay != null && !text.isEmpty()) {
            List<TodoItem> todos = dayTodos.computeIfAbsent(selectedDay, k -> new ArrayList<>());
            String priority = dayPrioritySelector.getValue() != null ? dayPrioritySelector.getValue() : "中";
            TodoItem todo = new TodoItem(text, false, priority);
            todos.add(todo);

            // 曜日別タスク表示を更新
            updateAllDayTodoDisplays();
            dayTodoField.clear();
            updateStatistics();
        }
    }

    private void addTodoToContainer(TodoItem todo, VBox container, List<TodoItem> todoList) {
        HBox todoBox = new HBox(5);
        todoBox.setAlignment(Pos.CENTER_LEFT);
        todoBox.setPadding(new Insets(2, 5, 2, 5));

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(todo.isCompleted());
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            todo.setCompleted(newVal);
        });

        Label todoLabel = new Label(todo.getText());
        todoLabel.setWrapText(true);
        todoLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(todoLabel, Priority.ALWAYS);

        Button deleteButton = new Button("×");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setPrefSize(20, 20);
        deleteButton.setOnAction(e -> {
            todoList.remove(todo);
            container.getChildren().remove(todoBox);
        });

        todoBox.getChildren().addAll(checkBox, todoLabel, deleteButton);
        container.getChildren().add(todoBox);
    }

    private void loadData() {
        try {
            AppData data = DataStore.load();
            if (data == null)
                return;
            // 月間目標
            monthlyGoalTextArea.setText(data.getMonthlyGoal());
            // 曜日別ToDoの読み込みは後で実装
            System.out.println("データを読み込みました");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("読込に失敗しました");
        }
    }

    private void setupSummaryLabels() {
        // 今月
        LocalDate now = LocalDate.now();
        monthSummaryLabel.setText(String.format("今月（%d月）", now.getMonthValue()));
        // 今週の範囲
        LocalDate weekStart = currentWeekStart;
        LocalDate weekEnd = weekStart.plusDays(6);
        weekSummaryLabel.setText(String.format("今週のタスク（%d/%d〜%d/%d）", weekStart.getMonthValue(),
                weekStart.getDayOfMonth(), weekEnd.getMonthValue(), weekEnd.getDayOfMonth()));
    }

    private void createTodoItemUI(TodoItem item, VBox container) {
        HBox todoBox = new HBox(8);
        todoBox.setAlignment(Pos.CENTER_LEFT);
        todoBox.setPadding(new Insets(4, 8, 4, 8));
        todoBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 4; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");

        // 優先度に基づいた色分け
        String priorityColor = getPriorityColor(item.getPriority());
        todoBox.setStyle(todoBox.getStyle() + "; -fx-border-color: " + priorityColor + "; -fx-border-width: 2;");

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(item.isCompleted());
        checkBox.setOnAction(e -> {
            item.setCompleted(checkBox.isSelected());
            updateTodoItemStyle(todoBox, item);
        });

        Label textLabel = new Label(item.getText());
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textLabel, Priority.ALWAYS);

        // 優先度表示
        Label priorityLabel = new Label(item.getPriority());
        priorityLabel
                .setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: "
                        + priorityColor + "; -fx-padding: 2 6; -fx-background-radius: 3;");

        Button deleteBtn = new Button("×");
        deleteBtn.setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 20; -fx-min-height: 20; -fx-background-radius: 10;");
        deleteBtn.setOnAction(e -> {
            container.getChildren().remove(todoBox);
            if (container == todayTodoContainer) {
                todayTodos.remove(item);
            } else {
                // 曜日別ToDoから削除
                for (List<TodoItem> todos : dayTodos.values()) {
                    todos.remove(item);
                }
            }
            updateStatistics();
        });

        todoBox.getChildren().addAll(checkBox, textLabel, priorityLabel, deleteBtn);
        container.getChildren().add(todoBox);

        updateTodoItemStyle(todoBox, item);
    }

    private String getPriorityColor(String priority) {
        switch (priority) {
            case "高":
                return "#f44336"; // 赤
            case "中":
                return "#ff9800"; // オレンジ
            case "低":
                return "#4caf50"; // 緑
            default:
                return "#ff9800";
        }
    }

    private void updateTodoItemStyle(HBox todoBox, TodoItem item) {
        if (item.isCompleted()) {
            todoBox.setStyle(todoBox.getStyle() + "; -fx-opacity: 0.6;");
        } else {
            todoBox.setStyle(todoBox.getStyle() + "; -fx-opacity: 1.0;");
        }
        updateStatistics();
    }

    private void updateStatistics() {
        // 今週の完了率を計算
        int totalWeeklyTasks = weeklyTodos.size();
        int completedWeeklyTasks = (int) weeklyTodos.stream().filter(TodoItem::isCompleted).count();
        double completionRate = totalWeeklyTasks > 0 ? (double) completedWeeklyTasks / totalWeeklyTasks * 100 : 0;
        weeklyCompletionRate.setText(String.format("%.1f%%", completionRate));

        // 高優先度タスク数を計算
        long highPriorityTasks = weeklyTodos.stream()
                .filter(todo -> "高".equals(todo.getPriority()) && !todo.isCompleted())
                .count();
        highPriorityCount.setText(String.valueOf(highPriorityTasks));

        // 総タスク数を計算
        int totalTasks = weeklyTodos.size() + todayTodos.size() + dayTodos.values().stream()
                .mapToInt(List::size)
                .sum();
        totalTaskCount.setText(String.valueOf(totalTasks));

        // 月間目標進捗（仮の計算）
        int monthlyGoalProgress = calculateMonthlyGoalProgress();
        monthlyProgress.setText(monthlyGoalProgress + "%");
    }

    private int calculateMonthlyGoalProgress() {
        // 月間目標の進捗を計算（簡易版）
        // 実際の実装では、月間目標の詳細な進捗管理が必要
        int totalTasks = weeklyTodos.size() + dayTodos.values().stream()
                .mapToInt(List::size)
                .sum();
        int completedTasks = (int) weeklyTodos.stream().filter(TodoItem::isCompleted).count();

        // 各曜日の完了タスクも加算
        for (List<TodoItem> todos : dayTodos.values()) {
            completedTasks += todos.stream().filter(TodoItem::isCompleted).count();
        }

        return totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
    }

    // 本日タブ用のメソッド
    private void updateTodayLabels() {
        LocalDate today = LocalDate.now();
        todayDateLabel.setText("本日のスケジュール（" + today.getMonthValue() + "/" + today.getDayOfMonth() + "）");
        setupTodayScheduleGrid();
    }

    private void setupTodayScheduleGrid() {
        todayScheduleGrid.getChildren().clear();
        todayScheduleGrid.getColumnConstraints().clear();
        todayScheduleGrid.getRowConstraints().clear();

        // 時間軸（6:00～23:00）の設定
        for (int hour = 6; hour <= 23; hour++) {
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setStyle(
                    "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 5; -fx-background-color: #f0f0f0; -fx-min-width: 60;");
            todayScheduleGrid.add(timeLabel, 0, hour - 6);

            // スケジュール表示エリア
            Label scheduleArea = new Label("");
            scheduleArea.setStyle(
                    "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-min-height: 40; -fx-background-color: white;");
            scheduleArea.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(scheduleArea, Priority.ALWAYS);
            todayScheduleGrid.add(scheduleArea, 1, hour - 6);
        }
    }

    @FXML
    private void addTodayTodo() {
        String text = newTodayTodoField.getText().trim();
        if (!text.isEmpty()) {
            String priority = todayPrioritySelector.getValue() != null ? todayPrioritySelector.getValue() : "中";
            TodoItem todo = new TodoItem(text, false, priority);
            todayTodos.add(todo);
            createTodoItemUI(todo, todayTodoContainer);
            newTodayTodoField.clear();
            updateStatistics();
        }
    }

    @FXML
    private void addTodaySchedule() {
        // 簡易的なスケジュール追加ダイアログ
        // 実際のプロジェクトでは、より詳細なダイアログを実装する
        System.out.println("本日のスケジュール追加機能（未実装）");
    }

    // サイドバーのビュー切り替えメソッド
    @FXML
    private void showTodayView() {
        hideAllViews();
        todayView.setVisible(true);
        updateButtonStyles(todayViewButton);
        setupTodayScheduleGrid();
    }

    @FXML
    private void showWeeklyView() {
        hideAllViews();
        weeklyView.setVisible(true);
        updateButtonStyles(weeklyViewButton);
    }

    @FXML
    private void showMonthlyGoalView() {
        hideAllViews();
        monthlyGoalView.setVisible(true);
        updateButtonStyles(monthlyGoalViewButton);
    }

    @FXML
    private void showStatisticsView() {
        hideAllViews();
        statisticsView.setVisible(true);
        updateButtonStyles(statisticsViewButton);
        updateStatistics(); // 統計を最新に更新
    }

    private void hideAllViews() {
        todayView.setVisible(false);
        weeklyView.setVisible(false);
        monthlyGoalView.setVisible(false);
        statisticsView.setVisible(false);
    }

    private void updateButtonStyles(Button activeButton) {
        // 全ボタンを非アクティブスタイルに
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-background-radius: 6; -fx-padding: 12 16;";
        todayViewButton.setStyle(inactiveStyle);
        weeklyViewButton.setStyle(inactiveStyle);
        monthlyGoalViewButton.setStyle(inactiveStyle);
        statisticsViewButton.setStyle(inactiveStyle);

        // アクティブボタンのスタイル設定
        String activeStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 6; -fx-padding: 12 16;";
        activeButton.setStyle(activeStyle);
    }

    private void setupMonthlyGoalLabel() {
        LocalDate now = LocalDate.now();
        String monthName = now.format(DateTimeFormatter.ofPattern("M月", Locale.JAPANESE));
        monthlyGoalLabel.setText(monthName + "の目標");
    }

    // 内部クラス：ToDoアイテム
    public static class TodoItem {
        private String text;
        private boolean completed;
        private String priority; // 優先度: "高", "中", "低"

        public TodoItem() {
            this.text = "";
            this.completed = false;
            this.priority = "中";
        }

        public TodoItem(String text) {
            this.text = text;
            this.completed = false;
            this.priority = "中";
        }

        public TodoItem(String text, boolean completed, String priority) {
            this.text = text;
            this.completed = completed;
            this.priority = priority;
        }

        // Getters and Setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }
}