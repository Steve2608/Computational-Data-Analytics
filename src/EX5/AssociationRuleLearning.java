package EX5;

import weka.associations.Apriori;
import weka.core.Instances;

import static util.Util.loadDataset;

public class AssociationRuleLearning {

	private static final String WEATHER_PATH = "data/weather.nominal.arff";
	private static final String SUPERMARKET_PATH = "data/supermarket.arff";
	private static Instances WEATHER;
	private static Instances SUPERMARKET;

	private static final int NUM_RULES = (int) 1e6;

	static {
		try {
			WEATHER = loadDataset(WEATHER_PATH);
			SUPERMARKET = loadDataset(SUPERMARKET_PATH);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static void printApriori(final Apriori apriori) {
		System.out.println(apriori);
		System.out.println(apriori.getAssociationRules().getRules().size());
	}

	private static Apriori getApriori(final Instances data, final double lowerBoundMinSupport, final double minMetric) throws Exception {
		final Apriori apriori = new Apriori();
		apriori.setOutputItemSets(true);
		apriori.setNumRules(NUM_RULES);
		apriori.setLowerBoundMinSupport(lowerBoundMinSupport);
		apriori.setMinMetric(minMetric);

		apriori.buildAssociations(data);

		return apriori;
	}

	private static Apriori getApriori(final Instances data, final int lowerBoundMinSupport, final double minMetric) throws Exception {
		return getApriori(data, (double) lowerBoundMinSupport / data.size(), minMetric);
	}

	private AssociationRuleLearning() {
	}

	private static Apriori getApriori(final Instances data) throws Exception {
		return getApriori(data, 0.1, 1.0);
	}

	private static final class EX_5A {
		public static void main(final String[] args) throws Exception {
			printApriori(getApriori(WEATHER));
		}
	}

	private static final class EX_5B {
		public static void main(final String[] args) throws Exception {
			printApriori(getApriori(WEATHER, 2, 0.75));
		}
	}

	private static final class EX_5C {
		public static void main(final String[] args) throws Exception {
			printApriori(getApriori(SUPERMARKET, 0.25, 0.75));
		}
	}
}
