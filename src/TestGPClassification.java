import java.util.ArrayList;

import org.jgap.gp.impl.GPGenotype;

/**
 * @author karu
 * Test class which reports accuracy results at the end of the evolution
 */
public class TestGPClassification {
	/**
	 * Constructor which is used in the main class, this is parsed the training and testing instances to be tested
	 * @param gp
	 * @param training
	 * @param testing
	 */
	public TestGPClassification(GPGenotype gp, ArrayList<Instance> training, ArrayList<Instance> testing) {
		this.testAlgorithm(gp, training, testing);
	}

	/**
	 * Method which uses the fitness function, evaluating it and return both the training and testing result accuracy.
	 * @param gp
	 * @param training
	 * @param testing
	 */
	private void testAlgorithm(GPGenotype gp, ArrayList<Instance> training, ArrayList<Instance> testing) {
		InstanceFitnessFunction fitnessFunction = new InstanceFitnessFunction(training);
		double result = fitnessFunction.evaluate(gp.getAllTimeBest()) * 100;
		result = 100 - result;
		System.out.println(
				"\nPercentage of training instances correctly classified: " + String.format("%.4f", result) + "%");

		fitnessFunction = new InstanceFitnessFunction(testing);
		result = fitnessFunction.evaluate(gp.getAllTimeBest()) * 100;
		result = 100 - result;
		System.out
				.println("\nPercentage of test instances correctly classified: " + String.format("%.4f", result) + "%");
	}

}
