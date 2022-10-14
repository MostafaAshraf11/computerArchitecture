import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Datapath {
	
	static HashMap<String, Short> IF = new HashMap<String, Short>();
	static HashMap<String, Byte> ID = new HashMap<String, Byte>();
	static boolean ending = false;

	public static void fetch() {
		short instruction = CPU.getCPU().getMemory().getInstructionMemory()[CPU.getPC().getPCData()];
		if(instruction == 0) {
			IF.put("instruction", (short) -2000);
			return;
		}
		CPU.getPC().setPCData((short) (CPU.getPC().getPCData() + 1));
		System.out.println("Fetching Instruction " + (CPU.getPC().getPCData()) + ": " + CPU.instructionToBinary(instruction));
		IF.put("instruction", instruction);
	}

	public static void decode() {
		
		short instruction = IF.get("instruction");
		
		byte opcode = (byte) ((instruction & 0b1111000000000000) >> 12);
		byte R1 = (byte) ((instruction & 0b0000111111000000) >>> 6);
		byte R2, address, imm;
		R2 = address = imm = (byte) (instruction & 0b0000000000111111);	
		
		if(imm >> 5 == 1)
			imm = (byte) (imm | 0b0000000011000000);
		
		ID.put("opcode", opcode);
		ID.put("R1", R1);
		ID.put("R2", R2);
		ID.put("address", address);
		ID.put("imm", imm);
		
		System.out.println("Fetched instruction from Fetch Stage: " + CPU.instructionToBinary(instruction));
		System.out.println();
		
		System.out.println("Decoding Instruction " + (CPU.getPC().getPCData()) + ": " + CPU.instructionToBinary(instruction));
	}

	public static void execute() {

		byte opcode = ID.get("opcode");
		byte R1 = ID.get("R1");
		byte R2, address, imm;
		R2 = ID.get("R2");
		address = ID.get("address");
		imm = ID.get("imm");;	
		
		short instruction = (short) ((opcode <<  12) | (R1 << 6) | R2);
		
		if(imm >> 5 == 1)
			imm = (byte) (imm | 0b0000000011000000);
		
		System.out.println("Parameters passed from Decode Stage to Execute Stage:");
		System.out.println("Opcode: " + opcode);
		System.out.println("RS: " + R1);
		System.out.println("RT: " + R2);
		System.out.println("Immediate: " + imm);
		System.out.println("Address: " + address);
		System.out.println();
		
		if(ending)
			System.out.println("Executing Instruction " + (CPU.getPC().getPCData()) + ": " + CPU.instructionToBinary(instruction));
		else
			System.out.println("Executing Instruction " + (CPU.getPC().getPCData()-1) + ": " + CPU.instructionToBinary(instruction));
		
		short result = 0;
		
		byte R1Data = (byte) CPU.getRegisters()[R1].getData();
		byte R2Data = (byte) CPU.getRegisters()[R2].getData();
		
		switch(opcode) {
			case 0: result = ALU.ADD(CPU.getRegisters()[R1], CPU.getRegisters()[R2]); break;
			case 1: result = ALU.SUB(CPU.getRegisters()[R1], CPU.getRegisters()[R2]); break;
			case 2: result = ALU.MUL(CPU.getRegisters()[R1], CPU.getRegisters()[R2]); break;
			case 3: ALU.LDI(CPU.getRegisters()[R1], imm); break;
			case 4: ALU.BEQZ(CPU.getRegisters()[R1], imm); break;
			case 5: result = ALU.AND(CPU.getRegisters()[R1], CPU.getRegisters()[R2]); break;
			case 6: result = ALU.OR(CPU.getRegisters()[R1], CPU.getRegisters()[R2]); break;
			case 7: ALU.JR(CPU.getRegisters()[R1], CPU.getRegisters()[R2]); break;
			case 8: result = ALU.SLC(CPU.getRegisters()[R1], imm); break;
			case 9: result = ALU.SRC(CPU.getRegisters()[R1], imm); break;
			case 10: ALU.LB(CPU.getRegisters()[R1], CPU.getCPU().getMemory(), address); break;
			case 11: ALU.SB(CPU.getRegisters()[R1], CPU.getCPU().getMemory(), address); break;
			default: break;
		}
		
		if(opcode == 0) {
			CPU.updateCarry(result);
			CPU.updateOverflow(result, R1Data, R2Data);
			CPU.updateNegative(result);
			CPU.updateSign();
			CPU.updateZero(result);
			return;
		} 
		
		if(opcode == 1) {
			CPU.updateOverflow(result, R1Data, R2Data);
			CPU.updateNegative(result);
			CPU.updateSign();
			CPU.updateZero(result);
			return;
		}
		
		if(opcode == 2 || opcode == 5 || opcode == 6 || opcode == 8 || opcode == 9) {
			CPU.updateNegative(result);
			CPU.updateZero(result);
			return;
		}
	}

	public static void run(String Assembly) throws IOException {
		
		CPU.parser(Assembly);

		int cycleCount = 1;		
		short fetched = 0;
		short decoded = -1;
		short oldPC = 0;

		while(true) {		
			
			if(fetched == -2000) {
				if(ending)
					break;
				System.out.println("Clock Cycle: " + cycleCount);
				oldPC = (short) (CPU.getPC().getPCData()-1);
				System.out.println("PC: " + CPU.getPC().getPCData());
				if((decoded !=4) || (decoded != 7))
					ending = true;
				else {
				    fetched = 0;
				}
				execute();
				cycleCount++;
				continue;
			}
			
			System.out.println("Clock Cycle: " + cycleCount);
			oldPC = (short) (CPU.getPC().getPCData()-1);
			System.out.println("PC: " + CPU.getPC().getPCData());
			
			if(cycleCount == 1 || fetched == 0) {
				fetch();
				fetched = IF.get("instruction");
			} 
			else if(cycleCount == 2 || decoded == -1) {
				decode();
				decoded = ID.get("opcode");
				fetch();
				fetched = IF.get("instruction");
			} 
			else if((decoded ==4) || (decoded == 7)) {
				
				oldPC = CPU.getPC().getPCData();
				execute();
				if(oldPC != CPU.getPC().getPCData()) {
					fetched = 0;
					decoded = -1;
				} 
				else {
					decode();
					decoded = ID.get("opcode");
					fetch();
					fetched = IF.get("instruction");
				}
			} 
			else {
				execute();
				decode();
				decoded = ID.get("opcode");
				fetch();
				fetched = IF.get("instruction");
			}
			
			cycleCount++;
			
			System.out.println("-------------------");
		}
		
		// Print All Register And Memory Values
		System.out.println("_________________________________");

		System.out.println("Program Counter: " + CPU.getPC().getPCData());
		System.out.println("         CVNSZ");
		System.out.println("SREG: " + String.format("%8s", Integer.toBinaryString(CPU.getSREG().getData())).replace(" ", "0")
							+ " (" + CPU.getSREG().getData() + ")");
		
		
		System.out.println("Registers:");
		CPU.printRegisterArray(CPU.getRegisters());

		System.out.println("Memory:");
		System.out.println(Arrays.toString(CPU.getCPU().getMemory().getDataMemory()));
	}


	public static void main(String[] args) throws IOException {
//		CPU.getRegisters()[10] = new Register("R10", (byte)-128);
//		CPU.getRegisters()[11] = new Register("R11", (byte)-128);
		run("Code1.txt");
	}
}
