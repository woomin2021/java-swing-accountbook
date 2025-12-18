package java_pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Info extends JPanel {

    public Info() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // 컴포넌트 주변 여백 설정

        // 장점
        JLabel good = new JLabel("<html>" +
                "<h2>장점</h2>" +
                "<ul>" +
                "<li>얼마를 벌어 얼마나 사용했는지를 알 수 있기 때문에 가정의 수입과 지출을 파악하는 데 큰 도움이 된다.</li>" +
                "<li>가정에서 어떤 항목의 씀씀이가 많은지를 세세하게 적을 수 있다.</li>" +
                "<li>씀씀이에 따라 현명한 지출을 하기 위한 노력을 할 수 있다.</li>" +
                "</ul>" +
                "</html>");
        good.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        good.setAlignmentX(Component.LEFT_ALIGNMENT);  // 왼쪽 정렬
        add(good);

        // Advice
        JLabel advice = new JLabel("Advice: 충동적인 소비를 줄이고 저축 목표를 세워라.");
        advice.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        advice.setForeground(Color.RED);
        advice.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(advice);


        //  여백 추가
        add(Box.createRigidArea(new Dimension(0, 20)));  // 세로로 20px 간격 추가




        JLabel quoteEx = new JLabel("명언을 보려면 아래 버튼을 눌러보세요!");
        quoteEx.setFont(new Font("Malgun Gothic", Font.ITALIC, 14));
        quoteEx.setForeground(Color.BLUE);
        quoteEx.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(quoteEx);

        add(Box.createRigidArea(new Dimension(0, 20)));

        // 명언버튼
        JButton quoteButton = new JButton("명언 보기");
        quoteButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
        quoteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(quoteButton);

        add(Box.createRigidArea(new Dimension(0, 40)));


        // 명언
        String[] quotes = {
                "위대한 성과는 힘이 아닌 인내의 산물이다. - 새뮤얼 존슨",
                "우리가 가진 것은 결코 우연이 아니다. 우리가 가진 것은 우리가 끊임없이 노력하고 노력한 결과물이다. - 레오나르도 다 빈치",
                "어떤 일이든, 처음부터 완벽할 필요는 없다. 중요한 것은 시작하는 것이다. - 존.C.맥스웰",
                "한 걸음씩 나아가자. 그렇게 하면 이전보다 더 가까워지게 된다. - 마야 앤젤루",
                "당신은 절대로 혼자이지 않습니다. 언제나 도와줄 사람들이 있습니다. - 오드리 햅번"
        };



        int[] index = {0}; // 배열로 선언하여 final 사용하지 않고도 내부 값 변경 가능

        quoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quoteEx.setText(quotes[index[0]]); // 현재 인덱스의 명언 표시
                index[0] = (index[0] + 1) % quotes.length; // 인덱스를 순환
            }
        });
    }
}
