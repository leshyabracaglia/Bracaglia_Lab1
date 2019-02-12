import java.util.Scanner;

public class runner {

	public static void main(String[] args) {

		Linker linker = new Linker();
	
	        Scanner sc = new Scanner(System.in);
	        
	        System.out.println("Please enter the input below:\n");
	        int mods = sc.nextInt(); //reads mods
	        linker.relConstants = new int[mods];
	        linker.list = new Instruction[mods][];
	        for(int i=0; i<mods; i++){
	        	int numvar = sc.nextInt();
	            for(int j=0; j<numvar; j++){ //collect variables
	            	String key = sc.next();
	            	if(linker.vars.containsKey(key)){//multiply defined
	            		int loc = sc.nextInt() + linker.location;
	            		linker.vars.put(key, loc);
	            		linker.errors+=("\nError: variable " + key + " multiply defined. Using most recent value of " + linker.vars.get(key));
	            	}else{
	            		int loc = sc.nextInt() + linker.location;
	            		linker.vars.put(key, loc);
	            	}
	            }
	            int numUses = sc.nextInt();
	            for(int j=0; j<numUses; j++){ //puts uses into storage
	            	String key = sc.next();
	            	int num = sc.nextInt();
	            	int translated = (i*10)+num;
	            	Use newUse = new Use(num, key, i);
	            	linker.useStorage.put(translated, newUse);
	            }
	            int relConst = sc.nextInt(); //relocation number
	            linker.relConstants[i] = linker.location;
	            linker.list[i] = new Instruction[relConst];
	            for(int j=0; j<relConst; j++){ //adds instructions to 2d list
	            	String def = sc.next(); 
	            	int refnum = sc.nextInt();
	            	Instruction in = new Instruction(def,refnum); 
	            	linker.list[i][j] = in;
	            }
	            linker.location+=relConst; //set location to new after passed through
	            
	            
	        }//end of mod loop
	        sc.close();
	        linker.passTwo();
	        linker.printList();
	   
	    
	    
	}

}
