import java.util.ArrayList;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

/**
 * @author karu
 *
 */
public class InstanceFitnessFunction extends GPFitnessFunction {
	/**
	 * Training Instances parsed from the main class within the initConfig method
	 * These will be used in the evaluate fitness function method
	 */
	private ArrayList<Instance> trainingInstances;
	/**
	 * Minimal acceptance error
	 */
	private static final double MIN_ER = 0.01;
	/**
	 * Added so it stops errors
	 */
	private static final long serialVersionUID = -3244818378241139131L;

	/**
	 * @param trainingInstances
	 */
	public InstanceFitnessFunction(ArrayList<Instance> trainingInstances) {
		this.setTrainingInstances(trainingInstances);
	}

	/**
	 * Method extracted from original MathProblem class, altered to suit this class
	 * Overrides abstract method evaluate.
	 */
	@Override
	protected double evaluate(IGPProgram igpProgram) {
		double correct = 0;

		for (Instance instance : this.getTrainingInstances()) {
			Main.problem.setVariablesOfInstance(instance);

			double result = igpProgram.execute_double(0, new Object[0]);
			int predictedClass;

			if (result < 0) {
				predictedClass = 2;
			} else {
				predictedClass = 4;
			}
			if (predictedClass == instance.getCondition()) {
				correct++;
			}
		}
		if (correct < MIN_ER) {
			return 0;
		}
		return correct / this.getTrainingInstances().size();
	}

	/**
	 * @return training instances
	 */
	public ArrayList<Instance> getTrainingInstances() {
		return trainingInstances;
	}

	/**
	 * @param trainingInstances
	 */
	public void setTrainingInstances(ArrayList<Instance> trainingInstances) {
		this.trainingInstances = trainingInstances;
	}

}