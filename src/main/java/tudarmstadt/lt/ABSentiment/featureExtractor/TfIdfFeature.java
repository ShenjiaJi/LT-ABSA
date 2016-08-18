package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * TF-IDF {@link FeatureExtractor}, extracts normalized TF-IDF scores using a pre-computed IDF file.
 */
public class TfIdfFeature implements FeatureExtractor {

    private int maxTokenId = -1;
    private int offset = 0;

    private HashMap<Integer, Double> termIdf = new HashMap<>();
    private HashMap<String, Integer> tokenIds = new HashMap<>();
    //private HashMap<Integer, String> tokenStrings;

    private Preprocessor preprocessor = new Preprocessor();

    /**
     * Constructor; specifies the IDF file. Feature offset is set to '0' by default.
     * @param idfFile path to the file containing IDF scores
     */
    public TfIdfFeature(String idfFile) {
        loadIdfList(idfFile);
    }

    /**
     * Constructor; specifies the IDF file. Feature offset is specified.
     * @param idfFile path to the file containing IDF scores
     * @param offset the feature offset, all features start from this offset
     */
    public TfIdfFeature(String idfFile, int offset) {
        this(idfFile);
        this.offset = offset;
    }

    @Override
    public Feature[] extractFeature(JCas cas) {

        Collection<String> documentText = preprocessor.getTokenStrings(cas);
        HashMap<Integer, Integer> tokenCounts = getTokenCounts(documentText);
        return getTfIdfScores(tokenCounts);
    }

    /**
     * Calculates the token counts for each token in the document.
     * @param documentText a collection of String tokens, that constitute the document
     * @return a HashMap that stores the token count for each token (tokenId->count)
     */
    private HashMap<Integer, Integer> getTokenCounts(Collection<String> documentText) {
        HashMap<Integer, Integer> tokenCounts = new HashMap<>();
        for (String token : documentText) {
            if (token == null) {
                continue;
            }
            Integer tokenId = tokenIds.get(token);
            if (tokenId == null) {
                continue;
            }
            if (tokenCounts.get(tokenId) != null) {
                tokenCounts.put(tokenId, tokenCounts.get(tokenId) + 1);
            } else {
                tokenCounts.put(tokenId, 1);
            }
        }
        return tokenCounts;
    }

    @Override
    public int getFeatureCount() {
        return maxTokenId;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * Calculates the instance array containing TF-IDF scores for each token
     * @param tokenCounts the token count for each token ID
     * @return an array of {@link Feature} elements
     */
    private Feature[] getTfIdfScores(HashMap<Integer, Integer> tokenCounts) {
        int count;
        double idf;
        double weight;
        double normalizedWeight;
        double norm = 0;

        HashMap<Integer, Double> termWeights = new HashMap<>();
        // calculate TF-IDF scores for each token, also add to normalizer
        for (int tokenID : tokenCounts.keySet()) {
            count = tokenCounts.get(tokenID);
            idf = termIdf.get(tokenID);
            weight = count * idf;

            if (weight > 0.0) {
                norm += Math.pow(weight, 2);
                termWeights.put(tokenID, weight);
            }
        }
        // calculate normalization
        norm = Math.sqrt(norm);

        Feature[] instance = new Feature[termWeights.size()];
        ArrayList<Integer> list = new ArrayList<>(termWeights.keySet());
        Collections.sort(list);
        Double w;
        int i =0;
        // add normalized TF-IDF scores to the training instance
        for (int tokenId: list) {
            w = termWeights.get(tokenId);
            if (w == null) {
                w = 0.0;
            }
            normalizedWeight = w / norm;
            instance[i++] = new FeatureNode(tokenId+offset, normalizedWeight);
        }
        return instance;
    }

    /**
     * Loads a word list with words, wordIds and IDF scores.
     * @param fileName the path to the input file
     */
    private void loadIdfList(String fileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokenLine = line.split("\\t");
                int tokenId = Integer.parseInt(tokenLine[1]);
                if (tokenId > maxTokenId) {
                    maxTokenId = tokenId;
                }
                tokenIds.put(tokenLine[0], tokenId);
                //tokenStrings.put(tokenId, tokenLine[0]);
                termIdf.put(tokenId, Double.parseDouble(tokenLine[2]));
            }
            br.close();
        } catch (IOException e) {
            //logger.log(Level.SEVERE, "Could not load word list " + fileName + "!");
            e.printStackTrace();
        }
    }
}