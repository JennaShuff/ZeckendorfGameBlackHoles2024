/*
<html>
<applet code="TreeDrawer.class" width="1300" height="1300"></applet>
</html>
*/

/**
 * @author Paul Baird-Smith 2017/2018
 *
 * TreeDrawer is used to give a visual representation of the tree structure of the
 * Zeckendorf game described in (Epstein 2018). It plays through a specified game,
 * determining all moves that can be made, and draw all possible paths to the end of
 * this game. 
 *
 * Each horizontal layer is composed of GameStates that can be reached in the same 
 * number of moves, namely the depth of the layer (e.g. any state in the 3rd layer
 * is reached in exactly 3 moves). States with red trim are states at which player 2
 * has a winning strategy over player 1, and states with blue trim are those at which
 * player 1 has a winning startegy. Lines between states signify that the lower state
 * can be reached after a single move from the upper state (parent/child relationship
 * in the tree structure).
 *
 * States highlighted in yellow are terminal. There can be at most 1 of these in any
 * layer by design. Experiments to this point have shown that player 2 always has a 
 * winning strategy (true up to 50), therefore we highlight states in green if they 
 * belong to "the" winning path for player 2 (in reality, there are several winning 
 * paths but we highlight just a single one).
 *
 * The TreeDrawer can be executed, after compilaton, by running the command
 *
 *          appletviewer TreeDrawer.java
 *
 * Do not delete the comment in the preamble, as this is used at runtime by the
 * appletviewer.
 *
 * email: ppb366@cs.utexas.edu
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.SwingUtilities;

public class TreeDrawer extends JFrame implements Runnable {

    /**
     * Constants for dimensions of canvas
     */
    private final int WINDOW_WIDTH = 1300, WINDOW_HEIGHT = 775;

    /**
     * Constants for the dimensions of the size of each displayed GameState
     */
    private final int BOX_BUFFER = 10, LAYER_BUFFER = 30;

    /**
     * Main thread and running status
     */
    private boolean running = false;
    private Thread ticker;

    /**
     * Constants related to the game being run by the TreeDrawer object
     */    
    private Game game;
    private LinkedList<GameState> winningPath;


    /**
     * Initialize the Game object on desired number, and play the game, gathering
     * important information. All data gathered before the drawing.
     */
    
	 public TreeDrawer(){
		init();
	 }
	
	 public void init() {
	
	// Start and play game
	List<Integer> setup = new ArrayList<>(
        Arrays.asList(8,0,0,0,0,0,0,0));
	game = new Game(setup);
	game.playGame();

	// Get the data for the winning path and save it as a global variable
	// so the TreeDrawer can query it
	winningPath = new LinkedList<GameState>();
	for(Tree<GameState> tree: game.getWinningPath()) {
	    winningPath.add(tree.getState());
	}
	setTitle("Zeck O'clock");
    }

    @Override
    public void setDefaultCloseOperation(int operation) {
        super.setDefaultCloseOperation(operation);
    }

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				TreeDrawer frame = new TreeDrawer();
				frame.setVisible(true);
			}
		});
	}

    /**
     * Called after the init method. Starts running the main thread. At the moment, this
     * is not strictly necessary, as the canvas is updated only once after initialization
     */
    public void start() {

	// Check if thread is running
	if(ticker == null || !ticker.isAlive()) {
	    // Thread is not running

	    // Update thread status
	    running = true;
	    ticker = new Thread(this);
	    ticker.setPriority(Thread.MIN_PRIORITY);

	    // Run thread
	    ticker.start();
	}
		// Resize window to specified dimensions and requst focus
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		requestFocusInWindow();
    }


    /**
     * Simple call to draw on the canvas once.
     */
    public void run() {
	repaint();
    }


    /**
     * Draws the tree structure of the game on the canvas
     *
     * @param g - The Graphics object used to draw on the canvas
     */
    public void paint(Graphics g) {

	// Get an interable list of the GameStates in the tree structure
	ArrayList<Tree<GameState>> flat = this.game.getRoot().flatten();

	// Maintain list of GSDs to draw connections between them 
	ArrayList<GameStateDrawable> drawables = new ArrayList<GameStateDrawable>();

	// Get the depth of the longest game and determine uniform height of GSDs
	int maxDepth = flat.get(flat.size() - 1).getState().getDepth();
	int boxHeight = (WINDOW_HEIGHT - ((maxDepth + 2) * LAYER_BUFFER)) / (maxDepth + 1);

	// Maintain the height of the current layer, and the index of the layer itself
	int curDepth = 0;
	int curY = LAYER_BUFFER;

	// Keep a list of the GameStates in the current layer. This will be used to
	// find how many states are in a given layer, and thus how wide each GSD
	// should be.
	ArrayList<GameState> curLayer = new ArrayList<GameState>();

	// Iterate through the list of GameStates
	for(int index = 0; index < flat.size(); index++) {

	    // Get the current GameState and its Tree wrapper. Determine its winner
	    // value if this has not already been done.
	    Tree<GameState> curTree = flat.get(index);
	    GameState curState = curTree.getState();
	    if(curState.getVal() == 0) {
		this.game.determineVal(curTree);
	    }

	    // Case 1: the state is not the first state in a new layer
	    if(curState.getDepth() == curDepth) {
		// Update the current layer list appropriately
		curLayer.add(curState);

	    // Edge case: The state is the first in a new layer
	    } else {

		// Get the size of the previous layer and set the width of the GSDs
		int layerSize = curLayer.size();
		int boxWidth = (WINDOW_WIDTH - ((layerSize + 1) * BOX_BUFFER)) / layerSize;

		// Draw all the GSDs on the same layer
		int curX = BOX_BUFFER;
		for(GameState state: curLayer) {
		    
		    // Init new GSD
		    GameStateDrawable gsd = new GameStateDrawable(state, 
								  curX, curY, 
								  boxWidth, boxHeight);
		    
		    // Determine fill color
		    if(this.winningPath.contains(state)) {
			gsd.setFillColor(Color.GREEN);
		    }

		    // Update canvas, lists, and x position
		    gsd.draw(g);
		    drawables.add(gsd);
		    curX += boxWidth + BOX_BUFFER;
		}

		// Update curY, curDepth, and curLayer
		curY += boxHeight + LAYER_BUFFER;
		curDepth = curState.getDepth();
		curLayer = new ArrayList<GameState>();
		curLayer.add(curState);
	    }
	}

	// At end of loop, determine if we have a final layer to draw
	if(curLayer.size() > 0) {

	    // Get size of final layer, and determine uniform width of GSDs
	    int layerSize = curLayer.size();
	    int boxWidth = (WINDOW_WIDTH - ((layerSize + 1) * BOX_BUFFER)) / layerSize;

	    // Draw final layer of GSDs
	    int curX = BOX_BUFFER;	    
	    for(GameState state: curLayer) {

		// Init new GSD
		GameStateDrawable gsd = new GameStateDrawable(state, 
							      curX, curY, 
							      boxWidth, boxHeight);
		
		// Determine fill color
		if(this.winningPath.contains(state)) {
		    gsd.setFillColor(Color.GREEN);
		}

		// Update canvas, lists, and x position
		gsd.draw(g);
		drawables.add(gsd);
		curX += boxWidth + BOX_BUFFER;
	    }
	}

	// Draw connections between the parent/child GameStates
	g.setColor(Color.BLACK);
	for(int i = 0; i < drawables.size(); i++) {
	    for(int j = 0; j < drawables.size(); j++) {
		
		// Loop through every pair of GSDs and see if their GameStates are 
		// parent/child
		GameStateDrawable d1 = drawables.get(i);
		GameStateDrawable d2 = drawables.get(j);
		GameState s1 = d1.getState();
		GameState s2 = d2.getState();

		// If they are parent/child, draw a connection line between them
		if(s1.hasChild(s2) && s2.getDepth() == s1.getDepth() + 1) {
		    g.drawLine(d1.getX() + d1.getWidth() / 2, 
			       d1.getY() + d1.getHeight(),
			       d2.getX() + d2.getWidth() / 2,
			       d2.getY());
		}
	    }
	}

	
    }

    
    /**
     * Terminate the main thread
     */
    public void stop() {
	running = false;
    }

}