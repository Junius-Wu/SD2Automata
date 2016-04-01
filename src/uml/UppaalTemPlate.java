package uml;

import java.util.ArrayList;

public class UppaalTemPlate 
{
	String Name;
	String declaration;
	ArrayList<UppaalLocation> locations=new ArrayList<UppaalLocation>();
	ArrayList<UppaalTransition>transitions=new ArrayList<UppaalTransition>();
	
	public String getName() 
	{
		return Name;
	}
	public void setName(String name) 
	{
		Name = name;
	}
	public ArrayList<UppaalLocation> getLocations() 
	{
		return locations;
	}
	public void setLocations(ArrayList<UppaalLocation> locations) 
	{
		this.locations = locations;
	}
	public ArrayList<UppaalTransition> getTransitions() 
	{
		return transitions;
	}
	public void setTransitions(ArrayList<UppaalTransition> transitions) 
	{
		this.transitions = transitions;
	}
}
