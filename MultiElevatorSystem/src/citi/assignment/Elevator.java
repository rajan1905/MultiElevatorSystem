package citi.assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;

import citi.assignment.constants.ElevatorConstants;
import citi.assignment.enums.RequestType;
import citi.assignment.interfaces.ElevatorInterface;

/**
 * 
 * This is an Elevator class which accepts Request actions to perform
 * upon.
 * 
 * @author rajan.singh
 *
 */
public class Elevator implements ElevatorInterface,Serializable
{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private short elevatorNumber;
	private BlockingQueue<Request> queue;
	private Thread serviceThread;
	private boolean isGoingUp;
	private short currentFloor;
	private boolean isActive;
	private boolean isUnderMaintenance;
	private ArrayList<Short> stoppingPointsPick;
	private ArrayList<Short> stoppingPointsDrop;
	private boolean isServingRequest;

	//Suppresses default constructor, ensuring non-instantiability.
	private Elevator(ElevatorBuilder elevatorBuilder)
	{
		elevatorNumber=elevatorBuilder.elevatorNumber;
		queue=elevatorBuilder.queue;
		isGoingUp=elevatorBuilder.isGoingUp;
		currentFloor=elevatorBuilder.currentFloor;
		isActive=elevatorBuilder.isActive;
		isUnderMaintenance=elevatorBuilder.isUnderMaintenance;
		isServingRequest=elevatorBuilder.isServingRequest;
		setServiceThread();
	}
	
	public static ElevatorBuilder getBuilder()
	{
		return new ElevatorBuilder();
	}
	
	public static class ElevatorBuilder
	{
		short elevatorNumber;
		BlockingQueue<Request> queue;
		private boolean isGoingUp;
		private short currentFloor;
		private boolean isActive;
		private boolean isUnderMaintenance;
		private boolean isServingRequest;
		
		public ElevatorBuilder elevatorNumber(short elevatorNumbr)
		{
			elevatorNumber=elevatorNumbr;
			return this;
		}
		
		public ElevatorBuilder queue(BlockingQueue<Request> theQueue)
		{
			queue=theQueue;
			return this;
		}
		
		public ElevatorBuilder isGoingUp(boolean goingUp)
		{
			isGoingUp=goingUp;
			return this;
		}
		
		public ElevatorBuilder currentFloor(short theCurrentFloor)
		{
			currentFloor=theCurrentFloor;
			return this;
		}
		
		public ElevatorBuilder isActive(boolean active)
		{
			isActive=active;
			return this;
		}
		
		public ElevatorBuilder isUnderMaintenance(boolean underMaintenance)
		{
			isUnderMaintenance=underMaintenance;
			return this;
		}
		
		public ElevatorBuilder isServingRequest(boolean servingRequest)
		{
			isServingRequest=servingRequest;
			return this;
		}
		public Elevator build()
		{
			return new Elevator(this);
		}
	}
	
	public void setServiceThread() 
	{
		if(serviceThread==null)
		{
			stoppingPointsPick=new ArrayList<Short>(1);
			stoppingPointsDrop=new ArrayList<Short>(1);
			serviceThread=new Thread(new ServiceThread(queue));
			serviceThread.start();
		}
	}

	@Override
	public void addRequest(Request request) throws InterruptedException 
	{
		if(request!=null)
		queue.put(request);
	}
		
