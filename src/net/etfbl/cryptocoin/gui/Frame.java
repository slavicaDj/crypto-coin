package net.etfbl.cryptocoin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Block;
import net.etfbl.cryptocoin.blockchain.Blockchain;
import net.etfbl.cryptocoin.blockchain.Transaction;
import net.etfbl.cryptocoin.blockchain.Wallet;
import net.etfbl.cryptocoin.exception.TransactionException;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Consts;
import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Frame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4747441225071636235L;

	private Wallet myWallet = new Wallet();

	private JPanel contentPane;
	private JTable tableBlocks;
	private JTable tableTxs;
	private JTextArea textPaneAdresaPrimaoca;
	private JTextField textFieldIznos;
	private JTextField textFieldFee;
	private JTextPane textPaneMojaAdresa;
	private JTextPane textPaneMojeStanje;
	private JLabel labelGreska;

	private TableModelBlocks tableModelBlocks = new TableModelBlocks();
	private TableModelTxs tableModelTxs = new TableModelTxs();

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
		if (!doesWalletExist()) {
			char[] passphrase = showModalDialog("Registracija", "Unesite lozinku", "");

			while (true) {
				if (passphrase == null)
					System.exit(0);

				if (passphrase != null && passphrase[0] != 0) {
					myWallet.register(passphrase, Consts.MY_WALLET_PATH + Consts.WALLET_FILE_NAME);
					Blockchain.init(myWallet.getPublicKey());
					break;
				}
				else
					passphrase = showModalDialog("Registracija", "Unesite lozinku", "Prazan unos!");
			}
		}
		else {
			char[] passphrase = showModalDialog("Prijava", "Unesite lozinku", "");

			while (true) {
				if (passphrase == null)
					System.exit(0);

				if (passphrase != null && myWallet.login(passphrase))
					break;
				else
					passphrase = showModalDialog("Prijava", "Unesite lozinku", "Pogrešna lozinka!");
			}
		}

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

		UIManager.put("TabbedPane.selected", Color.LIGHT_GRAY);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setFont(new Font("Calibri", Font.PLAIN, 18));
		tabbedPane.setBackground(Color.white);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = tabbedPane.getSelectedIndex();

				if (selectedIndex == 1) {
					ArrayList<Block> blocks = LevelDBHandler.getAllBlocks();
					tableModelBlocks = new TableModelBlocks();

					for (int i = 0; i < blocks.size(); i++)
						tableModelBlocks.addElement(blocks.get(i));

					tableBlocks.setModel(tableModelBlocks);
					tableBlocks.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
					tableBlocks.getColumnModel().getColumn(0).setPreferredWidth(430);
					tableBlocks.getColumnModel().getColumn(1).setPreferredWidth(430);
					tableBlocks.getColumnModel().getColumn(2).setPreferredWidth(1);
					tableBlocks.getColumnModel().getColumn(3).setPreferredWidth(440);
					tableModelBlocks.fireTableDataChanged();
				}
			}
		});
		panel.add(tabbedPane);
		
		JPanel panelPosaljiSredstva = new JPanel();
		panelPosaljiSredstva.setBackground(Color.WHITE);
		panelPosaljiSredstva.setLayout(null);
		
		JLabel labelAdresaPrimaoca = new JLabel("Adresa primaoca");
		labelAdresaPrimaoca.setBounds(54, 52, 130, 22);
		labelAdresaPrimaoca.setFont(new Font("Calibri", Font.PLAIN, 18));
		panelPosaljiSredstva.add(labelAdresaPrimaoca);
		
		JLabel labelIznos = new JLabel("Iznos");
		labelIznos.setBounds(149, 154, 43, 22);
		labelIznos.setFont(new Font("Calibri", Font.PLAIN, 18));
		panelPosaljiSredstva.add(labelIznos);
		
		textFieldIznos = new JTextField();
		textFieldIznos.setBounds(240, 155, 102, 21);
		textFieldIznos.setBackground(new Color(255, 255, 255));
		textFieldIznos.setFont(new Font("Courier New", Font.PLAIN, 15));
		textFieldIznos.setColumns(10);
		panelPosaljiSredstva.add(textFieldIznos);
		
		JLabel labelFee = new JLabel("Fee");
		labelFee.setBounds(157, 200, 27, 22);
		labelFee.setFont(new Font("Calibri", Font.PLAIN, 18));
		panelPosaljiSredstva.add(labelFee);
		
		textFieldFee = new JTextField();
		textFieldFee.setFont(new Font("Courier New", Font.PLAIN, 15));
		textFieldFee.setBounds(240, 199, 102, 22);
		textFieldFee.setBackground(new Color(255, 255, 255));
		textFieldFee.setColumns(10);
		panelPosaljiSredstva.add(textFieldFee);
		
		JButton buttonPosalji = new JButton("Pošalji");
		buttonPosalji.setBounds(240, 246, 102, 25);
		buttonPosalji.setFont(new Font("Calibri", Font.PLAIN, 16));
		buttonPosalji.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				BigDecimal amount = null;
				BigDecimal fee = null;
				char[] passphrase = null;
				boolean positive = false;
				String pkHex = textPaneAdresaPrimaoca.getText();
				String amountString = textFieldIznos.getText().trim().replace(",", ".");
				String feeString = textFieldFee.getText().trim().replace(",", ".");

				if (Util.isDecimal(amountString) && Util.isDecimal(feeString)) {
					amount = new BigDecimal(amountString);
					fee = new BigDecimal(feeString);
					BigDecimal zero = new BigDecimal(0);
	
					positive = (amount.compareTo(zero) > 0)  &&
									   (fee.compareTo(zero) > 0);
				}

				if (pkHex.matches(Consts.ADDRESS_REGEX) && positive) {

					passphrase = showModalDialog("Potvrda transakcije", "Unesite lozinku", "");

					while (true) {
						if (passphrase == null)
							return;

						if (passphrase != null && myWallet.login(passphrase)) {
							//send funds
							PublicKey pk = Crypto.getPublicKeyFromBytes(DatatypeConverter.parseHexBinary(pkHex));
							try {
							Blockchain.sendFunds(myWallet, passphrase, pk,
												 amount, fee);
							}
							catch (TransactionException e) {
								e.printStackTrace();
								labelGreska.setText(e.getMessage());
								return;
							}

							//cleanup
							textPaneMojeStanje.setText("" + LevelDBHandler.getBalance(myWallet.getPublicKey()));
							labelGreska.setText("");
							textPaneAdresaPrimaoca.setText("");
							textFieldIznos.setText("");
							textFieldFee.setText("");
							break;
						}
						else
							passphrase = showModalDialog("Potvrda transakcije", "Unesite lozinku", "Pogrešna lozinka!");
					}
				}
				else {
					labelGreska.setText("Podaci nisu validni!");
				}
			}
		});
		panelPosaljiSredstva.add(buttonPosalji);
		
		tabbedPane.addTab("Pošalji sredstva", panelPosaljiSredstva);
		tabbedPane.setForegroundAt(0, Color.BLACK);
		tabbedPane.setEnabledAt(0, true);
		
		labelGreska = new JLabel("");
		labelGreska.setBounds(240, 305, 391, 16);
		labelGreska.setForeground(Color.RED);
		labelGreska.setFont(new Font("Calibri", Font.PLAIN, 16));
		panelPosaljiSredstva.add(labelGreska);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(240, 52, 391, 76);
		panelPosaljiSredstva.add(scrollPane);
		
		textPaneAdresaPrimaoca = new JTextArea(10, 20);
		scrollPane.setViewportView(textPaneAdresaPrimaoca);
		textPaneAdresaPrimaoca.setBackground(new Color(255, 255, 255));
		textPaneAdresaPrimaoca.setFont(new Font("Courier New", Font.PLAIN, 15));
		textPaneAdresaPrimaoca.setLineWrap(true);
		textPaneAdresaPrimaoca.setWrapStyleWord(true);
		
		JPanel panelBlockExplorer = new JPanel();
		panelBlockExplorer.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("Block explorer", panelBlockExplorer);
		tabbedPane.setEnabledAt(1, true);

		tableBlocks = new JTable();
		tableBlocks.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JTable source = (JTable)arg0.getSource();
				int row = source.rowAtPoint(arg0.getPoint());
				String blockHash = (String)source.getModel().getValueAt(row, 0);

				Block block = LevelDBHandler.getBlock(DatatypeConverter.parseHexBinary(blockHash));
				ArrayList<Transaction> txs = block.getTransactions();
				tableModelTxs = new TableModelTxs();

				for (int i = 0; i < txs.size(); i++)
					tableModelTxs.addElement(txs.get(i));

				tableTxs.setModel(tableModelTxs);
				tableTxs.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//				tableBlocks.getColumnModel().getColumn(0).setPreferredWidth(430);
