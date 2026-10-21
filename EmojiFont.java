
import java.awt.Font;

public class EmojiFont {
    public static Font getFont(int style, int size) {
        // Try Segoe UI Emoji first (Windows), fallback to Segoe UI Symbol
        Font emojiFont = new Font("Segoe UI Emoji", style, size);
        if (!emojiFont.getFamily().equals("Segoe UI Emoji")) {
            emojiFont = new Font("Segoe UI Symbol", style, size);
        }
        return emojiFont;
    }
}