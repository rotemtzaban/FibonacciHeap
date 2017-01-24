/**
 * FibonacciHeap
 * <p>
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap {
    private static int totalLinks = 0;
    private static int totalCuts = 0;

    private int size;
    private HeapNode min;
    private int numMarked;
    private int numRoots;

    public FibonacciHeap() {
        this.size = 0;
        this.numMarked = 0;
        this.numRoots = 0;
        this.min = null;
    }

    /**
     * public boolean empty()
     * <p>
     * precondition: none
     * <p>
     * The method returns true if and only if the heap
     * is empty.
     */
    public boolean empty()

    {
        return size == 0;
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     */
    public HeapNode insert(int key) {
        HeapNode inserted = new HeapNode(key);
        if (empty()) {
            min = inserted;
        } else {
            // Insert the node after the minimum item of the heap
            inserted.next = min.next;
            inserted.next.prev = inserted;

            inserted.prev = min;
            min.next = inserted;

            if (inserted.key < min.key) {
                min = inserted;
            }
        }

        numRoots ++;
        size++;

        return inserted;
    }

    /**
     * public void deleteMin()
     * <p>
     * Delete the node containing the minimum key.
     */
    public void deleteMin() {

        if(empty()){
            return;
        }

        // Cutting the children of the minimum, inserting them to the root list
        while(min.child != null) {
            //cutting without increasing totalCuts
            cutInternal(min.child, min);
        }

        // Remove min from the root list
        min.next.prev  = min.prev;
        min.prev.next = min.next;

        numRoots --;

        // If there are other nodes make heap empty
        if(min.next == min) {
            min = null;
        } else {
            min = min.next;
            // Create an array with a larger size than the max possible degree, to contain the output roots
            consolidate();
        }

        size--;
    }

    private void consolidate() {
        HeapNode[] currentRoots = createRootList();
        HeapNode[] resultRoots = new HeapNode[getMaxPossibleRootDegree() + 1];
        for(HeapNode root : currentRoots) {
            HeapNode x = root;
            int degree = x.degree;
            // While there is a root with a same degree as x link the two roots
            while(resultRoots[degree] != null) {
                HeapNode y = resultRoots[degree];
                if(x.key > y.key) {
                    // Making sure x's key is smaller than y's key, by swapping them if it isn't
                    HeapNode temp = x;
                    x = y;
                    y = temp;
                }
                // linking the nodes together
                link(x, y);
                resultRoots[degree] = null;
                degree++;
            }

            resultRoots[degree] = x;
        }

        // Reset the root list, then add every root
        min = null;
        numRoots = 0;
        // For every root in the result roots array, insert it to the root list
        for (HeapNode root : resultRoots) {
            if(root != null) {
                addNodeToRootList(root);
                // Update the minimum if needed
                if(min == null || root.key < min.key) {
                    min = root;
                }
            }
        }
    }

    private int getMaxPossibleRootDegree() {
        // The maximum possible value of the degree is log(size) / log(phi) < log(size) / log(1.6)
        return (int) Math.ceil(Math.log(size) /  Math.log(1.6));
    }

    /**
    * Creates an array of all the current roots of the heap and returns it
     **/
    private HeapNode[] createRootList() {
        HeapNode[] rootList = new HeapNode[numRoots];
        int i = 0;
        //Iterate over the roots of the tree until returning to min and insert to the array
        HeapNode currNode = min;
        do {
            rootList[i] = currNode;
            currNode = currNode.next;
            i++;
        } while(currNode != min);

        return rootList;
    }

    /**
    * Linking 2 roots x,y together assuming x.key <= y.key
     **/
    private void link(HeapNode x, HeapNode y){
        FibonacciHeap.totalLinks++;
        y.parent = x;
        x.degree ++;

        if(x.child == null){
            // If x has no children, set y as x's child
            x.child = y;
            y.next = y;
            y.prev = y;
        }
        else {
            // If x has children, insert y to x's child list after x's first child
            y.next = x.child.next;
            y.next.prev = y;
            x.child.next = y;
            y.prev = x.child;
        }
    }

    /**
     * public HeapNode findMin()
     *
     * Return the node of the heap whose key is minimal.
     */
    public HeapNode findMin() {
        return min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Meld the heap with heap2
     */
    public void meld(FibonacciHeap heap2) {
        if (heap2.empty()) {
            return;
        }
        if (this.empty()) {
            this.min = heap2.min;
            this.size = heap2.size;
        }
        else {
            // Concatenate the root list of heap2 with current heap's root list
            concatenateNodeLists(this.min, heap2.min);
        }

        this.size += heap2.size;
        this.numRoots += heap2.numRoots;
        this.numMarked += heap2.numMarked;

        if(heap2.min.key < this.min.key) {
            this.min = heap2.min;
        }
    }

    /**
     * Concatenates the two node lists of node1 and node2
     * inserts the second node after the second node
     */
    private static void concatenateNodeLists(HeapNode node1, HeapNode node2) {
        // Insert the second node list between node1 and node1.next
        node1.next.prev = node2.prev;
        node2.prev.next = node1.next;

        node1.next = node2;
        node2.prev = node1;
    }
    /**
     * public int size()
     * <p>
     * Return the number of elements in the heap
     */
    public int size() {
        return size;
    }

    /**
     * public int[] countersRep()
     * <p>
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     */
    public int[] countersRep() {
        if(min == null) {
            return new int[1];
        }

        // Create an array of size maxDegree + 1 to contain the counters
        int[] counters = new int[getMaxDegree() + 1];

        HeapNode curr = min;
        do {
            // Update counter for every root of the heap
            counters[curr.degree]++;
            curr = curr.next;
        } while(curr != min);

        return counters;
    }

    /**
     * Returns the max degree of a node in the root list
     * assumes the heap isn't empty
     */
    private int getMaxDegree() {
        int max = 0;
        HeapNode curr = min;
        do {
            max = Math.max(max, curr.degree);
            curr = curr.next;
        } while(curr != min);

        return max;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     */
    public void delete(HeapNode x) {
        // Decrease the key of x to min.key - 1, so it will be the minimum key
        int delta = x.key - min.key + 1;
        decreaseKey(x, delta);
        // Delete the minimum, which is now x
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        x.key = x.key - delta;
        HeapNode parent = x.parent;
        //check if node isn't already a root, and if it's key changed to below it's parent's key
        if(parent != null && parent.key > x.key) {
            //cut the node from it's parent and perform cascading cuts
            cut(x, parent);
            cascadingCut(parent);
        }

        if(x.key < min.key) {
            min = x;
        }
    }

    /**
     * cuts a node from it's parent, adding the node to the root list, and increasing totalCuts
     * assumes node and parent aren't null
     * @param node - the node to be cut
     * @param parent - the nodes parents
     */
    private void cut(HeapNode node, HeapNode parent) {
        totalCuts++;
        cutInternal(node, parent);
    }

    /**
     * cuts a node from it's parent, without increasing totalCuts
     * @param node - the node to be cut
     * @param parent - the nodes parents
     */
    private void cutInternal(HeapNode node, HeapNode parent) {
        if (node.next == node) {
            parent.child = null;
        } else {
            //remove node from list, and replace the parent's child
            parent.child = node.next;
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }

        parent.degree --;

        addNodeToRootList(node);
    }

    /**
     * adds a node to the root list of the heap, appending it to the minimum of the heap
     * @param node
     */
    private void addNodeToRootList(HeapNode node) {
        //if heap is empty, set min to node
        if(min == null) {
            min = node;
            min.next = min;
            min.prev = min;
        }
        else {
            //put node after min in list
            node.next = min.next;
            min.next.prev = node;
            min.next = node;
            node.prev = min;
        }

        node.parent = null;
        node.setMarked(false);
        this.numRoots ++;
    }

    /**
     * perform cascading cuts on the node, if it's not a root and the node isn't marked mark it,
     * else cut the node from it's parent and perform cascading cuts on the parent
     */
    private void cascadingCut(HeapNode node) {
        HeapNode parent = node.parent;
        if (parent != null) {
            if (!node.marked) {
                node.setMarked(true);
            } else {
                cut(node, parent);
                cascadingCut(parent);
            }
        }
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return numMarked + numRoots;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the run-time of the program.
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
     * in its root.
     */
    public static int totalLinks() {
        return totalLinks; // should be replaced by student code
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return totalCuts; // should be replaced by student code
    }

    /**
     * public class HeapNode
     * A node of a fibonacci heap
     */
    public class HeapNode {
        int key;
        int degree;
        HeapNode next;
        HeapNode prev;
        HeapNode child;
        HeapNode parent;
        boolean marked;

        public HeapNode(int key) {
            this.key = key;
            this.degree = 0;
            this.next = this;
            this.prev = this;
            this.child = null;
            this.parent = null;
            this.marked = false;
        }

        /**
         * set's the node's marked field to mark, increasing or decreasing the heap's numMarked field if
         * marked has changed
         */
        private void setMarked(boolean mark) {
            if(this.marked != mark) {
                if(mark) {
                    numMarked++;
                }
                else {
                    numMarked --;
                }

                marked = mark;
            }
        }
    }
}
