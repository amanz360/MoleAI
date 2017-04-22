package moleAI;
import bwapi.*;
import java.util.ArrayList;

import jbt.execution.core.BTExecutorFactory;
import jbt.execution.core.ContextFactory;
import jbt.execution.core.IBTLibrary;
import jbt.execution.core.IContext;
import btlibrary.*;
public class SquadManager 
{
	public ArrayList<Squad> squads;
	private IBTLibrary btLibrary;
	
	public SquadManager()
	{
		squads = new ArrayList<Squad>();
		btLibrary = new TerranMarineBTLibrary();
	}
	
	public void addSquad(Squad newSquad)
	{
		if(!squads.contains(newSquad))
		{
			squads.add(newSquad);
		}
	}
	
	public void update(Game game)
	{
		for(Squad squad : squads)
		{
			squad.closestEnemy(game);
			squad.tickUnits();
			squad.drawSquadName(game);
		}
	}
	
	public void allocateUnit(MoleUnit newUnit, Position rallyPos)
	{
		IContext context = ContextFactory.createContext(btLibrary);
		context.setVariable("CurrentEntity", newUnit);
		context.setVariable("rallyPosition", rallyPos);
		
		for(Squad squad : squads)
		{
			if(!squad.isFull())
			{
				squad.addUnit(newUnit);
				context.setVariable("squad", squad);
				System.out.println("Adding unit to squad");
				newUnit.setBehavior(BTExecutorFactory.createBTExecutor(btLibrary.getBT("TerranMarine"), context));
				return;
			}
		}
		System.out.println("Making new squad");
		Squad newSquad = new Squad("squad " + squads.size(), 8);
		newSquad.addUnit(newUnit);
		context.setVariable("squad", newSquad);
		newUnit.setBehavior(BTExecutorFactory.createBTExecutor(btLibrary.getBT("TerranMarine"), context));
		squads.add(newSquad);
	}

}
