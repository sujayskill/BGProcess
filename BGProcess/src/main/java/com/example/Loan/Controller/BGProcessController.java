package com.example.Loan.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Loan.Service.BGProcessService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.web.bind.annotation.*;

@Controller
public class BGProcessController {

	private final BGProcessService bgpPocessService;

	public BGProcessController(BGProcessService bgpPocessService) {
		this.bgpPocessService = bgpPocessService;
	}

	@GetMapping("/")
	public String uploadForm() {
		return "FileUpload";
	}

	@PostMapping("/process")
	public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("color") String newColor,
			@RequestParam(value = "tolerance", defaultValue = "50") int tolerance,
			@RequestParam(value = "bgToReplace", defaultValue = "#FFFFFF") String bgToReplace, Model model) {

		try {
			// 1. Validate input
			if (file.isEmpty()) {
				throw new IllegalArgumentException("Please select an image file");
			}

			// 2. Process image (automatically converts to PNG)
			BufferedImage processed = bgpPocessService.replaceBackground(file.getBytes(), Color.decode(newColor),
					Color.decode(bgToReplace), tolerance);

			// 3. Prepare model
			model.addAttribute("original", bgpPocessService.convertToBase64(file.getBytes()));
			model.addAttribute("processed", bgpPocessService.convertToBase64(processed));

		} catch (Exception e) {
			model.addAttribute("error", "Error processing image: " + e.getMessage());
			e.printStackTrace();
		}

		return "FileDownload";
	}
}

//@Controller
//public class BGProcessController {
//
//	private final BGProcessService bgprocessService;
//
//	public BGProcessController(BGProcessService bgprocessService) {
//		this.bgprocessService = bgprocessService;
//	}
//
//	@GetMapping("/")
//	public String index() {
//		return "FileUpload";
//	}
//
//	@PostMapping("/process")
//	public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("color") String newColor,
//			@RequestParam(value = "tolerance", defaultValue = "50") int tolerance,
//			@RequestParam(value = "bgToReplace", defaultValue = "#FFFFFF") String bgToReplace, Model model) {
//
//		try {
//			// 1. Validate input
//			if (file.isEmpty()) {
//				throw new IllegalArgumentException("Please select an image file");
//			}
//
//			// 2. Process image
//			BufferedImage processed = bgprocessService.replaceBackground(file.getBytes(), Color.decode(newColor),
//					Color.decode(bgToReplace), tolerance);
//
//			// 3. Prepare model
//			model.addAttribute("original", bgprocessService.convertToBase64(file.getBytes()));
//			model.addAttribute("processed", bgprocessService.convertToBase64(processed));
//
//		} catch (Exception e) {
//			model.addAttribute("error", "Error processing image: " + e.getMessage());
//			e.printStackTrace();
//		}
//
//		return "FileDownload";
//	}
//
//}

//	@GetMapping("/")
//	public String index() {
//		return "FileUpload";
//	}
//
//	@PostMapping("/process")
//	public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("color") String color,
//			@RequestParam(value = "tolerance", defaultValue = "50") int tolerance, Model model) throws IOException {
//
//		if (file.isEmpty()) {
//			model.addAttribute("message", "Please select a file to upload");
//			return "FileUpload";
//		}
//
//		// Convert hex color to RGB
//		Color bgColor = Color.decode(color);
//
//		// We'll assume white (#FFFFFF) is the default background to replace
//		Color colorToReplace = Color.WHITE;
//
//		// Process the image
//		BufferedImage originalImage = ImageIO.read(file.getInputStream());
//		BufferedImage processedImage = replaceBackgroundColor(originalImage, colorToReplace, bgColor, tolerance);
//
//		// Convert processed image to base64 for display
//		String processedImageData = convertToBase64(processedImage, "png");
//
//		model.addAttribute("originalImage", convertToBase64(originalImage, "png"));
//		model.addAttribute("processedImage", processedImageData);
//		return "FileDownload";
//	}

//	private BufferedImage replaceBackgroundColor(BufferedImage originalImage, Color colorToReplace, Color newBgColor,
//			int tolerance) {
//		int width = originalImage.getWidth();
//		int height = originalImage.getHeight();
//
//		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		int targetRGB = colorToReplace.getRGB();
//		int newRGB = newBgColor.getRGB();
//
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				int pixelRGB = originalImage.getRGB(x, y);
//
//				if (isBackground(pixelRGB, targetRGB, tolerance)) {
//// Replace background pixel
//					newImage.setRGB(x, y, newRGB);
//				} else {
//// Keep original pixel
//					newImage.setRGB(x, y, pixelRGB);
//				}
//			}
//		}
//		return newImage;
//	}

