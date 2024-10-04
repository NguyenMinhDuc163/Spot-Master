package client.view.widget;

import javax.swing.*;
import java.awt.*;

//Tùy chỉnh kiểu nút bấm
public class ButtonWidget extends JButton
{
    public ButtonWidget(String text)
    {
        super.setBackground(Color.DARK_GRAY);
        super.setForeground(Color.ORANGE);
        super.setFont(new Font("Tahoma",0,20)); // Giữ nguyên font chữ
        super.setText(text);
        super.setFocusPainted(false);
    }
}
