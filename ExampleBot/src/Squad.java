import bwapi.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Squad
{
	private String name;
	private int capacity;
	private HashSet<MoleUnit> units;
	
	public Squad(String _name, int _capacity)
	{
		name = _name;
		capacity = _capacity;
		units = new HashSet<MoleUnit>();
	}
	
	public boolean containsUnit(MoleUnit unit)
	{
		return units.contains(unit);
	}
	
	public void addUnit(MoleUnit unit)
	{
		units.add(unit);
	}
	
	public void removeUnit(MoleUnit unit)
	{
		units.remove(unit);
	}
	
	public boolean isFull()
	{
		return units.size() == capacity;
	}
}
