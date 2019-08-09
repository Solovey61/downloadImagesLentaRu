import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Path dstPath = askPath();
		URL url = new URL("https://lenta.ru");
		Optional<Elements> images = parseImages(url.toString());
		saveImagesToFiles(images, dstPath);
	}

	private static Path askPath() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("Enter destination path: ");
			Path path = Path.of(scanner.nextLine());
			if (path.getParent() != null && Files.isWritable(path.getParent()) || Files.isWritable(path))
				return path;
			System.out.println("Destination is inaccessible");
			System.out.println();
		}
	}

	private static Optional<Elements> parseImages(String url) {
		try {
			return Optional.of(Jsoup.connect(url).get().select("img[src~=\\.(png|jpe?g|gif)]"));
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	private static void saveImagesToFiles(Optional<Elements> images, Path dstPath) {
		images.ifPresent(imagesElements -> {
			for (Element image : imagesElements) {
				try {
					URL url = new URL(image.attr("src"));
					Path dstFilePath = getFilePath(url, dstPath);
					FileUtils.copyURLToFile(url, dstFilePath.toFile());
					System.out.println(dstFilePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} );
	}

	private static Path getFilePath(URL url, Path dstPath) {
		if (url.toString().matches("https://icdn\\.lenta\\.ru/images/\\d{4}/\\d{2}/\\d{2}/.+")) {
			String[] splittedURL = url.toString().split("/");
			if (splittedURL.length >= 6) {
				String date = splittedURL[6];
				String month = splittedURL[5];
				String year = splittedURL[4];
				return dstPath.resolve(Path.of(String.format("%s-%s-%s-", date, month, year) + FilenameUtils.getName(url.getPath())));
			}
		}
		return dstPath.resolve(FilenameUtils.getName(url.getPath()));
	}
}
