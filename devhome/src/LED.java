
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LED  extends LecteurRFID implements Runnable{
	


	public  boolean fg = true;
	private boolean statut_led = false;
	private String Alarme;
	Thread thread;
	
	public String getAlarme() {
		return Alarme;
	}

	public void setAlarme(String alarme) {
		Alarme = alarme;
	}
	
	public LED() {
		super();
		  export();
		  directionLedRouge();
		  directionLedVerte();
		  valueLedRouge();
		  valueLedVerte();
		  run();
		  LedAlerte();
		  thAlarme();
		
	
	} 
	
	
	public void export(){
		FileOutputStream export = null;
		//creation des repertoires de controle des LEDs 
		File gpio64 = new File("/sys/class/gpio/gpio64");
		File gpio65 = new File("/sys/class/gpio/gpio65");
		
		try {
			//Creation du fichier 'exporte'
			export = new FileOutputStream("/sys/class/gpio/export");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Erreur ouverture export");
		}
		
		try{
			//Verification de l'existance des repertoires
			try {
				if(gpio64.exists()==false){
					//creer repertoir gpio64
					export.write("64".getBytes());
				}
				else{
					System.out.println("Le Repertoire64 Existe Deja");
				}
				if(gpio65.exists()==false){
					//creer repertoir gpio65
					export.write("65".getBytes());
				}
				else{
					System.out.println("Le Repertoire65 Existe Deja");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("echec ecriture export");
			}
		}
		 finally {
			try {
				//fermetur du fichier 'export'
				export.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		 }
	}
	
	public void directionLedRouge(){
		FileOutputStream direction = null;
		File gpio64_dir = new File("/sys/class/gpio/gpio64/direction");
		FileOutputStream value;
		
		 try {
			 //Creation du fichier d'allumage des LEDs
				direction = new FileOutputStream("/sys/class/gpio/gpio64/direction");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Erreur Ouverture Direction");
			}
			try{
				try {
					//ecriture sur le fichier correspondant a l'allumage des LEDs 
					value = new FileOutputStream("/sys/class/gpio/gpio64/value");
					direction.write("out".getBytes());
					value.write("1".getBytes());
					
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("echec Ecriture Direction");
				}
			
			}
			 finally {
				try {
					direction.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			 }
	
		 System.out.println("Fichier Direction64 deja initialiser");
		
	}
	
	public void directionLedVerte(){
		FileOutputStream direction1 = null;
		File gpio65_dir = new File("/sys/class/gpio/gpio65/direction");
		FileOutputStream value;
		
		 try {
			 //Creation du fichier d'allumage des LEDs
				direction1 = new FileOutputStream("/sys/class/gpio/gpio65/direction");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Erreur ouverture direction");
			}
			try{
				try {
					//ecriture sur le fichier correspondant a l'allumage des LEDs 
					value = new FileOutputStream("/sys/class/gpio/gpio65/value");
					direction1.write("out".getBytes());
					value.write("1".getBytes());
					
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("echec ecriture direction");
				}
			}
			 finally {
				try {
					direction1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			 }
	 
		 System.out.println("Fichier Direction65 deja initialiser");
	}
	
	
	public void valueLedRouge(){
		 FileOutputStream value = null;
		 try {
			 //Creation du fichier 'value'
			 value = new FileOutputStream("/sys/class/gpio/gpio64/value");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Erreur ouverture value");
			}
			try{
				try {
					statut_led = true;
					//Ecriture dans le fichier 
					//passage a 0
					value.write("0".getBytes());
					
					try {
						System.out.println("\t\t\t\t\t\t!! ROUGE !!");
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("Erreur thread time");
					}
					statut_led = false;

					//passage a 1
					value.write("1".getBytes());
					
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("echec ecriture value");
				}
			}
			 finally {
				try {
					value.write("1".getBytes());
					value.close();
					//System.out.println("fermeture value");
				} catch (IOException e) {
					e.printStackTrace();
				}			
			 }
	}
	
	public void valueLedVerte(){
		 FileOutputStream value1 = null;
		 try {
			 //Creation du fichier 'value'
			 value1 = new FileOutputStream("/sys/class/gpio/gpio65/value");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Erreur ouverture value");
			}
			try{
				try {
					statut_led = true;
					//Ecriture dans le fichier 
					//passage a 0
					value1.write("0".getBytes());
					
					try {
						System.out.println("\t\t\t\t\t\t!! VERT !!");
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("Erreur thread time");
					}
					statut_led = false;

					//passage a 1
					value1.write("1".getBytes());
					
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("echec ecriture value");
				}
			}
			 finally {
				try {
					value1.write("1".getBytes());
					value1.close();
					//System.out.println("fermeture value");
				} catch (IOException e) {
					e.printStackTrace();
				}			
			 }
	}

	public synchronized void run()
	{
		while(fg == true)
		{
			//boucle de clignottement de l'alarme
			System.out.println("\t   !! ALARME !!");
			LedAlerte();
		}
	}
		
	public void LedAlerte(){
		
		 FileOutputStream value = null;
		 //System.out.println(" Le code alarme :: -"+getAlarme()+"-");
		 try {
			 value = new FileOutputStream("/sys/class/gpio/gpio64/value");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Erreur ouverture value");
			}
			try{
					statut_led = true;
					try {
						//passage a 0
						value.write("0".getBytes());
						
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Erreur thread time");
						}

						//passage a 1
						value.write("1".getBytes());
						
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Erreur thread time");
						}
						
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("echec ecriture value");
					}
					statut_led = false;
			}
			 finally {
				try {
					value.write("1".getBytes());
					value.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			 }
			 
	}
	 
	public void thAlarme()
	{
		//en fonction de des caractere 'FA' ou 'AF' on change la valeur 
		//du FLAG fg afin d'allumer ou d'eteindre l'alarme
		if(getAlarme().charAt(0) == 'A' && getAlarme().charAt(1) == 'F')
		{
			thread = new Thread(this);
			fg = true;
			thread.start();
			//System.out.println("MON THREAD true ::: "+thread.currentThread().getName());
		}
		else if(getAlarme().charAt(0) == 'F' && getAlarme().charAt(1) == 'A')
		{
			fg = false;
			thread = null;
			//System.out.println("MON THREAD false ::: "+thread.currentThread().getName());
		}		
	}
	
	public boolean isStatut_led() {
		return statut_led;
	}

}
