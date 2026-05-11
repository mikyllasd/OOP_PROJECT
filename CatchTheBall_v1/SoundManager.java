import javax.sound.sampled.*;

/**
 * Generates simple synthesised sound effects using the Java Sound API.
 * No external audio files needed — all sounds are generated in memory.
 *
 * Demonstrates: Encapsulation, static utility class pattern.
 */
public class SoundManager {

    private static boolean muted = false;
    private static final int SAMPLE_RATE = 44100;

    public static void setMuted(boolean m) { muted = m; }
    public static boolean isMuted()        { return muted; }
    public static void toggleMute()        { muted = !muted; }

    // ── Public sound events ───────────────────────────────────────────

    public static void playCatch()    { playTone(520, 780, 80,  0.25f); }
    public static void playStar()     { playChord(new int[]{880, 1100}, new int[]{0, 80}, 100); }
    public static void playBomb()     { playNoise(200, 80, 300, 0.35f); }
    public static void playGameOver() { playTone(400, 150, 800, 0.3f); }
    public static void playTick()     { playTone(800, 800, 40,  0.1f); }
    public static void playCombo()    { playChord(new int[]{440, 554, 659}, new int[]{0, 80, 160}, 150); }

    public static void playLevelUp() {
        int[] freqs  = {523, 659, 784, 1047};
        int[] delays = {0,  100, 200, 300};
        playChord(freqs, delays, 180);
    }

    // ── Internal synthesis ────────────────────────────────────────────

    private static void playTone(int startFreq, int endFreq, int durationMs, float volume) {
        if (muted) return;
        new Thread(() -> {
            try {
                byte[] buf = generateTone(startFreq, endFreq, durationMs, volume, false);
                playBuffer(buf, durationMs);
            } catch (Exception ignored) { }
        }).start();
    }

    private static void playNoise(int startFreq, int endFreq, int durationMs, float volume) {
        if (muted) return;
        new Thread(() -> {
            try {
                byte[] buf = generateTone(startFreq, endFreq, durationMs, volume, true);
                playBuffer(buf, durationMs);
            } catch (Exception ignored) { }
        }).start();
    }

    private static void playChord(int[] freqs, int[] delays, int durationMs) {
        if (muted) return;
        for (int i = 0; i < freqs.length; i++) {
            final int freq  = freqs[i];
            final int delay = delays[i];
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    byte[] buf = generateTone(freq, freq, durationMs, 0.28f, false);
                    playBuffer(buf, durationMs);
                } catch (Exception ignored) { }
            }).start();
        }
    }

    private static byte[] generateTone(int startFreq, int endFreq, int durationMs,
                                       float volume, boolean distort) {
        int    samples = SAMPLE_RATE * durationMs / 1000;
        byte[] buf     = new byte[samples * 2];

        for (int i = 0; i < samples; i++) {
            double t     = (double) i / SAMPLE_RATE;
            double freq  = startFreq + (endFreq - startFreq) * ((double) i / samples);
            double angle = 2 * Math.PI * freq * t;
            double wave  = Math.sin(angle);

            if (distort) {
                // Sawtooth-like distortion for bomb
                wave = (wave > 0 ? 1 : -1) * Math.abs(wave);
                wave = Math.tanh(wave * 3) * 0.7;
            }

            // Envelope: linear fade-out
            double env   = 1.0 - (double) i / samples;
            short  value = (short)(wave * env * volume * Short.MAX_VALUE);
            buf[i * 2]     = (byte)(value & 0xFF);
            buf[i * 2 + 1] = (byte)((value >> 8) & 0xFF);
        }
        return buf;
    }

    private static void playBuffer(byte[] buf, int durationMs) throws Exception {
        AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
        if (!AudioSystem.isLineSupported(info)) return;

        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(fmt, buf.length);
        line.start();
        line.write(buf, 0, buf.length);
        line.drain();
        line.close();
    }
}
