package org.ia0l6.macro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ia0l6.bean.MouseType;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/****
 *** author：lao
 *** package：org.ia0l6.macro
 *** project：CrossFireMacro
 *** name：GaussianPurgatory
 *** date：2023/11/28  13:01
 *** filename：GaussianPurgatory
 *** desc：正态（高斯）炼狱
 ***/

public class GaussianPurgatory extends Macro implements Parameter {
    private static final Logger logger = LogManager.getLogger(GaussianPurgatory.class);

    public GaussianPurgatory() {
        super();
        leftMean = 30;
        leftStdDev = 6;
        rightMean = 150;
        rightStdDev = 10;
    }

    @Override
    public synchronized void run() {
        logger.info("-------------------炼狱--------------------");
        if (mouseType == MouseType.SIDE) {
            while (!isStop){
                logger.debug("-------------------侧键炼狱--------------------");
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                down = (int) rGenerator.nextGaussian(rightMean, rightStdDev);
                robot.delay(down);
//                logger.info(down);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                up = (int) rGenerator.nextGaussian(leftMean, leftStdDev);
                robot.delay(up);
//                logger.info(up);
            }
        } else {
            logger.info("-------------------左键炼狱--------------------");
            while (!isStop){
                robot.keyPress(KeyEvent.VK_K);
                down = (int) rGenerator.nextGaussian(rightMean, rightStdDev);
                robot.delay(down);

                robot.keyRelease(KeyEvent.VK_K);
                up = (int) rGenerator.nextGaussian(leftMean, leftStdDev);
                robot.delay(up);
            }
        }

    }

    @Override
    public void setParameters(int pStart, int pEnd, int rStart, int rEnd) {
        this.leftMean = (pStart + pEnd) / 2;
        this.leftStdDev = (pEnd - leftMean) / 3;
        this.rightMean = (rStart + rEnd) / 2;
        this.rightStdDev = (rEnd - rightMean) / 3;
    }
}

