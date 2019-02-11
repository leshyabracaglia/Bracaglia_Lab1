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
		passTwo(input); //calls second pass
	}
	
	//******************************************************************************
	//The first pass collects and stores number of modules, definitions, relocations constants, 
	public void passOne(File input){ 
		System.out.println("\nPass One: ");
		try {
	        Scanner sc = new Scanner(input);
	        
	        String modules = sc.next(); 
	        int mods = Integer.parseInt(modules);
	        this.relConstants = new int[mods];
	        list = new Instruction[mods][];
	        System.out.printf("%d modules\n", mods);
	        
	        for(int i=0; i<mods; i++){
	        	passOneModule(sc, i);
	        }
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		printList();
		
	}//end of passOne
	
	public void passOneModule(Scanner sc, int moduleNum){ //for reusability, implements pass one for one module only
		
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
	}
	
	//**************************************************************
	//Helper method to print the 2d list of instructions
	public void printList(){
		for(int i=0; i<list.length; i++){
			for(int j=0; j<list[i].length; j++){
				System.out.printf("Key: %s, Num: %d\n", list[i][j].getType(), list[i][j].getAddress());
			}System.out.println("\n");
		}
	}//end of printList
	
	//****************************************************************
	//Collects variables definitions in a module, computes absolute address, stores in vars hashmap
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
	
	public void passTwo(File input){
		System.out.println("\nPass Two: ");
		try {
			Scanner sc = new Scanner(input);
	        String modules = sc.next(); //get number of modules
	        int mods = Integer.parseInt(modules);
	        System.out.printf("%d modules\n", mods);
	        
	        for(int i=0; i<mods; i++){
	        	passTwoModule(sc, i);
	        }
	        int defs = sc.nextInt();
	        for(int i=0; i<defs*2; i++){
	        	System.out.printf("Skip %s\n", sc.next());
	        }
	        int uses = sc.nextInt();
	        System.out.printf("%d uses\n", uses);
	        
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}//end of passtwo
	
	public void passTwoModule(Scanner sc, int moduleNum){ //for reusability, implements pass two for one module only
		
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
        	case "R": reference(refnum); break;
        	case "E": external(refnum); break;
        	case "I": immediate(refnum); break;
        	case "A": absolute(refnum); break;
        	}
        }
        
	}
	
	public int reference(int refnum){
		int newnum = refnum+location;
		System.out.printf("Returning %d\n", newnum);
		return newnum;
	}
	
	public int external(int refnum){
		int newnum = 0;
		if(refnum%1000==777){ //end of reference
			//search for definition
			if(vars.containsKey(key))
		}
		return newnum;
	}

	public int absolute(int refnum){
		System.out.printf("Returning %d\n", refnum);
		return refnum;
	}

	public int immediate(int refnum){
		return 0;
	}

}///end of Linker class
