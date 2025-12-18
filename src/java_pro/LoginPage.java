package java_pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField idField; // 아이디 입력 필드
    private JPasswordField passwordField; // 비밀번호 입력 필드
    private JButton loginButton, signupButton; // 버튼들
    private Connection connection; // 데이터베이스 연결

    public LoginPage() {
        setTitle("Myongji Coin - Login");
        setSize(320, 300); // 창 크기
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 현재 창 닫기
        setLocationRelativeTo(null); // 화면 중앙에 위치
        Container ct = getContentPane();
        ct.setLayout(null); // 절대 좌표 배치

        // 컴포넌트 설정
        JLabel titleLabel = new JLabel("Myongji Coin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(90, 20, 150, 30);
        ct.add(titleLabel);

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(50, 70, 70, 30);
        ct.add(idLabel);

        idField = new JTextField(8);
        idField.setBounds(130, 70, 120, 30);
        ct.add(idField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(50, 110, 70, 30);
        ct.add(passwordLabel);

        passwordField = new JPasswordField(8);
        passwordField.setBounds(130, 110, 120, 30);
        ct.add(passwordField);

        loginButton = new JButton("로그인");
        loginButton.setBounds(40, 170, 80, 30);
        loginButton.addActionListener(new LoginActionListener());
        ct.add(loginButton);

        signupButton = new JButton("회원가입");
        signupButton.setBounds(130, 170, 100, 30);
        signupButton.addActionListener(new SignupActionListener());
        ct.add(signupButton);

        connectToDatabase(); // 데이터베이스 연결 ㄱ
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드하는거
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_go", "java_go", "1234");
            System.out.println("Database connected!"); // 연결 확인 해야 합니다
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            String password = new String(passwordField.getPassword());

            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?");
                ps.setString(1, id);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) { // 로그인 성공 시
                    int userId = rs.getInt("id"); // 로그인한 사용자 ID 가져오기
                    JOptionPane.showMessageDialog(null, "@@ " + id + "님 환영합니다! @@");
                    dispose(); // 현재 LoginPage 창 닫기
                    new DashboardPage(userId).setVisible(true); // DashboardPage에 사용자 ID 전달
                }
                else {
                    JOptionPane.showMessageDialog(null, "로그인 실패. 아이디와 비밀번호를 확인하세요.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "로그인 실패: " + ex.getMessage());
            }
        }
    }

    private class SignupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = JOptionPane.showInputDialog("아이디 (4자리):");
            String password = JOptionPane.showInputDialog("비밀번호 (4자리):");

            if (id != null && password != null && id.length() == 4 && password.length() == 4) {
                try {
                    PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
                    checkStmt.setString(1, id);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count == 0) { //
                        PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO users (name, password) VALUES (?, ?)");
                        insertStmt.setString(1, id);
                        insertStmt.setString(2, password);
                        insertStmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다.");
                    } else {
                        JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "회원가입 실패: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "아이디와 비밀번호는 4자리로 입력해주세요.");
            }
        }
    }

    @Override
    public void dispose() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPage().setVisible(true);
            }
        });
    }
}
