package nl.consag.testautomation.supporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InformaticaFixtureMessages {
    
    private static final Map<String, MessageStructure> msgList = createMap();
    
    private static Map<String, MessageStructure> createMap() {
        HashMap<String, MessageStructure> result = new HashMap<String, MessageStructure>();
        result.put("INFAPWC0001" + Constants.ENGLISH, new MessageStructure("INFAPWC0001" + Constants.ENGLISH,"INFAPWC","0001", Constants.FATAL,
                                        Constants.ENGLISH,"Could not establish connection to PowerCenter."));
        result.put("INFAPWC0001" + Constants.DUTCH, new MessageStructure("INFAPWC0001" + Constants.DUTCH,"INFAPWC","0001", Constants.FATAL,
                                        Constants.DUTCH,"Kon geen verbinding met PowerCenter maken."));
        
        
        return Collections.unmodifiableMap(result);
    }

public String getMessage(String code, String lang) {

    MessageStructure val = msgList.get(code+lang);
    if(val==null) 
        return Constants.NOT_FOUND;
    else
        return val.message;
}

public int getNrMessages() {
    return msgList.size();
}

public List<List<String>> getAllMessages() {
    
    List<List<String>> s = new ArrayList<List<String>>();
    
    for (Map.Entry<String, MessageStructure> entry : msgList.entrySet()) {
        List<String> r = new ArrayList<String>();
        String key = entry.getKey();
        MessageStructure msg = entry.getValue();
        r.add(key);
        r.add(msg.message);
        s.add(r);
    }
    return s;
}

}