//				tableBlocks.getColumnModel().getColumn(1).setPreferredWidth(430);
//				tableBlocks.getColumnModel().getColumn(2).setPreferredWidth(1);
//				tableBlocks.getColumnModel().getColumn(3).setPreferredWidth(440);
				tableModelTxs.fireTableDataChanged();
			}
		});
		tableBlocks.setFillsViewportHeight(true);
		tableBlocks.setForeground(Color.DARK_GRAY);
		tableBlocks.setFont(new Font("Calibri", Font.PLAIN, 16));
		tableBlocks.getTableHeader().setFont( new Font("Calibri" , Font.BOLD, 16));
		tableBlocks.setRowHeight(23);
		tableBlocks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableBlocks.setModel(new DefaultTableModel(
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

		JScrollPane scrollPaneBlocks = new JScrollPane(tableBlocks);
		scrollPaneBlocks.setColumnHeaderView(tableBlocks);
		scrollPaneBlocks.setViewportView(tableBlocks);

		tableTxs = new JTable();
		tableTxs.setFillsViewportHeight(true);
		tableTxs.setForeground(Color.DARK_GRAY);
		tableTxs.setFont(new Font("Calibri", Font.PLAIN, 16));
		tableTxs.getTableHeader().setFont( new Font("Calibri" , Font.BOLD, 16));
		tableTxs.setRowHeight(23);
		tableTxs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableTxs.setModel(new DefaultTableModel(
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
		JScrollPane scrollPaneTxs = new JScrollPane(tableTxs);
		scrollPaneTxs.setColumnHeaderView(tableTxs);
		scrollPaneTxs.setViewportView(tableTxs);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				 				scrollPaneBlocks, scrollPaneTxs);
		splitPane.setResizeWeight(0.5);
		panelBlockExplorer.add(splitPane, BorderLayout.CENTER);
		
		JPanel panelAdresaBalans = new JPanel();
		panelAdresaBalans.setBackground(Color.WHITE);
		contentPane.add(panelAdresaBalans, BorderLayout.NORTH);
		panelAdresaBalans.setLayout(new BoxLayout(panelAdresaBalans, BoxLayout.Y_AXIS));
		
		JLabel lblMojaAdresa = new JLabel("Moja adresa");
		lblMojaAdresa.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMojaAdresa.setFont(new Font("Calibri", Font.PLAIN, 20));
		panelAdresaBalans.add(lblMojaAdresa);

		String formattedAddress = Hex.toHexString(myWallet.getPublicKey().getEncoded());
		textPaneMojaAdresa = new JTextPane();
		textPaneMojaAdresa.setText(formattedAddress);
		textPaneMojaAdresa.setEditable(false);
		textPaneMojaAdresa.setFont(new Font("Courier New", Font.PLAIN, 16));
		StyledDocument docAdresa = textPaneMojaAdresa.getStyledDocument();
		SimpleAttributeSet centerAdresa = new SimpleAttributeSet();
		StyleConstants.setAlignment(centerAdresa, StyleConstants.ALIGN_CENTER);
		docAdresa.setParagraphAttributes(0, formattedAddress.length(), centerAdresa, false);
		panelAdresaBalans.add(textPaneMojaAdresa);
		
		JLabel lblMojeStanje = new JLabel("Moje stanje");
		lblMojeStanje.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblMojeStanje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMojeStanje.setFont(new Font("Calibri", Font.PLAIN, 20));
		panelAdresaBalans.add(lblMojeStanje);
		
		textPaneMojeStanje = new JTextPane();
		textPaneMojeStanje.setText("" + LevelDBHandler.getBalance(myWallet.getPublicKey()));
		textPaneMojeStanje.setBackground(Color.WHITE);
		textPaneMojeStanje.setEditable(false);
		textPaneMojeStanje.setFont(new Font("Courier New", Font.PLAIN, 16));
		StyledDocument docStanje = textPaneMojeStanje.getStyledDocument();
		SimpleAttributeSet centerStanje = new SimpleAttributeSet();
		StyleConstants.setAlignment(centerStanje, StyleConstants.ALIGN_CENTER);
		docStanje.setParagraphAttributes(0, 0, centerStanje, false);
		panelAdresaBalans.add(textPaneMojeStanje);
	}

	private char[] showModalDialog(String title, String labelText, String errorText) {
		Object[] options = {"Potvrdi"};

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JPanel panel = new JPanel();
		panel.add(new JLabel(labelText));
		JPasswordField passField = new JPasswordField("", 15);
		panel.add(passField);
		JLabel errorLabel = new JLabel(errorText);
		errorLabel.setForeground(Color.RED);
		panel.add(errorLabel);

		int result = JOptionPane.showOptionDialog(this, panel, title,
												  JOptionPane.OK_OPTION,
												  JOptionPane.PLAIN_MESSAGE,
												  null, options, null);

		if (result == JOptionPane.YES_OPTION)
			return (passField.getPassword().length != 0)? passField.getPassword() : new char[]{0};
		else
			return null;
	}

	private boolean doesWalletExist() {
		File file = new File(Consts.MY_WALLET_PATH);
		File[] filesList = file.listFiles();

		return filesList.length > 0;
	}
}
