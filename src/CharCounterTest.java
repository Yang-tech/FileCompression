import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.junit.Test;

/**
 * 
 */

/**
 * @author yanya
 *
 */
public class CharCounterTest {

	/**
	 * test whether the Constructor can create a new CharCounter object
	 */
	@Test
	public final void testConstructor() {
		ICharCounter cc = new CharCounter();
		assertNotNull(cc);
	}
	

	/**
	 * create the new CharCounter object and test whether the method "countAll"
	 * can catch the correct IOException when input is invalid 
	 */
	@Test
	public final void testCountAllException() {		
		try {
			ICharCounter cc = new CharCounter();
			File file = new File("");
			InputStream in = new BitInputStream(file);
			cc.countAll(in);
		} catch (IOException e) {
			System.out.println("Your input is not valid");
		}
	}
	
	/**
	 * create the new CharCounter object and test the method "countAll"
	 * can return the correct value
	 */
	@Test
	public final void testCountAll() {
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			//test the size of count of input
			assertEquals(10, cc.countAll(in));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	

	/**
	 * create the new CharCounter object and test whether the method "getCount"
	 * can throw the correct FileNotFoundException when input is invalid 
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testGetCountException() {
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			cc.countAll(in);
			//error input larger than 256
			cc.getCount(300);
		} catch (IOException e) {
			System.out.println("Your input is not valid");
		}			
	}
	
	/**
	 * create the new CharCounter object and test the method "getCount"
	 * can return the correct value
	 */
	@Test
	public final void testGetCount(){		
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			cc.countAll(in);
			//check the count of specific character
			assertEquals(3, cc.getCount('t'));
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}

	/**
	 * create the new CharCounter object and test the method "add"
	 * can return the correct value
	 */
	@Test
	public final void testAdd(){
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			cc.countAll(in);
			assertEquals(3, cc.getCount('t'));
			//add one character
			cc.add('t');
			//test the count
			assertEquals(4, cc.getCount('t'));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * create the new CharCounter object and test the method "set"
	 * can return the correct value
	 */
	@Test
	public final void testSet() {		
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			cc.countAll(in);
			assertEquals(3, cc.getCount('t'));
			//set the count to specific value
			cc.set('t', 0);
			assertEquals(0, cc.getCount('t'));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * create the new CharCounter object and test the method "clear"
	 * can return the correct value
	 */
	@Test
	public final void testClear(){	
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			cc.countAll(in);
			assertEquals(3, cc.getCount('t'));
			assertEquals(1, cc.getCount('e'));
			assertEquals(2, cc.getCount('s'));
			cc.clear();
			assertEquals(0, cc.getCount('t'));
			assertEquals(0, cc.getCount('e'));
			assertEquals(0, cc.getCount('s'));
			//test the table size
			assertEquals(0, cc.getTable().size());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * create the new CharCounter object and test the method "getTable"
	 * can return the correct value
	 */
	@Test
	public final void testGetTable() {
		try {
			ICharCounter cc = new CharCounter();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			cc.countAll(in);
			//make a table manually to test
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			int a = 't';
			map.put(a, 3);
			a = 'e';
			map.put(a, 1);
			a = 's';
			map.put(a, 2);
			a = 'r';
			map.put(a, 1);
			a = 'i';
			map.put(a, 1);
			a = 'n';
			map.put(a, 1);
			a = 'g';
			map.put(a, 1);
			assertEquals(cc.getTable(), map);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
