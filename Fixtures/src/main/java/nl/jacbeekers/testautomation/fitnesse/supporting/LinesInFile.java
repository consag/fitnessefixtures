package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.io.*;

public class LinesInFile {

    public int count(String fileName) {
        try{
            InputStream is = new BufferedInputStream(new FileInputStream(fileName));
            try {
                byte[] c = new byte[1024];
                int count = 0;
                int readChars = 0;
                boolean empty = true;
                while ((readChars = is.read(c)) != -1) {
                    empty = false;
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                        }
                    }
                }
                return (count == 0 && !empty) ? 1 : count;
            } finally {
                is.close();
            }
        } catch(FileNotFoundException e) {
            return -1;
        } catch(IOException e) {
            return -1;
        }

    }
}
