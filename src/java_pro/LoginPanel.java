package java_pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class LoginPanel extends JPanel {

    public LoginPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loginLabel = new JLabel("로그인하려면 버튼을 클릭하세요:");
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loginLabel);

        JButton loginButton = new JButton("로그인하기!");



        Color Color1 = loginButton.getBackground();
        Color Color2 = new Color(173, 216, 230); // 진한 파란색

        // 마우스 이벤트 처리 클래스 연결
        loginButton.addMouseListener(new ButtonEffect1(loginButton, Color1, Color2));


        LoginButtonAction1 actionListener = new LoginButtonAction1(this);
        loginButton.addActionListener(actionListener);

        add(loginButton);
    }
}

//  버튼 클릭 이벤트 처리
class LoginButtonAction1 implements ActionListener {
    private JPanel panel;

    public LoginButtonAction1(JPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 현재 창 닫기
        JFrame topLevelFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
        if (topLevelFrame != null) {
            topLevelFrame.dispose();
        }

        // 새 창 열기
        new LoginPage().setVisible(true);
    }
}

//  버튼 색상 효과 처리
class ButtonEffect1 implements MouseListener {
    private JButton button;
    private Color Color1;
    private Color Color2;


    public ButtonEffect1(JButton button, Color Color1, Color Color2) {
        this.button = button;
        this.Color1 = Color1;
        this.Color2 = Color2;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        button.setBackground(Color2);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        button.setBackground(Color1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        button.setBackground(Color2);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        button.setBackground(Color2);}

    @Override
    public void mouseReleased(MouseEvent e) {
        button.setBackground(Color1);

    }
}