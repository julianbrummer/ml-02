package uni.ml.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A dataset stores the attributes (e.g. the header/column labels of a table) and
 * data instances (e.g. the rows of a table).
 * The dataset can be decorated with several views.
 * @see DatasetView
 * @author Julian Brummer
 *
 */
@Accessors(fluent=true)
public class Dataset extends DatasetView {
	
	@Setter @Getter
	private String name;
	private List<EnumAttribute<?>> attributes = new ArrayList<>();
	private List<Instance> instances = new ArrayList<>();
	
	private EnumAttribute<String> parseAttribute(String line) {
		String[] parts = line.split("\\{");
		String attrName = parts[0].trim().split(" ")[1].trim();
		String[] values = parts[1].substring(0, parts[1].length()-1).split(", ");
		
		return new EnumAttribute<>(attrName, values);
	}
	
	private Instance parseInstance(String line) {
		String[] values = line.split(",");
		Instance instance = new Instance();
		for (int i = 0; i < numAttributes(); i++) {
			instance.addUnchecked(attributes.get(i), new Value<String>(values[i]));
		}
		return instance;		
	}
	
	public Dataset(EnumAttribute<?>...attributes) {
		this.attributes = new ArrayList<>(Arrays.asList(attributes));
	}
	
	
    /**
     * Parses the given ARFF file and adds the attributes and instances to the
     * dataset.
     * @throws IOException 
     *
     */
    public void loadFromFile(File file) throws IOException {
		String line = null;
		BufferedReader r = new BufferedReader(new FileReader(file));
	
		boolean header = true;
		while ((line = r.readLine()) != null) {
		    line = line.trim();
		    if (!line.isEmpty()) {
			    if (header) {
					if (line.startsWith("@relation")) {
					    name = line.split(" ")[1];
					} else if (line.startsWith("@attribute")) {
					    addAttribute(parseAttribute(line));
					} else if (line.startsWith("@data")) {
						header = false;
					}
			    } else {
				    addInstance(parseInstance(line));
			    }
		    }
		}
		r.close();
    }
    
    /**
	 * Adds an instance (row) to the dataset.
	 */
	public void addInstance(Instance instance) {
		instances.add(instance);
	}
	
	/**
	 * Adds instances (rows) to the dataset.
	 */
	public void addInstances(Instance... instances) {
		this.instances.addAll(Arrays.asList(instances));
	}
	
	/**
	 * Adds an attribute (column) to the dataset.
	 */
	public void addAttribute(EnumAttribute<?> attribute) {
		attributes.add(attribute);
	}
	
	/**
	 * Adds an attribute (column) to the dataset.
	 */
	public void addAttributes(EnumAttribute<?>... attributes) {
		this.attributes.addAll(Arrays.asList(attributes));
	}
    
	
	/**
	 * A convenience method to access all attributes except for those that are explicitly excluded.
	 * @param exclude The attributes to exclude from the set.
	 * @return the set of attributes (columns) of the dataset without the excluded attributes.
	 */
	public Set<EnumAttribute<?>> attributeSet(EnumAttribute<?>... exclude) {
		Set<EnumAttribute<?>> attrSet = new HashSet<>(attributes);
		attrSet.removeAll(Arrays.asList(exclude));
		return attrSet;
	}
	
	/**
	 * A convenience method to access the last attribute, e.g for classification.
	 * @return the last attribute.
	 */
	public EnumAttribute<?> lastAttribute() {
		return attributeAt(numAttributes()-1);
	}
	
	@Override
	public int numAttributes() {
		return attributes.size();
	}

	@Override
	public int numInstances() {
		return instances.size();
	}

	@Override
	public EnumAttribute<?> attributeAt(int index) {
		return attributes.get(index);
	}

	@Override
	public Instance instanceAt(int index) {
		return instances.get(index);
	}


}
