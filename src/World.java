import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class World
{
	public int xMin, yMin, xMax, yMax, yMid;
	public ArrayList<Molecule> molecules = new ArrayList<Molecule>();
	
	public static final int moleculeRadius = 5;	// default radius of molecules in pixels
	public static double perm = 5;			// short for "permeability". % chance that water molecule escapes bottom
	public static boolean oneWayPerm = true;	// if false, then permeability won't apply when molecule is returning to its side
	
	public static double temp = 373.2;		// temperature of the water in degrees Kelvin
	public static double pres = 760;		// atmospheric pressure in mm HG
	
	// the minimum and maximum temperatures and pressures
	public static final double minTemp = 250, maxTemp = 450, minPres = 0, maxPres = 6000;
	
	// create the World based on the fields
	public World(int numWater, int xMin, int yMin, int xMax, int yMax, int yMid)
	{
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.yMid = yMid;
		
		// no need to generate air molecules, because that is adjusted every tick based on the temperature
		
		// generate Water molecules
		for(int i = 0; i < numWater; i++)
			addWater();
		
		updatePerm(perm);
	}
	
	// add a molecule of air to the world
	public void addAir()
	{
		molecules.add(new Air(xMin, yMin, xMax, yMax, yMid, true));
	}
	
	// add a molecule of water to the world
	public void addWater()
	{
		molecules.add(new Water(xMin, yMin, xMax, yMax, yMid, false));
	}
	
	// make sure that the pressure and the amount of air is proportional
	public void adjustAir()
	{
		int currentAir = 0;					// current number of air molecules
		for(Molecule mole : getMolecules())
			if(mole instanceof Air)
				currentAir++;
		
		int desiredAir = (int)(pres/100);	// desired number of air molecules
		
		if(desiredAir > currentAir)			// not enough air
		{
			for(int i = currentAir; i <= desiredAir; i++)
				addAir();
		}
		else if(desiredAir < currentAir)	// too much air
		{
			for(Molecule mole : getMolecules())
			{
				if(mole instanceof Air)
				{
					molecules.remove(mole);		// remove air
					currentAir--;
				}
				
				if(currentAir == desiredAir)	// until desired amount is reached
					break;
			}
		}
	}
	
	// make sure that the temperature and the speed of air is proportional
	public void adjustWater()
	{
		for(Molecule mole : getMolecules())
			if(mole instanceof Water)
				mole.setScale(((temp-250)/200.0)*20.0+1);
	}
	
	// return a string the represents the state of water
	public String getState()
	{
		if(atBP())
			return "Boiling Point";
		if(isBoiling())
			return "Boiling";
		return "Not Boiling";
	}
	
	// return true if the 
	public boolean atBP()
	{
		double predictedTemp = getBPTemp();
		if(temp <= predictedTemp+2 && temp >= predictedTemp-2)
			return true;
		return false;
	}
	
	// return true if current temperature is > boiling point temperature for this pressure
	public boolean isBoiling()
	{
		if(temp > getBPTemp())
			return true;
		return false;
	}
	
	// may be 5 mm off
	public static double getBPPres()
	{
		double a = 8.14019;
		double b = 1810.94;
		double c = 244.485;
		
		double p = Math.pow(10, a-(b/(c+temp-273.2)));
		return p;
	}
	
	// may be 2 degrees off
	public static double getBPTemp()
	{
		double a = 8.14019;
		double b = 1810.94;
		double c = 244.485;
		
		double t = b / (a-Math.log10(pres)) - c;
		return t + 273.2;
	}
	
	// set the permeability variable of all the water molecules
	public void updatePerm(double newPerm)
	{
		// adjust the permeability based on the temperature and pressure
//		double BP = getBPPres();
//		perm = ((pres-BP)/(maxPres-BP))*100/2;		// old, less realistic method
		perm = ((((maxPres-pres)/maxPres)+(temp/maxTemp))/2.0)*100;
		fixOutOfBounds();
		
		for(Molecule mole : getMolecules())
//			if(mole instanceof Water)
				mole.setPerm(newPerm);
	}
	
	public void changePerm(double delta)
	{
		perm += delta;
	}
	
	public void fixOutOfBounds()
	{
		// prevent any invalid permeabilities
		if(perm > 100)
			perm = 100;
		if(perm < 0)
			perm = 0;
		
		// prevent any invalid temperatures
		if(temp > maxTemp)
			temp = maxTemp;
		if(temp < minTemp)
			temp = minTemp;
		
		// prevent any invalid pressures
		if(pres > maxPres)
			pres = maxPres;
		if(pres < minPres)
			pres = minPres;
	}
	
	// return a copy of the ArrayList of Molecules (used to avoid concurrent modification error)
	public ArrayList<Molecule> getMolecules()
	{
		ArrayList<Molecule> clone = new ArrayList<Molecule>();
		for(Molecule mole : molecules)
			clone.add(mole);
		return clone;
	}
	
	// do math for position changes, etc
	public void tick()
	{
		fixOutOfBounds();
		
		updatePerm(perm);
		
		adjustAir();
		adjustWater();
		
		// call tick method of all molecules
		for(Molecule mole : molecules)
			mole.tick();
	}
	
	// draw everything
	public void render(Graphics g)
	{
		// Graphics2D makes it look pretty
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// fill in a green background
		g.setColor(new Color(140, 230, 0));
		g.fillRect(xMin-20, yMin-20, xMax+40, yMax+125);
		
		
		// draw the bounds box
		g.setColor(Color.red);
		g.drawRect(xMin, yMin, xMax, yMax);
		
		
		// draw beaker
		g2.setColor(Color.black);
		BasicStroke thick = new BasicStroke(3.0f);
		g2.setStroke(thick);
		g2.drawLine(xMin, yMin, xMin, yMin+yMax);
		g2.drawLine(xMin+xMax, yMin, xMin+xMax, yMin+yMax);
		g2.drawLine(xMin, yMin+yMax, xMin+xMax, yMin+yMax);
		
		// use a normal sized line (this is needed if the last stroke was abnormal, ie dotted)
		g2.setColor(Color.black);
		BasicStroke normalStroke = new BasicStroke();
		g2.setStroke(normalStroke);
		
		// call render method of all molecules
		for(Molecule mole : getMolecules())
//			if(mole instanceof Air)
				mole.render(g2);
		
		// use a dotted line type stroke
		g2.setColor(Color.black);
		BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f);
		g2.setStroke(dashed);
		
		// draw the midline
		fixOutOfBounds();
		g2.setColor(new Color(0,0,0, (int)(255.0*(100-perm)/100)));			// the line is more transparent if it is more permeabile
		g2.drawLine(xMin, yMid, xMin+xMax, yMid);
		g2.setColor(new Color(0,0,0, 255));
		
		// draw water background
		g2.setColor(new Color(0, 100, 200, 150));
		g2.fillRect(xMin, yMid+2, xMax, yMax-(yMid-yMin)-2);
		
		// draw the flame
		int transparency = (int)(255.0*(temp-minTemp)/(maxTemp-minTemp));
		if(transparency > 255)
			transparency = 255;
		else if(transparency < 0)
			transparency = 0;
		g2.setStroke(normalStroke);
		g2.setColor(new Color(255, 0, 0, transparency));	// red
		g2.fillOval((xMin+xMax+xMin)/2-15, yMin+yMax+10, 30, 75);
		g2.setColor(new Color(255, 165, 0, transparency));	// orange
		g2.fillOval((xMin+xMax+xMin)/2-10, yMin+yMax+25+10, 20, 50);
		g2.setColor(Color.black);							// black
		g2.fillRect((xMin+xMax+xMin)/2-10, yMin+yMax+75, 20, 30);	// draw the barrel of the bunsen burner
		
		// draw text information
		g2.setColor(Color.black);
		g2.drawString(getState(), xMin, yMin+yMax+20);
//		g2.drawString(pres+" mmHG", xMin, yMin+yMax+40);
//		g2.drawString(temp+" K", xMin, yMin+yMax+60);
//		g2.drawString((int)(perm*100)/100+" % perm", xMin, yMin+yMax+80);
	}
}
