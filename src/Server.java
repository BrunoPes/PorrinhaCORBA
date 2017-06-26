import Porrinha.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import java.util.*;
import java.util.stream.Collectors;

public class Server extends ServerPorrinhaPOA {
	private Map<String, Integer> playedPicks = new HashMap<String, Integer>();
	private Map<String, Integer> guessedPicks = new HashMap<String, Integer>();
	private Map<String, Integer> clientsPicks = new HashMap<String, Integer>();
	private Map<String, Integer> idClients = new HashMap<String, Integer>();
	private Map<String, ClientPorrinha> clientsObjects = new HashMap<String, ClientPorrinha>();
	private int maxSum;
	private int totalPlayers;
	private int playersToWin;
	private int round = 0;
	private int turnPlayer = 0;
	private int lastPlayed = 0;
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

	public boolean isPlayerIngame(int playerID) {
		String name = this.getReserveMap(this.idClients).get(playerID);
		return this.clientsPicks.get(name) > 0;
	}

	public String nextPlayer(int curPlayer) {
		// System.out.println("Atual: " + curPlayer);
		int next = (curPlayer + 1) < this.totalPlayers ? (curPlayer + 1) : 0;

		while(!this.isPlayerIngame(next)) {
			if(++next > (this.totalPlayers - 1)) next = 0;
			// System.out.println("Next Step: " + next);
		}

		return this.getReserveMap(this.idClients).get(next);
	}

	public HashMap<Integer, String> getReserveMap(Map<String, Integer> map) {
		return (HashMap<Integer, String>) map.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	}

	// CORBA Interface Protocol Methods
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

		if(this.playedPicks.size() == this.playersToWin && this.playersToWin > 1) {
			while(!this.isPlayerIngame(this.turnPlayer)) {
				if(++this.turnPlayer > (this.totalPlayers - 1))
					this.turnPlayer = 0;
			}
			String name = this.getReserveMap(this.idClients).get(this.turnPlayer);
			this.lastPlayed = this.turnPlayer++;
			if(this.turnPlayer > (this.totalPlayers-1)) this.turnPlayer = 0;

			this.clientsObjects.get(name).tellResultGuess(this.maxSum);
		}
	}

	public void putResultGuess(String clientName, int guess) {
		System.out.println(clientName + " GUESS: " + guess);
		this.guessedPicks.put(clientName, guess);

		if(this.guessedPicks.size() < this.playersToWin) {
			String name = this.nextPlayer(this.lastPlayed);
			if(++this.lastPlayed > (this.totalPlayers-1)) this.lastPlayed = 0;
			this.clientsObjects.get(name).tellResultGuess(this.maxSum);
		} else {
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
					if(newPicks == 0) {
						this.playersToWin--;
						if(theWinner.equals("")) theWinner = playerName;
					}
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
