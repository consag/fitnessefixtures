package nl.jacbeekers.testautomation.fitnesse.supporting;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static nl.jacbeekers.testautomation.fitnesse.supporting.Constants.NOT_FOUND;
import static nl.jacbeekers.testautomation.fitnesse.supporting.ResultMessages.ERRMSG_NOTFOUND;

public class InfaCmdTools {

    public static final String INFA_FUNCTION_RUNMAPPING ="runMapping";
    public static final String INFA_FUNCTION_RUNPROFILE ="runProfile";

    private static final Map<String, InfaToolingStructure> toolList = createMap();
    private static Map<String, InfaToolingStructure> createMap() {
        HashMap<String, InfaToolingStructure> result = new HashMap<String, InfaToolingStructure>();

        result.put(INFA_FUNCTION_RUNMAPPING,
                new InfaToolingStructure(INFA_FUNCTION_RUNMAPPING, "ms", "runMapping", "-Wait true"));
        result.put(INFA_FUNCTION_RUNPROFILE,
                new InfaToolingStructure(INFA_FUNCTION_RUNPROFILE, "ps", "executeProfile", "-w true"));

        return Collections.unmodifiableMap(result);
    }

    public static String getTool(String function) {

        InfaToolingStructure val = toolList.get(function);
        if(val==null)
            return ERRMSG_NOTFOUND + " infacmd Tool for Function >" +function +"<.";
        else
            return val.tool;
    }

    public static String getToolOption(String function) {

        InfaToolingStructure val = toolList.get(function);
        if(val==null)
            return ERRMSG_NOTFOUND + " infacmd ToolOption for Function >" +function +"<.";
        else
            return val.toolOption;
    }

    public static String getWaitOption(String function) {

        InfaToolingStructure val = toolList.get(function);
        if(val==null)
            return NOT_FOUND ;
        else
            return val.waitOption;
    }

}
