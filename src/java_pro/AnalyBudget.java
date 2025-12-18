package java_pro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AnalyBudget extends JFrame {
    private int userId;
    private JComboBox period;
    private JComboBox yearCB1, yearCB2;
    private JComboBox monthCB1, monthCB2;
    private JTable analyT;
    private DefaultTableModel tableModel;
    private JLabel IncomeL, ExpenseL, BalanceL;

    public AnalyBudget(int userId) {
        this.userId = userId;
        setupFrame();
    }

    private void setupFrame() {
        setTitle("Myongji Coin - Dashboard");
        setSize(1800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(LeftP(), BorderLayout.WEST);
        mainPanel.add(TopP(), BorderLayout.NORTH);
        mainPanel.add(RightP(), BorderLayout.CENTER);
        mainPanel.add(SummaryP(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel SummaryP() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));

        IncomeL = new JLabel("총수입: 0원", SwingConstants.CENTER);
        ExpenseL = new JLabel("총지출: 0원", SwingConstants.CENTER);
        BalanceL = new JLabel("잔액: 0원", SwingConstants.CENTER);

        Font font = new Font("맑은고딕", Font.BOLD, 24); //보기좋게 폰트 크기 키움
        IncomeL.setFont(font);
        ExpenseL.setFont(font);
        BalanceL.setFont(font);

        summaryPanel.add(IncomeL);
        summaryPanel.add(ExpenseL);
        summaryPanel.add(BalanceL);

        return summaryPanel;
    }

    private JPanel LeftP() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("MYONGJI COIN", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // 로고 중앙 정렬

        JButton mainPg = new JButton("메인페이지 가기");
        mainPg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toMainP();
            }
        });

        JButton searchBu = new JButton("검색하기");
        searchBu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchCateg();
            }
        });


        JButton dailyExpenseBtn = new JButton("오늘 예상 지출");
        dailyExpenseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DailyExpense tracker = new DailyExpense();
                tracker.allExpense();
            }
        });


        JButton goalExpenseBtn = new JButton("목표지출");
        goalExpenseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MonthExpenseGoal expenseGoal = new MonthExpenseGoal();
                expenseGoal.checkExpenseGoal();
            }
        });


        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(20)); // 수직 간격추가
        leftPanel.add(mainPg);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(searchBu);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(dailyExpenseBtn);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(goalExpenseBtn);


        return leftPanel;
    }

    private void toMainP() {
        new DashboardPage(userId).setVisible(true);
        setVisible(false); // 메인 페이지로 이동
    }

    private JPanel TopP() {//삳단패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // 중앙 정렬 레이아웃
        // 콤보박스 (월별 / 년별)
        String[] combo1 = {"월별", "년별"};
        period = new JComboBox(combo1);
        period.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleP(); //toggle로가서 필드 변경
            }
        });


        yearCB1 = new JComboBox();
        yearCB2 = new JComboBox();
        for (int year = 2024; year <= 2033; year++) {
            yearCB1.addItem(year);
            yearCB2.addItem(year);
        }

        monthCB1 = new JComboBox();
        monthCB2 = new JComboBox();
        for (int month = 1; month <= 12; month++) {
            monthCB1.addItem(String.valueOf(month));
            monthCB2.addItem(String.valueOf(month));
        }

        // 적용 버튼 설정
        JButton applyBu = new JButton("적용");
        applyBu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confrimB();  //적용 누르면 해당 기간 사이를 검색
            }
        });

        // 패널에 각 컴포넌트 추가
        topPanel.add(period);
        topPanel.add(yearCB1);
        topPanel.add(monthCB1);
        topPanel.add(new JLabel(" ~ "));
        topPanel.add(yearCB2);
        topPanel.add(monthCB2);
        topPanel.add(applyBu);

        return topPanel;
    }


    private JPanel RightP() {
        JPanel rightPanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"날짜", "상세 내용", "카테고리", "금액", "수입/지출"}, 0);
        analyT = new JTable(tableModel);

        //데이터 많을경우 해서 스크롤
        rightPanel.add(new JScrollPane(analyT), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton deleteBu = new JButton("삭제");
        JButton applyBu = new JButton("적용"); // 요약 패널 갱신용 버튼


        deleteBu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteRow();
            }
        });

        applyBu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateBu();
            }
        });

        buttonPanel.add(deleteBu);
        buttonPanel.add(applyBu);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void DeleteRow() {
        int selectedRow = analyT.getSelectedRow();

        // 선택된 행이 없을 경우 사용자에게 경고 메시지를 표시후 return으로 메서드 종료
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 데이터를 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.removeRow(selectedRow);
        // 데이터 삭제 후 사용자에게 성공 메시지를 표시
        JOptionPane.showMessageDialog(this, "선택된 데이터가 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
    }


    private void UpdateBu() {
        int totalIncome = 0;
        int totalExpense = 0;
        //금액들 초기화 해주고


        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String type = (String) tableModel.getValueAt(i, 4); //테이블 모델 3번째 수입/지출 부분에서 따오는거
            int amount = Integer.parseInt(tableModel.getValueAt(i, 3).toString()); //테이블 모델 4번째 금액부분에서 금액 추가

            if ("수입".equals(type)) {
                totalIncome += amount;
            } else if ("지출".equals(type)) {
                totalExpense += amount;
            }
        }

        // 금액 최종 업데이트
        IncomeL.setText("총수입: " + totalIncome + "원");
        ExpenseL.setText("총지출: " + totalExpense + "원");
        BalanceL.setText("잔액: " + (totalIncome - totalExpense) + "원");
    }



    private void toggleP() {
        if ("년별".equals(period.getSelectedItem())) {
            monthCB1.setVisible(false);
            monthCB2.setVisible(false);
        } else {
            monthCB1.setVisible(true);
            monthCB2.setVisible(true);
        }
    }

    private void confrimB() {
        int year1 = (int) yearCB1.getSelectedItem(); //콤보박스 데이터 출력
        int year2 = (int) yearCB2.getSelectedItem();

        if ("년별".equals(period.getSelectedItem())) { //년별이면
            LoadTransData(year1, null, year2, null);  // 년별 데이터 로드
        } else { //아니면 월별도 켜져있으니깐
            String month1 = (String) monthCB1.getSelectedItem();
            String month2 = (String) monthCB2.getSelectedItem();
            LoadTransData(year1, month1, year2, month2);  // 월별 데이터 로드
        }
    }


    private void LoadTransData(int year1, String month1, int year2, String month2) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        tableModel.setRowCount(0); // 기존 테이블 내용 초기화, 남아있으면 검색의미 x

        int totalIncome = 0;
        int totalExpenseL = 0;

        for (int year = year1; year <= year2; year++) {
            // 년별일 경우 월은 1월부터 12월까지 모두 검색하도록 수정, 처음에 null값으로 검색 안되서 애좀 먹음
            String startMonth = (month1 != null) ? month1 : "01";
            String endMonth = (month2 != null) ? month2 : "12";

            for (int month = Integer.parseInt(startMonth); month <= Integer.parseInt(endMonth); month++) {
                String yearMonth = String.format("%04d-%02d", year, month); // "yyyy-MM" 형식으로 년도와 월을 결합
                totalIncome += LoadTrans(dbConnection, yearMonth, "수입");
                totalExpenseL += LoadTrans(dbConnection, yearMonth, "지출");
            }
        }

        IncomeL.setText("총수입: " + totalIncome + "원");
        ExpenseL.setText("총지출: " + totalExpenseL + "원");
        BalanceL.setText("잔액: " + (totalIncome - totalExpenseL) + "원");

        dbConnection.close();
    }


    private int LoadTrans(DatabaseConnection dbConnection, String yearMonth, String type) {
        int totalAmount = 0;
        List<Object[]> transactions = dbConnection.getTransactions(userId, yearMonth, type);


        for (int i = 0; i < transactions.size(); i++) {
            Object[] row = transactions.get(i);
            Object[] newRow = new Object[row.length + 1];
            for (int j = 0; j < row.length; j++) {
                newRow[j] = row[j];
            }
            newRow[row.length] = type;  // 마지막에 '수입' 또는 '지출' 타입을 추가하는 부분 < 콤보박스라 따로 뺴옴
            tableModel.addRow(newRow);  //행에 추가
        }


        if (type.equals("수입")) {
            totalAmount = dbConnection.getTotalIncome(userId, yearMonth);
        } else {
            totalAmount = dbConnection.getTotalExpense(userId, yearMonth);
        }

        return totalAmount;
    }



    private void SearchCateg() {
        JDialog searchpop = new JDialog(this, "검색하기", true);
        searchpop.setSize(400, 300);  //사이즈
        searchpop.setLocationRelativeTo(this);   // 부모창 크기에 맞게 위치에 나오게

        JPanel panel = new JPanel(new GridLayout(6, 2));

        JTextField dateF = new JTextField();
        JTextField descripF = new JTextField();
        JTextField categF = new JTextField();
        JTextField amountF = new JTextField();
        JComboBox typeCB = new JComboBox(new String[]{"수입", "지출"});

        panel.add(new JLabel("날짜"));
        panel.add(dateF);
        panel.add(new JLabel("상세 내용"));
        panel.add(descripF);
        panel.add(new JLabel("카테고리"));
        panel.add(categF);
        panel.add(new JLabel("금액"));
        panel.add(amountF);
        panel.add(new JLabel("수입/지출"));
        panel.add(typeCB);



        JButton searchBtn = new JButton("검색");
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateF.getText();
                String description = descripF.getText();
                String category = categF.getText();
                String amount = amountF.getText();
                String type = (String) typeCB.getSelectedItem();

                filterConf(date, description, category, amount, type);
                // 검색 후 필터링 메소드로 간다
                searchpop.dispose();  //닫기
            }
        });

        panel.add(searchBtn);
        searchpop.add(panel);
        searchpop.setVisible(true);
    }

    // 검색 필터링 기능
    private void filterConf(String date, String description, String category, String amount, String type) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        tableModel.setRowCount(0);  // 기존 테이블 내용 초기화, 남아있으면 검색의미 x

        // 검색된 거래 데이터를 가져와 테이블에 추가
        List<Object[]> filteredTransactions = dbConnection.searchTrans(userId, date, description, category, amount, type);
        for (Object[] row : filteredTransactions) {
            tableModel.addRow(row);
        }
        dbConnection.close();  // DB 연결 종료
    }

}
