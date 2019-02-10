import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Linker {

	int[] relConstants; //array containing relocation constants for modules
	HashMap<String, Integer> vars = new HashMap<String, Integer>(); //hashmap for variables
	int location;

	public Linker(File input){
		location = 0;
		passOne(input); //calls first pass
		passTwo(input); //calls second pass
	}
	
	public void passOne(File input){ //first pass gets absolute constants and relocation constants
		System.out.println("\nPass One: ");
		try {
	        Scanner sc = new Scanner(input);
	        
	        String modules = sc.next(); //get number of modules
	        int mods = Integer.parseInt(modules);
	        this.relConstants = new int[mods];
	        System.out.printf("%d modules\n", mods);
	        
	        for(int i=0; i<mods; i++){
	        	passOneModule(sc, i);
	        }
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		
	}//end of passOne
	
	public void passOneModule(Scanner sc, int moduleNum){ //for reusability, implements pass one for one module only
		
		getVars(sc); 
        
        int uses = sc.nextInt(); //we dont need uses in first pass
        System.out.printf("skip %d uses\n", uses);
        for(int i=0; i<uses*2; i++){
        	System.out.printf("Skip %s\n", sc.next());
        }
        
        int change = sc.nextInt();
        //location += change;
        //just take the variable but apply it next tmime?
        this.relConstants[moduleNum] = location;
        System.out.printf("location:%d\n", location);
        System.out.printf("relconstant:%d\n", this.relConstants[moduleNum]);
        for(int i=0; i<change*2; i++){
        	System.out.printf("Skip %s\n", sc.next());
        }
        location+=change;
	}
	
	public void getVars(Scanner sc){ //add variables in this module to hash map
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
        location = relConstants[moduleNum];
        System.out.printf("location:%d\n", location);
        for(int i=0; i<parts; i++){ //goes through every input in the module
        	String def = sc.next(); //indication number
        	int refnum = sc.nextInt();
        	System.out.printf("letter: %s, num: %d\n", def,refnum);
        	switch(def){
        	case "R": reference(refnum);
        	case "E": external(refnum);
        	case "I": immediate(refnum);
        	case "A": absolute(refnum);
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
			newnum = refnum%1000;
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
