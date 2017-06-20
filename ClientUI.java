import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class ClientUI extends JFrame implements MouseListener, KeyListener, WindowListener {

	private JButton connectButton = new JButton("Conectar");
	private JTextField userName = new JTextField();
	private JTextField hostName = new JTextField();

	public ClientUI(int width, int height) {
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
        connectionFields.setBounds(0,0,width-6,40);
        
		JLabel lblName = new JLabel("Nome:");
		lblName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
		JLabel lblHost = new JLabel("Host:");
		lblHost.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        this.userName.setMaximumSize(new Dimension(90, 20));
        // this.hostName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
		this.hostName.setMaximumSize(new Dimension(90, 20));
		// this.hostName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        this.connectButton.setMaximumSize(new Dimension(90, 20));
        this.connectButton.addMouseListener(this);

        connectionFields.add(lblName);
        connectionFields.add(this.userName);
        connectionFields.add(lblHost);
        connectionFields.add(this.hostName);
        connectionFields.add(this.connectButton);

        this.getContentPane().add(connectionFields);
		this.setVisible(true);
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
            // if(e.getSource() instanceof JButton) {
            //     JButton button = (JButton)e.getSource();
           	// }
        } catch(Exception exc) {
        	exc.printStackTrace();
        }
    }

	public void windowClosing(WindowEvent e) {
		System.out.println("Closing Window");
        // if(!this.connect.isEnabled()) {
        //     System.out.println("Closing Window");
        //     this.client.closeConnection(this.id);
        // }
    }

    public void keyReleased(KeyEvent e) {}    
    public void keyTyped(KeyEvent e) {}

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    
	//Window Events
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
				new ClientUI(width, height);
    		} catch(NumberFormatException e) {
    			e.printStackTrace();
    		}
    	} else {
    		new ClientUI(450, 300);
    	}
    }
}