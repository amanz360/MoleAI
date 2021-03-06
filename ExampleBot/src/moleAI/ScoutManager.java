package moleAI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class ScoutManager 
{
	
	public MoleUnit scoutWorker;
	private TilePosition scoutDest;
    public ArrayList<TilePosition> enemyBases;
    public HashSet<Position> enemyBuildingMemory = new HashSet<Position>();
    public Unit lastSeenEnemy;
    private int lastCheckedEnemy;
    private int lastCheckedSpawn;
    
    public ScoutManager()
    {
    	lastCheckedEnemy = 0;
    	lastCheckedSpawn = 0;
    	lastSeenEnemy = null;
    	scoutDest = null;
    	scoutWorker = null;
    	enemyBases = new ArrayList<TilePosition>();
    	enemyBuildingMemory = new HashSet<Position>();
    }
    
    public boolean noBuildingsKnown()
    {
    	return enemyBuildingMemory.isEmpty();
    }
    
    public void changeScout(MoleUnit scout)
    {
    	scoutWorker = scout;
    	scoutWorker.job = Information.Job.SCOUT;
    }
	
	public void update(Game game)
	{
		//always loop over all currently visible enemy units (even though this set is usually empty)
		for (Unit u : game.enemy().getUnits()) {
			if(game.enemy().getUnits().size() == 1)
			{
				lastSeenEnemy = u;
			}
			//if this unit is in fact a building
			if (u.getType().isBuilding()) {
				//check if we have it's position in memory and add it if we don't
				if (!enemyBuildingMemory.contains(u.getPosition())) 
				{
					enemyBuildingMemory.add(u.getPosition());
					//System.out.println("Adding position " + u.getPosition() + " to building memory" );
				}
			}
		}

		//loop over all the positions that we remember
		for (Position p : enemyBuildingMemory) {
			// compute the TilePosition corresponding to our remembered Position p
			TilePosition tileCorrespondingToP = new TilePosition(p.getX()/32 , p.getY()/32);
			
			//if that tile is currently visible to us...
			if (game.isVisible(tileCorrespondingToP)) {
				
				//loop over all the visible enemy buildings and find out if at least 
				//one of them is still at that remembered position 
				boolean buildingStillThere = false;
				for (Unit u : game.enemy().getUnits()) 
				{
					if (u.getType().isBuilding() && u.exists() && u.getPosition().getX() == p.getX() && u.getPosition().getY() == p.getY()) 
					{
						buildingStillThere = true;
						break;
					}
				}
				
				//if there is no more any building, remove that position from our memory 
				if (buildingStillThere == false) {
					enemyBuildingMemory.remove(p);
					System.out.println("Removing enemy building from memory at location: " + p.toString());
					break;
				}
			}
		}
		
		if(scoutWorker == null)
		{
			return;
		}
		
		if(!scoutWorker.myUnit.exists())
		{
			scoutWorker = null;
			scoutDest = null;
			return;
		}		
		
		if(scoutDest != null)
		{
			/*if(scoutWorker == null)
			{
				enemyBases.add(scoutDest);
				scoutDest = null;
			}*/
			if(scoutWorker.myUnit.getDistance(scoutDest.toPosition()) > 10)
			{
				scoutWorker.smartMove(scoutDest.toPosition(), game);
				return;
			}
			/*else if(scoutWorker.myUnit.getDistance(scoutDest.toPosition()) < 100 && enemyBases.contains(scoutDest))
			{
				Unit closestEnemy = scoutWorker.getClosestEnemyInRadius(100);
				scoutWorker.smartAttackUnit(closestEnemy, game);
			}*/
			else
			{
				if(enemyWorkerInRadius(game) && !enemyBases.contains(scoutDest))
				{
					System.out.println("Enemy base found!");
					enemyBases.add(scoutDest);
				}
				System.out.println("Scout reached destination: " + scoutDest.toString());
				//scoutDest = null;
			}
		}
		
		
		if(enemyBuildingMemory.isEmpty())
		{
			List<TilePosition> startLocations = game.getStartLocations();
			for (int i = 0; i < startLocations.size(); i++)
			{
				// if we haven't explored it yet
				if (!game.isExplored(startLocations.get(i))) 
				{
					// assign a worker to go scout it
					scoutWorker.smartMove(startLocations.get(i).toPosition(), game);
					scoutDest = startLocations.get(i);
					lastCheckedSpawn = i;
					return;
				}
			}
			
			List<BaseLocation> baseLocations = BWTA.getBaseLocations();
			for(BaseLocation location : baseLocations)
			{
				if(!game.isExplored(location.getTilePosition()))
				{
					scoutWorker.smartMove(location.getTilePosition().toPosition(), game);
					scoutDest = location.getTilePosition();
					return;
				}
			}
			List<Region> regions = game.getAllRegions();
			for(Region region : regions)
			{
				if(!game.isExplored(region.getPoint().toTilePosition()))
				{
					scoutWorker.smartMove(region.getPoint(), game);
					scoutDest = region.getPoint().toTilePosition();
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
	
	Position getClosestEnemyBuilding(Position commandSpot)
	{
		Position closestBuilding = null;
		for(Position p : enemyBuildingMemory)
		{
			if(closestBuilding == null || p.getDistance(commandSpot) < closestBuilding.getDistance(commandSpot))
			{
				closestBuilding = p;
			}
		}
		
		return closestBuilding;
	}
	
	boolean enemyWorkerInRadius(Game game)
	{
		for (Unit unit : game.enemy().getUnits())
		{
			if ((unit.getType().isWorker() || unit.getType().isResourceDepot() || unit.getType() == UnitType.Protoss_Probe) && (unit.getDistance(scoutWorker.myUnit) <= 600))
			{
				return true;
			}
		}

		return false;
	}
}
