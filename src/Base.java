
import bwapi.*;
import bwapi.Error;
import bwta.BWTA;
import bwta.BaseLocation;
import bwapi.PositionOrUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;


public class Base {
	
	public ArrayList<Unit> minerals;
	public ArrayList<Unit> gases;
	public ArrayList<Unit> mineralWorkers;
	public Map<Unit, ArrayList<Unit>> gasWorkers;
	public ArrayList<Unit> supplyDepots;
	public ArrayList<Unit> marineProduction;
	public ArrayList<Unit> medicProduction;
	public ArrayList<Unit> marineForce;
	public ArrayList<Unit> medicForce;
	public ArrayList<Unit> enemiesInBase;
	public ArrayList<Unit> builderWorkers;
	public Unit commandCenter;
	public TilePosition attackLocation;
	public int lastChecked;
	public boolean academy;
	public boolean underAttack;
	public boolean attacking;
	public int productionLevel;
	private BuildingManager buildingManager;
	private int workerCount;
	private boolean addedBuilder;
	private Unit lastSeenEnemy;
	
	
	
	Base()
	{
		minerals = new ArrayList<Unit>();
		gases = new ArrayList<Unit>();
		mineralWorkers = new ArrayList<Unit>();
		gasWorkers = new HashMap<Unit, ArrayList<Unit>>();
		supplyDepots = new ArrayList<Unit>();
		marineProduction = new ArrayList<Unit>();
		medicProduction = new ArrayList<Unit>();
		marineForce = new ArrayList<Unit>();
		medicForce = new ArrayList<Unit>();
		enemiesInBase = new ArrayList<Unit>();
		builderWorkers = new ArrayList<Unit>();
		buildingManager = new BuildingManager();
		workerCount = 0;
		lastChecked = 0;
		productionLevel = 0;
		academy = false;
		underAttack = false;
		attacking = false;
		addedBuilder = false;
		lastSeenEnemy = null;
		attackLocation = null;
	}
	
	int getMaxMiners()
	{
		return minerals.size() * 2;
	}
	
	void setWorkerCount(int count)
	{
		workerCount = count;
	}
	
	boolean gasesSaturated()
	{
		for(ArrayList<Unit> collectors : gasWorkers.values())
		{
			if(collectors.size() < 3)
			{
				return false;
			}
		}
		return true;
	}
	
	void setCC(Unit cc)
	{
		commandCenter = cc;
	}
	
	void addBuilder(Unit worker)
	{
		if(!builderWorkers.contains(worker))
		{
			System.out.println("Added builder: " + worker.toString());
			builderWorkers.add(worker);
		}
	}
	
	Unit chooseScout()
	{
		Unit scout = null;
		for(Unit worker : mineralWorkers)
		{
			if(!builderWorkers.contains(worker))
			{
				scout = worker;
				workerCount--;
				break;
			}
		}
		mineralWorkers.remove(scout);
		return scout;
	}
	
	boolean isSaturated()
	{
		return workerCount >= minerals.size()*2.5;
	}
	
	TilePosition getLocation()
	{
		return commandCenter.getTilePosition();
	}
	
	void cleanBase(Game game)
	{
		for(Unit worker : mineralWorkers)
		{
			if(!worker.exists())
			{
				mineralWorkers.remove(worker);
				workerCount--;
			}
			if(worker.isIdle() || worker.isCarryingGas() || worker.isGatheringGas())
			{
				mineClosestMineral(worker, game);
			}
		}
		
		for(Unit refinery : gases)
		{
			if(refinery.getType() == UnitType.Terran_Refinery)
			{
				if(gasWorkers.get(refinery).size() > 3)
				{
					while(gasWorkers.get(refinery).size() > 3)
					{
						if(!mineralWorkers.contains(gasWorkers.get(refinery).get(0)))
						{
							mineralWorkers.add(gasWorkers.get(refinery).get(0));
						}
						gasWorkers.get(refinery).remove(0);
					}
				}
				for(Unit worker : gasWorkers.get(refinery))
				{
					if(!worker.exists())
					{
						gasWorkers.get(refinery).remove(worker);
					}
					if(worker.isIdle())
					{
						worker.gather(refinery, false);
					}
				}
			}
			
		}
		
		for(Unit supplyDepot : supplyDepots)
		{
			if(!supplyDepot.exists())
			{
				supplyDepots.remove(supplyDepot);
			}
		}
		
		if(builderWorkers.size() == 0)
		{
			builderWorkers.add(mineralWorkers.get(0));
		}
		
		for(Unit builder : builderWorkers)
		{
			if(!builder.exists())
			{
				builderWorkers.remove(builder);
			}
			if(builder.isIdle())
			{
				mineClosestMineral(builder, game);
			}
		}
		
		buildingManager.builders = builderWorkers;
	}
	
