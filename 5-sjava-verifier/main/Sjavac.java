package ex5.main;

import java.io.File;

/**main file point for the s-Java compiler
 *responsible for handling command-line arguments,
 *running the verifier, and printing the correct exit code
 */
public class Sjavac {
    private static final int LEGAL_ARGS_NUM  =1;
    /**
     * Program entry point
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length !=LEGAL_ARGS_NUM) {
                throw new SJavaExceptions(ErrorType.IO_ERROR.getMessage());
            }

            File file = new File(args[0]);
            if (!file.exists() || !file.canRead()) {
                throw new SJavaExceptions(ErrorType.IO_ERROR.getMessage());
            }

            Verifier verifier = new Verifier();
            ErrorType result = verifier.verify(file);

            if (result.getMessage() != null) {
                System.err.println(result.getMessage());
            }

            System.out.println(result.getExitCode());

        } catch (SJavaExceptions e) {
            System.err.println(e.getMessage());
            System.out.println(ErrorType.IO_ERROR.getExitCode());
        }
    }
}
