
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

    public FibonacciHeap() {
        this.size = 0;
        this.min = null;
    }


    private FibonacciHeap(HeapNode node) {
        this.min = node;
        this.size = 1;
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
//        HeapNode inserted = new HeapNode(key);
//        FibonacciHeap heap = new FibonacciHeap(inserted);
//        this.meld(heap);
//        return inserted;
        size++;
        HeapNode inserted = new HeapNode(key);
        if (empty()) {
            min = inserted;
        } else {
            inserted.next = min.next;
            inserted.prev = min;
            min.next = inserted;
            inserted.next.prev = inserted;

            if (inserted.key < min.key) {
                min = inserted;
            }
        }

        return inserted;
    }

    /**
     * public void deleteMin()
     * <p>
     * Delete the node containing the minimum key.
     */
    public void deleteMin() {
        return; // should be replaced by student code
    }

    /**
     * public HeapNode findMin()
     * <p>
     * Return the node of the heap whose key is minimal.
     */
    public HeapNode findMin() {
        return min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * <p>
     * Meld the heap with heap2
     */
    public void meld(FibonacciHeap heap2) {
        if (heap2.empty()) {
            heap2.min = this.min;
            heap2.size = this.size;
            return;
        }
        if (empty()) {
            this.min = heap2.min;
            this.size = heap2.size;
        }

        HeapNode next = this.min.next;
        this.min.next = heap2.min;
        HeapNode prev = heap2.min.prev;
        this.min.next = heap2.min;
        heap2.min.prev = min;
        next.prev = prev;
        prev.next = next;

        this.size +=heap2.size;
        heap2.size = this.size;

        if(heap2.min.key < this.min.key) {
            this.min = heap2.min;
        }
        else {
            heap2.min = this.min;
        }
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
        int[] arr = new int[getMaxDegree() + 1];
        if(min == null) {
            return arr;
        }

        HeapNode curr = min;
        do {
            arr[curr.degree]++;
            curr = curr.next;
        } while(curr != min);

        return arr;
    }

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
     * <p>
     * Deletes the node x from the heap.
     */
    public void delete(HeapNode x) {
        int delta = x.key - min.key + 1;
        decreaseKey(x, delta);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        x.key = x.key - delta;
        HeapNode parent = x.parent;
        if(parent != null && parent.key > x.key) {
            cut(x, parent);
            cascadingCut(parent);
        }

        if(x.key < min.key) {
            min = x;
        }
    }


    private void cut(HeapNode node, HeapNode parent) {
        if(node.next == node) {
            parent.child = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }

        parent.degree --;

        addNodeToRootList(node);
    }

    private void addNodeToRootList(HeapNode node) {
        min.next.prev = node;
        node.next = min.next;
        min.next = node;
        node.prev = min;

        node.parent = null;
        node.marked = false;
    }

    private void cascadingCut(HeapNode node) {
        HeapNode parent = node.parent;
        if(parent != null) {
            if(!node.marked) {
                node.marked = true;
            } else {
                cut(node, parent);
                cascadingCut(parent);
            }
        }
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return getNumMarked(min) + getNumTrees();
    }

    private int getNumMarked(HeapNode node) {
        if(node == null) {
            return 0;
        }

        int numMarked = 0;
        HeapNode curr = node;
        do {
            if (curr.marked) {
                numMarked ++;
            }

            numMarked += getNumMarked(curr.child);
            curr = curr.next;
        } while(curr != node);

        return numMarked;
    }

    private int getNumTrees() {
        if (min == null) {
            return 0;
        }

        HeapNode curr = min;
        int count = 0;
        do {
            count ++;
            curr = curr.next;
        } while(curr != min);

        return count;
    }

    /**
     * public static int totalLinks()
     * <p>
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
     * <p>
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in
     * another file
     */
    public class HeapNode {
        int key;
        int degree;
        HeapNode next;
        HeapNode prev;
        HeapNode child;
        HeapNode parent;
        Boolean marked;

        private HeapNode(int key) {
            this.key = key;
            this.degree = 0;
            this.next = this;
            this.prev = this;
            this.child = null;
            this.parent = null;
            this.marked = false;
        }
    }
}
