import bwta.BWTA;
import bwta.BaseLocation;
import bwapi.*;

public class MapTools 
{
	TilePosition getNextExpansion(Player player, Game game)
	{
	    BaseLocation closestBase = null;
	    double minDistance = 100000;

	    TilePosition homeTile = player.getStartLocation();

	    // for each base location
	    for (BaseLocation base : BWTA.getBaseLocations())
	    {
	    	// if the base has gas
	        if (!base.isMineralOnly() && !(base == BWTA.getStartLocation(player)))
	        {
	            // get the tile position of the base
	            TilePosition tile = base.getTilePosition();
	            boolean buildingInTheWay = false;
	           
	            for (int x = 0; x < game.self().getRace().getCenter().tileWidth(); ++x)
	            {
	                for (int y = 0; y < game.self().getRace().getCenter().tileHeight(); ++y)
	                {	                
	                    TilePosition tp = new TilePosition(tile.getX() + x, tile.getY() + y);

	                    for (Unit unit : game.getUnitsOnTile(tp))
	                    {
	                        if (unit.getType().isBuilding() && !unit.isFlying())
	                        {
	                            buildingInTheWay = true;
	                            break;
	                        }
	                    }
	                }
	            }
	            
	            if (buildingInTheWay)
	            {
	                continue;
	            }
	            
	            // the base's distance from our main nexus
	            Position myBasePosition = player.getStartLocation().toPosition();
	            Position thisTile = tile.toPosition();
	            double distanceFromHome = myBasePosition.getDistance(thisTile);

	            // if it is not connected, continue
	            if (!BWTA.isConnected(homeTile,tile) || distanceFromHome < 0)
	            {
	                continue;
	            }

	            if (closestBase == null || distanceFromHome < minDistance)
	            {
	                closestBase = base;
	                minDistance = distanceFromHome;
	            }
	        }
	    }
	    
	    return closestBase.getTilePosition();
	}
}
