import java.awt.Color;
import java.awt.Graphics2D;

// Why abstract? There's no such thing as a general "molecule" - every molecule can be classified
public abstract class Molecule
{
	public int x, y, radius;					// x and y and radius of the circle
	public Color color = Color.red;				// color of the circle
	public Vector vec = new Vector();			// direction vector of the molecule
	public int xMin, xMax, yMin, yMid, yMax;	// Rectangle that midpoint of circle must stay in
	
	private double perm = 20;		// short for "permeability". % chance that molecule will move through middle
	
	// constructor without color for making water
	public Molecule(int radius, int xMin, int yMin, int xMax, int yMax, int yMid)
	{
		x = random(xMin, xMax);		// assign random (x,y) coordinates
		y = random(yMin, yMax);
		this.radius = radius;
		
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.yMid = yMid;
		color = Color.red;		// default color is red
	}
	
	// constructor without color for making air
	public Molecule(int radius, int xMin, int yMin, int xMax, int yMax, int yMid, boolean atTop)
	{
		this(radius, xMin, yMin, xMax, yMax, yMid);
		x = random(xMin, xMax);		// assign random (x,y) coordinates
		if(atTop)
			y = random(yMin, yMid-yMin);
		else
			y = random(yMin+yMin, yMid-yMin);
	}
	
	//constructor with color
	public Molecule(int radius, int xMin, int yMin, int xMax, int yMax, int yMid, Color color)
	{
		this(radius, xMin, yMin, xMax, yMax, yMid);
		this.color = color;
	}
	
	//constructor with color
	public Molecule(int radius, int xMin, int yMin, int xMax, int yMax, int yMid, Color color, boolean atTop)
	{
		this(radius, xMin, yMin, xMax, yMax, yMid, atTop);
		this.color = color;
	}
	
	// generate random number in [min, max]
	public static int random(int min, int max)
	{
		return (int)(Math.random()*max + min + 0.5);
	}
	
	// determine next position
	public void tick()
	{
//		x = randomX(xMin, xMax);
//		y = randomY(yMin, yMax);
		
//		System.out.println(xMin+"< "+x+" <"+(xMax+xMin)+"  ,  "+yMin+"< "+y+" <"+(yMax+yMin));
		
//		System.out.println("Vector("+vec.x+","+vec.y+")");
		
		if(hitMiddle())
		{
			vec.y *= -1;
			x = nextX();
			y = nextY();
		}
		
		// if the next move is within the bounds, move normally
		if(!isOutOfBounds(nextX(), nextY(), xMin, yMin, xMax, yMax))
		{
			x = nextX();
			y = nextY();
		}
		else
		{
			// if it hits a side, then flip the vector to make it bounce
			
			if(hitTop())
				vec.y *= -1;
			
			if(hitBottom())
				vec.y *= -1;
			
			if(hitRight())
				vec.x *= -1;
			
			if(hitLeft())
				vec.x *= -1;
			
			// move to next position after bouncing
			x = nextX();
			y = nextY();
		}
	}
	
	// the next x position using the current vector
	public int nextX()
	{
		return (int)(x + vec.x*vec.scale);
	}
	
	// the next y position using the current vector
	public int nextY()
	{
		return (int)(y + vec.y*vec.scale);
	}
	
	// returns true if molecule is about midline
	public boolean atTop()
	{
		if(y < yMid)
			return true;
		return false;
	}
	
	// return true if point (x,y) is out of the the bounds of Rectangle (xMin, yMin, xMax, yMax)
	public boolean isOutOfBounds(int x, int y, int xMin, int yMin, int xMax, int yMax)
	{
		if(x <= xMin || x >= xMax+xMin)
			return true;
		if(y <= yMin || y >= yMax+yMin)
			return true;
		return false;
	}
	
	// return true if current position is greater than or equal to the top of the bounds
	public boolean hitMiddle()
	{
		if(World.oneWayPerm)
		{
			// if a water molecule is moving back into the water, always let it through
			if(vec.y > 0 && this instanceof Water)
				return false;
			
			// if a air molecule is moving out of the water, always let it through
			if(vec.y < 0 && this instanceof Air)
				return false;
		}
		
		double ran = Math.random();
		
		if(nextY() <= yMid && nextY() >= yMid-Math.abs(vec.y*vec.scale))	//if hit middle
		{
			// if a air molecule is moving torwards the water, never let it through
			if(vec.y > 0 && this instanceof Air)
				return true;
			
			if(ran <= 1.0-(perm/100.0))		// use the perm variable to determine if it will bounce
				return true;
		}
		return false;
	}
	
	// return true if current position is greater than or equal to the top of the bounds
	public boolean hitTop()
	{
		if(nextY() <= yMin+radius && nextY() >= yMin-Math.abs(vec.y*vec.scale)-radius)	//if hit top
				return true;
		return false;
	}
	
	// return true if current position is less than or equal to the top of the bounds
	public boolean hitBottom()
	{
		if(nextY() >= yMin+yMax)
			return true;
		return false;
	}
	
	// return true if current position is less than or equal to the left of the bounds
	public boolean hitLeft()
	{
		if(nextX() <= xMin)
			return true;
		return false;
	}
	
	// return true if current position is greater than or equal to the right of the bounds
	public boolean hitRight()
	{
		if(nextX() >= xMin+xMax)
			return true;
		return false;
	}
	
	// set the speed of the molecule
	public void setScale(double scale)
	{
		vec.scale = scale;
	}
	
	// returns permeability value
	public double getPerm()
	{
		return perm;
	}
	
	// sets permeability value
	public void setPerm(double perm)
	{
		this.perm = perm;
	}
	
	//draw the molecule based on the current position and color
	public void render(Graphics2D g2)
	{
//		draw colored circle with black border
		g2.setColor(color);
		g2.fillOval(x-radius, y-radius, radius*2, radius*2);
		g2.setColor(Color.black);
		g2.drawOval(x-radius, y-radius, radius*2, radius*2);
		
//		g2.setColor(Color.red);
//		g2.drawLine(x, y, (int)(x+vec.x*vec.scale), (int)(y+vec.y*vec.scale));
	}
}
