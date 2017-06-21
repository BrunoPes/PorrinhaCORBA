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
	private JLabel controlText = new JLabel("Entre em uma sala!");

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
        gameFields.setBounds(0,41,(3*width)/5, 250);

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
        this.picks.setMaximumSize(new Dimension(40, 16));
        this.guess.setMaximumSize(new Dimension(40, 16));
        this.putPicksBtn.setMaximumSize(new Dimension(85, 19));
        this.putPicksBtn.addMouseListener(this);
        this.putGuessBtn.setMaximumSize(new Dimension(85, 19));
        this.putGuessBtn.addMouseListener(this);
        JLabel gapBetween = new JLabel("");
        gapBetween.setMaximumSize(new Dimension(50, 10));
        JLabel gapBetween2 = new JLabel("");
        gapBetween2.setMaximumSize(new Dimension(50, 10));
        JLabel gap = new JLabel("");
        gap.setMaximumSize(new Dimension((3*width)/5, 15));

		Font newFont = (this.controlText.getFont()).deriveFont((float)20);
		this.controlText.setFont(newFont);
		this.controlText.setMaximumSize(new Dimension((3*width)/5, 50));
		this.controlText.setAlignmentX(Component.CENTER_ALIGNMENT);

		picksFields.add(new JLabel("  "));
        picksFields.add(new JLabel("Palitos    "));
        picksFields.add(this.picks);
        picksFields.add(gapBetween);
        picksFields.add(this.putPicksBtn);
		guessFields.add(new JLabel("  "));
        guessFields.add(new JLabel("Palpite    "));
        guessFields.add(this.guess);
        guessFields.add(gapBetween2);
        guessFields.add(this.putGuessBtn);
		gameFields.add(this.controlText);
        gameFields.add(picksFields);
        gameFields.add(guessFields);

        this.getContentPane().add(connectionFields);
        this.getContentPane().add(gameFields);
		this.setVisible(true);
	}

	private void connectToServer() {
		System.out.println("Connect to server");
		String client = this.userName.getText();
		String host = this.hostName.getText();
		if(client.length() > 0 && host.length() > 0) {
			host = host.replace(" ", "").toLowerCase();
			client = client.replace(" ", "");
			this.userName.setText(client);
			this.hostName.setText(host);

			try {
				this.clientCorba.setClientUI(this);
				this.clientCorba.setClientAndServer(client, host);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void controlMessage(int control) {
		switch(control) {
			case 0:
				this.controlText.setText("Esperando a partida começar...");
				break;
			case 1:
				this.controlText.setText("Escolha seus palitos!");
				break;
			case 2:
				this.controlText.setText("Dê o seu palpite!");
				break;
		}
		System.out.println("Controle cod: " + control);
		this.controlText.updateUI();
	}

	/** Communication interface methods **/
	public void tellNumberOfPicks(ServerPorrinha server, int myPicks) {
		try {
			int picksNum = Integer.valueOf(this.picks.getText());
			if(picksNum > myPicks) {
				this.controlText.setText("Você só tem " + myPicks + " palitos");
				this.controlText.updateUI();
			} else {
				server.putNumberOfPicks(this.userName.getText(), picksNum);
			}
		} catch(NumberFormatException e) {
			String msg = "Escreva apenas números inteiros de 0 a " + myPicks;
			JOptionPane.showMessageDialog(this, null, msg, JOptionPane.WARNING_MESSAGE);
		}
	}

	public void tellResultGuess(ServerPorrinha server, int maxPicks) {
		try {
			int lastGuess = Integer.valueOf(this.guess.getText());
			if(lastGuess > maxPicks) {
				this.controlText.setText("Maior palpite possível é: " + maxPicks);
			} else {
				this.clientCorba.setLastGuess(lastGuess);
				server.putResultGuess(this.clientCorba.getName(), lastGuess);
				this.controlText.setText("Aguardando Resultado...");
			}
			this.controlText.updateUI();
		} catch(NumberFormatException e) {
			String msg = "Escreva apenas números inteiros de 0 a " + maxPicks;
			JOptionPane.showMessageDialog(this, null, msg, JOptionPane.WARNING_MESSAGE);
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
				ServerPorrinha server = this.clientCorba.getServer();
				int picksArray[] = {
					this.clientCorba.getMyPicks(),
					this.clientCorba.getMaxPicks()
				};

				switch(button.getText()) {
					case "Conectar":
						this.connectToServer();
						break;
					case "Escolher":
						this.tellNumberOfPicks(server, picksArray[0]);
						break;
					case "Dar palpite":
						this.tellResultGuess(server, picksArray[1]);
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
    	// if(args.length >= 2) {
    	// 	try {
		// 		int width = Integer.valueOf(args[0]);
	    // 		int height = Integer.valueOf(args[1]);
		// 		new ClientUI(args, width, height);
    	// 	} catch(NumberFormatException e) {
    	// 		e.printStackTrace();
    	// 	}
    	// } else {
		// }
    	new ClientUI(args, 450, 300);
    }
}
