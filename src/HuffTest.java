import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 */

/**
 * @author yanya
 *
 */
public class HuffTest implements IHuffConstants{

	/**
	 * create the new huff object and test the method "makeHuffTree"
	 * can return the correct value
	 */
	@Test
	public final void testMakeHuffTree() {
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));		
			HuffTree huffTree = huff.makeHuffTree(in);			
			HuffTree testTree = buildTree();
			//test the size and the weight of the tree
			assertEquals(testTree.size(), huffTree.size());
			assertEquals(testTree.weight(), huffTree.weight());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//build a tree manually to test
	private HuffTree buildTree() {
		//build the tree step by step
		HuffTree testTree = new HuffTree(0, 0);
		IHuffBaseNode root = new HuffInternalNode(null, null, 11);
		((HuffInternalNode) root).setLeft(new HuffInternalNode(null, null, 4));
		((HuffInternalNode) root).setRight(new HuffInternalNode(null, null, 7));
		HuffInternalNode left = (HuffInternalNode)((HuffInternalNode) root).left();
		left.setLeft(new HuffInternalNode(null, null, 2));
		left.setRight(new HuffInternalNode(null, null, 2));
		((HuffInternalNode) left.left()).setLeft(new HuffLeafNode('r', 1));
		((HuffInternalNode) left.left()).setRight(new HuffLeafNode('e', 1));
		((HuffInternalNode) left.right()).setLeft(new HuffLeafNode('g', 1));
		((HuffInternalNode) left.right()).setRight(new HuffLeafNode('i', 1));
		HuffInternalNode right = (HuffInternalNode)((HuffInternalNode) root).right();
		right.setLeft(new HuffLeafNode('t', 3));
		right.setRight(new HuffInternalNode(null, null, 4));
		((HuffInternalNode) right.right()).setLeft(new HuffLeafNode('s', 2));
		((HuffInternalNode) right.right()).setRight(new HuffInternalNode(null, null, 2));
		((HuffInternalNode) ((HuffInternalNode) right.right()).right()).setLeft(new HuffLeafNode('n', 1));
		((HuffInternalNode) ((HuffInternalNode) right.right()).right()).setRight(new HuffLeafNode(PSEUDO_EOF, 1));
		testTree.setRoot(root);
		return testTree;
	}
	
	/**
	 * create the new Huff object and test whether the method "makeHuffTree"
	 * can throw the correct IllegalArgumentException when input is invalid 
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testMakeHuffTreeException() throws IOException {
		Huff huff = new Huff();
		//error input: empty string
		InputStream in = new ByteArrayInputStream("".getBytes("UTF-8"));		
		huff.makeHuffTree(in);
	}

	/**
	 * create the new Huff object and test whether the method "makeTable"
	 * can throw the correct IllegalArgumentException when input is invalid 
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testMakeTableException(){
		Huff huff = new Huff();		
		try {
			InputStream in = new ByteArrayInputStream("".getBytes("UTF-8"));	
			huff.makeHuffTree(in);
			huff.makeTable();
		} catch (IOException e) {
			System.out.print("Your input is not valid");
		}		
	}
	
	/**
	 * create the new huff object and test the method "makeTable"
	 * can return the correct value
	 */
	@Test
	public final void testMakeTable() {
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));		
			huff.makeHuffTree(in);		
			//make a table manually to test
			Map<Integer, String> map = new HashMap<Integer, String>();
			int a = 't';
			map.put(a, "10");
			a = 'e';
			map.put(a, "001");
			a = 's';
			map.put(a, "110");
			a = 'r';
			map.put(a, "000");
			a = 'i';
			map.put(a, "011");
			a = 'n';
			map.put(a, "1110");
			a = 'g';
			map.put(a, "010");
			map.put(PSEUDO_EOF, "1111");
			assertEquals(huff.makeTable(), map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * create the new huff object and test the method "getCode"
	 * can return the correct value
	 */
	@Test
	public final void testGetCode() {
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));				
			huff.makeHuffTree(in);		
			huff.makeTable();
			//check the encoding of a specific character
			int i = 't';
			assertEquals(huff.getCode(i), "10");
			i = 'n';
			assertEquals(huff.getCode(i), "1110");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create the new huff object and test the method "showCount"
	 * can return the correct value
	 */
	@Test
	public final void testShowCounts() {
		Huff huff = new Huff();
		InputStream in;
		try {
			in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			huff.makeHuffTree(in);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		huff.makeTable();
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
		//check if two map are the same
		assertEquals(huff.showCounts(), map);
	}
	
	/**
	 * create the new huff object and test the method "headerSize"
	 * can return the correct value
	 */
	@Test
	public final void testHeaderSize() {
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			huff.makeHuffTree(in);		
			huff.makeTable();
			//create the ByteArrayOutputStream
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			//construct a BitOutputStream from out
			//check the size of the header that was written
			huff.writeHeader(new BitOutputStream(out));
			assertEquals(huff.headerSize(), 119);
			out.close(); //close the stream
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * create the new huff object and test the method "writeHeader"
	 * can return the correct value
	 */
	@Test
	public final void testWriteHeader() {		
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			huff.makeHuffTree(in);		
			huff.makeTable();
			//create the ByteArrayOutputStream
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			//construct a BitOutputStream from out
			//check the size of the header that was written
			assertEquals(119, huff.writeHeader(new BitOutputStream(out)));
			out.close(); //close the stream
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * create the new huff object and test the method "readerHeader"
	 * can return the correct value
	 */
	@Test
	public final void testReadHeader() {
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			huff.makeHuffTree(in);		
			huff.makeTable();
			//create the ByteArrayOutputStream
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			//construct a BitOutputStream from out
			//write header
			huff.writeHeader(new BitOutputStream(out));
			//construct a BitInputStream from output stream
			ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());
			BitInputStream newIn = new BitInputStream(inStream);
			assertEquals(huff.readHeader(newIn).size(), 87);
			out.close(); //close the stream
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * create the new huff object and test the method "readerHeader"
	 * can catch the IOException
	 */
	@Test
	public final void testReadHeaderException() {
		try {
			Huff huff = new Huff();
			InputStream in = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
			huff.makeHuffTree(in);		
			huff.makeTable();
			//create the ByteArrayOutputStream
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			//construct a BitOutputStream from out
			//write header
			huff.writeHeader(new BitOutputStream(out));
			//read a file with wrong magic number
			huff.readHeader(new BitInputStream(in));
			out.close(); //close the stream
		} catch (IOException e) {
			System.out.println("magic number not right");
		}
	}
	
	/**
	 * create the new Huff object and test whether the method "write"
	 * can throw the correct IllegalArgumentException when input is invalid 
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testWriteException1(){
		Huff huff = new Huff();		
		boolean force = true;
		//error input: empty string (file path)
		huff.write("", "/Users/yanya/Desktop/testOut.txt", force);
	}
	
	/**
	 * create the new Huff object and test whether the method "write"
	 * can throw the correct IllegalArgumentException when input is invalid 
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testWriteException2(){
		Huff huff = new Huff();		
		boolean force = true;
		//error input: null (file path)
		huff.write("/Users/yanya/Desktop/testIn.txt", null, force);
	}
	
	/**
	 * create the new huff object and test the method "write"
	 * can return the correct value when compress is forced
	 */
	@Test
	public final void testWriteForce() {
		Huff huff = new Huff();		
		boolean force = true;
		int size = huff.write("/Users/yanya/Desktop/testIn.txt", "/Users/yanya/Desktop/testOut.txt", force);
		//test the size of write
		assertEquals(151, size);
	}
	
	/**
	 * create the new huff object and test the method "write"
	 * can return the correct value when compress is unforced
	 */
	@Test
	public final void testWriteUnforce() {
		Huff huff = new Huff();		
		boolean force = false;
		int size = huff.write("/Users/yanya/Desktop/testIn.txt", "/Users/yanya/Desktop/testOut.txt", force);
		//test the size of write
		assertEquals(151, size);
	}
	
	/**
	 * create the new huff object and test the method "uncompress"
	 * can return the correct value
	 */
	@Test
	public final void testUncompress() {
		Huff huff = new Huff();	
		boolean force = true;
		System.out.println(huff.write("/Users/yanya/Desktop/testIn.txt", "/Users/yanya/Desktop/testOut.txt", force));
		int size = huff.uncompress("/Users/yanya/Desktop/testOut.txt", "/Users/yanya/Desktop/uncompress.txt");
		//test the uncompressed size
		assertEquals(80, size);
	}
}
