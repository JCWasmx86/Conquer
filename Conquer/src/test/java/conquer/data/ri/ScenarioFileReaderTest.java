package conquer.data.ri;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ScenarioFileReaderTest {
	private static final byte[] PNG_BYTES = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
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
			dos.writeInt(PNG_BYTES.length);
			dos.write(PNG_BYTES);
			HEADER = baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER);
			dos.writeInt(2);
			dos.writeDouble(1.0);
			dos.writeDouble(1.0);
			dos.writeUTF("clan1");
			dos.writeUTF("clan2");
			//Flags
			dos.writeInt(0);
			dos.writeInt(1);
			HEADER_WITH_CLANS_UNTIL_COLORS = baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(0);
			dos.writeInt(0);
			dos.writeInt(0);
			dos.writeInt(1);
			dos.writeInt(1);
			dos.writeInt(1);
			HEADER_UNTIL_RELATIONS = baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(1);
			dos.writeInt(50);
			HEADER_UNTIL_CITIES = baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ScenarioFileReaderTest() {
		//Private
	}

	private void check(final byte[] bytes) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			new ScenarioFileReader().read(bais);
		} catch (IOException e) {
			Assertions.fail(e);
		} catch (IllegalArgumentException iae) {
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
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(0xAA);
			dos.write(0x55);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testZeroPlayers() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER);
			dos.writeInt(0);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testOnePlayer() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER);
			dos.writeInt(1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativePlayers() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeCoins() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER);
			dos.writeInt(2);
			dos.writeDouble(-1.0);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testRNegative() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testRTooBig() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(50000);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testGNegative() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testGTooBig() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(50000);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testBNegative() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testBTooBig() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_WITH_CLANS_UNTIL_COLORS);
			dos.writeInt(1);
			dos.writeInt(1);
			dos.writeInt(50000);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeAmountOfRelations() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testFirstClanTooBig() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(4);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testFirstClanNegative() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(-4);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testSecondClanTooBig() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(4);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testSecondClanNegative() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(-4);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testNegativeRelationshipValue() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(1);
			dos.writeInt(-1);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}

	@Test
	void testRelationshipToClanItSelf() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_RELATIONS);
			dos.writeInt(1);
			dos.writeInt(0);
			dos.writeInt(0);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}
	@Test
	void testNegativeNumberOfCities() {
		byte[] bytes;
		try (final var baos = new ByteArrayOutputStream(); final var dos = new DataOutputStream(baos)) {
			dos.write(HEADER_UNTIL_CITIES);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			Assertions.fail(e);
			return;
		}
		this.check(bytes);
	}
}
