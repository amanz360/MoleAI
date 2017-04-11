import bwapi.*;
import java.util.ArrayList;
public class SquadManager 
{
	public ArrayList<Squad> squads;
	
	public SquadManager()
	{
		squads = new ArrayList<Squad>();
	}
	
	public void addSquad(Squad newSquad)
	{
		if(!squads.contains(newSquad))
		{
			squads.add(newSquad);
		}
	}
	
	public void allocateUnit(MoleUnit newUnit)
	{
		for(Squad squad : squads)
		{
			if(!squad.isFull())
			{
				squad.addUnit(newUnit);
				return;
			}
		}
		Squad newSquad = new Squad("squad " + squads.size(), 8);
		newSquad.addUnit(newUnit);
	}

}
