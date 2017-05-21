import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;

public class Sound {
  private byte[] mBytes;
  private DataLine.Info mInfo;
  private AudioFormat mFormat;

  private Sound() {
	  
  }

  public static Sound loadFromFile(String filename) {
    try {
      Sound s = new Sound();
      File audioFile = new File(filename);
      System.out.println(audioFile.canRead());
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
      s.mFormat = audioStream.getFormat();
      s.mInfo = new DataLine.Info(Clip.class, audioStream.getFormat());
      s.mBytes = new byte[(int) (s.mFormat.getFrameSize() * audioStream.getFrameLength())];
      audioStream.read(s.mBytes);
      return s;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void play() {
    try {
      Clip c = (Clip) AudioSystem.getLine(mInfo);
      c.open(mFormat, mBytes, 0, mBytes.length);
      c.start();
    } catch (Exception ex) {
      ;
    }
  }

  public Clip playLooping() {
    try {
      Clip c = (Clip) AudioSystem.getLine(mInfo);
      c.open(mFormat, mBytes, 0, mBytes.length);
      c.loop(Clip.LOOP_CONTINUOUSLY);
      return c;
    } catch (Exception ex) {
      return null;
    }
  }
}