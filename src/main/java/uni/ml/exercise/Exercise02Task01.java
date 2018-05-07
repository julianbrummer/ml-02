/**
 * Package for Machine Learning exercises.
 * @author Julian Brummer
 * @author Alexander Petri
 */
package uni.ml.exercise;


import java.io.File;
import java.io.IOException;

import uni.ml.dataset.Dataset;
import uni.ml.dataset.DatasetIndexedView;
import uni.ml.learning.DecisionTreeModel;
import uni.ml.learning.Selection;

/**
 * The main class for Exercise02 Task01.
 * @author Julian Brummer
 *
 */
public class Exercise02Task01 {

	
	public static void main(String[] args) {
		Dataset dataset = new Dataset();
		if (args.length > 0) {
			try {
				dataset.loadFromFile(new File(args[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}

			Selection.Split split = Selection.randomSplit(2.0f/3.0f, dataset.numInstances());
			
			System.out.println("Dataset: " + dataset.name());
			System.out.println();

			System.out.println("Training set:");
			System.out.println(new DatasetIndexedView(dataset, split.first()));
			System.out.println();
			System.out.println("Test set:");
			System.out.println(new DatasetIndexedView(dataset, split.second()));
			System.out.println();
			
			DecisionTreeModel model = new DecisionTreeModel();
			model.trainModelOnSubset(dataset, split.first(), dataset.lastAttribute());
			model.print();

			System.out.println("Dataset: " + dataset.name());
			System.out.println("Size of training set: " + split.sizeFirst());
			System.out.println("Size of test set: " + split.sizeSecond());
			System.out.println("Correctly classified: " + model.testModelOnSubset(dataset, split.second(), dataset.lastAttribute()));
			
			System.out.println(DecisionTreeModel.trainAndTestModel(dataset, 2.0f/3.0f, 10, dataset.lastAttribute()));
		}
		
	}

}
