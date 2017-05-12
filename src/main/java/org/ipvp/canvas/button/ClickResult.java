package org.ipvp.canvas.button;

/**
 * Represents the result of a player click in a Menu
 */
public class ClickResult {

    public enum Type {
        /**
         * The click was successfully handled  
         */
        SUCCESS,
        /**
         * The click failed because an unexpected condition occured
         */
        ERROR,
        /**
         * The click failed for an expected reason
         */
        FAILED
    }
    
    private final Type result;
    private final String message;
    
    public ClickResult() {
        this(Type.FAILED);
    }
    
    public ClickResult(Type result) {
        this(result, null);
    }
    
    public ClickResult(Type result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * Returns the result type of the click
     * 
     * @return The click result status
     */
    public Type getResult() {
        return result;
    }

    /**
     * Returns a message as a result of the click being handled
     * 
     * @return The message for a player to receive as a result of the click 
     */
    public String getMessage() {
        return message;
    }
}
