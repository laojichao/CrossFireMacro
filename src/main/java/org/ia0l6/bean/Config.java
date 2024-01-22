package org.ia0l6.bean;

/****
 *** author：lao
 *** package：org.ia0l6
 *** project：CrossFireMacro
 *** name：Config
 *** date：2023/12/4  13:59
 *** filename：Config
 *** desc：
 ***/

public class Config {
    private MouseType type;
    private int selected;

    private ThemeType themeType;


    public Config(MouseType type, int selected, ThemeType themeType) {
        this.type = type;
        this.selected = selected;
        this.themeType = themeType;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public MouseType getType() {
        return type;
    }

    public void setType(MouseType type) {
        this.type = type;
    }

    public ThemeType getThemeType() {
        return themeType;
    }

    public void setThemeType(ThemeType themeType) {
        this.themeType = themeType;
    }
}
