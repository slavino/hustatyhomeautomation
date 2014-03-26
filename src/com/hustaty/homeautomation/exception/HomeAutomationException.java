package com.hustaty.homeautomation.exception;

/**
 * User: hustasl
 * Date: 2/22/13
 * Time: 1:18 PM
 */
public class HomeAutomationException extends Exception {

    private static final long serialVersionUID = 1L;

    public HomeAutomationException() {
        super();
    }

    public HomeAutomationException(String message) {
        super(message);
    }

    public HomeAutomationException(Throwable cause) {
        super(cause);
    }

    public HomeAutomationException(String message, Throwable cause) {
        super(message, cause);
    }
}
