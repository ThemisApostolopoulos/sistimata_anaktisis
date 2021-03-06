package phase4;

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
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.shade.guava.base.Splitter;
import phase1.MyDocument;
import phase1.MyDocumentParser;
import phase1.MyQuery;
import phase1.MyQueryParser;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;





public class Application {

    //input files
    private String docsFile = "docs//cacm.all";
    private String queryFile = "docs//query.text";

    //output files
    private String indexLocation = ("index");
    private String resultsLocation = ("results4");

    //types and fields
    private final String searchField = "multipleFields";

    private static Word2Vec vec;
    private static Word2Vec vec2;
    private  List<MyDocument> docs;


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
        docs = documentParser.getDocuments();
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
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);
            StoredField abstractInfo= new StoredField("abstractInfo", mydoc.getAbstractInfo());
            doc.add(abstractInfo);
            String fullSearchableText = mydoc.getId() + " " + mydoc.getAuthors() + " " + mydoc.getDate() + " " +
                    mydoc.getContent() + mydoc.getKeywords() + " " + mydoc.getEntrydate() + " " +
                    mydoc.getTitle() + " " + mydoc.getAbstractInfo() ;
            StoredField reference = new StoredField("reference", fullSearchableText);
            String searchField = mydoc.getTitle() + " " + mydoc.getAuthors() + " " + mydoc.getKeywords() + " " + mydoc.getAbstractInfo();
            TextField multipleFields = new TextField("multipleFields", searchField, Field.Store.NO);
            doc.add(multipleFields);


            doc.add(reference);

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                // System.out.println("adding " + mydoc);
                indexWriter.addDocument(doc);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void searcher(int i) throws Exception {
//
//        SentenceIterator iter = new BasicLineIterator(docsFile);
//        vec = new Word2Vec.Builder()
//                .layerSize(200)
//                .windowSize(5)
//                .iterate(iter)
//                .epochs(3)
//                .elementsLearningAlgorithm(new SkipGram<>())
//                .build();
//        vec.fit();
//        WordVectorSerializer.writeWord2VecModel(vec, "savedModel.txt");
        //read the model
        vec = WordVectorSerializer.readWord2VecModel("savedModel.txt");
        System.out.println("vector size:" + vec.vectorSize());









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
            myWriter = new FileWriter("results4//results1.txt");
        }else if(numberOfResults == 30){
            myWriter = new FileWriter("results4//results2.txt");
        }else{
            myWriter = new FileWriter("results4//results3.txt");
        }
        //for every query
        for (MyQuery query : queries) {





            Query q = parser.parse(QueryParser.escape(query.getQuery()));
            System.out.println("query " + query.getQuery());
            String qid;
            if(query.getId() <10){
                qid = "0" + String.valueOf(query.getId());
            }else{
                qid = String.valueOf(query.getId());
            }
            if( query.getId() == 34 || query.getId() == 35 || query.getId() == 41 ||  query.getId() == 46 || query.getId() == 47 ||  (query.getId() >=50  && query.getId() <= 56)){
                //System.out.println(qid);
            }else{
            ArrayList<Tuple> cosArray = new ArrayList<>();
                //for every document
                for(int i=0; i<docs.size(); i++){

                    double cos = cosineSimForSentence(vec,query.getQuery(),docs.get(i).getTitle() + " " + docs.get(i).getAuthors() + " " + docs.get(i).getKeywords() + " " + docs.get(i).getAbstractInfo());
                    cosArray.add(new Tuple(cos,docs.get(i).getId()));

                }
                Collections.sort(cosArray);
                System.out.println(cosArray.size());
                for(int i=0; i<numberOfResults; i++){
                    myWriter.write(qid + "\t0\t" + cosArray.get(i).getDocId() + "\t0\t" + cosArray.get(i).getCos() + "\tSTANDARD\n");
                }

            }

        }
        myWriter.close();
    }








    public static double cosineSimForSentence(Word2Vec vector, String sentence1, String sentence2){
        Collection<String> label1 = Splitter.on(' ').splitToList(sentence1);
        Collection<String> label2 = Splitter.on(' ').splitToList(sentence2);
        try{
            return Transforms.cosineSim(vector.getWordVectorsMean(label1), vector.getWordVectorsMean(label2));
        }catch(Exception e){
            System.err.println(e);
        }
        return Transforms.cosineSim(vector.getWordVectorsMean(label1), vector.getWordVectorsMean(label2));

    }

    public static void main(String[] args) {
        try {
            Application indexerDemo = new Application();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}