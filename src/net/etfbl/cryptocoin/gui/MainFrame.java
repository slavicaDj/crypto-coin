package net.etfbl.cryptocoin.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Blockchain;
import net.etfbl.cryptocoin.blockchain.Wallet;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Util;

import java.awt.Panel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.UIManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.PublicKey;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 781324045937335159L;

	private Wallet myWallet = new Wallet();
	private Wallet secondWallet = new Wallet();
	private Wallet thirdWallet = new Wallet();

	private JPanel contentPane;
	private JTextField textFieldAdresaPrimaoca;
	private JTextField textFieldIznos;
	private JTextField textFieldFee;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		myWallet.register();
		secondWallet.register();
		thirdWallet.register();

		Blockchain.init(myWallet.getPublicKey());
		
		System.out.println("myWallet: " + Hex.toHexString(myWallet.getPublicKey().getEncoded()));
		System.out.println("secondWallet: " + Hex.toHexString(secondWallet.getPublicKey().getEncoded()));
		System.out.println("thirdWallet: " + Hex.toHexString(thirdWallet.getPublicKey().getEncoded()));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1132, 691);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Panel mainPanel = new Panel();
		mainPanel.setBackground(UIManager.getColor("text"));
		mainPanel.setBounds(0, 0, 1119, 644);
		contentPane.add(mainPanel);
		mainPanel.setLayout(null);
		
		Panel sideMenuPanel = new Panel();
		sideMenuPanel.setBounds(0, 0, 237, 644);
		mainPanel.add(sideMenuPanel);
		sideMenuPanel.setBackground(new Color(72, 61, 139));
		sideMenuPanel.setForeground(Color.WHITE);
		sideMenuPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Meni");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBackground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(38, 27, 126, 49);
		sideMenuPanel.add(lblNewLabel);
		
		Panel panelPosaljiSredstva = new Panel();
		panelPosaljiSredstva.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panelPosaljiSredstva.setBackground(new Color(200, 193, 244));
				//72 61 139
			}
		});
		panelPosaljiSredstva.setBounds(0, 179, 237, 49);
		sideMenuPanel.add(panelPosaljiSredstva);
		panelPosaljiSredstva.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Pošalji sredstva");
		lblNewLabel_1.setForeground(new Color(255, 250, 240));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(45, 0, 129, 49);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panelPosaljiSredstva.add(lblNewLabel_1);
		
		Panel panelIstorijaTransakcija = new Panel();
		panelIstorijaTransakcija.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panelIstorijaTransakcija.setBackground(new Color(200, 193, 244));
			}
		});
		panelIstorijaTransakcija.setBounds(0, 234, 237, 49);
		sideMenuPanel.add(panelIstorijaTransakcija);
		panelIstorijaTransakcija.setLayout(null);
		
		JLabel lblTransactionHistory = new JLabel("Istorija transakcija");
		lblTransactionHistory.setBounds(32, 0, 177, 49);
		lblTransactionHistory.setHorizontalAlignment(SwingConstants.CENTER);
		lblTransactionHistory.setForeground(new Color(255, 250, 240));
		lblTransactionHistory.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panelIstorijaTransakcija.add(lblTransactionHistory);
		
		Panel panelMojNovcanik = new Panel();
		panelMojNovcanik.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				panelMojNovcanik.setBackground(new Color(200, 193, 244));
			}
		});
		panelMojNovcanik.setLayout(null);
		panelMojNovcanik.setBounds(0, 124, 237, 49);
		sideMenuPanel.add(panelMojNovcanik);
		
		JLabel lblMyWallet = new JLabel("Moj novčanik");
		lblMyWallet.setBackground(Color.YELLOW);
		lblMyWallet.setHorizontalAlignment(SwingConstants.CENTER);
		lblMyWallet.setForeground(new Color(255, 255, 255));
		lblMyWallet.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblMyWallet.setBounds(40, 0, 120, 49);
		panelMojNovcanik.add(lblMyWallet);
		
		JLabel lblEtfbl = new JLabel("ETFBL 2019");
		lblEtfbl.setForeground(new Color(255, 255, 204));
		lblEtfbl.setBounds(142, 615, 83, 16);
		sideMenuPanel.add(lblEtfbl);
		
		Panel mojNovcanikPanel = new Panel();
		mojNovcanikPanel.setBounds(238, 0, 883, 644);
		mainPanel.add(mojNovcanikPanel);
		mojNovcanikPanel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Adresa");
		lblNewLabel_2.setBounds(445, 97, 56, 16);
		mojNovcanikPanel.add(lblNewLabel_2);
		
		JLabel lbl_newLabel3 = new JLabel("Stanje");
		lbl_newLabel3.setBounds(445, 206, 56, 16);
		mojNovcanikPanel.add(lbl_newLabel3);
		
		JLabel labelStanje = new JLabel("" + LevelDBHandler.getBalance(myWallet.getPublicKey()));
		labelStanje.setBounds(194, 172, 534, 38);
		labelStanje.setHorizontalAlignment(SwingConstants.CENTER);
		labelStanje.setFont(new Font("Courier New", Font.PLAIN, 18));
		mojNovcanikPanel.add(labelStanje);

		String formatedAddress = Hex.toHexString(myWallet.getPublicKey().getEncoded()).substring(0, 75) + "\n" +
								 Hex.toHexString(myWallet.getPublicKey().getEncoded()).substring(75, 150);
		JTextArea textArea = new JTextArea(formatedAddress);
		textArea.setBounds(12, 38, 900, 46);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Courier New", Font.PLAIN, 18));
		textArea.setEditable(false);
		mojNovcanikPanel.add(textArea);
		
		JLabel lblUnesitePodatke = new JLabel("Unesite podatke");
		lblUnesitePodatke.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblUnesitePodatke.setBounds(72, 308, 218, 46);
		mojNovcanikPanel.add(lblUnesitePodatke);
		
		JLabel lblAdresaPrimaoca = new JLabel("Adresa primaoca");
		lblAdresaPrimaoca.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblAdresaPrimaoca.setBounds(73, 379, 144, 27);
		mojNovcanikPanel.add(lblAdresaPrimaoca);
		
		JLabel lblIznos = new JLabel("Iznos");
		lblIznos.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblIznos.setBounds(72, 442, 56, 16);
		mojNovcanikPanel.add(lblIznos);
		
		textFieldAdresaPrimaoca = new JTextField();
		textFieldAdresaPrimaoca.setFont(new Font("Courier New", Font.PLAIN, 13));
		textFieldAdresaPrimaoca.setBounds(255, 383, 592, 22);
		mojNovcanikPanel.add(textFieldAdresaPrimaoca);
		textFieldAdresaPrimaoca.setColumns(10);
		
		textFieldIznos = new JTextField();
		textFieldIznos.setFont(new Font("Courier New", Font.PLAIN, 13));
		textFieldIznos.setBounds(255, 441, 116, 22);
		mojNovcanikPanel.add(textFieldIznos);
		textFieldIznos.setColumns(10);
		
		JButton btnPosalji = new JButton("Pošalji");
		btnPosalji.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String pkHex = textFieldAdresaPrimaoca.getText();
				double amount = Double.valueOf(textFieldIznos.getText());
				double fee = Double.valueOf(textFieldFee.getText());

				PublicKey pk = Util.getPublicKeyFromBytes(DatatypeConverter.parseHexBinary(pkHex));
				Blockchain.sendFunds(myWallet, pk, amount, fee);

				labelStanje.setText("" + LevelDBHandler.getBalance(myWallet.getPublicKey()));
				System.out.println(LevelDBHandler.getBalance(myWallet.getPublicKey()));
				System.out.println(LevelDBHandler.getBalance(secondWallet.getPublicKey()));
				System.out.println(LevelDBHandler.getBalance(thirdWallet.getPublicKey()));
			}
		});
		btnPosalji.setBounds(256, 550, 97, 25);
		mojNovcanikPanel.add(btnPosalji);
		
		JLabel lblFee = new JLabel("Fee");
		lblFee.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblFee.setBounds(72, 496, 56, 16);
		mojNovcanikPanel.add(lblFee);
		
		textFieldFee = new JTextField();
		textFieldFee.setBounds(255, 495, 116, 22);
		mojNovcanikPanel.add(textFieldFee);
		textFieldFee.setColumns(10);
	}
}
