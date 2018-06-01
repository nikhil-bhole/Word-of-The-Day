package utility;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;


public class WoD{

	static final String EXCEL_FILE_PATH = "";  //PROVIDE FILEPATH OF AN EXCEL SHEET.
	static final String DATE_TODAY = getDateToday();
	static final String CURRENT_MONTH = getCurrentMonth();
	static final String CONTACT_NAME = "";     //PROVIDE DESIRED CONTACT OR GROUP NAME.  
	static final String URL = "https://web.whatsapp.com/";
	
	static DataFormatter dataFormatter = new DataFormatter();
	public static WebDriver driver = null;
	
	public static List<String> getRawData() {
		List<String> raw = new ArrayList<String>();
		try {
			int position = Integer.parseInt(DATE_TODAY.substring(0, Math.min(DATE_TODAY.length(), 2)));
			FileInputStream file = new FileInputStream(new File(EXCEL_FILE_PATH));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(CURRENT_MONTH);
			Row row = sheet.getRow(position);
			if (row != null) {
				for (Cell cell : row) {
					raw.add(dataFormatter.formatCellValue(cell));
				}
			}
			workbook.close();

		} catch (Exception e) {

		}
		return raw;
	}
	
	public static String formatData(List<String> test) {
		String WORD = "*" + test.get(1) + "*" + "  " + "_" + test.get(2) + "_";
		String MEANING = test.get(3);
		String MARATHI_MEANING = test.get(4);
		String SYNONYMS = test.get(5);
		String USE_IN_SENTENCE = "```" + test.get(6) + "```";
		List<String> WORD_OF_THE_DAY = new ArrayList<String>();
		WORD_OF_THE_DAY.add("📖 Word of the day\n");
		WORD_OF_THE_DAY.add(WORD + "\n");
		WORD_OF_THE_DAY.add(MEANING + "\n");
		WORD_OF_THE_DAY.add(MARATHI_MEANING + "\n");
		WORD_OF_THE_DAY.add("_Synonyms_ \n" + SYNONYMS + "\n");
		WORD_OF_THE_DAY.add(USE_IN_SENTENCE);
		String listString = String.join("\n", WORD_OF_THE_DAY);
		return listString;
	}
	
	@Test
	static void send(){
		openBrowser();
		driver.get(URL);
		sleep(10000);

		WebDriverWait wait = new WebDriverWait(driver, 10);

		WebElement txtSearchContact = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='side']/div[2]/div/label/input")));
		txtSearchContact.sendKeys(CONTACT_NAME);

		WebElement spanContactName = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='"+ CONTACT_NAME + "']")));
		spanContactName.click();

		WebElement txtChatArea = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@contenteditable='true']")));
		pressShiftEnterOnNextLine(txtChatArea);

		sleep(2000);
		WebElement btnSendMessage = driver.findElement(By.className("_2lkdt"));
		btnSendMessage.click();
		System.out.println("Word of the day sent successfully.");
	}
	
	static void pressShiftEnterOnNextLine(WebElement element){
		String word = formatData(getRawData());
		String[] lines = word.split("\r\n|\r|\n");
		for (int i = 0; i < lines.length; i++) {
			element.sendKeys(lines[i]);
			element.sendKeys(Keys.SHIFT, Keys.ENTER);
		}
	}
	
	public static String getDateToday() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-YY");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	static String getCurrentMonth(){
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-YY");
		Date date = new Date();
		return new SimpleDateFormat("MMMM").format(date);
	}
	
	public static void openBrowser(){
		System.setProperty("webdriver.chrome.driver", "/home/nikhilbhole/chromedriver");
		driver=new ChromeDriver();
	 	driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
