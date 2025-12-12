package com.dmpkov.domino.view;

import com.dmpkov.domino.model.DominoTile;
import com.dmpkov.domino.model.Player;
import com.dmpkov.domino.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class GameWindow extends JFrame {
    private GameService gameService;
    private JLabel statusLabel;
    private JTextArea boardArea;
    private JPanel handPanel;
    private JPanel infoPanel;

    public GameWindow(GameService gameService) {
        this.gameService = gameService;

        setTitle("Domino Game");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton newGameBtn = new JButton("Новая игра");
        newGameBtn.setFocusPainted(false);
        newGameBtn.addActionListener(e -> startNewGame());
        topPanel.add(newGameBtn, BorderLayout.WEST);

        statusLabel = new JLabel("Нажмите 'Новая игра'", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(statusLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        boardArea = new JTextArea();
        boardArea.setEditable(false);
        boardArea.setFont(new Font("Monospaced", Font.BOLD, 20));
        boardArea.setBackground(new Color(35, 100, 50));
        boardArea.setForeground(Color.WHITE);
        boardArea.setLineWrap(true);
        boardArea.setWrapStyleWord(true);
        add(new JScrollPane(boardArea), BorderLayout.CENTER);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(220, 0));

        infoPanel.setBackground(new Color(100, 60, 30));

        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(infoPanel, BorderLayout.EAST);

        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        handPanel.setPreferredSize(new Dimension(800, 130));

        handPanel.setBackground(new Color(100, 60, 30));

        add(handPanel, BorderLayout.SOUTH);
    }

    private void startNewGame() {
        String[] options = {"2 игрока", "3 игрока", "4 игрока"};
        int x = JOptionPane.showOptionDialog(this,
                "Количество игроков:", "Настройки",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (x == -1) return;

        gameService.startNewGame(x + 2);
        refreshUI();
    }

    private void refreshUI() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        if (gameService.getBoard().isEmpty()) {
            sb.append("   Стол пуст.");
        } else {
            sb.append("   (L) ");
            for (DominoTile t : gameService.getBoard().getChain()) {
                sb.append(t.toString()).append(" ");
            }
            sb.append("(R)");
        }
        boardArea.setText(sb.toString());

        statusLabel.setText(gameService.getStatusMessage());

        updateInfoPanel();

        handPanel.removeAll();
        Player current = gameService.getCurrentPlayer();

        if (gameService.isGameOver()) {
            JLabel endLabel = new JLabel("ИГРА ОКОНЧЕНА");
            endLabel.setFont(new Font("Arial", Font.BOLD, 24));
            endLabel.setForeground(Color.WHITE);
            handPanel.add(endLabel);
        } else {
            if (!current.isBot()) {
                List<DominoTile> hand = gameService.getPlayerHand(current);

                for (DominoTile tile : hand) {
                    String label = "<html><center>" +
                            "<span style='font-size:14px'>" + tile.getLeft() + "</span>" +
                            "<br><hr width='26'>" +
                            "<span style='font-size:14px'>" + tile.getRight() + "</span>" +
                            "</center></html>";

                    JButton btn = new JButton(label);
                    btn.setPreferredSize(new Dimension(50, 80));
                    btn.setMargin(new Insets(2, 2, 2, 2));
                    btn.addActionListener(e -> onPlayerCardClick(tile));
                    handPanel.add(btn);
                }

                JButton drawBtn = new JButton("<html><center>Взять<br>Пас</center></html>");
                drawBtn.setPreferredSize(new Dimension(80, 80));
                drawBtn.setBackground(new Color(255, 230, 200));
                drawBtn.addActionListener(e -> {
                    gameService.handleDraw(current);
                    refreshUI();
                });
                handPanel.add(drawBtn);

            } else {
                JLabel botLabel = new JLabel("Очередь: " + current.getName());
                botLabel.setFont(new Font("Arial", Font.ITALIC, 18));
                botLabel.setForeground(Color.WHITE);

                JButton nextBtn = new JButton("Сделать ход бота >>");
                nextBtn.addActionListener(e -> {
                    gameService.botTurn();
                    refreshUI();
                });

                handPanel.add(botLabel);
                handPanel.add(nextBtn);
            }
        }

        handPanel.revalidate();
        handPanel.repaint();
    }

    private void updateInfoPanel() {
        infoPanel.removeAll();

        JLabel title = new JLabel("ИНФОРМАЦИЯ");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(title);
        infoPanel.add(Box.createVerticalStrut(10));

        JLabel deckInfo = new JLabel("Базар: " + gameService.getDeckSize() + " шт.");
        deckInfo.setForeground(new Color(200, 255, 200));
        deckInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(deckInfo);
        infoPanel.add(Box.createVerticalStrut(15));

        for (Player p : gameService.getPlayers()) {
            int count = gameService.getPlayerHand(p).size();
            String text = p.getName() + ": " + count;

            if (p == gameService.getCurrentPlayer() && !gameService.isGameOver()) {
                text += "  <--";
            }

            JLabel pLabel = new JLabel(text);
            pLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            pLabel.setForeground(Color.WHITE);

            if (p == gameService.getCurrentPlayer()) {
                pLabel.setFont(new Font("Arial", Font.BOLD, 13));
                pLabel.setForeground(Color.YELLOW);
            }

            pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(pLabel);
            infoPanel.add(Box.createVerticalStrut(5));
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private void onPlayerCardClick(DominoTile tile) {
        Object[] sideOptions = {"Влево", "Вправо"};
        int n = JOptionPane.showOptionDialog(this,
                "Куда положить " + tile + "?",
                "Ход",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, sideOptions, sideOptions[0]);

        if (n == -1) return;

        boolean success = gameService.humanTurn(tile, n == 0);

        if (!success) {
            JOptionPane.showMessageDialog(this, "Нельзя сюда положить!");
        } else {
            refreshUI();
        }
    }
}