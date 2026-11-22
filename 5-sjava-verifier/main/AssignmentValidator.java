package ex5.main;

/** this class responsible for validating assignment statements.
 * Check whether a value can legally be assigned to a variable
 * of a given target type, according to the s-Java rules.
 */
public class AssignmentValidator {
    /**
     * Validates an assignment of a value to a variable of a given type
     * @param targetType the declared type of the variable the user try to assign
     * @param value the right-hand side value
     * @param scope the current scope for variable lookup
     * @return true if the assignment is legal, false if not
     */
    public static boolean isValid(String targetType, String value, Scope scope) {
        //case 1: value is a literal
        if (LiteralUtils.isLiteral(value)) {
            return LiteralUtils.isValidForType(value, targetType);
        }

        //case 2: value is a variable
        VarInfo rhs = scope.lookup(value);
        if (rhs == null || !rhs.isInitialized()) return false;

        return TypeUtils.isCompatible(targetType, rhs.getType());
    }
}
