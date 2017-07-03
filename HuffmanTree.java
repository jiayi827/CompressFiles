// CSE 143 Homework 8, Spring 2017, Jiayi Wang
import java.io.*;
import java.util.*;
// HuffmanTree class to generate code for compressing text file
// Decode the compressed file to its original text
public class HuffmanTree {
	public HuffmanNode overallRoot;
	private PriorityQueue<HuffmanNode> pq;
	
	// Takes an array of frequencies for each ascii character "counts" as param
	// Add ascii number and its frequency to the priority queue, ordered by frequency
	// Create a HuffmanTree object and initialize it with data in the priority queue
	public HuffmanTree(int[] counts) {
		pq = new PriorityQueue<HuffmanNode>();
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0) {      // only add characters that appear in the text
				HuffmanNode node = new HuffmanNode(i, counts[i]);
				pq.add(node);
			}
		}
		// Add the "EOF" node
		pq.add(new HuffmanNode(counts.length, 1));
		overallRoot = buildTree(overallRoot, pq);
	}
	
	// Takes the root node "root" and the priority queue "pq" and params
	// Returns the root node
	// Repeatedly remove the first two nodes in pq and combine their frequency to make a new node
	// Building the tree by placing the two nodes under the new node as left and right children
	// Add the new node back to the pq
	private HuffmanNode buildTree(HuffmanNode root, PriorityQueue<HuffmanNode> pq) {
		while (!pq.isEmpty()) {
			HuffmanNode node1 = pq.remove();
			int asc1 = node1.ascii;
			int count1 = node1.count;
			if (!pq.isEmpty()) {
				HuffmanNode node2 = pq.remove();
				int asc2 = node2.ascii;
				int count2 = node2.count;
				root = new HuffmanNode(-1, count1 + count2, node1, node2);
				pq.add(root);
			}
		}
		return root;
	}
	
	// Takes a PrintStream object output as param
	public void write(PrintStream output) {
		write(output, overallRoot, "");
	}
	
	// Takes a PrintStream object, the root node, and a string as params
	// Build the string by traversing the tree,
	// adding 0 when going left in the tree, adding 1 when going right
	private void write(PrintStream output, HuffmanNode root, String str) {
		if (root != null) {
			if (root.left == null && root.right == null) {
				output.println(root.ascii);
				output.println(str);
			}
			write(output, root.left, str + "0");
			write(output, root.right, str + "1");
		}
	}
	
	// Takes a standard code file formatted user input as param
	public HuffmanTree(Scanner input) {
		overallRoot = new HuffmanNode(-1, -1);
		overallRoot = load(input);
	}
	
	// Takes a standard code file formatted user input as param
	// Returns the root node
	// Repeatedly get the acsii number and path at each level
	// Build the left subtree if the binary code is 0 and left node does not exist
	// Build the right subtree if the binary code is 1 and right node does not exist
	private HuffmanNode load(Scanner input) {
		HuffmanNode node = overallRoot;
		if (!input.hasNextLine()) {
			return node;
		}
        int ascii = Integer.parseInt(input.nextLine());
        String path = input.nextLine();
        for (int i = 0; i < path.length() - 1; i++) {
	    	if (path.charAt(i) == '0') {
	    		if (node.left == null) {
	    			node.left = new HuffmanNode(-1, -1);
	    		}
	    		node = node.left;
	    	} else {
	    		if (node.right == null) {
	    			node.right = new HuffmanNode(-1, -1);
	    		}
	    		node = node.right;
	    	}
	    }
        // When reaching a leaf in the tree
    	if (path.charAt(path.length() - 1) == '0') {
    		node.left = new HuffmanNode(ascii, -1);
    	} else {
    		node.right = new HuffmanNode(ascii, -1);
    	}
    	node = load(input);
    	return node;
	}
	
	// Takes a BitInputStream object, a PrintStream object and an int eof as params
	// Read one bit from input. If not the end of file, 
	// moving in the tree until reaching a leaf. Output the character in the leaf node
	public void decode(BitInputStream input, PrintStream output, int eof) {		
		int bit = input.readBit();
	    HuffmanNode node = overallRoot;
        while (node.ascii != eof) {
        	if (node.left == null && node.right == null) {
        		PrintStream ps = new PrintStream(output);
        		ps.write(node.ascii);
        		node = overallRoot;
        	} else {
        		if (bit == 0) {
        			node = node.left;
        		} else if (bit == 1) {
        			node = node.right;
        		} 
        		bit = input.readBit();
        	}
        }
    }
	
	// Inner class for creating HuffmanNode objects
	// HuffmanNode are comparable based on count value
	private class HuffmanNode implements Comparable<HuffmanNode> {
	    public int ascii;    // ascii number stored at this node
	    public int count;     // number stored at this node
        public HuffmanNode left;    // reference to left subtree
        public HuffmanNode right;   // reference to right subtree

        // Constructs a leaf node with the given data and count.
        public HuffmanNode(int ascii, int count) {
            this(ascii, count, null, null);
        }

        // Constructs a leaf or branch node with the given data, count and links.
        public HuffmanNode(int ascii, int count, HuffmanNode left, HuffmanNode right) {
            this.ascii = ascii;
            this.count = count;
            this.left = left;
            this.right = right;
        }
        // Print the acsii value and its frequency stored in each node
        public String toString() {
        	return ascii + " " + count;
        }
        // Make the HuffmanNode comparable by their count values
        public int compareTo(HuffmanNode n) {
        	return this.count - n.count;
        }
	}
}
