
import bwapi.*;
import bwapi.Error;
import bwta.BWTA;
import bwta.BaseLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;


public class Base {
	
	public ArrayList<Unit> minerals;
	public ArrayList<Unit> gases;
	public Map<Unit, Integer> gasWorkerCount;	
	public HashSet<MoleUnit> baseUnits;
	public HashSet<Unit> buildingMemory;
	public Unit commandCenter;
	public Position offensivePosition;
	public Information.BaseState state;
	public List<Unit> enemiesInBase;
	public Information.ProductionLevel productionLevel;
	
	Base()
	{
		minerals = new ArrayList<Unit>();
		gases = new ArrayList<Unit>();
		enemiesInBase = new ArrayList<Unit>();
		buildingMemory = new HashSet<Unit>();
		baseUnits = new HashSet<MoleUnit>();
		gasWorkerCount = new HashMap<Unit, Integer>();
		state = Information.BaseState.SAFE;
		productionLevel = Information.ProductionLevel.NONE;
		offensivePosition = null;
	}
	
	boolean gasesSaturated()
	{
		for(Unit refinery : gases)
		{
			if(numGasWorkersOnRefinery(refinery) < 3)
			{
				return false;
			}
		}
		return true;
	}
	
	void setCC(Unit cc)
	{
		this.commandCenter = cc;
	}
	
	void setBaseInfo()
	{
		List<Unit> units = this.commandCenter.getUnitsInRadius(500);
        System.out.println(units.size());
        for(Unit resource : units)
        {
        	if(resource.getType() == UnitType.Resource_Mineral_Field)
        	{
        		this.minerals.add(resource);
        	}
        	if(resource.getType() == UnitType.Resource_Vespene_Geyser)
        	{
        		this.gases.add(resource);
        		this.gasWorkerCount.put(resource, 0);
        	}
        }
        
        System.out.println("New base num minerals: " + minerals.size());
	}
	
	int numGasWorkersOnRefinery(Unit refinery)
	{
		int numWorkers = 0;
		for(MoleUnit worker : baseUnits)
		{
			if(worker.type == Information.UnitType.WORKER && worker.job == Information.Job.GAS && worker.myTarget.getUnit() == refinery)
			{
				if(worker.myTarget.getUnit() == refinery)
				{
					numWorkers++;
				}
			}
		}
		
		return numWorkers;
	}
	
	List<MoleUnit> getAllUnitsByType(Information.UnitType type)
	{
		ArrayList<MoleUnit> desired = new ArrayList<MoleUnit>();
		for(MoleUnit unit : baseUnits)
		{
			if(unit.type == type)
			{
				desired.add(unit);
			}
		}
		
		return desired;
	}
	
	ArrayList<Unit> getBuildingsByType(UnitType buildingType)
	{
		ArrayList<Unit> buildings = new ArrayList<Unit>();
		for(Unit b : buildingMemory)
		{
			if(b.getType() == buildingType)
			{
				buildings.add(b);
			}
		}
		
		return buildings;
	}
	
	boolean addBuilding(Unit building)
	{
		if(!buildingMemory.contains(building))
		{
			buildingMemory.add(building);
			return true;
		}
		
		return false;
	}
	
	
	boolean addUnit(MoleUnit unit)
	{
		if(!alreadyHaveUnit(unit))
		{
			baseUnits.add(unit);
			if(unit.type == Information.UnitType.WORKER && unit.job == Information.Job.UNDEFINED)
			{
				unit.job = Information.Job.MINERALS;
			}
			return true;
		}
		return false;		
	}
	
	boolean alreadyHaveUnit(MoleUnit unit)
	{
		for(MoleUnit myUnit : baseUnits)
		{
			if(myUnit.myUnit == unit.myUnit)
			{
				return true;
			}
		}
		
		return false;
	}
	
	boolean mineralsDepleted()
	{
		for(Unit mineral : minerals)
		{
			if(mineral.getResources() > 0)
			{
				return false;
			}
		}
		return true;
	}
	
	MoleUnit popWorker()
	{
		for(MoleUnit worker : baseUnits)
		{
			if(worker.type == Information.UnitType.WORKER && worker.job == Information.Job.MINERALS)
			{
				baseUnits.remove(worker);
				return worker;
			}
		}
		
		return null;
	}

