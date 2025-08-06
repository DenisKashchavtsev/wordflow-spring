package pro.dkart.wordflow.blog.application.service.adaptation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.UUID;

public class ImageService {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public static String downloadAndConvertImageToWebP(String imageUrl) {
        try {
            // Загружаем изображение
            BufferedImage image = ImageIO.read(new URL(imageUrl));

            if (image == null) {
                throw new RuntimeException("Не удалось загрузить изображение: " + imageUrl);
            }

            // Создаём папку, если нет
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Не удалось создать папку для изображений: " + UPLOAD_DIR);
                }
            }

            // Генерируем имя файла
            String fileName = UUID.randomUUID().toString() + ".webp";
            File outputFile = new File(UPLOAD_DIR + fileName);

            // Пишем файл
            boolean success = ImageIO.write(image, "webp", outputFile);
            if (!success) {
                throw new RuntimeException("Формат webp не поддерживается или не удалось сохранить: " + outputFile.getAbsolutePath());
            }

            System.out.println("Сохранено изображение: " + outputFile.getAbsolutePath());

            // Путь для сайта
            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при скачивании и преобразовании изображения: " + imageUrl, e);
        }
    }
}
