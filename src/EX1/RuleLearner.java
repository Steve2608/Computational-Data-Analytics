package EX1;

import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.JRip;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleLearner {

	public static void main(String[] args) throws Exception {
		final Set<String> files = getFileNames();
		for (String file : files) {
			System.out.println("Processing " + file);
			final Instances data = loadDataset(file);

			final JRip pruning = getRipper(data, true);
			System.out.println(pruning);
			System.out.println("###########################################################");

			final JRip noPruning = getRipper(data, false);
			System.out.println(noPruning);
			System.out.println("###########################################################");

			final ConjunctiveRule rule = getConjunctiveRule(data);
			System.out.println(rule);
			System.out.println("###########################################################");
		}

	}

	private static ConjunctiveRule getConjunctiveRule(Instances data) throws Exception {
		final ConjunctiveRule rule = new ConjunctiveRule();
		rule.buildClassifier(data);
		return rule;
	}

	private static Instances loadDataset(String path) throws Exception {
		final Instances data = DataSource.read(path);
		if (data.classIndex() == -1) data.setClassIndex(data.numAttributes() - 1);
		return data;
	}

	private static JRip getRipper(final Instances data, final boolean usePruning) throws Exception {
		final JRip ripper = new JRip();
		ripper.setUsePruning(usePruning);
		ripper.buildClassifier(data);
		return ripper;
	}

	private static int numberOfRules(JRip ripper) {
		return ripper.getRuleset().size();
	}

	private static Set<String> getFileNames() {
		return Arrays.stream(
				Objects.requireNonNull(Path.of("data")
						                       .toFile()
						                       .listFiles())
		).map(File::getAbsolutePath)
				       .filter(fileName -> fileName.endsWith(".arff"))
				       .collect(Collectors.toSet());
	}

}
