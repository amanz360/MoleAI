import bwapi.*;
import bwapi.Error;
import bwta.BWTA;
import bwta.BaseLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestBot1 extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;
    private ArrayList<Base> bases;
    private Error lastErr;
    private ScoutManager scouter;

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
        for(Base b: bases)
        {
        	if(b.commandCenter.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_SCV)
        	{
        		if(!b.isSaturated())
        		{
        			b.mineralWorkers.add(unit);
        		}
        		else if(!b.gasesSaturated())
        		{
        			Set<Unit> refineries = b.gasWorkers.keySet();
        			for(Unit refinery : refineries)
        			{
        				if(b.gasWorkers.get(refinery).size() < 3)
        				{
        					b.gasWorkers.get(refinery).add(unit);
        				}
        			}
        		}
        		else if(b.isSaturated() && b.gasesSaturated())
        		{
        			b.mineralWorkers.add(unit);
        		}
        	}
        	
        	for(Unit builderWorker : b.builderWorkers)
        	{
        		if(builderWorker.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_Barracks && (b.productionLevel == 1 || b.productionLevel == 2))
        		{
        			b.marineProduction.add(unit);
        			break;
        		}
           		else if(builderWorker.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_Academy)
           		{
           			b.academy = true;
           			break;
           		}
           		else if(builderWorker.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_Barracks && (b.productionLevel == 4 || b.productionLevel == 5))
        		{
           			b.medicProduction.add(unit);
           			break;
        		}
        	}
        	
        	for(Unit structure : b.marineProduction)
        	{
        		if(structure.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_Marine)
        		{
        			b.marineForce.add(unit);
        			System.out.println("Marine force size: " + b.marineForce.size());
        		}
        	}
        	
        	for(Unit structure : b.medicProduction)
        	{
        		if(structure.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_Medic)
        		{
        			b.medicForce.add(unit);
        			System.out.println("Medic force size: " + b.medicForce.size());
        		}
        		else if(structure.getDistance(unit) < 3 && unit.getType() == UnitType.Terran_Marine)
        		{
        			b.marineForce.add(unit);
        			System.out.println("Marine force size: " + b.marineForce.size());
        		}
        	}
        }
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        game.setLocalSpeed(10); 
        bases = new ArrayList<Base>();
        scouter = new ScoutManager();
            

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        int i = 0;
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
        	for(Position position : baseLocation.getRegion().getPolygon().getPoints()){
        		System.out.print(position + ", ");
        	}
        	System.out.println();
        }
        
        
        Base main = new Base();
        ArrayList<Unit> workers = new ArrayList<Unit>();
        
        for(Unit myUnit : self.getUnits())
        {
        	System.out.println("Iterating through units, current unit is " + myUnit.getType().toString());
        	if(myUnit.getType() == UnitType.Terran_Command_Center)
        	{
        		main.setCC(myUnit);
        	}
        	
        	if(myUnit.getType() == UnitType.Terran_SCV)
        	{
        		workers.add(myUnit);
        	}
        }
        
        List<Unit> units = main.commandCenter.getUnitsInRadius(500);
        System.out.println(units.size());
        ArrayList<Unit> minerals = new ArrayList<Unit>();
        ArrayList<Unit> gases = new ArrayList<Unit>();
        for(Unit resource : units)
        {
        	if(resource.getType() == UnitType.Resource_Mineral_Field)
        	{
        		minerals.add(resource);
        	}
        	if(resource.getType() == UnitType.Resource_Vespene_Geyser)
        	{
        		gases.add(resource);
        		ArrayList<Unit> collectors = new ArrayList<Unit>();
        		main.gasWorkers.put(resource, collectors);
        	}
        	
        }
        
     
        
        main.minerals = minerals;
        main.gases = gases;
        System.out.println("Number of mineral patches: " + main.minerals.size());
        System.out.println("Number of gases: " + main.gases.size());
        main.mineralWorkers = workers;
        main.addBuilder(main.mineralWorkers.get(0));
        main.setWorkerCount(4);
        bases.add(main);
        System.out.println("I have " + bases.size() + " bases");
    }

    @Override
    public void onFrame() {
        //game.setTextSize(10);
    	
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        lastErr = game.getLastError();

        StringBuilder units = new StringBuilder("My units:\n");
        for(Base b : bases)
        {
        	b.cleanBase(game);
        	b.checkProductionLevel();
        	b.produce(game, self, lastErr);
        	b.manageDefense(game);
        	if(scouter.scoutWorker == null && b.productionLevel >= 1 && scouter.enemyBases.size() == 0)
        	{
        		scouter.scoutWorker = b.chooseScout();
        		System.out.println("Assigned scout!");
        	}
        	if(!b.underAttack && b.marineForce.size() >= 30 && scouter.enemyBases.size() > 0)
        	{
        		System.out.println("Attacking!");
        		b.attackLocation(scouter.enemyBases.get(0), game);
        	}
        	else if(b.attacking && b.marineForce.size() < 5)
        	{
        		b.endAttack();
        	}
        }
        scouter.update(game);

        //draw my units on screen
        game.drawTextScreen(10, 25, units.toString());
    }

    public static void main(String[] args) {
        new TestBot1().run();
    }
}