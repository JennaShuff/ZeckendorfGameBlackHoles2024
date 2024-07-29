/**
 * @author - Paul Baird-Smith 2017-2018
 * ppb366@cs.utexas.edu
 *
 * Describes a GameState in the Zeckendorf Decomposition according to (Epstein 2018),
 * en route to publication at the time of writing. Each GameState object maintains a
 * decomposition state, which is a map keyed by indeces of Fibonacci numbers, whose
 * values are the frequencies of the respective Fibonacci numbers.
 *
 * Maintains a win value, a depth, and an n. The win value is set to 0 originally,
 * and is then assigned to 1 for a Player 1 victory or a -1 for a Player 2 victory.
 * The depth represents the number of moves taken to reach the GameState object from
 * the original GameState, defined by the number n of starting 1s.
 *
 * Applying moves 1, 2, or 3 to a GameState object yields a new GameState. If the
 * move is invalid according to the Decomposition game, the original GameState object
 * is returned, otherwise the rseult of applying the move to the state is returned.
 *
 *      Move 1 consists of summing 2 consecutive indices together.
 *      Move 2 consists of splitting 2 Fibonaccis of the same index into 2 new
 *             Fibonaccis
 *      Move 3 consists of summing two 1s together to make a 2
 *      Move 4 consists of summing 2 indices into the black hole
 *      Move 5 consists of splitting into the black hole
 *
 * The genChildren() method creates a list of all possible states that follow the 
 * GameState object. This list is obtained by applying all valid moves to a GameState.
 *
 * Utility methods are also provided to determine whether or not a GameState is
 * terminal (no valid moves can be taken) or if it is equivalent, in depth and state,
 * to another GameState object.
 *
 */

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class GameState {


    /**
     * GameState wraps the win value, depth, and state of a state in the fib
     * decomposition game.
     *
     * depth is the number of moves played up to this GameState object
     * winVal determines the winner of the game to this point
     *      1 - Player 1 wins the game
     *      0 - Winner undetermined
     *     -1 - Player 2 wins the game
     * state is the current frequencies of each fibonacci in the decomposition
     */
    private int winVal, depth, n;
    private ArrayList<Integer> state;
    private int hole;


    //===================================
    // GAME STATE CONSTRUCTORS
    //===================================

    /**
     * Constructs a GameState with its current state and depth
     *
     * @param state - The current state of the decomposition
     * @param depth - The number of moves played up to the state
     */
    public GameState(ArrayList<Integer> state, int depth) {
	this.state = state;
	this.depth = depth;
	this.winVal = 0;
    this.hole = 3;

	int sum = 0;
	for(int i = 0; i<state.size(); i++) {
	    sum += state.get(i);
	}
	this.n = sum;
    }


    /**
     * Constructs a GameState with its current state, depth, and win value
     *
     * @param state  - The current state of the decomposition
     * @param depth  - The number of moves played up to the state
     * @param winVal - Determines winner of the game at this GameState
     */
    public GameState(ArrayList<Integer> state, int depth, int winVal) {
	this.state = state;
	this.depth = depth;
	this.winVal = winVal;
    this.hole = 3;

	int sum = 0;
	for(int i = 0; i<state.size(); i++) {
	    sum += state.get(i);
	}
	this.n = sum;
    }


    /**
     * Constructs a GameState for the first state in the fibonacci decomposition
     * game.
     *
     * @param a - The number of starting 1s in the fibonacci decomposition
     */
    public GameState(List<Integer> setup) {
	this.depth = 0;
    this.hole = 3;
    
	
	this.state = new ArrayList<Integer>(setup);

	this.winVal = 0;
    this.n = setup.get(0)+setup.get(1)+setup.get(2);
    }

    public GameState(int n) {
        this.depth = 0;
        this.hole = 3;
    
        
        this.state = new ArrayList<Integer>(3);
        this.state.set(0, n);
        this.winVal = 0;
        this.n = n;
    }

    //===================================
    // GETTERS AND SETTERS
    //===================================

    /**
     * Getter and setter for the current fibonacci decomposition
     */
    public ArrayList<Integer> getState() { return this.state; }
    
    public void setState(ArrayList<Integer> newState) {
	    this.state = newState;
    }


    /**
     * Getter and setter for the depth of the GameState
     */
    public int getDepth() { return this.depth; }
    public void setDepth(int depth) { this.depth = depth; }


    /**
     * Getter and setter for the win value of the GameState
     */
    public int getVal() { return this.winVal; }
    public void setVal(int val) { this.winVal = val; } 

    /**
     * Getter and setter for the n value of the GameState
     */
    public int getN() { return this.n; }
    public void setN(int n) { this.n = n; }


    /**
     * Returns a deep copy of the current GameState object
     */
    public GameState stateDeepCopy() {
        ArrayList<Integer> newState = new ArrayList<Integer>(0);
        for(int i = 0; i<state.size(); i++) {
            newState.add(state.get(i));
        }
        return new GameState(newState, this.depth, this.winVal);
    }


    //===================================
    // MOVE MAKING METHODS
    //===================================

    /**
     * Make a move to sum two consecutive fibs at ind and ind + 1, if there at least 1 of each
     *
     * @param ind - The index of the smaller of the 2 consecutive fibonaccis to sum
     * @return The state obtained by applying move 1 at the passed index
     */
    public GameState move1(int ind) {

        // Make a copy of the current state
        ArrayList<Integer> newState = this.stateDeepCopy().getState();

        //Define the hole

        // Make sure you have the fibonaccis at the required indeces and
        //not at hole
        if (ind + 1 >= hole){
            return this;
        }
        if (newState.get(ind) < 1 || newState.get(ind + 1) < 1) {
            return this;
        }


        // Get frequency of fibonaccis at indeces ind and ind + 1
        int freq1 = newState.get(ind);
        int freq2 = newState.get(ind + 1);

        if (freq1 > 0 && freq2 > 0) {
	    // Remove one from the frequencies of the two fibonaccis being summed
            newState.set(ind, freq1 - 1);
            newState.set(ind + 1, freq2 - 1);

            // Add a new fibonacci at ind + 2
            int curCount = 0;
            if(ind+2 < hole) {
                curCount = newState.get(ind + 2);
                newState.set(ind + 2, curCount + 1);
            }
            
        }

        return new GameState(newState, this.depth + 1);
    }

    //Splitting 1s.
    public GameState move2() {

	// Make a deep copy of the state of the current GameState
        ArrayList<Integer> newState = this.stateDeepCopy().getState();

	// Check that the GameState has a key for 1
        if (newState.size() == 0) {
            return this;
        }

	// If the GameState does not have a key for 2, create one
        if (newState.size() < 2) {
            newState.add(2, 0);
        }
    
	// Get the frequency of 1 and 2 in the decomposition
        int freqOnes = newState.get(0);
        int freqTwos = newState.get(1);
	
	// Check that there are enough 1s to make a 2 and perform the move
        if (freqOnes >= 2) {
            newState.set(0, freqOnes - 2);
            newState.set(1, freqTwos + 1);
        }

        return new GameState(newState, this.depth + 1);

    }


    /**
     * Make a move to take two fibs of the same index and split into 2 new fibs
     *
     * @param ind - Index of the fibonacci to split
     * @return The state obtained by applying move 3 at the passed index
     */
    public GameState move3(int ind) {
       
	// Make a deep copy of the state of the current GameState
        ArrayList<Integer> newState = this.stateDeepCopy().getState();

	// If index is too small, or there are not enough of the given fibonacci,
    //or if it gets sucked into blackhole
	// return the original state
        if (ind < 1 || ind >= hole) {
            return this;
        }
        if (newState.get(ind) < 2) {
            return this;
        }
	
	// Special edge case, where we are splitting up two 2s
        if (ind == 1 && newState.get(ind) >= 2) {
            if(newState.size() == 2 && hole > 2){
                newState.add(0);
            }
	    // Get the frequency of 1s, 2s, and 3s
            int freq2 = newState.get(ind);
            int freq1 = newState.get(ind - 1);
            int freq3 = newState.get(ind + 1);
            

	    // Update counts of 1s, 2s, and 3s appropriately
            newState.set(ind, freq2 - 2);
            newState.set(ind - 1, freq1 + 1);
            newState.set(ind + 1, freq3 + 1);
            

            return new GameState(newState, this.depth + 1);

        }

	// Verify that all the necessary keys exist
        if (newState.size() <= ind+1 && newState.size() < hole) {
            newState.add(0);
        }

	// Get the frequency for the fibonacci to split, the one below
	// and the one above
        int freq = newState.get(ind);
        int freqLowerNum = newState.get(ind - 2);
        int freqHigherNum = 0;
        if(ind+1 < hole){
            freqHigherNum = newState.get(ind + 1);
        }

	// Update frequencies of the split fibonaccis, and the fibonaccis
	// these were split into
        newState.set(ind, freq - 2);
        newState.set(ind - 2, freqLowerNum + 1);
        if(ind+1 < hole){
            newState.set(ind + 1, freqHigherNum + 1);
        }

	//System.out.println("Split " + fib(ind) + "s at depth " + (this.depth + 1));
        return new GameState(newState, this.depth + 1);

    }
    
    //===================================
    // IDENTIFIER METHODS
    //===================================

    /**
     * Verify that the proposed state is different from the current state
     *
     * @param nextState - The next state of the fibonacci decomposition, after a
     *                    move has been played
     * @return true if the nextState has a different composition, false otherwise
     */
    public boolean isDifferent(GameState nextState) {

	ArrayList<Integer> nextDecomposition = nextState.getState();
	ArrayList<Integer> curDecomposition = this.getState();

        for (int i = 0; i < hole; i++) {
            if(nextDecomposition.get(i) != curDecomposition.get(i)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Determines if GameState is a terminal state
     *
     * @return true if no moves can be applied to the GameState, false otherwise
     */
    public boolean isEndState() {


	// Get the current decomposition
        ArrayList<Integer> decomp = this.getState();

	// Edge case with decomposition of 1
        if (decomp.size() == 1 && decomp.get(0) <= 1) {
            return true;
        }

	// Easy check to see if a state is not terminal (if any 1s are left)
        if (decomp.get(0) > 1) {
            return false;
        }

	// Check to see if any moves can be played from this state
        for (int i = 0; i < decomp.size(); i++) {
            
            int firstCount = decomp.get(i);
                

		// Check for any keys with more than 1 fibonacci
            if(firstCount > 1) {
                return false;
            }

		// Check for any consecutive keys with nonzero values
            if(i+1 < decomp.size()){
                int secondCount = decomp.get(i+1);
                if(firstCount != 0 && secondCount != 0) {
                    return false;
                }
            }
        }

        return true;
        /*
         *
         */
    }


    //===================================
    // CHILD METHODS
    //===================================

    /**
     * Make all valid moves from the current state
     *
     * @return - An ArrayList containing all the states the GameState could lead to
     * after a single move
     */
    public ArrayList<GameState> genChildren() {

	// Get the state of the GameState
        ArrayList<Integer> curState = this.getState();

	// Initialize a list that will hold all the possible subsequent GameStates
	ArrayList<GameState> children = new ArrayList<GameState>();

	// Generate all the possible next moves
        for(int i = 0; i < curState.size(); i++) {

	    // Find all the split moves that can be done and add resulting GameStates
	    // to list of children
            GameState newState3 = this.move3(i);
            if(this.isDifferent(newState3)) {
		children.add(newState3);
            }

	    // Find all the consescutive summing moves and add resulting GameStates
	    // to list of children
            GameState newState1 = this.move1(i);
            if(this.isDifferent(newState1)) {
                children.add(newState1);
            }

        }

	// Add the 1 + 1 sum move to list of children if possible
	GameState newState2 = this.move2();
        if(this.isDifferent(newState2)) {
	    children.add(newState2);
        }


        return children;
    }
  


    /**
     * Determines is the passed GameState represents a child of this GameState
     *
     * @param st - A potential child GameState of this GameState
     * @return - True if st represents a child of this GameState, false otherwise
     */
    public boolean hasChild(GameState st) {
	ArrayList<GameState> children = genChildren();
	for(GameState child: children) {
	    if(!child.isDifferent(st)) {
		return true;
	    }
	}

	return false;
    }


    //===================================
    // AUXILIARY METHODS
    //===================================

    /**
     * Get the nth fibonacci number using recursion. Not the fastest method
     *
     * @return - The nth fibonacci number, with fib(0) = 1 and fib(1) = 1
     */
    public int fib(int n) {

	// Recursively compute the nth fibonacci number according to the recurrence
	// relation.
        if(n <= 1) {
            return 1;
        } else {
            return fib(n - 1) + fib(n - 2);
        }
    }


    /**
     * Overrides the default Object toString method, returning important information
     * about the GameState
     *
     * @return - A String representation of the GameState object
     */
    @Override
    public String toString() {

	// Display the information in the state in a useful way
	String decompInfo = "";
	ArrayList<Integer> curState = this.getState();
	for(int i = 0; i < curState.size(); i++) {
	    decompInfo += "Has " + curState.get(i) + " (" + fib(i+1) + ")\n";
	}

	// Display the winner of the game starting at this GameState
	String winner = "Player " + this.winVal + " wins";

	return "After " + depth + " moves, the decomposition is:\n" 
	    + decompInfo + winner;
    }

    
}