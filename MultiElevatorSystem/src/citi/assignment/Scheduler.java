package citi.assignment;

import java.util.Collections;
import java.util.List;

import citi.assignment.comparators.ElevatorCompSort;
import citi.assignment.enums.RequestType;

/**
 * This is a Scheduler class which the schedules the service request 
 * to any Elevator in the System.
 * 
 * @author rajan.singh
 *
 */
public class Scheduler 
{
	/**
	 * 
	 * This method schedules the request to the Elevator which is eligible
	 * for the request.
	 * 
	 * The algorithm used here is SCAN.
	 * 
	 * @param request
	 * @param atFloor
	 * @param eligibleElevators
	 * @throws InterruptedException
	 */
	public void schedule(Request request, short atFloor, List<Elevator> eligibleElevators) throws InterruptedException
	{
		Elevator elevator=null;
		
		// TODO The algo needs to be worked upon for this part.
		
		//Check for waitingRequests and see if we can process them
		/*
		for(Request req : waitingRequests)
		{
			if(req.getRequestType()==request.getRequestType())
			{
				schedule(request, req.getAtFloor(), eligibleElevators);
				waitingRequests.remove(req);
			}
		}
		*/
		
		// Find if any elevator is at current floor.
		for(Elevator elev : eligibleElevators)
		{
			if(elev.getCurrentFloor()==atFloor && elev.isActive() && elev.getQueue().remainingCapacity()>0)
				{
					System.out.println("An elevator is present at same floor");
					elevator=elev;
					elevator.canAcceptRequest(request);
					return;
				}
		}
		
		//Check if some elevator on the way can pick you up. Sorting for nearest Elevator.
		Collections.sort(eligibleElevators, new ElevatorCompSort<Elevator>());
		
		if(request.getRequestType()==RequestType.REQUEST_GO_UP)
		{
			// Check if any lift already present at current floor and going up.
			for(Elevator elev : eligibleElevators)
			{
				if(elev.canAcceptRequest(request) && elev.getCurrentFloor()<atFloor)
				{
					elevator=elev;
					break;
				}
			}
		}
		else if(request.getRequestType()==RequestType.REQUEST_GO_DOWN)
		{
			// Check if any lift already present at current floor and going up.
			for(Elevator elev : eligibleElevators)
			{
				if(elev.canAcceptRequest(request) && elev.getCurrentFloor()>atFloor)
				{
					elevator=elev;
					break;
				}
			}
		}
	}
}
