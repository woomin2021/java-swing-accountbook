package java_pro;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.IntStream;

public class CalendarPanel extends JPanel {
    private final DatabaseConnection dbConnection;
    private final int userId;
        private final BottomPanel bottomPanel;
        private JPanel calendarGrid;

    // 사용자가 선택한 날짜를 찍어서 BottomPanel에 업뎃 (userId)
    // 데베 연결 객체 이거 없으면 데베에 데이터가 안들어감(dbConnection)달력에서 선택한 데이터를 BottomPanel과 데이터베이스 작업과 연결합니다
    //BottomPanel 객체임 이 클래스에서 선택한 날짜가 업데이트되는거
    public CalendarPanel(int userId, DatabaseConnection dbConnection, BottomPanel bottomPanel) { // 생성자
        this.userId = userId;
        this.dbConnection = dbConnection;
        this.bottomPanel = bottomPanel;

        setLayout(new BorderLayout());//상단에 연/월 선택 UI를 배치 ,중앙에 달력 그리드를 배치


        // 상단: 년/월 선택 및 적용 버튼
        JPanel choicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 년월 왼쪽 정렬
        //연도와 월을 고를수 있는 JComboBox 사용했음

        int startYear = 2024; // 기본값 2024년 부터 + 10년으로 했음
        String[] years = IntStream.range(startYear, startYear + 10) // 메서드는 startYear 부터 start + 10year 만큼 정수를 순차적으로 만들어줌 Ex 2024 .. 2025 +++
                .mapToObj(String::valueOf).toArray(String[]::new); // 객체 반환 문자열 반환 해 / 모든거를 toArray가 다 반환 해줌 스트름을 String에 저장하는거
        //요약 >> 숫자 생성 문자열 반환 배열 반환
        JComboBox<String> yearCombo = new JComboBox<>(years);
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{ // JComboBox 하나만 선택 가능한거
                "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
            // 1월 12월 까지 선택 가능하게 하는

        //선택한 년도를 월 기반으로 달력이 계속 업뎃 되고 BottomPanel에 월 선택된게 나오고 업뎃 됨
        JButton applyButton = new JButton("적용");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateCalendar(yearCombo, monthCombo); // 달력 갱신
                String selectedYear = (String) yearCombo.getSelectedItem();
                String selectedMonth = (String) monthCombo.getSelectedItem();
                bottomPanel.nowMonthly(selectedYear, selectedMonth); // 바텀 패널 갱신
                // 사용자가 연/월을 선택하고 적용 버튼을 누르면 호출됨
                // updateCalendar(yearCombo, monthCombo)로 호출하고  캘린더를 갱신하고
                // bottomPanel.nowMonthly(selectedYear, selectedMonth)로 BottomPanel의 수입/지출 데이터를 업데이트
            }
        });


        choicePanel.add(yearCombo);
        choicePanel.add(monthCombo);
        choicePanel.add(applyButton);

        // 캘린더 그리드 중앙에 위치 한다
        // GridLayout을 사용해서 7열로 있는 달력 생성
        //상단에 sun~mon 일욜 기준으로
        calendarGrid = new JPanel(new GridLayout(0, 7));
        add(choicePanel, BorderLayout.NORTH);
        add(calendarGrid, BorderLayout.CENTER);

        updateCalendar(yearCombo, monthCombo); // 이게 생성자 호출 되면 년,월 달력에 표시가 가능해지는
    }

    //선택된 년,월을 기반으로 달력 그리드를 업뎃
    //기존의 달력 데이터를 삭제하고 새로운 데이터를 추가.
    private void updateCalendar(JComboBox<String> yearCombo, JComboBox<String> monthCombo) {
        calendarGrid.removeAll();   // 기존 달력 제거 이게 선택 되고 추가나 삭제 되고 다 사라짐 그리고 다시
                                    // 적용 버튼 누르면 이제 내역 뜨는 그런 메커니즘을 구현 하기 위해 사용

        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        int month = Integer.parseInt((String) monthCombo.getSelectedItem());

        // 요일 헤더 추가 요일 이름은 calendarGrid의 첫 번째 행에 추가되는거
        String[] daysnames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysnames) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            calendarGrid.add(dayLabel);
        }

        // 날짜 계산
        YearMonth yearMonth = YearMonth.of(year, month); // 특정년도 년  월 보여줌 그리고 객체 만들어주고 2024년 1월 요런
        LocalDate firstDay = yearMonth.atDay(1); // 1일 을 반환하게 해놨음 Local 날짜 정보주는 클래스인데 이거 오류나면 import 안된거임 클릭해서 import해야함
        int goDaynames = firstDay.getDayOfWeek().getValue() % 7; //getDayOfWeek가 요일 가져옴 //Sun를 0으로 Mon를 1로 설정합니다 나누는거 ok?

        // goDaynames 만큼 빈 jLabel을 달력 그리드에 추가해줌 저게 5면 월 금 이전까지 빈칸을 5개 추가해줌
        // 예 1일이 금요일이면 빈칸 4개(0~3) (월화수목)를 추가 하는거 빈칸 추가 하는거라고 생각하는게 좋음
        for (int i = 0; i < goDaynames; i++) {
            calendarGrid.add(new JLabel(""));
        }
        // 날짜 버튼 추가
        //해당 월의 날짜 버튼(1일부터 마지막날까지)을 생성하고 클릭 이벤트를 추가하는거
        //버튼 클릭 시 선택된 날짜를 BottomPanel에 전달
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            // 해당 월(YearMonth 객체)의 날짜 수를 가져옴 1일부터 마지막 날까지 반복
            final int selectedDay = day; // day 값을 익명 클래스 내부에서 사용하기 위해 final로 선언 해주어야 함
            JButton dayButton = new JButton(String.valueOf(day)); // 현재 날짜를 표시하는 버튼 생성

            // 날짜 버튼에 이벤트 리스너 추가.
            dayButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 클릭한 날짜를 "YYYY-MM-DD" 형식의 문자열로 변환.
                    String formattedDate = String.format("%04d-%02d-%02d", year, month, selectedDay);
                    // 변환한 날짜를 BottomPanel의 dateField에 전달.
                    bottomPanel.setDateField(formattedDate);
                }
            });

            // 생성한 버튼을 캘린더의 그리드 레이아웃에 추가.
            calendarGrid.add(dayButton);
        }



        //새로운 데이터로 캘린더를 다시 보여주는 느낌
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
}
