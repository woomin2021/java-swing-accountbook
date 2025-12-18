package java_pro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BottomPanel extends JPanel {
    private final DatabaseConnection dbConnection;
    private final int userId;
    private final DefaultTableModel incomeModel; // 수입 데이터를 표시
    private final DefaultTableModel expenseModel;  // 지출 데이터를 표시
    private JTextField dateField;   //날짜 받는거
    private JTextField detailsField;//거래 세부내용 메모
    private JTextField amountField; // 거래 금액
    private JComboBox<String> categoryField;//카테고리 필드
    private JComboBox<String> typeField; // 드롭다운 필드
    private final JLabel incomeLabel;// 총수입
    private final JLabel expenseLabel; //총지출
    private final JLabel balanceLabel;//총잔액
    private final LeftPanel leftPanel; // leftPanel과 연결 한거 수입/지출 요약 데이터를 업뎃을 하기위해

    public BottomPanel(int userId, DatabaseConnection dbConnection, LeftPanel leftPanel) { // BottomPanel 클래스의 생성자
        this.userId = userId; // 사용자 id 데이터 필터
        this.dbConnection = dbConnection; // 추가 삭제 하는데 사용
        this.leftPanel = leftPanel;  // 월별 데이터 요약 해주는거

        setLayout(new BorderLayout());

        // 통계 레이블 초기화 (화면에는 출력 안함 자꾸 애가 출력이 텍스트로 나와서 그냥 가려버림 leftPanel에 나오는게 맞음)
        //레이블은 월별 거래 요약 데이터를 표시합니다.
        //이들은 LeftPanel에 데이터를 전달하고 동기화하는 데 사용됩니다.
        incomeLabel = new JLabel("수입: 0원");
        expenseLabel = new JLabel("지출: 0원");
        balanceLabel = new JLabel("수입 - 지출: 0원");

        // 트랜잭션 탭 수입 지출 왔다 갔다 하는거
        //JTabbedPane를 사용하여 수입과 지출을 각각 별도의 탭으로 관리.
        // 그룹홀더 JTabbedPan 사용한거 12주차 -1 에 자세히 설명 되 있음
        JTabbedPane tabs = new JTabbedPane();
        // 수입 지출 데이터 관리
        // 열 관리 그리고 초기 데이터 비어있음
        // income expense에 jTable로 안에 있음 JScroll로 사용
        incomeModel = new DefaultTableModel(new String[]{"Date", "Details", "Category", "Amount"}, 0); //incomeModel은 데이터를 담고 관리하는 홀더 객체
        expenseModel = new DefaultTableModel(new String[]{"Date", "Details", "Category", "Amount"}, 0);
        //DefaultTableModel를 사용한이유 테이블의 데이터 추가 삭제를 간단하게 사용 가능해서 addRow removeRow특정 행 삭제 가 손 쉬움
        tabs.addTab("수입", new JScrollPane(new JTable(incomeModel))); // 탭이 두개니까 addtab 두개 만듬
        tabs.addTab("지출", new JScrollPane(new JTable(expenseModel)));
        add(tabs, BorderLayout.CENTER);

        // 입력 패널 추가 (밑에 데이터 추가하는 곳)
        JPanel inputPanel = madeInputPanel();
        add(inputPanel, BorderLayout.SOUTH);

        // 현재 날짜 가져옴
        //적용 버튼이 활성화 되면 이달의 가계분석의 내용을 보여주기 위해서 가져옴 or 초기 데이터를 기본적으로  설정하기 위해서
        nowMonthly(LocalDate.now().getYear() + "", String.format("%02d", LocalDate.now().getMonthValue()));
        // 가져온 년도와 월에 해당하는 수입지출이 데베가 로드됨 그리고 UI 초기화 함

    }

    private JPanel madeInputPanel() { // 매서드
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        //거래 날짜 상세 내용 금액 받는 필드 모음
        dateField = new JTextField("YYYY-MM-DD", 10);
        detailsField = new JTextField(10);
        amountField = new JTextField(10);
        categoryField = new JComboBox<>(new String[]{"주수입", "부수입", "주거비", "식료품", "생활용품", "의료비", "기타"}); // 거래의 범주 Jcombo 메뉴
        typeField = new JComboBox<>(new String[]{"수입", "지출"}); // 이것도 jComboBox 사용해서 수입 인지 지출인지 정하게

        JButton addButton = new JButton("추가");
        JButton deleteButton = new JButton("삭제");
        //컴포너트 순서대로 추가
        inputPanel.add(dateField);
        inputPanel.add(detailsField);
        inputPanel.add(categoryField);
        inputPanel.add(amountField);
        inputPanel.add(typeField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        // 거래 추가 버튼에 대한 액션 리스너
        addButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addTransaction(); // 거래 추가 메서드 호출
            }
        });

