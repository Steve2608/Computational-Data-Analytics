package EX5;

import weka.associations.Apriori;
import weka.core.Instances;

import static util.Util.loadDataset;

public class AssociationRuleLearning {

	private static final String WEATHER_NOMINAL = "data/weather.nominal.arff";

	private static final int NUM_RULES = (int) 1e6;

	private AssociationRuleLearning() {
	}

	public static void main(final String[] args) throws Exception {
		final Instances data = loadDataset(WEATHER_NOMINAL);
		final Apriori apriori = getApriori();

		apriori.buildAssociations(data);
		// TODO keine Ahnung
	}

	private static Apriori getApriori(final double lowerBoundMinSupport, final double minMetric) {
		final Apriori apriori = new Apriori();
		apriori.setOutputItemSets(true);
		apriori.setNumRules(NUM_RULES);
		apriori.setLowerBoundMinSupport(lowerBoundMinSupport);
		apriori.setMinMetric(minMetric);
		return apriori;
	}

	private static Apriori getApriori() {
		return getApriori(0.1, 1.0);
	}
}
