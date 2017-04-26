package citi.assignment.interfaces;

import citi.assignment.Request;

public interface ElevatorInterface 
{
	public void addRequest(Request request) throws InterruptedException;
	public void processRequest(Request request) throws InterruptedException;
	public void handleEmergency();
	public boolean canAcceptRequest(Request request)throws InterruptedException;
}
