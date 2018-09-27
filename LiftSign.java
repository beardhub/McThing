package com.beardman.bukkit.exampleplugin;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LiftSign {

	private Location loc;
	private String lift;	// lift direction: Up, Down, Left, Right, Forward, Backward
	private Vector teleDir;	// represents direction to look for destination
	private BlockFace faceDir;
	private LiftSign(Location loc, String liftStr, BlockFace faceDir){
		this.loc = loc;
		this.faceDir = faceDir;
		lift = liftStr.substring(6, liftStr.indexOf("]"));
		Vector signDir;
		switch(faceDir){
		case NORTH:	signDir = new Vector(0,0,1);	break;
		case SOUTH:	signDir = new Vector(0,0,-1);	break;
		case EAST:	signDir = new Vector(-1,0,0);	break;
		case WEST:	signDir = new Vector(1,0,0);	break;
		default:	signDir = new Vector(0,0,0);	break;
		}
		switch(lift){
		case "Up":		teleDir = new Vector(0,1,0);	break;
		case "Down":	teleDir = new Vector(0,-1,0);	break;
		case "Forward":	teleDir = signDir;				break;
		case "Backward":teleDir = signDir.multiply(-1);	break;
		case "Left":	teleDir = signDir.crossProduct(new Vector(0,-1,0));	break;
		case "Right":	teleDir = signDir.crossProduct(new Vector(0,1,0)); break;
		default:		teleDir = new Vector(0,0,0);	break;
		}
	}
	public static LiftSign parseLiftSign(Sign sign){
		String liftLine = sign.getLine(1);
		// Not valid lift sign
		if (!Pattern.matches("\\[Lift (Up|Down|Left|Right|Forward|Backward)\\]", liftLine)){
			return null;
		}
		BlockFace facing = ((org.bukkit.material.Sign) sign.getData()).getFacing();
		return new LiftSign(sign.getLocation(), liftLine, facing);
	}
	public Location getDestination(Player player, int maxSearchDistance){
		Location signLoc = loc.clone(),
				 playerLoc = player.getLocation().clone();
		// iterate until max distance reached or valid lift found
		for (int i = 0; i < maxSearchDistance; i ++){
			signLoc.add(teleDir);
			playerLoc.add(teleDir);
			Block block = signLoc.getBlock();
			if (block.getType() == Material.WALL_SIGN 	// wall sign found
					&& ((org.bukkit.material.Sign) block.getState().getData()).getFacing() == faceDir // facing same direction as original
					&& LiftSign.parseLiftSign((Sign) block.getState()) != null){	// is a valid lift sign
						return playerLoc.clone();
			}
		}
		return null;
	}
	public String getLiftDir(){
		return lift;
	}
}
