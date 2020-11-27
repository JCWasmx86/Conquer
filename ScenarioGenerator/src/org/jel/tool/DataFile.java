package org.jel.tool;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataFile {
	private String background;
	private List<Double> coins = new ArrayList<>();
	private List<String> clanNames = new ArrayList<>();
	private List<Color> colors = new ArrayList<>();
	private List<City> cities = new ArrayList<>();
	private double[][] matrix;
	private HashMap<Integer, HashMap<Integer, Integer>> relations = new HashMap<>();
	private List<Integer> flags = new ArrayList<>();

	public DataFile addRelation(int i, int j, int v) {
		var c = relations.get(i);
		if (c == null)
			c = new HashMap<>();
		c.put(j, v);
		relations.put(i, c);
		return this;
	}

	public DataFile setBackground(String file) {
		this.background = file;
		return this;
	}

	public DataFile addPlayer(double coins, String name, Color c, int i) {
		this.coins.add(coins);
		this.clanNames.add(name);
		this.colors.add(c);
		this.flags.add(i);
		return this;
	}

	public DataFile addCity(City c) {
		this.cities.add(c);
		return this;
	}

	public DataFile addCityConnection(int a, int b, double weight) {
		if (matrix == null)
			matrix = new double[cities.size()][cities.size()];
		matrix[a][b] = weight;
		matrix[b][a] = weight;
		return this;
	}

	public void dump(String out) {
		try (DataOutputStream dos = new DataOutputStream((new FileOutputStream(out)))) {
			dos.write(0xAA);
			dos.write(0x55);
			byte[] back = readBackground();
			dos.writeInt(back.length);
			dos.write(back);
			dos.writeByte(this.coins.size());
			coins.forEach(t -> {
				try {
					dos.writeDouble(t);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			clanNames.forEach(t -> {
				try {
					dos.writeUTF(t);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			flags.forEach(a -> {
				try {
					dos.writeByte(a);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			colors.forEach(t -> {
				try {
					dos.writeInt(t.getRed());
					dos.writeInt(t.getGreen());
					dos.writeInt(t.getBlue());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			int size = 0;
			for (var a : relations.entrySet()) {
				size += a.getValue().size();
			}
			dos.writeInt(size);
			for (var a : relations.entrySet()) {
				for (var b : a.getValue().entrySet()) {
					dos.writeByte(a.getKey());
					dos.writeByte(b.getKey());
					dos.writeInt(b.getValue());
				}
			}
			dos.writeShort(cities.size());
			int a = 0;
			for (City c : this.cities) {
				byte[] b = this.readFile(c.getBackground());
				dos.writeInt(b.length);
				dos.write(b);
				dos.writeInt(c.getClan());
				dos.writeInt(c.getNumberOfPeople());
				dos.writeInt(c.getNumberOfSoldiers());
				dos.writeInt(c.getX());
				dos.writeInt(c.getY());
				dos.writeInt(c.getDefense());
				dos.writeDouble(c.getDefenseBonus());
				dos.writeDouble(c.getGrowth());
				dos.writeUTF(c.getName());
				int num = 0;
				for (int i = 0; i < this.cities.size(); i++)
					num += this.matrix[a][i] != 0 ? 1 : 0;
				dos.writeShort(num);
				for (int i = 0; i < this.cities.size(); i++) {
					if (this.matrix[a][i] != 0) {
						dos.writeShort(i);
						dos.writeDouble(matrix[a][i]);
					}
				}
				c.getProductions().forEach(t -> {
					try {
						dos.writeDouble(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				a++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] readBackground() throws IOException {
		return Files.readAllBytes(Paths.get("images", this.background));
	}

	private byte[] readFile(String s) throws IOException {
		return Files.readAllBytes(Paths.get("images", s));
	}
}
