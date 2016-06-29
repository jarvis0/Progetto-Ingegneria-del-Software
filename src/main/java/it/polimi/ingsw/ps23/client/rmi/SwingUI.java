package it.polimi.ingsw.ps23.client.rmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

import it.polimi.ingsw.ps23.server.model.initialization.RawObject;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public abstract class SwingUI {

	private static final String CONFIGURATION_PATH = "src/main/java/it/polimi/ingsw/ps23/client/commons/configuration/";
	private static final String IMAGES_PATH = "src/main/java/it/polimi/ingsw/ps23/client/commons/configuration/images/";
	private static final String ICON_PATH = "victoryPoint.png";
	private static final String BACKGROUND_PATH = "mapBackground.png";
	private static final String NOBILITY_TRACK_PATH = "nobilityTrack.png";
	private static final String KING_PATH = "king.png";
	private static final String COUNCILLOR_PATH = "Councillor.png";
	private static final String PNG_EXTENSION = ".png";
	private static final String PERMISSION_CARD_PATH = "permissionCard.png";
	private static final String CITIES_POSITION_CSV = "citiesPosition.csv";
	private static final String CITIES_CONNECTION_CSV = "citiesConnection.csv";
	private static final String OBJECTS_POSITION_CSV = "objectsPosition.csv";
	private static final String SANS_SERIF_FONT = "Sans serif";
	private static final String POLITIC_CARD_PATH = "Card.png";
	private static final String BONUS_TILE_PATH = "BonusTile.png";
	private static final String ELECT_COUNCILLOR = "elect councillor";
	private static final String ACQUIRE_BUSINESS_PERMIT_TILE = "acquire business permit tile";
	private static final String ASSISTANT_TO_ELECT_COUNCILLOR = "assistant to elect councillor";
	private static final String ADDITIONAL_MAIN_ACTION = "additional main action";
	private static final String ENGAGE_ASSITANT= "engage assistant";
	private static final String CHANGE_PERMIT_TILE= "change permit tile";
	private static final String BUILD_EMPORIUM_KING= "build emporium king";
	private static final String BUILD_EMPORIUM_TILE = "build emporium permit tile";
	
	private String mapPath;
	private Map<String, Component> components;
	private Map<String, Point> councilPoints;
	private JFrame frame;
	private JPanel mapPanel;
	private JPanel mainActionPanel;
	private JPanel quickActionPanel;
	private JTable playersTable;
	private DefaultTableModel tableModel;
	private JScrollPane scrollPane;
	private String playerName;
	private RMIGUIView rmiguiView;
	private String chosenAction;

	protected SwingUI(String mapType, String playerName) {
		this.playerName = playerName;
		mapPath = CONFIGURATION_PATH + mapType + "/";
		components = new HashMap<>();
		councilPoints = new HashMap<>();
		frame = new JFrame();
		frame.setTitle("Council of Four");
		Dimension dimension = new Dimension(814, 569);
		frame.setMinimumSize(dimension);
		frame.setIconImage(readImage(IMAGES_PATH + ICON_PATH));
		BufferedImage backgroundImage = readImage(IMAGES_PATH + "woodBackground.png");
		Image resizedBackgroundImage = backgroundImage.getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
		JLabel backgroundLabel = new JLabel(new ImageIcon(resizedBackgroundImage));
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.getContentPane().setLayout(new GridLayout());
		mapPanel = new JPanel();
		mapPanel.setLayout(null);
		backgroundLabel.setBounds(0, 0, 1366, 768);
		loadUI();
		frame.getContentPane().add(mapPanel);
		mapPanel.add(scrollPane);
		mapPanel.add(backgroundLabel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected void setRmiguiView(RMIGUIView rmiguiView) {
		this.rmiguiView = rmiguiView;
	}

	public Map<String, Component> getComponents() {
		return components;
	}

	public Map<String, Point> getCouncilPoints() {
		return councilPoints;
	}

	public JFrame getFrame() {
		return frame;
	}

	public JPanel getMapPanel() {
		return mapPanel;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	protected String getChosenAction() {
		return chosenAction;
	}
	
	protected JPanel getMainActionPanel() {
		return mainActionPanel;
	}
	
	protected JPanel getQuickActionPanel() {
		return quickActionPanel;
	}

	public static String getCouncillorPath() {
		return COUNCILLOR_PATH;
	}

	public static String getPermissionCardPath() {
		return PERMISSION_CARD_PATH;
	}

	public static String getSansSerifFont() {
		return SANS_SERIF_FONT;
	}

	public static String getPoliticCardPath() {
		return POLITIC_CARD_PATH;
	}

	public static String getBonusTilePath() {
		return BONUS_TILE_PATH;
	}
	
	public static String getImagesPath() {
		return IMAGES_PATH;
	}
	
	public static String getPngExtension() {
		return PNG_EXTENSION;
	}


	protected BufferedImage readImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot find the image file.", e);
		}
		return null;
	}

	Component getComponents(String componentName) {
		return components.get(componentName);
	}

	Point getCouncilPoint(String region) {
		return councilPoints.get(region);
	}

	private void loadKing() {
		BufferedImage kingImage = readImage(IMAGES_PATH + KING_PATH);
		Image resizedKingImage = kingImage.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		JLabel kingLabel = new JLabel(new ImageIcon(resizedKingImage));
		kingLabel.setBounds(0, 0, 35, 35);
		mapPanel.add(kingLabel);
		components.put("king", kingLabel);
	}

	private void loadCouncilsPositions() {
		List<String[]> rawCouncilsPosition = new RawObject(mapPath + OBJECTS_POSITION_CSV).getRawObject();
		for (String[] rawCouncilPosition : rawCouncilsPosition) {
			Point councilPoint = new Point();
			councilPoint.x = Integer.parseInt(rawCouncilPosition[1]);
			councilPoint.y = Integer.parseInt(rawCouncilPosition[2]);
			councilPoints.put(rawCouncilPosition[0], councilPoint);
		}
	}

	private void loadCities() {
		List<String[]> rawCitiesPosition = new RawObject(mapPath + CITIES_POSITION_CSV).getRawObject();
		for (String[] rawCityPosition : rawCitiesPosition) {
			BufferedImage cityImage = readImage(IMAGES_PATH + rawCityPosition[5] + PNG_EXTENSION);
			int x = Integer.parseInt(rawCityPosition[3]);
			int y = Integer.parseInt(rawCityPosition[4]);
			int width = Integer.parseInt(rawCityPosition[1]);
			int height = Integer.parseInt(rawCityPosition[2]);
			Image resizedCityImage = cityImage.getScaledInstance(width - 8, height - 8, Image.SCALE_SMOOTH);
			JLabel cityLabel = new JLabel(new ImageIcon(resizedCityImage));
			cityLabel.setBounds(0, 0, width - 8, height - 8);
			cityLabel.setLocation(x, y);
			JLabel cityName = new JLabel();
			cityName.setBounds(0, 0, width, height);
			cityName.setLocation(x - 18, y - 38);
			cityName.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 14));
			cityName.setForeground(Color.decode(rawCityPosition[6]));
			cityName.setText(rawCityPosition[0]);
			mapPanel.add(cityName);
			mapPanel.add(cityLabel);
			components.put(rawCityPosition[0], cityLabel);
		}
	}

	private void loadStreets() {
		List<String[]> rawCitiesConnection = new RawObject(mapPath + CITIES_CONNECTION_CSV).getRawObject();
		for (String[] rawCityConnection : rawCitiesConnection) {
			BufferedImage connectionImage = readImage(IMAGES_PATH + rawCityConnection[0] + PNG_EXTENSION);
			int x = Integer.parseInt(rawCityConnection[3]);
			int y = Integer.parseInt(rawCityConnection[4]);
			int width = Integer.parseInt(rawCityConnection[1]);
			int height = Integer.parseInt(rawCityConnection[2]);
			Image resizedConnectionImage = connectionImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			JLabel connectionLabel = new JLabel(new ImageIcon(resizedConnectionImage));
			connectionLabel.setBounds(0, 0, width, height);
			connectionLabel.setLocation(x, y);
			mapPanel.add(connectionLabel);
		}
	}

	private void loadMapBackground() {
		BufferedImage mapImage = readImage(IMAGES_PATH + BACKGROUND_PATH);
		Image resizedMapImage = mapImage.getScaledInstance(800, 464, Image.SCALE_SMOOTH);
		JLabel mapLabel = new JLabel(new ImageIcon(resizedMapImage));
		mapLabel.setBounds(0, 0, 800, 464);
		mapPanel.add(mapLabel);
	}

	private void loadNobiltyTrack() {
		BufferedImage mapImage = readImage(IMAGES_PATH + NOBILITY_TRACK_PATH);
		Image resizedMapImage = mapImage.getScaledInstance(799, 66, Image.SCALE_SMOOTH);
		JLabel mapLabel = new JLabel(new ImageIcon(resizedMapImage));
		mapLabel.setBounds(0, 0, 799, 66);
		mapLabel.setLocation(0, 464);
		mapPanel.add(mapLabel);
	}

	private void loadPlayersTable() {
		int numRows = 0;
		String[] columnNames = new String[] { "Name", "Victory Points", "Coins", "Assistants", "Nobility Points" };
		tableModel = new DefaultTableModel(numRows, columnNames.length);
		tableModel.setColumnIdentifiers(columnNames);
		playersTable = new JTable(tableModel);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(playersTable);
		scrollPane.setBounds(0, 0, 567, 110);
		scrollPane.setLocation(800, 0);
	}

	private void loadRegionButtons() {
		BufferedImage seasideImage = readImage(IMAGES_PATH + "seasideRegion.png");
		JButton btnSeaside = new JButton("");
		btnSeaside.setIcon(new ImageIcon(seasideImage));
		btnSeaside.setBounds(120, 0, 50, 50);
		mapPanel.add(btnSeaside, 0);
		BufferedImage hillImage = readImage(IMAGES_PATH + "hillRegion.png");
		JButton btnHill = new JButton("");
		btnHill.setIcon(new ImageIcon(hillImage));
		btnHill.setBounds(370, 0, 50, 50);
		mapPanel.add(btnHill, 0);
		BufferedImage mountainImage = readImage(IMAGES_PATH + "mountainRegion.png");
		JButton btnMountain = new JButton("");
		btnMountain.setIcon(new ImageIcon(mountainImage));
		btnMountain.setBounds(670, 0, 50, 50);
		mapPanel.add(btnMountain, 0);
	}

	private void loadUI() {
		loadCouncilsPositions();
		loadKing();
		loadCities();
		loadStreets();
		loadMapBackground();
		loadRegionButtons();
		loadNobiltyTrack();
		loadPlayersTable();
		loadMainActionButtons();
		loadQuickActionPanel();
	}
	
	public void refreshKingPosition(String city) {
		Point point = getComponents(city).getLocationOnScreen();
		getComponents("king").setLocation(point);
	}
	
	protected void loadMainActionButtons() {
		mainActionPanel = new JPanel();
		mainActionPanel.setBounds(895, 181, 215, 272);
		mapPanel.add(mainActionPanel,0);
		mainActionPanel.setVisible(false);
		GridBagLayout gblMainActionPanel = new GridBagLayout();
		gblMainActionPanel.columnWidths = new int[]{0, 0};
		gblMainActionPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gblMainActionPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gblMainActionPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainActionPanel.setLayout(gblMainActionPanel);
		
		JButton btnAcquireBusinessPermitTile = new JButton();
		btnAcquireBusinessPermitTile.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = ACQUIRE_BUSINESS_PERMIT_TILE;
            	rmiguiView.resume();
            }
        });      
		BufferedImage acquireBusinessPermitTileImage = readImage(IMAGES_PATH + "acquireBusinessPermitTile.png");
		btnAcquireBusinessPermitTile.setIcon(new ImageIcon(acquireBusinessPermitTileImage));
		GridBagConstraints gbcbtnAcquireBusinessPermitTile = new GridBagConstraints();
		gbcbtnAcquireBusinessPermitTile.insets = new Insets(0, 0, 5, 0);
		gbcbtnAcquireBusinessPermitTile.gridx = 0;
		gbcbtnAcquireBusinessPermitTile.gridy = 0;
		mainActionPanel.add(btnAcquireBusinessPermitTile, gbcbtnAcquireBusinessPermitTile);
		
		JButton btnBuildEmporiumKing = new JButton();
		btnBuildEmporiumKing.addActionListener(new ActionListener() {
			@Override 
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = BUILD_EMPORIUM_KING;
            	rmiguiView.resume();
            }
        });
		BufferedImage buildEmporiumKingImage = readImage(IMAGES_PATH + "buildEmporiumKing.png");
		btnBuildEmporiumKing.setIcon(new ImageIcon(buildEmporiumKingImage));
		btnBuildEmporiumKing.setBounds(0, 0, 182, 54);
		GridBagConstraints gbcbtnBuildEmporiumKing = new GridBagConstraints();
		gbcbtnBuildEmporiumKing.insets = new Insets(0, 0, 5, 0);
		gbcbtnBuildEmporiumKing.gridx = 0;
		gbcbtnBuildEmporiumKing.gridy = 1;
		mainActionPanel.add(btnBuildEmporiumKing, gbcbtnBuildEmporiumKing);
		
		JButton btnElectCouncillor = new JButton();
		btnElectCouncillor.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = ELECT_COUNCILLOR;
            	rmiguiView.resume();
            }
        });
		BufferedImage electCouncillorImage = readImage(IMAGES_PATH + "electCouncillor.png");
		btnElectCouncillor.setIcon(new ImageIcon(electCouncillorImage));
		GridBagConstraints gbcbtnElectCouncillor = new GridBagConstraints();
		gbcbtnElectCouncillor.insets = new Insets(0, 0, 5, 0);
		gbcbtnElectCouncillor.gridx = 0;
		gbcbtnElectCouncillor.gridy = 2;
		mainActionPanel.add(btnElectCouncillor, gbcbtnElectCouncillor);
		
		JButton btnBuildEmporiumPermitTile = new JButton();
		btnBuildEmporiumPermitTile.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = BUILD_EMPORIUM_TILE;
            	rmiguiView.resume();
            }
        });
		BufferedImage builEmporiumPermitTileImage = readImage(IMAGES_PATH + "buildEmporiumPermitTile.png");
		btnBuildEmporiumPermitTile.setIcon(new ImageIcon(builEmporiumPermitTileImage));
		GridBagConstraints gbcbtnBuildEmporiumPermitTile = new GridBagConstraints();
		gbcbtnBuildEmporiumPermitTile.gridx = 0;
		gbcbtnBuildEmporiumPermitTile.gridy = 3;
		mainActionPanel.add(btnBuildEmporiumPermitTile, gbcbtnBuildEmporiumPermitTile);
	}
	
	protected void loadQuickActionPanel() {
		quickActionPanel = new JPanel();
		quickActionPanel.setBounds(1120, 181, 199, 272);
		mapPanel.add(quickActionPanel,0);
		quickActionPanel.setVisible(false);
		GridBagLayout gblQuickActionPanel = new GridBagLayout();
		gblQuickActionPanel.columnWidths = new int[]{0, 0};
		gblQuickActionPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gblQuickActionPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gblQuickActionPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		quickActionPanel.setLayout(gblQuickActionPanel);
		
		JButton btnEngageAssistant = new JButton();
		btnEngageAssistant.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = ENGAGE_ASSITANT;
            	rmiguiView.resume();
            }
        });
		BufferedImage engageAssistantImage = readImage(IMAGES_PATH + "engageAssistant.png");
		btnEngageAssistant.setIcon(new ImageIcon(engageAssistantImage));
		GridBagConstraints gbcbtnEngageAssistant = new GridBagConstraints();
		gbcbtnEngageAssistant.insets = new Insets(0, 0, 5, 0);
		gbcbtnEngageAssistant.gridx = 0;
		gbcbtnEngageAssistant.gridy = 0;
		quickActionPanel.add(btnEngageAssistant, gbcbtnEngageAssistant);
		
		JButton btnChangePermitsTile= new JButton();
		btnChangePermitsTile.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = CHANGE_PERMIT_TILE;
            	rmiguiView.resume();
            }
        });
		BufferedImage changePermitsTileImage = readImage(IMAGES_PATH + "changePermitsTile.png");
		btnChangePermitsTile.setIcon(new ImageIcon(changePermitsTileImage));
		GridBagConstraints gbcbtnChangePermitsTile = new GridBagConstraints();
		gbcbtnChangePermitsTile.insets = new Insets(0, 0, 5, 0);
		gbcbtnChangePermitsTile.gridx = 0;
		gbcbtnChangePermitsTile.gridy = 1;
		quickActionPanel.add(btnChangePermitsTile, gbcbtnChangePermitsTile);
		
		JButton btnAssistantToElectCouncillor = new JButton();
		btnAssistantToElectCouncillor.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = ASSISTANT_TO_ELECT_COUNCILLOR;
            	rmiguiView.resume();
            }
        });
		BufferedImage assistantToElectCouncillorImage = readImage(IMAGES_PATH + "assistantToElectCouncillor.png");
		btnAssistantToElectCouncillor.setIcon(new ImageIcon(assistantToElectCouncillorImage));
		GridBagConstraints gbcbtnAssistantToElectCouncillor = new GridBagConstraints();
		gbcbtnAssistantToElectCouncillor.insets = new Insets(0, 0, 5, 0);
		gbcbtnAssistantToElectCouncillor.gridx = 0;
		gbcbtnAssistantToElectCouncillor.gridy = 2;
		quickActionPanel.add(btnAssistantToElectCouncillor, gbcbtnAssistantToElectCouncillor);
		
		JButton btnAdditionalMainAction= new JButton();
		btnAdditionalMainAction.addActionListener(new ActionListener() {
			@Override  
            public void actionPerformed(ActionEvent e)
            {
            	chosenAction = ADDITIONAL_MAIN_ACTION; 
            	rmiguiView.resume();
            }
        });
		BufferedImage additionalMainActionImage = readImage(IMAGES_PATH + "buyMainAction.png");
		btnAdditionalMainAction.setIcon(new ImageIcon(additionalMainActionImage));
		GridBagConstraints gbcbtnAdditionalMainAction = new GridBagConstraints();
		gbcbtnAdditionalMainAction.gridx = 0;
		gbcbtnAdditionalMainAction.gridy = 3;
		quickActionPanel.add(btnAdditionalMainAction, gbcbtnAdditionalMainAction);
	}
	
}