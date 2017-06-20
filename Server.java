import Porrinha.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import java.util.HashMap;
import java.util.Map;

public class Server extends ServerPorrinhaPOA {
	private Map<String, Integer> playedPicks = new HashMap<String, Integer>();
	private Map<String, Integer> guessedPicks = new HashMap<String, Integer>();
	private Map<String, Integer> clientsPicks = new HashMap<String, Integer>();
	private Map<String, ClientPorrinha> clientsObjects = new HashMap<String, ClientPorrinha>();
	private int totalPlayers;
	private NamingContext namingService;

	public Server(String serverName, int totalPlayers, String args[]) throws Exception {
		this.totalPlayers = totalPlayers;
		serverName = serverName.replace(" ", "").toLowerCase();

		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object poaPointer = orb.resolve_initial_references("RootPOA");
		org.omg.CORBA.Object nameServicePointer = orb.resolve_initial_references("NameService");

		POA rootPOA = POAHelper.narrow(poaPointer);
		this.namingService = NamingContextHelper.narrow(nameServicePointer);

		org.omg.CORBA.Object objRef = rootPOA.servant_to_reference(this);
		this.namingService.rebind(new NameComponent[]{new NameComponent(serverName, "")}, objRef);

		rootPOA.the_POAManager().activate();
		System.out.println("Servidor Pronto ...");

		orb.run();
	}

	public void registerClient(String clientName) {
		try {
			NameComponent[] clientNameComp = {new NameComponent(clientName, "")};
			ClientPorrinha newClient = ClientPorrinhaHelper.narrow(this.namingService.resolve(clientNameComp));

			if(this.clientsObjects.putIfAbsent(clientName, newClient) == null) {
				System.out.println("Waiting for " + clientName + " answer");
				this.clientsPicks.put(clientName, new Integer(3));
				newClient.tellNumberOfPicks();
			} else {
				//Ja tinha esse client?
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void putNumberOfPicks(String clientName, int picks) {
		this.playedPicks.put(clientName, picks);

		if(this.playedPicks.size() == this.totalPlayers) {
			//Jogadora ja deram os palpites
			for(ClientPorrinha client : this.clientsObjects.values()) {
				client.tellResultGuess();
			}
		}
	}

	public void putResultGuess(String clientName, int guess) {
		this.guessedPicks.put(clientName, guess);

		if(this.guessedPicks.size() == this.totalPlayers) {
			int maxSum = 0;
			int totalSum = 0;
			// ArrayList<String> winners = new ArrayList<String>();

			for(int picks : this.playedPicks.values()) {
				totalSum += picks;
			}

			for(Map.Entry<String, Integer> entry : this.guessedPicks.entrySet()) {
				int guessed = (int) entry.getValue();
				String playerName = (String) entry.getKey();
				if(guessed == totalSum) {
					// winners.add(playerName);
					Integer newPicks = this.clientsPicks.get(playerName) - 1;
					this.clientsPicks.put(playerName, newPicks);
				}

				maxSum += this.clientsPicks.get(playerName);
			}

			for(Map.Entry<String, ClientPorrinha> entry : this.clientsObjects.entrySet()) {
				String playerName = entry.getKey();
				ClientPorrinha client = entry.getValue();
				client.roundFinished(totalSum, maxSum);
				// ClientPorrinha client = (ClientPorrinha)this.clientsObjects.get(playerName);
				// for(String winner : winners) {
				// 	if(playerName.equals(winner)) {
				// 	}
				// }
			}
		}
	}

	public static void main(String args[]) {
		try {
			String serverName = args[0];
			new Server(serverName, 2, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
