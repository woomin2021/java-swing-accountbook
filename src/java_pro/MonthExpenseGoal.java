package java_pro;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MonthExpenseGoal {

    private Connection connection;

    public MonthExpenseGoal() {
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

    public void checkExpenseGoal() {
        String goal = JOptionPane.showInputDialog("1달 목표 지출액을 입력하세요 (원):");
        if (goal != null) {
            int goalAmount;
            try {
                goalAmount = Integer.parseInt(goal);
                if (goalAmount > 0) {

                    LocalDate now = LocalDate.now();
                    String nowMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));


                    int totalExpense = fetchMonthlyExpenses(nowMonth);


                    if (totalExpense >= goalAmount) {
                        JOptionPane.showMessageDialog(null, "목표 지출액 초과: " + totalExpense + "원\n" +
                                "실패: 목표 지출액(" + goalAmount + "원)을 초과했습니다.");
                    } else if (totalExpense >= goalAmount * 0.9) {
                        JOptionPane.showMessageDialog(null, "경고: 목표 지출액의 90%에 근접했습니다.\n" +
                                "현재 지출: " + totalExpense + "원\n" +
                                "목표 지출액: " + goalAmount + "원");
                    } else {
                        JOptionPane.showMessageDialog(null, "현재까지 지출: " + totalExpense + "원\n" +
                                "목표 지출액: " + goalAmount + "원\n" +
                                "잘 진행 중입니다!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "올바른 금액을 입력하세요.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "숫자 형식의 금액을 입력하세요.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "목표 지출액을 입력해야 합니다.");
        }
    }

    private int fetchMonthlyExpenses(String month) {
        int totalExpense = 0;
        String query = "SELECT SUM(amount) AS total_expense FROM transactions " +
                "WHERE type = '지출' AND DATE_FORMAT(date, '%Y-%m') = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalExpense = rs.getInt("total_expense"); // 정수로 가져옴
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "데이터베이스 오류: " + e.getMessage());
        }

        return totalExpense;
    }

    public static void main(String[] args) {
        MonthExpenseGoal app = new MonthExpenseGoal();
        app.checkExpenseGoal();
    }
}