	void trainSCV(Player self)
	{
		commandCenter.train(UnitType.Terran_SCV);
		workerCount++;
		System.out.println("SCV trained! Worker Count: " + workerCount);
	}
	
	void checkProductionLevel()
	{
		if(workerCount >= 10 && marineProduction.size() < 1 && productionLevel != 1)
		{
			System.out.println("Reached production level 1!");
			if(productionLevel != 1 && !buildingManager.isBeingBuilt(UnitType.Terran_Barracks))
			{
				productionLevel = 1;
				addBuilder(mineralWorkers.get(builderWorkers.size()));
				buildingManager.addBuildingTask(UnitType.Terran_Barracks);
				
			}
		}
		else if(workerCount >= 16 && marineProduction.size() < 2 && productionLevel != 2)
		{
			System.out.println("Reached production level 2!");
			if(productionLevel != 2 && !buildingManager.isBeingBuilt(UnitType.Terran_Barracks))
			{
				productionLevel = 2;
				addBuilder(mineralWorkers.get(builderWorkers.size()));
				buildingManager.addBuildingTask(UnitType.Terran_Barracks);
			}
		}
		else if(this.gasesSaturated() && !academy && marineProduction.size() >= 2 && productionLevel != 3)
		{
			System.out.println("Reached production level 3!");
			if(productionLevel != 3 && !buildingManager.isBeingBuilt(UnitType.Terran_Academy))
			{
				productionLevel = 3;
				addBuilder(mineralWorkers.get(builderWorkers.size()));
				buildingManager.addBuildingTask(UnitType.Terran_Academy);
			}
		}
		else if(academy && medicProduction.size() == 0 && productionLevel != 4)
		{
			System.out.println("Reached production level 4!");
			
			if(productionLevel != 4 && !buildingManager.isBeingBuilt(UnitType.Terran_Barracks))
			{
				productionLevel = 4;
				addBuilder(mineralWorkers.get(builderWorkers.size()));
				buildingManager.addBuildingTask(UnitType.Terran_Barracks);
			}
		}
		else if(academy && medicProduction.size() == 1 && productionLevel != 5)
		{
			System.out.println("Reached production level 5!");
			if(productionLevel != 5 && !buildingManager.isBeingBuilt(UnitType.Terran_Barracks))
			{
				productionLevel = 5;
				addBuilder(mineralWorkers.get(builderWorkers.size()));
				buildingManager.addBuildingTask(UnitType.Terran_Barracks);
			}
		}
	}
	
	void produce(Game game, Player self, Error lastErr)
	{
		buildingManager.update(game, self);

		if(!this.isSaturated() && self.minerals() >= 50 && self.supplyTotal() - self.supplyUsed() >= 2 && this.commandCenter.isIdle())
    	{
    		this.trainSCV(self);
    	}
		
    	if(self.supplyTotal() - self.supplyUsed() <= 4 + 2*productionLevel)
    	{
    		if(!buildingManager.isBeingBuilt(UnitType.Terran_Supply_Depot))
			{
				buildingManager.addBuildingTask(UnitType.Terran_Supply_Depot);
			}
    	}
    	
    	if(this.isSaturated())
    	{
    		Set<Unit> refineries = gasWorkers.keySet();
    		for(Unit refinery : refineries)
        	{
    			if(refinery.getType() == UnitType.Resource_Vespene_Geyser)
    			{
    				if(!buildingManager.isBeingBuilt(UnitType.Terran_Refinery))
    				{
    					buildingManager.addBuildingTask(UnitType.Terran_Refinery);
    				}
    			}
        	}
    		for(Unit refinery : refineries)
    		{
    			if(refinery.isCompleted() && self.minerals() >= 50 &&  gasWorkers.get(refinery).size() < 3 && self.supplyTotal() - self.supplyUsed() >= 2 && commandCenter.isIdle())
    			{
    				this.trainSCV(self);
    			}
    		}
    	}
    	
		trainMarines();
		trainMedics();
	}
	
