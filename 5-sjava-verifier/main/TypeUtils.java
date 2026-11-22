package ex5.main;

/**
 * Utility class for checking type compatibility in s-Java
 * Used to determine whether a value of one type can be
 * assigned to a variable of another type
 */
public class TypeUtils {
    /**
     * Checks whether a source type can be assigned to a target type
     * @param t the target type
     * @param s the source type
     * @return true if the assignment is allowed, false else
     */
    public static boolean isCompatible(String t, String s) {
        if (t.equals(s)) return true;
        if (t.equals(LanguageRules.DOUBLE) && s.equals(LanguageRules.INT)) return true;
        if (t.equals(LanguageRules.BOOLEAN)
                && (s.equals(LanguageRules.INT) || s.equals(LanguageRules.DOUBLE)))
            return true;
        return false;
    }
}
