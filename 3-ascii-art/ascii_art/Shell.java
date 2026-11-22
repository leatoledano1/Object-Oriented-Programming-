package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import exceptions.BadCommandException;
import exceptions.BadFormatException;
import exceptions.ResolutionException;
import image.Image;
import image.PadImage;
import image_char_matching.SubImgCharMatcher;

/**
 * the Shell class.
 * reads user commands, validate them,
 * runs the ascii-art program and print errors only here.
 * All other methods only throw exceptions and do not print.
 */
public class Shell {
    private static final int DEFAULT_RES = 2;
    private static final int ADD_INDEX = 4;
    private static final int REMOVE_INDEX = 7;
    private static final int EMPTY_RES = 3;
    private static final int OUTPUT_LEN = 6;
    private static final int ASCII_ART_LEN = 8;
    private static final int LOWEST_CHAR = 32;
    private static final int HIGHEST_CHAR = 126;
    private static final int MIN_CHARS  = 2;
    private static final String START = ">>> ";

    private static final String SPACE = " ";
    private static final String CMD_EXIT = "exit";
    private static final String CMD_CHARS = "chars";
    private static final String CMD_ADD = "add";
    private static final String CMD_REMOVE = "remove";
    private static final String CMD_RES = "res";
    private static final String CMD_REVERSE = "reverse";
    private static final String CMD_OUTPUT = "output";
    private static final String CMD_ASCIIART = "asciiArt";

    private static final String ARG_ALL = "all";
    private static final String ARG_SPACE = "space";
    private static final String ARG_UP = "up";
    private static final String ARG_DOWN = "down";
    private static final String ARG_CONSOLE = "console";
    private static final String ARG_HTML = "html";
    private static final String REV_ON = "ON.";
    private static final String REV_OFF = "OFF.";

    private static final String MSG_ERROR_LOAD = "ERROR: could not load image \"";
    private static final String MSG_DIDNT_EXEC = "Did not execute due to incorrect command.";
    private static final String MSG_ADD_BAD = "Did not add due to incorrect format.";
    private static final String MSG_REMOVE_BAD = "Did not remove due to incorrect format.";
    private static final String MSG_RES_BAD = "Did not change resolution due to incorrect format.";
    private static final String MSG_RES_BOUND = "Did not change resolution due to exceeding boundaries.";
    private static final String MSG_RES_SET = "Resolution set to ";
    private static final String MSG_REV = "Reverse mode is now ";
    private static final String MSG_OUTPUT_BAD = "Did not change output method due to incorrect format.";
    private static final String MSG_ASCII_TOO_SMALL = "Did not execute. Charset is too small.";


    private final SubImgCharMatcher matcher;
    private int resolution = DEFAULT_RES;
    private Image paddedImage;

    private boolean reverseMode = false;

    private enum OutputMode { CONSOLE, HTML }
    private OutputMode outputMode = OutputMode.CONSOLE;


    /**
     * constructor of the shell.
     * start with default matcher using digits only.
     */
    public Shell() {
        this.matcher = new SubImgCharMatcher("0123456789".toCharArray());
    }


