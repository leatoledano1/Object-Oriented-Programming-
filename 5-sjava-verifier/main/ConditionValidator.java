package ex5.main;

/**this class responsible for validating conditions
 * used in if and while statements in s-Java
 * A condition is considered valid if it is:
 *     A boolean literal
 *     A numeric literal
 *     A variable of type boolean, int, or double that is initialized
 */
public class ConditionValidator {
    /**validates a condition expression
     * @param cond the condition expression as a string
     * @param scope the cur rent scope for variable lookups
     * @return true if the condition is valid according to s-Java rules,
     *      false otherwise
     */
    public static boolean isValid(String cond, Scope scope) {
        cond = cond.trim();
        // Boolean literals
        if (cond.equals(LanguageRules.TRUE) || cond.equals(LanguageRules.FALSE)) return true;
        // Numeric literals (int or double)
        if (cond.matches(LanguageRules.INT_OR_DOUBLE_LITERAL)) return true;

        // Variable conditions
        VarInfo v = scope.lookup(cond);
        return v != null && v.isInitialized() &&
                (v.getType().equals(LanguageRules.BOOLEAN) || v.getType().equals(LanguageRules.INT)
                        || v.getType().equals(LanguageRules.DOUBLE));
    }
}
