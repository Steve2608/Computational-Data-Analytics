package EX1;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.rules.JRip;
import weka.core.Attribute;
import weka.core.Instances;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static util.Util.*;

public class RuleLearner {

	private static final String HEADER = "###########################################################";

	private RuleLearner() {
	}

	public static void main(final String[] args) throws Exception {
		final Set<String> files = getFileNames(DATA_PATH);
		final StringBuilder result = new StringBuilder(String.format("%-15s %-15s %-15s %-15s\n", "Dataset", "JRip", "JRip noPruning", "ConjunctiveRule"));
		final StringBuilder numberOfRules = new StringBuilder(String.format("| %-15s | %-15s | %-15s | %-15s |\n", "Dataset", "JRip", "JRip noPruning", "ConjunctiveRule"));
		numberOfRules.append("| --------------- | --------------- | --------------- | --------------- |\n");
		final StringBuilder numberOfConditions = new StringBuilder(String.format("| %-15s | %-15s | %-15s | %-15s |\n", "Dataset", "JRip", "JRip noPruning", "ConjunctiveRule"));
		numberOfConditions.append("| --------------- | --------------- | --------------- | --------------- |\n");
		final StringBuilder numberOfClasses = new StringBuilder(String.format("| %-15s | %-15s | %-15s | %-15s |\n", "Dataset", "JRip", "JRip noPruning", "ConjunctiveRule"));
		numberOfClasses.append("| --------------- | --------------- | --------------- | --------------- |\n");
		final StringBuilder defaultRule = new StringBuilder(String.format("| %-15s | %-15s | %-15s | %-20s |\n", "Dataset", "JRip", "JRip noPruning", "ConjunctiveRule"));
		defaultRule.append("| --------------- | --------------- | --------------- | --------------- |\n");
		final int[] totalRanks = {0, 0, 0};

		for (final String file : files) {
			System.out.println(HEADER);
			System.out.println("Processing " + file);
			System.out.println(HEADER);
			final Instances data = loadDataset(file);

			final JRip pruning = getRipper(data, true);
			final Evaluation evalPruning = cvModel(pruning, data);
			System.out.println(pruning);
			System.out.println(String.format("Accuracy= %.3f",evalPruning.pctCorrect()));
			System.out.println(HEADER);

			final JRip noPruning = getRipper(data, false);
			final Evaluation evalNoPruning = cvModel(noPruning, data);
			System.out.println(noPruning);
			System.out.println(String.format("Accuracy= %.3f",evalNoPruning.pctCorrect()));
			System.out.println(HEADER);

			final MyConjunctiveRule rule = getConjunctiveRule(data);
			final Evaluation evalRule = cvModel(rule, data);
			System.out.println(rule);
			System.out.println(String.format("Accuracy= %.3f",evalRule.pctCorrect()));
			System.out.println(HEADER);

			final List<Double> accs = List.of(evalPruning.pctCorrect(), evalNoPruning.pctCorrect(), evalRule.pctCorrect());
			final List<Double> sorted = accs.stream()
					                            .sorted(Comparator.reverseOrder())
					                            .collect(Collectors.toList());
			final List<Integer> ranks = accs.stream()
					                            .map(e -> sorted.indexOf(e) + 1)
					                            .collect(Collectors.toList());
			result.append(String.format("%-15s %f (%d)   %f (%d)   %f (%d)\n", data.relationName(),
					accs.get(0), ranks.get(0), accs.get(1), ranks.get(1), accs.get(2), ranks.get(2)));
			totalRanks[0] += ranks.get(0);
			totalRanks[1] += ranks.get(1);
			totalRanks[2] += ranks.get(2);

			numberOfRules.append(String.format("| %-15s | %-15s | %-15s | %-15s |\n", data.relationName(),
					pruning.getRuleset().size(), noPruning.getRuleset().size(), 1));

			numberOfConditions.append(String.format("| %-15s | %-15.2f | %-15.2f | %-15s |\n", data.relationName(),
					pruning.getRuleset().stream().mapToDouble(r -> r.size()).average().getAsDouble(),
					noPruning.getRuleset().stream().mapToDouble(r -> r.size()).average().getAsDouble(),
					rule.size()));

			numberOfClasses.append(String.format("| %-15s | %-15s | %-15s | %-15s |\n", data.relationName(),
					getClassCounts(pruning),
					getClassCounts(noPruning),
					getClassCounts(rule)
			));

			defaultRule.append(String.format("| %-15s | %-15s | %-15s | %-20s |\n", data.relationName(),
					getDefaultRule(pruning),
					getDefaultRule(noPruning),
					getDefaultRule(rule)));
		}

		System.out.println();
		final double n = files.size();
		final double[] avgRanks = {totalRanks[0] / n, totalRanks[1] / n, totalRanks[2] / n};
		result.append("---------------------------------------------------------------\n");
		result.append(String.format("%-15s %f        %f        %f\n", "AVG RANK",
				avgRanks[0], avgRanks[1], avgRanks[2]));
		System.out.println(result);
		System.out.println();
		System.out.println("Number of Rules per Classifier and Dataset:");
		System.out.println(numberOfRules);
		System.out.println();
		System.out.println("Average number of Conditions per Classifier and Dataset:");
		System.out.println(numberOfConditions);
		System.out.println();
		System.out.println("Number of Rules predicting certain Classes per Classifier and Dataset:");
		System.out.println(numberOfClasses);
		System.out.println();
		System.out.println("Default Rules per Classifier and Dataset:");
		System.out.println(defaultRule);
		System.out.println();

		performFriedmanNemenyiTests(avgRanks, n);

	}

