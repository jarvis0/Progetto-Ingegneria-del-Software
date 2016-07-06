package it.polimi.ingsw.ps23.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;

import javax.swing.JButton;
import javax.swing.JDialog;

public abstract class SwingUI {

	private static final String CONFIGURATION_PATH = "src/main/java/it/polimi/ingsw/ps23/client/commons/configuration/";
	private static final String IMAGES_PATH = "src/main/java/it/polimi/ingsw/ps23/client/commons/configuration/images/";
	private static final String COUNCILLOR_PATH = "Councillor.png";
	private static final String PERMIT_TILE_PATH = "permitTile.png";
	private static final String POLITIC_CARD_PATH = "Card.png";
	private static final String BONUS_TILE_PATH = "BonusTile.png";
	private static final String PNG_EXTENSION = ".png";

	private static final String KINGDOM = "kingdom";
	private static final String NO_KING_TILE = "noKingTile";

	private static final String ELECT_COUNCILLOR = "elect councillor";
	private static final String ACQUIRE_BUSINESS_PERMIT_TILE = "acquire business permit tile";
	private static final String ASSISTANT_TO_ELECT_COUNCILLOR = "assistant to elect councillor";
	private static final String ADDITIONAL_MAIN_ACTION = "additional main action";
	private static final String ENGAGE_ASSITANT = "engage assistant";
	private static final String CHANGE_PERMIT_TILE = "change permit tile";
	private static final String BUILD_EMPORIUM_KING = "build emporium king";
	private static final String BUILD_EMPORIUM_TILE = "build emporium permit tile";
	private static final String SKIP = "Skip";

	private String mapPath;
	private String playerName;
	private GUIView guiView;
	private String chosenAction;
	private String chosenRegion;
	private String chosenCity;
	private String chosenCouncillor;
	private List<JButton> regionsButtons;
	private JButton btnKingdom;
	private GUILoad guiLoad;
	private JPanel mainActionPanel;
	private JPanel quickActionPanel;
	private JButton skipButton;
	private Map<String, JLabel> cityLabels;
	private Map<String, Point> councilPoints;
	private Map<String, Map<JLabel, JLabel>> freeCouncillorsLabels;
	private JFrame frame;
	private JPanel mapPanel;
	private Map<String, Map<JLabel, List<JLabel>>> permitTiles;
	private int chosenTile;
	private DefaultTableModel tableModel;
	private JButton finished;
	private List<JLabel> cardsList;
	private Map<JLabel, List<JLabel>> playerPermitTiles;
	private Map<JLabel, Map <JLabel, List<JLabel>>> otherPlayersPermitTiles;
	private Map<JLabel, List<JLabel>> playerAllPermitTiles;
	boolean finish;
	private String chosenCard;
	private JSpinner marketSpinner;
	private JButton marketSendButton;
	private JDialog totalPermitsCardDialog;
	private int spinnerValue;
	private boolean cityListener;
	private boolean freeCuncillorListener;
	private boolean permitTileListener;
	private boolean politicCardListener;
	private JDialog otherPlayersDialog;
	private JButton otherPlayersStatusButton;
	private Map<String, Map<JLabel,List<JLabel>>> bonusTilePanels;

	protected SwingUI(GUIView guiView, String mapType, String playerName) {
		this.guiView = guiView;
		this.playerName = playerName;
		regionsButtons = new ArrayList<>();
		playerPermitTiles = new HashMap<>();
		otherPlayersPermitTiles = new HashMap<>();
		bonusTilePanels = new HashMap<>();
		mapPath = CONFIGURATION_PATH + mapType + "/";
		guiLoad = new GUILoad(mapPath);
		cityLabels = guiLoad.getCityLabels();
		marketSpinner = guiLoad.getMarketSpinner();
		marketSendButton = guiLoad.getMarketSendButton();
		freeCouncillorsLabels = new HashMap<>();
		cityListener = false;
		freeCuncillorListener = false;
		permitTileListener = false;
		politicCardListener = false;
		loadCitiesButtons();
		councilPoints = guiLoad.getCouncilPoints();
		cardsList = new ArrayList<>();
		permitTiles = new HashMap<>();
		finish = false;
		frame = guiLoad.getFrame();
		mapPanel = guiLoad.getMapPanel();
		tableModel = guiLoad.getTableModel();
		playerAllPermitTiles = new HashMap<>();
		totalPermitsCardDialog = new JDialog(frame, "Your Permission Total HandDeck");
		totalPermitsCardDialog.setBounds(300, 200, 600, 90);
		otherPlayersDialog = guiLoad.getOthersPlayersDialog();
		loadMarketInputArea();
		loadRegionButtons();
		loadMainActionPanel();
		loadQuickActionPanel();
		loadOthersPlayersStatusButton();
	}

	private void loadOthersPlayersStatusButton() {
		otherPlayersStatusButton = new JButton("Players Status");
		otherPlayersStatusButton.addActionListener(e -> 
			otherPlayersDialog.setVisible(true)
		);
		otherPlayersStatusButton.setBounds(1300, 150, 66, 40);
		mapPanel.add(otherPlayersStatusButton, 0);
	}

	protected JFrame getFrame() {
		return frame;
	}

	public int getChosenValue() {
		return spinnerValue;
	}

	public String getChosenAction() {
		return chosenAction;
	}

	public String getChosenRegion() {
		return chosenRegion;
	}

	public String getChosenCity() {
		return chosenCity;
	}

