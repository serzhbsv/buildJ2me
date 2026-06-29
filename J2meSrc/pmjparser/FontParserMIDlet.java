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

            int charCount = data[0] & 0xFF;
            form.append("Chars: " + charCount + "\n");

            int dataStart = 1 + charCount * 2;
            int w = 8, h = 12;
            int bytesPerChar = w * h;

            // Создаём сетку для первых 4 символов
            int cols = 4;
            int totalW = cols * (w + 2) + 2;
            int totalH = h + 4;
            Image grid = Image.createImage(totalW, totalH);
            Graphics g = grid.getGraphics();
            g.setColor(0xFFFFFF);
            g.fillRect(0, 0, totalW, totalH);

            for (int i = 0; i < Math.min(4, charCount); i++) {
                int start = dataStart + i * bytesPerChar;
                if (start + bytesPerChar > data.length) break;

                int x = i * (w + 2) + 2;
                int y = 2;
                for (int py = 0; py < h; py++) {
                    for (int px = 0; px < w; px++) {
                        int idx = start + py * w + px;
                        int pixel = data[idx] & 0xFF;
                        if (pixel > 0) {
                            g.setColor(0x000000);
                            g.drawLine(x + px, y + py, x + px, y + py);
                        }
                    }
                }
                g.setColor(0x888888);
                g.drawRect(x-1, y-1, w+1, h+1);
            }

            form.append(new ImageItem(null, grid, ImageItem.LAYOUT_DEFAULT, null));
            form.append("\nПоказаны первые 4 символа");

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
