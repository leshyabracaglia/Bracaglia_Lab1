import java.util.HashMap;

public class Linker {

	int[] relConstants; //array containing relocation constants for modules
	HashMap<String, Integer> vars = new HashMap<String, Integer>(); //hashmap for variables
	HashMap<Integer, Use> useStorage = new HashMap<Integer, Use>();//stores usages
	int location;
	Instruction[][] list; //this list holds the whole shabang
	String errors = ""; //to return all errors at end

	public Linker(){
		location = 0;
	}
	
	//a helper method to print the instruction list to check it
	public void printList(){ 
		for(int i=0; i<list.length; i++){
			for(int j=0; j<list[i].length; j++){
				System.out.printf("Key: %s, Num: %d\n", list[i][j].getType(), list[i][j].getAddress());
			}System.out.println("\n");
		}
	}//end of printList
	
	public void passTwo(){
		System.out.println("\nPass Two: ");
        int mods = relConstants.length;
        for(int i=0; i<mods; i++){
        	passTwoModule(i);
        }printList();
        System.out.println(errors);
	}//end of passtwo
	
	public void passTwoModule(int moduleNum){ //for reusability, implements pass two for one module only
		
        int numInst = list[moduleNum].length; //num of instructions in th module
        for(int i=0; i<numInst; i++){ //goes through every input in the module
        	String def = list[moduleNum][i].getType(); //indication number
        	int refnum = list[moduleNum][i].getAddress();
        	System.out.printf("letter: %s, num: %d\n", def,refnum);
        	switch(def){
        	case "R": reference(refnum, i, moduleNum); break;
        	case "E": external(i, moduleNum, useStorage); break;
        	case "I": immediate(refnum); break;
        	case "A": absolute(refnum); break;
        	}
        }
        
	}
	
	public void reference(int refnum, int locationInModule, int moduleNum){
		int newnum = refnum+relConstants[moduleNum];
		list[moduleNum][locationInModule].setAddress(newnum);
	}
	
	public void external(int locationInModule, int moduleNum, HashMap<Integer, Use> uses){
		int newnum = 0; 
		int refnum = list[moduleNum][locationInModule].getAddress();
		int value;
		if(refnum%1000==777 && uses.containsKey((moduleNum*10)+locationInModule)){ //end of reference and use
			String variableUsed = uses.get((moduleNum*10)+locationInModule).var;
			System.out.printf("variable: %s", variableUsed);
			if(!vars.containsKey(variableUsed)){ //variable not defined
				errors+=("\nError: variable " + variableUsed + "used but not defined. Substituting value of 111");
				value = 111;
			}else{
				value = vars.get(uses.get((moduleNum*10)+locationInModule).var);
			}
			newnum=(refnum-(refnum%1000)+value);
			list[moduleNum][locationInModule].setAddress(newnum);
		}else if(uses.containsKey((moduleNum*10)+locationInModule)){ //a link and use
			link(locationInModule, moduleNum);
		}else{ //error, not a valid use
			newnum = (refnum-(refnum%1000)+111);
			System.out.println("Error: inproper reference");
		}
		
	}
	
	public void link(int locationInModule, int moduleNum){
		System.out.printf("calling link(%d, %d)\n", locationInModule, moduleNum);
		int change = vars.get(useStorage.get((moduleNum*10)+locationInModule).var);
		int refnum = list[moduleNum][locationInModule].getAddress();
		int link = refnum%1000;
		int newRef = refnum-link+change;
		System.out.printf("going to add var value:%d", change); 
		System.out.printf("Current mods address is: %d\n", refnum);
		System.out.printf("next destination is: %d\n", link);
		System.out.printf("New reference number: %d\n", newRef);
		if(link==777){
			list[moduleNum][locationInModule].setAddress(newRef);
		}else{
			System.out.printf("calling chain(%d,%d)\n", link, moduleNum);
			list[moduleNum][locationInModule].setAddress(newRef);
			chain(link, moduleNum, change);
		}
	}
	
	public void chain(int locationInModule, int moduleNum, int change){ //a link that is not explicit use
		int refnum = list[moduleNum][locationInModule].getAddress();
		int link = refnum%1000;
		int newRef = refnum-link+change;
		if(link==777){ //end of chain
			list[moduleNum][locationInModule].setAddress(newRef);
		}else{//another chain
			list[moduleNum][locationInModule].setAddress(newRef);
			System.out.printf("calling chain(%d,%d,%d)\n", link, moduleNum, change);
			chain(link, moduleNum, change);
		}
	}

	public int absolute(int refnum){
		System.out.printf("Returning %d\n", refnum);
		return refnum;
	}

	public int immediate(int refnum){
		System.out.printf("Returning %d\n", refnum);
		return refnum;
	}

}///end of Linker class
