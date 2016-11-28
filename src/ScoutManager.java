import java.util.ArrayList;
import java.util.List;

import bwapi.*;
import bwta.BaseLocation;

public class ScoutManager 
{
	
	public Unit scoutWorker;
	private TilePosition scoutDest;
    public ArrayList<TilePosition> enemyBases;
    private int lastCheckedEnemy;
    private int lastCheckedSpawn;
    
    public ScoutManager()
    {
    	lastCheckedEnemy = 0;
    	lastCheckedSpawn = 0;
    	scoutDest = null;
    	scoutWorker = null;
    	enemyBases = new ArrayList<TilePosition>();
    }
	
	public void update(Game game)
	{
		if(scoutWorker == null)
		{
			return;
		}
		
		if(!scoutWorker.exists())
		{
			scoutWorker = null;
			return;
		}		
		
		if(scoutDest != null)
		{
			if(scoutWorker == null)
			{
				enemyBases.add(scoutDest);
				scoutDest = null;
			}
			if(scoutWorker.getDistance(scoutDest.toPosition()) > 35)
			{
				scoutWorker.move(scoutDest.toPosition());
				return;
			}
			else
			{
				if(enemyWorkerInRadius(game) && !enemyBases.contains(scoutDest))
				{
					System.out.println("Enemy base found!");
					enemyBases.add(scoutDest);
				}
				System.out.println("Scout reached destination: " + scoutDest.toString());
				scoutDest = null;
			}
		}
		
		
		if(enemyBases.size() == 0)
		{
			List<TilePosition> startLocations = game.getStartLocations();
			for (int i = 0; i < startLocations.size(); i++)
			{
				// if we haven't explored it yet
				if (!game.isExplored(startLocations.get(i))) 
				{
					// assign a worker to go scout it
					scoutWorker.move(startLocations.get(i).toPosition());
					scoutDest = startLocations.get(i);
					lastCheckedSpawn = i;
					return;
				}
			}
		}
		else
		{
			return;
			/**scoutDest = enemyBases.get(lastCheckedEnemy);
			scoutWorker.move(enemyBases.get(lastCheckedEnemy++).toPosition());
			
			if(lastCheckedEnemy == enemyBases.size())
			{
				lastCheckedEnemy = 0;
			}**/
		}
	}
	
	boolean enemyWorkerInRadius(Game game)
	{
		for (Unit unit : game.enemy().getUnits())
		{
			if ((unit.getType().isWorker() || unit.getType().isResourceDepot() || unit.getType() == UnitType.Protoss_Probe) && (unit.getDistance(scoutWorker) <= 600))
			{
				return true;
			}
		}

		return false;
	}
}
