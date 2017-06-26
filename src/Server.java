import Porrinha.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import java.util.*;

public class Server extends ServerPorrinhaPOA {
	private Map<String, Integer> playedPicks = new HashMap<String, Integer>();
	private Map<String, Integer> guessedPicks = new HashMap<String, Integer>();
	private Map<String, Integer> clientsPicks = new HashMap<String, Integer>();
	private Map<String, Integer> idClients = new HashMap<String, Integer>();
	private Map<String, ClientPorrinha> clientsObjects = new HashMap<String, ClientPorrinha>();
	private int maxSum;
	private int round = 0;
	private int totalPlayers;
	private int playersToWin;
	private String theWinner = "";
	private NamingContext namingService;

	public Server(String serverName, int totalPlayers, String args[]) throws Exception {
		this.totalPlayers = totalPlayers;
		this.playersToWin = totalPlayers;
		serverName = serverName.replace(" ", "").toLowerCase();

		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object poaPointer = orb.resolve_initial_references("RootPOA");
		org.omg.CORBA.Object nameServicePointer = orb.resolve_initial_references("NameService");

		POA rootPOA = POAHelper.narrow(poaPointer);
		this.namingService = NamingContextHelper.narrow(nameServicePointer);

		org.omg.CORBA.Object objRef = rootPOA.servant_to_reference(this);
		this.namingService.rebind(new NameComponent[]{new NameComponent(serverName, "")}, objRef);

		rootPOA.the_POAManager().activate();
		System.out.println("Servidor " + serverName +" Lançado...");

		orb.run();
	}

	public void registerClient(String clientName) {
		try {
			NameComponent[] clientNameComp = {new NameComponent(clientName, "")};
			ClientPorrinha newClient = ClientPorrinhaHelper.narrow(this.namingService.resolve(clientNameComp));

			if(this.clientsObjects.putIfAbsent(clientName, newClient) == null) {
				System.out.println("Player " + clientName + " has joined the room");
				this.clientsPicks.put(clientName, new Integer(3));
				this.idClients.put(clientName, new Integer(this.idClients.size()));

				if(this.clientsObjects.size() == this.totalPlayers) {
					String[] names = new String[this.totalPlayers];
					this.maxSum = this.totalPlayers * 3;
					
					for(Map.Entry<String, Integer> entry : this.idClients.entrySet()) {
						names[entry.getValue()] = entry.getKey() + ": " + 3;
					}

					for(ClientPorrinha client : this.clientsObjects.values()) {
						client.tellPlayersNames(names, this.totalPlayers);
						client.tellNumberOfPicks();
					}
				} else {
					newClient.waitForStart();
				}
			} else {
				//Ja tinha esse cliente?
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void putNumberOfPicks(String clientName, int picks) {
		this.playedPicks.put(clientName, picks);

		if(this.playedPicks.size() == this.playersToWin) {
			for(Map.Entry<String, ClientPorrinha> entry : this.clientsObjects.entrySet()) {
				String playerName = entry.getKey();
				ClientPorrinha client = entry.getValue();

				if(this.clientsPicks.get(playerName) > 0)
					client.tellResultGuess(this.maxSum);
			}
		}
	}

	public void putResultGuess(String clientName, int guess) {
		this.guessedPicks.put(clientName, guess);

		if(this.guessedPicks.size() == this.playersToWin) {
			this.maxSum = 0;
			int totalSum = 0;
			String[] playersPicks = {"", "", "", ""};
			List<String> winners = new ArrayList<String>();

			for(int picks : this.playedPicks.values()) {
				totalSum += picks;
			}

			System.out.println("---------- Rodada " + this.round + " ----------");
			System.out.println("Total:   " + totalSum);
			for(Map.Entry<String, Integer> entry : this.guessedPicks.entrySet()) {
				int guessed = (int) entry.getValue();
				String playerName = (String) entry.getKey();

				/**PALPITE CORRETO**/
				if(guessed == totalSum) { 
					System.out.println(playerName + " ACERTOU!");
					Integer newPicks = ((int)this.clientsPicks.get(playerName)) - 1;
					this.clientsPicks.put(playerName, newPicks);
					winners.add(playerName);
					if(picks == 0) this.playersToWin--;
				}

				int picks = this.clientsPicks.get(playerName);
				this.maxSum += picks;
				playersPicks[this.idClients.get(playerName)] = playerName+": "+picks;
				System.out.println(playerName+": "+picks);
			}

			for(Map.Entry<String, Integer> entry : this.clientsPicks.entrySet()) {
				int picks = entry.getValue();
				String playerName = entry.getKey();

				if(picks == 0) playersPicks[this.idClients.get(playerName)] = playerName+": "+picks;
			}

			for(Map.Entry<String, ClientPorrinha> entry : this.clientsObjects.entrySet()) {
				String playerName = entry.getKey();
				ClientPorrinha client = entry.getValue();
				client.roundFinished(totalSum, this.maxSum, playersPicks, winners.toArray(new String[winners.size()]));
			}

			this.round++;
			this.playedPicks.clear();
			this.guessedPicks.clear();
		}
	}

	public static void main(String args[]) {
		try {
			String serverName = args[0];
			new Server(serverName, 4, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}