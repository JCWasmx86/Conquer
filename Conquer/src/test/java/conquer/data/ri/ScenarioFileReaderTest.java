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
	}

	private ScenarioFileReaderTest() {
		//Private
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
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			new ScenarioFileReader().read(bais);
		} catch (IOException e) {
			Assertions.fail(e);
		} catch (IllegalArgumentException iae) {
			//Do nothing, exception is expected.
		}
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
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			new ScenarioFileReader().read(bais);
		} catch (IOException e) {
			Assertions.fail(e);
		} catch (IllegalArgumentException iae) {
			//Do nothing, exception is expected.
		}
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
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			new ScenarioFileReader().read(bais);
		} catch (IOException e) {
			Assertions.fail(e);
		} catch (IllegalArgumentException iae) {
			//Do nothing, exception is expected.
		}
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
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			new ScenarioFileReader().read(bais);
		} catch (IOException e) {
			Assertions.fail(e);
		} catch (IllegalArgumentException iae) {
			//Do nothing, exception is expected.
		}
	}
}
