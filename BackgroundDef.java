class BackgroundDef {
 int width;
 int height;
 int tileWidth;
 int tileHeight;
 Tile[] tiles;
 
 public BackgroundDef(int w, int h, int twidth, int theight){
	 tileWidth=twidth;
	 tileHeight = theight;
	 width = w;
	 height = h;
	 tiles = new Tile[w*h];
 }

 
 public Tile getTile(int x, int y) {
 return tiles[y * width + x];
 }
 public Tile getTileWithPixCoordinates(int x, int y){
	 return tiles[(y/tileHeight) * width + (x/tileWidth)];
 }
 public void setTile(int x, int y, int tex, boolean coll){
	 tiles[y*width+x] = new Tile(tex, coll);
 }
}

class Tile{
	int tex;
	boolean collidable;
	public Tile(int tex,boolean collidable){
		this.collidable = collidable;
		this.tex = tex;
	}
	
}