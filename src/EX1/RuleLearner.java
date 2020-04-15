package EX1;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.JRip;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class RuleLearner {

	public static void main(final String[] args) throws Exception {
		final Set<String> files = getFileNames();
		final StringBuilder result = new StringBuilder(String.format("%-15s\t %-11s\t %-10s\t %-10s\n", "Dataset", "JRip", "JRip noPruning", "ConjunctiveRule"));
		final int[] totalRanks = {0, 0, 0};

		for (final String file : files) {

			System.out.println("###########################################################");
			System.out.println("Processing " + file);
			System.out.println("###########################################################");
			final Instances data = loadDataset(file);

			final JRip pruning = getRipper(data, true);
			final Evaluation evalPruning = new Evaluation(data);
			evalPruning.crossValidateModel(pruning, data, 10, new Random(0));
			System.out.println(pruning);
			System.out.println(evalPruning.pctCorrect());
			System.out.println("###########################################################");

			final JRip noPruning = getRipper(data, false);
			final Evaluation evalNoPruning = new Evaluation(data);
			evalNoPruning.crossValidateModel(noPruning, data, 10, new Random(0));
			System.out.println(noPruning);
			System.out.println(evalNoPruning.pctCorrect());
			System.out.println("###########################################################");

			final ConjunctiveRule rule = getConjunctiveRule(data);
			final Evaluation evalRule = new Evaluation(data);
			evalRule.crossValidateModel(rule, data, 10, new Random(0));
			System.out.println(rule);
			System.out.println(evalRule.pctCorrect());
			System.out.println("###########################################################");

			final List<Double> accs = List.of(evalPruning.pctCorrect(), evalNoPruning.pctCorrect(), evalRule.pctCorrect());
			final List<Double> sorted = accs.stream()
					                            .sorted(Comparator.reverseOrder())
					                            .collect(Collectors.toList());
			final List<Integer> ranks = accs.stream()
					                            .map(e -> sorted.indexOf(e) + 1)
					                            .collect(Collectors.toList());
			result.append(String.format("%-15s\t %f (%d)\t %f (%d)\t %f (%d)\n", data.relationName(),
					accs.get(0), ranks.get(0), accs.get(1), ranks.get(1), accs.get(2), ranks.get(2)));
			totalRanks[0] += ranks.get(0);
			totalRanks[1] += ranks.get(1);
			totalRanks[2] += ranks.get(2);
		}

		System.out.println();
		final double n = files.size();
		final double[] avgRanks = {totalRanks[0] / n, totalRanks[1] / n, totalRanks[2] / n};
		result.append("-------------------------------------------------------------------\n");
		result.append(String.format("%-15s\t %f \t\t %f \t\t %f\n", "AVG RANK",
				avgRanks[0], avgRanks[1], avgRanks[2]));
		System.out.println(result);

		performFriedmanNemenyiTests(avgRanks, n);
	}

	private static void performFriedmanNemenyiTests(final double[] avgRanks, final double n) {
		System.out.println("Perform the Friedman statistics test:");
		// Perform the Friedman statistics test
		// E.g. k = 3, N = 10
		// Chi²F = 12N / k(k+1)  *  ( SUM(avgRanks²) - k(k+1)² / 4 )
		// Chi²F = 12*10 / 3*4  *  ( SUM(avgRanks²) - 3*4 / 4 )
		// Chi²(0.95,2) = 5.991   -> see https://people.richland.edu/james/lecture/m170/tbl-chi.html

		final double k = avgRanks.length;
		final double sumAvgRanks2 = DoubleStream.of(avgRanks).map(a -> a * a).sum();
		final double chi2F = 12 * n / k * (k + 1) * (sumAvgRanks2 - k * (k + 1) * (k + 1) / 4);
		// TODO: create lookup Chi²(0.95,k-1)
		final double chi2 = 5.991;
		if (chi2 < chi2F) {
			System.out.println(String.format("χ²(0.95;2) = %f < %f = χ²F", chi2, chi2F));
			System.out.println("Null hypotheses successfully rejected with p = 0.95!");

			System.out.println("Perform the Nemenyi post-hoc test:");
			// Perform the Nemenyi post-hoc test (which can be performed if the null hypothesis of the Friedman is rejected)
			// q_alpha_0.05_#c3 = 2.343
			// TODO: lookup q_alpha for p and k
			final double q_alpha = 2.343;
			final double CD = q_alpha * Math.sqrt(k * (k + 1) / (6 * n)); // Critical Distance between pairs of avgRanks
			// TODO: compare avgRanks all pairs of classifiers and check significant difference by CriticalDistance CD
			System.out.println(String.format("CD = %f", CD));
		} else {
			System.out.println(String.format("χ²(0.95;2) = %f >= %f = χ²F", chi2, chi2F));
			System.out.println("Null hypotheses could NOT be rejected with p = 0.95!");
		}
	}

	private static ConjunctiveRule getConjunctiveRule(final Instances data) throws Exception {
		final ConjunctiveRule rule = new ConjunctiveRule();
		rule.buildClassifier(data);
		return rule;
	}

	private static Instances loadDataset(final String path) throws Exception {
		final Instances data = DataSource.read(path);

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

	private static JRip getRipper(final Instances data, final boolean usePruning) throws Exception {
		final JRip ripper = new JRip();
		ripper.setUsePruning(usePruning);
		ripper.buildClassifier(data);
		return ripper;
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
