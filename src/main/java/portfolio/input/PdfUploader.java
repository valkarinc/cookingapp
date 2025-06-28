package portfolio.input;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PdfUploader {

    public static boolean uploadPdf(JFrame parentFrame) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select PDF to Upload");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return copyPdfToFolder(selectedFile, parentFrame);
        }

        return false;
    }

    private static boolean copyPdfToFolder(File pdfFile, JFrame parentFrame) {
        try {
            Constants.startPdfUpload();

            Path pdfsFolder = Paths.get(Constants.PDFS_FOLDER);
            if (!Files.exists(pdfsFolder)) {
                Files.createDirectories(pdfsFolder);
            }

            Path targetPath = pdfsFolder.resolve(pdfFile.getName());
            Files.copy(pdfFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Constants.registerPdfUpload(pdfFile.getName());

            JOptionPane.showMessageDialog(parentFrame,
                    "PDF uploaded successfully!\nFile: " + pdfFile.getName(),
                    "Upload Complete", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error uploading PDF: " + e.getMessage(),
                    "Upload Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}