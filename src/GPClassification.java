import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Pow;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 * @author karu Student ID : 300417869
 */
public class GPClassification extends GPProblem {

	private GPConfiguration config;
	private Variable[] variables;

	/**
	 * @param config
	 * @param vx
	 * @throws InvalidConfigurationException
	 */
	public GPClassification(GPConfiguration config, Variable[] vx) throws InvalidConfigurationException {
		super(config);
		this.config = config;
		this.variables = vx;
	}

	/**
	 * This method is used for setting up the commands and terminals that can be
	 * used to solve the problem. Notice, that the variables types, argTypes and
	 * nodeSets correspond to each other: they have the same number of elements and
	 * the element at the i'th index of each variable corresponds to the i'th index
	 * of the other variables!
	 *
	 * @return GPGenotype
	 * @throws InvalidConfigurationException
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public GPGenotype create() throws InvalidConfigurationException {
		Class[] types = { CommandGene.DoubleClass };
		Class[][] argTypes = { {}, };

		CommandGene[] mathCommands = { new Add(config, CommandGene.DoubleClass),
				new Pow(this.config, CommandGene.DoubleClass), new Multiply(config, CommandGene.DoubleClass),
				new Divide(config, CommandGene.DoubleClass), new Subtract(config, CommandGene.DoubleClass),
				new Terminal(config, CommandGene.DoubleClass, -1.0d, 10.0d, true), };

		CommandGene[] allCommandGenes = new CommandGene[mathCommands.length + this.getVariables().length];

		for (int i = 0; i < this.getVariables().length; i++)
			allCommandGenes[i] = this.variables[i];

		for (int i = this.getVariables().length; i < allCommandGenes.length; i++)
			allCommandGenes[i] = mathCommands[i - this.getVariables().length];

		CommandGene[][] nodeSets = new CommandGene[2][allCommandGenes.length];
		nodeSets[0] = allCommandGenes;
		nodeSets[1] = new CommandGene[0];

		return GPGenotype.randomInitialGenotype(this.config, types, argTypes, nodeSets, 20, true);
	}

	/**
	 * @param instance
	 */
	public void setVariablesOfInstance(Instance instance) {
		int[] patientAttributes = instance.getAttributes();
		assert patientAttributes.length == this.getVariables().length;

		for (int i = 0; i < this.getVariables().length; i++) {
			this.getVariables()[i].set((double) patientAttributes[i]);
		}
	}

	/**
	 * @return variables
	 */
	private Variable[] getVariables() {
		return this.variables;
	}
}
