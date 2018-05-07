package uni.ml.tree;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;

/**
 * Can be used to classify an instance of a dataset with a decision tree. 
 * @author Julian Brummer
 *
 */
@RequiredArgsConstructor
public class Classifier implements NodeVisitor {

	@NonNull
	private Node root;
	private Instance testInstance;
	private Value<?> classValue;
	
	@Override
	public void visit(InnerNode node) {
		Value<?> decisionValue = testInstance.value(node.decisionAttribute());
		node.child(decisionValue).accept(this);
	}

	@Override
	public void visit(Leaf node) {
		classValue = node.value();
	}
	
	public Value<?> classify(Instance instance) {
		testInstance = instance;
		root.accept(this);
		return classValue;
	}

	
}
