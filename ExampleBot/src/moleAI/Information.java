package moleAI;

public class Information {
	public enum UnitType
	{
	    UNDEFINED(0), MARINE (1), MEDIC (2), FIREBAT(3), WORKER (4);
	    
	    private final int num;
		UnitType(int num)
		{
			this.num = num;
		}
	}
	
	public enum BaseState
	{
		SAFE(0), DISTRESSED(1), ATTACKING(2);
		
		private final int num;
		BaseState(int num)
		{
			this.num = num;
		}
	}
	
	public enum Job
	{
		UNDEFINED(0), MINERALS (1), GAS (2), CONSTRUCTION (3), SCOUT (4), ATTACK (5), DEFEND (6), REGROUP(7);
	    
	    private final int num;
		Job(int num)
		{
			this.num = num;
		}
	}
	
	public enum BuildingStatus
	{
	    UNASSIGNED (0), ASSIGNED (1), UNDERCONSTRUCTION (2);
	    
	    private final int num;
		BuildingStatus(int num)
		{
			this.num = num;
		}
	}
	
	public enum ProductionLevel
	{
	    NONE(0), LOW (1), MEDIUM(2), HIGH(3), MASS(4);
	    
	    private final int num;
		ProductionLevel(int num)
		{
			this.num = num;
		}
		
		int getLevel()
		{
			return this.num;
		}
		
		ProductionLevel getLower()
		{
			if(num == 3)
			{
				return ProductionLevel.MEDIUM;
			}
			else if(num == 2)
			{
				return ProductionLevel.LOW;
			}
			else if(num == 1)
			{
				return ProductionLevel.NONE;
			}
			else
			{
				return ProductionLevel.NONE;
			}
		}
	}
}
