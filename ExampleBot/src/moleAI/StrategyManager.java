package moleAI;
import bwapi.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class StrategyManager {
	
	public BuildingManager buildingManager;
	public BuildOrderManager buildOrderManager;
	public MoleUnit expansionBuilder;
	public HashSet<Unit> buildings;
	public ArrayList<Unit> depots;
	public SquadManager squadManager;
	public HashSet<MoleUnit> allUnits;
	//public ArrayList<Base> bases;
	public ScoutManager scouter;
	public Map<String, Object> blackboard;
	public TilePosition nextExpansion;
	
	public StrategyManager()
	{
		expansionBuilder = null;
		buildings = new HashSet<Unit>();
		depots = new ArrayList<Unit>();
		allUnits = new HashSet<MoleUnit>();
		//bases = new ArrayList<Base>();
		buildingManager = new BuildingManager();
		scouter = new ScoutManager();
		blackboard = new HashMap<String, Object>();
		blackboard.put("squads", new ArrayList<Squad>());
		buildOrderManager = new BuildOrderManager();
		squadManager = new SquadManager();
		nextExpansion = null;
	}
	
	public void addBuilding(Unit building)
	{
		System.out.println("Adding " + building.getType());
		if(!buildings.contains(building) && building.getType() != UnitType.Terran_Command_Center)
		{
			buildings.add(building);
		}
		else if(!depots.contains(building) && building.getType() == UnitType.Terran_Command_Center)
		{
			//to access a depot's working SCV's, use blackbloard.get("depot"+<depotIndex>+"workers")
			depots.add(building);
			blackboard.put("depot"+(depots.size()-1)+"workers", new ArrayList<MoleUnit>());
			//blackboard.put("depot"+(depots.size()-1)+"refineries", new ArrayList<Unit>());
		}else if(building.getType() == UnitType.Terran_Refinery)
		{
			System.out.println("Adding refinery");
			for (int i = 0; i < depots.size(); i++)
			{
				if(!blackboard.containsKey("refinery"+i+"workers"))
				{
					
					blackboard.put("refinery"+i+"workers", new ArrayList<MoleUnit>());
					break;
				}
				/*if(building.getDistance(depots.get(i)) < 600)
				{
					//ArrayList<Unit> refineries = (ArrayList<Unit>)blackboard.get("refinery"+i+"workers");
					//refineries.add(building);
					//blackboard.put("depot"+i+"refineries", refineries);
					System.out.println("Adding refinery");
					blackboard.put("refinery"+i+"workers", new ArrayList<MoleUnit>());
					break;
				}*/
			}
		}
	}
	
	/*public void addBase(Base b)
	{
		if(!bases.contains(b))
		{
			bases.add(b);
		}
	}*/
	
	public boolean moleContains(Unit toFind)
	{
		for(MoleUnit unit : allUnits)
		{
			if(unit.myUnit.getID() == toFind.getID())
			{
				return true;
			}
		}
		return false;
	}
	
	public void addUnit(MoleUnit newUnit)
	{
		if(!this.moleContains(newUnit.myUnit))
		{
			allUnits.add(newUnit);
			//System.out.println("Adding new unit to all units. unit type: " + newUnit.type);
			if(newUnit.type == Information.UnitType.WORKER)
			{
				for(int i = 0; i < depots.size(); i++)
				{
					if(newUnit.myUnit.getDistance(depots.get(i)) < 5)
					{
						ArrayList<MoleUnit> depotWorkers = (ArrayList<MoleUnit>)blackboard.get("depot"+i+"workers");
						depotWorkers.add(newUnit);
						blackboard.put("depot"+i+"workers", depotWorkers);
						System.out.println("Adding worker to depot: " + i);
						System.out.println("Num workers: " + depotWorkers.size());
						break;
					}
				}
			}
			else if(newUnit.type == Information.UnitType.MARINE)
			{
				squadManager.allocateUnit(newUnit, nextExpansion.toPosition(), depots.get(0).getPosition());
			}
		}
		
		//System.out.println("New unit's job: " + newUnit.job.toString());

		
		// Distribute unit into appropriate base
		/*for(Base b : bases)
		{
			if(newUnit.myUnit.getDistance(b.commandCenter) < 5)
			{
				// If it's a worker, designate either mineral or gas.
				if(newUnit.type == Information.UnitType.WORKER)
				{
					if(!b.isSaturated())
					{
						newUnit.job = Information.Job.MINERALS;
						b.addUnit(newUnit);
						//System.out.println("Worked successfully added to base");
						return;
					}
					else if(!b.gasesSaturated())
					{
						newUnit.job = Information.Job.GAS;
						for(Unit refinery : b.gases)
						{
							int workerCount = b.gasWorkerCount.get(refinery);
							if(refinery.getType() == UnitType.Terran_Refinery && workerCount < 3)
							{
								newUnit.myTarget = new PositionOrUnit(refinery);
								b.gasWorkerCount.put(refinery, workerCount++);
								b.addUnit(newUnit);
								return;
							}
						}
					}
				}
			}
			for(Unit building : b.buildingMemory)
			{
				if(newUnit.myUnit.getDistance(building) < 3)
				{
					// Otherwise, it's an army unit and we just add it.
					b.addUnit(newUnit);
					return;
				}
			}
		}*/
	}
	
	public void mineClosestMineral(MoleUnit worker, Unit depot, Game game)
	{
		if(worker.myUnit.getDistance(depot) > 200)
		{
			worker.smartMove(depot.getPosition(), game);
		}
		
		if (worker.myUnit.isIdle() || worker.myUnit.isGatheringGas()) {
            Unit closestMineral = null;

            //find the closest mineral
            for (Unit neutralUnit : game.getNeutralUnits()) {
                if (neutralUnit.getType().isMineralField()) {
                    if (closestMineral == null || worker.myUnit.getDistance(neutralUnit) < worker.myUnit.getDistance(closestMineral)) {
                        closestMineral = neutralUnit;
                        worker.myTarget = new PositionOrUnit(closestMineral);
                    }
                }
            }

            //if a mineral patch was found, send the worker to gather it
            if (closestMineral != null) {
                worker.smartRightClick(closestMineral, game);
            }
        }
	}
	
	public void gatherClosestGas(MoleUnit worker, Unit depot, Game game)
	{
		if(worker.myUnit.getDistance(depot) > 200)
		{
			worker.smartMove(depot.getPosition(), game);
		}
		
		if (worker.myUnit.isIdle() || worker.myUnit.isGatheringMinerals()) {
            Unit closestGas = null;

            //find the closest refinery
            for (Unit building : buildings) {
                if (building.getType() == UnitType.Terran_Refinery) {
                    if (closestGas == null || worker.myUnit.getDistance(building) < worker.myUnit.getDistance(closestGas)) {
                        closestGas = building;
                        worker.myTarget = new PositionOrUnit(closestGas);
                    }
                }
            }

            //if a refinery was found, send the worker to gather it
            if (closestGas != null) {
                worker.smartRightClick(closestGas, game);
            }
        }
	}
	
	public void collectResources(Game game)
	{
		for(int i = 0; i < depots.size(); i++)
		{
			ArrayList<MoleUnit> depotWorkers = (ArrayList<MoleUnit>)blackboard.get("depot"+i+"workers");
			for(MoleUnit worker : depotWorkers)
			{
				if(worker.job == Information.Job.UNDEFINED)
				{
					boolean setGas = false;
					for(Unit refinery : buildings)
					{
						if(refinery.getType() == UnitType.Terran_Refinery && refinery.isCompleted() && depots.get(i).getDistance(refinery) < 400)
						{
							ArrayList<MoleUnit> gasWorkers = (ArrayList<MoleUnit>)blackboard.get("refinery"+i+"workers");
							if(!gasWorkers.contains(worker) && gasWorkers.size() < 3)
							{
								worker.job = Information.Job.GAS;
								gasWorkers.add(worker);
								worker.myTarget = new PositionOrUnit(refinery);
								worker.smartRightClick(refinery, game);
								blackboard.put("refinery"+i+"workers", gasWorkers);
								setGas = true;
								break;
							}
						}
					}
					
					if(!setGas)
					{
						worker.job = Information.Job.MINERALS;
					}
					
				}
				if(worker.job == Information.Job.MINERALS)
				{
					mineClosestMineral(worker,depots.get(i), game);
				}
				else if(worker.job == Information.Job.GAS)
				{
					gatherClosestGas(worker, depots.get(i), game);
				}
			}
			
			/*for(Unit refinery : buildings)
			{
				if(refinery.getType() == UnitType.Terran_Refinery && refinery.isCompleted() && depots.get(i).getDistance(refinery) < 300)
				{
					ArrayList<MoleUnit> gasWorkers = (ArrayList<MoleUnit>)blackboard.get("refinery"+i+"workers");
					for(MoleUnit worker : gasWorkers)
					{
						if(worker.myUnit.isIdle() || worker.myUnit.isGatheringMinerals())
						{
							worker.smartRightClick(refinery, game);
						}
					}
				}
			}*/
		}
	}
	
	public int workersNeeded(Unit depot)
	{
		//TODO: figure out how many workers are needed to saturate remaining mineral patches + gas
		return (9*2)+3;
	}
	
	public void saturateDepots(Game game)
	{
		for(int i = 0; i < depots.size(); i++)
		{
			ArrayList<MoleUnit> depotWorkers = (ArrayList<MoleUnit>)blackboard.get("depot"+i+"workers");
			if(depotWorkers.size() < 22 && canBuild(game.self(), UnitType.Terran_SCV))
			{
				if(!depots.get(i).isTraining())
				{
					depots.get(i).train(UnitType.Terran_SCV);
				}
			}
		}
	}
	
	public void update(Game game, Player self)
	{
		cleanDeadUnits();
		//System.out.println("cleaned dead units");
		ArrayList<MoleUnit> mainWorkers = (ArrayList<MoleUnit>)blackboard.get("depot0workers");
		if(scouter.noBuildingsKnown() && scouter.scoutWorker == null && mainWorkers.size() >= 9)
		{
			scouter.changeScout(mainWorkers.get(0));
			mainWorkers.remove(0);
			blackboard.put("depot0workers", mainWorkers);
		}
		scouter.update(game);
		if(self.supplyTotal() < 400 && self.supplyUsed() >= self.supplyTotal() - squadManager.squads.size()*2 && self.minerals() >= 100 && !buildingManager.isQueued(UnitType.Terran_Supply_Depot))
		{
			buildingManager.addBuildingTask(UnitType.Terran_Supply_Depot);
		}
		if(buildOrderManager.canBuildNext(self, mainWorkers.size()))
		{
			UnitType toBuild = buildOrderManager.popItem();
			buildingManager.addBuildingTask(toBuild);
		}
		for(Unit building : buildings)
		{
			if(building.canTrain(UnitType.Terran_Marine) && !building.isTraining())
			{
				building.train(UnitType.Terran_Marine);
			}
			else if(building.canResearch(TechType.Stim_Packs))
			{
				building.research(TechType.Stim_Packs);
			}
		}
		nextExpansion = MapTools.getNextExpansion(self, game);
		
		buildingManager.update(game, mainWorkers, self);
		//System.out.println("Updated building manager");
		collectResources(game);
		//System.out.println("collected resources");
		saturateDepots(game);
		//System.out.println("saturated depots");
		Position enemyBase = scouter.getClosestEnemyBuilding(depots.get(0).getPosition());
		if(squadManager.squads.size() > 4 && squadManager.squads.get(2).isFull())
		{
			if(enemyBase == null)
			{
				System.out.println("Don't know where enemy is");
			}
			squadManager.update(game, enemyBase);
			
		}
		else
		{
			squadManager.update(game, nextExpansion.toPosition());
		}
		
		//System.out.println("updated squad manager");
		
		//checkForNewExpansions();
		//cleanDeadBases();
		
		//runBases(game, self);
		//setBuildGoals(self, game);
		//researchTech(self);
	}
	
	public void cleanDeadUnits()
	{
		for(MoleUnit unit : allUnits)
		{
			if(!unit.myUnit.exists())
			{
				allUnits.remove(unit);
				squadManager.removeUnit(unit);
			}
		}
	}
	
	/*public void checkForNewExpansions()
	{
		for(Unit building : this.buildings)
		{
			if(building.getType() == UnitType.Terran_Command_Center)
			{
				boolean fresh = true;
				for(Base b : bases)
				{
					if(b.commandCenter.getID() == building.getID())
					{
						fresh = false;
					}
				}
				if(fresh)
				{
					Base newBase = new Base();
					newBase.setCC(building);
					newBase.setBaseInfo();
					bases.add(newBase);
				}
			}
		}
	}*/
	
	/*public void runBases(Game game, Player self)
	{
		cleanDeadBases();
		boolean shouldAttack = shouldAttackNow();
		for(Base b : bases)
		{
			b.cleanDeadBuildings();
			b.cleanDeadUnits();
			b.runWorkers(game);
			b.produce(self);
			b.checkForAttack(game);
			if(shouldAttack && b.state == Information.BaseState.SAFE)
			{
				
				if(!scouter.noBuildingsKnown())
				{
					b.state = Information.BaseState.ATTACKING;
					b.offensivePosition = scouter.getClosestEnemyBuilding(b.commandCenter.getPosition());
				}
				else if(scouter.lastSeenEnemy != null)
				{
					b.state = Information.BaseState.ATTACKING;
					b.offensivePosition = scouter.lastSeenEnemy.getPosition();
					if(game.isVisible(scouter.lastSeenEnemy.getTilePosition()) && !scouter.lastSeenEnemy.exists())
					{
						scouter.lastSeenEnemy = null;
						if(scouter.scoutWorker != null)
						{
							scouter.scoutWorker.job = Information.Job.MINERALS;
							b.addUnit(scouter.scoutWorker);
							scouter.scoutWorker = null;
						}
					}
				}
				else
				{
					b.offensivePosition = b.commandCenter.getPosition();
				} 
			}
			else if(!shouldAttack && b.state == Information.BaseState.ATTACKING && totalUnitCount(Information.UnitType.MARINE) + totalUnitCount(Information.UnitType.MEDIC) + totalUnitCount(Information.UnitType.FIREBAT) <= 15 * bases.size())
			{
				b.state = Information.BaseState.SAFE;
				b.offensivePosition = null;
			}
			b.manageMarines(game);
		}
	}*/
	
	boolean canBuild(Player self, UnitType buildingType)
	{
		if(self.minerals() >= buildingType.mineralPrice() && self.gas() >= buildingType.gasPrice())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*public void setBuildGoals(Player self, Game game)
	{
		
		for(Base b : bases)
		{
			if(b.getAllUnitsByType(Information.UnitType.WORKER).size() >= 10 && b.productionLevel == Information.ProductionLevel.NONE)
			{
				if(canBuild(self, UnitType.Terran_Barracks))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Barracks, b);
					b.productionLevel = Information.ProductionLevel.LOW;
				}
				
			}
			else if(b.getAllUnitsByType(Information.UnitType.WORKER).size() >= 14 && b.productionLevel == Information.ProductionLevel.LOW)
			{
				if(canBuild(self, UnitType.Terran_Barracks))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Barracks, b);
					b.productionLevel = Information.ProductionLevel.MEDIUM;
				}
			}
			else if(b.getAllUnitsByType(Information.UnitType.WORKER).size() >= 18 && b.productionLevel == Information.ProductionLevel.MEDIUM)
			{
				
				if(totalBuildingCount(UnitType.Terran_Academy) == 0 && !buildingManager.isQueued(UnitType.Terran_Academy) && canBuild(self, UnitType.Terran_Academy))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Academy, b);
				}
				
				if(canBuild(self, UnitType.Terran_Barracks))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Barracks, b);
					b.productionLevel = Information.ProductionLevel.HIGH;
				}
				
			}else if(bases.size() > 1 && b.productionLevel == Information.ProductionLevel.HIGH)
			{
				if(canBuild(self, UnitType.Terran_Barracks))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Barracks, b);
					b.productionLevel = Information.ProductionLevel.MASS;
				}
			}
			else if(b.isSaturated() && b.getBuildingsByType(UnitType.Terran_Refinery).size() < b.gases.size())
			{
				if(canBuild(self, UnitType.Terran_Refinery) && !buildingManager.isQueued(UnitType.Terran_Refinery, b))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Refinery, b.gases.get(0).getTilePosition(), b);
				}
			}
			else if(b.productionLevel == Information.ProductionLevel.MASS && totalBuildingCount(UnitType.Terran_Engineering_Bay) < 2)
			{
				if(canBuild(self, UnitType.Terran_Engineering_Bay) && !buildingManager.isQueued(UnitType.Terran_Engineering_Bay, b))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Engineering_Bay, b);
				}
			}
			if( game.self().supplyTotal() < 400 && game.self().supplyTotal() - game.self().supplyUsed() <= 4 + 2*totalBuildingCount(UnitType.Terran_Barracks) && canBuild(self, UnitType.Terran_Supply_Depot))
			{
				//System.out.println("Total barracks: " + totalBuildingCount(UnitType.Terran_Barracks));
				if(!buildingManager.isQueued(UnitType.Terran_Supply_Depot, b))
				{
					buildingManager.addBuildingTask(UnitType.Terran_Supply_Depot, b);
				}
				
			}
		}
		
		if(shouldExpandNow(self, game) && !buildingManager.isQueued(UnitType.Terran_Command_Center))
		{
			TilePosition expansionSpot = MapTools.getNextExpansion(self, game);
			buildingManager.addBuildingTask(UnitType.Terran_Command_Center, expansionSpot, bases.get(bases.size()-1));
						
		}
	}*/
	
	/*public void researchTech(Player self)
	{
		if(self.getUpgradeLevel(UpgradeType.Caduceus_Reactor) == 0)
		{
			if(totalBuildingCount(UnitType.Terran_Barracks) >= 3 && totalBuildingCount(UnitType.Terran_Academy) > 0)
			{
				//System.out.println("Reached tech threshold");
				for(Unit building : buildings)
				{
					//System.out.println("Looking for academy");
					if(building.canResearch(TechType.Stim_Packs))
					{
						building.research(TechType.Stim_Packs);
					}
					else if(building.canUpgrade(UpgradeType.U_238_Shells))
					{
						building.upgrade(UpgradeType.U_238_Shells);
					}
					else if(building.canUpgrade(UpgradeType.Caduceus_Reactor))
					{
						building.upgrade(UpgradeType.Caduceus_Reactor);
					}
				}
			}
		}
		else if(self.getUpgradeLevel(UpgradeType.Terran_Infantry_Armor) == 0 || self.getUpgradeLevel(UpgradeType.Terran_Infantry_Weapons) == 0)
		{
			for(Unit building : buildings)
			{
				if(building.canUpgrade(UpgradeType.Terran_Infantry_Weapons))
				{
					building.upgrade(UpgradeType.Terran_Infantry_Weapons);
				}
				else if(building.canUpgrade(UpgradeType.Terran_Infantry_Armor))
				{
					building.upgrade(UpgradeType.Terran_Infantry_Armor);
				}
			}
		}
	}*/
	
	/*public int totalUnitCount(Information.UnitType type)
	{
		int count = 0;
		for(Base b : bases)
		{
			count += b.getAllUnitsByType(type).size();
		}
		
		return count;
	}*/
	
	/*public int totalBuildingCount(UnitType type)
	{
		int count = 0;
		for(Base b : bases)
		{
			count += b.getBuildingsByType(type).size();
		}
		return count;
	}*/

	
	/*private void cleanDeadBases()
	{
		ArrayList<Base> toRemove = new ArrayList<Base>();
		for(Base b : bases)
		{
			if(!b.commandCenter.exists())
			{
				toRemove.add(b);
			}
		}
		bases.remove(toRemove);
	}*/
	
	/*public boolean shouldAttackNow()
	{
		if(scouter.noBuildingsKnown())
		{
			return false;
		}else if(totalUnitCount(Information.UnitType.MARINE) + totalUnitCount(Information.UnitType.MEDIC) + totalUnitCount(Information.UnitType.FIREBAT) >= 25 * bases.size())
		{
			return true;
		}
		else
		{
			return false;
		}
	}*/
	
	public boolean shouldExpandNow(Player player, Game game)
	{
		if(MapTools.getNextExpansion(player, game) == TilePosition.None)
		{
			System.out.println("No valid expansion spot");
			return false;
		}
		
		int frame  = game.getFrameCount();
	    int minute = frame / (24*60);
	    int numDepots = game.self().allUnitCount(UnitType.Terran_Command_Center);
	    
	    //int expansionTimes[] = {5, 10, 20, 30, 40, 50};
	    int expansionTimes[] = {5};
	    for(int i = 0; i < expansionTimes.length; i++)
	    {
	    	if(numDepots < (i+2) && minute > expansionTimes[i])
	    	{
	    		//System.out.println("Should expand!");
	    		return true;
	    	}
	    }
	    
	    return false;
	    
	}
	
	public void drawUnitInfo(Player self, Game game)
	{
		//System.out.println("All units size: " + allUnits.size());
		for (MoleUnit myUnit : allUnits) {

	          if(myUnit.myUnit.getType().isBuilding() || !myUnit.myUnit.isCompleted())	continue;
	       
	          game.drawTextMap(myUnit.myUnit.getPosition().getX(), myUnit.myUnit.getPosition().getY(), myUnit.job.toString());
	          if(myUnit.type == Information.UnitType.WORKER)	continue;
	          game.drawLineMap(myUnit.myUnit.getPosition().getX(), myUnit.myUnit.getPosition().getY(), myUnit.myUnit.getOrderTargetPosition().getX(), 
	          		 myUnit.myUnit.getOrderTargetPosition().getY(), bwapi.Color.Green);
	        }
	}

}
