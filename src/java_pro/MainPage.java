package java_pro;
import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {

    public MainPage() {
        Container ct = getContentPane();
        ct.setLayout(new BorderLayout());

        // 탑 패널
        TopPanel topPanel = new TopPanel();
        ct.add(topPanel, BorderLayout.NORTH);

        // 중앙 패널
        JPanel midPanel = new JPanel(new BorderLayout());
        ct.add(midPanel, BorderLayout.CENTER);
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("로그인", new LoginPanel());
        tabbedPane.addTab("정보", new Info());
        tabbedPane.addTab("메모", new Memo());

        midPanel.add(tabbedPane, BorderLayout.CENTER);
        ct.add(midPanel, BorderLayout.CENTER);

        // 하단 패널
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(0, 51, 102));
        JLabel rights = new JLabel("© 2024 Myongji household accounts. All Rights Reserved.");
        rights.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        rights.setForeground(Color.WHITE);
        bottomPanel.add(rights);

        ct.add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        MainPage mainPage = new MainPage();
        mainPage.setTitle("Myongji Household Accounts");
        mainPage.setSize(800, 600);
        mainPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPage.setLocationRelativeTo(null);
        mainPage.setVisible(true);
    }
}