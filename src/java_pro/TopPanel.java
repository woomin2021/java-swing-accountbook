package java_pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TopPanel extends JPanel {
    public TopPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 144, 255)); // 다저블루 색상

        JLabel title = new JLabel("Welcome to Household Accounts");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.WHITE);

        JLabel time = new JLabel();
        time.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        time.setHorizontalAlignment(SwingConstants.CENTER);
        time.setForeground(Color.WHITE);

        Timer time2 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                time.setText("현재 시간: " + now.format(formatter));
            }
        });
        time2.start();

        add(title, BorderLayout.CENTER);
        add(time, BorderLayout.SOUTH);
    }
}
