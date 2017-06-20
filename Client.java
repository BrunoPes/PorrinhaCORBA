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
	private Scanner scanner = new Scanner(System.in);
	
	private NamingContext namingService;
	private ServerPorrinha server;

	public Client(String clientName, String serverName, String[] args) throws Exception {
		this.myName = clientName;
		
		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object poaPointer = orb.resolve_initial_references("RootPOA");
		org.omg.CORBA.Object namingPointer = orb.resolve_initial_references("NameService");

		POA rootPOA = POAHelper.narrow(poaPointer);
		this.namingService = NamingContextHelper.narrow(namingPointer);
		org.omg.CORBA.Object serverPointer = this.namingService.resolve(new NameComponent[]{new NameComponent(serverName, "")});
		this.server = ServerPorrinhaHelper.narrow(serverPointer);

		org.omg.CORBA.Object clientPointer = rootPOA.servant_to_reference(this);
		this.namingService.rebind(new NameComponent[]{new NameComponent(clientName, "")}, clientPointer);
		
		rootPOA.the_POAManager().activate();
		System.out.println("Client connected to server!");

		this.server.registerClient(this.myName);
		orb.run();
	}

	public void tellNumberOfPicks() {

		int picks;
		try {
			System.out.print("Escolha o número de palitos: ");
			picks = this.scanner.nextInt();
			while(picks > this.myPicks) {
				System.out.println("Você só tem " + this.myPicks + ".");
				System.out.print("Dê uma quantidade de palitos válida: ");
				picks = this.scanner.nextInt();
			}

			if(picks <= this.myPicks) this.server.putNumberOfPicks(this.myName, picks);
		} catch(NumberFormatException e) {
			System.out.println("Escreva apenas números inteiros de 0 a " + this.myPicks);
		}
	}

	public void tellResultGuess() {
		try {
			System.out.println("Dê seu palpite do resultado: ");
			this.lastGuess = this.scanner.nextInt();
			while(this.lastGuess > this.maxPicksSum ) {
				System.out.println("A soma máxima de palitos é " + this.maxPicksSum);
				System.out.println("Dê outro palpite: ");
				this.lastGuess = this.scanner.nextInt();
			}
			this.server.putResultGuess(this.myName, this.lastGuess);
		} catch(NumberFormatException e) {
			System.out.println("Escreva apenas números inteiros de 0 a " + this.maxPicksSum);
		}
	}

	public void roundFinished(int result, int maxSum) {
		this.maxPicksSum = maxSum;
		if(this.lastGuess == result) this.myPicks--;
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
