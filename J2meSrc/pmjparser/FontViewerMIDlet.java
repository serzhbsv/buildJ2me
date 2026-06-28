import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FontViewerMIDlet extends MIDlet implements CommandListener {
    private Display display;
    private Form form;
    private Command exitCommand;

    public void startApp() {
        display = Display.getDisplay(this);
        form = new Form("Шрифт font.pmj");
        exitCommand = new Command("Выход", Command.EXIT, 1);
        form.addCommand(exitCommand);
        form.setCommandListener(this);

        try {
            // 1. Загружаем font.pmj как байтовый массив
            byte[] fontData = loadResource("/font.pmj");
            if (fontData == null) {
                form.append("Ошибка: файл не найден");
                display.setCurrent(form);
                return;
            }

            // 2. Парсим шрифт
            int charCount = fontData[0] & 0xFF;
            int charWidth = 8;
            int charHeight = 12;
            int dataStart = 1 + charCount * 2; // заголовок: 1 байт + таблица смещений (2 байта на символ)

            // 3. Создаём изображение со всеми символами в виде сетки
            int cols = 16;
            int rows = (charCount + cols - 1) / cols;
            int totalWidth = cols * (charWidth + 2) + 2;
            int totalHeight = rows * (charHeight + 2) + 2;

            Image gridImage = Image.createImage(totalWidth, totalHeight);
            Graphics g = gridImage.getGraphics();
            g.setColor(0x000000);
            g.fillRect(0, 0, totalWidth, totalHeight);

            // Рисуем каждый символ
            for (int i = 0; i < charCount; i++) {
                int col = i % cols;
                int row = i / cols;
                int x = col * (charWidth + 2) + 2;
                int y = row * (charHeight + 2) + 2;

                // Извлекаем данные символа
                int startPos = dataStart + i * (charWidth * charHeight);
                if (startPos + charWidth * charHeight > fontData.length) break;

                // Создаём временное изображение для символа
                Image charImg = Image.createImage(charWidth, charHeight);
                Graphics cg = charImg.getGraphics();
                cg.setColor(0xFFFFFF);
                cg.fillRect(0, 0, charWidth, charHeight);

                // Рисуем пиксели
                for (int py = 0; py < charHeight; py++) {
                    for (int px = 0; px < charWidth; px++) {
                        int idx = startPos + py * charWidth + px;
                        if (idx < fontData.length) {
                            int pixel = fontData[idx] & 0xFF;
                            if (pixel > 0) {
                                cg.setColor(0x000000);
                                cg.drawLine(px, py, px, py);
                            }
                        }
                    }
                }

                // Вставляем символ в сетку
                g.drawImage(charImg, x, y, Graphics.TOP | Graphics.LEFT);
            }

            // Показываем изображение
            ImageItem imageItem = new ImageItem(null, gridImage, ImageItem.LAYOUT_DEFAULT, null);
            form.append(imageItem);

        } catch (Exception e) {
            form.append("Ошибка: " + e.toString());
        }

        display.setCurrent(form);
    }

    private byte[] loadResource(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) return null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public void pauseApp() {}
    public void destroyApp(boolean unconditional) {}

    public void commandAction(Command c, Displayable d) {
        if (c == exitCommand) {
            destroyApp(true);
            notifyDestroyed();
        }
    }
}
