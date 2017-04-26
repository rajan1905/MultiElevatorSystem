package citi.assignment.comparators;

import java.util.Comparator;

import citi.assignment.Elevator;

public class ElevatorCompSort<T> implements Comparator<T> 
{
	@Override
	public int compare(T o1, T o2) 
	{
		Elevator elevator1=(Elevator) o1;
		Elevator elevator2=(Elevator) o2;
		
		if(elevator1.getCurrentFloor()>elevator2.getCurrentFloor())
		{
			return -1;
		}
		else if(elevator1.getCurrentFloor()<elevator2.getCurrentFloor())
		{
			return 1;
		}
		return 0;
	}

}