    /**
     * main loop
     * read commands, try to execute them,
     * catch exceptions and print their messages.
     * @param imageName name of the image file to load
     */
    public void run(String imageName) {
        try {
            Image original = new Image(imageName);
            this.paddedImage = PadImage.padToPowerOfTwo(original);
        } catch (Exception e) {
            System.out.println(MSG_ERROR_LOAD + imageName + "\"");
            return;
        }

        while (true) {
            System.out.print(START);
            String input = KeyboardInput.readLine().trim();

            try {

                if (input.equals(CMD_EXIT) || input.startsWith(CMD_EXIT + SPACE)) {
                    return;
                }

                if (input.equals(CMD_CHARS) || input.startsWith(CMD_CHARS + SPACE)) {
                    printChars();
                    continue;
                }

                if (input.equals(CMD_ADD) || input.startsWith(CMD_ADD + SPACE)) {
                    handleAdd(input.substring(ADD_INDEX).trim());
                    continue;
                }

                if (input.equals(CMD_REMOVE) || input.startsWith(CMD_REMOVE + SPACE)) {
                    handleRemove(input.substring(REMOVE_INDEX).trim());
                    continue;
                }

                if (input.startsWith(CMD_RES)) {
                    String arg = input.length() == EMPTY_RES? "" :
                            input.substring(EMPTY_RES + 1 ).trim();
                    handleRes(arg);
                    continue;
                }

                if (input.equals(CMD_REVERSE) || input.startsWith(CMD_REVERSE + SPACE)) {
                    reverseMode = !reverseMode;
                    System.out.println(MSG_REV +
                            (reverseMode ? REV_ON: REV_OFF));
                    continue;
                }

                if (input.equals(CMD_OUTPUT) || input.startsWith(CMD_OUTPUT + SPACE)) {
                    String arg = input.length() == OUTPUT_LEN ? ""
                            : input.substring(OUTPUT_LEN +1).trim();
                    handleOutput(arg);
                    continue;
                }

                if (input.equals(CMD_ASCIIART) || input.startsWith(CMD_ASCIIART + SPACE)) {
                    String arg = input.length() == ASCII_ART_LEN? ""
                            : input.substring(ASCII_ART_LEN + 1).trim();
                    handleAsciiArt(arg);
                    continue;
                }

                throw new BadCommandException(MSG_DIDNT_EXEC);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * print all current chars in matcher.
     * Never throw.
     */
    private void printChars() {
        for (char c : matcher.getCharSet()) {
            System.out.print(c + SPACE);
        }
        System.out.println();
    }


    /**handle "add" command.
     * @param arg argument after the "add" command
     * @throws BadFormatException if the command format is wrong or charset too small
     */
    private void handleAdd(String arg) throws BadFormatException {

        if (arg.equals(ARG_ALL) || arg.startsWith(ARG_ALL + SPACE)) {
            for (char c = LOWEST_CHAR; c <= HIGHEST_CHAR; c++) matcher.addChar(c);
            return;
        }

        if (arg.equals(ARG_SPACE) || arg.startsWith(ARG_SPACE + SPACE)) {
            matcher.addChar(' ');
            return;
        }

        if (arg.length() == 1) {
            char c = arg.charAt(0);
            if (c < LOWEST_CHAR || c > HIGHEST_CHAR)
                throw new BadFormatException(MSG_ADD_BAD);
            matcher.addChar(c);
            return;
        }

        if (arg.length() == 3 && arg.charAt(1) == '-') {
            char start = arg.charAt(0);
            char end = arg.charAt(2);

            if (start < LOWEST_CHAR || end > HIGHEST_CHAR)
                throw new BadFormatException(MSG_ADD_BAD);

            if (start > end) { char t = start; start = end; end = t; }

            for (char c = start; c <= end; c++) matcher.addChar(c);
            return;
        }

        throw new BadFormatException(MSG_ADD_BAD);
    }


    /**removes one char.
     * @param arg argument after the "remove" commands
     * @throws BadFormatException if the command format is wrong
     */
    private void handleRemove(String arg) throws BadFormatException {
        if (arg.length() != 1)
            throw new BadFormatException(MSG_REMOVE_BAD);
        matcher.removeChar(arg.charAt(0));
    }


    /**
     * handles resolution change.
     * @param arg argument after the "res" commands (up or down )
     * @throws BadFormatException if the command format is wrong
     * @throws ResolutionException if boundaries violated
     */
    private void handleRes(String arg)
            throws ResolutionException, BadFormatException {

        if (arg.isEmpty()) {
            System.out.println(MSG_RES_SET + resolution + ".");
            return;
        }

        if ((!arg.equals(ARG_UP) && !arg.startsWith(ARG_UP + SPACE))
                && !arg.equals(ARG_DOWN) && !arg.startsWith(ARG_DOWN + SPACE))
            throw new BadFormatException(MSG_RES_BAD);

        int newRes = resolution;
        if (arg.equals(ARG_UP) || arg.startsWith(ARG_UP + SPACE)) newRes *= 2;
        else newRes /= 2;

        int min = Math.max(1, paddedImage.getWidth() / paddedImage.getHeight());
        int max = paddedImage.getWidth();

        if (newRes < min || newRes > max)
            throw new ResolutionException(MSG_RES_BOUND);

        resolution = newRes;
        System.out.println(MSG_RES_SET + resolution + ".");
    }


    /**
     * change output mode
     * @param arg argument after the "output" commands
     * @throws BadFormatException if the command format is wrong
     */
    private void handleOutput(String arg) throws BadFormatException {
        if (arg.equals(ARG_CONSOLE) || arg.startsWith(ARG_CONSOLE + SPACE)) {
            outputMode = OutputMode.CONSOLE;
            return;
        }
        if (arg.equals(ARG_HTML) || arg.startsWith(ARG_HTML + SPACE)) {
            outputMode = OutputMode.HTML;
            return;
        }
        throw new BadFormatException(MSG_OUTPUT_BAD);
    }

    /**handles the asciiArt command.
     * Runs the ASCII-art algorithm and sends the output to the correct
     * output method using the AsciiOutput interface we were given
     * this method does not print anything itself, all exceptions are handled
     * by the main shell loop
     *
     * @param arg the argument after the command (must be empty)
     * @throws BadCommandException if the command format is wrong or charset too small
     */

    private void handleAsciiArt(String arg) throws BadCommandException {
        if (!arg.isEmpty())
            throw new BadCommandException(MSG_DIDNT_EXEC);

        if (matcher.getCharSet().size() < MIN_CHARS)
            throw new BadCommandException(MSG_ASCII_TOO_SMALL);

        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(paddedImage, resolution, matcher, reverseMode);
        char[][] output = algo.run();

        AsciiOutput outMethod;
        if (outputMode == OutputMode.CONSOLE) {
            outMethod = new ConsoleAsciiOutput();
        } else {
            outMethod = new HtmlAsciiOutput("out.html", "Courier New");
        }

        outMethod.out(output);
    }

    /**
     * the main method only calls the Shell run method.
     * @param args command-line arguments containing the image name
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage of this Program : java ascii_art.Shell <imageName>");
            return;
        }
        new Shell().run(args[0]);
    }
}