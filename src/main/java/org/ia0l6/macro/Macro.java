package org.ia0l6.macro;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ia0l6.bean.MouseType;

import java.awt.*;

/****
 *** @author：lao
 *** package：org.ia0l6.macro
 *** project：CrossFireMacro
 *** name：Macro
 *** date：2024/1/12  21:34
 *** filename：Macro
 *** desc：鼠标宏总接口
 **
*/

public class Macro extends Thread {

    private static final Logger logger = LogManager.getLogger(Macro.class);
    protected Robot robot;
    protected boolean isStop;
    protected int leftMean;
    protected int leftStdDev;
    protected int rightMean;
    protected int rightStdDev;
    protected int down;
    protected int up;
    protected RandomDataGenerator rGenerator;

    public Macro()  {
        rGenerator = new RandomDataGenerator();
        isStop = false;
        leftMean = 30;
        leftStdDev = 6;
        rightMean = 150;
        rightStdDev = 10;
        down = 0;
        up = 0;
        mouseType = MouseType.SIDE;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        logger.info("初始化成功");
    }

    protected MouseType mouseType;

    public void stopMacro() {
        isStop = true;
        logger.info("停止宏");
    }

    public void setMouseType(MouseType mouseType) {
        this.mouseType = mouseType;
    }
}
