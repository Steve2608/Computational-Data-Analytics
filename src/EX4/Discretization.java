package EX4;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

import java.util.ArrayList;
import java.util.Enumeration;

import static util.Util.*;

public class Discretization {

	private final static String PATTERN = "glass"; // 7 numerical attributes

	private Discretization() {
	}

	public static void main(String[] args) throws Exception {
		final Instances data = load(PATTERN);
		System.out.println(data.toSummaryString());
		final Discretize discretizer = new Discretize();
		discretizer.setInputFormat(data);
		runExperiment(discretizer, data);

		final Discretize binaryDiscretizer = new Discretize();
		binaryDiscretizer.setInputFormat(data);
		binaryDiscretizer.setMakeBinary(true);
		runExperiment(binaryDiscretizer, data);
	}

	private static void runExperiment(Discretize discretizer, Instances data) throws Exception {
		final Instances dataDiscr = Filter.useFilter(data, discretizer); // TODO maybe TTS before
		checkBins(dataDiscr, data);

		final J48 j48 = getJ48(data);
		printTreeStatistics(j48, "J48 Summary");
		final J48 j48_discr = getJ48(dataDiscr);
		printTreeStatistics(j48, "J48_Discretized Summary");

		Evaluation evalJ48 = cvModel(j48, data);
		Evaluation evalJ48_discr = cvModel(j48_discr, dataDiscr);

		System.out.printf("J48: Accuracy= %.3f\n", evalJ48.pctCorrect());
		System.out.printf("J48_Discretized: Accuracy= %.3f\n", evalJ48_discr.pctCorrect());

		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(discretizer);

		Evaluation eval_fc = cvModel(fc, data);

		System.out.printf("FilteredClassifier: Accuracy= %.3f\n", eval_fc.pctCorrect());
		System.out.println("______________________________________________________________");
	}

	private static void checkBins(final Instances dataDiscr, final Instances data) {
		final ArrayList<String> numerics = new ArrayList<>();
		//Find numeric attributes
		for (final Enumeration<Attribute> e = data.enumerateAttributes(); e.hasMoreElements(); ) {
			Attribute a = e.nextElement();
			if (a.isNumeric())
				numerics.add(a.name());
		}
		System.out.printf("Found %d numeric attributes!\n", numerics.size());
		numerics.forEach(a -> fetchBins(dataDiscr, a));
	}

	private static void fetchBins(final Instances dataDiscr, final String a) {
		assert (dataDiscr.attribute(a).isNominal()); // Should now be nominal
		//System.out.println(dataDiscr.toSummaryString());
		//System.out.println(dataDiscr.attribute(a));


		Attribute att = dataDiscr.attribute(a);
		if (att == null)
			att = dataDiscr.attribute(a + "_1");
		final int nbins = att.numValues();
		System.out.printf("Attribute %s now has %d bins\n", a, nbins);
		for (final Enumeration<Object> e = att.enumerateValues(); e.hasMoreElements(); ) {
			Object o = e.nextElement();
			System.out.println(o);

		}
		System.out.println();
	}

	private static Instances load(String pattern) throws Exception {
		final String path = getFileNames(DATA_PATH).stream().filter(s -> s.contains(pattern)).findFirst().orElseThrow();
		return loadDataset(path);
	}


	private static void printTreeStatistics(J48 tree, String label) {
		System.out.println(label);
		System.out.println(tree.toSummaryString());
	}

}
