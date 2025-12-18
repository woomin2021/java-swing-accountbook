package java_pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Memo extends JPanel {

    private JTextField textField;
    private JTextArea textArea;

    public Memo() {

         textField = new JTextField("원하는 글을 적어보세요!", 30);


        textArea = new JTextArea(15, 30);  
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);


        JButton save = new JButton("메모 저장");
        JButton clear = new JButton("메모 지우기");


        save.addActionListener(new SaveAction());
        clear.addActionListener(new ClearAction());


        JPanel button = new JPanel(new FlowLayout());
        button.add(save);
        button.add(clear);


        setLayout(new BorderLayout(10, 10));


        add(new JLabel("메모를 입력하세요:"), BorderLayout.NORTH);
        add(textField, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }


    private class SaveAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String memo = textField.getText();


            if (!memo.isEmpty()) {
                textArea.append(memo + "\n");
                textField.setText("");
            }
        }
    }


    private class ClearAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            textField.setText("");
        }
    }
}
