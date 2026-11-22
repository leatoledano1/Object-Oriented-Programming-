package ex5.main;

/**
 * A class for handling literal values in s-Java.
 * This class provides helper methods for identifying literals
 * and checking whether a literal value is compatible with
 * a given variable type.
 */
public class LiteralUtils {
    /**
     * Checks whether a given string represents a literal value
     * Supported literals include numeric values, boolean values,
     * character literals, and string literals
     * @param v the value to check
     * @return true if the value is a literal, false otherwise
     */
    public static boolean isLiteral(String v) {
        return v.matches(LanguageRules.DOUBLE_END_WITH_POINT)
                || v.matches(LanguageRules.DOUBLE_START_WITH_POINT)
                || v.equals(LanguageRules.TRUE) || v.equals(LanguageRules.FALSE)
                || v.matches(LanguageRules.ONE_CHAR)
                || v.matches(LanguageRules.LEGAL_STRING);
    }

    /**
     * This function checks whether a literal value is valid for a specific type
     * @param v the literal value
     * @param t the target type
     * @return true if the literal can be assigned to the given type,false otherwise
     */
    public static boolean isValidForType(String v, String t) {
        return switch (t) {
            case LanguageRules.INT -> v.matches(LanguageRules.INT_LITERAL);
            case LanguageRules.DOUBLE -> v.matches(LanguageRules.DOUBLE_END_WITH_POINT)
                            || v.matches(LanguageRules.DOUBLE_START_WITH_POINT);
            case LanguageRules.BOOLEAN -> v.equals(LanguageRules.TRUE)
                            || v.equals(LanguageRules.FALSE)
                            || isValidForType(v, LanguageRules.INT)
                            || isValidForType(v, LanguageRules.DOUBLE);
            case LanguageRules.CHAR -> v.matches(LanguageRules.ONE_CHAR);
            case LanguageRules.STRING -> v.matches(LanguageRules.LEGAL_STRING);
            default -> false;
        };
    }
}
