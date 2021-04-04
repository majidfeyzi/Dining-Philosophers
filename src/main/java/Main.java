import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import javax.swing.*;
import java.awt.*;

public class Main {

	private final int WIDTH = 1024;
	private final int HEIGHT = WIDTH / 16 * 9;
	
    private int philosophersCount = 5;

	private JFrame frame;
	private Table table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		// Get count of philosophers must be odd
		philosophersCount = Integer.parseInt(JOptionPane.showInputDialog(
		        frame, 
		        "Enter the count of philosophers: ", 
		        "Get input", 
		        JOptionPane.DEFAULT_OPTION
		    ));
		if (philosophersCount <= 1)
			System.exit(0);
		
		frame = new JFrame();
		frame.setSize(WIDTH, HEIGHT);
		frame.setBackground(Color.white);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new Table(philosophersCount, new Point(WIDTH / 2, HEIGHT / 2));
		table.setBackground(Color.white);
		frame.getContentPane().add(table, BorderLayout.CENTER);
		
    	// Create actor system
    	ActorSystem system = ActorSystem.create("system");
    	
    	// Create fork objects
        Fork[] forks = new Fork[philosophersCount];
        for (int i = 0; i < philosophersCount; i++)
            forks[i] = new Fork();

        // Create philosophers and specify its left and right philosophers
        for (int i = 0; i < philosophersCount; i++) {
        	
            int index = i;
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % philosophersCount];
            String leftNeighbor = i == 0 ? String.valueOf(philosophersCount-1) : String.valueOf((i - 1) % philosophersCount);
            String rightNeighbor = i == 0 ? "1" : String.valueOf((i + 1) % philosophersCount);
            
            system.actorOf(Props.create(Philosopher.class,() -> new Philosopher(index, leftFork, rightFork, leftNeighbor, rightNeighbor)), String.valueOf(i));
            forks[i].setOwner(String.valueOf(i));
        }

        // Tell to philosophers to getting start
        for (int i = 0; i < philosophersCount; i++)
        {
            String path = system.child(String.valueOf(i)).toString();
            system.actorSelection(path).tell(Philosopher.START, ActorRef.noSender());
        }
        
		// Add monitor as actor to system to show states of philosophers when receive message
		Props monitor = Props.create(Monitor.class, () -> new Monitor(philosophersCount, table));
		system.actorOf(monitor, Monitor.NAME);
	}
}