	public String getChosenCouncillor() {
		return chosenCouncillor;
	}

	public String getChosenCard() {
		return chosenCard;
	}

	public int getChosenTile() {
		return chosenTile;
	}

	protected static String getKingdom() {
		return KINGDOM;
	}

	protected static String getNoKingTile() {
		return NO_KING_TILE;
	}

	protected BufferedImage readImage(String path) {
		return guiLoad.readImage(path);
	}

	private JLabel getCityLabel(String componentName) {
		return cityLabels.get(componentName);
	}

	private Point getCouncilPoint(String region) {
		return councilPoints.get(region);
	}
	/**
	 * Sets the value of display of the king buttons in GUI. True will set visible, false not visible.
	 * @param display - value of display
	 */
	public void enableKingButton(boolean display) {
		btnKingdom.setEnabled(display);
	}

	private List<JLabel> drawBonus(Container container, String bonusName, String bonusValue, Point point, int width, int height, int yOffset) {
		int x = (int) point.getX();
		int y = (int) point.getY();
		List<JLabel> bonusList = new ArrayList<>();
		BufferedImage bonusImage = guiLoad.readImage(IMAGES_PATH + bonusName + PNG_EXTENSION);
		Image resizedBonusImage = bonusImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		JLabel bonusLabel = new JLabel(new ImageIcon(resizedBonusImage));
		bonusLabel.setBounds(0, 0, width, height);
		bonusLabel.setLocation(x, y + yOffset);
		bonusList.add(bonusLabel);
		container.add(bonusLabel, 0);
		int bonusNumber = Integer.parseInt(bonusValue);
		if (bonusNumber > 1 || "victoryPoint".equals(bonusName)) {
			JLabel bonusLabelValue = new JLabel();
			bonusLabelValue.setBounds(0, 0, width, height);
			bonusLabelValue.setLocation(x + 8, y + yOffset);
			bonusLabelValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
			if ("coin".equals(bonusName)) {
				bonusLabelValue.setForeground(Color.black);
			} else {
				bonusLabelValue.setForeground(Color.white);
			}
			bonusLabelValue.setText(String.valueOf(bonusNumber));
			bonusList.add(bonusLabelValue);
			container.add(bonusLabelValue, 0);
		}
		return bonusList;
	}

	protected void addRewardTokens(List<String> citiesName, List<List<String>> citiesBonusesName,
			List<List<String>> citiesBonusesValue) {
		for (int i = 0; i < citiesName.size(); i++) {
			Component cityComponent = getCityLabel(citiesName.get(i));
			Point point = cityComponent.getLocationOnScreen();
			int x = point.x;
			int y = point.y;
			for (int j = 0; j < citiesBonusesName.get(i).size(); j++) {
				drawBonus(mapPanel, citiesBonusesName.get(i).get(j), citiesBonusesValue.get(i).get(j), new Point(x + 50, y - 20), 23, 25, 0);
				x += 22;
			}
		}
	}
	
	private void drawNobilityTrackBonus(List<List<String>> stepsBonusesName, List<List<String>> stepsBonusesValue, int xParam, int yParam, int yOffsetParam, int i, int j) {
		int x = xParam;
		int y = yParam;
		if (!("nullBonus").equals(stepsBonusesName.get(i).get(j))) {
			int width = 23;
			int height = 25;
			if ("1".equals(stepsBonusesValue.get(i).get(j))) {
				y = 490;
			}
			if (("recycleRewardToken").equals(stepsBonusesName.get(i).get(j))) {
				y = 476;
				height = 40;
			}
			drawBonus(mapPanel, stepsBonusesName.get(i).get(j), stepsBonusesValue.get(i).get(j), new Point(x, y), width, height, yOffsetParam);
		}
	}

	protected void addNobilityTrackBonuses(List<List<String>> stepsBonusesName, List<List<String>> stepsBonusesValue) {
		int stepNumber = 0;
		for (int i = 0; i < stepsBonusesName.size(); i++) {
			int yOffset = 0;
			int x = (int) 38.1 * stepNumber + 8;
			int y = 495;
			for (int j = 0; j < stepsBonusesName.get(i).size(); j++) {
				drawNobilityTrackBonus(stepsBonusesName, stepsBonusesValue, x, y, yOffset, i, j);
				yOffset -= 25;
			}
			stepNumber++;
		}
	}

	protected void refreshKingPosition(String city) {
		Point point = getCityLabel(city).getLocationOnScreen();
		guiLoad.getKingLabel().setLocation(point);
	}

	protected void refreshFreeCouncillors(List<String> freeCouncillors) {
		for (Entry<String, Map<JLabel, JLabel>> entry : freeCouncillorsLabels.entrySet()) {
			Map<JLabel, JLabel> freeCouncillor = entry.getValue();
			for (Entry<JLabel, JLabel> jlabel : freeCouncillor.entrySet()) {
				mapPanel.remove(jlabel.getValue());
				mapPanel.remove(jlabel.getKey());
			}
		}
		Point freeCouncillorsPoint = councilPoints.get("free");
		Map<String, Integer> freeCouncillorsMap = new HashMap<>();
		for (String freeCouncillor : freeCouncillors) {
			if (freeCouncillorsMap.containsKey(freeCouncillor)) {
				freeCouncillorsMap.put(freeCouncillor, freeCouncillorsMap.get(freeCouncillor) + 1);
			} else {
				freeCouncillorsMap.put(freeCouncillor, 1);
			}
		}
		drawFreeCouncillor(freeCouncillorsMap, freeCouncillorsPoint);
	}
	
