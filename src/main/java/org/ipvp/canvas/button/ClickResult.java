package org.ipvp.canvas.button;

public class ClickResult {
    
    public enum ResultType {
        SUCCESS,
        FAILED
    }
    
    private final ResultType result;
    private final String message;
    
    public ClickResult() {
        this(ResultType.FAILED);
    }
    
    public ClickResult(ResultType result) {
        this(result, null);
    }
    
    public ClickResult(ResultType result, String message) {
        this.result = result;
        this.message = message;
    }
    
    public ResultType getResult() {
        return result;
    }
    
    public String getMessage() {
        return message;
    }
}
