package org.ia0l6.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ia0l6.bean.MouseType;
import org.ia0l6.bean.ThemeType;
import org.ia0l6.bean.Config;
import org.ia0l6.macro.*;
import org.ia0l6.utils.GsonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/****
 *** author：lao
 *** package：org.example
 *** project：JNAMacro
 *** name：MainFrame
 *** date：2023/10/16  20:33
 *** filename：Main
 *** desc：侧键炼狱，侧键USP 不可以自定义延迟
 ***     采用高斯分布来模拟人类左手和右手的延迟
 ***/

public class MainWindow extends JFrame implements WindowListener, NativeMouseInputListener,
        NativeKeyListener {
    private static final Logger logger = LogManager.getLogger(MainWindow.class);
    private JPanel jPanel;
    static JLabel jl_state;
    private final static String title = "行囊助手";
    private final static String version = "v24.01.01";
    private Config configBean = null;
    private final static String fileName = "config.json";
    //总开关
    private static boolean enable = false;
    private GaussianPurgatory pThread = null;
    private GaussianUSP uspThread = null;
    private Knife knifeThread = null;
    private int height = 350;
    private int width = 300;
    private MouseType mouseType = MouseType.SIDE;
    private int select = 0;

//    private boolean alt = true;

    public MainWindow(String title) throws IOException {
        // 将事件调度程序设置为swing-safe执行器服务。
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/crossfire.png"));
        setIconImage(image);
        this.setSize(height, width);
        this.setTitle(title);
        this.setLocationRelativeTo(null);
        this.setLayout(null);           //设置为空布局
        this.setResizable(false);
        this.setVisible(true);
        addWindowListener(this);
        //设置默认关闭操作，不退出程序隐藏窗口
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        Frame frame = this;
        //设置菜单栏
        initMenuBar();
        //创建托盘图标
        createTrayIcon(frame);
        //初始化面板
        initPanel();
    }


    // 初始化菜单栏
    private void initMenuBar()
    {
        // 创建菜单栏并添加到 Frame 对象中
        JMenuBar jBar = new JMenuBar();
        this.setJMenuBar(jBar);

        // 创建菜单并添加到菜单栏中
        JMenu cmenu = new JMenu("主题");
        jBar.add(cmenu);
        ThemeType theme[] = ThemeType.values();
        Arrays.stream(theme).forEach(t -> {
            JMenuItem menuItem = new JMenuItem(t.toString().replace("IJTheme", "").replace("Flat", ""));
            menuItem.addActionListener(e -> {
                try {
                    switchTheme(t);
                } catch (IOException ex) {
                    logger.error(ex);
                    throw new RuntimeException(ex);
                }
            });
            cmenu.add(menuItem);
        });
        JMenu menu = new JMenu("关于");

        JMenuItem docItem = new JMenuItem("使用教程");
        docItem.addActionListener(e -> {
            String url = "https://docs.qq.com/doc/DUG5mbElJd29Ud3hJ";
            openUrl(url);
        });
        jBar.add(menu);

        JMenuItem qItem = new JMenuItem("开源地址");
        qItem.addActionListener(e -> {
            //QQ聊天窗口
            String url = "https://github.com/laojichao/CrossFireMacro";
            openUrl(url);
        });

        JMenuItem vItem = new JMenuItem(version);
        menu.add(docItem);
        menu.add(qItem);
        menu.add(vItem);
    }

    private void initPanel() throws IOException {
        jPanel = new JPanel();
        jPanel.setBounds(0, 0 , height, width);
//        jPanel.setBackground(Color.lightGray);
        jPanel.setLayout(null);
        this.add(jPanel);
        Font font = new Font("Serif", Font.TRUETYPE_FONT, 18);
        JLabel jl_tips = new JLabel("<html>鼠标滚轮按下开启或关闭<br>侧键炼狱和USP(左键炼狱)</html>");
        jl_tips.setFont(font);
        jl_tips.setBounds(10, 10, 300, 60);

        jl_state = new JLabel("运行状态：已关闭");
        jl_state.setFont(font);
        jl_state.setForeground(Color.RED);
        jl_state.setBounds(10, 70, 300, 30);
        JLabel jl_warming = new JLabel("鼠标宏有风险，使用请谨慎");
        jl_warming.setForeground(Color.cyan);
        jl_warming.setFont(font);
        jl_warming.setBounds(10, 100, 300, 30);
        JLabel jl_select = new JLabel("侧键和左键炼狱不共存，只生效一个");
        jl_select.setFont(font);
        jl_select.setBounds(10, 130, 300, 30);
        JLabel jl_type = new JLabel("版本选择:");
        jl_type.setFont(font);
        jl_type.setBounds(10, 170, 100, 30);
        JComboBox jcb_type = new JComboBox();
        jcb_type.addItem("侧键炼狱");
        jcb_type.addItem("左键炼狱(改开火键K)");
        JLabel jl_select_type = new JLabel("侧键选择:");
        jl_select_type.setFont(font);
        jl_select_type.setBounds(10, 210, 100, 30);
        JComboBox jcb_select_type = new JComboBox();
        jcb_select_type.addItem("USP");
        jcb_select_type.addItem("炼狱快刀");
        jcb_select_type.setBounds(100, 210, 150, 30);

        File file = new File(fileName);
        if (file.exists()) {
            configBean = GsonUtils.read(fileName, Config.class);
            mouseType = configBean.getType();
            select = configBean.getSelected();
            int type = mouseType == MouseType.SIDE ? 0 : 1;
            jcb_type.setSelectedIndex(type);
            switchTheme(configBean.getThemeType());
            jcb_select_type.setSelectedIndex(select);
        } else {
            configBean = new Config(MouseType.SIDE, 0, ThemeType.FlatGitHubIJTheme);
//            GsonUtils.write(fileName, configBean);
            jcb_type.setSelectedIndex(0);
            jcb_select_type.setSelectedIndex(0);
            switchTheme(configBean.getThemeType());
        }


        jcb_type.addActionListener(e -> {
            int type = jcb_type.getSelectedIndex();
            mouseType = type == 0 ? MouseType.SIDE : MouseType.LEFT;
            configBean.setType(mouseType);
            try {
                GsonUtils.write(fileName, configBean);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        jcb_select_type.addActionListener(e -> {
            select = jcb_select_type.getSelectedIndex();
            configBean.setSelected(select);
            try {
                GsonUtils.write(fileName, configBean);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        jcb_type.setBounds(100, 170, 150, 30);

        jPanel.add(jl_tips);
        jPanel.add(jl_state);
        jPanel.add(jl_warming);
        jPanel.add(jl_select);
        jPanel.add(jl_type);
        jPanel.add(jcb_type);
        jPanel.add(jl_select_type);
        jPanel.add(jcb_select_type);
        this.add(jPanel);

    }


    /**
     * 主函数
     * @param args
     */
    public static void main(String[] args) {
        // 设置主题
        FlatIntelliJLaf.setup();
//        UIManager.put("TextComponent.arc", 5);
//        UIManager.put("Component.focusWidth", 1);
//        UIManager.put("Component.innerFocusWidth", 1);
//        UIManager.put("Button.innerFocusWidth", 1);
//        UIManager.put("TitlePane.unifiedBackground", true);
//        UIManager.put("TitlePane.menuBarEmbedded", true);
        // 设置字体，设置字体抗锯齿
//        System.setProperty("awt.useSystemAAFontSettings", "on");
//        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow windows = new MainWindow(title);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public void nativeMousePressed(NativeMouseEvent e) {

        if (enable) {
            if (mouseType == MouseType.LEFT && e.getButton() == NativeMouseEvent.BUTTON1 && pThread == null) {
                //左键炼狱
                pThread = new GaussianPurgatory();
                pThread.setMouseType(mouseType);
                pThread.start();
            } else if (e.getButton() == NativeMouseEvent.BUTTON4) {
                //侧键4
                if (select == 0 && uspThread == null) {
                    //侧键USP
                    System.out.println("USP");
                    uspThread = new GaussianUSP();

                    uspThread.setMouseType(mouseType);
                    uspThread.start();
                } else if (select == 1 && knifeThread == null) {
                    //侧键快刀
                    System.out.println("快刀");
                    knifeThread = new Knife();
                    knifeThread.setMouseType(mouseType);
                    knifeThread.start();
                }

            } else if (mouseType == mouseType.SIDE && e.getButton() == NativeMouseEvent.BUTTON5 && pThread == null) {
                System.out.println("侧键");
                pThread = new GaussianPurgatory();
                pThread.setMouseType(mouseType);
                pThread.start();
            }
        }

        if (e.getButton() == NativeMouseEvent.BUTTON3) {
            enable = !enable;
            if (enable) {
                System.out.println("开关已经打开");
                updateResult("运行状态：已开启");
            } else  {
                System.out.println("开关已经关闭");
                updateResult("运行状态：已关闭");
            }
        }

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (mouseType == MouseType.LEFT && e.getButton() == NativeMouseEvent.BUTTON1 && pThread != null && pThread.isAlive()) {
            pThread.stopMacro();
            pThread = null;
        } else if (e.getButton() == NativeMouseEvent.BUTTON4) {
            if (select == 0 &&  uspThread != null && uspThread.isAlive()) {
                uspThread.stopMacro();
                uspThread = null;
            } else if (select == 1 && knifeThread!= null && knifeThread.isAlive()) {
                knifeThread.stopMacro();
                knifeThread = null;
            }

        } else if (mouseType == MouseType.SIDE && e.getButton() == NativeMouseEvent.BUTTON5 && pThread != null && pThread.isAlive()) {
            pThread.stopMacro();
            pThread = null;
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
//        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_ALT) {
//            alt = false;
//            System.out.println(nativeEvent.getModifiers());
//        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        //鼠标移动监听器
        GlobalScreen.addNativeMouseMotionListener(this);
        //鼠标点击监听器
        GlobalScreen.addNativeMouseListener(this);
        //按键监听器
        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("windowClosing");
    }


    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("windowClosed");
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            throw new RuntimeException(ex);
        }
        System.runFinalization();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    /**
     * 更新状态提示
     * @param result
     */
    private static void updateResult(String result) {
        if (result.equals("运行状态：已关闭")) {
            jl_state.setForeground(Color.red);
        } else {
            jl_state.setForeground(Color.green);
        }
        jl_state.setText(result);
    }

    /**
     * 创建托盘图标
     * @param frame
     */
    private void createTrayIcon(Frame frame) {
        if (!SystemTray.isSupported()) {//判断系统是否支持托盘
            logger.info("System tray is not supported.");
            return;
        }

        try {
            String title = "CF炼狱助手";//系统栏通知标题
            String company = "炼狱USP";//系统通知栏内容
            SystemTray systemTray = SystemTray.getSystemTray();//获取系统默认托盘
            Image image = Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/crossfire.png"));//系统栏图标
            TrayIcon trayIcon = new TrayIcon(image, title, createMenu(frame));//添加图标,标题,内容,菜单
            trayIcon.setImageAutoSize(true);//设置图像自适应

            trayIcon.addActionListener(e -> {
                //双击托盘弹出主窗体
                frame.setVisible(true);//显示当前窗口
                frame.toFront();//将此窗口置于前端
            });//双击打开窗口
            systemTray.add(trayIcon);//添加托盘
            trayIcon.displayMessage(title, company, TrayIcon.MessageType.INFO);//弹出一个info级别消息框
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    /**
     * 托盘中的菜单
     * @param frame
     * @return
     */
    private PopupMenu createMenu(Frame frame){
        PopupMenu menu = new PopupMenu();//创建弹出式菜单

        MenuItem openItem = new MenuItem("打开");//创建菜单项
        //给菜单项添加事件监听器，单击时打开系统
        openItem.addActionListener(e -> {
            frame.show();
            frame.toFront();
        });

        MenuItem exitItem = new MenuItem("退出");//创建菜单项
        //给菜单项添加事件监听器，单击时退出系统
        exitItem.addActionListener(e -> System.exit(0));

        menu.add(openItem);//添加打开系统菜单
        menu.addSeparator();//菜单分割符
        menu.add(exitItem);//添加退出系统菜单
        return menu;
    }


    /**
     * 切换主题
     * @param theme
     * @throws IOException
     */
    private void switchTheme(ThemeType theme) throws IOException {
        logger.info("切换主题" + ThemeType.valueOf(theme.toString()));
        configBean.setThemeType(theme);
        GsonUtils.write(fileName, configBean);
        switch (theme) {
            case FlatArcDarkIJTheme:
                FlatArcDarkIJTheme.setup();
                break;
            case FlatAtomOneDarkIJTheme:
                FlatAtomOneDarkIJTheme.setup();
                break;
            case FlatAtomOneLightIJTheme:
                FlatAtomOneLightIJTheme.setup();
                break;
            case FlatDraculaIJTheme:
                FlatDraculaIJTheme.setup();
                break;
            case FlatGitHubIJTheme:
                FlatGitHubIJTheme.setup();
                break;
            case FlatGitHubDarkIJTheme:
                FlatGitHubDarkIJTheme.setup();
                break;
            case FlatLightOwlIJTheme:
                FlatLightOwlIJTheme.setup();
                break;
            case FlatMaterialDarkerIJTheme:
                FlatMaterialDarkerIJTheme.setup();
                break;
            case FlatMaterialDeepOceanIJTheme:
                FlatMaterialDeepOceanIJTheme.setup();
                break;
            case FlatMaterialLighterIJTheme:
                FlatMaterialLighterIJTheme.setup();
                break;
            case FlatMaterialOceanicIJTheme:
                FlatMaterialOceanicIJTheme.setup();
                break;
            case FlatMaterialPalenightIJTheme:
                FlatMaterialPalenightIJTheme.setup();
                break;
            case FlatMonokaiProIJTheme:
                FlatMonokaiProIJTheme.setup();
                break;
            case FlatMoonlightIJTheme:
                FlatMoonlightIJTheme.setup();
                break;
            case FlatNightOwlIJTheme:
                FlatNightOwlIJTheme.setup();
                break;
            case FlatSolarizedDarkIJTheme:
                FlatSolarizedDarkIJTheme.setup();
                break;
            case FlatSolarizedLightIJTheme:
                FlatSolarizedLightIJTheme.setup();
                break;
        }
        FlatLaf.updateUI();
    }


    /**
     * 打开链接
     * @param url
     */
    private void openUrl(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(URI.create(url));
        } catch (IOException ex) {
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }


}


