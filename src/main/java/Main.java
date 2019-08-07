import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Path dstPath = askPath();
		URL url = new URL("https://lenta.ru");
		Elements images = parseImages(url.toString());
		saveImagesToFiles(images, dstPath);
	}

	private static Path askPath() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter destination path: ");
		return Path.of(scanner.nextLine());
	}

	private static Elements parseImages(String url) {
		try {
			return Jsoup.connect(url).get().select("img[src~=\\.(png|jpe?g|gif)]");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void saveImagesToFiles(Elements images, Path dstPath) {
		for (Element image : images) {
			try {
				URL url = new URL(image.attr("src"));
				String name = FilenameUtils.getName(url.getPath());
				FileUtils.copyURLToFile(url, dstPath.resolve(name).toFile());
				System.out.println(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
