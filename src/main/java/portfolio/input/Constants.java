package portfolio.input;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {

    public static final int NO_PAGES = 0;
    public static final int MIN_PAGES = 1;
    public static final int MAX_PAGES = 112;
    public static final int DEFAULT_START_PAGES = 5;

    public static final String APP_NAME = "The Slater Cooking Portfolio";
    public static final String APP_VERSION = "1.0.0";
    public static final String AUTHOR = "Chaz";

    public static final String DEFAULT_SAVE_EXTENSION = ".ckp";
    public static final String BACKUP_FOLDER = "backups/";
    public static final String IMAGES_FOLDER = "images/";
    public static final String RECIPES_FOLDER = "recipes/";
    public static final String PDFS_FOLDER = "pdfs/";
    public static final String PDF_EXTENSION = ".pdf";

    public static boolean clickedNewPage = false;
    public static boolean createdNewPage = false;
    public static boolean onHomeScreen = true;
    public static boolean noPages = true;
    public static boolean hasUnsavedChanges = false;
    public static boolean isEditMode = false;

    public static int currentPageNumber = 1;
    public static int totalPages = NO_PAGES;
    public static String currentRecipeTitle = "";
    public static LocalDateTime lastSaved = null;
    public static String lastUploadedPdf = "";
    public static boolean pdfUploadInProgress = false;

    public static final String PAGE_TYPE_RECIPE = "RECIPE";
    public static final String PAGE_TYPE_PHOTO = "PHOTO";
    public static final String PAGE_TYPE_STORY = "STORY";
    public static final String PAGE_TYPE_TECHNIQUE = "TECHNIQUE";
    public static final String PAGE_TYPE_INGREDIENT_NOTES = "INGREDIENT_NOTES";
    public static final String PAGE_TYPE_MENU_PLANNING = "MENU_PLANNING";
    public static final String PAGE_TYPE_PDF = "PDF";

    public static final String[] DEFAULT_PAGE_NAMES = {
            "Welcome to My Kitchen",
            "Signature Recipes",
            "Family Favorites",
            "Experimental Dishes",
            "Cooking Techniques"
    };

    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    public static final String FONT_FAMILY = "Arial";
    public static final int DEFAULT_FONT_SIZE = 12;

    public static final String PRIMARY_COLOR = "#2C3E50";
    public static final String SECONDARY_COLOR = "#E67E22";
    public static final String BACKGROUND_COLOR = "#ECF0F1";
    public static final String TEXT_COLOR = "#2C3E50";

    public static int getTotalPages() {
        return totalPages;
    }

    public static void setTotalPages(int pages) {
        totalPages = Math.max(NO_PAGES, Math.min(pages, MAX_PAGES));
        noPages = (totalPages == NO_PAGES);
    }

    public static boolean hasNoPages() {
        return totalPages == NO_PAGES;
    }

    public static boolean hasMaxPages() {
        return totalPages >= MAX_PAGES;
    }

    public static boolean canAddPage() {
        return totalPages < MAX_PAGES;
    }

    public static void registerNewPageClick() {
        clickedNewPage = true;
        if (onHomeScreen && canAddPage()) {

            currentPageNumber = totalPages + 1;
        }
    }

    public static void confirmPageCreation() {
        if (clickedNewPage && canAddPage()) {
            setTotalPages(totalPages + 1);
            createdNewPage = true;
            clickedNewPage = false;
            hasUnsavedChanges = true;
        }
    }

    public static void navigateToPage(int pageNumber) {
        if (pageNumber >= MIN_PAGES && pageNumber <= totalPages) {
            currentPageNumber = pageNumber;
            onHomeScreen = (pageNumber == 1);
        }
    }

    public static void markSaved() {
        hasUnsavedChanges = false;
        lastSaved = LocalDateTime.now();
    }

    public static String getLastSavedTime() {
        if (lastSaved == null) {
            return "Never saved";
        }
        return lastSaved.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    public static void resetSession() {
        totalPages = NO_PAGES;
        currentPageNumber = 1;
        noPages = true;
        onHomeScreen = true;
        clickedNewPage = false;
        createdNewPage = false;
        hasUnsavedChanges = false;
        isEditMode = false;
        currentRecipeTitle = "";
        lastSaved = null;
    }

    public static String getPageName(int pageNumber) {
        if (pageNumber <= 0 || pageNumber > totalPages) {
            return "Invalid Page";
        }
        if (pageNumber <= DEFAULT_PAGE_NAMES.length) {
            return DEFAULT_PAGE_NAMES[pageNumber - 1];
        }
        return "Page " + pageNumber;
    }

    public static void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            hasUnsavedChanges = true;
        }
    }

    public static void registerPdfUpload(String pdfFileName) {
        lastUploadedPdf = pdfFileName;
        pdfUploadInProgress = false;
        hasUnsavedChanges = true;
    }

    public static void startPdfUpload() {
        pdfUploadInProgress = true;
    }

    public static String getLastUploadedPdf() {
        return lastUploadedPdf;
    }

}