	boolean isSaturated()
	{
		List<MoleUnit> workers = this.getAllUnitsByType(Information.UnitType.WORKER);
		int minerCount = 0;
		
		for(MoleUnit worker : workers)
		{
			if(worker.job == Information.Job.MINERALS)
			{
				minerCount++;
			}
				
		}
		
		return minerCount >= minerals.size()*2.5;
	}
	
	TilePosition getLocation()
	{
		return commandCenter.getTilePosition();
	}
	
	void runWorkers()
	{
		
		for(MoleUnit worker : baseUnits)
		{
			if(worker.type == Information.UnitType.WORKER)
			{
				if(worker.job == Information.Job.UNDEFINED)
				{
					worker.job = Information.Job.MINERALS;
				}
				
				if(worker.job == Information.Job.MINERALS)
				{
					if(worker.myUnit.isIdle() || worker.myUnit.isGatheringGas())
					{
						mineClosestMineral(worker.myUnit);
					}
				}
				else if(worker.job == Information.Job.GAS)
				{
					if(worker.myUnit.isIdle() || worker.myUnit.isGatheringMinerals())
					{
						if(worker.myTarget.getUnit().getType() != UnitType.Terran_Refinery)
						{
							worker.job = Information.Job.MINERALS;
						}
						else
						{
							worker.myUnit.gather(worker.myTarget.getUnit());
						}
					}
				}
				else if(worker.job == Information.Job.CONSTRUCTION)
				{
					//System.out.println("Construction worker found");
				}
			}
		}
	}
	
	void cleanDeadBuildings()
	{
		for(Unit building : buildingMemory)
		{
			if(!building.exists())
			{
				buildingMemory.remove(building);
				if(building.getType() == UnitType.Terran_Barracks)
				{
					this.productionLevel = this.productionLevel.getLower();
				}
			}
		}
	}
	
	void cleanDeadUnits()
	{
		for(MoleUnit unit : baseUnits)
		{
			if(!unit.myUnit.exists())
			{
				//System.out.println("Cleaning dead unit");
				baseUnits.remove(unit);
			}
		}
	}
	
	public void checkForAttack(Game game)
	{
		List<Unit> nearby = commandCenter.getUnitsInRadius(1000);
		List<Unit> enemies = game.enemy().getUnits();
		nearby.retainAll(enemies);
		if(!nearby.isEmpty())
		{
			//System.out.println("Enemies found!");
			state = Information.BaseState.DISTRESSED;
			enemiesInBase = nearby;
		}
		else if(state == Information.BaseState.DISTRESSED)
		{
			state = Information.BaseState.SAFE;
		}
	}
	
	void manageMarines(Game game)
	{
		if(state == Information.BaseState.SAFE)
		{
			// TODO: regroup to smart spot based on current base locations and last seen enemy
			TilePosition forwardPosition = MapTools.getNextExpansion(game.self(), game);
			offensivePosition = forwardPosition.toPosition();
			assaultPosition(game);
		}
		else if(state == Information.BaseState.DISTRESSED)
		{
			//System.out.println("Defending!");
			//defendBase(game);
			offensivePosition = getClosestBaseThreat(this.commandCenter).getPosition();
			assaultPosition(game);
		}
		else if(state == Information.BaseState.ATTACKING)
		{
			assaultPosition(game);
		}
	}
	
	Unit getClosestBaseThreat(MoleUnit unit)
	{
		Unit closest = null;
		
		for(Unit enemy : enemiesInBase)
		{
			if(closest == null)
			{
				closest = enemy;
			}
			else if(unit.myUnit.getDistance(enemy) < unit.myUnit.getDistance(closest))
			{
				closest = enemy;
			}
		}
		
		return closest;
	}
	
	Unit getClosestBaseThreat(Unit unit)
	{
		Unit closest = null;
		
		for(Unit enemy : enemiesInBase)
		{
			if(closest == null)
			{
				closest = enemy;
			}
			else if(unit.getDistance(enemy) < unit.getDistance(closest))
			{
				closest = enemy;
			}
		}
		
		return closest;
	}
	
