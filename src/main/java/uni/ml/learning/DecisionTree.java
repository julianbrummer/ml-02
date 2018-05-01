package uni.ml.learning;

import java.util.HashSet;
import java.util.Set;

import uni.ml.dataset.Dataset;
import uni.ml.dataset.DatasetIndexedView;
import uni.ml.dataset.DatasetPredicateView;
import uni.ml.dataset.DatasetView;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Value;
import uni.ml.tree.InnerNode;
import uni.ml.tree.Leaf;
import uni.ml.tree.Node;
import uni.ml.tree.TreeStringBuilder;


public class DecisionTree {

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
	public static Node trainModel(DatasetView examples, EnumAttribute<?> classAttribute, Set<EnumAttribute<?>> attributes) {
	
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
			// remove decision attribute and build subtree
			Set<EnumAttribute<?>> remainingAttributes = new HashSet<>(attributes);
			remainingAttributes.remove(node.decisionAttribute());
			node.addChild(value, trainModel(subset, classAttribute, remainingAttributes));
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
	public static Node trainModel(Dataset examples, EnumAttribute<?> classAttribute) {
		return trainModel(examples, classAttribute, examples.attributeSet(classAttribute));
	}
	
	/**
	 * Recursively creates a decision (sub-)tree from a subset of an example set.
	 * All attributes within the dataset (except for the classAttribute) are possible candidates for partition attributes.
	 * @param examples The dataset to create the decision tree from.
	 * @param indices Specifies a subset of the dataset by selecting instances (rows) through indices.
	 * @param classAttribute The classification/target attribute. 
	 * @return The root node of the (sub-)tree.
	 */
	public static Node trainModelOnSubset(Dataset examples, int[] indices, EnumAttribute<?> classAttribute) {
		return trainModel(new DatasetIndexedView(examples, indices), classAttribute, examples.attributeSet(classAttribute));
	}
	
	/**
	 * Convenience method to print a decision tree.
	 * @param root The root node of the tree to print.
	 */
	public static void print(Node root) {
		System.out.println(new TreeStringBuilder().toString(root));
	}
	
}
