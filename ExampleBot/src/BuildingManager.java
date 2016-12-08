import java.util.ArrayList;

import bwapi.Game;
import bwapi.Player;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class BuildingManager 
{
	private ArrayList<Building> buildings;
	public ArrayList<Unit> builders;
	
	public BuildingManager()
	{
		buildings = new ArrayList<Building>();
		builders = new ArrayList<Unit>();
	}
	
	public void update(Game game, Player self)
	{
		validateBuildings();					// check to see if assigned workers have died en route or while constructing
		assignWorkersToUnassignedBuildings();   // assign workers to the unassigned buildings and label them 'planned'    
	    constructAssignedBuildings(game);       // for each planned building, if the worker isn't constructing, send the command    
	    checkForStartedConstruction(self);      // check to see if any buildings have started construction and update data structures    
	    //checkForDeadTerranBuilders();         // if we are terran and a building is under construction without a worker, assign a new one    
	    checkForCompletedBuildings();           // check to see if any buildings have completed and update data structures
	}
	
	public void validateBuildings()
	{
		ArrayList<Building> buildingsToRemove = new ArrayList<Building>();

		for(Building b : buildings)
		{
			if(b.status != Building.BuildingStatus.UNDERCONSTRUCTION)
			{
				continue;
			}
			
			if(b.buildingUnit == null || !b.buildingUnit.getType().isBuilding() || b.buildingUnit.getHitPoints() <= 0)
			{
				buildingsToRemove.add(b);
			}
		}
				
		buildings.removeAll(buildingsToRemove);
	}
	
	public void assignWorkersToUnassignedBuildings()
	{
		for(Building b : buildings)
		{
			if(b.status != Building.BuildingStatus.UNASSIGNED)
			{
				continue;
			}
			
			Unit workerToAssign = getBuilder(b);
			
			if(workerToAssign != null)
			{
				System.out.println("Assigning worker to building type: " + b.type.toString());
				b.builderUnit = workerToAssign;
				b.status = Building.BuildingStatus.ASSIGNED;
			}
		}
	}
	
	public void constructAssignedBuildings(Game game)
	{
		for(Building b : buildings)
		{
			if(b.status != Building.BuildingStatus.ASSIGNED)
			{
				continue;
			}
			
			if(!b.builderUnit.isConstructing() && (b.builderUnit.isGatheringMinerals() || !b.builderUnit.isMoving()))
			{
				if(b.buildCommandGiven)
				{
					System.out.println("Retrying construction: " + b.type.toString());
					b.builderUnit = null;
					b.buildCommandGiven = false;
					b.status = Building.BuildingStatus.UNASSIGNED;
				}
				else
				{
					System.out.println("Worker ordered to construct: " + b.type.toString());
					TilePosition buildLocation = game.getBuildLocation(b.type, b.builderUnit.getTilePosition());
					b.builderUnit.build(b.type, buildLocation);
					b.finalPosition = buildLocation;
					b.buildCommandGiven = true;
				}
			}
		}
	}
	
	void checkForStartedConstruction(Player self)
	{
		for(Unit buildingStarted : self.getUnits())
		{
			if(!buildingStarted.getType().isBuilding() || !buildingStarted.isBeingConstructed())
			{
				continue;
			}
			
			for(Building b : buildings)
			{
				if(b.status != Building.BuildingStatus.ASSIGNED)
				{
					continue;
				}
				
				if(b.type == buildingStarted.getType())
				{
					System.out.println("Building is under construction: " + b.type.toString());
					b.underConstruction = true;
					b.buildingUnit = buildingStarted;
					b.status = Building.BuildingStatus.UNDERCONSTRUCTION;
					break;
				}
			}
		}
	}
	
	void checkForDeadTerranBuilders()
	{
		for(Building b : buildings)
		{
			if(b.status != Building.BuildingStatus.UNDERCONSTRUCTION)
			{
				continue;
			}
			
			if(!b.builderUnit.exists())
			{
				Unit replacement = getBuilder(new Building(b.buildingUnit.getType()));
				if(replacement != null)
				{
					System.out.println("Replacing dead builder");
					b.builderUnit = replacement;
					replacement.rightClick(b.finalPosition.toPosition());
				}
			}
		}
	}
	
	void checkForCompletedBuildings()
	{
		ArrayList<Building> toRemove = new ArrayList<Building>();
		
		for(Building b : buildings)
		{
			if(b.status != Building.BuildingStatus.UNDERCONSTRUCTION)
			{
				continue;
			}
			
			if(b.buildingUnit.isCompleted())
			{
				System.out.println("Building completed: " + b.type.toString());
				toRemove.add(b);
			}
		}
		
		buildings.removeAll(toRemove);
	}
	
	public boolean isBeingBuilt(UnitType buildingType)
	{
		for(Building b : buildings)
		{
			if(b.type.toString().equals(buildingType.toString()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void addBuildingTask(UnitType buildingType)
	{
		Building newBuilding = new Building(buildingType);
		newBuilding.status = Building.BuildingStatus.UNASSIGNED;
		buildings.add(newBuilding);
	}
	
	public Unit getBuilder(Building b)
	{
		Unit myBuilder = null;
		
		for(Unit builder : builders)
		{
			if(builder.isCompleted() && builder.canBuild(b.type) && !builder.isConstructing())
			{
				myBuilder = builder;
			}
		}
		
		return myBuilder;
	}
}
