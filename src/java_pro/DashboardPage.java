package java_pro;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Year;

public class DashboardPage extends JFrame {
    private final DatabaseConnection dbConnection; // 데베 작업 수행 하는거 
    private final int userId; // 로그인한 사용자 id 저장 사용자별 데이터 저장 

    public DashboardPage(int userId) {
        this.userId = userId;
        setTitle("Myongji Coin - Dashboard"); // 제목 
        setSize(1800, 1000); // 창 크기 이런것는 만지지말아주세요 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창닫기 
        setLocationRelativeTo(null); // 중앙에 위치 하게 

        dbConnection = new DatabaseConnection(); // 작업 수행 가능 한거임 수입 지출 찍을 수 있게 

        JPanel mainPanel = new JPanel(new BorderLayout()); // Border로 써서 5개 영역으로 나눔 

        // LeftPanel 생성 및 추가
        LeftPanel leftPanel = new LeftPanel(this, userId, dbConnection); // 왼쪽 메뉴 담당 로그아웃 가계분석 입력 내역 담당함
        mainPanel.add(leftPanel, BorderLayout.WEST);// west 쪽의 left패널에 추가 

        // BottomPanel 생성 및 LeftPanel 전달
        BottomPanel bottomPanel = new BottomPanel(userId, dbConnection, leftPanel); // 수입 지출 추가 삭제 등을 할 수 있는 패널
        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // south 추가 했음 

        // CalendarPanel 생성 및 추가
        mainPanel.add(new CalendarPanel(userId, dbConnection, bottomPanel), BorderLayout.CENTER);
        // 사용자 데이터 데베 연결 날짜 선택 입력 업뎃 거래 필터 
        add(mainPanel);

        // 현재 월 요약 정보 로드 및 LeftPanel 업데이트 가능 
        CurrentMonth(leftPanel);
    }

    //현재 연도와 월의 데이터를 불러와 LeftPanel을 업데이트 하는 메서드
    private void CurrentMonth(LeftPanel leftPanel) {
        String currentYear = String.valueOf(Year.now().getValue());
        String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());

        // 데베에서 현재 월의 수입 및 지출 정보 가져오기
        int totalIncome = dbConnection.getTotalIncome(userId, currentYear + "-" + currentMonth);
        int totalExpense = dbConnection.getTotalExpense(userId, currentYear + "-" + currentMonth);

        // LeftPanel에 데이터 업데이트
        leftPanel.updateMonth(totalIncome, totalExpense);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DashboardPage(1).setVisible(true); // DashboardPage 초기화 및 표시
            }
        });
        //GUI 업데이트는 항상 Event Dispatch Thread (EDT)에서 실행해야 합니다. invokeLater는 EDT에서 실행 대기열에 작업을 추가합니다.
        //이 방식은 Java Swing 프로그램이 스레드-안전하게 실행되도록 보장합니다.
        //CurrentMonth 메서드
        //현재 연도와 월을 가져와 데이터베이스에서 사용자의 수입 및 지출 데이터를 가져오고, 이를 LeftPanel에 업데이트하는 기능을 수행합니다.
        //GUI 프로그램의 실행을 보장하기 위해 Event Dispatch Thread 에서 DashboardPage를 실행.
        //Runnable 익명 클래스를 사용하여 DashboardPage를 화면에 표시.

        //CurrentMonth
        //Year.now().getValue()와 LocalDate.now().getMonthValue()로 현재 연도와 월을 가져옴.
        // DatabaseConnection의 getTotalIncome과 getTotalExpense를 호출하여 수입과 지출 데이터를 가져옴.
        //LeftPanel의 updateMonth 메서드로 데이터를 전달하여 UI를 업데이트.
    }
}
