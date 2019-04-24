import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Variable;

/**
 * @author karu Student ID : 300417869
 *
 */
public class Main {
	/**
	 * Max evolutions we will allow
	 */
	private static final int MAX_EVO = 300;

	private String nameFile;
	private MathProblem problem;
	private GPConfiguration config;
	protected ArrayList<Instance> trainingInstances;
	protected ArrayList<Instance> testingInstances;

	/**
	 * This is where we will load our training file, test file and names.data file
	 * In this method we are initializing the lists to a new array of patients. From
	 * here we will scan the desired files and load them into their specified lists
	 * appropriately.
	 * 
	 * @param trainingFile
	 * @param testFile
	 * @param nameFile
	 */
	private Main(String trainingFile, String testFile, String nameFile) {

		this.trainingInstances = new ArrayList<Instance>();
		this.testingInstances = new ArrayList<Instance>();
		this.nameFile = nameFile;

		Scanner scan;
		try {
			scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(trainingFile)));
		} catch (NullPointerException e) {
			e.printStackTrace();
			return;
		}

		for (String line = scan.nextLine(); scan.hasNextLine(); line = scan.nextLine()) {
			String[] data = line.split(",");
			this.trainingInstances.add(new Instance(data));
		}

		try {
			scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(testFile)));
		} catch (NullPointerException e) {
			e.printStackTrace();
			return;
		}

		for (String line = scan.nextLine(); scan.hasNextLine(); line = scan.nextLine()) {
			String[] data = line.split(",");
			this.testingInstances.add(new Instance(data));
		}
	}

	/**
	 * @param config
	 * @return array of variables
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public Variable[] createVariables(GPConfiguration config) throws Exception {
		Variable[] variables = new Variable[9];

		Scanner scan;
		try {
			scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(this.nameFile)));
		} catch (NullPointerException e) {
			throw new RuntimeException("Invalid File Specified");
		}

		for (int i = 0; i < 9; i++) {
			String name = scan.nextLine();
			variables[i] = new Variable(config, name, CommandGene.DoubleClass);
		}

		return variables;
	}

	/**
	 * These will be the parameters and stopping values for our program. This is
	 * where we initialize the config and setup the relevant GPFitnessEvaluator and
	 * FitnessFunction.
	 * 
	 */
	private void initConfig() throws Exception {
		this.config = new GPConfiguration();
		this.config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
		this.config.setFitnessFunction(new InstanceFitnessFunction(this.trainingInstances));

		this.config.setMaxCrossoverDepth(8);
		this.config.setMaxInitDepth(4);
		this.config.setPopulationSize(1000);
		this.config.setStrictProgramCreation(true);
		this.config.setReproductionProb(0.2f);
		this.config.setCrossoverProb(0.9f);
		this.config.setMutationProb(35.0f);

		this.problem = new MathProblem(this.config, createVariables(this.config));
	}

	/**
	 * Get the best solution by evolution Start the computation with maximum 1000
	 * evolutions. if a satisfying result is found (fitness value almost 0), JGAP
	 * stops earlier automatically. Print the best solution so far to the console.
	 * Create a graphical tree of the best solution's program and write it to a PNG
	 * file. This calls upon inbuilt functions within the GPGenotype class
	 * 
	 * @throws Exception
	 */
	private void run() throws Exception {
		GPGenotype gp = this.problem.create();
		gp.setVerboseOutput(true);
		gp.evolve(MAX_EVO);
		gp.setGPConfiguration(this.config);
		gp.outputSolution(gp.getAllTimeBest());
		this.problem.showTree(gp.getAllTimeBest(), "best-solution.png");
		testAlgorithm(gp);
	}

	private void testAlgorithm(GPGenotype gp) {
		InstanceFitnessFunction fitnessFunction = new InstanceFitnessFunction(trainingInstances);
		double result = fitnessFunction.evaluate(gp.getAllTimeBest()) * 100; // convert incorrect to percentage
		result = 100 - result;
		System.out.println(
				"\nPercentage of training instances correctly classified: " + String.format("%.4f", result) + "%");

		fitnessFunction = new InstanceFitnessFunction(testingInstances);
		result = fitnessFunction.evaluate(gp.getAllTimeBest()) * 100; // convert incorrect to percentage
		result = 100 - result;
		System.out
				.println("\nPercentage of test instances correctly classified: " + String.format("%.4f", result) + "%");
	}

	/**
	 * Launch the program if files exist and arguments are met
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Invalid input. \nEnter correct amount of arguments");
			return;
		} else {
			Main main = new Main(args[0], args[1], args[2]);
			main.initConfig();
			main.run();
		}
	}

	/**
	 * @author karu
	 * 
	 *         Method evaluate extracted from original MathProblem class, altered to
	 *         work with main class
	 * 
	 */
	public class InstanceFitnessFunction extends GPFitnessFunction {
		/**
		 * Training Patients parsed from the main class within the initConfig method
		 * These will be used in the evaluate fitness function method
		 */
		private ArrayList<Instance> trainingInstances;
		/**
		 * Minimal acceptance error
		 */
		private static final double MIN_ER = 0.001;
		/**
		 * Added so it stops errors
		 */
		private static final long serialVersionUID = -3244818378241139131L;

		public InstanceFitnessFunction(ArrayList<Instance> trainingInstances) {
			this.setTrainingInstances(trainingInstances);
		}

		/**
		 * Method extracted from original MathProblem class, altered to suit this class
		 * Overrides abstract method evaluate Provide the variable X with the input
		 * number. See method create(), declaration of "nodeSets" for where X is
		 * defined.
		 */
		@Override
		protected double evaluate(IGPProgram igpProgram) {
			double correct = 0;

			for (Instance instance : this.getTrainingInstances()) {
				problem.setVariablesOfInstance(instance);

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

		public ArrayList<Instance> getTrainingInstances() {
			return trainingInstances;
		}

		public void setTrainingInstances(ArrayList<Instance> trainingInstances) {
			this.trainingInstances = trainingInstances;
		}
	}
}
