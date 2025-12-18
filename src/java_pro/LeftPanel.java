package java_pro;

import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;

    public LeftPanel(JFrame momFrame, int userId, DatabaseConnection dbConnection) { //momFrame(상위 프레임 (대시보드 창)) 쓰는 이유는 입력하기 눌렀을때 기존 창을 닫기 위해서
                                                                                        // 이 패널이 속한 프레임 대시보드 전체 창 이라
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 세로로 정렬
        setPreferredSize(new Dimension(220, 0)); //페널 크기 고정
        setBackground(new Color(240, 240, 240));

        JLabel logoLabel = new JLabel("MYONGJI COIN", SwingConstants.CENTER);// 로고 텍스트 가운데로 갖고옴
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //
        JButton inputButton = madeButton("입력 하기", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                momFrame.dispose();
                new DashboardPage(userId).setVisible(true); // 입력하기 버튼 누르면 대쉬보드 페이지 열림 약간 새로고침 느낌 /현재 프레임 momframe 을 dispose 새로운 dashboradpage 보여줌
            }
        });


        JButton viewButton = madeButton("내역 보기", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                momFrame.dispose();
                new AnalyBudget(userId).setVisible(true); // 이거 누르면 김강 페이지로 이동
            }
        });

        JButton logoutButton = madeButton("로그아웃", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                momFrame.dispose();
                new LoginPage().setVisible(true);
            }
        }); // 로그아웃 되서 로그인 페이지로 이동


        JPanel analysisPanel = madeAnalysisPanel(); // 가계 분석 패널 생성하는거 이게 계산은 Bottom 패널에 하고 여기서 받아와서 사용 하는거
                                                    //이해 안되면 물어봐줘

        add(Box.createVerticalStrut(20));
        add(logoLabel);
        add(Box.createVerticalStrut(20));
        add(inputButton);
        add(Box.createVerticalStrut(10));
        add(viewButton);
        add(Box.createVerticalStrut(20));
        add(analysisPanel);
        add(Box.createVerticalStrut(20));
        add(logoutButton);
    }

    private JPanel madeAnalysisPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 수직 정렬
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // 색을 반전 주려고 테두리 그레이로

        // 가계분석 패널 각종 폰트 제목정렬등등 속성 사용 했음 이 코드는 건들지 말아주세요 배치 맞춰논거라
        JLabel title = new JLabel("이달의 가계분석");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        incomeLabel = new JLabel("수입: 0원"); // 초기값은 0원으로 통일
        incomeLabel.setForeground(Color.RED);
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 14));;

        expenseLabel = new JLabel("지출: 0원");
        expenseLabel.setForeground(Color.BLUE);
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 14));;

        balanceLabel = new JLabel("수입 - 지출: 0원");
        balanceLabel.setForeground(Color.BLACK);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));;

        // 컴포너트 추가 (컴포너트 추가 한거)
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(incomeLabel);
        panel.add(expenseLabel);
        panel.add(balanceLabel);

        return panel;
    }

    // 수입과 지출 정보를 업데이트하는 메서드 잔고 보여준다고 생각하면됨
    //수입, 지출, 잔고 데이터를 업데이트하고 UI에 반영 하는거
    //조회 전용 메서드
    public void updateMonth(int totalIncome, int totalExpense) {
        incomeLabel.setText("수입: " + totalIncome + "원"); // 돈들어오는거
        expenseLabel.setText("지출: " + totalExpense + "원"); // 돈쓴거
        balanceLabel.setText("수입 - 지출: " + (totalIncome - totalExpense) + "원"); // 잔고
    }
    // 버튼을 누르면 입력을 누르면 입력 대쉬보드 페이지 띄우는거 처리해주는 리스너 이벤트 처리 입력 등등 이런거 처리
    private JButton madeButton(String text, java.awt.event.ActionListener listener) { // String text 버튼에 표시할 텍스트 / 클릭 이벤트를 처리할 리스너
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(150, 40));
        button.setBackground(new Color(200, 200, 200));
        button.addActionListener(listener); // 클릭이벤트 처리!!!!! 리스너 추가: 버튼 클릭 시 실행할 동작을 설정 !!
        return button;
    }
}

//프로그램 실행 시 DashboardPage에서 LeftPanel이 생성되고 배치 됩니다
//사용자가 버튼을 클릭
// "입력하기": 현재 페이지 새로고침.
//"내역 보기": 가계 분석 페이지로 이동.
//"로그아웃": 로그인 페이지로 이동.
//updateMonth가 호출되면 수입, 지출, 잔고 데이터가 업데이트되고 라벨에 표시 madeAnalysisPanel 메서드에서 생성한 "이달의 가계분석 라벨에 반영되어 데이터 표시
