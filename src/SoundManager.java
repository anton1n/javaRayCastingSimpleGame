import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> clips;
    private AudioInputStream audioStream;

    public SoundManager() {
        clips = new HashMap<>();
    }

    public void loadSound(String identifier, String path) {
        try {
            audioStream = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clips.put(identifier, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playSound(String identifier) {
        Clip clip = clips.get(identifier);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loopSound(String identifier) {
        Clip clip = clips.get(identifier);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.setLoopPoints(0, -1);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.out.println("Clip not found for identifier: " + identifier);
        }
    }



    public void stopSound(String identifier) {
        Clip clip = clips.get(identifier);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void close() {
        for (Clip clip : clips.values()) {
            clip.close();
        }
        try {
            audioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
