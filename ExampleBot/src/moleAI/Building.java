package moleAI;
import java.util.ArrayList;

import bwapi.*;
public class Building 
{
	
	      
		public TilePosition     desiredPosition;
		public TilePosition     finalPosition;
		//public Base				base;
		public Position         position;
		public UnitType         type;
		public Unit             buildingUnit;
		public MoleUnit             builderUnit;
		public Information.BuildingStatus   status;
		public int              lastOrderFrame;
		public boolean          buildCommandGiven;
		public boolean          underConstruction;

		public Building()
	    {
			this.desiredPosition = bwapi.TilePosition.None;
			this.finalPosition = bwapi.TilePosition.None;
			this.position = bwapi.Position.None;
			this.type = UnitType.Unknown;
			this.buildingUnit = null;
			this.builderUnit = null;
			this.lastOrderFrame = 0;
			this.status = Information.BuildingStatus.UNASSIGNED;
			this.buildCommandGiven = false;
			this.underConstruction = false;
			//this.base = null;
	    }
		
		public Building(UnitType type)
	    {
			this.desiredPosition = bwapi.TilePosition.None;
			this.finalPosition = bwapi.TilePosition.None;
			this.position = bwapi.Position.None;
			this.type = type;
			this.buildingUnit = null;
			this.builderUnit = null;
			this.lastOrderFrame = 0;
			this.status = Information.BuildingStatus.UNASSIGNED;
			this.buildCommandGiven = false;
			this.underConstruction = false;
			//this.base = null;
	    } 

		class BuildingData 
		{
		    private ArrayList<Building>     _buildings;

			public BuildingData() {}
			
		    ArrayList<Building> getBuildings() {return _buildings;}

			void        addBuilding(Building b) {_buildings.add(b);}
			void        removeBuilding(Building b) {_buildings.remove(b);}
		    void        removeBuildings(ArrayList<Building> buildings){_buildings.removeAll(buildings);}
		    
			boolean     isBeingBuilt(UnitType type)
			{
				for(Building b : _buildings)
				{
					if(b.type == type)
					{
						return true;
					}
				}
				
				return false;
			}
		};
}
