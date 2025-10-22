package assignment5;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;
import java.awt.Font;


import javax.swing.JFileChooser;

import edu.princeton.cs.introcs.StdDraw;

/**
 * A Zombie Simulator!
 */
public class ZombieSimulator {
	public static final int X = 0;
	public static final int Y = 1;
	private static final String ZOMBIE_TOKEN_VALUE = "Zombie";

	private static final Color ZOMBIE_COLOR = new Color(146, 0, 0);
	private static final Color NONZOMBIE_COLOR = new Color(0, 0, 0);
	private static final Color TEXT_COLOR = new Color(73, 0, 146);
	public static final double ENTITY_RADIUS = 0.008;

	public static final double RANDOM_DELTA_HALF_RANGE = 0.006;

    public static void second(String[] args) {
        // Create a new Font object
        Font myFont = new Font("mononoki", Font.ITALIC, 24);

        // Set the font in StdDraw
        StdDraw.setFont(myFont); 
	}

	/**
	 * Read entities from a file.
	 */
	public static void readEntities(Scanner in, boolean[] areZombies, double[][] positions) {
		int i = 0; // index for arrays


		while (in.hasNext()) {
        	String type = in.next();        // read token (Zombie or Human)
        	double x = in.nextDouble();     // read x-coordinate
        	double y = in.nextDouble();     // read y-coordinate

        	positions[i][X] = x;
        	positions[i][Y] = y;
        	areZombies[i] = Objects.equals(type, ZOMBIE_TOKEN_VALUE);

        i++;
    	}
	}

	/**
	 * Draw all the entities. Zombies are drawn as ZOMBIE_COLOR filled circles of
	 * radius ENTITY_RADIUS and non-zombies with filled NONZOMBIE_COLOR filled
	 * circles of radius ENTITY_RADIUS). Further, add feedback for nonzombie count
	 * (when ready to do so), and any additional desired drawing features.
	 * 
	 * @param areZombies the zombie state of each entity
	 * @param positions  the (x,y) position of each entity
	 */
	public static void drawEntities(boolean[] areZombies, double[][] positions) {
	
		// DONE: Clear the frame
		StdDraw.clear();
	
		// Loop through all entities
    	for (int i = 0; i < positions.length; i++) {
        	double x = positions[i][X];
        	double y = positions[i][Y];

        	if (areZombies[i]) {
            	StdDraw.setPenColor(ZOMBIE_COLOR);
        	} else {
            	StdDraw.setPenColor(NONZOMBIE_COLOR);
    		}

        	// Draw filled circle at entity position
        	StdDraw.filledCircle(x, y, ENTITY_RADIUS);
    	}

    // Optional: display number of non-zombies
    int nonZombieCount = 0;
    for (boolean isZombie : areZombies) {
        if (!isZombie) nonZombieCount++;
    }
    StdDraw.setPenColor(TEXT_COLOR);
    StdDraw.text(0.2, 0.95, "Non-zombies: " + nonZombieCount); // top-left corner

    // Show the frame
		StdDraw.show();
	}

	/**
	 * Check if the entity at the given index is touching a zombie. (HINT: You know
	 * the location of the center of each entity and that they all have a radius of
	 * ENTITY_RADIUS. If the circles representing two entities overlap they are
	 * considered to be touching. Consider using the distance formula.)
	 *
	 * @param index      the index of the entity to check
	 * @param areZombies the zombie state of each entity
	 * @param positions  the (x,y) position of each entity
	 * @return true if the entity at index is touching a zombie, false otherwise
	 */
	public static boolean touchingZombie(int index, boolean[] areZombies, double[][] positions) {
	
		// Loop over all entities
    for (int i = 0; i < positions.length; i++) {

        // Skip comparing the entity to itself
        if (i == index) continue;

        // Only check against zombies
        if (areZombies[i]) {
            double dx = positions[index][X] - positions[i][X];
            double dy = positions[index][Y] - positions[i][Y];
            double distance = Math.sqrt(dx * dx + dy * dy);

            // If circles overlap, return true
            if (distance <= 2 * ENTITY_RADIUS) {
                return true;
            }
        }
    }

    // If we went through all and found no touching zombies
    return false;
}

