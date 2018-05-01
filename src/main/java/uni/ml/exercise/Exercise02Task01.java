/**
 * Package for Machine Learning exercises.
 * @author Julian Brummer
 * @author Alexander Petri
 */
package uni.ml.exercise;


import java.io.File;
import java.io.IOException;

import uni.ml.dataset.Dataset;
import uni.ml.learning.DecisionTree;
import uni.ml.tree.Node;

/**
 * The main class for Exercise02 Task01.
 * @author Julian Brummer
 *
 */
public class Exercise02Task01 {

	
	public static void main(String[] args) {
		Dataset dataset = new Dataset();
		
		try {
			dataset.loadFromFile(new File("weather.nominal.arff"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(dataset);
		
		System.out.println();
		Node root = DecisionTree.trainModel(dataset, dataset.lastAttribute());
		DecisionTree.print(root);		
	}

}
