package com.apet2929.clothsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Simulator extends ApplicationAdapter implements InputProcessor {
	static final int CREATING_NODES = 0;
	static final int CREATING_CONNECTIONS = 1;
	static final int RUNNING = 2;
	static final int PAUSED = 3;
	static final int NUM_STATES = 4;
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 1000;

	SpriteBatch batch;
	ShapeRenderer sr;
	Texture img;
	ArrayList<Node> nodes;
	ArrayList<Connection> connections;
	int state = CREATING_NODES;

	Node selected;


	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);

		nodes = new ArrayList<>();
		connections = new ArrayList<>();

		int[] nodePositions = new int[]{
				300, 300,
				400, 400,
				300, 400,
				400, 300,
				350, 350,
				390, 310,
				380, 200
		};

		String[] connections = new String[]{
				"1 2 3",
				"2 3 4 5",
				"3 4 5",
				"5 4 6",
				"6 4 0",
		};

//		loadSimulator(nodePositions, connections);
//		saveState("testSave1");
		loadState("testSave1");
//		this.nodes.get(3).setFixed(true);
		Gdx.input.setInputProcessor(this);

	}

	@Override
	public void render () {
		if(shouldUpdate()) {
			float delta = Gdx.graphics.getDeltaTime();
			for (Connection connection : connections) connection.update();
			for (Node node : nodes) node.applyForce(new Vector2(0, -9.8f * node.weight));
			for (Node node : nodes) node.update(delta);
		}

		ScreenUtils.clear(0, 0, 0, 1);

		sr.begin();

		sr.set(ShapeRenderer.ShapeType.Filled);
		for(Node node : nodes) node.render(sr);
		if(selected != null) sr.circle(selected.position.x, selected.position.y, 17);


		sr.set(ShapeRenderer.ShapeType.Line);
		for(Connection connection : connections) connection.render(sr);
		sr.end();

		batch.begin();
		batch.draw(img, 0,0);
		batch.end();
	}

	private void loadSimulator(int[] positions, String[] connections){
		loadNodes(positions);
		for (String connection : connections) {
			loadConnections(connection);
		}
	}

	private void loadNodes(int[] positions){
		for (int i = 0; i < positions.length; i+=2) {
			Node n = new Node(positions[i],positions[i+1]);
			nodes.add(n);
		}
	}

	private void loadConnections(String line){
		String[] data = line.split(" ");
		int indexA = Integer.parseInt(data[0]); // index of node to connect to
		Node nodeA = nodes.get(indexA);
		for (int i = 1; i < data.length; i++) {
			int indexB = Integer.parseInt(data[i]);
			Node nodeB = nodes.get(indexB);
			createConnection(nodeA, nodeB);
		}
	}

	void createConnection(Node a, Node b){
		connections.add(new Connection(a, b));
	}

	void tryCreateConnection(int x, int y){
		if(selected == null) {
			selected = getClosestNode(x, y);
		} else {
			Node b = getClosestNode(x, y);
			if(b == selected){
				selected.toggleFixed();
			}

			createConnection(selected, getClosestNode(x,y));
			selected = null;
		}
	}

	Node getClosestNode(int x, int y){
		if(nodes.size() == 0) return null;
		Node closest = nodes.get(0);
		float closestDist = closest.position.dst2(x, y);
		for (Node node : nodes) {
			float dist = node.position.dst2(x, y);
			if(dist < closestDist) {
				closest = node;
				closestDist = dist;
			}
		}
		return closest;
	}

	void createNode(int x, int y){
		nodes.add(new Node(x, y));
	}

	boolean shouldUpdate(){
		return state == RUNNING;
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;
		System.out.println("Touch up!");
		if(state == CREATING_NODES){
			createNode(screenX, screenY);
			System.out.println("Created node");
		} else if(state == CREATING_CONNECTIONS){
			tryCreateConnection(screenX, screenY);
		}

		return true;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	void deleteNode(Node node){
		nodes.remove(node);
		ArrayList<Connection> toRemove = new ArrayList<>();
		for (int i = 0; i < connections.size(); i++) {
			if(connections.get(i).hasNode(node)) toRemove.add(connections.get(i));
		}
		for (Connection con : toRemove) {
			connections.remove(con);
		}
	}

	public static ArrayList<Object> readObjectsFromFile(File file) throws IOException, ClassNotFoundException {
		ArrayList<Object> al = new ArrayList<Object>();
		boolean cont = true;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			while(cont){
				Object obj=null;
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (EOFException e){
					cont = false;
				}
				if(obj != null)
					al.add(obj);
				else
					cont = false;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return al;
	}

	public void loadState(String fileName){
		nodes.clear();
		connections.clear();
		ArrayList<Object> objects;
		try {
			objects = readObjectsFromFile(new File(fileName));
		} catch (Exception e){
			System.out.println("Could not load from file!");
			e.printStackTrace();
			return;
		}

		for (Object o : objects) {
			try {
				Node node = (Node) o;
				nodes.add(node);
				System.out.println("Loaded node");
			} catch (ClassCastException e){
				connections.add((Connection) o);
				System.out.println("Loaded connection");
			}
		}
	}

	public void saveState(String fileName){
		try {
			FileOutputStream fos = new FileOutputStream(fileName, true);
			try {
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				for (Node node : nodes){
					try {
						oos.writeObject(node);
						System.out.println("saved node");
					} catch (NotSerializableException e) {
						System.out.println("An object was not serializable, it has not been saved.");
						e.printStackTrace();
					}
				}

				for (Connection connection : connections){
					try {
						oos.writeObject(connection);
						System.out.println("saved connection");
					} catch (NotSerializableException e) {
						System.out.println("An object was not serializable, it has not been saved.");
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	String getDesiredFileName(String action){
		Scanner scanner = new Scanner(System.in);
		System.out.println("What file do you want to " + action + "?");
		String n = scanner.next();
		scanner.close();
		return n;
	}


	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(character == 10){ // return character
			state++;
			state = state % NUM_STATES;
			System.out.println("state = " + state);
		} else if(character == 's'){
			state = PAUSED;
			String fileName = getDesiredFileName("save to");
			saveState(fileName);
		} else if(character == 'l'){
			state = PAUSED;
			String fileName = getDesiredFileName("load from");
			loadState(fileName);
		}
		else if(character == 'c'){
			state = CREATING_NODES;
			nodes.clear();
			connections.clear();
		} else if(character == 'd'){
			if(state == CREATING_CONNECTIONS){
				deleteNode(selected);
				selected = null;
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
