package phase1;

/*import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List; */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;



public class IndexerDemo {

    public IndexerDemo() throws Exception {

        String txtfile = "docs//cacm.all"; //txt file to be parsed and indexed, it contains one document per line
        String indexLocation = ("index"); //define were to store the index

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexLocation + "'...");
            System.out.println(txtfile);

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
            List<MyDoc> docs = TXTParsing.parse(txtfile);
            for (MyDoc doc : docs) {
                indexDoc(indexWriter, doc);
            }

            indexWriter.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }

    }

    private void indexDoc(IndexWriter indexWriter, MyDoc mydoc){

        try {

            // make a new, empty document
            Document doc = new Document();

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
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);
            StoredField abstractInfo= new StoredField("abstractInfo", mydoc.getAbstractInfo());
            doc.add(abstractInfo);
            String fullSearchableText = mydoc.getId() + " " + mydoc.getAuthors() + " " + mydoc.getDate() + " " +
                    mydoc.getContent() + mydoc.getKeywords() + " " + mydoc.getEntrydate() + " " +
                    mydoc.getTitle() + " " + mydoc.getAbstractInfo() ;
            TextField reference = new TextField("reference", fullSearchableText, Field.Store.NO);


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

    public static void main(String[] args) {
        try {
            IndexerDemo indexerDemo = new IndexerDemo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}



