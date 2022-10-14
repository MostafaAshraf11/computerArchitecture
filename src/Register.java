
public class Register {
	
	private byte Data;
	private short PCData;// 16 bits
	private String Name;
	
	public Register(String Name) {
		this.Name = Name;
		if(Name == "PC")
			this.PCData = 0;
		else
			this.Data = 0;			
	}
	
	public Register(String Name, byte Data) {
		this.Name = Name;
		this.Data = Data;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public short getPCData() {
		return PCData;
	}

	public void setPCData(short pCData) {
		PCData = pCData;
	}

	public short getData() {
		return Data;
	}


	public void setData(byte data) {
		Data = data;
	}

}
