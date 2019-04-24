
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
        for(int i = 1; i < 10; i++){
            this.attributes[i-1] = getInt(attributes[i]);
        }

        id = getInt(attributes[0]);
        condition = getInt(attributes[10]);
    }


    /**
     * Turns String <code>s</code> into an integer. If s == "?" it turns it into 1
     * @param s
     * @return
     */
    private static int getInt(String s) {
        int i;
        try {
            i = Integer.valueOf(s); 
        } catch (NumberFormatException e) {
            i = -1;
        }
        return i;
    }


	/**
	 * @return
	 */
	public int[] getAttributes() {
		return attributes;
	}

	/**
	 * @return
	 */
	public int getCondition() {
		return condition;
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(int[] attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
}
