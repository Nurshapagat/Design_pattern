package src.main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Sound {

    Clip clip;
    String[] soundPaths = new String[5];

    public Sound() {
        soundPaths[0] = "Dungeon Survivor/res/sounds/dice.wav";
        soundPaths[1] = "Dungeon Survivor/res/sounds/Hit_Hurt.wav";
        soundPaths[2] = "Dungeon Survivor/res/sounds/move.wav";
        soundPaths[3] = "Dungeon Survivor/res/sounds/dice1.wav";
    }

    public void setFile(int i) {
        try {
            File soundFile = new File(soundPaths[i]);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки звука: " + soundPaths[i]);
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0); // чтобы звук начинался сначала при каждом воспроизведении
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
