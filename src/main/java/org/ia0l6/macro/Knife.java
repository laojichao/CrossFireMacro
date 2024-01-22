package org.ia0l6.macro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ia0l6.bean.MouseType;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/****
 *** @author：lao
 *** package：org.ia0l6
 *** project：CrossFireMacro
 *** name：Knife
 *** date：2023/11/26  21:08
 *** filename：Knife
 *** desc：侧键炼狱快刀
 ***/

public class Knife extends Macro {
    private static final Logger logger = LogManager.getLogger(Knife.class);

    public Knife() {
        super();
    }

    @Override
    public synchronized void run() {
        logger.info("-------------------炼狱快刀-K-------------------");
        if (mouseType == MouseType.SIDE) {
            while (!isStop){
                down = rGenerator.nextInt(9, 11);
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.delay(down);
                up = rGenerator.nextInt(279, 281);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                robot.delay(up);

                down = rGenerator.nextInt(9, 11);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(down);
                int up = rGenerator.nextInt(9, 11);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(up);
            }
        } else {
            while (!isStop){
                down = rGenerator.nextInt(9, 11);
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.delay(down);
                up = rGenerator.nextInt(279, 281);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                robot.delay(up);

                down = rGenerator.nextInt(9, 11);
                robot.keyPress(KeyEvent.VK_K);
                robot.delay(down);
                up = rGenerator.nextInt(9, 11);
                robot.keyRelease(KeyEvent.VK_K);
                robot.delay(up);
            }
        }
    }
}
