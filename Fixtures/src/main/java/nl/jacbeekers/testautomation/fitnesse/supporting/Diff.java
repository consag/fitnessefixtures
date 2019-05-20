package nl.jacbeekers.testautomation.fitnesse.supporting;

/*   
* Copyright 2004 The Apache Software Foundation
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/
//based on Apache technology http://www.apache.org
//Based on http://www.java2s.com/Tutorial/Java/0180__File/Comparetextfilelinebyline.htm
//slightly modified by Consag Consultancy Services B.V.

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;


public class Diff
{
    public static void readersAsText(String name1, String name2,
        List<String> diffs)
        throws IOException
    {
        Reader r1 = new FileReader(name1);
        Reader r2 = new FileReader(name2);
        LineNumberReader reader1 = new LineNumberReader(r1);
        LineNumberReader reader2 = new LineNumberReader(r2);
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();
        while (line1 != null && line2 != null)
        {
            if (!line1.equals(line2))
            {
                diffs.add("line " + reader1.getLineNumber() +
                    ":" + "1=>" + line1 + "< 2=>" + line2 +"<.");
                break;
            }
            line1 = reader1.readLine();
            line2 = reader2.readLine();
        }
        if (line1 == null && line2 != null)
            diffs.add("#2 has extra lines at line " +
                reader2.getLineNumber() + ": >" + line2 +"<.");
        if (line1 != null && line2 == null)
            diffs.add("#1 has extra lines at line " +
                reader1.getLineNumber() + ": >" + line1 +"<.");
    }
}
