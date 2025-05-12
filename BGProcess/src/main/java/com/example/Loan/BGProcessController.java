package com.example.Loan;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Controller
public class BGProcessController {

	@GetMapping("/")
	public String index() {
		return "FileUpload";
	}

	@PostMapping("/process")
	public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("color") String color,
			@RequestParam(value = "tolerance", defaultValue = "50") int tolerance, Model model) throws IOException {

		if (file.isEmpty()) {
			model.addAttribute("message", "Please select a file to upload");
			return "FileUpload";
		}

		// Convert hex color to RGB
		Color bgColor = Color.decode(color);

		// We'll assume white (#FFFFFF) is the default background to replace
		Color colorToReplace = Color.WHITE;

		// Process the image
		BufferedImage originalImage = ImageIO.read(file.getInputStream());
		BufferedImage processedImage = replaceBackgroundColor(originalImage, colorToReplace, bgColor, tolerance);

		// Convert processed image to base64 for display
		String processedImageData = convertToBase64(processedImage, "png");

		model.addAttribute("originalImage", convertToBase64(originalImage, "png"));
		model.addAttribute("processedImage", processedImageData);
		return "FileDownload";
	}

	private BufferedImage replaceBackgroundColor(BufferedImage originalImage, Color colorToReplace, Color newBgColor,
			int tolerance) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int targetRGB = colorToReplace.getRGB();
		int newRGB = newBgColor.getRGB();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixelRGB = originalImage.getRGB(x, y);

				if (isBackground(pixelRGB, targetRGB, tolerance)) {
// Replace background pixel
					newImage.setRGB(x, y, newRGB);
				} else {
// Keep original pixel
					newImage.setRGB(x, y, pixelRGB);
				}
			}
		}
		return newImage;
	}

	private boolean isBackground(int pixelRGB, int targetRGB, int tolerance) {
// Extract RGB components
		int pixelR = (pixelRGB >> 16) & 0xFF;
		int pixelG = (pixelRGB >> 8) & 0xFF;
		int pixelB = pixelRGB & 0xFF;

		int targetR = (targetRGB >> 16) & 0xFF;
		int targetG = (targetRGB >> 8) & 0xFF;
		int targetB = targetRGB & 0xFF;

// Calculate color distance
		double distance = Math
				.sqrt(Math.pow(pixelR - targetR, 2) + Math.pow(pixelG - targetG, 2) + Math.pow(pixelB - targetB, 2));

		return distance <= tolerance;
	}

	private String convertToBase64(BufferedImage image, String format) throws IOException {
		// 1. Create a byte array output stream to hold the image data
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 2. Write the image to the output stream in the specified format (PNG, JPEG,
		// etc.)
		ImageIO.write(image, format, baos);

		// 3. Convert the byte array to a Base64 string
		String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());

		// 4. Format as a data URL that can be used directly in HTML
		return "data:image/" + format + ";base64," + base64String;
	}
}










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
