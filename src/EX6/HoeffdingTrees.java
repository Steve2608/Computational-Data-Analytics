package EX6;

import util.Util;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.RandomRBF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static util.Util.cvModel;


public class HoeffdingTrees {

	private static final int[] EXAMPLE_SIZES = {10, 100, 250, 500, 1000, 10_000, 50_000, 100_000};


	public static void main(String[] args) throws Exception {
		final ArrayList<HashMap<String, Double>> testResults = new ArrayList<>(EXAMPLE_SIZES.length);
		final RandomRBF randomGenerator = new RandomRBF();

		System.out.println("___STARTING___");
		for (int numExamples : EXAMPLE_SIZES) {

			final HashMap<String, Double> results = new HashMap<>();
			results.put("N_Samples", (double) numExamples);

			System.out.println(String.format("Dataset Size: %d", numExamples));
			randomGenerator.setNumExamples(numExamples);
			randomGenerator.defineDataFormat();
			// Get new Dataset
			final Instances examples = randomGenerator.generateExamples();
			examples.setClassIndex(examples.numAttributes() - 1);

			// Init timers
			final Timer j48Timer = new Timer();
			final Timer hoeffdingTimer = new Timer();

			Thread.sleep(4000);
			// Fetch new trees
			// Train on full set
			j48Timer.start();
			final J48 j48 = Util.getJ48(examples);
			j48Timer.stop();

			hoeffdingTimer.start();
			final HoeffdingTree hoeffdingTree = Util.getHoeffdingTree(examples);
			hoeffdingTimer.stop();

			//Reflection warning timing hack
			if (numExamples != 10) {
				results.put("J48_Time", j48Timer.getDiffInSeconds());
				results.put("Hoeffding_Time", hoeffdingTimer.getDiffInSeconds());

				results.put("J48_N_Leaves", j48.measureTreeSize());
				results.put("Hoeffding_N_Leaves", getNumLeaves(hoeffdingTree));

				// 10 Fold CV
				final Evaluation j48Eval = cvModel(j48, examples);
				final Evaluation hoeffdingEval = cvModel(hoeffdingTree, examples);

				results.put("J48_Acc", j48Eval.pctCorrect());
				results.put("Hoeffding_Acc", hoeffdingEval.pctCorrect());


				// Print info
				System.out.println("___________J48___________");
				System.out.println("Number of leaves: " + j48.measureNumLeaves());
				System.out.println("Accuracy (10-Fold):" + j48Eval.pctCorrect());
				System.out.println("Time to fit: " + j48Timer.getDiffString());
				System.out.println(j48.toString());

				System.out.println("________Hoeffding________");
				System.out.println("Number of leaves: " + getNumLeaves(hoeffdingTree));
				System.out.println("Accuracy (10-Fold):" + hoeffdingEval.pctCorrect());
				System.out.println("Time to fit: " + hoeffdingTimer.getDiffString());
				System.out.println(hoeffdingTree.toString());

				testResults.add(results);
			}
		}
		makeReport(testResults);
	}

	private static void makeReport(ArrayList<HashMap<String, Double>> testResults) {
		final StringBuilder sb = new StringBuilder(
				String.format("| %-20s | %-20s | %-20s | %-20s | %-20s | %-20s | %-20s|\n", "N_Samples",
						"J48_Acc (%)", "Hoeffding_Acc (%)", "J48_N_Leaves", "Hoeffding_N_Leaves", "J48_Time (s)",
						"Hoeffding_Time (s)"));

		sb.append("|----------------------|----------------------|----------------------|----------------------|----------------------|----------------------|---------------------|\n");

		testResults.forEach(map -> sb.append(
				String.format("| %-20d | %-20.3f | %-20.3f | %-20d | %-20d | %-20.3f | %-20.3f|\n", (int) (double) map.get("N_Samples"),
						map.get("J48_Acc"), map.get("Hoeffding_Acc"), (int) (double) map.get("J48_N_Leaves"), (int) (double) map.get("Hoeffding_N_Leaves"),
						map.get("J48_Time"), map.get("Hoeffding_Time")))
		);
		System.out.println(sb.toString());

	}

	private static void printj48TreeStats(final J48 tree, final Evaluation eval) {
		System.out.println(String.format("Size of Tree: %.0f", tree.measureTreeSize()));
		System.out.println(String.format("Accuracy: %.2f%%", eval.pctCorrect()));
		System.out.println();
	}

	private static double getNumNodes(final HoeffdingTree hoeffdingTree) {
		return hoeffdingTree.toString().split("\n").length - 1;
	}

	private static double getNumLeaves(final HoeffdingTree hoeffdingTree) {
		return Arrays.stream(hoeffdingTree.toString().split("\n")).filter(s -> s.contains("adaptive")).count();
	}

//	private static double getSize(final HoeffdingTree hoeffdingTree) {
//		try {
//			Class<? extends HoeffdingTree> cls = hoeffdingTree.getClass();
//			Field m_root = cls.getDeclaredField("m_root");
//			m_root.setAccessible(true);
//			HNode node = (HNode) m_root.get(hoeffdingTree);
//			System.out.println(node);
//			Class<? extends HNode> hNodeCls = node.getClass();
//			Class<HNode> superCls = (Class<HNode>) hNodeCls.getSuperclass();
//			Class<HNode> superSuperCls = (Class<HNode>) superCls.getSuperclass();
//
//			System.out.println(superCls);
//
//			Field nodeNumFld = superCls.getDeclaredField("m_nodeNum");
//			nodeNumFld.setAccessible(true);
//
//			int size = (int) nodeNumFld.get(node);
//			return size;
//		} catch (NoSuchFieldException | IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		return -1;
//	}

	private static final class Timer {

		private long startTime;
		private long endTime;

		public void start() {
			startTime = System.nanoTime();
		}

		public void stop() {
			endTime = System.nanoTime();
		}

		public long getDiff() {
			return endTime - startTime;
		}

		public double getDiffInSeconds() {
			return getDiff() * 10e-9;
		}

		public String getDiffString() {
			return String.format("%.3fs", getDiffInSeconds());
		}

		@Override
		public String toString() {
			return "Timer{" +
					"startTime=" + startTime +
					", endTime=" + endTime +
					"diff: " + (endTime - startTime) + "ns" +
					'}';
		}
	}

}
