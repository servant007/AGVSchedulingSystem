package schedulingSystem.toolKit;
public class MyToolKit {
	
	public byte[] HexString2Bytes(String src) {
		if (null == src || 0 == src.length()) {
			return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
        	ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

	public byte uniteBytes(byte src0, byte src1) { 
			byte _b0 = Byte.decode("0x" + new String(new byte[] {src0})).byteValue(); 
			_b0 = (byte) (_b0 << 4); 
			byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue(); 
			byte ret = (byte) (_b0 ^ _b1); 
			return ret; 
	}

	public String printHexString( byte[] b) { 
		String buff = "";
		for (int i = 0; i < b.length; i++) { 
			String hex = Integer.toHexString(b[i] & 0xFF); 
			if (hex.length() == 1) { 
				hex = '0' + hex; 
			} 
			buff+=hex;
		} 
		return buff;
	}
}
