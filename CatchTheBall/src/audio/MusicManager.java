package OOP_PROJECT.CatchTheBall.src.audio;

import javax.sound.sampled.*;

public class MusicManager {
    private Thread           musicThread;
    private volatile boolean running      = false;
    private volatile boolean muted        = false;
    private volatile float   volume       = 0.4f;
    private volatile int     currentTheme = 0;
    private volatile int     targetTheme  = 0;
    private volatile boolean changingTheme = false;
    private static final int SAMPLE_RATE  = 44100;

    public void setThemeForLevel(int level) {
        int theme;
        if      (level <= 3) theme = 0;
        else if (level <= 6) theme = 1;
        else if (level <= 9) theme = 2;
        else                 theme = 3;
        if (theme != currentTheme) {
            targetTheme   = theme;
            changingTheme = true;
        }
    }

    public void start() {
        if (running) return;
        running = true;
        musicThread = new Thread(() -> {
            while (running) {
                if (changingTheme) {
                    currentTheme  = targetTheme;
                    changingTheme = false;
                }
                if (!muted) playTheme(currentTheme);
                else        sleep(200);
            }
        });
        musicThread.setDaemon(true);
        musicThread.start();
    }

    public void stop() {
        running = false;
        if (musicThread != null) musicThread.interrupt();
    }

    private void playTheme(int theme) {
        try {
           byte[] data = generateTheme(theme, 8);
            AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(fmt);
            line.open(fmt);
            FloatControl fc = null;
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN))
                fc = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            if (fc != null) {
                float db = volume < 0.001f ? fc.getMinimum() : 20f * (float)Math.log10(volume);
                fc.setValue(Math.max(fc.getMinimum(), Math.min(fc.getMaximum(), db)));
            }
            line.start();
            int chunkSize = 4410, offset = 0;
            while (offset < data.length && running && !changingTheme) {
                int len = Math.min(chunkSize, data.length - offset);
                line.write(data, offset, len);
                offset += len;
            }
            line.drain(); line.close();
        } catch (Exception ignored) {}
    }

    private byte[] generateTheme(int theme, int seconds) {
        int samples = SAMPLE_RATE * seconds;
        byte[] buf  = new byte[samples * 2];
        double[] noteFreqs;
        double tempo;
        switch (theme) {
            case 0:  noteFreqs = new double[]{261.6, 293.7, 329.6, 392.0, 440.0}; tempo = 4.0; break;
            case 1:  noteFreqs = new double[]{392.0, 440.0, 493.9, 523.3, 587.3}; tempo = 6.0; break;
            case 2:  noteFreqs = new double[]{220.0, 246.9, 261.6, 293.7, 329.6}; tempo = 7.0; break;
            default: noteFreqs = new double[]{130.8, 196.0, 261.6, 329.6, 392.0}; tempo = 8.0; break;
        }
        int noteLen = (int)(SAMPLE_RATE / tempo);
        for (int i = 0; i < samples; i++) {
            int    noteIdx = (i / noteLen) % noteFreqs.length;
            double freq    = noteFreqs[noteIdx];
            double t       = i / (double) SAMPLE_RATE;
            double env     = Math.min(1.0, Math.min((i % noteLen) / 200.0, (noteLen - i%noteLen) / 200.0));
            double wave    = Math.sin(2*Math.PI*freq*t)*0.6
                           + Math.sin(2*Math.PI*freq*2*t)*0.25
                           + Math.sin(2*Math.PI*freq*3*t)*0.1;
            short val = (short)(wave * 32767 * 0.28 * env);
            buf[i*2]   = (byte)(val & 0xFF);
            buf[i*2+1] = (byte)((val >> 8) & 0xFF);
        }
        return buf;
    }

    private void sleep(int ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }

    public boolean isMuted()          { return muted; }
    public void    setMuted(boolean m){ muted = m; }
    public void    toggleMute()       { muted = !muted; }
    public float   getVolume()        { return volume; }
    public void    setVolume(float v) { volume = Math.max(0f, Math.min(1f, v)); }
}