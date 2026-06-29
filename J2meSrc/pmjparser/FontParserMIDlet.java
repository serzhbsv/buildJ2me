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

            // 2. Парсим вручную
            int charCount = data[0] & 0xFF;
            form.append("Chars: " + charCount + "\n");

            // 3. Читаем смещения (2 байта на символ)
            int[] offsets = new int[charCount];
            for (int i = 0; i < charCount; i++) {
                int idx = 1 + i * 2;
                offsets[i] = ((data[idx] & 0xFF) << 8) | (data[idx + 1] & 0xFF);
            }

            // 4. Данные начинаются после заголовка (1 + charCount*2)
            int dataStart = 1 + charCount * 2;
            int w = 8, h = 12;
            int bytesPerChar = w * h;

            // 5. Показываем первый символ
            if (dataStart + bytesPerChar <= data.length) {
                Image img = Image.createImage(w, h);
                Graphics g = img.getGraphics();
                g.setColor(0xFFFFFF);
                g.fillRect(0, 0, w, h);

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int idx = dataStart + y * w + x;
                        int pixel = data[idx] & 0xFF;
                        if (pixel > 0) {
                            g.setColor(0x000000);
                            g.drawLine(x, y, x, y);
                        }
                    }
                }
                form.append(new ImageItem(null, img, ImageItem.LAYOUT_DEFAULT, null));
            } else {
                form.append("Not enough data for first char");
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
th && firstChar[idx] > 0) {
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
