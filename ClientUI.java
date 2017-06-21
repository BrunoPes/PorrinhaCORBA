import Porrinha.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class ClientUI extends JFrame implements MouseListener, KeyListener, WindowListener {
	private Client clientCorba;

	private JButton putPicksBtn = new JButton("Escolher");
	private JButton putGuessBtn = new JButton("Dar palpite");
	private JButton connectButton = new JButton("Conectar");
	private JTextField userName = new JTextField();
	private JTextField hostName = new JTextField();
	private JTextField picks = new JTextField();
	private JTextField guess = new JTextField();

	public ClientUI(String[] args, int width, int height) {
		this.clientCorba = new Client(args);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, width, height);
        this.getContentPane().setLayout(null);
        this.setResizable(false);
        this.addWindowListener(this);

        JPanel connectionFields = new JPanel();
        connectionFields.setLayout(new BoxLayout(connectionFields, BoxLayout.X_AXIS));
        connectionFields.setAlignmentY(Component.CENTER_ALIGNMENT);
        connectionFields.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        connectionFields.setBackground(Color.WHITE);
        connectionFields.setBounds(0,0,width-1,40);

		JLabel lblName = new JLabel("Nome:");
		lblName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		JLabel lblHost = new JLabel("Host:");
		lblHost.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 5));
        this.userName.setMaximumSize(new Dimension(150, 20));
		this.hostName.setMaximumSize(new Dimension(150, 20));
        this.connectButton.setMaximumSize(new Dimension(85, 20));
        this.connectButton.addMouseListener(this);

        connectionFields.add(lblName);
        connectionFields.add(this.userName);
        connectionFields.add(lblHost);
        connectionFields.add(this.hostName);
		connectionFields.add(new JLabel("   "));
        connectionFields.add(this.connectButton);

        JPanel gameFields = new JPanel();
        gameFields.setLayout(new BoxLayout(gameFields, BoxLayout.Y_AXIS));
        gameFields.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameFields.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gameFields.setBackground(Color.WHITE);
        gameFields.setBounds((width/5),41,(3*width)/5, 229);


        JPanel picksFields = new JPanel();
        picksFields.setLayout(new BoxLayout(picksFields, BoxLayout.X_AXIS));
        picksFields.setBackground(Color.WHITE);
        picksFields.setBounds(0,0,(3*width)/5,  100);
        picksFields.setMaximumSize(new Dimension((3*width)/5, 30));
        JPanel guessFields = new JPanel();
        guessFields.setLayout(new BoxLayout(guessFields, BoxLayout.X_AXIS));
        guessFields.setBackground(Color.WHITE);
        guessFields.setBounds(0,200,(3*width)/5, 100);
        guessFields.setMaximumSize(new Dimension((3*width)/5, 30));
        this.picks.setMaximumSize(new Dimension(70, 16));
        this.guess.setMaximumSize(new Dimension(70, 16));
        this.putPicksBtn.setMaximumSize(new Dimension(100, 16));
        this.putPicksBtn.addMouseListener(this);
        this.putGuessBtn.setMaximumSize(new Dimension(100, 16));
        this.putGuessBtn.addMouseListener(this);
        JLabel gapBetween = new JLabel("");
        gapBetween.setMaximumSize(new Dimension(60, 10));
        JLabel gapBetween2 = new JLabel("");
        gapBetween2.setMaximumSize(new Dimension(60, 10));
        JLabel gap = new JLabel("");
        gap.setMaximumSize(new Dimension((3*width)/5, 15));

        picksFields.add(new JLabel("Palitos    "));
        picksFields.add(this.picks);
        picksFields.add(gapBetween);
        picksFields.add(this.putPicksBtn);
        guessFields.add(new JLabel("Palpite    "));
        guessFields.add(this.guess);
        guessFields.add(gapBetween2);
        guessFields.add(this.putGuessBtn);
        gameFields.add(picksFields);
        gameFields.add(guessFields);

        this.getContentPane().add(connectionFields);
        this.getContentPane().add(gameFields);
		this.setVisible(true);
	}


	/** Communication interface methods **/
	public void tellNumberOfPicks(ServerPorrinha server, int myPicks) {
		int picks = 0;
		try {
			System.out.print("Escolha o número de palitos: ");
			// picks = this.scanner.nextInt();
			while(picks > myPicks) {
				System.out.println("Você só tem " + myPicks + ".");
				System.out.print("Dê uma quantidade de palitos válida: ");
				// picks = this.scanner.nextInt();
			}

			if(picks <= myPicks) server.putNumberOfPicks(this.userName.getText(), picks);
		} catch(NumberFormatException e) {
			System.out.println("Escreva apenas números inteiros de 0 a " + myPicks);
		}
	}

	public void tellResultGuess(ServerPorrinha server, int maxPicksSum) {
		try {
			int lastGuess = this.clientCorba.getLastGuess();
			System.out.println("Dê seu palpite do resultado: ");
			// lastGuess = this.scanner.nextInt();
			while(lastGuess > maxPicksSum ) {
				System.out.println("A soma máxima de palitos é " + maxPicksSum);
				System.out.println("Dê outro palpite: ");
				// lastGuess = this.scanner.nextInt();
			}
			this.clientCorba.setLastGuess(lastGuess);
			server.putResultGuess(this.userName.getText(), lastGuess);
		} catch(NumberFormatException e) {
			System.out.println("Escreva apenas números inteiros de 0 a " + maxPicksSum);
		}
	}

	public void roundFinished(int result, int maxSum) {
		
	}

	/** Listeners **/
	public void windowClosing(WindowEvent e) {
		System.out.println("Closing Window");
        // if(!this.connect.isEnabled()) {
        //     System.out.println("Closing Window");
        //     this.client.closeConnection(this.id);
        // }
    }

	public void keyPressed(KeyEvent key) {
		try {
			int keyCode = key.getKeyCode();
			switch(keyCode) {
				case 0: break;
				default: break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    public void mousePressed(MouseEvent e) {
        try {
            if(e.getSource() instanceof JButton) {
                JButton button = (JButton)e.getSource();

				switch(button.getText()) {
					case "Conectar":
						System.out.println("Connect to server");
						String client = this.userName.getText();
						String host = this.hostName.getText();
						if(client.length() > 0 && host.length() > 0) {
							host = host.replace(" ", "").toLowerCase();
							client = client.replace(" ", "");
							this.userName.setText(client);
							this.hostName.setText(host);
							this.clientCorba.setClientAndServer(client, host);
						}
						break;
				}
			}
        } catch(Exception exc) {
        	exc.printStackTrace();
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

    public static void main(String args[]) {
    	if(args.length >= 2) {
    		try {
				int width = Integer.valueOf(args[0]);
	    		int height = Integer.valueOf(args[1]);
				new ClientUI(args, width, height);
    		} catch(NumberFormatException e) {
    			e.printStackTrace();
    		}
    	} else {
    		new ClientUI(args, 450, 300);
    	}
    }
}
