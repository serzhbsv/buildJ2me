import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import java.io.InputStream;

public class FontExtractMIDlet extends MIDlet implements CommandListener {
    private Display display;
    private Form form;
    private Command exitCommand;

    public void startApp() {
        display = Display.getDisplay(this);
        form = new Form("Шрифт из игры");
        exitCommand = new Command("Выход", Command.EXIT, 1);
        form.addCommand(exitCommand);
        form.setCommandListener(this);

        try {
            // 1. Загружаем font.pmj
            InputStream is = getClass().getResourceAsStream("/font.pmj");
            if (is == null) {
                form.append("Файл не найден");
                display.setCurrent(form);
                return;
            }
            byte[] fontData = new byte[is.available()];
            is.read(fontData);
            is.close();

            // 2. Создаём экземпляр Class_2c3 (основной класс шрифта)
            Class_2c3 font = new Class_2c3();

            // 3. Загружаем шрифт через метод sub_60
            //    В игре это делает Class_1d5, но мы можем вызвать напрямую
            //    Параметры: байты, высота (12), ширина (???), массивы (null)
            //    Внимание: точная сигнатура метода sub_60 может отличаться
            //    Оригинал: public final byte[][] sub_60(byte[] byArray, int n, int n2, int[] nArray, String[] stringArray)
            //    Мы передаём: fontData, 12, 8*193, null, null
            byte[][] charData = font.sub_60(fontData, 12, 8 * 193, null, null);

            if (charData == null) {
                form.append("Ошибка загрузки шрифта");
                display.setCurrent(form);
                return;
            }

            form.append("Символов: " + charData.length + "\n");

            // 4. Показываем первый символ (если есть)
            if (charData.length > 0) {
                byte[] firstChar = charData[0];
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
            }

        } catch (Exception e) {
            form.append("Ошибка: " + e.toString());
            e.printStackTrace();
        }

        display.setCurrent(form);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exitCommand) {
            destroyApp(true);
            notifyDestroyed();
        }
    }

    public void pauseApp() {}
    public void destroyApp(boolean unconditional) {}
}
