import java.awt.Color;

public class Water extends Molecule
{
	public Water(int xMin, int yMin, int xMax, int yMax, int yMid)
	{
		super(2*World.moleculeRadius, xMin, yMin, xMax, yMax, yMid, new Color(0, 140, 225));	// water is always blue
	}
	
	public Water(int xMin, int yMin, int xMax, int yMax, int yMid, boolean atTop)
	{
		super(2*World.moleculeRadius, xMin, yMin, xMax, yMax, yMid, new Color(0, 140, 225), atTop);	// water is always blue
	}
}
