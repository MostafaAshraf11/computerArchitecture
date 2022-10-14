import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CPU {
	
	private static Register[] Registers;       // 8 Bits except PC (16 Bits)
	private static Register PC;
	private static Register SREG;
	private static Memory memory;
	private static ArrayList<String> list = new ArrayList<String>();

	private static CPU CPU = new CPU();
	
	public CPU() {
		memory = new Memory();
		Registers = new Register[64];
		
		for(int i = 0; i < 64; i++) {
			Registers[i] = new Register("R" + i);
		}
		
		PC = new Register("PC");
		SREG = new Register("SREG");
	}
	
	public static void parser(String Assembly) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(Assembly));
			String ins;
			while((ins = in.readLine()) != null) {
				list.add(ins);
			}
			String[] instructions = list.toArray(new String[1024]);
		
		for(int i=0; i < list.size(); i++) {
			String[] line = instructions[i].split(" ");
			short instruction = 0;
			short var1;
			short var2;
			
			if(line[1].length() > 2) {
				int num1 = 10*Character.getNumericValue(line[1].charAt(1));
				var1 = (short) ((short) (num1 + Character.getNumericValue(line[1].charAt(2))));
			} else {
				var1 = (short) ((short) Character.getNumericValue(line[1].charAt(1)));
			}

			//Check on R/I Format 
			if(line[2].charAt(0) == 'R') {
				if(line[2].length() > 2) {
					int num2 = 10*Character.getNumericValue(line[2].charAt(1));
					var2 = (short) (num2 + Character.getNumericValue(line[2].charAt(2)));
				} else {
					var2 = (short) Character.getNumericValue(line[2].charAt(1));
				}
			} else {
				var2 = (short) (Integer.parseInt(line[2]));
			}
			
			instruction = (short) (((var1 << 6) & 0b0000111111000000) | (var2 & 0b0000000000111111));

			// Assign opcode
			switch(line[0].toUpperCase()) {
				case "ADD": ;break;
				case "SUB": instruction = (short) (0b0001000000000000 | instruction); break;
				case "MUL": instruction = (short) (0b0010000000000000 | instruction); break;
				case "LDI": instruction = (short) (0b0011000000000000 | instruction); break;
				case "BEQZ": instruction = (short) (0b0100000000000000 | instruction); break;
				case "AND": instruction = (short) (0b0101000000000000 | instruction); break;
				case "OR": instruction = (short) (0b0110000000000000 | instruction); break;
				case "JR": instruction = (short) (0b0111000000000000 | instruction); break;
				case "SLC": instruction = (short) (0b1000000000000000 | instruction); break;
				case "SRC": instruction = (short) (0b1001000000000000 | instruction); break;
				case "LB": instruction = (short) (0b1010000000000000 | instruction); break;
				case "SB": instruction = (short) (0b1011000000000000 | instruction); break;
				default: instruction = 0; break;
			}
			
			memory.getInstructionMemory()[i] = instruction;
		}
	}
	
	public static void printRegisterArray(Register[] registers) {
		System.out.print("[");
		for(int i = 0; i < registers.length; i++) {
			if(i == registers.length-1) {
				System.out.print(registers[i].getData());
			} else {
			System.out.print(registers[i].getData() + ", ");
			}
		}
		System.out.print("]");
		System.out.println();
	}
	
	public static String instructionToBinary(short instruction) {
		return String.format("%4s", Integer.toBinaryString((instruction & 0b00000000000000001111000000000000)>>12)).replace(" ", "0")
			+ " " + String.format("%6s", Integer.toBinaryString((instruction & 0b00000000000000000000111111000000)>> 6)).replace(" ", "0")
			+ " " + String.format("%6s", Integer.toBinaryString(instruction & 0b00000000000000000000000000111111)).replace(" ", "0");
	}
	
	public static void updateCarry(short result) {
		if(((result >>> 8) & 0b0000000000000001) == 1) {
			SREG.setData((byte) (SREG.getData() | 0b00010000));
		}
		if(((result >>> 8) & 0b0000000000000001) == 0) {
			SREG.setData((byte) (SREG.getData() & 0b11101111));
		}
	}

	
	public static void updateOverflow(short result, byte R1Data, byte R2Data) {
		boolean bothPos = (R1Data >= 0) && (R2Data >= 0);
		boolean bothNeg = (R1Data < 0) && (R2Data < 0);
		if(((byte)result ==(byte) (R1Data + R2Data)) && (bothPos || bothNeg )) {
			if(((result & 0b0000000010000000) >>> 7 == 0 && bothNeg) || ((result & 0b0000000010000000) >>> 7 == 1 && bothPos))
				SREG.setData((byte) (SREG.getData() | 0b00001000));
			else
				SREG.setData((byte) (SREG.getData() & 0b11110111));
		}
		else if(result == R1Data - R2Data && !(bothPos || bothNeg)) {
			if((result & 0b0000000010000000) >> 7 == (R2Data & 0b0000000010000000) >> 7)
				SREG.setData((byte) (SREG.getData() | 0b00001000));
			else
				SREG.setData((byte) (SREG.getData() & 0b11110111));
		}
	}

	public static void updateNegative(short result) {
		if((result & 0b0000000010000000) >> 7 == 1)
			SREG.setData((byte) (SREG.getData() | 0b00000100));
		else
			SREG.setData((byte) (SREG.getData() & 0b11111011));
	}
	
	public static void updateSign() {
		byte N = (byte) (SREG.getData() >> 2);
		byte V = (byte) (SREG.getData() >> 3);
		byte S = (byte) ((((~N & V) | (N & ~V)) & 0b00000001) << 1) ;
		SREG.setData((byte) ((byte) (SREG.getData() & 0b11111101)| S));
	}
	
	public static void updateZero(short result) {
		if((result & 0b0000000011111111) == 0)
			SREG.setData((byte) (SREG.getData() | 0b00000001));
		else
			SREG.setData((byte) (SREG.getData() & 0b11111110));
	}
	
	public static CPU getCPU() {
		return CPU;
	}


	public void setCPU(CPU cPU) {
		CPU = cPU;
	}


	public Memory getMemory() {
		return memory;
	}


	public void setMemory(Memory mem) {
		memory = mem;
	}


	public static Register[] getRegisters() {
		return Registers;
	}


	public static void setRegisters(Register[] registers) {
		Registers = registers;
	}


	public static Register getPC() {
		return PC;
	}


	public static void setPC(Register pC) {
		PC = pC;
	}


	public static Register getSREG() {
		return SREG;
	}


	public static void setSREG(Register sREG) {
		SREG = sREG;
	}
	
	public static ArrayList<String> getList() {
		return list;
	}

	public static void setList(ArrayList<String> list) {
		CPU.list = list;
	}

	
}
