package nl.jacbeekers.testautomation.fitnesse.supporting;

public class MessageStructure {

    private String key;
    public String code;
    public String severity;
    public String message;

    public MessageStructure(String key, String code, String severity, String message) {
        // Constructor
        this.key=key;
        this.code=code;
        this.severity=severity;
        this.message=message;
    }

}
