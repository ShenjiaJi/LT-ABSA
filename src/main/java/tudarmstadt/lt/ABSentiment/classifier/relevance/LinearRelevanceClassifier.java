package tudarmstadt.lt.ABSentiment.classifier.relevance;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;

/**
 * The LinearRelevanceClassifier classifies the relevance of a document.
 */
public class LinearRelevanceClassifier extends LinearClassifier  {

    /**
     * Constructor for the relevance classifier; expects the path to the model file.
     * @param modelFile path to the SVM model file
     */
    public LinearRelevanceClassifier(String modelFile) {
        this(modelFile, "relevance-label-mappings.tsv");
    }

    /**
     * Constructor for the relevance classifier; expects the path to the model file, as well as the label mapping file.
     * @param modelFile path to the SVM model file
     * @param labelMappingsFile path to the file containing label ids and their String representation
     */
    public LinearRelevanceClassifier(String modelFile, String labelMappingsFile) {
        model = loadModel(modelFile);
        features = loadFeatureExtractors();

        labelMappings = loadLabelMapping(labelMappingsFile);
    }
}
