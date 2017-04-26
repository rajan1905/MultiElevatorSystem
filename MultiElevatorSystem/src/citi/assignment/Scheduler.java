package citi.assignment;

import java.util.Collections;
import java.util.List;

import citi.assignment.comparators.ElevatorCompSort;
import citi.assignment.enums.RequestType;

public class Scheduler 
{
	public void schedule(Request request, short atFloor, List<Elevator> eligibleElevators, List<Elevator> allElevators) throws InterruptedException
	{
Elevator elevator=null;
		
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
		else
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
		
		// No elevator present for current floor. Need to find the Elevator which could be the first to serve.
		if(elevator==null)
		{	Collections.sort(allElevators,new ElevatorCompSort<Elevator>());
			elevator=allElevators.get(0);
			RequestType reqType=(request.getRequestType()==RequestType.REQUEST_GO_UP)?RequestType.REQUEST_GO_DOWN:request.getRequestType();
			Request req=new Request("", reqType , request.getAtFloor(), elevator.getCurrentFloor());
			elevator.canAcceptRequest(req);
			elevator.canAcceptRequest(request);
		}
	}
}
