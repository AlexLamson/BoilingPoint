import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BoilingPointAL extends JApplet implements Runnable, ActionListener, ChangeListener
{
	private static final long serialVersionUID = 8864158495101925325L;				// because stupid warnings
	
	public static int tickTime = 50;
	public static boolean isRunning = false;
	public static boolean isPaused = false;
	
	public static String windowName = "Effects of Pressure and Temperature on Boiling Point";
	
	public static Dimension size = new Dimension(600, 400);							// size of window
	
	private Image screen;
	public static JFrame frame;
	
	// various graphics objects
	public static JSlider tempSlider, presSlider;
	public static JTextField tempField, presField;
	public static JButton tempButton, presButton;
	
	// painted graphics
	public static BeakerSim beakerSim;
	
	public BoilingPointAL()
	{
		setPreferredSize(size);
		
		Container pane = getContentPane();
	    pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// temperature label
		JLabel tempLabel = new JLabel("Temperature (K)");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		pane.add(tempLabel, c);
		
		// pressure label
		JLabel presLabel = new JLabel("Pressure (mm Hg)");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 5;
		c.gridy = 0;
		pane.add(presLabel, c);
		
		// temperature slider
		tempSlider = new JSlider(JSlider.VERTICAL, (int)World.minTemp, (int)World.maxTemp, (int)World.temp);
		tempSlider.setName("tempSlider");
		tempSlider.addChangeListener(this);
		tempSlider.setMajorTickSpacing(50);
		tempSlider.setPaintTicks(true);
		c.fill = GridBagConstraints.VERTICAL;
		c.ipady = 100;      //make it tall
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		
		//Create the temperature label table.
        Hashtable<Integer, JLabel> tempLabelTable = new Hashtable<Integer, JLabel>();
        for(int i = 0; i <= tempSlider.getMaximum(); i += tempSlider.getMajorTickSpacing())
        	tempLabelTable.put(new Integer(i), new JLabel(""+i));
        tempSlider.setLabelTable(tempLabelTable);
        tempSlider.setPaintLabels(true);
		pane.add(tempSlider, c);
		
		// pressure slider
		presSlider = new JSlider(JSlider.VERTICAL, (int)World.minPres, (int)World.maxPres, (int)World.pres);
		presSlider.setName("presSlider");
		presSlider.addChangeListener(this);
		presSlider.setMajorTickSpacing(1000);
		presSlider.setPaintTicks(true);
		c.fill = GridBagConstraints.VERTICAL;
		c.ipady = 100;      //make it tall
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 3;
		c.gridy = 1;
		
		//Create the temperature label table.
        Hashtable<Integer, JLabel> presLabelTable = new Hashtable<Integer, JLabel>();
        for(int i = 0; i <= presSlider.getMaximum(); i += presSlider.getMajorTickSpacing())
        	presLabelTable.put(new Integer(i), new JLabel(""+i));
        presSlider.setLabelTable(presLabelTable);
        presSlider.setPaintLabels(true);
		pane.add(presSlider, c);
		
		// temperature field
		tempField = new JTextField(""+World.temp, 4);
		tempField.setName("tempField");
		tempField.setEditable(true);
		tempField.setFont(new Font("Verdana", Font.PLAIN, 15));
		tempField.addActionListener(this);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 3;
		pane.add(tempField, c);
		
		// pressure field
		presField = new JTextField(""+World.pres, 4);
		presField.setName("presField");
		presField.setEditable(true);
		presField.setFont(new Font("Verdana", Font.PLAIN, 15));
		presField.addActionListener(this);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 3;
		c.gridy = 3;
		pane.add(presField, c);
		
		// bp temperature button
		tempButton = new JButton("Find BP temperature");
		tempButton.setName("tempButton");
		tempButton.addActionListener(this);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 5;
		pane.add(tempButton, c);
		
		// bp pressure button
		presButton = new JButton("Find BP pressure");
		presButton.setName("presButton");
		presButton.addActionListener(this);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 3;
		c.gridy = 5;
		pane.add(presButton, c);
		
		// add the painted graphics as a JPanel
		beakerSim = new BeakerSim(25, 360, 35, 200, 250, 160);
		c.gridx = 5;
		c.gridy = 0;
		pane.add(beakerSim, c);
	}

	public static void restart()
	{
		BoilingPointAL main = new BoilingPointAL();
		main.start();
	}

	public void start()
	{
		//start the main loop
		isRunning = true;
		new Thread(this).start();
		requestFocus();
	}

	public void stop()
	{
		isRunning = false;
	}
	
	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource() instanceof JSlider)
		{
			JSlider source = (JSlider)e.getSource();
			
			double val = source.getValue();
			
	        if(source.getName().equals("tempSlider"))
	        	World.temp = val;
	        else if(source.getName().equals("presSlider"))
	        	World.pres = val;
			
			if(!source.getValueIsAdjusting())
			{
				if(source.getName().equals("tempSlider"))
		        	tempField.setText(""+World.temp);
		        else if(source.getName().equals("presSlider"))
		        	presField.setText(""+World.pres);
			}
		}
	}
	
	//called every button press, when you press enter in a text box, or when the timer fires
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof JTextField)			// if you entered a new value in a text field
		{
			JTextField textField = (JTextField)e.getSource();
			
			if(textField.getName().equals("tempField"))
			{
				tempSlider.setValue((int)Double.parseDouble(textField.getText()));	// set either temperature
			}
			else if(textField.getName().equals("presField"))
			{
				presSlider.setValue((int)Double.parseDouble(textField.getText()));	// or pressure
			}
		}
		else if(e.getSource() instanceof JButton)		// if you pressed a button
		{
			JButton button = (JButton)e.getSource();
			
			if(button.getName().equals("tempButton"))		// if it was the temperature button
			{
				double newTemp = World.getBPTemp();				// find the boiling point temperature
				World.temp = newTemp;							// and set the temperature to that
				tempField.setText(""+(int)(newTemp*10)/10.0);	// out to one decimal place
				tempSlider.setValue((int)newTemp);
			}
			else if(button.getName().equals("presButton"))	// otherwise
			{
				double newPres = World.getBPPres();			// do that to the pressure
				World.pres = newPres;
				presField.setText(""+(int)(newPres*10)/10.0);
				presSlider.setValue((int)newPres);
			}
		}
	}
	
	public void tick()
	{
//		if(frame.getWidth() != realSize.width || frame.getHeight() != realSize.height)
//			frame.pack();
		
		//call tick methods here
		beakerSim.tick();
	}

	public void render()
	{
		Graphics g = screen.getGraphics();
		
		super.paint(g);				// render the graphics components
		
//		g.setColor(new Color(140, 230, 0));
//		g.fillRect(0, 0, pixel.width, pixel.height);
		
		
		//call render methods here
		beakerSim.render(g);
		
//		g.setColor(Color.red);
//		g.drawLine(0, 0, pixel.width, pixel.height);
//		g.setColor(Color.green);
//		g.drawLine(0, pixel.height, pixel.width, 0);
		
		g = getGraphics();

		g.drawImage(screen, 0, 0, size.width, size.height, null);
		g.dispose();		// throw away to avoid lag from too many graphics objects
	}

	public void run()
	{
		screen = createVolatileImage(size.width, size.height);	//actually use the graphics card (less lag)
		
		render();
		
		while(isRunning)
		{
			if(!isPaused)
			{
				tick();			//do math and any calculations
				render();		//draw the objects
			}

			try
			{
				Thread.sleep(tickTime);
			}catch(Exception e){ }
		}
	}

	public static void main(String[] args)
	{
		BoilingPointAL main = new BoilingPointAL();

		frame = new JFrame();
		frame.add(main);
		frame.pack();

		frame.setTitle(windowName);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);		//null makes it go to the center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		main.start();
	}
}