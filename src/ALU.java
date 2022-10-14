
public class ALU {
	
	public static short ADD(Register R1, Register R2) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) + (R2.getData() & 0b00000000000000000000000011111111));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);  
		System.out.println(R1.getData() + " by Adding.");
		return result;
	}
	
	public static short SUB(Register R1, Register R2) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) - (R2.getData() & 0b00000000000000000000000011111111));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);
		System.out.println(R1.getData() + " by Subtracting.");
		return result;
	}
	
	public static short MUL(Register R1, Register R2) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) * (R2.getData() & 0b00000000000000000000000011111111));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);  
		System.out.println(R1.getData() + " by Multiplying.");
		return result;
	}
	
	public static void LDI(Register R1, byte IMM) {
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData(IMM);
		System.out.println(R1.getData() + ".");
	}
	
	public static void BEQZ(Register R1, int IMM) {
		if(R1.getData() == 0) {
			CPU.getPC().setPCData((short) (CPU.getPC().getPCData() + IMM));
			System.out.println(">> Branching to instruction " + (CPU.getPC().getPCData()+1) + ".");
		}
		else
		System.out.println(">> No Branching since Register " + R1.getName() + "'s value is not zero.");
	}
	
	public static short AND(Register R1, Register R2) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) & (R2.getData() & 0b00000000000000000000000011111111));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);
		System.out.println(R1.getData() + " by ANDing.");
		return result;
	}
	
	public static short OR(Register R1, Register R2) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) | (R2.getData() & 0b00000000000000000000000011111111));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);
		System.out.println(R1.getData() + " by ORing.");
		return result;
	}
	
	public static void JR(Register R1, Register R2) {
		CPU.getPC().setPCData((short) ((R1.getData() << 6) | R2.getData()));
		System.out.println(">> Jumping to instruction " + (CPU.getPC().getPCData()+1) + ".");
	}
	
	public static short SLC(Register R1, int IMM) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) << IMM | (R1.getData() & 0b00000000000000000000000011111111) >>> (8 - IMM));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);
		System.out.println(R1.getData() + " by shifting left " + IMM + " times.");
		return result;
	}
	
	public static short SRC(Register R1, int IMM) {
		short result = (short) ((R1.getData() & 0b00000000000000000000000011111111) >>> IMM | (R1.getData() & 0b00000000000000000000000011111111) << (8 - IMM));
		System.out.print(">> Register " + R1.getName() + " has changed from " + R1.getData() + " to ");
		R1.setData((byte) result);
		System.out.println(R1.getData() + " by shifting left " + IMM + " times.");
		return result;
	}
	
	public static void LB(Register R1, Memory MEM, int ADDRESS) {
		R1.setData(MEM.getDataMemory()[ADDRESS]);
		System.out.println(">> Value of " + MEM.getDataMemory()[ADDRESS] + " from Memory Address " + ADDRESS + " has been stored in Register " + R1.getName() + ".");
	}
	
	public static void SB(Register R1, Memory MEM, int ADDRESS) {
		MEM.getDataMemory()[ADDRESS] = (byte) R1.getData(); 
		System.out.println(">> Register " + R1.getName() + "'s value of " + R1.getData() +" has been stored in Memory Address " + ADDRESS +  ".");
	}
}