	void defendBase(Game game)
	{
		for(MoleUnit marine : baseUnits)
		{
			if(marine.type == Information.UnitType.MARINE)
			{
				Unit enemy = getClosestBaseThreat(marine);
				if(enemy != null)
				{
					marine.myTarget = new PositionOrUnit(enemy);
					marine.smartKiteTarget(enemy, game);
					marine.job = Information.Job.DEFEND;
				}
			}
		}
	}
	
	void assaultPosition(Game game)
	{
		ArrayList<MoleUnit> marines = new ArrayList<MoleUnit>();
		for(MoleUnit marine : baseUnits)
		{
			if(marine.type == Information.UnitType.MARINE)
			{
				marines.add(marine);
				List<Unit> enemies = marine.getEnemiesInRadius(300);
				if(enemies.isEmpty())
				{
					marine.myTarget = new PositionOrUnit(offensivePosition);
					marine.job = Information.Job.ATTACK;
					marine.smartAttackMove(offensivePosition, game);
				}
				else
				{
					Unit enemy = marine.getClosestEnemyInRadius(300);
					marine.myTarget = new PositionOrUnit(enemy);
					marine.job = Information.Job.ATTACK;
					if(!marine.myUnit.isStimmed() && marine.myUnit.canUseTech(TechType.Stim_Packs))
					{
						marine.myUnit.useTech(TechType.Stim_Packs);
					}
					marine.smartKiteTarget(enemy, game);
				}
			}
		}
		
		/*Position marineCenter = MoleUnit.getCenterOfUnits(marines);
		for(MoleUnit medic :baseUnits)
		{
			if(medic.type == Information.UnitType.MEDIC)
			{
				if(medic.myUnit.isIdle() || medic.myUnit.getDistance(marineCenter) > 250)
				{
					medic.smartAttackMove(marineCenter, game);
				}
			}
		}*/
	}
	
	void produce(Player self)
	{
		if((!this.isSaturated() || !this.gasesSaturated()) && self.minerals() >= 50 && self.supplyTotal() - self.supplyUsed() >= 2 && this.commandCenter.isIdle())
		{
			commandCenter.train(UnitType.Terran_SCV);
		}
		
		for(Unit building : buildingMemory)
		{
			if(building.getType() == UnitType.Terran_Barracks)
			{
				//System.out.println("Found barracks");
				if(true)
				{
					//System.out.println("Marine count low");
					if(building.isIdle() && building.canTrain(UnitType.Terran_Marine))
					{
						//System.out.print("Training marine");
						building.train(UnitType.Terran_Marine);
					}
				}
				else if(this.getAllUnitsByType(Information.UnitType.MEDIC).size() < this.getAllUnitsByType(Information.UnitType.MARINE).size()*.2 && !this.isProducing(UnitType.Terran_Medic) && building.canTrain(UnitType.Terran_Medic))
				{
					if(building.isIdle() && building.canTrain(UnitType.Terran_Medic))
					{
						building.train(UnitType.Terran_Medic);
					}
				}
				else
				{
					if(building.isIdle() && building.canTrain(UnitType.Terran_Marine))
					{
						building.train(UnitType.Terran_Marine);
					}
				}
			}
		}
	}
	
	boolean isProducing(UnitType type)
	{
		for(Unit building : buildingMemory)
		{
			if(building.isTraining() && building.getTrainingQueue().contains(type))
			{
				return true;
			}
		}
		return false;
	}
	
	boolean isConstructing(UnitType type)
	{
		for(MoleUnit worker : baseUnits)
		{
			if(worker.type == Information.UnitType.WORKER && worker.job == Information.Job.CONSTRUCTION)
			{
				if(worker.myUnit.getBuildType() == type)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	void mineClosestMineral(Unit worker)
	{
		//if it's a worker and it's idle, send it to the closest mineral patch
        if (worker.getType().isWorker() && worker.isIdle()) {
            Unit closestMineral = null;

            //find the closest mineral
            for (Unit neutralUnit : commandCenter.getUnitsInRadius(500)) {
                if (neutralUnit.getType().isMineralField()) {
                    if (closestMineral == null || worker.getDistance(neutralUnit) < worker.getDistance(closestMineral)) {
                        closestMineral = neutralUnit;
                    }
                }
            }

            //if a mineral patch was found, send the worker to gather it
            if (closestMineral != null) {
                worker.gather(closestMineral, false);
            }
        }
	}
}