	void trainMarines()
	{
		for(Unit barracks : marineProduction)
		{
			if(barracks.getType() == UnitType.Terran_Barracks && barracks.isIdle())
			{
				barracks.train(UnitType.Terran_Marine);
			}
		}
	}
	
	void trainMedics()
	{
		for(Unit barracks : medicProduction)
		{
			if(barracks.getType() == UnitType.Terran_Barracks && barracks.isIdle() && medicForce.size() <= 0.33 *  marineForce.size())
			{
				barracks.train(UnitType.Terran_Medic);
			}
			else if(barracks.getType() == UnitType.Terran_Barracks && barracks.isIdle() && medicForce.size() > 0.33 *  marineForce.size())
			{
				barracks.train(UnitType.Terran_Marine);
			}
		}
	}
	
	void manageDefense(Game game)
	{
		for(Unit marine : marineForce)
		{
			if(!marine.exists())
			{
				System.out.println("Marine down!");
				marineForce.remove(marine);
				System.out.println("Remaing marines: " + marineForce.size());
			}
		}
		for(Unit medic : medicForce)
		{
			if(!medic.exists())
			{
				System.out.println("Medic down!");
				medicForce.remove(medic);
				System.out.println("Remaining medics: " + medicForce.size());
			}
		}
		
		List<Unit> units = commandCenter.getUnitsInRadius(1000);
		List<Unit> enemies = game.enemy().getUnits();
		//System.out.println(enemies);
		
		for(Unit enemy : enemies)
		{
			if(units.contains(enemy) && !enemiesInBase.contains(enemy))
			{
				System.out.println("Found new enemy!");
				enemiesInBase.add(enemy);
				lastSeenEnemy = enemy;
			}
		}

		for(Unit enemy : enemiesInBase)
		{
			if(!enemy.isVisible() || !enemy.exists())
			{
				System.out.println("Enemy gone!");
				if(enemiesInBase.size() == 1)
				{
					lastSeenEnemy = enemiesInBase.get(0);
				}
				enemiesInBase.remove(enemy);
				
			}
		}
		
		if(enemiesInBase.size() > 0)
		{
			underAttack = true;
			if(attacking)	return;
			sendForce(marineForce, game);
			
		}
		else
		{
			underAttack = false;
			if(attacking)	return;
			
			for(Unit marine : marineForce)
			{
				UnitCommand lastCommand = marine.getLastCommand();
				if(lastCommand.getUnitCommandType() != UnitCommandType.Move)
				{
					marine.move(commandCenter.getPosition());
				}
			}
		}		
	}
	
	void sendForce(ArrayList<Unit> force, Game game)
	{
		for(Unit marine : force)
		{
			if(!marine.isCompleted() && marine.getLastCommandFrame() >= game.getFrameCount() || marine.isAttackFrame())
			{
				//System.out.println("Skipping attack frame...");
				return;
			}
			
			UnitCommand lastCommand = marine.getLastCommand();
			//System.out.println("last command: " + lastCommand.getUnitCommandType().toString());
			//System.out.println("first enemy position: " + enemiesInBase.get(0).getPosition().toString());
			//System.out.println("last target position" + lastCommand.getTarget().getPosition());
			if(lastCommand.getUnitCommandType() != UnitCommandType.Attack_Move || marine.isIdle())
			{
				marine.attack(getClosestEnemy(marine).getPosition());
			}
			
			System.out.println(marine.toString() + " Marine HP: " + marine.getHitPoints() + " / " + marine.getType().maxHitPoints());
			
			if(marine.getHitPoints() < marine.getType().maxHitPoints())
			{
				healMarine(marine);
			}
		}
	}
	
