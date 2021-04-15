package conquer.data.ri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import conquer.data.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScenarioFileReaderTest {
	private static final byte[] PNG_BYTES = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
		0, 0, 0, 1, 0, 0, 0, 1, 1
		, 0, 0, 0, 0, 55, 110, -7, 36, 0, 0, 0, 16, 73, 68, 65, 84, 120, -100, 98, 96, 1, 0, 0, 0, -1, -1, 3, 0, 0
		, 6, 0, 5, 87, -65, -85, -44, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};

	private static final byte[] HEADER;
	private static final byte[] HEADER_WITH_CLANS_UNTIL_COLORS;
	private static final byte[] HEADER_UNTIL_RELATIONS;
	private static final byte[] HEADER_UNTIL_CITIES;

	static {
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(0xaa);
			dos.write(0x55);
			dos.writeInt(ScenarioFileReaderTest.PNG_BYTES.length);
			dos.write(ScenarioFileReaderTest.PNG_BYTES);
			HEADER = baos.toByteArray();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER);
			dos.writeInt(2);
			dos.writeDouble(1.0);
			dos.writeDouble(1.0);
			dos.writeUTF("clan1");
			dos.writeUTF("clan2");
			//Flags
			dos.writeInt(0);
			dos.writeInt(1);
			HEADER_WITH_CLANS_UNTIL_COLORS = baos.toByteArray();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(0);
			dos.writeInt(0);
			dos.writeInt(0);
			dos.writeInt(1);
			dos.writeInt(1);
			dos.writeInt(1);
			HEADER_UNTIL_RELATIONS = baos.toByteArray();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(1);
			dos.writeInt(50);
			HEADER_UNTIL_CITIES = baos.toByteArray();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ScenarioFileReaderTest() {
		//Private
	}

	private void check(final byte[] bytes) {
		try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			new ScenarioFileReader().read(bais);
		} catch (final IOException e) {
			Assertions.fail(e);
		} catch (final IllegalArgumentException iae) {
			System.err.println(Thread.currentThread().getStackTrace()[2].getMethodName() + ": " + iae.getMessage());
			return;
		}
		Assertions.fail("Didn't fail!");
	}

	@Test
	void testWrongMagicNumber() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream()) {
			baos.write(0x00);//Should be 0xaa
			baos.write(0x01);//Should be 0x55
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeBackgroundImageSize() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(0xAA);
			dos.write(0x55);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testZeroPlayers() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER);
			dos.writeInt(0);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testOnePlayer() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER);
			dos.writeInt(1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativePlayers() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeCoins() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER);
			dos.writeInt(2);
			dos.writeDouble(-1.0);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testRNegative() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testRTooBig() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(50000);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testGNegative() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testGTooBig() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(50000);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testBNegative() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testBTooBig() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(1);
			dos.writeInt(50000);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeAmountOfRelations() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testFirstClanTooBig() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(4);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testFirstClanNegative() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(-4);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testSecondClanTooBig() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(4);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testSecondClanNegative() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(-4);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeRelationshipValue() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testRelationshipToClanItSelf() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(0);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeNumberOfCities() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeSizeOfCityPicture() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeClanOfCity() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			dos.writeInt(ScenarioFileReaderTest.PNG_BYTES.length);
			dos.write(ScenarioFileReaderTest.PNG_BYTES);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testBadClanOfCity() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			dos.writeInt(ScenarioFileReaderTest.PNG_BYTES.length);
			dos.write(ScenarioFileReaderTest.PNG_BYTES);
			dos.writeInt(121);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeNumberOfPeople() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeNumberOfSoldiers() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 2);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeX() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 3);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeY() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 4);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	private void addBaseOfCity(final DataOutputStream dos, final int n) throws IOException {
		dos.writeInt(ScenarioFileReaderTest.PNG_BYTES.length);
		dos.write(ScenarioFileReaderTest.PNG_BYTES);
		for (var i = 0; i < n; i++) {
			dos.writeInt(1);
		}
	}

	@Test
	void testNegativeDefense() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 5);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeBonus() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeGrowth() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeNumberOfConnections() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city");
			dos.writeShort(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testCityConnectionToItSelf() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city");
			dos.writeShort(1);
			dos.writeShort(0);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testCityConnectionToBadCity() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city");
			dos.writeShort(1);
			dos.writeShort(5);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testBadDistance() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city");
			dos.writeShort(1);
			dos.writeShort(1);
			dos.writeDouble(-5);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeProduction() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(1);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city");
			dos.writeShort(1);
			dos.writeShort(1);
			dos.writeDouble(5);
			dos.writeDouble(-1);
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNotConnectedGraph() {
		final byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(ScenarioFileReaderTest.HEADER_UNTIL_CITIES);
			dos.writeShort(2);
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city");
			dos.writeShort(0);
			for (var i = 0; i < Resource.values().length; i++) {
				dos.writeDouble(1);
			}
			this.addBaseOfCity(dos, 6);
			dos.writeDouble(1);
			dos.writeDouble(1);
			dos.writeUTF("city1");
			dos.writeShort(0);
			for (var i = 0; i < Resource.values().length; i++) {
				dos.writeDouble(1);
			}
			bytes = baos.toByteArray();
		} catch (final IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}
}
