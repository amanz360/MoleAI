package moleAI;
import bwapi.*;
import bwapi.Error;
import bwta.BWTA;
import bwta.BaseLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestBot1 extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private static Game game;

    private Player self;
    private ArrayList<Base> bases;
    private Error lastErr;
    private ScoutManager scouter;
    private StrategyManager strategy;

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
        
        if(unit.getType().isBuilding())
        {
        	strategy.addBuilding(unit);
        }
        else if(unit.getType() == UnitType.Terran_SCV)
        {
    		//System.out.println("Adding worker");
    		//System.out.println("Num Bases: " + strategy.bases.size());
    		strategy.addUnit(new MoleUnit(unit, Information.UnitType.WORKER));
      
        }
        else if(unit.getType() == UnitType.Terran_Marine)
        {
        	strategy.addUnit(new MoleUnit(unit, Information.UnitType.MARINE));
        }
        else if(unit.getType() == UnitType.Terran_Firebat)
        {
        	strategy.addUnit(new MoleUnit(unit, Information.UnitType.FIREBAT));
        }
        else if(unit.getType() == UnitType.Terran_Medic)
        {
        	strategy.addUnit(new MoleUnit(unit, Information.UnitType.MEDIC));
        }
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        game.setLocalSpeed(0); 
        strategy = new StrategyManager();
        strategy.buildOrderManager.addItem(UnitType.Terran_Barracks, 10);
        strategy.buildOrderManager.addItem(UnitType.Terran_Barracks, 12);
        strategy.buildOrderManager.addItem(UnitType.Terran_Barracks, 16);
            

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        int i = 0;
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	//System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
        	for(Position position : baseLocation.getRegion().getPolygon().getPoints()){
        		//System.out.print(position + ", ");
        	}
        	//System.out.println();
        }
        
        
        //Base main = new Base();
        
        for(Unit myUnit : self.getUnits())
        {
        	//System.out.println("Iterating through units, current unit is " + myUnit.getType().toString());
        	if(myUnit.getType() == UnitType.Terran_Command_Center)
        	{
        		strategy.addBuilding(myUnit);
        	}
        	/*else
        	{
        		strategy.addUnit(new MoleUnit(myUnit, Information.UnitType.WORKER));
        	}
        	
        	/*if(myUnit.getType() == UnitType.Terran_SCV)
        	{
        		main.addUnit(new MoleUnit(myUnit, Information.UnitType.WORKER, Information.Job.MINERALS));
        	}*/
        }
  
        
        //strategy.addBase(main);
        //strategy.buildingManager.main = main;
    }

    @Override
    public void onFrame() {
        //game.setTextSize(10);
    	//System.out.println("Numbases: " + strategy.bases.size());
    	
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());

        lastErr = game.getLastError();
        
        strategy.update(game, self);
        strategy.drawUnitInfo(self, game);
    }

    public static void main(String[] args) {
        new TestBot1().run();
    }
}