package com.jcloisterzone.ui.controls;

import static com.jcloisterzone.ui.controls.ControlPanel.ACTIVE_BG_COLOR;
import static com.jcloisterzone.ui.controls.ControlPanel.BG_COLOR;
import static com.jcloisterzone.ui.controls.ControlPanel.CORNER_DIAMETER;
import static com.jcloisterzone.ui.controls.ControlPanel.PANEL_WIDTH;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.jcloisterzone.Player;
import com.jcloisterzone.TradeResource;
import com.jcloisterzone.figure.Follower;
import com.jcloisterzone.figure.SmallFollower;
import com.jcloisterzone.figure.Special;
import com.jcloisterzone.game.expansion.AbbeyAndMayorGame;
import com.jcloisterzone.game.expansion.BridgesCastlesBazaarsGame;
import com.jcloisterzone.game.expansion.KingAndScoutGame;
import com.jcloisterzone.game.expansion.TowerGame;
import com.jcloisterzone.game.expansion.TradersAndBuildersGame;
import com.jcloisterzone.ui.Client;
import com.jcloisterzone.ui.UiUtils;

public class PlayerPanel extends FakeComponent {

    //private static final Color DELIM_TOP_COLOR = new Color(100,100,100);
    //private static final Color DELIM_BOTTOM_COLOR = new Color(160,160,160);
    private static final Color DELIM_TOP_COLOR = new Color(250,250,250);
    private static final Color DELIM_BOTTOM_COLOR = new Color(220,220,220);

    private static Font FONT_POINTS = new Font("Georgia", Font.BOLD, 30);
    private static Font FONT_NICKNAME = new Font("Georgia", Font.BOLD, 18);
    private static Font FONT_MEEPLE = new Font("Georgia", Font.BOLD, 18);

//    private static Font FONT_POINTS = new Font(null, Font.BOLD, 30);
//    private static Font FONT_NICKNAME = new Font(null, Font.BOLD, 18);
//    private static Font FONT_MEEPLE = new Font(null, Font.BOLD, 18);;

    private static final int PADDING_L = 9;
    private static final int PADDING_R = 11;
    private static final int LINE_HEIGHT = 32;
    private static final int DELIMITER_Y = 34;

    private final Client client;
    private final Player player;
    private final Color color;

    private final PlayerPanelImageCache cache;

    //context coordinates variables
    private int bx, by;

    public PlayerPanel(Client client, Player player, PlayerPanelImageCache cache) {
        this.client = client;
        this.player = player;
        this.cache = cache;
        this.color = client.getPlayerColor(player);
    }

    private void drawDelimiter(Graphics2D g2, int y) {
        g2.setColor(DELIM_TOP_COLOR);
        g2.drawLine(PADDING_L, y, PANEL_WIDTH /*-PADDING_R*/, y);
        g2.setColor(DELIM_BOTTOM_COLOR);
        g2.drawLine(PADDING_L, y+1, PANEL_WIDTH /*-PADDING_R*/, y+1);
    }

