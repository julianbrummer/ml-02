package uni.ml.learning;

import java.util.HashSet;
import java.util.Set;

import uni.ml.dataset.Dataset;
import uni.ml.dataset.DatasetIndexedView;
import uni.ml.dataset.DatasetPredicateView;
import uni.ml.dataset.DatasetView;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;
import uni.ml.tree.Classifier;
import uni.ml.tree.InnerNode;
import uni.ml.tree.Leaf;
import uni.ml.tree.Node;
import uni.ml.tree.TreeStringBuilder;


public class DecisionTreeModel {

	private Node root;
	
	/**
	 * Selects the partition attribute resulting in the maximum information gain.
	 */
	public static EnumAttribute<?> selectPartitionAttribute(DatasetView dataset, EnumAttribute<?> classAttribute, Set<EnumAttribute<?>> attributes) {
		EnumAttribute<?> partitionAttribute = null;
		float maxGain = Float.NEGATIVE_INFINITY;
		// iterate over attributes and check information gain using the attribute as a partitioner
		for (EnumAttribute<?> attribute : attributes) {
			float gain = Measures.informationGain(dataset, classAttribute, attribute);
			if (gain > maxGain) {
				maxGain = gain;
				partitionAttribute = attribute;
			}
		}
		return partitionAttribute;
	}
	
	/**
	 * Recursively creates a decision (sub-)tree from an example set. 
	 * @param examples The dataset or a view on a subset.
	 * @param classAttribute The classification/target attribute.
	 * @param attributes A list of attributes from which to select a decision attribute for this node.  
	 * @return The root node of the (sub-)tree.
	 */
	private Node trainModel(DatasetView examples, EnumAttribute<?> classAttribute, Set<EnumAttribute<?>> attributes) {
	
		if (Measures.entropy(examples, classAttribute) == 0) // all instances have the same value for the target attribute
			return new Leaf(examples.instanceAt(0).value(classAttribute)); // return a leaf with that value
		
		if (attributes.isEmpty()) // return most common value if there are no more attributes to split on
			return new Leaf(Measures.mostCommonValue(examples, classAttribute));
	
		// splitting is possible, so we create an inner node and select the best partition attribute
		InnerNode node = new InnerNode();
		node.decisionAttribute(selectPartitionAttribute(examples, classAttribute, attributes));
		
		// iterate over values of the decision attribute
		for (Value<?> value : node.decisionAttribute()) {
			// select subset containing only instances with the same decision value
			DatasetView subset = DatasetPredicateView.selectInstances(examples, node.decisionAttribute(), value);
			if (subset.hasInstances()) {
				// remove decision attribute and build subtree
				Set<EnumAttribute<?>> remainingAttributes = new HashSet<>(attributes);
				remainingAttributes.remove(node.decisionAttribute());
				node.addChild(value, trainModel(subset, classAttribute, remainingAttributes));
			} else {
				node.addChild(value, new Leaf(Measures.mostCommonValue(examples, classAttribute)));
			}
		}
		
		return node;
	}
	
	
	/**
	 * Recursively creates a decision (sub-)tree from a full example set.
	 * All attributes within the dataset (except for the classAttribute) are possible candidates for partition attributes.
	 * @param examples The dataset to create the decision tree from.
	 * @param classAttribute The classification/target attribute. 
	 * @return The root node of the (sub-)tree.
	 */
	public void trainModel(Dataset examples, EnumAttribute<?> classAttribute) {
		this.root = trainModel(examples, classAttribute, examples.attributeSet(classAttribute));
	}
	
	/**
	 * Recursively creates a decision (sub-)tree from a subset of an example set.
	 * All attributes within the dataset (except for the classAttribute) are possible candidates for partition attributes.
	 * @param examples The dataset to create the decision tree from.
	 * @param indices Specifies a subset of the dataset by selecting instances (rows) through indices.
	 * @param classAttribute The classification/target attribute. 
	 * @return The root node of the (sub-)tree.
	 */
	public void trainModelOnSubset(Dataset examples, int[] indices, EnumAttribute<?> classAttribute) {
		this.root = trainModel(new DatasetIndexedView(examples, indices), classAttribute, examples.attributeSet(classAttribute));
	}
	
	/**
	 * Tests the model with a test dataset.
	 * @param testSet The dataset to test the model.
	 * @return The percantage of correctly classified instances.
	 */
	public float testModel(DatasetView testSet, EnumAttribute<?> classAttribute) {
		Classifier classifier = new Classifier(root);
		int numCorrectlyClassified = 0;
		for (Instance instance : testSet.instances()) {
			if (classifier.classify(instance).equals(instance.value(classAttribute))) {
				numCorrectlyClassified++;
			}
		}
		return (float) numCorrectlyClassified/testSet.numInstances();
	}

	
	/**
	 * Tests the model with a subset of a dataset.
	 * @param dataset The dataset to select a test set from.
	 * @param indices Specifies a subset of the dataset by selecting instances (rows) through indices.
	 * @return The percantage of correctly classified instances.
	 */
	public float testModelOnSubset(Dataset dataset, int[] indices, EnumAttribute<?> classAttribute) {
		return testModel(new DatasetIndexedView(dataset, indices), classAttribute);
	}
	
	
	/**
	 * Convenience method to print a decision tree.
	 * @param root The root node of the tree to print.
	 */
	public void print() {
		System.out.println(new TreeStringBuilder().toString(root));
	}
	
	/**
	 * Trains and tests a model a number of times.
	 * @param dataset The dataset to train and test the model with. The dataset is split randomly into a training and test set. 
	 * @param trainingRatio The ratio of the dataset to use for training.
	 * @param repeats The number of training and test cycles.
	 * @param classAttribute The target/classification attribute.
	 * @return The mean and standard deviation of correctly classified instances.
	 */
	public static ClassificationResult trainAndTestModel(Dataset dataset, float trainingRatio, int repeats, EnumAttribute<?> classAttribute) {
		float meanClassified = 0.0f;
		float deviationClassified = 0.0f;
		float[] classified = new float[repeats];
		DecisionTreeModel model = new DecisionTreeModel();
		for (int i = 0; i < repeats; i++) {
			Selection.Split split = Selection.randomSplit(trainingRatio, dataset.numInstances());
			model.trainModelOnSubset(dataset, split.first(), classAttribute);
			classified[i] = model.testModelOnSubset(dataset, split.second(), classAttribute);
			meanClassified += classified[i];
		}
		meanClassified /= repeats;
		for (int i = 0; i < repeats; i++) {
			deviationClassified += Math.pow(classified[i]-meanClassified, 2);
		}
		deviationClassified /= repeats;
		deviationClassified = (float) Math.sqrt(deviationClassified);
		return new ClassificationResult(meanClassified, deviationClassified);
	}
	
}
