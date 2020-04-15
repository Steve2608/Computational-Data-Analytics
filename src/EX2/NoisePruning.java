package EX2;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.AddNoise;

import static util.Util.*;

public class NoisePruning {

	private static final int[] PERCENT = {5, 10, 25, 50, 75, 100};
	private static final String BEST_PERFORMING = "data/hypothyroid.arff";

	private NoisePruning() {
	}

	public static void main(final String[] args) throws Exception {
		final Instances data = loadDataset(BEST_PERFORMING);
		final AddNoise noise = new AddNoise();

		for (final int percent : PERCENT) {
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
			final J48[] trees = new J48[]{
					getJ48(data),
					getJ48(data, 0.1f),
					getJ48(data, 0.5f)
			};
			// cross validation
			for (final J48 tree : trees) {
				printTreeStats(tree, cvModel(tree, data));
			}
		}
	}

	private static void printTreeStats(final J48 tree, final Evaluation eval) {
		System.out.println("Number of Rules: " + tree.measureNumRules());
		System.out.println("Size of Tree: " + tree.measureTreeSize());
		System.out.println("Accuracy: " + eval.pctCorrect());
		System.out.println();
	}

	private static void printTreeStats(final J48 tree) {
		System.out.println("Number of Rules: " + tree.measureNumRules());
		System.out.println("Size of Tree: " + tree.measureTreeSize());
		System.out.println();
	}

}
