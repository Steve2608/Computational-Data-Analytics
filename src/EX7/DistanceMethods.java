package EX7;

import util.Util;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.neighboursearch.BallTree;
import weka.core.neighboursearch.KDTree;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.datagenerators.classifiers.classification.RandomRBF;

import java.util.*;
import java.util.stream.Collectors;

public class DistanceMethods {

	private static final int N_CLASSES = 10;
	private static final int N_SAMPLES = 25_000;
	private static final int SEED = 872_442_345;
	private static final int MAX_NEIGHBORS = 50;

	private static final boolean ALL_DISTANCE_WEIGHTINGS = true;

	public static void main(final String[] args) throws Exception {
		final List<EvaluationTuple> times = compareExecutionTimes();
		final List<EvaluationTuple> accuracies = compareAccuracies(times);
		compareBest3(accuracies);
	}

	private static void compareBest3(final List<EvaluationTuple> accuracies) throws Exception {
		printSection("For the best three values for k, does a distance weight method further improve the accuracy?");
		final List<EvaluationTuple> mostAccurate = accuracies
				                                           .stream()
				                                           .sorted(Comparator.comparingDouble(EvaluationTuple::getAccuracy).reversed())
				                                           .limit(3)
				                                           .collect(Collectors.toList());
		for (final EvaluationTuple evaluationTuple : mostAccurate) {
			// no distance weighting
			evaluateNNAlgorithm(
					new IBk(evaluationTuple.getK()),
					generateExamples(),
					evaluationTuple.algorithm.getClass().getDeclaredConstructor().newInstance()
			);

			final IBk iBKinverse = new IBk();
			// set distance metric to inverted distance
			iBKinverse.setOptions(String.format("-I -K %d", evaluationTuple.getK()).split(" "));
			evaluateNNAlgorithm(
					iBKinverse,
					generateExamples(),
					evaluationTuple.algorithm.getClass().getDeclaredConstructor().newInstance()
			);

			final IBk iBKsimilarity = new IBk();
			// set distance metric to similar
			iBKsimilarity.setOptions(String.format("-F -K %d", evaluationTuple.getK()).split(" "));
			evaluateNNAlgorithm(
					iBKsimilarity,
					generateExamples(),
					evaluationTuple.algorithm.getClass().getDeclaredConstructor().newInstance()
			);
		}
	}

	private static List<EvaluationTuple> compareAccuracies(final List<EvaluationTuple> times) throws Exception {
		printSection("For the fastest search algorithm in the previous task, " +
				             "compare the accuracies for different numbers of nearest neighbors " +
				             "(in Weka: -K (KNN))");
		final NearestNeighbourSearch fastest = times
				                                       .stream()
				                                       .min(Comparator.comparingLong(EvaluationTuple::getExecutionTime))
				                                       .orElseThrow().getAlgorithm();
		final List<EvaluationTuple> accuracies = new ArrayList<>(MAX_NEIGHBORS);

		// used to plot accuracies
		final List<Double> noWeightingAccs = new ArrayList<>(MAX_NEIGHBORS);
		for (int neighbors = 1; neighbors <= MAX_NEIGHBORS; neighbors++) {
			final EvaluationTuple noWeighting = evaluateNNAlgorithm(
					new IBk(neighbors),
					generateExamples(),
					fastest.getClass().getDeclaredConstructor().newInstance()
			);
			accuracies.add(noWeighting);
			noWeightingAccs.add(noWeighting.getAccuracy());
		}
		System.out.println("Accuracies - WEIGHT_NONE");
		System.out.println(noWeightingAccs);

		if (ALL_DISTANCE_WEIGHTINGS) {
			final List<Double> inverseWeightingAccs = new ArrayList<>(MAX_NEIGHBORS);
			final List<Double> similarityWeightingAccs = new ArrayList<>(MAX_NEIGHBORS);

			for (int neighbors = 1; neighbors <= MAX_NEIGHBORS; neighbors++) {
				final IBk iBKinverse = new IBk();
				// set distance metric to inverted distance
				iBKinverse.setOptions(String.format("-I -K %d", neighbors).split(" "));
				final EvaluationTuple inverseWeighting = evaluateNNAlgorithm(
						iBKinverse,
						generateExamples(),
						fastest.getClass().getDeclaredConstructor().newInstance()
				);

				final IBk iBKsimilarity = new IBk();
				// set distance metric to inverted distance
				iBKsimilarity.setOptions(String.format("-F -K %d", neighbors).split(" "));
				final EvaluationTuple similarityWeighting = evaluateNNAlgorithm(
						iBKsimilarity,
						generateExamples(),
						fastest.getClass().getDeclaredConstructor().newInstance()
				);

				inverseWeightingAccs.add(inverseWeighting.getAccuracy());
				similarityWeightingAccs.add(similarityWeighting.getAccuracy());
			}

			// reprint to keep results together
			System.out.println("Accuracies - WEIGHT_NONE");
			System.out.println(noWeightingAccs);
			System.out.println("Accuracies - WEIGHT_INVERSE");
			System.out.println(inverseWeightingAccs);
			System.out.println("Accuracies - WEIGHT_SIMILARITY");
			System.out.println(similarityWeightingAccs);
		}

		return accuracies;
	}

