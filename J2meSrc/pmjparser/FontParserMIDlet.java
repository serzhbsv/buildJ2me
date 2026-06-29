import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import java.io.InputStream;

public class FontParserMIDlet extends MIDlet implements CommandListener {
    private Display display;
    private Form form;
    private Command exit;

    public void startApp() {
        display = Display.getDisplay(this);
        form = new Form("Font from Game");
        exit = new Command("Exit", Command.EXIT, 1);
        form.addCommand(exit);
        form.setCommandListener(this);

        try {
            // 1. Загружаем font.pmj
            InputStream is = getClass().getResourceAsStream("/font.pmj");
            if (is == null) {
                form.append("File not found");
                display.setCurrent(form);
                return;
            }
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
            form.append("File size: " + data.length + " bytes\n");

            // 2. Создаём экземпляр Class_2c3 (класс шрифта из игры)
            Class_2c3 font = new Class_2c3();

            // 3. Вызываем метод sub_60, как это делает игра
            //    Параметры: данные, высота (12), ширина (8*193 = 1544), null, null
            //    ВНИМАНИЕ: метод может вернуть null, если что-то не так
            byte[][] chars = font.sub_60(data, 12, 8 * 193, null, null);

            if (chars == null) {
                form.append("sub_60 вернул null");
                display.setCurrent(form);
                return;
            }

            form.append("Chars loaded: " + chars.length + "\n");

            // 4. Показываем первый символ
            if (chars.length > 0 && chars[0] != null) {
                byte[] firstChar = chars[0];
                int w = 8, h = 12;
                Image img = Image.createImage(w, h);
                Graphics g = img.getGraphics();
                g.setColor(0xFFFFFF);
                g.fillRect(0, 0, w, h);

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int idx = y * w + x;
                        if (idx < firstChar.length && firstChar[idx] > 0) {
                            g.setColor(0x000000);
                            g.drawLine(x, y, x, y);
                        }
                    }
                }
                form.append(new ImageItem(null, img, ImageItem.LAYOUT_DEFAULT, null));
            } else {
                form.append("Первый символ пуст");
            }

        } catch (Exception e) {
            form.append("Error: " + e.toString());
            e.printStackTrace();
        }

        display.setCurrent(form);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exit) {
            destroyApp(true);
            notifyDestroyed();
        }
    }

    public void pauseApp() {}
    public void destroyApp(boolean unconditional) {}
}