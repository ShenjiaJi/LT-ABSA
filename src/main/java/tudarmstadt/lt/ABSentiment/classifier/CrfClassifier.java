package tudarmstadt.lt.ABSentiment.classifier;


import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import tudarmstadt.lt.ABSentiment.classifier.aspecttarget.AspectAnnotator;
import tudarmstadt.lt.ABSentiment.type.uima.AspectTarget;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

/**
 * The CRF classifier is used for aspect target detection.
 */
public class CrfClassifier {
    private AnalysisEngine classifier;

    /**
     * Constructor, creates an AnalysisEngine with a CRF classifier.
     * @param modelDirectory the directory where the model is located
     */
    public CrfClassifier(String modelDirectory) {
        try {
            classifier = createEngine(AspectAnnotator.class,
                    GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                    modelDirectory + "model.jar");
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the CRF classifier that annotates {@link AspectTarget} in a CAS
     * @param cas the input CAS
     * @return a processed CAS with {@link AspectTarget} annotations
     */
    public JCas processCas(JCas cas) {
        try {
            runPipeline(cas, classifier);
            return cas;
        } catch (AnalysisEngineProcessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
