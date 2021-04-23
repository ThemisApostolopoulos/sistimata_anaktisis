package phase1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class TXTParsing {

    public static List<MyDoc> parse(String file) throws Exception {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));

            MyDoc document = null;
            List<MyDoc> documents = new LinkedList<MyDoc>();

            String line = "";
            char state = 0;
            while ((line = reader.readLine()) != null) {
                if ((line = line.trim()).isEmpty()) {
                    continue;
                }

                if (line.charAt(0) == '.') {
                    state = line.charAt(1);
                    if (state == 'I') {
                        if (document != null) {
                            documents.add(document);
                        }
                        document = new MyDoc();
                        document.setId(Integer.parseInt(line.substring(2).trim()));
                    }
                } else {
                    switch (state) {
                        case 'A':
                            document.addAuthor(line);
                            break;
                        case 'D':
                            document.addDate(line);
                            break;
                        case 'C':
                            document.addContent(line);
                            break;
                        case 'K':
                            for (String keyword : line.split(",")) {
                                document.addKeywords(keyword.trim());
                            }
                            break;
                        case 'N':
                            document.addEntrydate(line);
                            break;
                        case 'T':
                            document.addTitle(line);
                            break;
                        case 'W':
                            document.addAbstractInfo(line);
                            break;
                        case 'X':
                            document.addReference(line);
                            break;

                        case 'I':
                        default:
                    }
                }
            }

            return documents;

        }catch (Throwable err) {
            err.printStackTrace();
            return null;
        }
    }
}