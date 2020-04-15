package EX2;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.AddNoise;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import static util.Util.*;

public class NoisePruning {

	private static final int[] PERCENT = {5, 10, 25, 50, 75, 100};
	private static final String BEST_PERFORMING = "data/hypothyroid.arff";

	private NoisePruning() {
	}

	public static void main(final String[] args) throws Exception {
		final Instances data = loadDataset(BEST_PERFORMING);
		final AddNoise noise = new AddNoise();
		final String header = "#####################";

		for (final int percent : PERCENT) {
			System.out.println(header + "\n" + percent + "% Noise\n" + header + "\n");
			// apply noise
			final int attr = data.numAttributes();
			for (int i = 0; i < attr; i++) {
				if (!data.attribute(i).isNumeric()) {
					noise.addNoise(data, 0, percent, i, true);
				}
			}
			// get the tree classifier
			final J48 treeParam = getJ48Param(data);
			printTreeStats(treeParam, cvModel(treeParam, data));

			// parametrized trees with different confidence Factors
			final J48 best = new J48GridSearcher(
					new int[]{1, 2, 3, 4, 5},
					IntStream.rangeClosed(1, 50).mapToObj(i -> ((float) i) / 100).toArray(Float[]::new)
			).search(data);
			printTreeStats(best, cvModel(best, data));

			System.out.println();
			System.out.println();
		}
	}

	private static void printTreeStats(final J48 tree, final Evaluation eval) {
		System.out.println(String.format("Number of Rules: %.0f", tree.measureNumRules()));
		System.out.println(String.format("Size of Tree: %.0f", tree.measureTreeSize()));
		System.out.println("MinNumObj: " + tree.getMinNumObj());
		System.out.println("ConfidenceFactor: " + tree.getConfidenceFactor());
		System.out.println(String.format("Accuracy: %.2f%%", eval.pctCorrect()));
		System.out.println();
	}

	private static class J48GridSearcher {
		private final int[] minNumObj;
		private final float[] confidenceFactor;
		private final J48 tree = new J48();

		private J48GridSearcher(final int[] minNumObj, final float[] confidenceFactor) {
			this.minNumObj = Objects.requireNonNull(minNumObj).clone();
			this.confidenceFactor = Objects.requireNonNull(confidenceFactor).clone();
		}

		private J48GridSearcher(final int[] minNumObj, final Float[] confidenceFactor) {
			this.minNumObj = Objects.requireNonNull(minNumObj).clone();
			Objects.requireNonNull(confidenceFactor);

			this.confidenceFactor = new float[confidenceFactor.length];
			for (int i = 0; i < confidenceFactor.length; i++) {
				// hacky automatic unboxing
				this.confidenceFactor[i] = confidenceFactor[i];
			}
		}

		private J48 search(final Instances data, final boolean fitToData) throws Exception {
			Objects.requireNonNull(data, "data must not be null");
			// rest to false params
			int bestMinNumObj = -1;
			float bestConfidenceError = -1f;
			double bestAcc = -1;

			for (final int min : minNumObj) {
				tree.setMinNumObj(min);
				for (final float conf : confidenceFactor) {
					tree.setConfidenceFactor(conf);
					final Evaluation eval = cvModel(tree, data);
					final double acc = eval.pctCorrect();
					if (acc > bestAcc) {
						bestAcc = acc;
						bestMinNumObj = min;
						bestConfidenceError = conf;
					}
				}
			}

			final J48 best = new J48();
			best.setConfidenceFactor(bestMinNumObj);
			best.setConfidenceFactor(bestConfidenceError);
			if (fitToData) best.buildClassifier(data);
			return best;
		}

		private J48 search(final Instances data) throws Exception {
			return search(data, true);
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			final J48GridSearcher that = (J48GridSearcher) o;

			if (!Arrays.equals(minNumObj, that.minNumObj)) return false;
			return Arrays.equals(confidenceFactor, that.confidenceFactor);
		}

		@Override
		public int hashCode() {
			return 31 * Arrays.hashCode(minNumObj) + Arrays.hashCode(confidenceFactor);
		}

		@Override
		public String toString() {
			return "J48GridSearcher{minNumObj=" + Arrays.toString(minNumObj) + ", confidenceFactor=" + Arrays.toString(confidenceFactor) + "}";
		}
	}

}
