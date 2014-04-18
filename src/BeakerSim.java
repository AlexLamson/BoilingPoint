import java.awt.Graphics;

import javax.swing.Box;
import javax.swing.JPanel;

// holds the world in a JPanel with a fixed width
public class BeakerSim extends JPanel
{
	private static final long serialVersionUID = -7220689122529377944L;	// because stupid warnings
	
	public World world;
	
	public BeakerSim(int numWater, int xMin, int yMin, int xMax, int yMax, int yMid)
	{
		super(true);								// enable double buffering (fixes flickering on some computers)
		add(Box.createHorizontalStrut( 425 ));		// set the width
		
		world = new World(numWater, xMin, yMin, xMax, yMax, yMid);	//send fields to world
		World.perm = 0;			// reset permeability
		
	}
	
	public void tick()
	{
		world.tick();
	}
	
	public void render(Graphics g)
	{
		world.render(g);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		render(g);
	}
}
