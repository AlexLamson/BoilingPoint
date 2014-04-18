import java.applet.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import javax.swing.*;

// use this class if the other applet is too flicker-y
public class Main extends Applet implements Runnable
{
	private static final long serialVersionUID = 8864158495101925325L;				//because stupid warnings
	
	public static int pixelScale = 1;												//change the scale the pixels are multiplied by when drawn to
	
	public static int computerSpeed = 10;		//higher number for slower computers
	public static int tickTime = 5;
	public static boolean isRunning = false;
	public static boolean isPaused = false;
	
	public static String windowName = "Effects of Pressure and Temperature on Boiling Point";

	public static boolean debugMode = true;
	
	public static Dimension realSize;															//size of whole window
	public static Dimension size = new Dimension(600, 400);				//drawable area
	public static Dimension pixel = new Dimension(size.width/pixelScale, size.height/pixelScale);	//"pixels" in drawable area

	public static Point mse = new Point(0, 0);

	public static boolean isMouseLeft = false;
	public static boolean isMouseMiddle = false;
	public static boolean isMouseRight = false;

	private Image screen;
	public static JFrame frame;
	public static World world;
	
	public Main()
	{
		setPreferredSize(size);
	}

	public static void restart()
	{
		Main main = new Main();
		main.start();
	}

	public void start()
	{
		//defining objects
		world = new World(25, pixel.width/4, pixel.height/4, pixel.width/2, pixel.height/2, pixel.height/2);
		
		addKeyListener(new Listening());
		addMouseListener(new Listening());
		addMouseMotionListener(new Listening());
		addMouseWheelListener(new Listening());

		//start the main loop
		isRunning = true;
		new Thread(this).start();
		requestFocus();
	}

	public void stop()
	{
		isRunning = false;
	}

	public void tick()
	{
//		if(frame.getWidth() != realSize.width || frame.getHeight() != realSize.height)
//			frame.pack();
		
		//call tick methods here
		world.tick();
	}

	public void render()
	{
		Graphics g = screen.getGraphics();

		g.setColor(new Color(140, 230, 0));
		g.fillRect(0, 0, pixel.width, pixel.height);
		
		
		//call render methods here
		world.render(g);
		
//		g.setColor(Color.red);
//		g.drawLine(0, 0, pixel.width, pixel.height);
//		g.setColor(Color.green);
//		g.drawLine(0, pixel.height, pixel.width, 0);
		
		g = getGraphics();

		g.drawImage(screen, 0, 0, size.width, size.height, 0, 0, pixel.width, pixel.height, null);
		g.dispose();		//throw it away to avoid lag from too many graphics objects
	}

	public void run()
	{
		screen = createVolatileImage(pixel.width, pixel.height);	//actually use the graphics card (less lag)
		
		render();
//		JOptionPane.showMessageDialog(null, "Controls:\n\nWho knows!?");
		
		while(isRunning)
		{
			if(!isPaused)
			{
				tick();			//do math and any calculations
				render();		//draw the objects
			}

			try
			{
				Thread.sleep(tickTime*(int)computerSpeed);
			}catch(Exception e){ }
		}
	}

	public static void main(String[] args)
	{
		Main main = new Main();

		frame = new JFrame();
		frame.add(main);
		frame.pack();

		realSize = new Dimension(frame.getWidth(), frame.getHeight());

		frame.setTitle(windowName);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);		//null makes it go to the center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		main.start();
	}
}