package com.tke.island.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;

public class Cuboid {

	private World world;
	private double x,X,y,Y,z,Z;

	public Cuboid(Location a, Location b) {
		if (a.getWorld() != b.getWorld())
			throw new IllegalArgumentException("World between 'a' and 'b' isn't equal");
		this.world = a.getWorld();
		this.x = Math.min(a.getX(), b.getX());
		this.X = Math.max(a.getX(), b.getX());
		this.y = Math.min(a.getY(), b.getY());
		this.Y = Math.max(a.getY(), b.getY());
		this.z = Math.min(a.getZ(), b.getZ());
		this.Z = Math.max(a.getZ(), b.getZ());
	}

	public boolean isInside(Entity entity) {
		return isInside(entity.getLocation());
	}

	public boolean isInside(Block block) {
		return isInside(block.getLocation());
	}

	public boolean isInside(Location location) {
		if (location.getWorld() != world)
			return false;
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		return (x >= this.x && y >= this.y && z >= this.z &&
				x <= this.X && y <= this.Y && z <= this.Z);
	}

	public List<Block> getBlocks() {
		List<Block> blocks = new ArrayList<>();
		for (int x = (int) this.x; x <= this.X; x++) {
			for (int y = (int) this.y; y <= this.Y; y++) {
				for (int z = (int) this.z; z <= this.Z; z++) {
					Block block = world.getBlockAt(x, y, z);
					blocks.add(block);
				}
			}
		}
		return blocks;
	}
	public List<Block> getBlocks(boolean ignoreAir) {
		List<Block> blocks = new ArrayList<>();
		for (int x = (int) this.x; x <= this.X; x++) {
			for (int y = (int) this.y; y <= this.Y; y++) {
				for (int z = (int) this.z; z <= this.Z; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (ignoreAir) {
						if (block.getType() != Material.AIR) {
							blocks.add(block);
						}
					} else
						blocks.add(block);
				}
			}
		}
		return blocks;
	}

	public Map<Material, Location> getMaterials() {
		Map<Material, Location> blocks = Maps.newHashMap();
		for (int x = (int) this.x; x <= this.X; x++) {
			for (int y = (int) this.y; y <= this.Y; y++) {
				for (int z = (int) this.z; z <= this.Z; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() != Material.AIR)
						blocks.put(block.getType(), block.getLocation());
				}
			}
		}
		return blocks;
	}

	public List<Location> getLocations() {
		List<Location> locations = new ArrayList<>();
		for (int x = (int) this.x; x <= this.X; x++) {
			for (int y = (int) this.y; y <= this.Y; y++) {
				for (int z = (int) this.z; z <= this.Z; z++)
					locations.add(new Location(world, x, y, z));
			}
		}
		return locations;
	}

}
