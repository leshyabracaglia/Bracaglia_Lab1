import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Linker {

	int[] relConstants; //array containing relocation constants for modules
	HashMap<String, Integer> vars = new HashMap<String, Integer>(); //hashmap for variables
	int location;
	Instruction[][] list; //this list holds the whole shabang

	public Linker(File input){
		location = 0;
		passOne(input); //calls first pass
	}
	
	public void passOne(File input){ 
		System.out.println("\nPass One: ");
		try {
	        Scanner sc = new Scanner(input);
	        
	        int mods = sc.nextInt();
	        this.relConstants = new int[mods];
	        list = new Instruction[mods][];
	        System.out.printf("%d modules\n", mods);
	        
	        for(int i=0; i<mods; i++){
	        	passOneModule(sc, i);
	        }
	    } 
	    catch (FileNotFoundException e) {
	    }
		printList();
		passTwo();
		
	}//end of passOne
	
	//implements pass one for one module only
	public void passOneModule(Scanner sc, int moduleNum){ 
		
		getVars(sc); //extracts variable definitions 
		int uses = sc.nextInt();
        System.out.printf("%d uses\n", uses);
        HashMap<Integer, String> storage = new HashMap<Integer, String>();//stores usages
        for(int i=0; i<uses; i++){ //puts uses into storage
        	String key = sc.next();
        	System.out.printf("Key: %s\n",key);
        	int num = sc.nextInt();
        	System.out.printf("num: %d\n",num);
        	storage.put(num, key);
        }
        int change = sc.nextInt();
        list[moduleNum] = new Instruction[change];
        this.relConstants[moduleNum] = location;
        System.out.printf("location:%d\n", location);
        System.out.printf("relconstant:%d\n", this.relConstants[moduleNum]);
        for(int i=0; i<change; i++){ //adds instructions to 2d list
        	String def = sc.next(); 
        	int refnum = sc.nextInt();
        	Instruction in = new Instruction(def,refnum); 
        	list[moduleNum][i] = in;
        }
        location+=change;
	}//end of passone module
	
	//a helper method to print the instruction list to check it
	public void printList(){ 
		for(int i=0; i<list.length; i++){
			for(int j=0; j<list[i].length; j++){
				System.out.printf("Key: %s, Num: %d\n", list[i][j].getType(), list[i][j].getAddress());
			}System.out.println("\n");
		}
	}//end of printList
	
	public void getVars(Scanner sc){ //Collects variables, comput
		int numvar = sc.nextInt();
        System.out.printf("%d variables\n", numvar);
        for(int i=0; i<numvar; i++){ //collect variables
        	String key = sc.next();
        	int loc = sc.nextInt() + location;
        	System.out.printf("(%s,%d)\n", key, loc);
        	vars.put(key, loc);
        }
	}//end of getVars
	
	public void passTwo(){
		System.out.println("\nPass Two: ");
        int mods = relConstants.length;
        for(int i=0; i<mods; i++){
        	passTwoModule(i);
        }
	}//end of passtwo
	
	public void passTwoModule(int moduleNum){ //for reusability, implements pass two for one module only
		
		int defs = sc.nextInt();
		location=0;
        for(int i=0; i<defs*2; i++){
        	System.out.printf("Skip %s\n", sc.next());
        }
        int uses = sc.nextInt();
        System.out.printf("%d uses\n", uses);
        HashMap<Integer, String> storage = new HashMap<Integer, String>();//stores usages
        for(int i=0; i<uses; i++){ //puts uses into storage
        	String key = sc.next();
        	System.out.printf("Key: %s\n",key);
        	int num = sc.nextInt();
        	System.out.printf("num: %d\n",num);
        	storage.put(num, key);
        }
        int parts = sc.nextInt();
        for(int i=0; i<parts; i++){ //goes through every input in the module
        	String def = sc.next(); //indication number
        	int refnum = sc.nextInt();
        	System.out.printf("letter: %s, num: %d\n", def,refnum);
        	switch(def){
        	case "R": reference(refnum, moduleNum); break;
        	case "E": external(i, moduleNum, storage); break;
        	case "I": immediate(refnum); break;
        	case "A": absolute(refnum); break;
        	}
        }
        
	}
	
	public int reference(int refnum, int moduleNum){
		int newnum = refnum+relConstants[moduleNum];
		System.out.printf("Returning %d\n", newnum);
		return newnum;
	}
	
	public int external(int locationInModule, int moduleNum, HashMap<Integer, String> uses){
		int newnum = 0; 
		int refnum = list[moduleNum][locationInModule].getAddress();
		if(refnum%1000==777 && uses.containsKey(locationInModule)){ //end of reference
			int value = vars.get(uses.get(locationInModule));
			newnum=(refnum-(refnum%1000)+value);
			System.out.printf("Returning %d\n", newnum);
			return newnum;
		}else if(uses.containsKey(locationInModule)){ //a link
			int change = vars.get(uses.get(locationInModule));
			int link = (refnum-(refnum%1000));
			link(link, moduleNum, change);
			newnum=(link+change);
			System.out.printf("Returning %d\n", newnum);
			return newnum;
		}else{ //error
			newnum = (refnum-(refnum%1000)+111);
			System.out.println("Error: inproper reference");
			return newnum;
		}
		
	}
	
	public int link(int destination, int moduleNum, int var){
		int refnum = list[moduleNum][destination].getAddress();
		int newnum;
		if(refnum%1000==777){
			newnum = (refnum-(refnum%1000)+var);
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
