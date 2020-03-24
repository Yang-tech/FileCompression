import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Huff implements ITreeMaker, IHuffEncoder, IHuffModel, IHuffHeader {

	private HuffTree huff;
	private MinHeap Hheap;
	private IHuffBaseNode root;
	private Map<Integer, String> map;
	private Map<Integer, Integer> countMap;
	private ICharCounter cc;
	private int tmpHeaderSize;
	private int headerSize;
	private int inputSize;
	private int outputSize;
	private int outSize;
	
	/**
     * Build the Huffman/coding tree.
     */
	@Override
	public HuffTree makeHuffTree(InputStream stream) throws IOException {
		cc = new CharCounter();
		inputSize = cc.countAll(stream);
		if(inputSize == 0) {
			throw new IllegalArgumentException();
		}
		countMap = cc.getTable();		
		//make a huff tree array
		HuffTree[] treeArray = new HuffTree[countMap.size() + 1];
		int i = 0;
		for(Map.Entry<Integer, Integer> kv: countMap.entrySet()) {
			treeArray[i++] = new HuffTree(kv.getKey(), kv.getValue());
		}
		//add the PSEUDO_EOF node
		treeArray[i] = new HuffTree(PSEUDO_EOF, 1);
		Hheap = new MinHeap(treeArray, treeArray.length, treeArray.length);	
		huff = buildTree();
		if(huff != null) {
			root = huff.root();
		}		
		return huff;		
	}
	
	/**
	 * build Huffman tree with the heap
	 * @return the HuffTree built with the heap
	 */
	public HuffTree buildTree() {
		HuffTree tmp1, tmp2, tmp3 = null;
		while (Hheap.heapsize() > 1) { // While two items left
			tmp1 = (HuffTree) Hheap.removemin();
			tmp2 = (HuffTree) Hheap.removemin();
			tmp3 = new HuffTree(tmp1.root(), tmp2.root(),
  	                             tmp1.weight() + tmp2.weight());
			Hheap.insert(tmp3);   // Return new tree to heap
  	  	}
		return tmp3;              // Return the tree
  	}
	
	/**
     * Initialize state from a tree, the tree is obtained
     * from the treeMaker.
     * @return the map of chars/encoding
     */
	@Override
	public Map<Integer, String> makeTable() {
		map = new HashMap<Integer, String>();
		//tmp header size is for calculating the output size before compressing
		tmpHeaderSize = BITS_PER_INT;
		//preorder traverse the tree
		traverseTree(root, "");
		return map;
	}
	
	/**
     * Traverse the huff tree with preorder traversal
     * @param root is the root of the tree to traverse
     * @param str is the code represents the path from root to a leaf
     */
	private void traverseTree(IHuffBaseNode root, String str) {
		if(root == null) {
			throw new IllegalArgumentException();
		}
		if(root.isLeaf()) {
			map.put(((HuffLeafNode)root).element(), str);
			tmpHeaderSize += 10;
			return;
		}
		tmpHeaderSize += 1;
		traverseTree(((HuffInternalNode)root).left(), str + "0");
		traverseTree(((HuffInternalNode)root).right(), str + "1");
	}

	/**
     * Returns coding, e.g., "010111" for specified chunk/character. It
     * is an error to call this method before makeTable has been
     * called.
     * @param i is the chunk for which the coding is returned
     * @return the huff encoding for the specified chunk
     */
	@Override
	public String getCode(int i) {
		return map.get(i);
	}

	/**
     * @return a map of all characters and their frequency
     */
	@Override
	public Map<Integer, Integer> showCounts() {
		return countMap;
	}

	/**
	 * count the (bit) size of the file header
	 */
	@Override
	public int headerSize() {		
		return this.headerSize;
	}

	/**
	 * Write the header, including magic number and all bits needed to
     * reconstruct a tree
	 */
	@Override
	public int writeHeader(BitOutputStream out) {
		// write out the magic number
		out.write(BITS_PER_INT, MAGIC_NUMBER);
		headerSize += BITS_PER_INT;
		traverseHuff(root, out);
		return headerSize;
	}
	
	/**
	 * make a preorder traversal of the huff tree to write the header after magic number, 
	 * if encounter an internal node, write a 0, if leaf node, write 1 and a 9-bits ASCII
	 * code to represent the character
	 * @param root is the root of the huff tree
	 * @param out is the output where we write the header to
	 */
	private void traverseHuff(IHuffBaseNode root, BitOutputStream out) {
		if(root == null) {
			throw new IllegalArgumentException();
		}
		if(root.isLeaf()) {
			//1 is for leaf node
			out.write(1, 1);
			//9 is for specific character
			out.write(9, ((HuffLeafNode)root).element());
			headerSize += 10;
			return;
		}
		//0 is for internal ndoe
		out.write(1, 0);
		headerSize += 1;
		traverseHuff(((HuffInternalNode)root).left(), out);
		traverseHuff(((HuffInternalNode)root).right(), out);
	}
	
	/**
     * Read the header and return an ITreeMaker object corresponding to
     * the information/header read.
     */
	@Override
	public HuffTree readHeader(BitInputStream in) throws IOException {
		int magic = in.read(BITS_PER_INT);
		if (magic != MAGIC_NUMBER){
		   throw new IOException("magic number not right");
		}
		//read the header and build the huff tree recursively
		HuffTree res = readTree(in);
		return res;
	}
	
	/**
	 * build the huff tree recursively, first read bit by bit, if read a 1, representing
	 * a leaf node, then read next 9 bits, else recursively read the child nodes
	 * @param in
	 * @return HuffTree built by the input stream
	 * @throws IOException
	 */
	private HuffTree readTree(BitInputStream in) throws IOException {
		//leaf node
		if(in.read(1) == 1) {
			int el = in.read(9);
			return new HuffTree(el, 0);
		}
		//internal node
		HuffTree l = readTree(in);
		HuffTree r = readTree(in);
		return new HuffTree(l.root(), r.root(), l.weight() + r.weight());
	}

	/**
     * Write a compressed version of the data read by the InputStream parameter
     */
	@Override
	public int write(String inFile, String outFile, boolean force) {
		//edge case check
		if(inFile == null || outFile == null) {
			throw new IllegalArgumentException();
		}
		if(inFile.length() == 0 || outFile.length() == 0) {
			throw new IllegalArgumentException();
		}
		BitInputStream input = new BitInputStream(inFile);		
		try {
			makeHuffTree(input);
			makeTable();
			int count = 0;
			//count the encoding size
			for(Map.Entry<Integer, Integer> kv: countMap.entrySet()) {
				count += map.get(kv.getKey()).length() * kv.getValue();
			}			
			String code = map.get(PSEUDO_EOF);
			int len = code.length();
			//add encoding size, header size and length of EOF together as output size
			outputSize = count + tmpHeaderSize + len;
			if(outputSize > (inputSize * BITS_PER_WORD) && !force) {
				return outputSize;
			}			
			BitOutputStream out = new BitOutputStream(outFile);
			writeHeader(out);
			//reset and read the file again to write the encoding
			input.reset();
			writeCode(input, out);			
			out.write(len, Integer.parseInt(code, 2));	
			out.close();
		} catch (IllegalArgumentException e) {			
			System.out.print("Your input is not valid");
		} catch (IOException e) {			
			e.printStackTrace();
		}			
		return outputSize;
	}
	
	/**
	 * write the encoding part of the compressed file
	 * @param input the stream of file to be compressed
	 * @param out the compressed output
	 */
	private void writeCode(BitInputStream input, BitOutputStream out) {
		try {
			int c;
			while((c = input.read()) != -1) {
				//get the encoding from encoding map
				String code = map.get(c);
				int len = code.length();
				out.write(len, Integer.parseInt(code, 2));		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * Uncompress a previously compressed file.
     */
	@Override
	public int uncompress(String inFile, String outFile) {
		BitInputStream input = new BitInputStream(inFile);	
		BitOutputStream out = new BitOutputStream(outFile);
		outSize = 0;
		HuffTree newHuff;
		try {
			newHuff = readHeader(input);	
			IHuffBaseNode root = newHuff.root();
			IHuffBaseNode cur = root;			
			int bits;
			while (true) {
				bits = input.read(1);			
				if (bits == -1) {
					throw new IOException("unexpected end of input file");
				}
				else {
					// use the zero/one value of the bit read to traverse Huffman coding tree
				    // if a leaf is reached, decode the character and print
				    // if the character is pseudo-EOF, decompression done
					if (cur.isLeaf()) {
						int element = ((HuffLeafNode)cur).element();
					    if (element == PSEUDO_EOF) {
					    	break; // out of loop
					    }	        
					    else {
					    	//write character stored in leaf-node
					    	out.write(BITS_PER_WORD, element);
					    	System.out.println((char)element);
					    	outSize += BITS_PER_WORD;
					    	cur = root;
					    }	        
				    }
					
					// read a 0, go left in tree
					if ((bits & 1) == 0) {
						cur = ((HuffInternalNode)cur).left();
					}
					// read a 1, go right in tree
					else {
						cur = ((HuffInternalNode)cur).right();
					}					
				}				
			}				
		} catch (IOException e) {
			System.out.println("Uncompressing Error");
		}		
		out.close();
		return outSize;
	}
}