    private void drawTextShadow(Graphics2D g2, String text, int x, int y) {
        //TODO shadow color based on color ??
        /*g2.setColor(Color.DARK_GRAY);
        g2.drawString(text, x+0.8f, y+0.7f);*/
        g2.setColor(ControlPanel.SHADOW_COLOR);
        g2.drawString(text, x+0.6f, y+0.5f);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    private void drawMeepleBox(Graphics2D g2, Player playerKey, String imgKey, int count) {
        drawMeepleBox(g2, playerKey, imgKey, count, false);
    }

    private void drawMeepleBox(Graphics2D g2, Player playerKey, String imgKey, int count, boolean showOne) {
        if (count == 0) return;

        int w = 30;
        if (count > 1 || (count == 1 && showOne)) {
            w = count < 10 ? 47 : 60;
        }
        int h = 22;
        if (bx+w > PANEL_WIDTH-PADDING_R) {
            bx = PADDING_L;
            by += LINE_HEIGHT;
        }
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(bx, by, w, h, 8, 8);
        g2.drawImage(cache.get(playerKey, imgKey), bx, by-4, null);

        if (count > 1 || (count == 1 && showOne)) {
            g2.setColor(Color.BLACK);
            g2.drawString(""+count, bx+LINE_HEIGHT, by+17);
        }
        bx += w + 8;
    }



    @Override
    public void paintComponent(Graphics2D parentGraphics) {

//		GridPanel gp = client.getGridPanel();

        boolean isActive = client.getGame().getActivePlayer() == player;
        boolean playerTurn = client.getGame().getTurnPlayer() == player;

//		gp.profile(" > get flags");

        BufferedImage bimg = UiUtils.newTransparentImage(PANEL_WIDTH, 200);
        Graphics2D g2 = bimg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//		gp.profile(" > create buffer");

        drawDelimiter(g2, DELIMITER_Y);

        g2.setFont(FONT_POINTS);
        drawTextShadow(g2, ""+player.getPoints(), PADDING_L, 27);

        g2.setFont(FONT_NICKNAME);
        drawTextShadow(g2, player.getNick(), 78, 27);

//		gp.profile(" > nick & score");

        g2.setFont(FONT_MEEPLE);
        bx = PADDING_L;
        by = 43;

        int small = 0;
        String smallImgKey = SmallFollower.class.getSimpleName();
        for(Follower f : player.getFollowers()) {
            if (!f.isDeployed()) {
                if (f instanceof SmallFollower) {
                    small++;
                } else { //all small followers are at beginning of collection
                    drawMeepleBox(g2, player, smallImgKey, small, true);
                    small = 0;
                    drawMeepleBox(g2, player, f.getClass().getSimpleName(), 1);
                }
            }
        }
        drawMeepleBox(g2, player, smallImgKey, small, true); //case when only small followers are in collection (not drawn yet)

//		gp.profile(" > followers");

        for(Special meeple : player.getSpecialMeeples()) {
            drawMeepleBox(g2, player, meeple.getClass().getSimpleName(), 1);
        }

//		gp.profile(" > special");

        AbbeyAndMayorGame ab = client.getGame().getAbbeyAndMayorGame();
        TowerGame tower = client.getGame().getTowerGame();
        BridgesCastlesBazaarsGame bcb = client.getGame().getBridgesCastlesBazaarsGame();
        KingAndScoutGame ks = client.getGame().getKingAndScoutGame();
        TradersAndBuildersGame tb = client.getGame().getTradersAndBuildersGame();


        if (ab != null) {
            drawMeepleBox(g2, null, "abbey", ab.hasUnusedAbbey(player) ? 1 : 0);
        }

        if (tower != null) {
            drawMeepleBox(g2, null, "towerpiece", tower.getTowerPieces(player), true);
        }

        if (bcb != null) {
            drawMeepleBox(g2, null, "bridge", bcb.getPlayerBridges(player), true);
            drawMeepleBox(g2, null, "castle", bcb.getPlayerCastles(player), true);
        }


        if (ks != null) {
            if (ks.getKing() == player) {
                drawMeepleBox(g2, null, "king", 1);
            }
            if (ks.getRobberBaron() == player) {
                drawMeepleBox(g2, null, "robber", 1);
            }
        }
        if (tb != null) {
            drawMeepleBox(g2, null, "cloth", tb.getTradeResources(player, TradeResource.CLOTH), true);
            drawMeepleBox(g2, null, "grain", tb.getTradeResources(player, TradeResource.GRAIN), true);
            drawMeepleBox(g2, null, "wine", tb.getTradeResources(player, TradeResource.WINE), true);
        }
        if (tower != null) {
            List<Follower> capturedFigures = tower.getPrisoners().get(player);
            Map<Class<? extends Follower>, Integer> groupedByType;
            if (!capturedFigures.isEmpty()) {
                groupedByType = Maps.newHashMap();
                for(Player opponent : client.getGame().getAllPlayers()) {
                    if (opponent == player) continue;
                    for (Follower f : capturedFigures) {
                        if (f.getPlayer() == opponent) {
                            Integer prevVal = groupedByType.get(f.getClass());
                            groupedByType.put(f.getClass(), prevVal == null ? 1 : prevVal+1);
                        }
                    }
                    for (Entry<Class<? extends Follower>, Integer> entry : groupedByType.entrySet()) {
                        drawMeepleBox(g2, opponent, entry.getKey().getSimpleName(), entry.getValue());
                    }
                    groupedByType.clear();
                }
            }
        }

//		gp.profile(" > expansions");


//		//debug counts
//		drawMeepleBox(g2, BigFollower.class, 1);
//		drawMeepleBox(g2, Builder.class, 1);
//		drawMeepleBox(g2, Pig.class, 1);
//		drawMeepleBox(g2, Mayor.class, 1);
//		drawMeepleBox(g2, Wagon.class, 1);
//		drawMeepleBox(g2, Barn.class, 1);
//		drawMeepleBox(g2, client.getFigureTheme().getNeutralImage("towerpiece"), 5);
//		drawMeepleBox(g2, client.getFigureTheme().getNeutralImage("king"), 1);
//		drawMeepleBox(g2, client.getFigureTheme().getNeutralImage("robber"), 1);
//		drawMeepleBox(g2, client.getFigureTheme().getNeutralImage("bridge"), 3);
//		drawMeepleBox(g2, client.getFigureTheme().getNeutralImage("castle"), 3);

        int realHeight = by + (bx > PADDING_L ? LINE_HEIGHT : 0);

        if (isActive) {
            //TODO
            //parentGraphics.setColor(Color.BLACK);
            //parentGraphics.fillRoundRect(0, -5, PANEL_WIDTH+CORNER_DIAMETER, realHeight+10, CORNER_DIAMETER, CORNER_DIAMETER);
        }

        parentGraphics.setColor(playerTurn ? ACTIVE_BG_COLOR : BG_COLOR);
        parentGraphics.fillRoundRect(0, 0, PANEL_WIDTH+CORNER_DIAMETER, realHeight, CORNER_DIAMETER, CORNER_DIAMETER);

        parentGraphics.drawImage(bimg, 0, 0, PANEL_WIDTH, realHeight, 0, 0, PANEL_WIDTH, realHeight, null);
        parentGraphics.translate(0, realHeight + 12); //add also padding

//		gp.profile(" > complete");
    }

//	 form legacy
//
//	  class PayRansomListener extends MouseAdapter {
//		/* nez delat nejaky buttony bude jednodussi listener a odpocet ze
//		 * souradnic, zejmena protoze figurky se kresli ze dvou obrazku
//		 * a buttony nebo JLabely by byly take slozitejsi
//		 */
//
//		@Override
//		public void mouseClicked(MouseEvent e) {
//			//vyska figurky je 19px
//			if (e.getButton() != MouseEvent.BUTTON1) return;
//			for(Entry<Rectangle, Class<? extends Follower>> entry : ransomClickable.entrySet()) {
//				Rectangle rect = entry.getKey();
//				if (rect.contains(e.getX(), e.getY())) {
//					client.getServer().payRansom(p.getIndex(), entry.getValue());
//				}
//			}
//		}
//	}

//	if (game.hasExpansion(Expansion.TOWER)) {
//		TowerGame tg = game.getTowerGame();
//
//		ransomClickable.clear();
//		g2.setFont(resourceFont);
//		int towerPieces = game.getTowerGame().getTowerPieces(p);
//		g2.drawString((towerPieces < 10 ? " ":"") + towerPieces + "", 5, TOWER_Y + 16);
//		g2.drawImage(figures.get(PanelImages.TOWER_PIECE), 30, TOWER_Y, null);
//
//		//TODO comment bez nahrady - captured v panelu
//
//		List<Follower> capturedFigures = tg.getPrisoners().get(p);
//		int x = TOWER_FIRST_CAPTURED_X;
//		for(Follower f : capturedFigures) {
//			int playerIndex = f.getPlayer().getIndex();
//			boolean canPayRansom = client.isClientActive() &&
//							client.getGame().getTurnPlayer().getIndex() == playerIndex &&
//							! client.getGame().getTowerGame().isRansomPaidThisTurn();
//
//			Color color = client.getPlayerColor(p);
//			Image figImage = client.getFigureTheme().getFigureImage(f, color);
//			//Image img = figImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
//			//img = new ImageIcon(img).getImage();
//
//			g2.drawImage(figImage, x, TOWER_Y, TOWER_FIGURE_WIDTH, TOWER_FIGURE_WIDTH, null);
//			if (canPayRansom) {
//				g2.setColor(Color.BLACK);
//				g2.drawRect(x, TOWER_Y-1, TOWER_FIGURE_WIDTH, TOWER_FIGURE_WIDTH);
//				ransomClickable.put(new Rectangle(x, TOWER_Y, TOWER_FIGURE_WIDTH, TOWER_FIGURE_WIDTH), f.getClass());
//			}
//			x += TOWER_FIGURE_WIDTH + 2;
//
//		}
//	}


}
