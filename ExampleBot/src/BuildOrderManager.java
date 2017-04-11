import java.util.ArrayList;
import bwapi.*;

public class BuildOrderManager 
{
	ArrayList<UnitType> unitQueue;
	ArrayList<Integer> workerLimit;
	
	public BuildOrderManager()
	{
		unitQueue = new ArrayList<UnitType>();
		workerLimit = new ArrayList<Integer>();
	}
	
	public void addItem(UnitType toAdd, Integer neededPop)
	{
		unitQueue.add(toAdd);
		workerLimit.add(neededPop);
	}
	
	public UnitType popItem()
	{
		UnitType result = null;
		if(unitQueue.size() > 0)
		{
			result = unitQueue.get(0);
			unitQueue.remove(0);
			workerLimit.remove(0);
		}
		return result;
	}
	
	public boolean canBuildNext(Player self, Integer workerPop)
	{
		if(unitQueue.size() > 0)
		{
			int mineralCost = unitQueue.get(0).mineralPrice();
			int gasCost = unitQueue.get(0).gasPrice();
			if(self.gas() >= gasCost && self.minerals() >= mineralCost && workerPop >= workerLimit.get(0))
			{
				return true;
			}
		}
		return false;
	}

}
