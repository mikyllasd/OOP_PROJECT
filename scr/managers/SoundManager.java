package managers;
import javax.sound.sampled.*;
public class SoundManager {
    private boolean muted = false;
    private float masterVolume = 0.5f;

    public void playTone(int frequency, int durationMs, float volume) {
        if (muted) return;
        float vol = volume * masterVolume;
        new Thread(() -> {
            try {
                AudioFormat fmt = new AudioFormat(44100, 16, 1, true, false);
                int samples = (int)(44100 * durationMs / 1000.0);
                byte[] buf  = new byte[samples * 2];
                for (int i = 0; i < samples; i++) {
                    double angle = 2.0 * Math.PI * frequency * i / 44100.0;
                    double env   = Math.min(1.0,
                            Math.min(i / 200.0, (samples - i) / 200.0));
                    short val = (short)(Math.sin(angle) * 32767 * vol * env);
                    buf[i * 2]     = (byte)(val & 0xFF);
                    buf[i * 2 + 1] = (byte)((val >> 8) & 0xFF);
                }
                SourceDataLine line = AudioSystem.getSourceDataLine(fmt);
                line.open(fmt, buf.length);
                line.start();
                line.write(buf, 0, buf.length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        }).start();
    }
    public void playCatch(boolean isGood) {
        if (isGood) playTone(880, 80, 0.3f);
        else        playTone(220, 200, 0.3f);
    }
    public void playLevelUp() {
        new Thread(() -> {
            int[] notes = {523, 659, 784, 1047};
            for (int n : notes) { playTone(n, 100, 0.25f); sleep(90); }
        }).start();
    }
    public void playCombo() {
        new Thread(() -> {
            playTone(1047, 60, 0.2f); sleep(50);
            playTone(1319, 80, 0.2f);
        }).start();
    }
    public void playPowerUp()  { playTone(1200, 150, 0.25f); }
    public void playBadCatch() { playTone(180, 250, 0.35f); }
    public void playLowTime()  { playTone(440, 100, 0.2f); }
    public void playGameOver() {
        new Thread(() -> {
            int[] notes = {523, 392, 330, 262};
            for (int n : notes) { playTone(n, 150, 0.25f); sleep(140); }
        }).start();
    }
    public void playMenuClick() { playTone(660, 60, 0.15f); }
    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
    public boolean isMuted()            { return muted; }
    public void toggleMute()            { muted = !muted; }
    public void setMuted(boolean m)     { muted = m; }
    public float getMasterVolume()      { return masterVolume; }
    public void setMasterVolume(float v){ masterVolume = Math.max(0f, Math.min(1f, v)); }
}