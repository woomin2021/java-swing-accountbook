package java_pro;

import javax.swing.*;
import java.sql.*;

public class DailyExpense {

    private Connection connection;

    public DailyExpense() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_go", "java_go", "1234");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void allExpense() {

        double monthlyAverage = calMonthAverageExpense();
        if (monthlyAverage == 0) {
            JOptionPane.showMessageDialog(null, "지출 데이터가 없습니다.");

        }


        if (monthlyAverage != 0) {
            double dailyExpectedExpense = monthlyAverage / 30;
            JOptionPane.showMessageDialog(null, "다음 달 하루 예상 지출액은: " + String.format("%.2f", dailyExpectedExpense) + "원입니다.");

            String todayExpenseInput = JOptionPane.showInputDialog("오늘의 지출액을 입력하세요:");
            if (todayExpenseInput != null && !todayExpenseInput.isEmpty()) {
                try {
                    double todayExpense = Double.parseDouble(todayExpenseInput);
                    if (todayExpense > 0) {
                        if (todayExpense > dailyExpectedExpense) {
                            JOptionPane.showMessageDialog(null, "경고: 하루 예상 지출액 초과.");
                        } else {
                            JOptionPane.showMessageDialog(null, "잘했습니다! 예상 지출액 이내입니다.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "올바른 금액을 입력하세요.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "숫자 형식의 금액을 입력하세요.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "지출액을 입력해야 합니다.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "지출 데이터가 없습니다.");
        }

    }

    private double calMonthAverageExpense() {
        double monthlyAverage = 0;
        String query = "SELECT AVG(monthly_expense) AS avg_monthly_expense " +
                "FROM (SELECT SUM(amount) AS monthly_expense " +
                "FROM transactions WHERE type = '지출' GROUP BY DATE_FORMAT(date, '%Y-%m')) AS monthly_data";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                monthlyAverage = rs.getDouble("avg_monthly_expense");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "데이터베이스 오류: " + e.getMessage());
        }

        return monthlyAverage;
    }

    public static void main(String[] args) {
        DailyExpense start = new DailyExpense();
        start.allExpense();
    }
    }

