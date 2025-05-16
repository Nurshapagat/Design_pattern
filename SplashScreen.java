
package com.owner.gaming.canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import jaco.mp3.player.MP3Player;
import com.owner.gaming.utils.MusicPlayer;

public class SplashScreen extends JWindow {
    private JLabel label = new JLabel();
    private MP3Player player;

    public SplashScreen() throws IOException {
        setSize(1440, 900);
        setLayout(null);

        ImageIcon icon = new ImageIcon(ImageIO.read(Board.class.getResource("splash.png")));
        label.setIcon(icon);
        label.setBounds(0, 0, 1440, 900);
        this.add(label);

        // START GAME button
        JButton startBtn = new JButton("START GAME");
        startBtn.setBounds(620, 600, 200, 50);
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(startBtn);

        // EXIT button
        JButton exitBtn = new JButton("EXIT");
        exitBtn.setBounds(620, 670, 200, 50);
        exitBtn.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(exitBtn);

        // Ensure buttons are above background image
        label.setLayout(null);
        label.add(startBtn);
        label.add(exitBtn);

        setLocationRelativeTo(null);
        setVisible(true);

        playSound();

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.stop();
                setVisible(false
                );
                dispose();
                try {
                    GameFrame gameFrame = new GameFrame();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void playSound() {
        player = new MP3Player(SplashScreen.class.getResource("sound.mp3"));
        player.play();
        com.owner.gaming.utils.MusicPlayer.startMusic();
    }

    public static void main(String[] args) {
        try {
            new SplashScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
