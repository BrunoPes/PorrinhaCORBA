import Porrinha.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import java.lang.Thread;

class ClientThread extends Thread {
	private Client clientCorba;
	private String clientName;
	private String serverName;
	private ClientUI clientUI;

	public ClientThread(String[] args) {
		this.clientCorba = new Client(args);
	}

	public Client getClientCorba() {
		return this.clientCorba;
	}

	public void configClientCorba(String client, String server, ClientUI userInterface) throws Exception {
		this.clientName = client;
		this.serverName = server;
		this.clientUI = userInterface;
		this.clientCorba.setServer(serverName);
		this.clientCorba.setClient(this.clientName);
		this.clientCorba.setClientUI(this.clientUI);
	}

	public void run() {
		try {
			this.clientCorba.startConnection();
			System.out.println("Rodando e operante");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

public class Client extends ClientPorrinhaPOA {
	private int myPicks = 3;
	private int lastGuess;
	private int maxPicksSum;
	private String myName;
	private ClientUI clientUI;

	private ORB orb;
	private POA rootPOA;
	private NamingContext namingService;
	private ServerPorrinha server;

	public Client(String[] args) {
		try {
			this.orb = ORB.init(args, null);
			org.omg.CORBA.Object poaPointer = this.orb.resolve_initial_references("RootPOA");
			org.omg.CORBA.Object namingPointer = this.orb.resolve_initial_references("NameService");
			this.rootPOA = POAHelper.narrow(poaPointer);
			this.namingService = NamingContextHelper.narrow(namingPointer);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Client(String clientName, String serverName, String[] args) throws Exception {
		this(args);
		this.setClientAndServer(clientName, serverName);
	}

	public ServerPorrinha getServer() {
		return this.server;
	}

	public void setClient(String clientName) throws Exception {
		this.myName = clientName.toLowerCase();
		org.omg.CORBA.Object clientPointer = this.rootPOA.servant_to_reference(this);
		this.namingService.rebind(new NameComponent[]{new NameComponent(this.myName, "")}, clientPointer);
	}

	public void setServer(String serverName) throws Exception {
		org.omg.CORBA.Object serverPointer = this.namingService.resolve(new NameComponent[]{new NameComponent(serverName, "")});
		this.server = ServerPorrinhaHelper.narrow(serverPointer);
		System.out.println("Client connected to server!");
	}

	public void setClientAndServer(String clientName, String serverName) throws Exception {
		this.setServer(serverName);
		this.setClient(clientName);
		this.startConnection();
	}

	public void setClientUI(ClientUI userInterface) {
		this.clientUI = userInterface;
	}

	public void startConnection() throws Exception {
		this.rootPOA.the_POAManager().activate();
		this.server.registerClient(this.myName);
		this.orb.run();
	}

	public int getLastGuess() {
		return this.lastGuess;
	}

	public int getMyPicks() {
		return this.myPicks;
	}

	public int getMaxPicks() {
		return this.maxPicksSum;
	}

	public String getName() {
		return this.myName;
	}

	public void setLastGuess(int guess) {
		this.lastGuess = guess;
	}

	public void setMyPicks(int picks) {
		this.myPicks = picks;
	}

	// CORBA Interface Protocol Methods
	public void waitForStart() {
		System.out.println("Wait start");
		this.clientUI.controlMessage(0, 0);
	}

	public void tellPlayersNames(String[] names, int length) {
		try {
			System.out.println("Names!!!  " + names[0] + "Length: " + length);
			int index = 0;
			for(int i=0; i < length; i++) {
				if(LabelsHelper.getName(names[i]).equals(this.myName)) index = i;
			}
			this.clientUI.updateGameLabels(names, "Palpite máximo: " + (length*3), index);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void tellNumberOfPicks() {
		System.out.println("Tell picks");
		this.clientUI.controlMessage(1, 0);
	}

	public void tellResultGuess(int maxSum) {
		this.maxPicksSum = maxSum;
		System.out.println("Tell Guess!");
		this.clientUI.controlMessage(2, maxSum);
	}

	public void roundFinished(int result, int maxSum, String[] playersPicks, String[] winners) {
		System.out.println("Finish! Result: " + result);
		this.maxPicksSum = maxSum;

		if(this.lastGuess == result && this.myPicks > 0) {
			this.myPicks--;
		}

		this.clientUI.roundFinished(result, maxSum, playersPicks, winners, (this.myPicks <= 0));
	}

	public static void main(String args[]) {
		try {
			String clientName = args[0];
			String serverName = args[1];
			new Client(clientName, serverName, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
