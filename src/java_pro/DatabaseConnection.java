package java_pro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection connection;
    //객체로 데이터베이스와 연결 관리 해줌
    //연결을 초기화 해주고 데베 작업 수행 가능



    public DatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_go", "java_go", "1234");
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            connection = null;
        }
    }
    // MySql 데베 연결

    // 데베 연결 상태 확인
    private boolean isConnected() {
        return connection != null;
    }

    // 특정 사용자의 월별 총 수입 계산
    //결과는 Resultset으로 sum를 통해 총 수입 계산

    public int getTotalIncome(int userId, String month) {
        if (!isConnected()) {
            System.err.println("Database connection is not established.");
            return 0;
        }

        String query = "SELECT SUM(amount) AS total_income FROM transactions WHERE user_id = ? AND type = '수입' AND DATE_FORMAT(date, '%Y-%m') = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_income");
            }
        } catch (SQLException e) {
            System.err.println("Failed to calculate total income: " + e.getMessage() + " | Query: " + query);
        }
        System.err.println("Query returned no results for total income.");
        return 0;
    }

    // getTotalIncome 이랑 동일한데 지출
    public int getTotalExpense(int userId, String month) {
        if (!isConnected()) {
            System.err.println("Database connection is not established.");
            return 0;
        }

        String query = "SELECT SUM(amount) AS total_expense FROM transactions WHERE user_id = ? AND type = '지출' AND DATE_FORMAT(date, '%Y-%m') = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_expense");
            }
        } catch (SQLException e) {
            System.err.println("Failed to calculate total expense: " + e.getMessage() + " | Query: " + query);
        }
        System.err.println("Query returned no results for total expense.");
        return 0;
    }

    //특정 사용자의 월별 수입 또는 지출 데이터를 조회 가능
    //결과를 List<Object[]>로 반환되는거 Bottom 있는곳으로
    //각 행은 Object[]로 표현되며, 날짜, 세부사항, 카테고리, 금액 데이터 다 있다고 보면 됨
    public List<Object[]> getTransactions(int userId, String month, String type) {
        if (!isConnected()) {
            System.err.println("Database connection is not established.");
            return new ArrayList<>();
        }

        String query = "SELECT date, details, category, amount FROM transactions WHERE user_id = ? AND DATE_FORMAT(date, '%Y-%m') = ? AND type = ?";
        List<Object[]> transactions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, month);
            stmt.setString(3, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Object[]{
                        rs.getString("date"),
                        rs.getString("details"),
                        rs.getString("category"),
                        rs.getInt("amount")
                });
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve transactions: " + e.getMessage() + " | Query: " + query);
        }
        return transactions;
    }

    // 데이터베이스에 새로운 거래를 삽입합니다.
    //입력 데이터를 유효성 검증 후 쿼리를 실행합니다.
    //트랜잭션 처리를 통해 작업 도중 실패 시 롤백(rollback) 처리합니다.
    public boolean addTransaction(int userId, String date, String details, String category, int amount, String type) {
        if (!isConnected()) {
            System.err.println("Database connection is not established.");
            return false;
        }

        if (userId <= 0 || date == null || date.isEmpty() || details == null || details.isEmpty()
                || category == null || category.isEmpty() || amount <= 0 || type == null || type.isEmpty()) {
            System.err.println("Invalid input parameters for addTransaction.");
            return false;
        }

        String query = "INSERT INTO transactions (user_id, date, details, category, amount, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            stmt.setInt(1, userId);
            stmt.setString(2, date);
            stmt.setString(3, details);
            stmt.setString(4, category);
            stmt.setInt(5, amount);
            stmt.setString(6, type);
            stmt.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Failed to add transaction: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
        return false;
    }

    // 거래 삭제 해주는거 거래위주라 보면 됨 그중에 지출인거 수입도 지출도 거래에 해당되서
    public boolean deleteTransaction(int userId, String date, String details, String category, int amount, String type) {
        if (!isConnected()) {
            System.err.println("Database connection is not established.");
            return false;
        }

        String query = "DELETE FROM transactions WHERE user_id = ? AND date = ? AND details = ? AND category = ? AND amount = ? AND type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, date);
            stmt.setString(3, details);
            stmt.setString(4, category);
            stmt.setInt(5, amount);
            stmt.setString(6, type);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete transaction: " + e.getMessage() + " | Query: " + query);
        }
        return false;
    }


    //<김강>
    //월,년별 분석페이지 검색 메소드
    public List<Object[]> searchTrans(int userId, String date, String description, String category, String amount, String type) {
        List<Object[]> transactions = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT date, details, category, amount, type FROM transactions WHERE user_id = ?");

        // 필터링 조건에 따라 쿼리 추가
        if (date != null && !date.isEmpty()) {
            queryBuilder.append(" AND date LIKE ?");
        }
        if (description != null && !description.isEmpty()) {
            queryBuilder.append(" AND details LIKE ?");
        }
        if (category != null && !category.isEmpty()) {
            queryBuilder.append(" AND category LIKE ?");
        }
        if (amount != null && !amount.isEmpty()) {
            queryBuilder.append(" AND amount = ?");
        }
        if (type != null && !type.isEmpty()) {
            queryBuilder.append(" AND type = ?");
        }

        String query = queryBuilder.toString();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);

            // 조건에 맞는 파라미터 설정
            if (date != null && !date.isEmpty()) {
                stmt.setString(paramIndex++, "%" + date + "%");
            }
            if (description != null && !description.isEmpty()) {
                stmt.setString(paramIndex++, "%" + description + "%");
            }
            if (category != null && !category.isEmpty()) {
                stmt.setString(paramIndex++, "%" + category + "%");
            }
            if (amount != null && !amount.isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(amount));
            }
            if (type != null && !type.isEmpty()) {
                stmt.setString(paramIndex++, type);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getString("date"),       // 날짜
                        rs.getString("details"),    // 상세 내용
                        rs.getString("category"),   // 카테고리
                        rs.getInt("amount"),        // 금액
                        rs.getString("type")        // 수입/지출
                };
                transactions.add(row);
            }
        } catch (SQLException e) {
            System.err.println("거래 필터링 조회 실패: " + e.getMessage());
        }

        return transactions;
    }


    //데이터베이스 연결을 종료합니다.
    public void close() {
        if (connection == null) {
            System.err.println("Connection is already closed or was never opened.");
            return;
        }
        try {
            connection.close();
            System.out.println("Database connection closed.");
            connection = null; //
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }
}
