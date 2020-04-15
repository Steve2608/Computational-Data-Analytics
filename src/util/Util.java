package util;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class Util {

	public static Instances loadDataset(final String path) throws Exception {
		final Instances data = ConverterUtils.DataSource.read(path);

		// For some Datasets the class attribute is not the last one, and for some it's not called 'class'
		if (data.classIndex() == -1) {
			Attribute attClass = data.attribute("class");
			if (attClass == null) {
				attClass = data.attribute("Class");
			}
			if (attClass != null) {
				data.setClassIndex(attClass.index());
			} else {
				data.setClassIndex(data.numAttributes() - 1);
			}
		}
		return data;
	}
}
