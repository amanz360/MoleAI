package moleAI;
import bwapi.*;
import jbt.execution.core.ExecutionTask.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Squad
{
	private String name;
	private int capacity;
	private HashSet<MoleUnit> units;
	public Game _game;
	public Unit squadTarget;
	
	public Squad(String _name, int _capacity)
	{
		name = _name;
		capacity = _capacity;
		units = new HashSet<MoleUnit>();
		squadTarget = null;
		_game = null;
	}
	
	public String getName()
	{
		return name;
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
	
	public void tickUnits()
	{
		for(MoleUnit unit : units)
		{
			if(unit.behaviorExecutor.getStatus() == Status.RUNNING || unit.behaviorExecutor.getStatus() == Status.UNINITIALIZED)
			{
				unit.tickBehavior();
			}
		}
	}
	
	public void drawSquadName(Game game)
	{
		for(MoleUnit unit : units)
		{
			game.drawTextMap(unit.myUnit.getPosition().getX(), unit.myUnit.getPosition().getY(), "\n"+name);
		}
	}
	
	public Position getCenterOfSquad()
	{
		List<MoleUnit> myUnits = new ArrayList<MoleUnit>(units);
		return MoleUnit.getCenterOfUnits(myUnits);
	}
	
	public Unit closestEnemy(Game game)
	{
		Position center = getCenterOfSquad();
		List<Unit> enemies = game.getUnitsInRadius(center, 400);
		
		Unit closest = null;
		for(Unit enemy : enemies)
		{
			if(enemy.getPlayer() != game.enemy())
			{
				continue;
			}
			if((enemy.isCloaked() || enemy.isBurrowed()) && !enemy.isDetected())
			{
				continue;
			}
			if(closest == null)
			{
				closest = enemy;
			}
			else if(enemy.getDistance(center) < closest.getDistance(center))
			{
				closest = enemy;
			}
		}
		squadTarget = closest;
		_game = game;
		return closest;
	}
}
