package phase1;

import java.util.LinkedList;
import java.util.List;

public class MyDocument {

    private List<String> authors;
    private String date;
    private String content;
    private int id;
    private List<String> keywords;
    private String entrydate;
    private String title;
    private String abstractInfo;
    private List<String> references;

    public MyDocument() {
        this.authors = new LinkedList<String>();
        this.references = new LinkedList<String>();
        this.keywords = new LinkedList<String>();
        this.abstractInfo = "";
        this.entrydate = "";
        this.content = "";
        this.title = "";
        this.date = "";
        this.id = -1;
    }


    public void addKeywords(String keyword) {
        this.keywords.add(keyword);
    }

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public void addDate(String date) {
        this.date += (this.date.isEmpty() ? "" : " ") + date;
    }

    public void addContent(String content) {
        this.content += (this.content.isEmpty() ? "" : " ") + content;
    }

    public void addEntrydate(String entrydate) {
        this.entrydate += (this.entrydate.isEmpty() ? "" : " ") + entrydate;
    }

    public void addTitle(String title) {
        this.title += (this.title.isEmpty() ? "" : " ") + title;
    }

    public void addAbstractInfo(String abstractTxt) {
        this.abstractInfo += (this.abstractInfo.isEmpty() ? "" : " ") + abstractTxt;
    }

    public void addReference(String reference) {
        this.references.add(reference);
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getEntrydate() {
        return entrydate;
    }

    public void setEntryDate(String entrydate) {
        this.entrydate = entrydate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractInfo() {
        return abstractInfo;
    }

    public void setAbstractInfo(String abstractInfo) {
        this.abstractInfo = abstractInfo;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }
}
