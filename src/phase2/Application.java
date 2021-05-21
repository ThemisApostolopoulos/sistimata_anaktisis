package phase2;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.classification.utils.DocToDoubleVectorUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import phase1.MyDocument;
import phase1.MyDocumentParser;
import phase1.MyQuery;
import phase1.MyQueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class Application {

    //input files
    private String docsFile = "docs//cacm.all";
    private String queryFile = "docs//query.text";

    //output files
    private String indexLocation = ("index2");


    public Application() throws Exception {
        //createDocumentIndex();
        createQueriesIndex();
    }

    private void createQueriesIndex() throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new ClassicSimilarity();
        Directory index = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setSimilarity(similarity);


        // iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // create the IndexWriter with the configuration as above
        IndexWriter indexWriter = new IndexWriter(index, iwc);
        MyQueryParser queryParser = new MyQueryParser(queryFile);
        queryParser.parse();
        List<MyQuery> queries = queryParser.getQueries();
        for (MyQuery query : queries) {
            indexQuery(indexWriter, query);
        }
        indexWriter.close();
        IndexReader reader = DirectoryReader.open(index);
        testSparseFreqDoubleArrayConversionQuery(reader);
        reader.close();

    }



    private void indexQuery(IndexWriter indexWriter, MyQuery query) {
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setTokenized(true);
        type.setStored(true);
        type.setStoreTermVectors(true);

        try {
            // make a new, empty document
            Document q = new Document();
            // create the fields of the document and add them to the document

            Field queryField = new Field("queryField", query.getQuery(), type);
            q.add(queryField);

            indexWriter.addDocument(q);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void testSparseFreqDoubleArrayConversionQuery(IndexReader reader) throws IOException {
        Terms fieldTerms = MultiFields.getTerms(reader, "queryField");   //the number of terms in the lexicon after analysis of the Field "title"
        System.out.println("Terms:" + fieldTerms.size());

        TermsEnum it = fieldTerms.iterator();						//iterates through the terms of the lexicon
        while(it.next() != null) {
            System.out.print(it.term().utf8ToString() + " "); 		//prints the terms
        }
        System.out.println();
        System.out.println();
        if (fieldTerms != null && fieldTerms.size() != -1) {
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            FileWriter myWriter;
            myWriter = new FileWriter("results2//queryResults.txt");
            for (ScoreDoc scoreQuery : indexSearcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE).scoreDocs) {   //retrieves all documents
                System.out.println("QueryID: " + scoreQuery.doc);
                Terms docTerms = reader.getTermVector(scoreQuery.doc, "queryField");

                Double[] vector = DocToDoubleVectorUtils.toSparseLocalFreqDoubleArray(docTerms, fieldTerms); //creates document's vector
                if(vector == null){
                    System.err.println("Document " + scoreQuery.doc + " had a null terms vector for body");
                }else{
                    NumberFormat nf = new DecimalFormat("0.#");
                    System.out.println(vector.length-1 + "\n");
                    for(int i = 0; i<=vector.length-1; i++ ) {
                        myWriter.write(nf.format(vector[i])+ " ");
                    }
                    myWriter.write("\n");
                }

            }
        }
    }

    private void createDocumentIndex() throws Exception {
        //Directory index = FSDirectory.open(Paths.get(indexLocation));

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new ClassicSimilarity();
        Directory index = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setSimilarity(similarity);


       // iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // create the IndexWriter with the configuration as above
        IndexWriter indexWriter = new IndexWriter(index, iwc);

        // parse txt document using TXT parser and index it
        MyDocumentParser documentParser = new MyDocumentParser(docsFile);
        documentParser.parse();
        List<MyDocument> docs = documentParser.getDocuments();
        for (MyDocument doc : docs) {
            indexDoc(indexWriter, doc);
        }
        indexWriter.close();

        IndexReader reader = DirectoryReader.open(index);
        testSparseFreqDoubleArrayConversionDocument(reader);
        reader.close();
        }

    private void indexDoc(IndexWriter indexWriter, MyDocument mydoc){
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setTokenized(true);
        type.setStored(true);
        type.setStoreTermVectors(true);

        try {
            // make a new, empty document
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            // create the fields of the document and add them to the document

            Field abstractInfo = new Field("abstractInfo", mydoc.getAbstractInfo(), type);
            doc.add(abstractInfo);
            Field title = new Field("title", mydoc.getAbstractInfo(), type);
            doc.add(title);
            Field authors = new Field("authors", mydoc.getAbstractInfo(), type);
            doc.add(authors);
            Field keywords = new Field("keywords", mydoc.getAbstractInfo(), type);
            doc.add(keywords);

            String searchField = mydoc.getTitle() + " " + mydoc.getAuthors() + " " + mydoc.getKeywords() + " " + mydoc.getAbstractInfo();
            Field multipleFields = new Field("multipleFields", searchField, type);
            doc.add(multipleFields);




            System.out.println("adding " + mydoc);
            indexWriter.addDocument(doc);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void testSparseFreqDoubleArrayConversionDocument(IndexReader reader) throws Exception {

        Terms fieldTerms = MultiFields.getTerms(reader, "multipleFields");   //the number of terms in the lexicon after analysis of the Field "title"
        System.out.println("Terms:" + fieldTerms.size());

        TermsEnum it = fieldTerms.iterator();						//iterates through the terms of the lexicon
        while(it.next() != null) {
            System.out.print(it.term().utf8ToString() + " "); 		//prints the terms
        }
        System.out.println();
        System.out.println();
        if (fieldTerms != null && fieldTerms.size() != -1) {
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            FileWriter myWriter;
            myWriter = new FileWriter("results2//results.txt");
            for (ScoreDoc scoreDoc : indexSearcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE).scoreDocs) {   //retrieves all documents
                System.out.println("DocID: " + scoreDoc.doc);
                Terms docTerms = reader.getTermVector(scoreDoc.doc, "multipleFields");

                Double[] vector = DocToDoubleVectorUtils.toSparseLocalFreqDoubleArray(docTerms, fieldTerms); //creates document's vector
                if(vector == null){
                    System.err.println("Document " + scoreDoc.doc + " had a null terms vector for body");
                }else{
                    NumberFormat nf = new DecimalFormat("0.#");
                    System.out.println(vector.length-1 + "\n");
                    for(int i = 0; i<=vector.length-1; i++ ) {
                        myWriter.write(nf.format(vector[i])+ " ");
                    }
                    myWriter.write("\n");
                }

            }
        }
    }



    public static void main(String[] args) {
        try {
            Application indexerDemo = new Application();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}