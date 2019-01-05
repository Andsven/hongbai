package cn.it.execute;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

import cn.it.entity.Block;
import cn.it.entity.Config;
import cn.it.entity.ReferencePoint;
import cn.it.util.Utils;

public class PaintExecutor {

	/*
	 * private static final int UP = 0; private static final int SELF = 1; private
	 * static final int DOWN = 2;
	 */
	private int totalHeight = 0;
	private int totalWidth = 0;
	private int countOfDurationText = 0;
	private int timeMarkerindex = 0;// 统计时间刻度数量 每5分钟加长刻度线

	@Test
	/**
	 * 运行绘图程序
	 * 
	 * @throws IOException
	 */
	public void run(File dataFile, File configFile, File imgFile) throws IOException {
		// File configFile = new File("src/testconfig.properties");
		Utils.initialConfig(configFile);
		// File dataFile = new File("src/testdata.txt");
		List<Block> initialData = Utils.initialData(dataFile);
		Utils.calStartAndEndIndexOfAllBlock(initialData, Utils.config.getReferencePointList());
		// try (FileInputStream fin = new FileInputStream("src/source20171231.jpg")) {
		try (FileInputStream fin = new FileInputStream(imgFile)) {
			BufferedImage image = ImageIO.read(fin);
			totalHeight = image.getHeight();
			totalWidth = image.getWidth();
			Graphics2D g2d = image.createGraphics();
			// 绘制灰色背景
			if (Utils.config.isPaintBackGround()) {
				paintBackground(g2d, Utils.config.getYIndexOfBlockDurationText() - 5, 25);
				paintBackground(g2d, Utils.config.getYIndexOfTimeMarker(), 12);
				int blockBGlength = Utils.config.getBlockBGlength();
				paintBackground(g2d, Utils.config.getYIndexOfBlockArtistText()-13+blockBGlength/2, blockBGlength);
			}

			for (Block block : initialData) {
				paintBlock(g2d, block);
			}
			for (Block block : initialData) {
				paintBlockDuration(g2d, block);
			}
			drawTimeMarker(g2d, Utils.config.getReferencePointList());
			// 绘制水平线
			// drawHorizonLine(g2d);
			g2d.dispose();
			String outFilePath = "C:/Users/Administrator/Desktop/";// 输出到桌面
			String ofileName = "out"; // 输出文件名
			int index = 1;
			File outputFile = new File(outFilePath + ofileName + ".jpg");
			while (outputFile.exists()) {
				outputFile = new File(outFilePath + ofileName + "_" + index + ".jpg");// 重命名情况
				index++;
			}
			try (OutputStream fos = new FileOutputStream(outputFile)) {
				ImageIO.write(image, "jpg", fos);
				fos.flush();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 绘制block上方的持续时间
	 * 
	 * @param g2d
	 * @param block
	 */
	private void paintBlockDuration(Graphics2D g2d, Block block) {

		int YIndexOfDurationText = Utils.config.getYIndexOfBlockDurationText();
		if (countOfDurationText % 2 == 0) {
			YIndexOfDurationText += 6;
		} else {
			YIndexOfDurationText -= 6;
		}
		countOfDurationText++;
		g2d.setFont(new Font(Font.SERIF, Font.PLAIN, Utils.config.getSizeOfDurationText()));
		g2d.drawString(block.getDuration(), block.getCorrectStartXIndex() - 2, YIndexOfDurationText);
	}

	/**
	 * 绘制灰色背景
	 * 
	 * @param g2d
	 * @param yIndex 背景中心位置
	 * @param length 背景所占的高度
	 */
	private void paintBackground(Graphics2D g2d, int yIndex, int length) {
		g2d.setColor(Color.DARK_GRAY);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Utils.config.getTransparencyOfRECT());
		g2d.setComposite(ac);
		g2d.fillRect(0, yIndex - length / 2, totalWidth, length);
	}

	/**
	 * 绘制时间刻度
	 * 
	 * @param g2d
	 */
	private void drawTimeMarker(Graphics2D g2d, List<ReferencePoint> rpList) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0));
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < rpList.size() - 1; i++) {
			drawTimeMarkBtw2Point(g2d, rpList.get(i), rpList.get(i + 1));
		}
	}

	/**
	 * 描绘两个点之间的时间刻度
	 * 
	 * @param g2d
	 * @param rp1
	 * @param rp2
	 */
	private void drawTimeMarkBtw2Point(Graphics2D g2d, ReferencePoint rp1, ReferencePoint rp2) {
		int count = (rp2.getTransferedTime() - rp1.getTransferedTime()) / 60;
		for (int i = 0; i < count; i++) {
			drawOneTimeMarker(g2d, rp1, i);
		}
	}

	/**
	 * 绘制圖上方的一个时间刻度
	 * 
	 * @param g2d
	 * @param x   刻度横坐标位置
	 * @param i   第几个刻度
	 */
	private void drawOneTimeMarker(Graphics2D g2d, ReferencePoint rp, int i) {
		int x = (int) (rp.getPixel() + i * rp.getRightPixelsPerSecond() * 60);

		int y = Utils.config.getYIndexOfTimeMarker();
		if (timeMarkerindex % 5 == 0) {
			g2d.drawLine(x, y - 3, x, y + 3);
		} else {
			g2d.drawLine(x, y - 1, x, y + 1);
		}
		timeMarkerindex++;
	}

	/**
	 * 绘制切分区间
	 * 
	 * @param g2d
	 * @param ele
	 */
	private void paintBlock(Graphics2D g2d, Block block) {
		paintBlockBG(g2d, block);
		paintBlockText(g2d, block);
	}

	private void paintBlockBG(Graphics2D g2d, Block block) {
		int x1 = block.getCorrectStartXIndex();
		int x2 = block.getCorrectEndXIndex();
		// 设置矩形背景颜色
		Color color = null;
		if (block.getTeam() == Block.Team.RED) {
			color = Color.RED;
		} else if (block.getTeam() == Block.Team.WHITE) {
			color = Color.BLUE;
		} else if (block.getTeam() == Block.Team.GREEN) {
			color = Color.GREEN;
		} else {
			color = Color.ORANGE;
		}
		g2d.setColor(color);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Utils.config.getTransparencyOfRECT());
		g2d.setComposite(ac);
		g2d.fillRect(x1, 0, x2 - x1, totalHeight);
	}

	private void paintBlockText(Graphics2D g2d, Block block) {
		// 设置绘制文字
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0));
		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Utils.config.getSizeOfArtistText()));
		// 设置字体颜色
		g2d.setColor(Color.WHITE);
		String str = block.getArtist();
		int y = Utils.config.getYIndexOfBlockArtistText();
		// 获取字体高度,宽度
		int h = g2d.getFontMetrics().getHeight() - 2;
		int w1 = ((block.getCorrectEndXIndex() - block.getCorrectStartXIndex()) - 15) / 2;
		for (int i = 0; i < str.length(); i++) {
			g2d.drawString(String.valueOf(str.charAt(i)), block.getCorrectStartXIndex() + w1, y);
			y += h;
		}
	}

	// 绘制水平线
	/*
	 * private void drawHorizonLine(Graphics2D g2d) { Point p =
	 * Utils.getInitialData();
	 * 
	 * int upNum = Integer.valueOf(Utils.getConfig().getProperty("upNum")); int
	 * downNum = Integer.valueOf(Utils.getConfig().getProperty("downNum"));
	 * drawLine(g2d, p, Client.UP, upNum); //在P点开始往上画N条 drawLine(g2d, p,
	 * Client.SELF, 1); //在P画N条 drawLine(g2d, p, Client.DOWN, downNum); //在P点往下画N条 }
	 */

	/*
	 * private void drawLine(Graphics2D g2d, Point p, int mode, int count) {
	 * 
	 * float initValue = Float.valueOf(p.getDiscription());
	 * 
	 * // 设置虚线 Stroke dash = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_MITER, 3.5f, new float[] { 15, 10, }, 0f);
	 * g2d.setStroke(dash);
	 * 
	 * int initIndex = p.getX(); if (mode == Client.SELF) { drawOneLine(g2d,
	 * initIndex, p.getDiscription()); return; }
	 * 
	 * int isUp = (mode == 0 ? -1 : 1); for (int i = 0; i < count; i++) { initValue
	 * -= isUp; if (initValue % 5 == 0) { g2d.setColor(Color.ORANGE); } else {
	 * g2d.setColor(Color.WHITE); } String str = String.valueOf((int) initValue) +
	 * ".0"; initIndex += (isUp * p.getWidth()); drawOneLine(g2d, initIndex, str); }
	 * }
	 */
	/*
	 * private void drawOneLine(Graphics2D g2d, int y, String str) {
	 * g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)
	 * 1.0)); g2d.drawString(str, 20, y + 4); g2d.drawString(str, totalWidth - 30, y
	 * + 4); g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
	 * (float) 0.7)); g2d.drawLine(50, y, totalWidth - 30, y); }
	 */
}
