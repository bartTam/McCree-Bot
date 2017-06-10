package util;

public class Utils {

	
	public static byte convertToSignedByte(int input){
		if(input < 0){
			return (byte)((0x7F & input) | 0x80);
		} else {
			return (byte)(0xFF & input);
		}
	}
	
	public static int convertToSignedInt(byte input){
		if(input < 0){
			return (0xFFFFFFFF & input);
		} else {
			return (0x000000FF & input);
		}
	}
	
}
