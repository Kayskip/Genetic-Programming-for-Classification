
/**
 * @author karu Student ID: 300417869
 */
public class Instance {

	private int[] attributes;
	private int id;
	private int condition;

	/**
	 * @param attributes
	 */
	public Instance(String[] attributes) {
		assert attributes.length == 11 : "Must have 11 attributes";
		this.attributes = new int[9];

		for (int i = 1; i < 10; i++)
			this.attributes[i - 1] = getInt(attributes[i]);

		this.id = getInt(attributes[0]);
		this.condition = getInt(attributes[10]);
	}

	/**
	 * Turns String into an integer. If s == "?" it turns it into 1 (as described in
	 * the hand out)
	 * 
	 * @param s
	 * @return
	 */
	private static int getInt(String string) {
		int i;
		try {
			i = Integer.valueOf(string);
		} catch (NumberFormatException e) {
			i = -1;
		}
		return i;
	}

	/**
	 * @return attributes
	 */
	public int[] getAttributes() {
		return attributes;
	}

	/**
	 * @return condition
	 */
	public int getCondition() {
		return condition;
	}

	/**
	 * @param attributes set
	 */
	public void setAttributes(int[] attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id set
	 */
	public void setId(int id) {
		this.id = id;
	}
}