	void endAttack()
	{
		attacking = false;
		for(Unit marine : marineForce)
		{
			marine.move(commandCenter.getPosition());
		}
		
		for(Unit medic : medicForce)
		{
			medic.move(commandCenter.getPosition());
		}
	}
	
	void attackLocation(TilePosition location, Game game)
	{
		attacking = true;
		
		for(Unit marine : marineForce)
		{
			if(!marine.isCompleted() && marine.getLastCommandFrame() >= game.getFrameCount() || marine.isAttackFrame())
			{
				//System.out.println("Skipping attack frame...");
				return;
			}
			
			UnitCommand lastCommand = marine.getLastCommand();
			//System.out.println("last command: " + lastCommand.getUnitCommandType().toString());
			//System.out.println("first enemy position: " + enemiesInBase.get(0).getPosition().toString());
			//System.out.println("last target position" + lastCommand.getTarget().getPosition());
			if(lastCommand.getUnitCommandType() != UnitCommandType.Attack_Move || marine.isIdle())
			{
				marine.attack(location.toPosition());
			}
			
			System.out.println(marine.toString() + " Marine HP: " + marine.getHitPoints() + " / " + marine.getType().maxHitPoints());
			
			if(marine.getHitPoints() < marine.getType().maxHitPoints())
			{
				healMarine(marine);
			}
		}
		
		for(Unit medic : medicForce)
		{
			UnitCommand lastCommand = medic.getLastCommand();
			if(medic.isIdle())
			{
				medic.follow(marineForce.get(marineForce.size()/2));
			}
		}
	}
	
	void healMarine(Unit marine)
	{
		Unit closestMedic = getClosestMedic(marine);
		UnitCommand lastCommand = closestMedic.getLastCommand();
		if(!marine.isBeingHealed() && (lastCommand.getUnitCommandType() != UnitCommandType.Move || closestMedic.isIdle()))
		{
			System.out.println("Healing marine");
			closestMedic.move(marine.getPosition());
		}
		getClosestMedic(marine).rightClick(marine);
	}
	
	void superHealMarine(Unit marine)
	{
		PositionOrUnit unit = new PositionOrUnit(marine);
		ArrayList<Unit> availableMedics = getAvailableMedics();
		for(Unit medic : availableMedics)
		{
			medic.issueCommand(UnitCommand.useTech(medic, TechType.Healing, unit));
		}
	}
	
	Unit getClosestMedic(Unit marine)
	{
		Unit closest = null;
		for(Unit medic : medicForce)
		{
			UnitCommand lastCommand = medic.getLastCommand();
			if(closest == null)
			{
				closest = medic;
			}
			else if(marine.getDistance(medic) < marine.getDistance(closest) && (lastCommand.getUnitCommandType() != UnitCommandType.Move || medic.isIdle()))
			{
				closest = medic;
			}
		}
		
		return closest;
	}
	
	ArrayList<Unit> getAvailableMedics()
	{
		
		ArrayList<Unit> availableMedics = new ArrayList<Unit>();
		
		for(Unit medic : medicForce)
		{
			UnitCommand lastCommand = medic.getLastCommand();
			if(lastCommand.getUnitCommandType() != UnitCommandType.Use_Tech || medic.isIdle())
			{
				availableMedics.add(medic);
			}
		}
		
		return availableMedics;
	}
	
	Unit getClosestEnemy(Unit marine)
	{
		Unit closest = null;
		for(Unit enemy : enemiesInBase)
		{
			if(closest == null)
			{
				closest = enemy;
			}
			else if(marine.getDistance(enemy) < marine.getDistance(closest))
			{
				closest = enemy;
			}
		}
		return closest;
	}
	
	void mineClosestMineral(Unit worker, Game game)
	{
		//if it's a worker and it's idle, send it to the closest mineral patch
        if (worker.getType().isWorker() && worker.isIdle()) {
            Unit closestMineral = null;

            //find the closest mineral
            for (Unit neutralUnit : game.neutral().getUnits()) {
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