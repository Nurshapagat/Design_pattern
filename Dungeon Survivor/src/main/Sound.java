package src.main;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class Sound {

    private Clip clip;          // Для эффектов (шаги и т.п.)
    private Clip bgMusicClip;   // Для фоновой музыки

    private final String bgMusicPath = "/res/sounds/back1.wav";
    private final String[] soundPaths = new String[5];

    public Sound() {
        soundPaths[2] = "/res/sounds/step.wav"; // пример звука
    }

    public void setFile(int i) {
        try {
            if (i < 0 || i >= soundPaths.length || soundPaths[i] == null) {
                return;
            }
            if (clip != null && clip.isOpen()) {
                return;
            }

            InputStream audioSrc = getClass().getResourceAsStream(soundPaths[i]);
            if (audioSrc == null) {
                System.out.println("Не найден ресурс звука: " + soundPaths[i]);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

            if (clip != null && clip.isOpen()) {
                clip.close();
            }

            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки звука: " + soundPaths[i]);
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void loadBackgroundMusic() {
        try {
            if (bgMusicClip != null && bgMusicClip.isOpen()) {
                return;
            }

            InputStream audioSrc = getClass().getResourceAsStream(bgMusicPath);
            if (audioSrc == null) {
                System.out.println("Не найден ресурс фоновой музыки: " + bgMusicPath);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

            if (bgMusicClip != null && bgMusicClip.isOpen()) {
                bgMusicClip.close();
            }

            bgMusicClip = AudioSystem.getClip();
            bgMusicClip.open(ais);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки фоновой музыки: " + bgMusicPath);
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (bgMusicClip != null) {
            if (!bgMusicClip.isRunning()) {
                bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } else {
            System.out.println("Фоновая музыка не загружена, не могу запустить");
        }
    }

    public void stopBackgroundMusic() {
        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            bgMusicClip.stop();
        }
    }

}
