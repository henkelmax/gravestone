package de.maxhenkel.gravestone.util;

public class BlockPos {
	private int x, y, z;

	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPos(double x, double y, double z) {
		this.x = (int) Math.floor(x);
		this.y = (int) Math.floor(y);
		this.z = (int) Math.floor(z);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public BlockPos down(){
		return new BlockPos(x, y-1, z);
	}

	@Override
	public String toString() {
		return "BlockPos [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
