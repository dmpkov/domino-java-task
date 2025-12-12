package com.dmpkov.domino;

import com.dmpkov.domino.service.GameService;
import com.dmpkov.domino.view.GameWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            GameService service = new GameService();

            GameWindow window = new GameWindow(service);

            window.setVisible(true);
        });
    }
}