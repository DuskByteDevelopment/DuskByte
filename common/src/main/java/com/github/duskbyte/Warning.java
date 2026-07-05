package com.github.duskbyte;

import javax.swing.*;

public class Warning {

    public static void show() {
        JOptionPane pane = new JOptionPane(
                "这个客户端是免费的\n别拿去改一改就卖\n卖的你看着办\n\n" +
                "GitHub 开源的 你卖你试试",
                JOptionPane.WARNING_MESSAGE
        );
        JDialog dialog = pane.createDialog(null, "DISCLAIMER");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
