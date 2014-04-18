import java.awt.Color;

public class Air extends Molecule
{
	public Air(int xMin, int yMin, int xMax, int yMax, int yMid)
	{
		super(World.moleculeRadius, xMin, yMin, xMax, yMax, yMid, Color.lightGray);	// water is always gray
	}
	
	public Air(int xMin, int yMin, int xMax, int yMax, int yMid, boolean atTop)
	{
		super(World.moleculeRadius, xMin, yMin, xMax, yMax, yMid, Color.lightGray, atTop);	// water is always gray
	}
}
