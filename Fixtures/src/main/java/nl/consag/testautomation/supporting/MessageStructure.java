package nl.consag.testautomation.supporting;

public class MessageStructure {

    private String key;
    public String area;
    public String code;
    public String severity;
    public String lang;
    public String message;

    public MessageStructure(String key, String area, String code, String severity, String lang, String message) {
        // Constructor
        this.key=key;
        this.area=area;
        this.code=code;
        this.severity=severity;
        this.lang=lang;
        this.message=message;
    }

}
