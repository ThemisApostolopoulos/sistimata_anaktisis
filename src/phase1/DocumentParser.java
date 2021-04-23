package phase1;

import java.io.FileNotFoundException;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

public class DocumentParser {

    private static final char PREFIX = '.';
    private static final char AUTHORS = 'A';
    private static final char DATE = 'B';
    private static final char CONTENT = 'C';
    private static final char ID = 'I';
    private static final char KEYWORDS = 'K';
    private static final char ENTRYDATE = 'N';
    private static final char TITLE = 'T';
    private static final char ABSTRACT = 'W';
    private static final char REFERENCE = 'X';

    private BufferedReader reader;
    private List<Document> documents;

    public DocumentParser(String file) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(new File(file)));
        this.documents = new LinkedList<Document>();
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void parse() throws Exception {

        Document document = null;
        String line = "";
        char state = 0;
        while ((line = reader.readLine()) != null) {
            if ((line = line.trim()).isEmpty()) {
                continue;
            }
            //when you find a line that starts with . (this line is attribute)
            if (line.charAt(0) == '.') {
                state = line.charAt(1);
                if (state == 'I') {
                    if (document != null) {
                       documents.add(document);
                    }
                    document = new Document();
                    document.setId(Integer.parseInt(line.substring(2).trim()));
                }
            } else {
                switch (state) {
                    case AUTHORS:
                       document.addAuthor(line);
                        break;
                    case DATE:
                        document.addDate(line);
                        break;
                    case CONTENT:
                        document.addContent(line);
                        break;
                    case KEYWORDS:
                        for (String keyword : line.split(",")) {
                            document.addKeywords(keyword.trim());
                        }
                        break;
                    case ENTRYDATE:
                        document.addEntrydate(line);
                        break;
                    case TITLE:
                        document.addTitle(line);
                        break;
                    case ABSTRACT:
                        document.addAbstractInfo(line);
                        break;
                    case REFERENCE:
                        document.addReference(line);
                        break;

                    case ID:
                    default:
                    }
                }
            }
            documents.add(document);
            reader.close();
    }
}