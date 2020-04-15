package util;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.JRip;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {

	private static final int SEED = 0;

	private Util() {
	}

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

	public static Set<String> getFileNames(final String dir) {
		return Arrays.stream(
				Objects.requireNonNull(Path.of(dir)
						                       .toFile()
						                       .listFiles())
		).map(File::getAbsolutePath)
				       .filter(fileName -> fileName.endsWith(".arff"))
				       .collect(Collectors.toSet());
	}

	public static ConjunctiveRule getConjunctiveRule(final Instances data) throws Exception {
		final ConjunctiveRule rule = new ConjunctiveRule();
		rule.buildClassifier(data);
		return rule;
	}

	public static JRip getRipper(final Instances data, final boolean usePruning) throws Exception {
		final JRip ripper = new JRip();
		ripper.setUsePruning(usePruning);
		ripper.buildClassifier(data);
		return ripper;
	}

	public static Evaluation cvModel(final Classifier clf, final Instances data, final int folds) throws Exception {
		final Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(clf, data, folds, new Random(SEED));
		return eval;
	}

	public static Evaluation cvModel(final Classifier clf, final Instances data) throws Exception {
		return cvModel(clf, data, 10);
	}

}
