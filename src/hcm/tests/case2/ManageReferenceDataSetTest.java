package hcm.tests.case2;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.ExcelUtilities;
import common.ReporterManager;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class ManageReferenceDataSetTest extends BaseTest{

private static final int MAX_TIME_OUT = 30;	
	
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 11;
	private static final int defaultlabel = 10;
	
	private String projectName = "Default";
	private String sumMsg = "", errMsg = "";
	private String dataSetName = "";
	private int projectRowNum = TestCaseRow;
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator;
	private int label = defaultlabel;
	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;
	private int projectSheetcolNum = 7;
	private int afrrkInt = 0;
	
	private boolean hasManagedDataSets = false;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		manageReferenceDataSets();
	  
	  	}
	
        catch (AssertionError ae)
        {
            takeScreenshot();
            logFailure(ae.getMessage());

            throw ae;
        }
        catch (Exception e)
        {
            takeScreenshot();
            logFailure(e.getMessage());

            throw e;
        }
    }

	public void manageReferenceDataSets() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		takeScreenshot();
		
		while(!hasManagedDataSets && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasManagedDataSets = manageRefDataSets(task);
		}
		
		System.out.println(sumMsg);
		log("Reference Data Sets has been managed.");
		System.out.println("Reference Data Sets has been managed.");
	}
	
	private String selectProjectName() throws Exception{
		System.out.println("Setting Project to be edited...RowNum: "+projectRowNum+" vs. "+TestCaseRow);
		final String projectSheetName = "Create Implementation Project";

		XSSFSheet projectSheet = ExcelUtilities.ExcelWBook.getSheet(projectSheetName);
		XSSFCell projectCell;
		String newProjectName ="";
		
		if(projectRowNum <= 0){
			projectRowNum = TestCaseRow;
		}
		
	  	try{	        	   
	  		projectCell = projectSheet.getRow(projectRowNum).getCell(projectSheetcolNum);      	  
	  		projectCell.setCellType(projectCell.CELL_TYPE_STRING);
	  		newProjectName = projectCell.getStringCellValue();
	            
	            }catch (Exception e){
	            	e.printStackTrace();
	            	newProjectName="";
	            }
	  	
		System.out.println("New Project Name is now..."+newProjectName);
				
		return newProjectName;
	}

	private boolean manageRefDataSets(TaskListManagerTopPage task) throws Exception{
		int afrrkInt = 0;
		boolean hasSkipped = false;
		
		searchProjectName(task);
		locateManageRefDataSetsPage(task);
		//searchDataSets(task);
		sumMsg += "\n====================== R E P O R T   S U M M A R Y =====================\n";
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			try{
					if(!hasSkipped){
						addNewRow(afrrkInt);
					}
					createDataSets(task, afrrkInt);
					hasSkipped = false;
					afrrkInt += 1;
					sumMsg += "[SUCCESS] Data Set: "+dataSetName+" has been created successfully.\n";
				} catch(DuplicateEntryException de){
					takeScreenshot();
					hasSkipped = true;
					//TaskUtilities.jsFindThenClick("//button[text()='ancel']");
					//Nested try bad..
					/*try{
								TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
								TaskUtilities.jsFindThenClick("//div[text()='Manage Reference Data Sets']/../..//a[@title='Go to Task']");
								TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Reference Data Sets']", MAX_TIME_OUT);
							
							} catch(TimeoutException toe){
								
								searchProjectName(task);
								locateManageRefDataSetsPage(task);
							}*/
					//Nested try bad..
					if(getExcelData(inputs+1, defaultcolNum, "text").isEmpty()){
						TaskUtilities.jsFindThenClick("//button[text()='ancel']");
						TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
					}
					errMsg += ReporterManager.trimErrorMessage(de+errMsg);
					errMsg = errMsg+"\n";
					sumMsg += "[FAILED] Unable to create Data Set: "+dataSetName+"...\n"+errMsg;
				}
			colNum = defaultcolNum;
			inputs += 1;
			errMsg = "";
			if(getExcelData(inputs, defaultcolNum, "text").length()>0)
				sumMsg += "------------------------------------------------------------------------\n";
		}
		
		sumMsg += "====================== E N D   O F   R E P O R T =======================\n";
		return true;
	}
	
	private void searchProjectName(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//a[text()='Manage Implementation Projects']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//a[text()='Manage Implementation Projects']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Implementation Projects']", MAX_TIME_OUT);
		
		searchData = projectName;
		labelLocator = "Name";
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
		
		TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, searchData);
		TaskUtilities.jsFindThenClick("//button[text()='Search']");
		Thread.sleep(3500);
		TaskUtilities.customWaitForElementVisibility("//a[text()='"+searchData+"']", MAX_TIME_OUT);
	}
	
	private void locateManageRefDataSetsPage(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.jsFindThenClick("//a[text()='"+searchData+"']");
	
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
		TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.scrollDownToElement(false, "big");
			}
		});
		
		if(is_element_visible("//div[text()='Workforce Deployment']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Workforce Deployment']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Define Common Applications Configuration for Human Capital Management']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Define Enterprise Structures for Human Capital Management']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Define Enterprise Structures for Human Capital Management']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Enterprise Structures for Human Capital Management']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Enterprise Structures for Human Capital Management']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Define Reference Data Sharing for Human Capital Management']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Define Reference Data Sharing for Human Capital Management']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Reference Data Sharing for Human Capital Management']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Reference Data Sharing for Human Capital Management']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Manage Reference Data Sets']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//div[text()='Manage Reference Data Sets']");
		TaskUtilities.jsFindThenClick("//div[text()='Manage Reference Data Sets']/../..//a[@title='Go to Task']");
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Reference Data Sets']", MAX_TIME_OUT);
	}

	private void addNewRow(int afrrkInt) throws Exception{
		TaskUtilities.jsFindThenClick("//img[@title='New']/..");
		TaskUtilities.customWaitForElementVisibility("//tr[@_afrrk="+afrrkInt+"]", MAX_TIME_OUT);
	}
	private void createDataSets(TaskListManagerTopPage task, int afrrkInt) throws Exception{
		String spanReference = "";
		
		String pseudoRoot;
		while(getExcelData(label, colNum, "text").length()>0){
			 pseudoRoot = "//tr[@_afrrk="+afrrkInt+"]";
			
			labelLocator = getExcelData(label, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			labelLocatorPath = pseudoRoot+labelLocatorPath;
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			if(labelLocator.contentEquals("Set Code")){
				spanReference = dataLocator;
			}
			
			TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
			
			colNum += 1;
		}
		
		dataSetName = getExcelData(inputs, defaultcolNum, "text");
		
		//TaskUtilities.jsFindThenClick("//button[text()='ave and Close']");
		TaskUtilities.jsFindThenClick("//button[text()='Save']");
		TaskUtilities.customWaitForElementVisibility("//span[text()='"+spanReference+"']", 15, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		/*TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//div[text()='Manage Reference Data Sets']/../..//a[@title='Go to Task']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Reference Data Sets']", MAX_TIME_OUT);*/
		
	}
	
	private int  surveyCurrentTableInputs(String currentStep) throws Exception{
		int afrrkInt = -1;
		
		List<WebElement> queryFolder = driver.findElements(By.xpath("//div[contains(@id,'dynamicRegion')]//tr"));
		System.out.println("folder size is "+queryFolder.size());
		for(WebElement inputEntry : queryFolder){
			
			String afrrk = inputEntry.getAttribute("_afrrk");
			System.out.println("afrrk is "+afrrk);
			
			if(afrrk != null && !afrrk.isEmpty() && !afrrk.contentEquals("")){
				if(Integer.parseInt(afrrk) > afrrkInt){
					afrrkInt =  Integer.parseInt(afrrk);
				}else{
					//Skips...
				}
			}
		}
		
		System.out.print("afrrkInt is now: "+afrrkInt);
		return afrrkInt;
	}
}
