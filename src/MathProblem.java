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
 * @author karu
 * Student ID : 300417869
 */
public class MathProblem extends GPProblem {

	private GPConfiguration config;
	private Variable vx;

	/**
	 * @param config
	 * @param vx
	 * @throws InvalidConfigurationException
	 */
	public MathProblem(GPConfiguration config, Variable vx) throws InvalidConfigurationException {
		super(config);
		this.config = config;
		this.vx = vx;
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
	@Override
	public GPGenotype create() throws InvalidConfigurationException {
		Class[] types = { CommandGene.DoubleClass };
		Class[][] argTypes = { {}, };
		CommandGene[][] nodeSets = { { vx, new Pow(this.config, CommandGene.DoubleClass),
				new Multiply(this.config, CommandGene.DoubleClass), new Add(this.config, CommandGene.DoubleClass),
				new Divide(this.config, CommandGene.DoubleClass), new Subtract(this.config, CommandGene.DoubleClass),
				new Terminal(this.config, CommandGene.DoubleClass, 2.0d, 10.0d, true) } };

		return GPGenotype.randomInitialGenotype(config, types, argTypes, nodeSets, 20, true);
	}
}
