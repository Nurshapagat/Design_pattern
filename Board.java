
package com.owner.gaming.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.owner.gaming.sprites.KenPlayer;
import com.owner.gaming.sprites.Power;
import com.owner.gaming.sprites.RyuPlayer;
import com.owner.gaming.utils.GameConstants;
import com.owner.gaming.utils.PlayerConstants;
import com.owner.gaming.utils.MusicPlayer;

public class Board extends JPanel implements GameConstants, PlayerConstants {
    BufferedImage imageBg;
    private RyuPlayer ryuPlayer;
    private KenPlayer kenPlayer;
    private Timer timer;
    private Power ryuPower;
    private Power kenPower;
    private long lastKenAttackTime = 0;
    private long kenCooldownUntil = 0;
    private long ryuImmuneUntil = 0;
    private boolean isGameOver;
    private boolean isBlocking = false;
    private String victoryMessage = "";

    public Board() throws IOException {
        loadBackgroundImage();
        ryuPlayer = new RyuPlayer();
        kenPlayer = new KenPlayer();
        setFocusable(true);
        bindEvents();
        gameLoop();
        loadPower();
        MusicPlayer.startMusic(); // Auto music on
    }

    private void loadPower() {
        ryuPower = new Power(50, "Ryu".toUpperCase());
        kenPower = new Power(GWIDTH/2+150, "Ken".toUpperCase());
    }

    private void paintPower(Graphics pen) {
        ryuPower.printBox(pen);
        kenPower.printBox(pen);
    }

    public void collision() {
        long now = System.currentTimeMillis();

        if (isCollide()) {
            if (ryuPlayer.isAttacking() && kenPlayer.isAttacking()) {
                kenPlayer.setCurrentMove(DAMAGE);
                kenPower.setHealth();
                ryuPlayer.setCurrentMove(DAMAGE);
                ryuPower.setHealth();
            } else if (ryuPlayer.isAttacking()) {
                kenPlayer.setCurrentMove(DAMAGE);
                kenPower.setHealth();
            } else if (kenPlayer.isAttacking()) {
                if (now > ryuImmuneUntil && !isBlocking) {
                    ryuPlayer.setCurrentMove(DAMAGE);
                    ryuPower.setHealth();
                    ryuImmuneUntil = now + 2000;
                }
            }

            if (kenPower.getHealth() <= 0) {
                isGameOver = true;
                victoryMessage = "RYU WINS!";
            } else if (ryuPower.getHealth() <= 0) {
                isGameOver = true;
                victoryMessage = "KEN WINS!";
            }

            ryuPlayer.setCollide(true);
            ryuPlayer.setSpeed(0);
            kenPlayer.setCollide(true);
            kenPlayer.setSpeed(0);
            ryuPlayer.setAttacking(false);
            kenPlayer.setAttacking(false);
        } else {
            ryuPlayer.setSpeed(SPEED);
            kenPlayer.setSpeed(SPEED);
        }
    }

    private void printMessage(Graphics pen) {
        pen.setColor(Color.RED);
        pen.setFont(new Font("times",Font.BOLD, 50));
        pen.drawString("Game Over", GWIDTH/2-145, GHEIGHT/2-270);
    }

    private boolean isCollide() {
        int xDistance = Math.abs(ryuPlayer.getX() - kenPlayer.getX());
        int yDistance = Math.abs(ryuPlayer.getY() - kenPlayer.getY());
        int maxW = Math.max(ryuPlayer.getW(), kenPlayer.getW());
        int maxH = Math.max(ryuPlayer.getH(), kenPlayer.getH());
        return xDistance <= maxW - 10 && yDistance <= maxH;
    }

    private void gameLoop() {
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
                ryuPlayer.fall();
                collision();

                int dx = ryuPlayer.getX() - kenPlayer.getX();
                if (Math.abs(dx) > 60) {
                    if (dx > 0) {
                        kenPlayer.setSpeed(SPEED);
                        kenPlayer.setCurrentMove(WALK);
                        kenPlayer.move();
                    } else {
                        kenPlayer.setSpeed(-SPEED);
                        kenPlayer.setCurrentMove(WALK);
                        kenPlayer.move();
                    }
                } else {
                    if (Math.random() > 0.5) {
                        kenPlayer.setAttacking(true);
                        kenPlayer.setCurrentMove(PUNCH);
                    } else {
                        kenPlayer.setAttacking(true);
                        kenPlayer.setCurrentMove(KICK);
                    }
                }
            }
        });
        timer.start();
    }

    private void bindEvents() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                ryuPlayer.setSpeed(0);
                kenPlayer.setSpeed(0);
                isBlocking = false;
                ryuPlayer.setAttacking(false);
                kenPlayer.setAttacking(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        ryuPlayer.setSpeed(-SPEED);
                        ryuPlayer.setCurrentMove(WALK);
                        ryuPlayer.move();
                        ryuPlayer.setCollide(false);
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (!ryuPlayer.isCollide()) {
                            ryuPlayer.setCollide(false);
                            ryuPlayer.setSpeed(SPEED);
                        }
                        ryuPlayer.setCurrentMove(WALK);
                        ryuPlayer.move();
                    }
                    case KeyEvent.VK_K -> {
                        isBlocking = false;
                        ryuPlayer.setAttacking(true);
                        ryuPlayer.setCurrentMove(KICK);
                        ryuPlayer.move();
                    }
                    case KeyEvent.VK_P -> {
                        isBlocking = false;
                        ryuPlayer.setAttacking(true);
                        ryuPlayer.setCurrentMove(PUNCH);
                        ryuPlayer.move();
                    }
                    case KeyEvent.VK_SPACE -> ryuPlayer.jump();
                    case KeyEvent.VK_B -> isBlocking = true;
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics pen) {
        super.paintComponent(pen);
        printBackgroundImage(pen);
        ryuPlayer.printPlayer(pen);
        kenPlayer.printPlayer(pen);
        paintPower(pen);
        if (isGameOver) {
            MusicPlayer.stopMusic(); // Auto music off
            printVictory(pen);
            printMessage(pen);
            timer.stop();
        }
    }

    private void printVictory(Graphics pen) {
        pen.setColor(Color.YELLOW);
        pen.setFont(new Font("Arial", Font.BOLD, 48));
        pen.drawString(victoryMessage, GWIDTH / 2 - 180, GHEIGHT / 2 - 100);
    }

    private void printBackgroundImage(Graphics pen) {
        pen.drawImage(imageBg, 0, 0, GWIDTH, GHEIGHT, null);
    }

    private void loadBackgroundImage() {
        try {
            imageBg = ImageIO.read(Board.class.getResource("bg.jpeg"));
        } catch (Exception ex) {
            System.out.println("Background Image Loading Fail...");
            System.exit(0);
        }
    }
}