// 거래 삭제 버튼에 대한 액션 리스너
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                deleteTransaction(); // 거래 삭제 메서드 호출
            }
        });

        return inputPanel;
    }

    private void addTransaction() {
        //필드에서 입력값을 읽어옴
        //amount가 숫자가 아닌 경우 오류 메시지를 표시하고 종료
        String date = dateField.getText();
        String details = detailsField.getText();
        String category = (String) categoryField.getSelectedItem(); // 아이템
        int amount;
        try {
            amount = Integer.parseInt(amountField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "금액은 숫자만 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String type = (String) typeField.getSelectedItem();

        if (dbConnection.addTransaction(userId, date, details, category, amount, type)) {
            JOptionPane.showMessageDialog(this, "거래가 성공적으로 추가되었습니다.");
            DefaultTableModel model = type.equals("수입") ? incomeModel : expenseModel; // 거래 타입이 수입이면 incomeModel추가   지출이면 expenseModel 추가
            model.addRow(new Object[]{date, details, category, amount}); // >> 이게바로 DefaultTableModel를 쓴 이유 간단하게 addrow 메서드를 사용해 나의 필드들을 쉽게 추가 가능
            updateMonthlyCont();
            // 데이터 변경 후 새롭게 다시 갱신
            // db에 저장 데이터를 가능함 그리고 성공하면 이제 테이블에 새행 추가 가능함
        } else {
            JOptionPane.showMessageDialog(this, "거래 추가 실패.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaction() {
        // JTabbedPane의 참조를 명시적으로 가져옴
        //현재 선택된 탭이 수입인지 지출인지 확인하는거
        JTabbedPane tabs = (JTabbedPane) getComponent(0); // 첫 번째 컴포넌트가 JTabbedPane임 확실하게 해줌
        int selectedTab = tabs.getSelectedIndex(); // 선택된 탭 인덱스 가져오기

        if (selectedTab == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 탭을 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 선택된 탭의 JTable 참조 가져오기
        // 표를 만들어주는 jtable를 사용해 삭제를 하는
        JTable table = (JTable) ((JScrollPane) tabs.getComponentAt(selectedTab)).getViewport().getView();
        int selectedRow = table.getSelectedRow(); // 선택된 행 가져오기

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 데이터를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 선택된 행의 데이터 가져오기
        DefaultTableModel model = selectedTab == 0 ? incomeModel : expenseModel;
        String date = (String) model.getValueAt(selectedRow, 0);
        String details = (String) model.getValueAt(selectedRow, 1);
        String category = (String) model.getValueAt(selectedRow, 2);
        int amount = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
        String type = selectedTab == 0 ? "수입" : "지출";

        // 데이터베이스에서 삭제 시도
        if (dbConnection.deleteTransaction(userId, date, details, category, amount, type)) {
            model.removeRow(selectedRow); // 테이블에서 행 제거 addrow와 같은로직
            JOptionPane.showMessageDialog(this, "거래가 성공적으로 삭제되었습니다.");
            updateMonthlyCont(); // 요약 정보 업데이트 삭제도 바로 다시 갱신 해줌
        } else {
            JOptionPane.showMessageDialog(this, "거래 삭제 실패.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }



    public void nowMonthly(String year, String month) {
        int totalIncome = dbConnection.getTotalIncome(userId, year + "-" + month);
        int totalExpense = dbConnection.getTotalExpense(userId, year + "-" + month);
        //getTotalIncome 및 getTotalExpense를 통해 총 수입 및 지출을 가져옴
        // LeftPanel 업데이트
        leftPanel.updateMonth(totalIncome, totalExpense);

        incomeModel.setRowCount(0); //기존 데이터를 삭제 하고 새 데이터를 추가하기위한 거
        expenseModel.setRowCount(0);
        // 거래 리스트를 가져옴 수입 또는 지출꺼를
        List<Object[]> income = dbConnection.getTransactions(userId, year + "-" + month, "수입");
        List<Object[]> expense = dbConnection.getTransactions(userId, year + "-" + month, "지출");

        income.forEach(incomeModel::addRow); // 수입 테이블에 추가 해줌
        expense.forEach(expenseModel::addRow);
        // 기존 데이터 모두 지우고 새 데이터 추가 해줌
    }

    private void updateMonthlyCont() {
        String currentYear = LocalDate.now().getYear() + "";
        String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());
        nowMonthly(currentYear, currentMonth);
        // 현재 월의 데이터를 자동으로 갱신 해준다
    }

    public void setDateField(String date) {
        dateField.setText(date);
    } // 외부 업뎃 가능
}
