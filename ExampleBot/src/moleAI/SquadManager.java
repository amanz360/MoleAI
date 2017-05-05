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
	private Position enemyBase;
	
	public SquadManager()
	{
		squads = new ArrayList<Squad>();
		btLibrary = new TerranMarineBTLibrary();
		enemyBase = null;
	}
	
	public void addSquad(Squad newSquad)
	{
		if(!squads.contains(newSquad))
		{
			squads.add(newSquad);
		}
	}
	
	public void update(Game game, Position enemy)
	{
		if(enemy != null)
		{
			enemyBase = enemy;
		}
		for(Squad squad : squads)
		{
			if(enemyBase != null)
			{
				squad.rallyPosition = enemyBase;
			}
			squad.closestEnemy(game);
			squad.tickUnits();
			squad.drawSquadName(game);
		}
	}
	
	public void removeUnit(MoleUnit oldUnit)
	{
		for(Squad squad : squads)
		{
			if(squad.containsUnit(oldUnit))
			{
				squad.removeUnit(oldUnit);
				break;
			}
		}
	}
	
	public void allocateUnit(MoleUnit newUnit, Position defaultRally, Position retreatPosition)
	{
		IContext context = ContextFactory.createContext(btLibrary);
		context.setVariable("CurrentEntity", newUnit);
		context.setVariable("retreatPosition", retreatPosition);
		
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
		newSquad.rallyPosition = defaultRally;
		context.setVariable("squad", newSquad);
		newUnit.setBehavior(BTExecutorFactory.createBTExecutor(btLibrary.getBT("TerranMarine"), context));
		squads.add(newSquad);
	}

}