	@Override
	public void processRequest(Request request) throws InterruptedException 
	{
		short queueSize=(short) queue.size();
		Request requestBackup=request;
	
		isServingRequest=true;
		// First Going to the floor to pick the person
		if(request.getRequestType()==RequestType.REQUEST_GO_UP)
		{
		while(currentFloor<request.getAtFloor())
		{
			currentFloor++;
			
			//A new request is in and it falls in our path
			if(queueSize!=queue.size())
			{
				queue.put(request);
				Request checkForNewRequest=queue.take();
				
				if(!checkForNewRequest.equals(requestBackup))
					request=checkForNewRequest;
			}
			
			if(stoppingPointsPick.size()>0)
				if(currentFloor==stoppingPointsPick.get(0))
					{
						System.out.println("Elevator : "+elevatorNumber+" stopped to pick at floor : "+currentFloor);
						stoppingPointsPick.remove(0);
					}
				
				if(stoppingPointsDrop.size()>0)
					if(currentFloor==stoppingPointsDrop.get(0))
						{
							System.out.println("Elevator : "+elevatorNumber+" stopped to drop at floor : "+currentFloor);
							stoppingPointsDrop.remove(0);
						}
				Thread.sleep(ElevatorConstants.TIME_TO_REACH_A_SINGLE_FLOOR);
				
		}
		}
		else
		{
			while(currentFloor>request.getAtFloor())
			{
				currentFloor--;
				
				//A new request is in and it falls in our path
				if(queueSize!=queue.size())
				{
					queue.put(request);
					Request checkForNewRequest=queue.take();
					
					if(!checkForNewRequest.equals(requestBackup))
						request=checkForNewRequest;
				}
				
				if(stoppingPointsPick.size()>0)
					if(currentFloor==stoppingPointsPick.get(0))
						{
							System.out.println("Elevator : "+elevatorNumber+" stopped to pick at floor : "+currentFloor);
							stoppingPointsPick.remove(0);
						}
					
					if(stoppingPointsDrop.size()>0)
						if(currentFloor==stoppingPointsDrop.get(0))
							{
								System.out.println("Elevator : "+elevatorNumber+" stopped to drop at floor : "+currentFloor);
								stoppingPointsDrop.remove(0);
							}
					Thread.sleep(ElevatorConstants.TIME_TO_REACH_A_SINGLE_FLOOR);
					
			}
		}
		if(currentFloor==ElevatorConstants.TOWER_FLOORS)
		{
			setGoingUp(false);
			System.out.println("Elevator : "+elevatorNumber+" is now going DOWN");
		}
		else if(currentFloor==0)
		{
			setGoingUp(true);
			System.out.println("Elevator : "+elevatorNumber+" is now going UP");
		}
		
		isServingRequest=false;
	}

	@Override
	public void handleEmergency()
	{
		//TODO This needs to be worked on.
	}
	
	public BlockingQueue<Request> getQueue()
	{
		return queue;
	}

	public void setQueue(BlockingQueue<Request> queue) 
	{
		this.queue = queue;
	}

	public boolean isGoingUp() 
	{
		return isGoingUp;
	}

	public void setGoingUp(boolean isGoingUp) 
	{
		this.isGoingUp = isGoingUp;
	}

	public short getCurrentFloor() 
	{
		return currentFloor;
	}

	public void setCurrentFloor(short currentFloor) 
	{
		this.currentFloor = currentFloor;
	}

	public boolean isServingRequest() 
	{
		return isServingRequest;
	}

	public void setServingRequest() 
	{
		if(isServingRequest==false)
		{
			if(currentFloor>((ElevatorConstants.TOWER_FLOORS/2)+1))
				setGoingUp(false);		
		}
	}

	public boolean isActive() 
	{
		return isActive;
	}

	public void setActive(boolean isActive) 
	{
		this.isActive = isActive;
	}

	public boolean isUnderMaintenance() 
	{
		return isUnderMaintenance;
	}

	public void setUnderMaintenance(boolean isUnderMaintenance) 
	{
		this.isUnderMaintenance = isUnderMaintenance;
	}

	private class ServiceThread implements Runnable
	{
		BlockingQueue<Request> queue;
		
		public ServiceThread(BlockingQueue<Request> theQueue) 
		{
			queue=theQueue;
		}
		
		@Override
		public void run() 
		{
			//Run indefinitely once started
			while(true)
			{
				try 
				{
					Request request=queue.take();
					processRequest(request);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		return "Elevator# : "+elevatorNumber+" at floor : "+currentFloor;
	}

	@Override
	public boolean canAcceptRequest(Request request) throws InterruptedException 
	{
		if(queue.remainingCapacity()>0 && isActive && !isUnderMaintenance)
		{
			addRequest(request);
			calculateStoppingPoint(request);
			
			return true;
		}
		return false;
	}
	
	public void calculateStoppingPoint(Request request)
	{
		stoppingPointsPick.add(request.getAtFloor());
		stoppingPointsDrop.add(request.getGoingToFloor());
		
		Collections.sort(stoppingPointsPick);
		Collections.sort(stoppingPointsDrop);
	}
}
