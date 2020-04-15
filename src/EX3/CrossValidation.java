package EX3;

import util.Util;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.rules.JRip;
import weka.core.Instances;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

import java.util.*;
import java.util.stream.Collectors;

import static util.Util.DATA_PATH;
import static util.Util.cvModel;

public class CrossValidation {

	private static final long SEED = 0L;
	private static final int N_DATA_SETS = 5;
	private static final String HEADER = "###########################################################";

	private CrossValidation() {
	}

	public static void main(final String[] args) throws Exception {
		// Load 5 datasets
		final List<Instances> datasets = fetchBiggestDatasets(DATA_PATH, N_DATA_SETS);
		System.out.println(HEADER);
		System.out.println("Selected the following datasets: ");
		datasets.forEach(data -> System.out.println("Dataset" + data.relationName() + "Samples: " + data.size()));
		System.out.println();

		final StringBuilder sb = new StringBuilder(String.format("%-45s %15s %15s %15s %15s %15s %15s %15s %15s\n",
				"Dataset", "1x5 CV", "1x10 CV", "1x20 CV", "LOOCV", "Self", "10x10 CV", "5x2 CV", "Validation"));

		final HashMap<String, List<Double>> resultMap = new HashMap<>(N_DATA_SETS);

		for (final Instances data : datasets) {
			final ArrayList<Double> accs = new ArrayList<>();
			System.out.println(HEADER);
			System.out.println("Performing Cross Validation on Dataset" + data.relationName() + "Samples: " + data.size());
			final TTS tts = trainValSplit(data);
			// TODO tts.train.size()
			final int[] cvParams = {5, 10, 20, 50};
			for (final int folds : cvParams)
				accs.add(getCVAcc(tts, folds));

			accs.add(evalOnTrainSet(tts.train));

			final List<Double> _10x10CV = performMultiCV(10, 10, tts);
			double avg = _10x10CV.stream().mapToDouble(d -> d).average().orElse(-100);
			System.out.println("Average 10x10 CV Acc: " + avg);
			accs.add(avg);

			final List<Double> _5x2CV = performMultiCV(5, 2, tts);
			avg = _5x2CV.stream().mapToDouble(d -> d).average().orElse(-100);
			System.out.println("Average 5x2 CV Acc: " + avg);
			accs.add(avg);

			accs.add(evalOnValSet(tts));

			resultMap.put(data.relationName(), accs);
			sb.append(String.format("%-45s %14.3f%% %14.3f%% %14.3f%% %14.3f%% %14.3f%% %14.3f%% %14.3f%% %14.3f%%\n", data.relationName(),
					accs.get(0), accs.get(1), accs.get(2), accs.get(3), accs.get(4), accs.get(5), accs.get(6), accs.get(7)));
			System.out.println();
		}
		System.out.println(sb.toString());
	}

	private static List<Instances> fetchBiggestDatasets(final String rootPath, final int nSets) {
		return Util.getFileNames(rootPath).stream().map(path -> {
			try {
				return Util.loadDataset(path);
			} catch (final Exception e) {
				e.printStackTrace();
				return null;
			}
		}).sorted(Comparator.comparingInt((Instances inst) -> Objects.requireNonNull(inst).size()).reversed())
				       .limit(nSets)
				       .collect(Collectors.toList());
	}

	private static TTS trainValSplit(final Instances data) throws Exception {
		final StratifiableFolds sf = new StratifiableFolds(data, 2, SEED);
		return new TTS(sf.getFold(1), sf.getFold(2));
	}

	private static double getCVAcc(final TTS tts, final int folds, final long seed) throws Exception {
		System.out.println("Performing " + folds + "-fold CV");
		final JRip ripper = new JRip();
		ripper.setSeed(seed);
		final Evaluation eval = cvModel(ripper, tts.train, folds);
		final double acc = eval.pctCorrect();
		System.out.println(acc);
		return acc;
	}

	private static double getCVAcc(final TTS tts, final int folds) throws Exception {
		return getCVAcc(tts, folds, SEED);
	}

	private static double evalOnTrainSet(final Instances train) throws Exception {
		System.out.println("Performing Evaluation on Train set!");
		final JRip ripper = new JRip();
		ripper.setSeed(SEED);
		ripper.buildClassifier(train);
		final Evaluation eval = new Evaluation(train);
		eval.evaluateModel(ripper, train);
		final double acc = eval.pctCorrect();
		System.out.println(acc);
		return acc;
	}

	private static double evalOnValSet(final TTS tts) throws Exception {
		System.out.println("Performing Evaluation on Validation set!");
		final JRip ripper = new JRip();
		ripper.setSeed(SEED);
		ripper.buildClassifier(tts.train);
		final Evaluation eval = new Evaluation(tts.test);
		eval.evaluateModel(ripper, tts.test);
		final double acc = eval.pctCorrect();
		System.out.println(acc);
		return acc;
	}

	private static List<Double> performMultiCV(final int numCV, final int folds, final TTS tts) throws Exception {
		System.out.println("Performing: " + numCV + "x" + folds + "-Cross Validation");
		final List<Double> accs = new ArrayList<>(numCV);
		final Random r = new Random(SEED);
		for (int i = 0; i < numCV; i++) {
			System.out.println("Iter: " + (i + 1));
			accs.add(getCVAcc(tts, folds, r.nextLong()));
		}
		return accs;
	}

	private static final class TTS {
		private final Instances train;
		private final Instances test;

		private TTS(final Instances train, final Instances test) {
			this.train = Objects.requireNonNull(train);
			this.test = Objects.requireNonNull(test);
		}
	}

	private static class StratifiableFolds extends StratifiedRemoveFolds {

		private final Instances data;

		private StratifiableFolds(final Instances data, final int nFolds, final long seed) throws Exception {
			this.data = Objects.requireNonNull(data);
			setNumFolds(nFolds);
			setSeed(seed);
			setInvertSelection(false);
			setInputFormat(this.data);
		}

		private Instances getFold(final int fold) throws Exception {
			if (fold <= 0 || fold > getNumFolds())
				throw new IllegalArgumentException(String.valueOf(fold));
			setFold(fold);
			return useFilter(data, this);
		}
	}

}
