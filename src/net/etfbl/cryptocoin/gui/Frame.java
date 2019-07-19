package net.etfbl.cryptocoin.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import net.etfbl.cryptocoin.blockchain.Block;
import net.etfbl.cryptocoin.blockchain.Blockchain;
import net.etfbl.cryptocoin.blockchain.Wallet;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Util;

import javax.swing.UIManager;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.encoders.Hex;

import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Component;

public class Frame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4747441225071636235L;

	private Wallet myWallet = new Wallet();
	private Wallet walletA = new Wallet();
	private Wallet walletB = new Wallet();

	private JPanel contentPane;
	private JTable table;
	private JTextField textFieldAdresaPrimaoca;
	private JTextField textFieldIznos;
	private JTextField textFieldFee;
	private JTextArea textAreaMojaAdresa;
	private JTextField textFieldMojeStanje;

	private CustomTableModel customTableModel = new CustomTableModel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame frame = new Frame();
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
	public Frame() {
		myWallet.register();
		walletA.register();
		walletB.register();

		Blockchain.init(myWallet.getPublicKey());
		
		System.out.println("myWallet: " + LevelDBHandler.getBalance(myWallet.getPublicKey()) + " " + Hex.toHexString(myWallet.getPublicKey().getEncoded()));
		System.out.println("secondWallet: "  + LevelDBHandler.getBalance(walletA.getPublicKey()) + " " + Hex.toHexString(walletA.getPublicKey().getEncoded()));
		System.out.println("thirdWallet: "   + LevelDBHandler.getBalance(walletB.getPublicKey()) + " " + Hex.toHexString(walletB.getPublicKey().getEncoded()));

		String formattedAddress = Hex.toHexString(myWallet.getPublicKey().getEncoded());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 820, 545);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedIndex() == 1) {
					ArrayList<Block> blocks = LevelDBHandler.getAllBlocks();
					customTableModel = new CustomTableModel();

					for (int i = 0; i < blocks.size(); i++)
						customTableModel.addElement(blocks.get(i));

					table.setModel(customTableModel);
					customTableModel.fireTableDataChanged();
				}
				System.out.println(LevelDBHandler.getBalance(myWallet.getPublicKey()));
			}
		});
		panel.add(tabbedPane);
		
		JPanel panelPosaljiSredstva = new JPanel();
		tabbedPane.addTab("Pošalji sredstva", null, panelPosaljiSredstva, null);
		tabbedPane.setEnabledAt(0, true);
		panelPosaljiSredstva.setLayout(null);
		
		JLabel labelAdresaPrimaoca = new JLabel("Adresa primaoca");
		labelAdresaPrimaoca.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelAdresaPrimaoca.setBounds(27, 70, 130, 22);
		panelPosaljiSredstva.add(labelAdresaPrimaoca);
		
		textFieldAdresaPrimaoca = new JTextField();
		textFieldAdresaPrimaoca.setFont(new Font("Courier New", Font.PLAIN, 13));
		textFieldAdresaPrimaoca.setColumns(10);
		textFieldAdresaPrimaoca.setBounds(204, 74, 559, 21);
		panelPosaljiSredstva.add(textFieldAdresaPrimaoca);
		
		JLabel labelIznos = new JLabel("Iznos");
		labelIznos.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelIznos.setBounds(118, 126, 43, 22);
		panelPosaljiSredstva.add(labelIznos);
		
		textFieldIznos = new JTextField();
		textFieldIznos.setFont(new Font("Courier New", Font.PLAIN, 13));
		textFieldIznos.setColumns(10);
		textFieldIznos.setBounds(208, 130, 102, 21);
		panelPosaljiSredstva.add(textFieldIznos);
		
		JLabel labelFee = new JLabel("Fee");
		labelFee.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelFee.setBounds(134, 174, 27, 22);
		panelPosaljiSredstva.add(labelFee);
		
		textFieldFee = new JTextField();
		textFieldFee.setColumns(10);
		textFieldFee.setBounds(208, 176, 102, 22);
		panelPosaljiSredstva.add(textFieldFee);
		
		JButton buttonPosalji = new JButton("Pošalji");
		buttonPosalji.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String pkHex = textFieldAdresaPrimaoca.getText();
				BigDecimal amount = new BigDecimal(textFieldIznos.getText());
				BigDecimal fee = new BigDecimal(textFieldFee.getText());

				PublicKey pk = Util.getPublicKeyFromBytes(DatatypeConverter.parseHexBinary(pkHex));
				Blockchain.sendFunds(myWallet, pk, amount, fee);

				textFieldMojeStanje.setText("" + LevelDBHandler.getBalance(myWallet.getPublicKey()));
				System.out.println(LevelDBHandler.getBalance(myWallet.getPublicKey()));
				System.out.println(LevelDBHandler.getBalance(walletA.getPublicKey()));
				System.out.println(LevelDBHandler.getBalance(walletB.getPublicKey()));
			}
		});
		buttonPosalji.setBounds(208, 230, 102, 25);
		panelPosaljiSredstva.add(buttonPosalji);
		
		JPanel panelBlockExplorer = new JPanel();
		tabbedPane.addTab("Block explorer", null, panelBlockExplorer, null);
		tabbedPane.setEnabledAt(1, true);
		panelBlockExplorer.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelBlockExplorer.add(scrollPane_1);
		
		table = new JTable();
		scrollPane_1.setViewportView(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{},
				{},
				{},
				{},
				{},
			},
			new String[] {
			}
		));
		table.setFillsViewportHeight(true);
		
		JPanel panelAdresaBalans = new JPanel();
		contentPane.add(panelAdresaBalans, BorderLayout.NORTH);
		panelAdresaBalans.setLayout(new BoxLayout(panelAdresaBalans, BoxLayout.Y_AXIS));
		
		JLabel lblMojaAdresa = new JLabel("Moja adresa");
		lblMojaAdresa.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMojaAdresa.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelAdresaBalans.add(lblMojaAdresa);
		
		textAreaMojaAdresa = new JTextArea(formattedAddress);
		textAreaMojaAdresa.setLineWrap(true);
		textAreaMojaAdresa.setFont(new Font("Courier New", Font.PLAIN, 12));
		panelAdresaBalans.add(textAreaMojaAdresa);
		textAreaMojaAdresa.setColumns(10);
		
		JLabel lblMojeStanje = new JLabel("Moje stanje");
		lblMojeStanje.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMojeStanje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMojeStanje.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelAdresaBalans.add(lblMojeStanje);
		
		textFieldMojeStanje = new JTextField("" + LevelDBHandler.getBalance(myWallet.getPublicKey()));
		textFieldMojeStanje.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldMojeStanje.setFont(new Font("Courier New", Font.PLAIN, 12));
		panelAdresaBalans.add(textFieldMojeStanje);
		textFieldMojeStanje.setColumns(10);
	}
}
