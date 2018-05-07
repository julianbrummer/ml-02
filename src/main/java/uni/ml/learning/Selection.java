package uni.ml.learning;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Convenient class to select instance indices from a dataset.
 * @author Julian Brummer
 */
public class Selection {
	
	@AllArgsConstructor
	@ToString(includeFieldNames=true)
	public static class Split {
		private List<Integer> first;
		private List<Integer> second;
		
		public int[] first() {
			return first.stream().mapToInt(i->i).toArray();
		}
		
		public int[] second() {
			return second.stream().mapToInt(i->i).toArray();
		}
		
		public int sizeFirst() {
			return first.size();
		}
		
		public int sizeSecond() {
			return second.size();
		}
	}
	
	public static Split randomSplit(float ratio, int numIndices) {
		int n = Math.min((int) Math.ceil(ratio*numIndices), numIndices);
		int[] indices = IntStream.range(0, numIndices).toArray();
		List<Integer> indexList = Arrays.stream(indices).boxed().collect(Collectors.toList());
		Collections.shuffle(indexList);
		return new Split(indexList.subList(0, n), indexList.subList(n, numIndices));
	}
}