	private static String getDefaultRule(MyConjunctiveRule rule) {
		if (!rule.hasAntds()) {
			String ruleString =  rule.toString().split("\n")[4];
			return String.format("Rule is default (%s)",ruleString);
		}
		else {
			return "-";
		}
	}

	private static String getDefaultRule(JRip jrip) throws NoSuchFieldException, IllegalAccessException {
		Field f = jrip.getClass().getDeclaredField("m_Class");
		f.setAccessible(true);
		Attribute ca = (Attribute) f.get(jrip);
		String defaultRule = jrip.getRuleset().stream().map(r -> (JRip.RipperRule) r).filter(r -> !r.hasAntds()).map(r -> r.toString(ca)).findFirst().orElse(null);
		if (defaultRule != null) {
			double[] simStats = jrip.getRuleStats(1).getSimpleStats(0);
			return defaultRule + " (" + simStats[0] + "/" + simStats[4] + ")";
		} else {
			return "-";
		}
	}

	private static String getClassCounts(MyConjunctiveRule rule) {
		String[] split = rule.toString().split("\n")[4].split("=");
		return split[split.length-1]+"=1";
	}

	private static String getClassCounts(JRip jrip) throws NoSuchFieldException, IllegalAccessException {
		Field f = jrip.getClass().getDeclaredField("m_Class");
		f.setAccessible(true);
		Attribute ca = (Attribute) f.get(jrip);
		List<String> classes = jrip.getRuleset().stream().map(r -> (JRip.RipperRule) r).map(r -> ca.value((int) r.getConsequent())).collect(Collectors.toList());
		String classCounts = new HashSet<>(classes).stream().map(s -> s + "=" + Collections.frequency(classes, s)).collect(Collectors.joining(", "));
		return classCounts;
	}

	// Perform the Friedman statistics test
	// E.g. k = 3, N = 10
	// Chi²F = 12N / k(k+1)  *  ( SUM(avgRanks²) - k(k+1)² / 4 )
	// Chi²F = 12*10 / 3*4  *  ( SUM(avgRanks²) - 3*4 / 4 )
	// Chi²(0.95,2) = 5.991   -> see https://people.richland.edu/james/lecture/m170/tbl-chi.html
	//
	// Perform the Nemenyi post-hoc test
	// q_alpha_0.05_#c3 = 2.343
	private static void performFriedmanNemenyiTests(final double[] avgRanks, final double n) {
		System.out.println("Perform the Friedman statistics test:");
		final double k = avgRanks.length;
		final double sumAvgRanks2 = DoubleStream.of(avgRanks).map(a -> a * a).sum();
		System.out.println(String.format("Sum(avgRanks²) = %f", sumAvgRanks2));
		final double chi2F = 12 * n / k * (k + 1) * (sumAvgRanks2 - k * (k + 1) * (k + 1) / 4);
		// TODO: Lookup chi2 for p and k
		final double chi2 = 5.991; // Chi²(0.95,k-1)
		if (chi2 < chi2F) {
			System.out.println(String.format("χ²(0.95;2) = %f < %f = χ²F", chi2, chi2F));
			System.out.println("Null hypotheses successfully rejected with p = 0.95!");

			System.out.println("Perform the Nemenyi post-hoc test: \n(which can be performed because the null hypothesis of the Friedman is rejected)");
			System.out.println("q_alpha_0.05_#c3 = 2.343 ");
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

}