//	private boolean isBackground(int pixelRGB, int targetRGB, int tolerance) {
//// Extract RGB components
//		int pixelR = (pixelRGB >> 16) & 0xFF;
//		int pixelG = (pixelRGB >> 8) & 0xFF;
//		int pixelB = pixelRGB & 0xFF;
//
//		int targetR = (targetRGB >> 16) & 0xFF;
//		int targetG = (targetRGB >> 8) & 0xFF;
//		int targetB = targetRGB & 0xFF;
//
//// Calculate color distance
//		double distance = Math
//				.sqrt(Math.pow(pixelR - targetR, 2) + Math.pow(pixelG - targetG, 2) + Math.pow(pixelB - targetB, 2));
//
//		return distance <= tolerance;
//	}

//	private String convertToBase64(BufferedImage image, String format) throws IOException {
//		// 1. Create a byte array output stream to hold the image data
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//		// 2. Write the image to the output stream in the specified format (PNG, JPEG,
//		// etc.)
//		ImageIO.write(image, format, baos);
//
//		// 3. Convert the byte array to a Base64 string
//		String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
//
//		// 4. Format as a data URL that can be used directly in HTML
//		return "data:image/" + format + ";base64," + base64String;
//	}
//
//}

//private BufferedImage replaceBackground(BufferedImage original, Color newBgColor, Color colorToReplace,
//		int tolerance) {
//	int width = original.getWidth();
//	int height = original.getHeight();
//
//	BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//	Graphics2D g2d = newImage.createGraphics();
//
//	// First fill with new background color
//	g2d.setColor(newBgColor);
//	g2d.fillRect(0, 0, width, height);
//
//	// Then copy only non-background pixels from original
//	for (int y = 0; y < height; y++) {
//		for (int x = 0; x < width; x++) {
//			int pixel = original.getRGB(x, y);
//			Color pixelColor = new Color(pixel, true);
//
//			// Skip if pixel matches background (with tolerance)
//			if (!isBackgroundPixel(pixelColor, colorToReplace, tolerance)) {
//				newImage.setRGB(x, y, pixel);
//			}
//		}
//	}
//
//	g2d.dispose();
//	return newImage;
//}

//private double colorDistance(Color c1, Color c2) {
//	int rDiff = c1.getRed() - c2.getRed();
//	int gDiff = c1.getGreen() - c2.getGreen();
//	int bDiff = c1.getBlue() - c2.getBlue();
//	return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
//}

//private boolean isBackgroundPixel(Color pixelColor, Color bgColor, int tolerance) {
//// Check alpha (transparency)
//if (pixelColor.getAlpha() < 50)
//	return true;
//
//// Check color distance
//return colorDistance(pixelColor, bgColor) <= tolerance;
//}

//@PostMapping("/process")
//public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("color") String color,
//		Model model) throws IOException {
//
//	if (file.isEmpty()) {
//		model.addAttribute("message", "Please select a file to upload");
//		return "FileUpload";
//	}
//
//	// Convert hex color to RGB
//	Color bgColor = Color.decode(color);
//
//	// Process the image
//	BufferedImage originalImage = ImageIO.read(file.getInputStream());
//	BufferedImage processedImage = changeBackgroundColor(originalImage, bgColor);
//
//	// Convert processed image to base64 for display
//	String processedImageData = convertToBase64(processedImage, "png");
//
//	model.addAttribute("originalImage", convertToBase64(originalImage, "png"));
//	model.addAttribute("processedImage", processedImageData);
//	return "FileDownload";
//}
//
//private BufferedImage changeBackgroundColor(BufferedImage originalImage, Color newBgColor) {
//	int width = originalImage.getWidth();
//	int height = originalImage.getHeight();
//
//	BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//	Graphics2D graphics = newImage.createGraphics();
//
//	// Fill background with new color
//	graphics.setColor(newBgColor);
//	graphics.fillRect(0, 0, width, height);
//
//	// Draw original image on top
//	graphics.drawImage(originalImage, 0, 0, null);
//	graphics.dispose();
//
//	return newImage;
//}
//
//private String convertToBase64(BufferedImage image, String format) throws IOException {
//	ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	ImageIO.write(image, format, baos);
//	return "data:image/" + format + ";base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
//}
