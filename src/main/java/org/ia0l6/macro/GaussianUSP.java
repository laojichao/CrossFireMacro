package org.ia0l6.macro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ia0l6.bean.MouseType;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

/****
 *** @author：lao
 *** package：org.ia0l6.macro
 *** project：CrossFireMacro
 *** name：GaussianUSP
 *** date：2023/11/28  13:11
 *** filename：GaussianUSP
 *** desc：
 ***/


public class GaussianUSP extends Macro implements Parameter{
    private static final Logger logger = LogManager.getLogger(GaussianUSP.class);

    public GaussianUSP() {
        super();
        leftMean = 50;
        leftStdDev = 5;
        rightMean = 60;
        rightStdDev = 5;
    }
    @Override
    public void setParameters(int pStart, int pEnd, int rStart, int rEnd) {
        this.leftMean = (pStart + pEnd) / 2;
        this.leftStdDev = (pEnd - leftMean) / 3;
        this.rightMean = (rStart + rEnd) / 2;
        this.rightStdDev = (rEnd - rightMean) / 3;
    }

    @Override
    public void run() {
        logger.info("----------------高斯USP--------------------");
        Random random = new Random();
        if (mouseType == MouseType.SIDE) {
            while (!isStop) {
                down = (int) rGenerator.nextGaussian(leftStdDev, leftMean);
                up = (int) rGenerator.nextGaussian(rightStdDev, rightMean);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(down);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(up);
            }
        } else {
            while (!isStop){
                down = (int) rGenerator.nextGaussian(leftStdDev, leftMean);
                up = (int) rGenerator.nextGaussian(rightStdDev, rightMean);
                robot.keyPress(KeyEvent.VK_K);
                robot.delay(down);
                robot.keyRelease(KeyEvent.VK_K);
                robot.delay(up);
            }
        }
    }
}
