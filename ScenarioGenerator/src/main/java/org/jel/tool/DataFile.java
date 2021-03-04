package org.jel.tool;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DataFile {
    private String background;
    private final List<Double> coins = new ArrayList<>();
    private final List<String> clanNames = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private final List<City> cities = new ArrayList<>();
    private double[][] matrix;
    private final HashMap<Integer, HashMap<Integer, Integer>> relations = new HashMap<>();
    private final List<Integer> flags = new ArrayList<>();

    public DataFile addCity(final City c) {
        this.cities.add(c);
        return this;
    }

    public DataFile addCityConnection(final int cityA, final int cityB, final double distance) {
        if (this.matrix == null) {
            this.matrix = new double[this.cities.size()][this.cities.size()];
        }
        this.matrix[cityA][cityB] = distance;
        this.matrix[cityB][cityA] = distance;
        return this;
    }

    public DataFile addPlayer(final double coins, final String name, final Color clanColor, final int flags) {
        this.coins.add(coins);
        this.clanNames.add(name);
        this.colors.add(clanColor);
        this.flags.add(flags);
        return this;
    }

    public DataFile addRelation(final int clanA, final int clanB, final int relationship) {
        var c = this.relations.get(clanA);
        if (c == null) {
            c = new HashMap<>();
        }
        c.put(clanB, relationship);
        this.relations.put(clanA, c);
        return this;
    }

    public void dump(final String out) {
        try (final var dos = new DataOutputStream((new FileOutputStream(out)))) {
            dos.write(0xAA);
            dos.write(0x55);
            final var back = this.readBackground();
            dos.writeInt(back.length);
            dos.write(back);
            dos.writeInt(this.coins.size());
            this.coins.forEach(t -> {
                try {
                    dos.writeDouble(t);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            this.clanNames.forEach(t -> {
                try {
                    dos.writeUTF(t);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            this.flags.forEach(a -> {
                try {
                    dos.writeInt(a);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            this.colors.forEach(t -> {
                try {
                    dos.writeInt(t.getRed());
                    dos.writeInt(t.getGreen());
                    dos.writeInt(t.getBlue());
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
            var size = 0;
            for (final var a : this.relations.entrySet()) {
                size += a.getValue().size();
            }
            dos.writeInt(size);
            for (final var a : this.relations.entrySet()) {
                for (final var b : a.getValue().entrySet()) {
                    dos.writeInt(a.getKey());
                    dos.writeInt(b.getKey());
                    dos.writeInt(b.getValue());
                }
            }
            dos.writeShort(this.cities.size());
            var a = 0;
            for (final City c : this.cities) {
                final var b = this.readFile(c.getBackground());
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
                var num = 0;
                for (var i = 0; i < this.cities.size(); i++) {
                    num += this.matrix[a][i] != 0 ? 1 : 0;
                }
                dos.writeShort(num);
                for (var i = 0; i < this.cities.size(); i++) {
                    if (this.matrix[a][i] != 0) {
                        dos.writeShort(i);
                        dos.writeDouble(this.matrix[a][i]);
                    }
                }
                c.getProductions().forEach(t -> {
                    try {
                        dos.writeDouble(t);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                });
                a++;
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBackground() throws Exception {
        try (final var stream = this.getClass().getClassLoader().getResourceAsStream("images/" + this.background)) {
            return stream.readAllBytes();
        }
    }

    private byte[] readFile(final String s) throws IOException {
        try (final var stream = this.getClass().getClassLoader().getResourceAsStream("images/" + s)) {
            return stream.readAllBytes();
        }
    }

    public DataFile setBackground(final String file) {
        this.background = file;
        return this;
    }
}
