public class Memory {
	
	private static short[] InstructionMemory; // 16 Bits
	private static byte[] DataMemory;          // 8 Bits
	private static int instructionSize = 0;
	
	public Memory() {
		DataMemory = new byte[2048];
		InstructionMemory = new short[1024];
	}

	public byte[] getDataMemory() {
		return DataMemory;
	}

	public void setDataMemory(byte[] dataMemory) {
		DataMemory = dataMemory;
	}

	public short[] getInstructionMemory() {
		return InstructionMemory;
	}

	public void setInstructionMemory(short[] instructionMem) {
		InstructionMemory = instructionMem;
		int num = 0;
		for(int i = 0; i <this.getInstructionMemory().length && this.getInstructionMemory()[i]!=0; i++) {
			num++;
		}
		instructionSize = num;
	}

	public static int getInstructionSize() {
		return instructionSize;
	}

	public static void setInstructionSize(int instructionSize) {
		Memory.instructionSize = instructionSize;
	}

}
