package phase1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class MyQueryParser {
    private static final char PREFIX = '.';
    private static final char AUTHORS = 'A';
    private static final char SOURCE = 'N';
    private static final char QUERY = 'W';
    private static final char ID = 'I';


    private BufferedReader reader;
    private List<MyQuery> queries;

    public MyQueryParser(String file) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(new File(file)));
        this.queries = new LinkedList<MyQuery>();
    }

    public List<MyQuery> getQueries() {
        return queries;
    }

    public void parse() throws Exception {
        //reads and process the queries
        MyQuery query = null;
        String line = "";
        char state = 0;

        while ((line = reader.readLine()) != null) {
            if ((line = line.trim()).isEmpty()) {
                continue;
            }
            if (line.charAt(0) == PREFIX) {
                state = line.charAt(1);
                if (state == ID) {
                    if (query != null) {
                        queries.add(query);
                    }
                    query = new MyQuery();
                    query.setId(Integer.parseInt(line.substring(2).trim()));
                }
            } else {
                switch (state) {
                    case AUTHORS:
                        query.addAuthor(line);
                        break;
                    case QUERY:
                        query.addQuery(line);
                        break;
                    case SOURCE:
                        query.addSource(line);
                        break;
                    /* Fields.ID and no state should never happen */
                    case ID:
                    default:
                }
            }
        }
        queries.add(query);
        reader.close();
    }
}
