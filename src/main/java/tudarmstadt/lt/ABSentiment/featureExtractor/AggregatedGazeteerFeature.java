package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * AggregatedGazeteerFeature class helps to extract aggregated lexicon feature based on the number of token occurrences in the input lexicon file
 * Created by abhishek on 20/6/17.
 */
public class AggregatedGazeteerFeature implements FeatureExtractor {

    private int offset = 0;

    private HashMap<String, HashSet<Integer>> termIndexMap = new HashMap<>();
    private HashMap<String, Integer> labelIndexMap = new HashMap<>();
    private ArrayList<String> terms = new ArrayList<>();
    private int totalNumberOfLabels;
    private int featureCount;
    private int columnType;

    private Preprocessor preprocessor = new Preprocessor(true);

    /**
     * Constructor; specifies the aggregate gazetteer file. Feature offset is set to '0' by default.
     * @param aggregatedGazeteer path to a file containing word with/without their polarity
     */
    public AggregatedGazeteerFeature(String aggregatedGazeteer) {
        loadGazeteerFile(aggregatedGazeteer);
    }

    /**
     * Constructor; specifies the aggregate gazetteer file. Feature offset is specified.
     * @param aggregatedGazeteer path to a file containing word with/without their polarity
     * @param offset the feature offset, all features start from this offset
     */
    public AggregatedGazeteerFeature(String aggregatedGazeteer, int offset) {
        this(aggregatedGazeteer);
        this.offset = offset;
    }

    @Override
    public Feature[] extractFeature(JCas cas) {
        Collection<String> documentText = preprocessor.getTokenStrings(cas);
        double[] featureVector = new double[featureCount];
        float documentLength = documentText.size();

        for(int i = 0;i<featureVector.length;i++){
            featureVector[i] = 0;
        }

        if(columnType == 1){
            for(String token:documentText){
                if(terms.contains(token)){
                    featureVector[0]++;
                }
            }
        }else if(columnType == 2){
            for(String token:documentText){
                if(termIndexMap.containsKey(token)){
                    for(Integer index:termIndexMap.get(token)){
                        featureVector[index]++;
                    }
                }
            }
        }
        Feature[] features = new Feature[featureCount];

        if(documentLength != 0) {
            for(int i = 0;i<featureVector.length;i++){
                featureVector[i] /= documentLength;
            }
        }
        for(int i=0;i<featureCount;i++){
            features[i] = new FeatureNode(i + offset + 1, featureVector[i]);
        }
        return features;
    }

    @Override
    public int getFeatureCount() {
        return featureCount;
    }

    @Override
    public int getOffset() {
        return offset;
    }


    /**
     * Loads a word list with words with/without their polarity.
     * @param fileName path to a file containing word with/without their polarity
     */
    private void loadGazeteerFile(String fileName) {
        BufferedReader br;
        String line;
        int i = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            if ((line = br.readLine()) != null) {
                String[] tokenLine = line.split("\\t");
                if (tokenLine.length == 0) {
                    columnType = 0;
                }else if(tokenLine.length == 1 ){
                    columnType = 1;
                }else{
                    columnType = 2;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            if(columnType == 1){
                while ((line = br.readLine()) != null) {
                    String[] tokenLine = line.split("\\t");
                    terms.add(tokenLine[0]);
                }
                i++;
            }else if(columnType == 2){
                while ((line = br.readLine()) != null) {
                    String[] tokenLine = line.split("\\t");
                    if(!labelIndexMap.containsKey(tokenLine[tokenLine.length-1])){
                        labelIndexMap.put(tokenLine[tokenLine.length-1], i++);
                    }
                    HashSet<Integer> indexHashSet;
                    if(!termIndexMap.containsKey(tokenLine[0])){
                        indexHashSet  = new HashSet<>();
                    }else{
                        indexHashSet = termIndexMap.get(tokenLine[0]);
                    }
                    indexHashSet.add(labelIndexMap.get(tokenLine[tokenLine.length-1]));
                    termIndexMap.put(tokenLine[0], indexHashSet);
                }
            }
            totalNumberOfLabels = i;
            featureCount = totalNumberOfLabels;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
