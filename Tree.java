/**
 * @author - Paul Baird-Smith 2017-2018
 * ppb366@cs.utexas.edu
 *
 * Class describing a generic layered data structure. This describes a
 * specific kind of directed acyclic graph, where the distance of a Tree 
 * from the root is well-defined (i.e. all paths that exist from the root to
 * the Tree are of the same length).
 *
 * Each Tree object holds a list of parent Trees and children Trees, as well
 * as a state, or value. The class supports adding and removing of children
 * and parents, and can also be flattened into an ArrayList, using the given
 * utility funtion.
 *
 */

import java.util.ArrayList;
import java.util.LinkedList;

public class Tree<K> {

    /**
     * Maintain parents and children of tree node
     */
    private ArrayList<Tree<K>> children;
    private ArrayList<Tree<K>> parents;
    private K state;

    /**
     * Initialize empty parent and children lists, and value
     */
    public Tree(K value) {
	children = new ArrayList<Tree<K>>();
	parents = new ArrayList<Tree<K>>();
	this.state = value;
    }

    /**
     * Assign and retrieve the value of the tree node
     */
    public K getState() { return this.state; }
    public void setState(K state) { this.state = state; }

    /**
     * Add a new child to the tree node
     *
     * @param t - New tree node to add to list of children
     */
    public void addChild(Tree<K> t) {
	this.children.add(t);
	t.parents.add(this);
    }

    /**
     * Remove a child from the tree node's children list
     *
     * @param t - Tree node to remove from list of children
     */
    public void removeChild(Tree<K> t) {
	t.parents.remove(this);
	this.children.remove(t);
    }


    /**
     * Add a new parent to the tree node
     *
     * @param t - New tree node to add to list of parents
     */
    public void addParent(Tree<K> t) {
	t.children.add(this);
	this.parents.add(t);
    }

    /**
     * Remove a parent from the tree node's parent list
     *
     * @param t - Tree node to remove from list of parents
     */
    public void removeParent(Tree<K> t) {
	this.parents.remove(t);
	t.children.remove(this);
    }

    /**
     * Retrieve the children of the tree node
     */
    public ArrayList<Tree<K>> getChildren() {
	return this.children;
    }

    /**
     * Set the list of children of the tree node to a new passed list
     *
     * @param children - The children of the tree node
     */
    public void setChildren(ArrayList<Tree<K>> children) {
	this.children = children;
    }

    /**
     * Retrieves the parents of the tree node
     */
    public ArrayList<Tree<K>> getParents() {
	return this.parents;
    }

    /**
     * Set the list of parents of the tree node to a new passed list
     *
     * @param children - The parents of the tree node
     */
    public void setParents(ArrayList<Tree<K>> parents) {
	this.parents = parents;
    }

    
    /**
     * Flattens the tree to make an iterable ArrayList
     *
     * @return - A flattened version of the Tree object
     */
    public ArrayList<Tree<K>> flatten() {

	// Keep track of tree roots to explore
        LinkedList<Tree<K>> toExplore = new LinkedList<Tree<K>>();

	// Maintain a list for the flattened tree
        ArrayList<Tree<K>> flat = new ArrayList<Tree<K>>();

	// Add this tree to the list of tree roots to explore
        toExplore.add(this);

	// Keep reading through all the trees, until none are left to explore
        while(toExplore.size() > 0) {
	    
	    // Obtain and remove the head of the list of tree roots to explore
            Tree<K> root = toExplore.poll();

	    // Add the root to the flattened list if it is not already in it
            if(!flat.contains(root)) {
                flat.add(root);
            }

	    // Add all children of the root to the list of trees to explore
            for(Tree<K> child: root.getChildren()) {
                toExplore.add(child);
            }

        }

        return flat;
    }
    public String toString(){
        return state.toString();
    }

}