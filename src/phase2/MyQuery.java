package phase2;

import java.util.LinkedList;
import java.util.List;

public class MyQuery {

    private int id;
    private String source;
    private List<String> authors;
    private String query;

    public MyQuery() {
        this.authors = new LinkedList<String>();
        this.query = "";
        this.source = "";
        this.id = -1;
    }

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public void addQuery(String query) {
        this.query += (this.query.isEmpty() ? "" : " ") + query;
    }

    public void addSource(String source) {
        this.source += (this.source.isEmpty() ? "" : " ") + source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}