package tudarmstadt.lt.ABSentiment.classifier;


import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

/**
 * The LinearClassifier provides common methods for classification
 */
public class LinearClassifier extends LinearTesting implements Classifier {

    protected Model model;

    protected Vector<FeatureExtractor> features;
    protected HashMap<Double, String> labelMappings;

    protected String label;
    private double score;
    private double[] probEstimates;

    @Override
    public String getLabel(JCas cas) {
        Vector<Feature[]> instanceFeatures = applyFeatures(cas, features);

        Feature[] instance = combineInstanceFeatures(instanceFeatures);
        probEstimates = new double[model.getNrClass()];
        Double prediction = Linear.predictProbability(model, instance, probEstimates);

        label = labelMappings.get(prediction);
        score = probEstimates[Double.valueOf(prediction).intValue()];
        return label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public double getScore() {
        return score;
    }

    /**
     * Loads the label--identifier mappings to retrieve the correct String label for the predicted label.
     * @param fileName path to the mapping file
     */
    protected HashMap<Double, String> loadLabelMapping(String fileName) {
        HashMap<Double, String> lMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                String[] catLine = line.split("\\t");
                Double labelId = Double.parseDouble(catLine[0]);
                lMap.put(labelId, catLine[1]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lMap;
    }
}
