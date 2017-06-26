import Porrinha.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class ClientUI extends JFrame implements MouseListener, KeyListener, WindowListener {
	private static final long serialVersionUID = 1L;
	private Client clientCorba;
	private ClientThread cliThread;

	private JButton putPicksBtn = new JButton("Escolher");
	private JButton putGuessBtn = new JButton("Palpite");
	private JButton connectButton = new JButton("Conectar");
	private JTextField userName = new JTextField();
	private JTextField hostName = new JTextField();
	private JTextField picks = new JTextField();
	private JTextField guess = new JTextField();
	private JLabel controlText = new JLabel("Entre em uma sala!");
	private JLabel player1 = new JLabel("Jogador 1: ");
	private JLabel player2 = new JLabel("Jogador 2: ");
	private JLabel player3 = new JLabel("Jogador 3: ");
	private JLabel player4 = new JLabel("Jogador 4: ");
	private JLabel[] playersLbls = {
		new JLabel("Jogador 1: "),
		new JLabel("Jogador 2: "),
		new JLabel("Jogador 3: "),
		new JLabel("Jogador 4: ")
	};
	private JLabel maxGuess = new JLabel("Palpite máximo: ");

	public ClientUI(String[] args, int width, int height) {
		this.cliThread = new ClientThread(args);
		this.clientCorba = this.cliThread.getClientCorba();

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
        gameFields.setBounds(0,41,(3*width)/5, 130);

        JPanel gameLabels = new JPanel();
        gameLabels.setLayout(new BoxLayout(gameLabels, BoxLayout.Y_AXIS));
        gameLabels.setBackground(Color.WHITE);
        gameLabels.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameLabels.setBounds((3*width)/5+2, 41, (2*width)/5-8, 130);
        gameLabels.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		for(int i=0; i<this.playersLbls.length; i++)
			gameLabels.add(this.playersLbls[i]);
        // gameLabels.add(this.player1);
        // gameLabels.add(this.player2);
        // gameLabels.add(this.player3);
        // gameLabels.add(this.player4);

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

		Font newFont = (this.controlText.getFont()).deriveFont((float)18);
		this.controlText.setFont(newFont);
		this.controlText.setMaximumSize(new Dimension((3*width)/5, 50));
		this.controlText.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.maxGuess.setMaximumSize(new Dimension(((3*width)/5)-20, 50));
        this.maxGuess.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        gameFields.add(this.maxGuess);

		this.setFieldsEnabled(1, false);
		this.setFieldsEnabled(2, false);

        this.getContentPane().add(connectionFields);
        this.getContentPane().add(gameFields);
        this.getContentPane().add(gameLabels);
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
				this.cliThread.configClientCorba(client, host, this);
				this.cliThread.start();
				this.setFieldsEnabled(0, false);
			} catch(org.omg.CosNaming.NamingContextPackage.NotFound notFound) {
				this.alertShow("Sala não encontrada", "A sala pesquisada não existe. Tente novamente.");
				this.setFieldsEnabled(0, true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setFieldsEnabled(int fieldsGroup, boolean enabled) {
		switch(fieldsGroup) {
		case 0:
			this.userName.setEnabled(enabled);
			this.hostName.setEnabled(enabled);
			this.connectButton.setEnabled(enabled);
			break;
		case 1:
			this.picks.setEnabled(enabled);
			this.putPicksBtn.setEnabled(enabled);
			if(enabled) {
				this.picks.setText("");
				this.guess.setText("");
			}
			break;
		case 2:
			this.guess.setEnabled(enabled);
			this.putGuessBtn.setEnabled(enabled);
			break;
		}
	}

	public void setMyLabelColor(int labelIndex, Color color) {
		System.out.println("Update Color from Label " + labelIndex);
		JLabel label = this.playersLbls[labelIndex];
		label.setBackground(color);
		label.setForeground(color);
		label.updateUI();
	}

	public void updateGameLabels(String[] playersLabels, String maxLabel, int indexLabel) {
		if(indexLabel >= 0) {
			this.setMyLabelColor(indexLabel, Color.RED.darker());
		}

		if(playersLabels.length == 4) {
			for(int i=0; i < this.playersLbls.length; i++) {
				this.playersLbls[i].setText(playersLabels[i]);
				this.playersLbls[i].updateUI();
			}
		}

		if(!maxLabel.equals("")) {
			this.maxGuess.setText(maxLabel);
			this.maxGuess.updateUI();
		}
	}

	public void alertShow(String title, String msg) {
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.WARNING_MESSAGE);
	}

	public void controlMessage(int control, int value) {
		switch(control) {
			case 0:
				this.controlText.setText("Espere a partida começar");
				break;
			case 1:
				this.controlText.setText("Escolha seus palitos!");
				this.setFieldsEnabled(1, true);
				break;
			case 2:
				this.updateGameLabels(new String[]{""}, "Palpite máximo: " + value, -1);
				this.controlText.setText("Dê seu palpite!");
				this.setFieldsEnabled(2, true);
				break;
		}
		this.controlText.updateUI();
	}

	/** Communication interface methods **/
	public void tellNumberOfPicks(ServerPorrinha server, int myPicks) {
		try {
			int picksNum = Integer.valueOf(this.picks.getText());
			if(picksNum > myPicks || picksNum < 0) {
				this.alertShow("Valor inválido", "Você só tem " + myPicks + " palitos");
				this.controlText.updateUI();
			} else {
				this.controlText.setText("Aguarde a sua vez!");
				this.setFieldsEnabled(1, false);
				server.putNumberOfPicks(this.userName.getText(), picksNum);
			}
		} catch(NumberFormatException e) {
			this.alertShow("Valor inválido", "Escreva apenas: Inteiros de 0 a " + myPicks);
		}
	}

	public void tellResultGuess(ServerPorrinha server, int maxPicks) {
		try {
			int lastGuess = Integer.valueOf(this.guess.getText());
			if(lastGuess > maxPicks || lastGuess < 0) {
				this.alertShow("Palpite inválido", "Maior palpite possível é: " + maxPicks);
			} else {
				this.controlText.setText("Aguardando Resultado");
				this.clientCorba.setLastGuess(lastGuess);
				this.setFieldsEnabled(2, false);
				server.putResultGuess(this.clientCorba.getName(), lastGuess);
			}
			this.controlText.updateUI();
		} catch(NumberFormatException e) {
			String msg = "Escreva apenas números inteiros de 0 a " + maxPicks;
			this.alertShow("Palpite Inválido", msg);
		}
	}

	public void roundFinished(int result, int maxSum, String[] playersPicks, String[] winners, boolean iDidWin) {
		this.updateGameLabels(playersPicks, "Palpite máximo: " + maxSum, -1);

		String msg = "Os vencedores dessa rodadam foram:";
		for(String winnerLabel : winners) {
			msg += "\n" + winnerLabel;
		}
		System.out.println(msg);
		// this.alertShow("Fim da Rodada", msg);

		if(iDidWin) {
			this.setFieldsEnabled(1, false);
			this.setFieldsEnabled(2, false);
		} else {
			this.controlMessage(1, 0);
		}
	}

	/** Listeners **/
	public void windowClosing(WindowEvent e) {
		//System.out.println("Closing Window");
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
					case "Palpite":
						this.tellResultGuess(server, picksArray[1]);
						break;
					default: break;
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
    	new ClientUI(args, 450, 200);
    }
}
