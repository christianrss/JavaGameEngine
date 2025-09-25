package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		RawModel model = OBJLoader.loadObjModel("tree", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		ModelTexture texture = staticModel.getTexture();
		
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);

		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		fern.getTexture().setHasTransparency(true);
		
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		
		TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader), 
				new ModelTexture(loader.loadTexture("lowPolyTree")));

		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random(5666778);
		for(int i = 0; i < 1200; i++) {
			if (i % 2 == 0) {
				entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 800 - 400, 0 ,
						random.nextFloat() * -400), 0, 0, 0, 1.8f));
				entities.add(new Entity(flower, new Vector3f(random.nextFloat() * 800 - 400, 0,
						random.nextFloat() * -400), 0, 0, 0, 2.3f));
				entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 800 - 400, 0,
						random.nextFloat() * -400), 0, random.nextFloat() * 360, 0, 0.9f));
			}
			
			if (i % 12 == 0) {
				entities.add(new Entity(bobble, new Vector3f(random.nextFloat() * 800 - 400, 0,
						random.nextFloat() * -600), 0, random.nextFloat() * 360, 0,
						random.nextFloat() * 0.1f + 0.6f));
				entities.add(new Entity(staticModel, new Vector3f(random.nextFloat() * 800 - 400,
						0, random.nextFloat() * -600), 0, 0, 0, random.nextFloat() * 1 + 4));
			
			}
		}
		
		texture.setShineDamper(10);
		texture.setReflectivity(0);
		//TexturedModel staticModel = new TexturedModel(model, texture);
		
		//Entity entity = new Entity(staticModel, new Vector3f(0,0,-25),0,0,0,1);
		Light light = new Light(new Vector3f(20000,40000,20000), new Vector3f(1,1,1));
		
		List<Terrain> terrains = new ArrayList<>();
		int size = 10;
		int radius = size / 2;

		for (int x = -radius; x < radius; x++) {
			for (int z = -radius; z < radius; z++) {
				Terrain terrain = new Terrain(x, z, loader, texturePack, blendMap);
				terrains.add(terrain);
			}
		}
		
		MasterRenderer renderer = new MasterRenderer();

		/*RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
			loader.loadTexture("white")));*/

		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel person = new TexturedModel(bunnyModel, new ModelTexture(
			loader.loadTexture("playerTexture")));

		Player player = new Player(person, new Vector3f(300, 0, -500), 0, 0, 0, 1);
		Camera camera = new Camera(player);

		while (!Display.isCloseRequested()) {
			//entity.increasePosition(0, 0, -0.1f);
			//entity.increaseRotation(0, 1, 0);
			camera.move();
			player.move();
			renderer.processEntity(player);

			for (Terrain terrain : terrains) {
				renderer.processTerrain(terrain);
			}
			
			for (Entity entity:entities) {
				renderer.processEntity(entity);
			}
			
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();

	}

}
