import app.msime.android.NineKeyShuangpinKeypad;
import app.msime.android.NineKeyShuangpinKeypad.Face;
import java.util.List;

/** 九键双拼的键面文字、读屏文字、方案名和表的问题提示（#6572）。键位本身来自 host-api，这里只核对怎么印、怎么说。 */
public final class NineKeyShuangpinKeypadSmoke {
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        // 米旮旯 3 的 2 键：b c 和零声母，韵母 a ai iang。
        Face two = new Face('2', List.of("b", "c"), List.of("a", "ai", "iang"), true);
        check(NineKeyShuangpinKeypad.label(two).equals("零 b c\na ai iang"), NineKeyShuangpinKeypad.label(two));
        check(NineKeyShuangpinKeypad.compactHint(two).equals("零 b c"), "touch hint omits the full final table");
        check(NineKeyShuangpinKeypad.description(two).equals("2，声母 b c 和零声母，韵母 a ai iang"),
            NineKeyShuangpinKeypad.description(two));
        // 0 键只有韵母，1 键也是：只印一行。
        Face zero = new Face('0', List.of(), List.of("u", "ü", "ei"), false);
        check(NineKeyShuangpinKeypad.label(zero).equals("u ü ei"), NineKeyShuangpinKeypad.label(zero));
        check(NineKeyShuangpinKeypad.compactHint(zero).equals("韵母"), "final-only keys use a short category hint");
        check(NineKeyShuangpinKeypad.description(zero).equals("0，韵母 u ü ei"), NineKeyShuangpinKeypad.description(zero));
        Face eight = new Face('8', List.of("t", "zh", "sh"), List.of(), false);
        check(NineKeyShuangpinKeypad.label(eight).equals("t zh sh"), "initials only");
        check(NineKeyShuangpinKeypad.compactHint(eight).equals("t zh sh"), "three initials remain visible");
        Face crowded = new Face('9', List.of("b", "p", "m", "f"), List.of("a"), false);
        check(NineKeyShuangpinKeypad.compactHint(crowded).equals("b p m f"), "touch hints preserve every initial without ellipsis");
        Face nine = new Face('9', List.of("w", "x", "y", "z"), List.of("ua", "uai", "ian", "iao"), false);
        check(NineKeyShuangpinKeypad.compactHint(nine).equals("w x y z"), "the ninth key keeps z visible");
        check(NineKeyShuangpinKeypad.face(List.of(two, zero), '0') == zero, "lookup by key");
        check(NineKeyShuangpinKeypad.face(List.of(two, zero), '1') == null, "a key the table does not use");

        check(NineKeyShuangpinKeypad.normalizedProfile(null).equals("migala3"), "default profile");
        check(NineKeyShuangpinKeypad.normalizedProfile("gushi2").equals("migala3"), "unknown profile");
        check(NineKeyShuangpinKeypad.normalizedProfile("custom").equals("custom"), "custom profile");
        check(NineKeyShuangpinKeypad.profileTitle("gushi2_low").equals("辜氏 2 低重码"), "title");
        check(NineKeyShuangpinKeypad.BUILTIN_PROFILES.equals(List.of("migala3", "migala2", "gushi2_low")), "builtins");

        check(NineKeyShuangpinKeypad.validKey("finals", "0"), "0 is a key");
        check(!NineKeyShuangpinKeypad.validKey("finals", "22"), "a final takes one digit");
        check(NineKeyShuangpinKeypad.validKey("zero_initials", "21"), "zero initial pair");
        check(!NineKeyShuangpinKeypad.validKey("zero_initials", "2"), "zero initial takes two digits");
        check(!NineKeyShuangpinKeypad.validKey("initials", "a"), "letters are not keys");

        check(NineKeyShuangpinKeypad.unitLabel("ve").equals("üe"), "umlaut");
        check(NineKeyShuangpinKeypad.problemMessage("missing_unit", "finals", "iong", null)
            .equals("韵母「iong」还没有放到数字键上"), "missing");
        check(NineKeyShuangpinKeypad.problemMessage("invalid_key", "zero_initials", "a", "2")
            .equals("零声母「a」的键「2」不对：零声母要两位数字"), "invalid key");
        check(NineKeyShuangpinKeypad.problemMessage("duplicate_unit", "initials", "zh", null)
            .equals("声母「zh」写了两次"), "duplicate");

        check(NineKeyShuangpinKeypad.normalizedHintMode(null).equals("compact"), "default hint mode");
        check(NineKeyShuangpinKeypad.normalizedHintMode("unknown").equals("compact"), "unknown hint mode");
        check(NineKeyShuangpinKeypad.normalizedHintMode("both").equals("both"), "both hint mode");
        check(NineKeyShuangpinKeypad.normalizedHintMode("dynamic").equals("dynamic"), "dynamic hint mode");
        check(NineKeyShuangpinKeypad.hint(two, "compact", true).equals("零 b c"), "compact ignores expectingFinal");
        check(NineKeyShuangpinKeypad.hint(two, "both", false).equals("零 b c\na ai iang"), "both shows initials and finals");
        check(NineKeyShuangpinKeypad.hint(zero, "both", false).equals("u ü ei"), "both on final-only key");
        check(NineKeyShuangpinKeypad.hint(two, "dynamic", false).equals("零 b c"), "dynamic initial phase");
        check(NineKeyShuangpinKeypad.hint(two, "dynamic", true).equals("a ai iang"), "dynamic final phase");
        check(NineKeyShuangpinKeypad.hint(zero, "dynamic", false).equals("韵母"), "dynamic initial phase on final-only key");
        check(NineKeyShuangpinKeypad.hint(zero, "dynamic", true).equals("u ü ei"), "dynamic final phase on final-only key");
        check(NineKeyShuangpinKeypad.hint(eight, "dynamic", true).equals(""), "dynamic final phase on initial-only key");

        check(!NineKeyShuangpinKeypad.expectingFinal("", "", 0), "idle expects initial");
        check(NineKeyShuangpinKeypad.expectingFinal("2", "2", 1), "single digit expects final");
        check(!NineKeyShuangpinKeypad.expectingFinal("23", "23", 2), "two digits expect next initial");
        check(NineKeyShuangpinKeypad.expectingFinal("236", "236", 3), "three digits expect final");
        check(!NineKeyShuangpinKeypad.expectingFinal("8'", "8", 1), "trailing separator expects initial");
        check(NineKeyShuangpinKeypad.expectingFinal("8'4", "84", 2), "digit after separator expects final");
        check(NineKeyShuangpinKeypad.expectingFinal("ni'6", "646", 3), "locked syllable plus one digit expects final");
        check(!NineKeyShuangpinKeypad.expectingFinal("ni'64", "6464", 4), "locked syllable plus two digits expects initial");
        check(!NineKeyShuangpinKeypad.expectingFinal("e", "23", 2), "all locked expects initial");
        check(NineKeyShuangpinKeypad.expectingFinal("8'45", "845", 2), "caret after 4 in 8'45 expects final");
        check(!NineKeyShuangpinKeypad.expectingFinal("2345", "2345", 0), "caret at start expects initial");
        System.out.println("NineKeyShuangpinKeypadSmoke ok");
    }
}
