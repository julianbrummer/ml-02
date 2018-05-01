package uni.ml.dataset;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Stores some comparable value.
 * A value can be assigned to an attribute within an {@link Instance}.
 * 
 * @author Julian Brummer
 *
 * @param <T> The type of the value. This can be any type, but it must be comparable.
 */
@RequiredArgsConstructor
@Accessors(fluent=true)
@ToString(includeFieldNames = false, doNotUseGetters = true)
@EqualsAndHashCode
public class Value<T extends Comparable<T>> implements Comparable<Value<? extends T>> {
	@NonNull @Getter
	private T value;

	@Override
	public int compareTo(Value<? extends T> other) {
		return value.compareTo(other.value);
	}
	
	public boolean lessThan(Value<? extends T> other) {
		return compareTo(other) < 0;
	}
	
	public boolean greaterThan(Value<? extends T> other) {
		return compareTo(other) > 0;
	}
}
