import Porrinha.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import java.util.Scanner;

public class Client extends ClientPorrinhaPOA {
	private int myPicks = 3;
	private int lastGuess;
	private int maxPicksSum;
	private String myName;
	// private Scanner scanner = new Scanner(System.in);
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

	public void setClient(String clientName) throws Exception {
		this.myName = clientName.toLowerCase();
		org.omg.CORBA.Object clientPointer = this.rootPOA.servant_to_reference(this);
		this.namingService.rebind(new NameComponent[]{new NameComponent(this.myName, "")}, clientPointer);
	}

	public void setServer(String serverName) throws Exception {
		org.omg.CORBA.Object serverPointer = this.namingService.resolve(new NameComponent[]{new NameComponent(serverName, "")});
		this.server = ServerPorrinhaHelper.narrow(serverPointer);
	}

	public void startConnection() throws Exception {
		this.rootPOA.the_POAManager().activate();
		System.out.println("Client connected to server!");
		this.server.registerClient(this.myName);
		this.orb.run();
	}

	public void setClientAndServer(String clientName, String serverName) throws Exception {
		this.setClient(clientName);
		this.setServer(serverName);
		this.startConnection();
	}

	public void tellNumberOfPicks() {
		this.clientUI.tellNumberOfPicks(this.server);
	}

	public void tellResultGuess() {
		this.clientUI.tellResultGuess(this.server);
	}

	public void roundFinished(int result, int maxSum) {
		this.clientUI.roundFinished(result, maxSum);
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
