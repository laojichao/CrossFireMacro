package org.ia0l6.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ia0l6.macro.*;
import org.ia0l6.service.SysTime;
import org.ia0l6.service.TimeService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/****
 *** author：lao
 *** package：org.ia0l6
 *** project：CrossFireMacro
 *** name：Window
 *** date：2023/11/27  11:46
 *** filename：Window
 *** desc：侧键炼狱USP自定义延迟款 高斯分布
 ***/

public class Window extends JFrame implements WindowListener, NativeMouseInputListener {

    private static final Logger logger = LogManager.getLogger(Window.class);
    private JPanel mainPanel;
    private JPanel purPanel;
    private JTextField tfpLeftMean;
    private JTextField tfpRightMean;
    private JTextField tfpRightDev;
    private JTextField tfpLeftDev;
    private JLabel labelTips;
    private JLabel labelStatus;
    private JLabel labelResult;
    private JLabel labelDelay;
    private JLabel labelPur;
    private JLabel labelpLeft;
    private JLabel labelpRight;
    private JLabel labelUSP;
    private JTextField jtuLeftMean;
    private JTextField jtuLeftDev;
    private JTextField jtuRightMean;
    private JTextField jtuRightDev;
    private JLabel labeluLeft;
    private JLabel labeluRight;
    private static int width = 400;
    private static int height = 220;

    //总开关
    private static boolean enable = false;

    private GaussianPurgatory pThread = null;
    private GaussianUSP uspThread = null;

    private void createUIComponents() {
        mainPanel = new JPanel(); // 主面板
        add(mainPanel);
    }

    public Window(){
        setTitle("侧键炼狱高斯v23.11.26");
        setResizable(false);
        setSize(width, height);
        //设置默认关闭操作，不退出程序隐藏窗口
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//        frame.pack();
        setVisible(true);
        setLocationRelativeTo(null);

        Image image = Toolkit.getDefaultToolkit().getImage(Window.class.getResource("/crossfire.png"));
        setIconImage(image);
        addWindowListener(this);

        createUIComponents();
        createTrayIcon(this);
    }


    public void nativeMousePressed(NativeMouseEvent e) {
        if (enable) {
//            logger.debug("PressKey Code = " + e.getButton());
            if (e.getButton() == NativeMouseEvent.BUTTON4 && uspThread == null) {
                uspThread = new GaussianUSP();
//                uspThread.setParameters(Integer.parseInt(jtuLeftMean.getText()), Integer.parseInt(jtuLeftDev.getText()),
//                        Integer.parseInt(jtuRightMean.getText()), Integer.parseInt(jtuRightDev.getText()));
                uspThread.start();
            } else if (e.getButton() == NativeMouseEvent.BUTTON5 && pThread == null) {
                pThread = new GaussianPurgatory();
//                pThread.setParameters(Integer.parseInt(tfpLeftMean.getText()), Integer.parseInt(tfpLeftDev.getText()),
//                        Integer.parseInt(tfpRightMean.getText()), Integer.parseInt(tfpRightDev.getText()));
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

    public void nativeMouseReleased(NativeMouseEvent e) {
        if (e.getButton() == NativeMouseEvent.BUTTON4) {
            if (uspThread != null && uspThread.isAlive()) {
                uspThread.stopMacro();
                uspThread = null;
            }
        } else if (e.getButton() == NativeMouseEvent.BUTTON5) {
            if (pThread != null && pThread.isAlive()) {
                pThread.stopMacro();
                pThread = null;
            }
        }
    }



    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("windowOpened");
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
        System.out.println("windowIconified");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("windowDeiconified");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        System.out.println("windowActivated");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("windowDeactivated");
    }

    private void updateResult(String result) {
        labelResult.setText(result);
    }

    // 创建托盘图标
    private void createTrayIcon(Frame frame) {
        if (!SystemTray.isSupported()) {//判断系统是否支持托盘
            logger.info("System tray is not supported.");
            return;
        }

        try {
            String title = "CF炼狱助手";//系统栏通知标题
            String company = "侧键炼狱USP";//系统通知栏内容
            SystemTray systemTray = SystemTray.getSystemTray();//获取系统默认托盘
            Image image = Toolkit.getDefaultToolkit().getImage(Window.class.getResource("/crossfire.png"));//系统栏图标
            PopupMenu popupMenu = createMenu(frame);//创建弹出式菜单
            TrayIcon trayIcon = new TrayIcon(image, title, popupMenu);//添加图标,标题,内容,菜单
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


    //托盘中的菜单
    private PopupMenu createMenu(Frame frame){
        PopupMenu menu = new PopupMenu();//创建弹出式菜单

        MenuItem openItem = new MenuItem("打开");//创建菜单项
        //给菜单项添加事件监听器，单击时打开系统
        openItem.addActionListener(e -> {
            frame.setVisible(true);
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


    public static void main(String[] args) {

        // 将事件调度程序设置为swing-safe执行器服务。
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        FlatDarkLaf.setup();
        UIManager.put("TextComponent.arc", 5);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 1);
        UIManager.put("Button.innerFocusWidth", 1);
        UIManager.put("TitlePane.unifiedBackground", true);
        UIManager.put("TitlePane.menuBarEmbedded", true);

// 设置字体，设置字体抗锯齿
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        Window window = new Window();
    }

}
