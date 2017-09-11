import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class ITunesFormatM4aEmbeddedLyricsExtractor {

    private String path;

    private String lyrics;

    private int allocatedBytes = 65536;

    private byte[] data = null;

    private static final byte[] LYRICS_SIGNATURE = {
        0x6c, 0x79, 0x72
    };

    private static final int LYRICS_SIZE_INFORMATION_BYTES = 4;

    private static final byte[] LYRICS_HEADER = {
        0x64, 0x61, 0x74, 0x61, 0x00, 0x00,
        0x00, 0x01, 0x00, 0x00, 0x00, 0x00
    };

    private static final int SEARCH_SIGNATURE = 0;

    private static final int GET_LYRICS_SIZE = 1;

    private static final int CONFIRM_LYRICS = 2;

    private static final int STORE_LIRICS_DATA = 3;

    ITunesFormatM4aEmbeddedLyricsExtractor(String path) {
        this.path = path;
        try {
            readLyrics();
        } catch (Exception e) {
            // nothing to do
        }
    }

    ITunesFormatM4aEmbeddedLyricsExtractor(String path, int allocatedBytes) {
        this.path = path;
        this.allocatedBytes = allocatedBytes;
        try {
            readLyrics();
        } catch (Exception e) {
            // nothing to do
        }
    }

    private void readLyrics() throws IOException {
        FileInputStream input = new FileInputStream(this.path);
        byte[] buffer = new byte[allocatedBytes];
        int offset = 0;
        byte[] tmp = new byte[4];
        int status = SEARCH_SIGNATURE;
        int dataSize = 0;

        while (input.read(buffer) != -1) {
            for (byte b : buffer) {

                // search lyrics signature
                if (status == SEARCH_SIGNATURE) {
                    if (b == LYRICS_SIGNATURE[offset]) {
                        ++offset;

                        if (offset == LYRICS_SIGNATURE.length) {
                            status = GET_LYRICS_SIZE;
                            offset = 0;
                        }

                    } else if (offset != 0) {
                        offset = 0;
                    }

                // get lyrics section size
                } else if (status == GET_LYRICS_SIZE) {
                    tmp[offset] = b;
                    ++offset;

                    if (offset == LYRICS_SIZE_INFORMATION_BYTES) {
                        status = CONFIRM_LYRICS;
                        offset = 0;
                        dataSize = ByteBuffer.wrap(tmp).getInt()
                            - LYRICS_SIZE_INFORMATION_BYTES
                            - LYRICS_HEADER.length;
                    }

                // check if lyrics header or not
                } else if (status == CONFIRM_LYRICS) {
                    if (b == LYRICS_HEADER[offset]) {
                        ++offset;

                        if (offset == LYRICS_HEADER.length) {
                            status = STORE_LIRICS_DATA;
                            offset = 0;
                            this.data = new byte[dataSize];
                        }
                    } else {
                        status = SEARCH_SIGNATURE;
                        offset = 0;
                    }

                // store lyrics data
                } else if (status == STORE_LIRICS_DATA) {
                    this.data[offset] = b;
                    ++offset;

                    if (offset == dataSize) {
                        return;
                    }
                }

            }
        }

        return;
    }

    public String getLyrics() {
        return getLyrics("UTF-8");
    }

    public String getLyrics(String format) {
        if (this.data == null) {
            return null;
        }

        try {
            // replace line feed code
            return new String(this.data, format).replace((char)13, '\n');

        } catch (Exception e) {
            return null;
        }
    }
}
