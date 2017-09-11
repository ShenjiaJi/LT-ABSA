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

package uhh_lt.ABSA.ABSentiment.featureExtractor.precomputation;

import uhh_lt.ABSA.ABSentiment.reader.InputReader;
import uhh_lt.ABSA.ABSentiment.reader.TsvReader;
import uhh_lt.ABSA.ABSentiment.reader.XMLReader;
import uhh_lt.ABSA.ABSentiment.type.Document;

/**
 * Computes global IDF scores for a corpus in TSV format.
 */
public class ComputeCorpusIdfScores {

    /**
     * Computes global IDF scores from an input file and saves them in a file; specifies a minimum frequency for terms.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the output file with term ids and idf scores
     * @param minFrequency the minimum frequency for a term
     */
    public static void computeIdfScores(String inputFile, String outputFile, int minFrequency) {
        ComputeIdf idf = new ComputeIdf();

        idf.setMinFrequency(minFrequency);
        InputReader fr;
        if (inputFile.endsWith(".xml")) {
            fr = new XMLReader(inputFile);
        } else {
            fr = new TsvReader(inputFile);
        }

        System.out.println("Computing tf-idf scores...\n");
        int i = 0;
        for (Document d: fr) {
            idf.addDocument(d);
            i++;
            if (i % 20000 == 0) {
                System.out.print("\n" + i +" ");
            } else if (i % 1000 == 0) {
                System.out.print(".");
            }
        }
        idf.saveIdfScores(outputFile);
    }

    /**
     * Computes global IDF scores from an input file and saves them in a file.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the output file
     */
    public static void computeIdfScores(String inputFile, String outputFile) {
        computeIdfScores(inputFile, outputFile, 1);
    }

}
