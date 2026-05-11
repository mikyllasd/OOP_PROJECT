package utils;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class FontManager {
    private static Set<String> availableFonts;
    private static final String EMOJI_PRIMARY   = "Segoe UI Emoji";
    private static final String EMOJI_FALLBACK1 = "Apple Color Emoji";
    private static final String EMOJI_FALLBACK2 = "Noto Color Emoji";
    private static final String BOLD_PRIMARY    = "Arial Black";
    private static final String BOLD_FALLBACK   = "DejaVu Sans Bold";
    private static final String BODY_PRIMARY    = "Arial";
    private static final String BODY_FALLBACK   = "SansSerif";
    static {
        availableFonts = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                               .getAvailableFontFamilyNames()
        ));
    }
    public static Font getEmoji(int size) {
        if (availableFonts.contains(EMOJI_PRIMARY))
            return new Font(EMOJI_PRIMARY, Font.PLAIN, size);
        if (availableFonts.contains(EMOJI_FALLBACK1))
            return new Font(EMOJI_FALLBACK1, Font.PLAIN, size);
        if (availableFonts.contains(EMOJI_FALLBACK2))
            return new Font(EMOJI_FALLBACK2, Font.PLAIN, size);
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }
    public static Font getBold(int size) {
        if (availableFonts.contains(BOLD_PRIMARY))
            return new Font(BOLD_PRIMARY, Font.PLAIN, size);
        if (availableFonts.contains(BOLD_FALLBACK))
            return new Font(BOLD_FALLBACK, Font.BOLD, size);
        return new Font(Font.SANS_SERIF, Font.BOLD, size);
    }
    public static Font getBody(int size, int style) {
        if (availableFonts.contains(BODY_PRIMARY))
            return new Font(BODY_PRIMARY, style, size);
        return new Font(BODY_FALLBACK, style, size);
    }
    public static Font getBody(int size)     { return getBody(size, Font.PLAIN); }
    public static Font getBodyBold(int size) { return getBody(size, Font.BOLD); }
    private FontManager() {}
}