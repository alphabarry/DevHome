import java.io.*;
import java.net.Socket;
import java.util.*;
import gnu.io.*;
/**
 * Class declaration
 *
 *
 * @author
 * @version 1.8, 08/03/00
 */
public class LecteurRFID implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId;
    static Enumeration portList;
    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;
	private String message;
	private String ID=null;
	private int nbByte = 0;
	private byte[] trameRfid = new byte[11];
	private boolean recu = false ;

    /**
     * Method declaration
     *
     *
     * @param args
     *
     * @see
     */
    public static void main(String[] args) {
    boolean	portFound = false;
    String defaultPort = "/dev/ttyUSB0";

 	if (args.length > 0) {
	    defaultPort = args[0];
	} 
   
	portList = CommPortIdentifier.getPortIdentifiers();

	while (portList.hasMoreElements()) {
	    portId = (CommPortIdentifier) portList.nextElement();
	  
    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		if (portId.getName().equals(defaultPort)) {
		    System.out.println("Found port: "+defaultPort);
		    portFound = true;
		   LecteurRFID reader = new LecteurRFID();
		   LED l = new LED();
		 
		} 
	    } 
	} 
	if (!portFound) {
	    System.out.println("port " + defaultPort + " not found.");
	} 

	System.out.println("configuration du port reussi");
    } 

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public LecteurRFID() {
	try {
	    serialPort = (SerialPort) portId.open("/dev/ttyUSB0", 2000);
	} catch (PortInUseException e) {}

	try {
	    inputStream = serialPort.getInputStream();
	} catch (IOException e) {}

	try {
	    serialPort.addEventListener(this);
	} catch (TooManyListenersException e) {}

	serialPort.notifyOnDataAvailable(true);

	try {
	    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
					   SerialPort.STOPBITS_1, 
					   SerialPort.PARITY_NONE);
	} catch (UnsupportedCommOperationException e) {}

	readThread = new Thread(this);

	readThread.start();
	while (recu==false) ;
	AfficherTrame(trameRfid, nbByte);
	ExtraireId(trameRfid) ;
	AfficherId();
	String serverName = "192.168.12.176";
	int port = 1500;

	try {
		System.out.println("CONNECTE SUR L'ADRESSE IP :" + serverName+ " SUR LE PORT : "
				+ port);
		Socket client = new Socket(serverName, port);
		System.out.println("ACTUELLEMENT CONNECTE : "+ client.getRemoteSocketAddress());
		OutputStream outToServer = client.getOutputStream();
		//DataOutputStream out = new DataOutputStream(outToServer);
		System.out.println("Id = "+ID);
		PrintWriter fout = new PrintWriter(outToServer) ;
		fout.println(ID);
		//out.writeUTF("VALEUR DE L'ID : "+ID+ " RECU DEPUIS L'ADRESSE SUIVANTE : "+client.getLocalSocketAddress());
		fout.flush();
		InputStream inFromServer = client.getInputStream();	
		DataInputStream	in = new DataInputStream(inFromServer);
		System.out.println("REPONSE DU SERVEUR : " + in.readUTF());
		client.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	System.exit(0);
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void run() {
	try {
		while (recu==false ) {
			Thread.sleep(1000);
		}
		System.out.println("Trame reçue");
		
//	   AfficherTrame(trameRfid, nbByte);
//		System.exit(0) ;
	} catch (InterruptedException e) {}
    } 

    /**
     * Method declaration
     *
     *
     * @param event
     *
     * @see
     */
    public void serialEvent(SerialPortEvent event) {

	    try {
		while (inputStream.available() > 0) {
		    nbByte = inputStream.read(trameRfid);
		} 
		recu =true ;
		//System.out.print(new String(trameRfid));
	    } catch (IOException e) {}
 } 

	/**
	 * Cette methode permets de faire la conversion binaire en String.
	 * @param b
	 * @return une chaine.
	 */
	public String conversion(byte b) {
		String s = null;
		byte[] resultat = new byte[2];
		byte temp = b;
		temp = (byte) (temp >> 4);
		temp = (byte) (temp & 0x0F);
		if (temp <= 9)
			temp = (byte) (temp + 0x30);
		else
			temp = (byte) (temp + 0x37);
		resultat[0] = temp;
		temp = b;
		temp = (byte) (temp & 0x0F);
		if (temp <= 9)
			temp = (byte) (temp + 0x30);
		else
			temp = (byte) (temp + 0x37);
		resultat[1] = temp;
		s = new String(resultat, 0, 2);
		return s;
	}

	/**
	 * Affichage en hexadecimal.
	 * @param trame
	 * @param nbByte
	 */
	public void AfficherTrame(byte[] trame, int nbByte) {
		int i = 0;
		System.out.print("Affichage de la trame en hexadecimal : ");
		while (i < nbByte) {
			System.out.print(conversion(trame[i])+" ");
			i++;
		}
		System.out.println();
	}

	/**
	 * Extraction de l'ID du badge.
	 * @param trame
	 * @return l'ID du badge.
	 */
	public String ExtraireId(byte[] trame) {
		int i = 0;
		for (i = 3; i < 8;) {
			message = conversion(trame[i]);
			if (i == 3)
				ID = message;
			else {
				ID = ID + message;
				
			}
			i++;
		}
		return ID;
	}
	/**
	 * Affichage de l'ID du badege.
	 */
	public void AfficherId() {
		System.out.println();
		System.out.println("L'ID du badge est:"+ this.getID());
	}

	public String getID() {
		return ID;
	}

}