	private void drawFreeCouncillor(Map<String, Integer> freeCouncillorsMap, Point freeCouncillorsPoint) {
		int x = freeCouncillorsPoint.x;
		int y = freeCouncillorsPoint.y;
		for (Entry<String, Integer> entry : freeCouncillorsMap.entrySet()) {
			String color = entry.getKey();
			BufferedImage councillorImage = guiLoad.readImage(IMAGES_PATH + color + COUNCILLOR_PATH);
			Image resizedCouncillorImage = councillorImage.getScaledInstance(18, 39, Image.SCALE_SMOOTH);
			JLabel councillorLabel = new JLabel(new ImageIcon(resizedCouncillorImage));
			councillorLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			councillorLabel.setEnabled(false);
			councillorLabel.setDisabledIcon(new ImageIcon(resizedCouncillorImage));
			councillorLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(freeCuncillorListener) {
					chosenCouncillor = color;
					councillorLabel.setEnabled(true);
					guiView.resume();
					}
				}
			});
			councillorLabel.setBounds(0, 0, 28, 52);
			councillorLabel.setLocation(x, y);
			mapPanel.add(councillorLabel, 0);
			JLabel councillorsValue = new JLabel();
			councillorsValue.setBounds(0, 0, 10, 25);
			councillorsValue.setLocation(x + 11, y + 16);
			councillorsValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			if ("black".equals(color) || "purple".equals(color) || "blue".equals(color)) {
				councillorsValue.setForeground(Color.white);
			} else {
				councillorsValue.setForeground(Color.black);
			}
			councillorsValue.setText(String.valueOf(entry.getValue()));
			mapPanel.add(councillorsValue, 0);
			Map<JLabel, JLabel> freeCouncillor = new HashMap<>();
			freeCouncillor.put(councillorLabel, councillorsValue);
			freeCouncillorsLabels.put(color, freeCouncillor);
			y += 43;
		}
	}

	private void drawCouncillor(String color, int x, int y) {
		BufferedImage councillorImage = guiLoad.readImage(IMAGES_PATH + color + COUNCILLOR_PATH);
		Image resizedCouncillorImage = councillorImage.getScaledInstance(14, 39, Image.SCALE_SMOOTH);
		JLabel councillorLabel = new JLabel(new ImageIcon(resizedCouncillorImage));
		councillorLabel.setBounds(0, 0, 15, 39);
		councillorLabel.setLocation(x, y);
		mapPanel.add(councillorLabel, 0);
	}

	private void drawCouncil(List<String> council, int xCoord, int yCoord) {
		int x = xCoord;
		int y = yCoord;
		for (String councillor : council) {
			x -= 16;
			drawCouncillor(councillor, x, y);
		}
	}

	protected void refreshCouncils(List<String> councilsName, List<List<String>> councilsColor) {
		for (int i = 0; i < councilsName.size(); i++) {
			Point point = getCouncilPoint(councilsName.get(i));
			drawCouncil(councilsColor.get(i), point.x, point.y);
		}
	}

	private void drawPermitTile(Container container, Map<JLabel, List<JLabel>> permitLabels,
			List<String> permitTileCities, List<String> permitTileBonusesName, List<String> permitTileBonusesValue,
			int indexOfTile, Point point) {
		int x = (int) point.getX();
		int y = (int) point.getY();
		List<JLabel> listJlabel = new ArrayList<>();
		BufferedImage permissionTileImage = readImage(IMAGES_PATH + PERMIT_TILE_PATH);
		Image resizedPermissionTile = permissionTileImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		JLabel permitTileLabel = new JLabel(new ImageIcon(resizedPermissionTile));
		permitTileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		permitTileLabel.setBounds(0, 0, 50, 50);
		permitTileLabel.setLocation(x, y);
		permitTileLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(permitTileListener) {
					chosenTile = indexOfTile;
					permitTileLabel.setEnabled(true);
					guiView.resume();
				}
			}
		});
		container.add(permitTileLabel, 0);
		permitTileLabel.setEnabled(false);
		int cityCoordX = x + 5;
		int cityCoordY = y;
		int citiesNumber = permitTileCities.size();
		for (int i = 0; i < citiesNumber; i++) {
			String cityName = permitTileCities.get(i);
			JLabel cityInitial = new JLabel();
			String slash = new String();
			if (i != citiesNumber - 1) {
				slash = " / ";
			}
			cityInitial.setText(cityName + slash);
			cityInitial.setBounds(0, 0, 23, 25);
			cityInitial.setLocation(cityCoordX, cityCoordY);
			cityInitial.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			cityInitial.setForeground(Color.black);
			container.add(cityInitial, 0);
			listJlabel.add(cityInitial);
			cityCoordX += 17;
		}
		int bonusCoordX = x - 47;
		int bonusCoordY = y + 40;
		for (int i = 0; i < permitTileBonusesName.size(); i++) {
			listJlabel.addAll(drawBonus(container, permitTileBonusesName.get(i), permitTileBonusesValue.get(i), new Point(bonusCoordX + 50, bonusCoordY - 20), 23, 25, 0));
			bonusCoordX = bonusCoordX + 24;
		}
		permitLabels.put(permitTileLabel, listJlabel);
	}

	private void drawPermitTiles(Container container, Map<JLabel, List<JLabel>> permitLabels,
			List<List<String>> permitTilesCities, List<List<String>> permitTilesBonusesName,
			List<List<String>> permitTilesBonusesValue, Point point, int increment) {
		int x = (int) point.getX();
		int y = (int) point.getY();
		int indexOfTile = 0;
		for (int i = 0; i < permitTilesCities.size(); i++) {
			drawPermitTile(container, permitLabels, permitTilesCities.get(i), permitTilesBonusesName.get(i),
					permitTilesBonusesValue.get(i), indexOfTile, new Point(x, y));
			x += increment;
			indexOfTile++;
		}
	}

	protected void refreshPermitTilesUp(List<String> regions, List<List<List<String>>> allPermitTilesCities,
			List<List<List<String>>> allPermitTilesBonusesName, List<List<List<String>>> allPermitTilesBonusesValue) {
		for (int i = 0; i < regions.size(); i++) {
			Point point = getCouncilPoint(regions.get(i));
			int x = point.x;
			int y = point.y;
			Map<JLabel, List<JLabel>> permitTilesLabels = new HashMap<>();
			drawPermitTiles(mapPanel, permitTilesLabels, allPermitTilesCities.get(i), allPermitTilesBonusesName.get(i),
					allPermitTilesBonusesValue.get(i), new Point(x - 120, y - 12), -52);
			permitTiles.put(regions.get(i), permitTilesLabels);
		}
	}

	private void drawBonusTile(String groupName, String bonusName, String bonusValue) {
		Point regionPoint = getCouncilPoint(groupName);
		int x = regionPoint.x;
		int y = regionPoint.y;
		if (KINGDOM.equals(groupName)) {
			x -= 63;
			y -= 40;
		} else {
			x += 7;
			y -= 8;
		}
		BufferedImage tileImage = guiLoad.readImage(IMAGES_PATH + groupName + BONUS_TILE_PATH);
		Image resizedTileImage = tileImage.getScaledInstance(50, 35, Image.SCALE_SMOOTH);
		JLabel tileLabel = new JLabel(new ImageIcon(resizedTileImage));
		tileLabel.setBounds(0, 0, 50, 35);
		tileLabel.setLocation(x, y);
		mapPanel.add(tileLabel, 0);
		Map<JLabel, List<JLabel>> permiTilesMap = new HashMap<>();
		permiTilesMap.put(tileLabel, drawBonus(mapPanel, bonusName, bonusValue, new Point(x + 25, y + 10), 23, 25, -5));
		bonusTilePanels.put(groupName, permiTilesMap);
		
	}

	protected void refreshBonusTiles(List<String> groupsName, List<String> groupsBonusName,
			List<String> groupsBonusValue, String kingBonusName, String kingBonusValue) {
		for (Entry<String, Map<JLabel, List<JLabel>>> bonusTile : bonusTilePanels.entrySet()) {
			for(Entry<JLabel, List<JLabel>> bonusLabel  : bonusTile.getValue().entrySet()) {
				mapPanel.remove(bonusLabel.getKey());
				for(JLabel jLabel : bonusLabel.getValue()) {
					mapPanel.remove(jLabel);
				}
			}
		}
		bonusTilePanels.clear();
		for (int i = 0; i < groupsBonusName.size(); i++) {
			String regionBonusName = groupsBonusName.get(i);
			drawBonusTile(groupsName.get(i), regionBonusName, groupsBonusValue.get(i));
		}
		if (!NO_KING_TILE.equals(kingBonusName)) {
			drawBonusTile(KINGDOM, kingBonusName, kingBonusValue);
		}
	}

	protected void refreshPlayersTable(List<String> playersName, List<String> playersCoins,
			List<String> playersAssistants, List<String> playersNobilityTrackPoints,
			List<String> playersVictoryPoints, List<String> playersAreOnline) {
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		for (int i = 0; i < playersName.size(); i++) {
			String nameField =  playersName.get(i);
			String playerCoinsField = playersCoins.get(i);
			String playerAssistantsField = playersAssistants.get(i);
			String playersNobilityTrackPointsField = playersNobilityTrackPoints.get(i);
			String playerVictoryPointsField = playersVictoryPoints.get(i);
			String playerIsOnline = playersAreOnline.get(i); 
			Object[] vector = {nameField, playerCoinsField, playerAssistantsField, playersNobilityTrackPointsField, playerVictoryPointsField, playerIsOnline};
			tableModel.addRow(vector);
		}
	}

	protected void refreshPoliticCards(Map<String, List<String>> playersPoliticCards) {
		int x = 0;
		int y = 535;
		for (JLabel card : cardsList) {
			mapPanel.remove(card);
		}
		if (finished != null) {
			mapPanel.remove(finished);
		}
		for (String card : playersPoliticCards.get(playerName)) {
			BufferedImage cardImage = readImage(IMAGES_PATH + card + POLITIC_CARD_PATH);
			Image resizedCardImage = cardImage.getScaledInstance(42, 66, Image.SCALE_SMOOTH);
			JLabel cardLabel = new JLabel(new ImageIcon(resizedCardImage));
			cardLabel.setDisabledIcon(new ImageIcon(resizedCardImage));
			cardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			cardsList.add(cardLabel);
			cardLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(politicCardListener) {
						chosenCard = card;
						cardLabel.setEnabled(false);
						guiView.resume();
					}
				}
			});
			cardLabel.setBounds(0, 0, 42, 66);
			cardLabel.setLocation(x, y);
			x += 44;
			mapPanel.add(cardLabel, 0);
			cardLabel.setEnabled(false);
		}
		finished = new JButton("Finish");
		finished.addActionListener(e -> {
			finish = true;
			guiView.resume();
		});
		finished.setBounds(x, y, 80, 40);
		finished.setEnabled(false);
		mapPanel.add(finished, 0);
	}

	protected void loadMainActionPanel() {
		mainActionPanel = new JPanel();
		mainActionPanel.setBounds(925, 181, 215, 272);
		mapPanel.add(mainActionPanel, 0);
		mainActionPanel.setVisible(false);
		GridBagLayout gblMainActionPanel = new GridBagLayout();
		gblMainActionPanel.columnWidths = new int[] { 0, 0 };
		gblMainActionPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gblMainActionPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gblMainActionPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		mainActionPanel.setLayout(gblMainActionPanel);
		JButton btnAcquireBusinessPermitTile = new JButton();
		btnAcquireBusinessPermitTile.addActionListener(e -> {
			chosenAction = ACQUIRE_BUSINESS_PERMIT_TILE;
			guiView.resume();
		});
		BufferedImage acquireBusinessPermitTileImage = guiLoad.readImage(IMAGES_PATH + "acquireBusinessPermitTile.png");
		btnAcquireBusinessPermitTile.setIcon(new ImageIcon(acquireBusinessPermitTileImage));
		GridBagConstraints gbcbtnAcquireBusinessPermitTile = new GridBagConstraints();
		gbcbtnAcquireBusinessPermitTile.insets = new Insets(0, 0, 5, 0);
		gbcbtnAcquireBusinessPermitTile.gridx = 0;
		gbcbtnAcquireBusinessPermitTile.gridy = 0;
		mainActionPanel.add(btnAcquireBusinessPermitTile, gbcbtnAcquireBusinessPermitTile);

		JButton btnBuildEmporiumKing = new JButton();
		btnBuildEmporiumKing.addActionListener(e -> {
			chosenAction = BUILD_EMPORIUM_KING;
			guiView.resume();
		});
		BufferedImage buildEmporiumKingImage = guiLoad.readImage(IMAGES_PATH + "buildEmporiumKing.png");
		btnBuildEmporiumKing.setIcon(new ImageIcon(buildEmporiumKingImage));
		btnBuildEmporiumKing.setBounds(0, 0, 182, 54);
		GridBagConstraints gbcbtnBuildEmporiumKing = new GridBagConstraints();
		gbcbtnBuildEmporiumKing.insets = new Insets(0, 0, 5, 0);
		gbcbtnBuildEmporiumKing.gridx = 0;
		gbcbtnBuildEmporiumKing.gridy = 1;
		mainActionPanel.add(btnBuildEmporiumKing, gbcbtnBuildEmporiumKing);

		JButton btnElectCouncillor = new JButton();
		btnElectCouncillor.addActionListener(e -> {
			chosenAction = ELECT_COUNCILLOR;
			guiView.resume();
		});
		BufferedImage electCouncillorImage = guiLoad.readImage(IMAGES_PATH + "electCouncillor.png");
		btnElectCouncillor.setIcon(new ImageIcon(electCouncillorImage));
		GridBagConstraints gbcbtnElectCouncillor = new GridBagConstraints();
		gbcbtnElectCouncillor.insets = new Insets(0, 0, 5, 0);
		gbcbtnElectCouncillor.gridx = 0;
		gbcbtnElectCouncillor.gridy = 2;
		mainActionPanel.add(btnElectCouncillor, gbcbtnElectCouncillor);

		JButton btnBuildEmporiumPermitTile = new JButton();
		btnBuildEmporiumPermitTile.addActionListener(e -> {
			chosenAction = BUILD_EMPORIUM_TILE;
			guiView.resume();
		});
		BufferedImage builEmporiumPermitTileImage = guiLoad.readImage(IMAGES_PATH + "buildEmporiumPermitTile.png");
		btnBuildEmporiumPermitTile.setIcon(new ImageIcon(builEmporiumPermitTileImage));
		GridBagConstraints gbcbtnBuildEmporiumPermitTile = new GridBagConstraints();
		gbcbtnBuildEmporiumPermitTile.gridx = 0;
		gbcbtnBuildEmporiumPermitTile.gridy = 3;
		mainActionPanel.add(btnBuildEmporiumPermitTile, gbcbtnBuildEmporiumPermitTile);
	}

	protected void loadQuickActionPanel() {
		quickActionPanel = new JPanel();
		quickActionPanel.setBounds(1150, 181, 199, 272);
		mapPanel.add(quickActionPanel, 0);
		quickActionPanel.setVisible(false);
		GridBagLayout gblQuickActionPanel = new GridBagLayout();
		gblQuickActionPanel.columnWidths = new int[] { 0, 0 };
		gblQuickActionPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gblQuickActionPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gblQuickActionPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		quickActionPanel.setLayout(gblQuickActionPanel);

		JButton btnEngageAssistant = new JButton();
		btnEngageAssistant.addActionListener(e -> {
			chosenAction = ENGAGE_ASSITANT;
			guiView.resume();
		});
		BufferedImage engageAssistantImage = guiLoad.readImage(IMAGES_PATH + "engageAssistant.png");
		btnEngageAssistant.setIcon(new ImageIcon(engageAssistantImage));
		GridBagConstraints gbcbtnEngageAssistant = new GridBagConstraints();
		gbcbtnEngageAssistant.insets = new Insets(0, 0, 5, 0);
		gbcbtnEngageAssistant.gridx = 0;
		gbcbtnEngageAssistant.gridy = 0;
		quickActionPanel.add(btnEngageAssistant, gbcbtnEngageAssistant);

		JButton btnChangePermitsTile = new JButton();
		btnChangePermitsTile.addActionListener(e -> {
			chosenAction = CHANGE_PERMIT_TILE;
			guiView.resume();
		});
		BufferedImage changePermitsTileImage = guiLoad.readImage(IMAGES_PATH + "changePermitsTile.png");
		btnChangePermitsTile.setIcon(new ImageIcon(changePermitsTileImage));
		GridBagConstraints gbcbtnChangePermitsTile = new GridBagConstraints();
		gbcbtnChangePermitsTile.insets = new Insets(0, 0, 5, 0);
		gbcbtnChangePermitsTile.gridx = 0;
		gbcbtnChangePermitsTile.gridy = 1;
		quickActionPanel.add(btnChangePermitsTile, gbcbtnChangePermitsTile);

		JButton btnAssistantToElectCouncillor = new JButton();
		btnAssistantToElectCouncillor.addActionListener(e -> {
			chosenAction = ASSISTANT_TO_ELECT_COUNCILLOR;
			guiView.resume();
		});
		BufferedImage assistantToElectCouncillorImage = guiLoad
				.readImage(IMAGES_PATH + "assistantToElectCouncillor.png");
		btnAssistantToElectCouncillor.setIcon(new ImageIcon(assistantToElectCouncillorImage));
		GridBagConstraints gbcbtnAssistantToElectCouncillor = new GridBagConstraints();
		gbcbtnAssistantToElectCouncillor.insets = new Insets(0, 0, 5, 0);
		gbcbtnAssistantToElectCouncillor.gridx = 0;
		gbcbtnAssistantToElectCouncillor.gridy = 2;
		quickActionPanel.add(btnAssistantToElectCouncillor, gbcbtnAssistantToElectCouncillor);

		JButton btnAdditionalMainAction = new JButton();
		btnAdditionalMainAction.addActionListener(e -> {
			chosenAction = ADDITIONAL_MAIN_ACTION;
			guiView.resume();
		});
		BufferedImage additionalMainActionImage = guiLoad.readImage(IMAGES_PATH + "buyMainAction.png");
		btnAdditionalMainAction.setIcon(new ImageIcon(additionalMainActionImage));
		GridBagConstraints gbcbtnAdditionalMainAction = new GridBagConstraints();
		gbcbtnAdditionalMainAction.gridx = 0;
		gbcbtnAdditionalMainAction.gridy = 3;
		quickActionPanel.add(btnAdditionalMainAction, gbcbtnAdditionalMainAction);
		skipButton = new JButton(SKIP);
		skipButton.addActionListener(e -> {
			chosenAction = SKIP;
			guiView.resume();
		});
		skipButton.setEnabled(false);
		skipButton.setBounds(1283, 453, 66, 40);
		mapPanel.add(skipButton, 0);
	}

	private void loadRegionButtons() {
		BufferedImage kingImage = guiLoad.readImage(IMAGES_PATH + "kingIcon.png");
		btnKingdom = new JButton();
		btnKingdom.addActionListener(e -> {
			chosenRegion = "king";
			guiView.resume();
		});
		btnKingdom.setIcon(new ImageIcon(kingImage));
		btnKingdom.setBounds(865, 414, 50, 50);
		mapPanel.add(btnKingdom, 0);
		btnKingdom.setEnabled(false);
		BufferedImage seasideImage = guiLoad.readImage(IMAGES_PATH + "seasideRegion.png");
		JButton btnSeaside = new JButton();
		btnSeaside.addActionListener(e -> {
			chosenRegion = "seaside";
			guiView.resume();
		});
		btnSeaside.setIcon(new ImageIcon(seasideImage));
		btnSeaside.setBounds(120, 0, 50, 50);
		mapPanel.add(btnSeaside, 0);
		regionsButtons.add(btnSeaside);
		btnSeaside.setEnabled(false);
		BufferedImage hillImage = guiLoad.readImage(IMAGES_PATH + "hillRegion.png");
		JButton btnHill = new JButton();
		btnHill.addActionListener(e -> {
			chosenRegion = "hill";
			guiView.resume();
		});
		btnHill.setIcon(new ImageIcon(hillImage));
		btnHill.setBounds(370, 0, 50, 50);
		mapPanel.add(btnHill, 0);
		regionsButtons.add(btnHill);
		btnHill.setEnabled(false);
		BufferedImage mountainImage = guiLoad.readImage(IMAGES_PATH + "mountainRegion.png");
		JButton btnMountain = new JButton();
		btnMountain.addActionListener(e -> {
			chosenRegion = "mountain";
			guiView.resume();
		});
		btnMountain.setIcon(new ImageIcon(mountainImage));
		btnMountain.setBounds(670, 0, 50, 50);
		mapPanel.add(btnMountain, 0);
		regionsButtons.add(btnMountain);
		btnMountain.setEnabled(false);
	}

	private void loadCitiesButtons() {
		Set<Entry<String, JLabel>> cityLabelsSet = cityLabels.entrySet();
		for (Entry<String, JLabel> cityLabel : cityLabelsSet){
			cityLabel.getValue().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(cityListener) {
						chosenCity = cityLabel.getKey();
						cityLabel.getValue().setEnabled(true);
						guiView.resume();
					}
				}
			});
		}
	}
	
	protected void refreshCitiesToolTip(List<String> citiesName, List<List<String>> citiesBuiltEmporium) {
		int i = 0;
		for(String cityName : citiesName) {
			StringBuilder toolTipString = new StringBuilder();
			toolTipString.append("List of Player's Emporiums: \n");
			JLabel cityLabel = cityLabels.get(cityName);
			for(String playerNameString : citiesBuiltEmporium.get(i)) {
				toolTipString.append(playerNameString);
			}
			cityLabel.setToolTipText(new String(toolTipString) + "\n");
			i++;
		}
	}

	private void enableCitiesButtons(boolean display) {
		cityListener = display;
		Set<Entry<String, JLabel>> cityLabelsSet = cityLabels.entrySet();
		for (Entry<String, JLabel> cityLabel : cityLabelsSet) {
			cityLabel.getValue().setEnabled(display);
		}
		
	}

	private void enableCards(boolean display) {
		politicCardListener = display;
		for (JLabel jLabel : cardsList) {
			jLabel.setEnabled(display);
		}
	}
	/**
	 * Sets the visibility of the panel of main action and quick action. If is it true it will display the 
	 * panel else the panel will be not displayed.
	 * @param isAvailableMainAction - the visibility of main action
	 * @param isAvailableQuickAction - the visibility of quick action
	 */
	public void showAvailableActions(boolean isAvailableMainAction, boolean isAvailableQuickAction) {
		mainActionPanel.setVisible(isAvailableMainAction);
		quickActionPanel.setVisible(isAvailableQuickAction);
		skipButton.setEnabled(false);
		if (!isAvailableMainAction && isAvailableQuickAction) {
			skipButton.setEnabled(true);
		}
		enableRegionButtons(false);
	}
	/**
	 * Sets the visibility of the free councillor buttons. If is it true it will display the button else
	 * the button will be not enabled.
	 * @param display - the visibility of the free councillors buttons
	 */
	public void enableFreeCouncillorsButtons(boolean display) {
		freeCuncillorListener = display;
		for (Entry<String, Map<JLabel, JLabel>> entry : freeCouncillorsLabels.entrySet()) {
			Map<JLabel, JLabel> freeCouncillor = entry.getValue();
			for (Entry<JLabel, JLabel> jlabel : freeCouncillor.entrySet()) {
				jlabel.getKey().setEnabled(display);
			}
		}

	}
	/**
	 * Sets the visibility of politic cards label. If is it true it will display the labels else
	 * the labels will be not enabled.
	 * @param display - the visibility of the politic card labels
	 */
	public void enablePoliticCards(boolean display) {
		enableCards(display);
	}
	/**
	 * Sets the visibility of the specific permit tile panel. If is it true it will display the panel else
	 * the panel will be not enabled
	 * @param chosenCouncil - the selected council
	 * @param display - the visibility of the permit tile panel
	 */
	public void enablePermitTilesPanel(String chosenCouncil, boolean display) {
		permitTileListener = display;
		Map<JLabel, List<JLabel>> permitTilesLabels = permitTiles.get(chosenCouncil);
		for (JLabel jLabel : permitTilesLabels.keySet()) {
			jLabel.setEnabled(display);
		}
	}
	/**
	 * Sets the visibility of the cities. If is it true it will display the cities else the cities will be not
	 * enabled
	 * @param display - the visibility of cities
	 */
	public void enableCities(boolean display) {
		enableCitiesButtons(display);
	}
	/**
	 * Sets the visibility of the region buttons. If is it true it will display the region buttons else
	 * the region buttons will be not enabled
	 * @param display - the visibility of region buttons
	 */
	public void enableRegionButtons(boolean display) {
		for (JButton regionButton : regionsButtons) {
			regionButton.setEnabled(display);
		}
	}
	/**
	 * Sets the visibility of the permit tile deck. If is it true it will display the permit tile deck else
	 * the permit tile deck will be not displayed.
	 * @param display - the visibility of permit deck
	 */
	public void enablePermitTileDeck(boolean display) {
		permitTileListener = true;
		for (JLabel jLabel : playerPermitTiles.keySet()) {
			jLabel.setEnabled(display);
		}
	}

	protected void clearChosenRegion() {
		chosenRegion = null;
	}

	public void clearSwingUI() {
		chosenCard = null;
		finish = false;
		clearChosenRegion();
		Set<Entry<String, Map<JLabel, List<JLabel>>>> allPermitTilesEntries = permitTiles.entrySet();
		for (Entry<String, Map<JLabel, List<JLabel>>> permitTilesEntry : allPermitTilesEntries) {
			Map<JLabel, List<JLabel>> permitTilesLabel = permitTilesEntry.getValue();
			for (JLabel label : permitTilesLabel.keySet()) {
				label.setEnabled(false);
			}
		}
	}
	
	public boolean hasFinished() {
		return finish;
	}
	/**
	 * Loads the area used by the user when the game enter in market phase.
	 */
	public void loadMarketInputArea() {
		marketSendButton.addActionListener(e -> {
			spinnerValue = (int) marketSpinner.getValue();
			guiView.resume();
		});
	}
	/**
	 * Appends a string to the text displayed on the console.
	 * @param string - the string to append
	 */
	public void appendConsoleText(String string) {
		guiLoad.appendText(string);
	}
	/**
	 * Set the visibility of the market input area. If is it true it will display the input area else
	 * the market input area will be not displayed.
	 * @param display - the visibility of market input area
	 */
	public void enableMarketInputArea(boolean display) {
		marketSpinner.setVisible(display);
		marketSpinner.setEnabled(display);
		marketSendButton.setVisible(display);
		marketSendButton.setEnabled(display);
	}
	/**
	 * Sets the visibility of finish button. If is it true it will display the button else
	 * the finish button will be not enabled.
	 * @param display
	 */
	public void enableFinish(boolean display) {
		finished.setEnabled(display);
	}

	private void refreshAcquiredPermitTiles(List<String> playersName, List<List<List<String>>> permitTilesCities,
			List<List<List<String>>> permitTilesBonusesName, List<List<List<String>>> permitTilesBonusesValue) {
		Set<Entry<JLabel, List<JLabel>>> playerPermitTilesSet = playerPermitTiles.entrySet();
		for (Entry<JLabel, List<JLabel>> playerPermitTile : playerPermitTilesSet) {
			mapPanel.remove(playerPermitTile.getKey());
			for (JLabel jLabel : playerPermitTile.getValue()) {
				mapPanel.remove(jLabel);
			}
		}
		playerPermitTiles.clear();
		
		int x = 0;
		int y = 611;
		int playerIndex = playersName.indexOf(playerName);
		int increment = 52;
		drawPermitTiles(mapPanel, playerPermitTiles, permitTilesCities.get(playerIndex), permitTilesBonusesName.get(playerIndex),
					permitTilesBonusesValue.get(playerIndex), new Point(x, y), increment);
		
	}
	
	private void refreshOtherPlayersStatusDialog(List<String> playersName, List<List<List<String>>> permitTilesCities,
			List<List<List<String>>> permitTilesBonusesName, List<List<List<String>>> permitTilesBonusesValue) {
	
		for (Entry<JLabel, Map<JLabel, List<JLabel>>> playerPermitTile : otherPlayersPermitTiles.entrySet()) {
			otherPlayersDialog.remove(playerPermitTile.getKey());
			for(Entry<JLabel, List<JLabel>> permitLabel  : playerPermitTile.getValue().entrySet()) {
				otherPlayersDialog.remove(permitLabel.getKey());
				for(JLabel jLabel : permitLabel.getValue()) {
					otherPlayersDialog.remove(jLabel);
				}
			}
		}
		otherPlayersPermitTiles.clear();

		int x = 0;
		int y = 30;
		int currentPlayerIndex = playersName.indexOf(playerName);
		int increment = 52;
		int i = 0;
		for (String playerNameString : playersName) {
			if(i != currentPlayerIndex) {
				JLabel playerNameLabel = new JLabel();
				playerNameLabel.setText(playerNameString);
				playerNameLabel.setBounds(0, 0, 100, 25);
				playerNameLabel.setLocation(x, y - 30);
				playerNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
				playerNameLabel.setForeground(Color.black);
				otherPlayersDialog.add(playerNameLabel,0);
				Map<JLabel, List<JLabel>> otherPlayerPermitTilesMap = new HashMap<>();
				drawPermitTiles(otherPlayersDialog, otherPlayerPermitTilesMap, permitTilesCities.get(i), permitTilesBonusesName.get(i),
							permitTilesBonusesValue.get(i), new Point(x, y), increment);
				otherPlayersPermitTiles.put(playerNameLabel, otherPlayerPermitTilesMap);
				y += 70;
			}
			i++;
		}
	}

	protected void refreshGamePlayersPermitTiles(List<String> playersName, List<List<List<String>>> permitTilesCities,
		List<List<List<String>>> permitTilesBonusesName, List<List<List<String>>> permitTilesBonusesValue) {
		refreshAcquiredPermitTiles(playersName, permitTilesCities, permitTilesBonusesName, permitTilesBonusesValue);
		refreshOtherPlayersStatusDialog(playersName, permitTilesCities, permitTilesBonusesName, permitTilesBonusesValue);
	}
	
	protected void refreshAllPermitTiles(List<String> playersName, List<List<List<String>>> permitTilesCities,
			List<List<List<String>>> permitTilesBonusesName, List<List<List<String>>> permitTilesBonusesValue) {
		Set<Entry<JLabel, List<JLabel>>> playerAllPermitTilesSet = playerAllPermitTiles.entrySet();
		for (Entry<JLabel, List<JLabel>> playerPermitTile : playerAllPermitTilesSet) {
			totalPermitsCardDialog.remove(playerPermitTile.getKey());
			for (JLabel jLabel : playerPermitTile.getValue()) {
				totalPermitsCardDialog.remove(jLabel);
			}
		}
		playerAllPermitTiles.clear();
		int x = 0;
		int y = 0;
		int playerIndex = playersName.indexOf(playerName);
		drawPermitTiles(totalPermitsCardDialog, playerAllPermitTiles, permitTilesCities.get(playerIndex), permitTilesBonusesName.get(playerIndex), permitTilesBonusesValue.get(playerIndex), new Point(x, y), 52);
		
	}

	public void enableTotalHandDeck(boolean display) {
		permitTileListener = display;
		for (JLabel jLabel : playerAllPermitTiles.keySet()) {
			jLabel.setEnabled(display);
		}
		totalPermitsCardDialog.setVisible(display);
	}	
	
	
}