package xyz.bartkk.betterflags;

public class FlagUtils {
	static public byte[] packFlagColors(byte[] unpacked) {
		byte[] packed = new byte[96];

		for (int i = 0; i < 96; i++) {
			packed[i] = 0;
			packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 0] & 3) << 0);
			packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 1] & 3) << 2);
			packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 2] & 3) << 4);
			packed[i] = (byte)(packed[i] | (unpacked[i * 4 + 3] & 3) << 6);
		}

		return packed;
	}

	static public byte[] unpackFlagColors(byte[] packed) {
		byte[] unpacked = new byte[384];

		for (int i = 0; i < 96; i++) {
			unpacked[i * 4 + 0] = (byte)((packed[i] & 3) >> 0);
			unpacked[i * 4 + 1] = (byte)((packed[i] & 12) >> 2);
			unpacked[i * 4 + 2] = (byte)((packed[i] & 48) >> 4);
			unpacked[i * 4 + 3] = (byte)((packed[i] & 192) >> 6);
		}

		return unpacked;
	}
}
