package lk.jiat.techmart.web.util;

import jakarta.servlet.http.HttpSession;

public final class FlashMessageUtil {
    public static final String FLASH_MESSAGE = "flashMessage";
    public static final String FLASH_TYPE = "flashType";

    private FlashMessageUtil() {
    }

    public static void success(HttpSession session, String message) {
        set(session, "success", message);
    }

    public static void info(HttpSession session, String message) {
        set(session, "info", message);
    }

    public static void warning(HttpSession session, String message) {
        set(session, "warning", message);
    }

    public static void error(HttpSession session, String message) {
        set(session, "error", message);
    }

    public static void set(HttpSession session, String type, String message) {
        if (session == null || message == null || message.trim().isEmpty()) {
            return;
        }
        session.setAttribute(FLASH_MESSAGE, message.trim());
        session.setAttribute(FLASH_TYPE, type == null ? "info" : type);
    }
}
