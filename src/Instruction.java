
public class Instruction {
	//An object that is the pair of the letter instruction (A<R<I<E) and its address

	private String type;
	private int address;
	
	public Instruction(String letter, int num){
		type = letter;
		address = num;
	}
	
	public int getAddress(){
		return address;
	}
	
	public void setAddress(int inadd){
		address = inadd;
	}
	
	public String getType(){
		return type;
	}
	
}
