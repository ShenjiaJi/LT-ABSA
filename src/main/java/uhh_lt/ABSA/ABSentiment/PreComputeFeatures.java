/*
 * ******************************************************************************
 *  Copyright 2017
 *  Copyright (c) 2017 Universität Hamburg
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package uhh_lt.ABSA.ABSentiment;

import uhh_lt.ABSA.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;
import uhh_lt.ABSA.ABSentiment.featureExtractor.precomputation.ComputeIdfTermsCategory;
import uhh_lt.ABSA.ABSentiment.featureExtractor.precomputation.ComputeMaxDocumentLength;
import uhh_lt.ABSA.ABSentiment.training.util.ProblemBuilder;

/**
 * Pre-computation of IDF map and other data used in features.
 */
public class PreComputeFeatures extends ProblemBuilder {

    /**
     * Extracts different data for feature extractors.
     * @param args
     */
    public static void main(String[] args) {


        String configurationFile = "configuration.txt";
        if (args.length > 0) {
            configurationFile = args[0];
        }

        initialise(configurationFile);

        ComputeCorpusIdfScores.computeIdfScores(corpusFile, idfFile, 100);
        ComputeMaxDocumentLength.computeMaxDocumentLength(trainFile, maxLengthFile);

        ComputeIdfTermsCategory.computeIdfScores(configurationFile, trainFile, relevanceIdfFile, false, "relevance");
        ComputeIdfTermsCategory.computeIdfScores(configurationFile, trainFile, sentimentIdfFile, false, "sentiment");
        ComputeIdfTermsCategory.computeIdfScores(configurationFile, trainFile, aspectIdfFile, false, "aspect");
        ComputeIdfTermsCategory.computeIdfScores(configurationFile, trainFile, aspectCoarseIdfFile, true, "aspect");
    }

}