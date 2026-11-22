package ex5.main;

/** A class for the constants
 * regex, types and others
 */
public class LanguageRules {

    /** keyword for int type */
    public static final String INT = "int";

    /** keyword for double type */
    public static final String DOUBLE = "double";

    /** keyword for boolean type */
    public static final String BOOLEAN = "boolean";

    /** keyword for string type */
    public static final String STRING = "String";

    /** keyword for char type */
    public static final String CHAR = "char";

    /** boolean literal true */
    public static final String TRUE = "true";

    /** boolean literal false */
    public static final String FALSE = "false";

    /** regex for a legal variable or method name */
    public static final String VAR_NAME =
            "(?:[A-Za-z][A-Za-z0-9_]*|_[A-Za-z0-9][A-Za-z0-9_]*)";

    /** regex for integer literal */
    public static final String INT_LITERAL =
            "[+-]?\\d+";

    /** regex for integer or double literal with optional decimal part */
    public  static final String INT_OR_DOUBLE_LITERAL =
            "[+-]?\\d+(\\.\\d+)?";

    /** regex for double literal ending with a point */
    public static final String DOUBLE_END_WITH_POINT =
            "[+-]?\\d+(\\.\\d*)?";

    /** regex for double literal starting with a point */
    public static final String DOUBLE_START_WITH_POINT =
            "[+-]?\\d*\\.\\d+";

    /** regex for a single character literal */
    public static final String ONE_CHAR = "'.'";

    /** regex for a legal string literal */
    public static final String LEGAL_STRING =  "\".*\"";
}
