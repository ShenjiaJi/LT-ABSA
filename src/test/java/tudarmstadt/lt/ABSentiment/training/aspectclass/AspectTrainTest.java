package tudarmstadt.lt.ABSentiment.training.aspectclass;

import tudarmstadt.lt.ABSentiment.training.ComputeIdfScores;

import static org.junit.Assert.*;

public class AspectTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "/relevance-train.tsv";
        String idfFile = "idfmap.tsv";

        ComputeIdfScores.computeIdfScores(trainingFile, idfFile);
        Train.main(new String[0]);
    }

}