	/**
	 * Update the areZombies states and positions of all entities (assume Brownian
	 * motion).
	 *
	 * The rules for an update are:
	 * 
	 * Each entity should move by a random value between -RANDOM_DELTA_HALF_RANGE 
	 * and +RANDOM_DELTA_HALF_RANGE in both the x and the y coordinates.
	 * 
	 * Entities should not be able to leave the screen. x and y coordinates should
	 * be kept between [0-1.0]
	 *
	 * If a non-zombie is touching a zombie it should change to a zombie. (HINT: you
	 * need to check all entities. On each one that is NOT a zombie, you can re-use
	 * code you've already written to see if it's "touching" a Zombie and, if so,
	 * change it to a zombie.)
	 *
	 * @param areZombies the zombie state of each entity
	 * @param positions  the (x,y) position of each entity
	 */
	public static void updateEntities(boolean[] areZombies, double[][] positions) {
		// TODO: Complete this method: It should update the positions of items in the
		// entities array

		// Step 1: Move every entity randomly
    	for (int i = 0; i < positions.length; i++) {
        	double dx = (Math.random() * 2 * RANDOM_DELTA_HALF_RANGE) - RANDOM_DELTA_HALF_RANGE;
        	double dy = (Math.random() * 2 * RANDOM_DELTA_HALF_RANGE) - RANDOM_DELTA_HALF_RANGE;

		// Update position
        	positions[i][X] += dx;
        	positions[i][Y] += dy;

        // Step 2: Keep within [0, 1.0]
        	if (positions[i][X] < 0) positions[i][X] = 0;
        	if (positions[i][X] > 1.0) positions[i][X] = 1.0;
        	if (positions[i][Y] < 0) positions[i][Y] = 0;
        	if (positions[i][Y] > 1.0) positions[i][Y] = 1.0;
    	}

		// Step 3: Infection spread check
    	
		// Use a copy so new infections don’t affect checks in the same round
    	boolean[] newZombies = new boolean[areZombies.length];
    	for (int i = 0; i < areZombies.length; i++) {
        	newZombies[i] = areZombies[i]; // copy current state
    		}

    	for (int i = 0; i < areZombies.length; i++) {
        	if (!areZombies[i]) { // only check non-zombies
            	if (touchingZombie(i, areZombies, positions)) {
                	newZombies[i] = true; // becomes a zombie
            	}
        	}
    	}

    	// Apply updated states
    		for (int i = 0; i < areZombies.length; i++) {
        		areZombies[i] = newZombies[i];
    		}
		}

	/**
	 * Return the number of nonzombies remaining
	 */
	/**
 * Return the number of nonzombies remaining
 */
		public static int nonzombieCount(boolean[] areZombies) {
   			int count = 0;
    		for (int i = 0; i < areZombies.length; i++) {
        		if (!areZombies[i]) {
            		count++;
        		}
    		}
    	return count;
		}

	/**
	 * Run the zombie simulation.
	 */
	private static void runSimulation(Scanner in) {
		StdDraw.enableDoubleBuffering(); // reduce unpleasant drawing artifacts, speed things up

		// TODO: Uncomment and fix the code below.
		int N = in.nextInt();
		boolean[] areZombies = new boolean[N];
		double[][] positions = new double[N][2];
		readEntities(in, areZombies, positions);
		drawEntities(areZombies, positions);
		
		StdDraw.pause(500);

		// TODO: Write the loop that will run the simulation.
		// Continue if nonzombies remain
		// Update zombie state and positions
		// Redraw
		
		while (nonzombieCount(areZombies) > 0) {
			updateEntities(areZombies, positions);
			drawEntities(areZombies, positions);
			StdDraw.pause(50);
		}
		
		StdDraw.setPenColor(Color.RED);
		StdDraw.text(0.5, 0.5, "All zombies!");
		StdDraw.show();
	}

	public static void main(String[] args) throws FileNotFoundException {
		JFileChooser chooser = new JFileChooser("zombieSims");
		chooser.showOpenDialog(null);
		File f = new File(chooser.getSelectedFile().getPath());
		Scanner in = new Scanner(f); //making Scanner with a File
		runSimulation(in);
	}

}
