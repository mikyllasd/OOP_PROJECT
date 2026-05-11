package utils;
public class MathUtils {
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    public static float pulse(int timer, float speed) {
        return (float)(0.5 + 0.5 * Math.sin(timer * speed));
    }
    public static float pulseFull(int timer, float speed, float min, float max) {
        float t = (float)(0.5 + 0.5 * Math.sin(timer * speed));
        return lerp(min, max, t);
    }
    public static float easeOut(float t) { return 1f - (1f - t) * (1f - t); }
    public static float easeIn(float t)  { return t * t; }
    private MathUtils() {}
}