import java.util.Random;

public class Vector
{
	public double x, y;
	public double scale = 10;
	
	//Assign a random vector with length 1
	public Vector()
	{
		double offset = 0.1;
		
		double randomX = Math.random()*(1-(2*offset))+offset;
		double randomY = Math.sqrt(1-randomX*randomX);
		
		randomX *= randomSign();
		randomY *= randomSign();
		
		x = randomX;
		y = randomY;
	}
	
	//randomly return either 1 or -1
	public int randomSign()
	{
		int sign = 1;
		if(new Random().nextBoolean())
			sign = -1;
		return sign;
	}
	
	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
}
