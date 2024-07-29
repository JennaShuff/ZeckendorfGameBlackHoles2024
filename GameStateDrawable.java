import java.util.ArrayList;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class GameStateDrawable {

    /**
     * Each drawable GameStateDrawable (GSD) maintains its own GameState object
     */
    private GameState state;

    /**
     * Parameters for the location, dimension, and color of the drawable GSD
     */
    private int x, y, width, height;
    private Color trimColor, fillColor;

    /**
     * Constants that dictate the largest dimensions for a GSD
     */
    private static final int MAX_WIDTH = 30, MAX_HEIGHT = 10;

    /**
     * Offsets for the text contained inside each GSD
     */
    private final int X_BORDER = 1, Y_BORDER = 1;

    /**
     * Strings that hold a representation of the keys and values in the GameState
     */
    private String keys, values;
    

    /**
     * Constructor for a GSD
     *
     * @param state - The GameState that is drawn in the GSD
     * @param x - The x coordinate of the top left corner of the GSD
     * @param y - The y coordinate of the top left corner of the GSD
     * @param width - The width of the GSD box
     * @param height = The height of the GSD box
     */
    public GameStateDrawable(GameState state, int x, int y, int width, int height) {
	this.state = state;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	
	// Initialize all the global variables according to the wrapped GameState
	initGlobalVariables();
    }


    /**
     * Initializes all the global variables of the GSD according to the constants of
     * the GameState
     */ 
    public void initGlobalVariables() {

	// Initialize the keys and values Strings
	this.keys = "";
	this.values = "";

	// Get the children of the wrapped GameState. The number of children is used
	// to determine whether or not the GameState is terminal.
	ArrayList<GameState> children = state.genChildren();
	int numChildren = children.size();

	// Initialize the trim and fill colors according to Player 1/2 victory,
	// and whether or not the state is terminal.
	this.trimColor = Color.BLACK;
	if (state.getVal() == 1 && numChildren > 0) {
	    this.trimColor = new Color(102, 178, 255);
	    this.fillColor = Color.WHITE;
	} else if (state.getVal() == 1 && numChildren == 0) {
	    this.trimColor = Color.BLUE;
	    this.fillColor = Color.YELLOW;
	} else if (state.getVal() == -1 && numChildren > 0) {
	    this.trimColor = new Color(255, 102, 102);
	    this.fillColor = Color.WHITE;
	} else if (state.getVal() == - 1 && numChildren == 0) {
	    this.trimColor = Color.RED;
	    this.fillColor = Color.YELLOW;
	}

	// Generate the key and value Strings so that the value corresponding to
	// a key lies directly below it.
	for(int i = 0; i < state.getState().size(); i++) {
	    keys += " " + fib(i+1);
	    values += " " + state.getState().get(i);

	    // Pad with spaces to keep the value and key Strings aligned
	    while(keys.length() < values.length()) { keys += " "; }
	    while(values.length() < keys.length()) { values += " "; }
	}

	
    }


    //=================================
    // GETTERS AND SETTERS
    //=================================

    /**
     * Getter and Setter for the x-coordinate of the GSD
     */
    public int getX() { return this.x; }
    public void setX(int x) { this.x = x; }


    /**
     * Getter and Setter for the y-coordinate of the GSD
     */
    public int getY() { return this.y; }
    public void setY(int y) { this.y = y; }


    /**
     * Getter and Setter for the width of the GSD
     */
    public int getWidth() { return this.width; }
    public void setWidth(int width) { this.width = width; }


    /**
     * Getter and Setter for the height of the GSD
     */
    public int getHeight() { return this.height; }
    public void setHeight(int height) { this.height = height; }


    /**
     * Getter and Setter for the keys of the GameState
     */
    public String getKeyString() { return this.keys; }
    public void setKeyString(String keys) { this.keys = keys; }


    /**
     * Getter and Setter for the values of the GSD
     */
    public String getValueString() { return this.values; }
    public void setValueString(String values) { this.values = values; }


    /**
     * Getter and Setter for the GameState of the GSD
     */
    public GameState getState() { return this.state; }
    public void setState(GameState state) { this.state = state; }
    

    /**
     * Getter and Setter for the fill color of the GSD
     */
    public Color getFillColor() { return this.fillColor; }
    public void setFillColor(Color c) { this.fillColor = c; }

    
    /**
     * Getter and Setter for the trim color of the GSD
     */
    public Color getTrimColor() { return this.trimColor; }
    public void setTrimColor(Color trimColor) { this.trimColor = trimColor; }


    //=================================
    // DRAWING AND AUXILIARY METHODS
    //=================================

    /**
     * Draws the GSD on a canvas
     *
     * @param g - The Graphics object that draws the GSD
     */
    public void draw(Graphics g) {
	
	// Fill the GameState rectangle with the correct fill color
	g.setColor(this.fillColor);
	g.fillRect(this.x, this.y, this.width, this.height);

	// Draw the trim to the rectangle according to player 1/2 victory
	g.setColor(this.trimColor);
	g.drawRect(this.x, this.y, this.width, this.height);

	// Fix the font size to best fit into the GSD box
	adaptTextSize(g);

	// Draw the information of the GameState
	FontMetrics fm = g.getFontMetrics();
	int fontHeight = fm.getHeight();
	g.drawString(keys, 
		     this.x + this.X_BORDER, 
		     this.y + this.Y_BORDER + (fontHeight + 1));
	g.drawString(values, 
		     this.x + this.X_BORDER, 
		     this.y + this.Y_BORDER + 2 * (fontHeight + 1));
	
	
    }


    /**
     * Fixes the font size according to the size of the GSD, so the text fits in
     * the GSD box.
     *
     * @param g - The Graphics that draws the GSD
     */
    public void adaptTextSize(Graphics g) {
	g.setFont(new Font("Times", 1, 12));
	FontMetrics fm = g.getFontMetrics();
	int maxStringWidth = Math.max(fm.stringWidth(keys), fm.stringWidth(values));
	while(g.getFont().getSize() > 7 
	      && (maxStringWidth > this.width || 2 * fm.getHeight() + 3 >= this.height)) {
	    reduceFont(g);
	    fm = g.getFontMetrics();
	    maxStringWidth = Math.max(fm.stringWidth(keys), fm.stringWidth(values));
	}
    }


    /**
     * Reduces the size of the Font being used by the Graphics object by 1.
     *
     * @param g - The Graphics object the draws the GSD
     */
    public void reduceFont(Graphics g) {
	Font f = g.getFont();
	int curSize = f.getSize();
	f = new Font(f.getName(), f.getStyle(), curSize - 1);
	g.setFont(f);
    }


    /**
     * An auxiliary method used to compute the nth fibonacci number
     *
     * @param n - The index of the fibonacci number to compute
     */
    public int fib(int n) {

	// Recursively compute the nth finbonacci number using the recurrence relation
	if(n <= 1) {
	    return 1;
	} else {
	    return fib(n - 1) + fib(n - 2);
	}
    }
    
    // Main method used for debugging
    public static void main(String[] args) {
	GameState root = new GameState(13);
	GameState child = root.genChildren().get(0).genChildren().get(0);

	GameStateDrawable d = new GameStateDrawable(child, 0, 0, 0, 0);
	System.out.println(d.getKeyString());
	System.out.println(d.getValueString());	
    }

    

}