	private static List<EvaluationTuple> compareExecutionTimes() throws Exception {
		printSection("Compare the classification results and the execution times with " +
				             "LinearNNSearch, KDTree and BallTree as nearestNeighbourSearchAlgorithm");
		final List<EvaluationTuple> times = new ArrayList<>(3);
		times.add(evaluateNNAlgorithm(new IBk(), generateExamples(), new LinearNNSearch()));
		times.add(evaluateNNAlgorithm(new IBk(), generateExamples(), new KDTree()));
		times.add(evaluateNNAlgorithm(new IBk(), generateExamples(), new BallTree()));
		return times;
	}

	private static EvaluationTuple evaluateNNAlgorithm(final IBk nnSearch, final Instances data,
	                                                   final NearestNeighbourSearch algorithm) throws Exception {
		return evaluateNNAlgorithm(nnSearch, data, algorithm, 10);
	}

	private static EvaluationTuple evaluateNNAlgorithm(final IBk nnSearch, final Instances data,
	                                                   final NearestNeighbourSearch algorithm, final int folds) throws Exception {
		final long start = System.currentTimeMillis();

		nnSearch.setNearestNeighbourSearchAlgorithm(algorithm);
		// build classifier on the whole data set?
		// nnSearch.buildClassifier(data);
		final Evaluation eval = Util.cvModel(nnSearch, data, folds);

		final long end = System.currentTimeMillis();
		return eval(eval, end - start, nnSearch, algorithm, folds);
	}

	private static Instances generateExamples() throws Exception {
		final RandomRBF rbf = new RandomRBF();
		rbf.setNumClasses(N_CLASSES);
		rbf.setNumExamples(N_SAMPLES);
		rbf.setSeed(SEED);

		// Needs to be called before generating examples
		rbf.defineDataFormat();
		final Instances data = Objects.requireNonNull(rbf.generateExamples());
		data.setClassIndex(N_CLASSES);
		return data;
	}

	private static EvaluationTuple eval(final Evaluation eval,
	                                    final long milliDuration,
	                                    final IBk nnSearch,
	                                    final NearestNeighbourSearch algorithm,
	                                    final int folds) {
		final EvaluationTuple result = new EvaluationTuple(algorithm, nnSearch.getKNN(), eval.pctCorrect(), milliDuration);

		final String separator = "-".repeat(10);
		final String distanceWeighting;
		switch (nnSearch.getDistanceWeighting().toString()) {
			case "1":
				distanceWeighting = "WEIGHT_NONE";
				break;
			case "2":
				distanceWeighting = "WEIGHT_INVERSE";
				break;
			case "4": // ?!
				distanceWeighting = "WEIGHT_SIMILARITY";
				break;
			default:
				throw new RuntimeException("distanceWeighting was not specified");
		}
		System.out.format("%s %s(k=%d, weighting=%s) %s\n",
				separator,
				algorithm.getClass().getSimpleName(),
				result.getK(),
				distanceWeighting,
				separator);
		System.out.println("Evaluation with " + folds + "-fold CV");
		System.out.println("Accuracy: " + result.getAccuracy());
		System.out.format(Locale.US, "Computation time in ms: %,d\n\n", milliDuration);

		return result;
	}

	private static void printSection(final String s) {
		final String header = "#".repeat(20);
		System.out.format("%s %s %s\n", header, s, header);
	}

	private static final class EvaluationTuple {
		private final NearestNeighbourSearch algorithm;
		private final int k;
		private final double accuracy;
		private final long executionTime;

		public EvaluationTuple(final NearestNeighbourSearch algorithm, final int k, final double accuracy, final long executionTime) {
			this.algorithm = algorithm;
			this.k = k;
			this.accuracy = accuracy;
			this.executionTime = executionTime;
		}

		public NearestNeighbourSearch getAlgorithm() {
			return algorithm;
		}

		public int getK() {
			return k;
		}

		public double getAccuracy() {
			return accuracy;
		}

		public long getExecutionTime() {
			return executionTime;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			final EvaluationTuple that = (EvaluationTuple) o;
			return k == that.k &&
					       Double.compare(that.accuracy, accuracy) == 0 &&
					       executionTime == that.executionTime &&
					       algorithm.equals(that.algorithm);
		}

		@Override
		public int hashCode() {
			return Objects.hash(algorithm, k, accuracy, executionTime);
		}
	}

}
