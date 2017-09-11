# ITunesFormatM4aEmbeddedLyricsExtractor

This library extract lyrics embedded in m4a format audio file created by ITunes.

Please note that this library is created by deducing its format from multiple lyrics embedded m4a, not necessarily conforming to the official api of m4a.


## deduced m4a lyrics API
| offset | label | size | remarks |
| ------ | ----- | ---- | ------- |
| 0 | signature | 3 bytes | always 'l', 'y', 'r' |
| 3 | header size | 4 bytes | big-endian, size of lyrics header excluding signature |
| 7 | padding | 12 bytes | common to all header, always '64 61 74 61 00 00 00 01 00 00 00 00' |
| 19 | data | ? |
