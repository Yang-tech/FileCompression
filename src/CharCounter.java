import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CharCounter implements ICharCounter, IHuffConstants{

	private int[] count;
	private Map<Integer, Integer> map;
	
	/**
	 * class Constructor
	 */
	public CharCounter() {
		//use array of ALPH_SIZE to store the count
		count = new int[ALPH_SIZE];
	}
	
	/**
     * Returns the count associated with specified character.
     * @param ch is the chunk/character for which count is requested
     * @return count of specified chunk
     * @throws IllegalArgumentException when input is not valid
     */
	@Override
	public int getCount(int ch) {
		if(ch < 0 || ch >= ALPH_SIZE) {
			throw new IllegalArgumentException();
		}
		//use array to store the count
		return this.count[ch];
	}

	/**
     * Initialize state by counting bits/chunks in a stream
     * @param stream is source of data
     * @return count of all chunks/read
     * @throws IOException if reading fails
     */
	@Override
	public int countAll(InputStream stream) throws IOException {
		InputStream input = new BitInputStream(stream);
		int c = input.read();
		int cnt = 0;
		while(c != -1) {
			//count of c
			this.count[c]++;
			//total count
			cnt++;
			c = input.read();			
		}
		input.close();
		return cnt;
	}

	/**
     * Update state to record one occurrence of specified chunk/character.
     * @param i is the chunk being recorded
     */
	@Override
	public void add(int i) {
		this.count[i]++;		
	}

	/**
     * Set the value/count associated with a specific character/chunk.
     * @param i is the chunk/character whose count is specified
     * @param value is # occurrences of specified chunk
     */
	@Override
	public void set(int i, int value) {
		this.count[i] = value;		
	}

	/**
     * All counts cleared to zero.
     */
	@Override
	public void clear() {
		//set the whole array of value 0
		Arrays.fill(this.count, 0);
	}

	/**
     * @return a map of all characters and their frequency
     */
	@Override
	public Map<Integer, Integer> getTable() {
		//translate the array to hash table
		map = new HashMap<>();
		for(int i = 0; i < ALPH_SIZE; i++) {
			if(this.count[i] != 0) {
				map.put(i, this.count[i]);
			}			
		}
		return map;
	}

}
