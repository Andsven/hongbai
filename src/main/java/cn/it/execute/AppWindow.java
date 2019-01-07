package cn.it.execute;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

//import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
/**
 * 红白收视图按歌手演唱时间段划分区域 需要三个文件:data.txt config.properties img.jpg data.txt
 * 视频中每组歌手演唱时间段 每组歌手一行数据(文件名必须含有"data"字符串) config.properties
 * 描绘收视图过程中需要的各种设定(文件名必须含有"config"字符串) img.jpg 提前处理过的收视图原图,必须保持时间轴水平
 * 点击run按钮生成输出图片,输出位置在桌面,文件名为out.jpg,已有重名文件则按out_1 out_2依次重命名
 * 
 * @author golafun
 * @since 2018/12/18
 * @version 0.1
 *
 */
public class AppWindow extends JFrame {
	private JPanel contentPanel;
	// 声明一个 JFileChooser 对象
	private JLabel dataFileMsg;
	private JLabel configFileMsg;
	private JLabel imgFileMsg;

	private JLabel msgLabel;
	private JButton dataFileButton;
	private JButton configFileButton;
	private JButton imgFileButton;
	private File dataFile;
	private File imgFile;
	private File configFile;
	
	private String oldDirPath="";

	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow frame = new AppWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public AppWindow() throws HeadlessException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IOException {
		super();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		// 将 chooser 实例化
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		contentPanel = new JPanel();
//		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new GridLayout(5, 1));

		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(1, 2));
		dataFileButton = (JButton) createFileButton("choose data file");
		p1.add(dataFileButton);
		dataFileMsg = new JLabel("path:");
		dataFileMsg.setSize(200, 0);
		dataFileMsg.setBounds(30, 10, 100, 10);
		p1.add(dataFileMsg);
		contentPanel.add(p1);

		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(1, 2));
		configFileButton = (JButton) createFileButton("choose config file");
		p2.add(configFileButton);
		configFileMsg = new JLabel("path:");
		configFileMsg.setSize(200, 0);
		configFileMsg.setBounds(30, 40, 100, 40);
		p2.add(configFileMsg);
		contentPanel.add(p2);

		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(1, 2));
		imgFileButton = (JButton) createFileButton("choose img file");
		p3.add(imgFileButton);
		imgFileMsg = new JLabel("path:");
		imgFileMsg.setSize(200, 0);
		imgFileMsg.setBounds(30, 70, 100, 70);
		p3.add(imgFileMsg);
		contentPanel.add(p3);

		JButton uploadDirButton = createDirButton();
		contentPanel.add(uploadDirButton);

		JPanel p4 = new JPanel();
		p4.setBorder(new EmptyBorder(5, 5, 5, 5));
		p4.setLayout(new GridLayout(1, 2));
		JButton runButton = new JButton("run!");
		runButton.setBounds(2, 2, 20, 10);
		runButton.addMouseListener(new RunButtonMouseAdapter());
		runButton.setEnabled(false);
		p4.add(runButton);
		msgLabel = new JLabel("Msg:");
		p4.add(msgLabel);
		contentPanel.add(p4);

	}

	class RunButtonMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {

			PaintExecutor paintExecutor = new PaintExecutor();
			try {
				paintExecutor.run(dataFile, configFile, imgFile);
				msgLabel.setText("paint jpg OK");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private Component createFileButton(String msg) {
		JButton button = new JButton(msg);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("C:\\Users\\Administrator\\Desktop"));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设定只能选择到文件
				int state = fileChooser.showOpenDialog(getContentPane());// 此句是打开文件选择器界面的触发语句
				if (state == 1) {
					return;// 撤销则返回
				} else {
					File file = fileChooser.getSelectedFile();// f为选择到的文件
					if (file.getName().contains("data") && msg.contains("data")) {
						dataFile = file;
						JlabelSetText(msgLabel, file.getName() + " uploaded susseceful");
						JlabelSetText(dataFileMsg, "path:" + file.getAbsolutePath());
					} else if (file.getName().contains("config") && msg.contains("config")) {
						configFile = file;
						JlabelSetText(msgLabel, file.getName() + " uploaded susseceful");
						JlabelSetText(configFileMsg, "path:" + file.getAbsolutePath());
					} else if (file.getName().contains("img") && msg.contains("img")) {
						imgFile = file;
						JlabelSetText(msgLabel, file.getName() + " uploaded susseceful");
						JlabelSetText(imgFileMsg, "path:" + file.getAbsolutePath());
					} else {
						JlabelSetText(msgLabel, "FILENAME MUST CONTAINTS data/config/img");
					}
				}
				if(dataFile!=null &&dataFile!=null && configFile!=null) {
					
				}
			}
		});
		return button;
	}

	private JButton createDirButton() {
		JButton button = new JButton("upload Dir with Setting files");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if("".contentEquals(oldDirPath)) {
					fileChooser.setCurrentDirectory(new File("C:\\Users\\Administrator\\Desktop"));
				}else {
					fileChooser.setCurrentDirectory(new File(oldDirPath));
				}
				
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设定只能选择到文件
				int state = fileChooser.showOpenDialog(getContentPane());// 此句是打开文件选择器界面的触发语句
				if (state == 1) {
					return;// 撤销则返回
				} else {
					File dir = fileChooser.getSelectedFile();
					oldDirPath = dir.getAbsolutePath();
					File[] listFiles = dir.listFiles();
					for (File f : listFiles) {
						if (f.getName().contains("data")) {
							dataFile = f;
							JlabelSetText(dataFileMsg, "path:" + f.getAbsolutePath());
						}
						if (f.getName().contains("config")) {
							configFile = f;
							JlabelSetText(configFileMsg, "path:" + f.getAbsolutePath());
						}
						if (f.getName().contains("img")) {
							imgFile = f;
							JlabelSetText(imgFileMsg, "path:" + f.getAbsolutePath());
						}
						if (dataFile != null && configFile != null && imgFile != null) {
							JlabelSetText(msgLabel, "SUCCESSFUL! all setting files have been uploaded ");
						} else {
							JlabelSetText(msgLabel, "ERROR! setting files not exist!");
						}
					}

				}
			}
		});
		return button;
	}

	void JlabelSetText(JLabel jLabel, String longString) {
		StringBuilder builder = new StringBuilder("<html>");
		char[] chars = longString.toCharArray();
		FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
		int start = 0;
		int len = 0;
		while (start + len < longString.length()) {
			while (true) {
				len++;
				if (start + len > longString.length())
					break;
				if (fontMetrics.charsWidth(chars, start, len) > jLabel.getWidth()) {
					break;
				}
			}
			builder.append(chars, start, len - 1).append("<br/>");
			start = start + len - 1;
			len = 0;
		}
		builder.append(chars, start, longString.length() - start);
		builder.append("</html>");
		jLabel.setText(builder.toString());
	}
}