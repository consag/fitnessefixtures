package nl.jacbeekers.testautomation.fitnesse.supporting;

public class InfaToolingStructure {

    private String key;
    public String function;
    public String tool;
    public String toolOption;

    public InfaToolingStructure(String key, String tool, String toolOption) {
        // Constructor
        this.key=key;
        this.tool=tool;
        this.toolOption=toolOption;
    }

}
