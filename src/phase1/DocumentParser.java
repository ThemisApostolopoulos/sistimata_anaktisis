package phase1;

import java.io.FileNotFoundException;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

public class DocumentParser {
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
        //reads and process the documents
        try {
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
            documents.add(document);
            reader.close();

        }catch (Throwable err) {
            err.printStackTrace();
        }
    }
}