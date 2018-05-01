package uni.ml.tree;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uni.ml.dataset.Value;


/**
 * A leaf of a decision tree contains only a single value.
 * 
 * @author Julian Brummer
 *
 */
@RequiredArgsConstructor
@ToString(includeFieldNames=false)
public class Leaf extends Node {
	@NonNull
	private Value<?> value;

	/**
	 * Make leaf visitable, e.g. for printing the tree.
	 */
	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
