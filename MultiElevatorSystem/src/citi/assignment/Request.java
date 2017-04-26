package citi.assignment;

import citi.assignment.enums.RequestType;

public class Request implements Comparable<Request>
{
	final private String personName;
	final private RequestType requestType;
	final private short goingToFloor;
	final private short atFloor;

	Request(String name, RequestType reqType, short gngToFloor, short atFlor)
	{
		personName=name;
		requestType=reqType;
		goingToFloor=gngToFloor;
		atFloor=atFlor;
	}

	public RequestType getRequestType() 
	{
		return requestType;
	}

	public short getGoingToFloor() 
	{
		return goingToFloor;
	}

	public short getAtFloor() 
	{
		return atFloor;
	}
	
	@Override
	public String toString()
	{
		return "Request [Person name = "+personName+" Type = "+requestType+", Going To Floor = "+goingToFloor+", From floor = "+atFloor+"]";
	}

	@Override
	public int compareTo(Request o) 
	{
		if(this.atFloor<o.atFloor)
			return -1;
		
		else if(this.atFloor>o.atFloor)
			return 1;
		
		return 0;
	}
}
