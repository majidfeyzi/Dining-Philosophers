import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Table extends JPanel {
	
	// Count of philosophers around table
	private int philosophersCount = 0;
	
	// States of philosophers to show
	private List<String> states = new ArrayList<String>();

	private final int tableSize = 768;
    private final int philosopherSize = 150;
    private final int philosopherHandSize = philosopherSize / 2;
    private final int forkSize = philosopherHandSize;
    private final Point tableCenter;
	
    private Graphics2D graphics;
    
    public Table(int philosophersCount, Point tableCenter) {
    	this.philosophersCount = philosophersCount;
    	this.tableCenter = tableCenter;
    	
		// Philosophers has thinking state in the beginning
		for (int i=0; i<philosophersCount; i++)
			states.add(PhilosopherState.Thinking.toString()); 
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
        graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setFont(new Font("Tahoma", Font.BOLD, 30)); 
        
        drawTable();
    }
    
    /*
     * Draw dining table
     * */
    public void drawTable() {
        graphics.drawOval(tableCenter.getX() - (tableSize / 2), tableCenter.getY() - (tableSize / 2), tableSize, tableSize);
    	
        // Draw philosophers
        float pangle = 0;
        for (int i = 0; i < philosophersCount; i++)
        {
            if (states.get(i).equals(PhilosopherState.Eating.toString()))
                drawPhilosopher(tableSize / 2, pangle, String.valueOf(i + 1), true, true);
            else if (states.get(i).equals(PhilosopherState.WaitingForRightFork.toString()))
                drawPhilosopher(tableSize / 2, pangle, String.valueOf(i + 1), true, false);
            else
                drawPhilosopher(tableSize / 2, pangle, String.valueOf(i + 1), false, false);

            // Compute next philosopher position angle according to philosophers count
            pangle += (360 / philosophersCount);
        }

        // Draw forks between philosophers 
        float fangle = (360 / philosophersCount) / 2;
        for (int i = 0; i < philosophersCount; i++)
        {
            int next = i + 1 == philosophersCount ? 0 : i + 1;
            if (states.get(i).equals(PhilosopherState.Eating.toString()) || 
            		states.get(i).equals(PhilosopherState.WaitingForRightFork.toString()) ||
            		states.get(next).equals(PhilosopherState.Eating.toString()))
            drawFork((tableSize / 2) - philosopherSize, fangle, String.valueOf(i + 1), true);
            else
                drawFork((tableSize / 2) - philosopherSize, fangle, String.valueOf(i + 1), false);

            // Compute next fork position angle according to forks count
            fangle += (360 / philosophersCount);
        }
    }
    
    /*
     * Draw philosophers circle using radius (from table center) and angle
     * */
    public void drawPhilosopher(float radius, float angle, String name, boolean hasLeftFork, boolean hasRightFork) {

        // Compute center of philosopher circle
        int centerX = (int)((float)(radius * Math.cos(angle * Math.PI / 180)) + tableCenter.getX());
        int centerY = (int)((float)(radius * Math.sin(angle * Math.PI / 180)) + tableCenter.getY());
        
        // Draw philosopher circle in blue color and show its name inside circle
        graphics.setColor(Color.blue);
        graphics.fillOval(centerX - (philosopherSize / 2), centerY - (philosopherSize / 2), philosopherSize, philosopherSize);
    	graphics.setColor(Color.white);
        graphics.drawString(name, centerX - 10, centerY + 5);
        
        // Change (decrease) radius of hands
        float hradius = radius - 10;
        
        // Compute center of philosopher left hand circle
        float langle = angle + 10;
        int centerLX = (int)((float)(hradius * Math.cos(langle * Math.PI / 180)) + tableCenter.getX());
        int centerLY = (int)((float)(hradius * Math.sin(langle * Math.PI / 180)) + tableCenter.getY());
        
        // Draw philosopher left hand
        if (hasLeftFork)
        	graphics.setColor(Color.green);
        else
        	graphics.setColor(Color.lightGray);
        graphics.fillOval(centerLX - (philosopherHandSize / 2), centerLY - (philosopherHandSize / 2), philosopherHandSize, philosopherHandSize);
    	graphics.setColor(Color.black);
        graphics.drawString("L", centerLX - 10, centerLY + 5);
        
        // Compute center of philosopher right hand circle
        float rangle = angle - 10;
        int centerRX = (int)((float)(hradius * Math.cos(rangle * Math.PI / 180)) + tableCenter.getX());
        int centerRY = (int)((float)(hradius * Math.sin(rangle * Math.PI / 180)) + tableCenter.getY());

        // Draw philosopher right hand
        if (hasRightFork)
        	graphics.setColor(Color.green);
        else
        	graphics.setColor(Color.lightGray);
        graphics.fillOval(centerRX - (philosopherHandSize / 2), centerRY - (philosopherHandSize / 2), philosopherHandSize, philosopherHandSize);
    	graphics.setColor(Color.black);
        graphics.drawString("R", centerRX - 10, centerRY + 5);
    }
    
    /*
     * Draw forks circle using radius (from table center) and angle
     * */
    public void drawFork(float radius, float angle, String name, boolean isInUse) {

        // Compute center of philosopher circle
        int centerX = (int)((float)(radius * Math.cos(angle * Math.PI / 180)) + tableCenter.getX());
        int centerY = (int)((float)(radius * Math.sin(angle * Math.PI / 180)) + tableCenter.getY());

        // Draw fork circle in green color
        if (!isInUse) {
        	graphics.setColor(Color.green);
            graphics.fillOval(centerX - (forkSize / 2), centerY - (forkSize / 2), forkSize, forkSize);
        } else {
        	graphics.setColor(Color.black);
            graphics.drawOval(centerX - (forkSize / 2), centerY - (forkSize / 2), forkSize, forkSize);
        }
    	graphics.setColor(Color.black);
        graphics.drawString(name, centerX - 10, centerY + 5);
    }

    /*
     * Update philosophers states and then show changes 
     * */
    public void update(List<String> states) {
    	
    	this.states = states;
		repaint();
    }
}
