package phase1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class Application {

    //input files
    private String docsFile = "docs//cacm.all";
    private String queryFile = "docs//query.text";

    //output files
    private String indexLocation = ("index");
    private String resultsLocation = ("results");

    //types and fields
    private final String searchField = "title";

    public Application() throws Exception {
        createDocumentIndex();
        searcher(20);
        searcher(30);
        searcher(50);
    }

    private void createDocumentIndex() throws Exception {
        Directory dir = FSDirectory.open(Paths.get(indexLocation));
        // define which analyzer to use for the normalization of documents
        StandardAnalyzer analyzer = new StandardAnalyzer();
        // define retrieval model
        Similarity similarity = new ClassicSimilarity();
        // configure IndexWriter
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setSimilarity(similarity);

        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // create the IndexWriter with the configuration as above
        IndexWriter indexWriter = new IndexWriter(dir, iwc);

        // parse txt document using TXT parser and index it
        MyDocumentParser documentParser = new MyDocumentParser(docsFile);
        documentParser.parse();
        List<MyDocument> docs = documentParser.getDocuments();
        for (MyDocument doc : docs) {
            indexDoc(indexWriter, doc);
        }
        indexWriter.close();
    }

    private void indexDoc(IndexWriter indexWriter, MyDocument mydoc){

        try {
            // make a new, empty document
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            // create the fields of the document and add them to the document
            StoredField id= new StoredField("id", mydoc.getId());
            doc.add(id);
            for(int i = 0; i<mydoc.getAuthors().size(); i++){
                StoredField author= new StoredField("author", mydoc.getAuthors().get(i));
                doc.add(author);
            }
            StoredField date = new StoredField("date", mydoc.getDate());
            doc.add(date);
            StoredField content = new StoredField("content", mydoc.getContent());
            doc.add(content);
            for(int i = 0; i<mydoc.getKeywords().size(); i++){
                StoredField keyword= new StoredField("keyword", mydoc.getKeywords().get(i));
                doc.add(keyword);
            }
            StoredField entrydate = new StoredField("entrydate", mydoc.getEntrydate());
            doc.add(entrydate);
            TextField title = new TextField("title", mydoc.getTitle(), Field.Store.YES);
            doc.add(title);
            StoredField abstractInfo= new StoredField("abstractInfo", mydoc.getAbstractInfo());
            doc.add(abstractInfo);
            String fullSearchableText = mydoc.getId() + " " + mydoc.getAuthors() + " " + mydoc.getDate() + " " +
                    mydoc.getContent() + mydoc.getKeywords() + " " + mydoc.getEntrydate() + " " +
                    mydoc.getTitle() + " " + mydoc.getAbstractInfo() ;
            StringField reference = new StringField("reference", fullSearchableText, Field.Store.NO);


            doc.add(reference);

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + mydoc);
                indexWriter.addDocument(doc);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void searcher(int i) throws Exception {
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
        IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.
        indexSearcher.setSimilarity(new ClassicSimilarity());

        search(indexSearcher, searchField, i);
        indexReader.close();
    }

    private void search(IndexSearcher indexSearcher, String field, int num) throws Exception {
        int numberOfResults = num;
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(searchField, analyzer);
        MyQueryParser queryParser = new MyQueryParser(queryFile);
        queryParser.parse();
        List<MyQuery> queries = queryParser.getQueries();
        FileWriter myWriter;
        //for different number of results
        if(numberOfResults == 20){
            myWriter = new FileWriter("results//results1.txt");
        }else if(numberOfResults == 30){
            myWriter = new FileWriter("results//results2.txt");
        }else{
            myWriter = new FileWriter("results//results3.txt");
        }
        //for every query
        for (MyQuery query : queries) {
            Query q = parser.parse(QueryParser.escape(query.getQuery()));
            TopDocs results = indexSearcher.search(q,  numberOfResults);
            ScoreDoc[] hits = results.scoreDocs;

            String qid;
            if(query.getId() <10){
                qid = "0" + String.valueOf(query.getId());
            }else{
                qid = String.valueOf(query.getId());
            }

            //for every document
            for(int i=0; i<hits.length; i++){
                Document hitDoc = indexSearcher.doc(hits[i].doc);

                String docid;
                if(hitDoc.get("id").length() == 2){
                    docid = "00" + hitDoc.get("id");
                }else if(hitDoc.get("id").length() == 3){
                    docid = "0" + hitDoc.get("id");
                }else if(hitDoc.get("id").length() == 1){
                    docid = "000" + hitDoc.get("id");
                }else{
                    docid = hitDoc.get("id");
                }
                myWriter.write(qid + " 0 " + docid + " 0 " + hits[i].score + " STANDARD\n");
            }
        }
        myWriter.close();
    }

    public static void main(String[] args) {
        try {
            Application indexerDemo = new Application();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}