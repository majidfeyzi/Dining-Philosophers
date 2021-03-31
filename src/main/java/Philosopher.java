import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Philosopher extends AbstractActor implements Runnable{

	// Messages that philosophers can send and receive
	public final static String START = "Start";
	public final static String Eat = "Eat";
    
    // Keep that this philosopher is going to eat or not
    private boolean isGoingToEat = false;
    
    private final int index;
    private final Fork leftFork, rightFork;
    private String leftNeighbor, rightNeighbor;

    private final Thread thread;

    Philosopher(int index, Fork leftFork, Fork rightFork, String leftNeighbor, String rightNeighbor) {
        super();
        this.index = index;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.leftNeighbor = leftNeighbor;
        this.rightNeighbor = rightNeighbor;
        
        thread = new Thread(this);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(Eat, s -> {getSender().tell(String.valueOf(isGoingToEat), getSelf()); })
                .matchEquals(START, s -> { start(); })
                .build();
    }

    public void run() {
        while (true) {
        	
        	// Philosopher getting start to thinking in the beginning
            think();

            // Before eating philosopher check that other philosophers needs forks or not
            giveUpForks();
        	
        	// Philosopher is waiting to take left and right fork respectively
            takeForks();

            // After taking left and right fork philosopher getting start to eat
            eat();

            // After eating philosopher check that other philosophers needs forks or not
            giveUpForks();
        }
    }

    void takeForks() {
        isGoingToEat = true;
        waitForFork(leftFork);
        waitForFork(rightFork);
        isGoingToEat = false;
    }

    void waitForFork(Fork fork) {
    	
    	// Take fork if it isn't in current philosopher
        if (!fork.getOwner().equals(String.valueOf(index))) {
            synchronized (fork) {
                try {
                    while (!fork.getOwner().equals(String.valueOf(index))) {
                        giveUpForks();
                        fork.wait();
                    }
                } catch (InterruptedException e) {}
            }
        }

        // Update states after taking left fork or right fork
        String path = getContext().getSystem().child(Monitor.NAME).toString();
        ActorSelection monitor = getContext().getSystem().actorSelection(path);
        if (fork == leftFork)
        	monitor.tell(PhilosopherState.WaitingForLeftFork.toString(), getSelf());
        else
        	monitor.tell(PhilosopherState.WaitingForRightFork.toString(), getSelf());
    }

    void giveUpForks() {
        giveUpFork(leftFork, leftNeighbor);
        giveUpFork(rightFork, rightNeighbor);
    }

    void giveUpFork(Fork fork, String receiver) {

        synchronized (fork) {	
        	try {
        		
	        	// Ask from receiver that is going to start eating or not and give fork to receiver if need it
	            boolean isReceiverGoingToEat = false;
	            String path = getContext().getSystem().child(String.valueOf(receiver)).toString();
	            Timeout timeout = new Timeout(10, TimeUnit.SECONDS);
	            Future<Object> future = Patterns.ask(getContext().getSystem().actorSelection(path), Eat, timeout);
                String result = (String) Await.result(future, timeout.duration());
                if (result.equals("true"))
                	isReceiverGoingToEat = true;
	            
	            // If receiver is going to eat and fork owner is current philosopher and not using it so make clean and give fork to the receiver
	            if (isReceiverGoingToEat && !fork.isClean() && fork.getOwner().equals(String.valueOf(index))) {
	                fork.setClean(true);
	                fork.setOwner(receiver);
	                fork.notify();   
	            }
	            
        	} catch (Exception e) {}
        }
    }

    void eat() {
    	
    	// Right and left forks both will be dirty in getting start to eating
        rightFork.setClean(false);
        leftFork.setClean(false);

        // Update states before getting start eating
        String path = getContext().getSystem().child(Monitor.NAME).toString();
        ActorSelection monitor = getContext().getSystem().actorSelection(path);
    	monitor.tell(PhilosopherState.Eating.toString(), getSelf());
        	
        try {
            Thread.sleep(1000 + new Random().nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Update states after eating
    	monitor.tell(PhilosopherState.Thinking.toString(), getSelf());
    }

    void think() {

        // Update states before getting start thinking
        String path = getContext().getSystem().child(Monitor.NAME).toString();
        ActorSelection monitor = getContext().getSystem().actorSelection(path);
    	monitor.tell(PhilosopherState.Thinking.toString(), getSelf());
        	
        try {
            Thread.sleep(1000 + new Random().nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        thread.start();
    }
}
