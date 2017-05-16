package citi.assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import citi.assignment.comparators.ElevatorCompSort;
import citi.assignment.constants.ElevatorConstants;
import citi.assignment.enums.RequestType;

/**
 *  This class acts as an entry point to the application.
 *  It generates random request for GOING_UP and GOING_DOWN and assigns 
 *  to any of the Elevators present.
 */
public class ElevatorSystem 
{
	private static List<Elevator> elevatorList;
	private static Scheduler scheduler;
		
	public static void main(String[] args) throws InterruptedException 
	{
		scheduler=new Scheduler();
		createElevators();
		
		System.out.println("------Welcome to Citi------");
		
		System.out.println("-----Creating and Serving Request-----");
		for(int i=1;i<=10;i++)
		{
			Request request=requestGenerator();
			System.out.println(request);
			distributeTheRequest(request);
			Thread.sleep(1000);
		}
		
		/*//Serving the remainder requests
		for(Request request : waitingRequests)
		{
			distributeTheRequest(request);
			Thread.sleep(1000);
		}*/
		
	}
	
	/**
	 * Create n Elevators with customizable capacity
	 * for the Building.
	 */
	public static void createElevators()
	{
		int count=1;
		
		elevatorList=new ArrayList<Elevator>(ElevatorConstants.ELEVATOR_COUNT);
		
		while(count<=ElevatorConstants.ELEVATOR_COUNT)
		{
			Elevator elevator=Elevator.getBuilder()
					.elevatorNumber((short) count)
					.currentFloor((short)0)
					.isActive(true)
					.isGoingUp(true)
					.isUnderMaintenance(false)
					.isServingRequest(false)
					.queue(new PriorityBlockingQueue<Request>(ElevatorConstants.ELEVATOR_CAPACITY))
					.build();
			
			elevatorList.add(elevator);
			count++;
		}
	}
	
	/**
	 * Create random requests for GOING_UP or GOING_DOWN
	 * 
	 * @return {@link Request} 
	 */
	public static Request requestGenerator()
	{
		Random random=new Random();
		short goingTo=(short) random.nextInt(10);
		short from=(short) random.nextInt(10);
		Request request=null;
		
		boolean up=random.nextBoolean();
		
		if(up)
		{
			if(goingTo!=from && goingTo>from)
			{
				request=new Request(RequestType.REQUEST_GO_UP, goingTo, from);
			}
			else
			{
				request=requestGenerator();
			}
		}
		else
		{
			if(goingTo!=from && goingTo<from)
			{
				request=new Request(RequestType.REQUEST_GO_DOWN, goingTo, from);
			}
			else
			{
				request=requestGenerator();
			}
		}
		
		return request;
	}
	
	/**
	 * Distributes the request to the Elevators present in the building.
	 * 
	 * @param request
	 */
	public static void distributeTheRequest(Request request) throws InterruptedException
	{
		switch(request.getRequestType())
		{
		case REQUEST_GO_UP:
		case REQUEST_GO_DOWN:
			processGoingUpDownRequest(request);
			break;
		case REQUEST_START:
			// TODO Work on this part. Presently, it would be to set isActive parameter on the Elevator.
			// The scope need o check for full system impact.
			break;
		case REQUEST_STOP:
			// TODO Work on this part. Presently, it would be to set isActive parameter on the Elevator.
			// The scope need o check for full system impact.
			break;
		default:
			break;
		}
	}
	
	/**
	 * This method finds out eligible elevators for the REQUEST_TYPE and passes the request
	 * to those elevators to further find out a single Elevator which would process the request.
	 * 
	 * @param request
	 */
	public static void processGoingUpDownRequest(Request request) throws InterruptedException
	{
		List<Elevator> knowAvailableElevators=knowAvailableElevators(request.getRequestType());
		
		// This is to process if there are no elevators present
		if(knowAvailableElevators.size()==0)
		{
			checkIfAnyElevatorCouldBeForOppositeDirection(request);
		}
		else
		{
			dispatchRequestToElevator(request, knowAvailableElevators);
		}
	}
	
	/**
	 * Distributes the request to the Elevators present in the building.
	 * 
	 * @param requestType
	 * @return {@link List}
	 */
	public static List<Elevator> knowAvailableElevators(RequestType requestType)
	{
		List<Elevator> resultElevators=new LinkedList<Elevator>();
		for(Elevator elevator : elevatorList)
		{
			if(requestType==RequestType.REQUEST_GO_UP && elevator.isGoingUp())
			resultElevators.add(elevator);
			
			if(requestType==RequestType.REQUEST_GO_DOWN && !elevator.isGoingUp())
				resultElevators.add(elevator);
		}
		
		return resultElevators;
	}
	
	/**
	 * This method receives the eligible Elevator List and dispatches the request to an Elevator
	 * 
	 * @param request
	 * @param list
	 */
	public static void dispatchRequestToElevator(Request request , List<Elevator> list) throws InterruptedException
	{
		short personAtFloor=request.getAtFloor();
		scheduler.schedule(request, personAtFloor, list);
	}
	
	public static void checkIfAnyElevatorCouldBeForOppositeDirection(Request request) throws InterruptedException
	{
		// No Elevator is available for going upwards
		Elevator elevator;
		elevator=findElevatorWhichCanComeFirst(request.getRequestType());
		
		//TODO
		//This is an explicit action. Should be placed in Scheduler.
		if(elevator!=null)
		{
			elevator.canAcceptRequest(request);
		}
	}
	
	public static Elevator findElevatorWhichCanComeFirst(RequestType requestType)
	{
		List<Elevator> eligibleElevators= new ArrayList<Elevator>(ElevatorConstants.ELEVATOR_COUNT);
		Elevator elevator;
		
		// Finding elevators which are not busy
		for(Elevator elev : elevatorList)
		{
			if(!elev.isServingRequest())
			{
				eligibleElevators.add(elev);
			}
		}
		
		if(eligibleElevators.size()==0)
		{
			return null;
		}
		Collections.sort(eligibleElevators,new ElevatorCompSort<Elevator>());
		
		//This is the shortest to the ground floor
		if(requestType==RequestType.REQUEST_GO_UP)
		{
			elevator=eligibleElevators.get(0);
			elevator.setCurrentFloor((short) 0);
			elevator.setGoingUp(false);
		}
		else
		{
			elevator=eligibleElevators.get(eligibleElevators.size()-1);
			elevator.setCurrentFloor(ElevatorConstants.TOWER_FLOORS);
			elevator.setGoingUp(true);
		}
		return elevator;
	}
}
