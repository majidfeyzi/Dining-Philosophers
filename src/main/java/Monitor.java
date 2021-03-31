import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Monitor extends AbstractActor {

	// Actor name to access
	public static final String NAME = "Monitor";
	
	private final List<String> states = new ArrayList<>();
	private final Table table;
	
	public Monitor(int philosophersCount, Table table) {

		this.table = table;
		
		// Philosophers has thinking state in the beginning
		for (int i=0; i<philosophersCount; i++)
			states.add(PhilosopherState.Thinking.toString()); 
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(String.class, this::print).build();
	}

	private void print(String message) {
			// Format output in columns
			String format = "%-" + (100 / states.size()) + "s";
			states.set(Integer.parseInt(getSender().path().name()), message); 
			
			// Print states of philosophers 
			Arrays.stream(states.toArray()).forEach(s -> System.out.printf(format, s));
			System.out.print("\n");

			// Show states in table
			table.update(states);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
