package com.warewise.admin.tui.ui;

public class UiConstants {
    // ANSI escape codes for colors and styles
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BOLD   = "\u001B[1m";
    public static final String ANSI_BLINK  = "\u001B[5m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_WHITE  = "\u001B[37m";
    public static final String SAVE_CURSOR = "\033[s";
    public static final String RESTORE_CURSOR = "\033[u";
    // ANSI code to clear the screen and reset cursor position
    public static final String CLEAR_SCREEN = "\033[H\033[2J";

    public static String topBorder    = "+-----------------------------+";
    public static String middleBorder = "+-----------------------------+";
    public static String bottomBorder = "+-----------------------------+";

    public static String uiHeader =
            """
                    ██╗    ██╗ █████╗ ██████╗ ███████╗██╗    ██╗██╗███████╗███████╗
                    ██║    ██║██╔══██╗██╔══██╗██╔════╝██║    ██║██║██╔════╝██╔════╝
                    ██║ █╗ ██║███████║██████╔╝█████╗  ██║ █╗ ██║██║███████╗█████╗ \s
                    ██║███╗██║██╔══██║██╔══██╗██╔══╝  ██║███╗██║██║╚════██║██╔══╝ \s
                    ╚███╔███╔╝██║  ██║██║  ██║███████╗╚███╔███╔╝██║███████║███████╗
                     ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚══╝╚══╝ ╚═╝╚══════╝╚══════╝
            """;

}
