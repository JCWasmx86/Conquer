package conquer.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

class MagicNumberURLInputStreamTest {

    @Test
    void testNullArguments() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MagicNumberURLInputStream((URL) null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MagicNumberURLInputStream((URI) null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MagicNumberURLInputStream((File) null));
    }

    @Test
    void testReadAfterMagicNumber() throws IOException {
        try (final var input = new MagicNumberURLInputStream(new URL("https://www.example.com"))) {
            final var N = 20;
            final var magic = input.getMagicNumber(N);
            for (var i = 0; i < N; i++) {
                final var cmp = input.read();
                Assertions.assertEquals(magic[i], cmp);
                System.out.println(cmp + "//" + magic[i] + "//" + ((char) cmp) + "//" + ((char) magic[i]));
            }
        }
    }

    @Test
    void readFromFile() throws IOException {
        final var text = "abcdefgh";
        final var other = "foobarbaz";
        Files.write(Paths.get("foo.txt"), (text + other).getBytes());
        try (final var input = new MagicNumberURLInputStream(new File("foo.txt"))) {
            final var magic = input.getMagicNumber(text.length());
            Assertions.assertArrayEquals(text.getBytes(), magic);
            final var bytes = input.readAllBytes();
            Assertions.assertArrayEquals((text + other).getBytes(), bytes);
        }
        Files.delete(Paths.get("foo.txt"));
    }

    @Test
    void testRead() throws IOException {
        try (final var input = new MagicNumberURLInputStream(new URL("https://www.example.com"))) {
            System.out.println(new String(input.readAllBytes()));
        }
    }
}
