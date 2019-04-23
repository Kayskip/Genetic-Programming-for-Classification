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
 * @author karu 
 * Student ID : 300417869
 *
 */
public class Main {
	/**
	 * Max evolutions we will allow
	 */
	private static final int MAX_EVO = 1000;
	/**
	 * Input size of our data set
	 */
	private static final int INPUT_SIZE = 20;

	private double x[];
	private double y[];
	private String nameFile;
	private Variable variable;
	private MathProblem problem;
	private GPConfiguration config;
	private ArrayList<Instance> trainingPatients;
	private ArrayList<Instance> testingPatients;

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

		this.trainingPatients = new ArrayList<Instance>();
		this.testingPatients = new ArrayList<Instance>();
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
			this.trainingPatients.add(new Instance(data));
		}

		try {
			scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(testFile)));
		} catch (NullPointerException e) {
			e.printStackTrace();
			return;
		}

		for (String line = scan.nextLine(); scan.hasNextLine(); line = scan.nextLine()) {
			String[] data = line.split(",");
			this.testingPatients.add(new Instance(data));
		}
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
		this.config.setFitnessFunction(new PatientFitnessFunction(this.trainingPatients));

		this.config.setMaxCrossoverDepth(8);
		this.config.setMaxInitDepth(4);
		this.config.setPopulationSize(1000);
		this.config.setStrictProgramCreation(true);
		this.config.setReproductionProb(0.2f);
		this.config.setCrossoverProb(0.9f);
		this.config.setMutationProb(35.0f);

		this.variable = Variable.create(this.config, "X", CommandGene.DoubleClass);

		this.problem = new MathProblem(this.config, this.variable);
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
		gp.outputSolution(gp.getAllTimeBest());
		this.problem.showTree(gp.getAllTimeBest(), "best-solution.png");
	}

	/**
	 * Launch the program if files exist and arguments are met
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Invalid usage. \nArguments: Filename");
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
	public class PatientFitnessFunction extends GPFitnessFunction {
		/**
		 * Training Patients parsed from the main class within the initConfig method
		 * These will be used in the evaluate fitness function method
		 */
		private ArrayList<Instance> trainingPatients;
		/**
		 * Minimal acceptance error
		 */
		private static final double MIN_ER = 0.001;
		/**
		 * 
		 */
		private final Object[] noArguments = new Object[0];
		/**
		 * Added so it stops errors
		 */
		private static final long serialVersionUID = -3244818378241139131L;

		public PatientFitnessFunction(ArrayList<Instance> trainingPatients) {
			this.trainingPatients = trainingPatients;
		}

		/**
		 * Method extracted from original MathProblem class, altered to suit this class
		 * Overrides abstract method evaluate Provide the variable X with the input
		 * number. See method create(), declaration of "nodeSets" for where X is
		 * defined.
		 */
		@Override
		protected double evaluate(IGPProgram igpProgram) {
			double totalError = 0;
			for (int i = 0; i < INPUT_SIZE; i++) {
				variable.set(x[i]);
				try {
					double result = igpProgram.execute_double(0, this.noArguments);
					totalError += Math.abs(result - y[i]);

					if (Double.isInfinite(totalError)) {
						return Double.MAX_VALUE;
					}
				} catch (ArithmeticException ex) {
					System.out.println("x = " + x[i]);
					System.out.println(igpProgram);
					throw ex;
				}
			}
			if (totalError < MIN_ER) {
				return 0;
			}
			return totalError;
		}
	}
}
