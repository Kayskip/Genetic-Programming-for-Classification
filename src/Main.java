import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.jgap.gp.CommandGene;
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
	private static final int MAX_EVO = 200;

	private String nameFile;
	public static GPClassification problem;
	private GPConfiguration config;
	private ArrayList<Instance> trainingInstances;
	private ArrayList<Instance> testingInstances;

	/**
	 * This is where we will load our training file, test file and names.data file
	 * In this method we are initializing the lists to a new array of instances. From
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
	 * Creates the variables into a list of 9
	 * Loads the names file into variables
	 * 
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

		problem = new GPClassification(this.config, createVariables(this.config));
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
		GPGenotype gp = problem.create();
		gp.setVerboseOutput(true);
		gp.evolve(MAX_EVO);
		gp.setGPConfiguration(this.config);
		gp.outputSolution(gp.getAllTimeBest());
		problem.showTree(gp.getAllTimeBest(), "best-solution.png");
		new TestGPClassification(gp, testingInstances, trainingInstances);
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
}
