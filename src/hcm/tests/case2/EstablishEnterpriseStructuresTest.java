package hcm.tests.case2;

import static util.ReportLogger.logFailure;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import common.BaseTest;
import common.ExcelUtilities;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class EstablishEnterpriseStructuresTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;	
	
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 10;
	private static final int defaultlabel = 9;
	
	private static int 	divisionlabel, divisioninputs, legalEntitylabel, legalEntityinputs,
						busUnitlabel, busUnitinputs, refSetlabel, refSetinputs,
						manageBusUnitAsslabel, manageBusUnitAssinputs;
	
	private String projectName = "Default";
	private String sumMsg = "";
	private int projectRowNum = TestCaseRow;
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator, rateTypeName;
	private int label = defaultlabel;
	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;
	private int projectSheetcolNum = 7;
	private int lastInputs;
	private int inputCount;
	
	private boolean hasEstablishedEnterprise = false;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		establishEnterprise();
	  
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

	public void establishEnterprise() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		//default labels...
		getInputCount();
		divisionlabel = label + inputCount + 1;
		legalEntitylabel = divisionlabel + inputCount + 1;
		busUnitlabel = legalEntitylabel + inputCount + 1;
		refSetlabel = busUnitlabel + inputCount + 1;
		manageBusUnitAsslabel = refSetlabel + inputCount + 1;
		
		while(!hasEstablishedEnterprise && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasEstablishedEnterprise = establishEntStructures(task);
		}
		
	}
	
	private int getInputCount() throws Exception{
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			inputCount += 1;
			inputs += 1;
		}
		inputs = defaultinputs;
		return lastInputs;
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
	
	private boolean establishEntStructures(TaskListManagerTopPage task) throws Exception{
		
		locateEnterpriseConfigPage(task);
		createEnterpriseConfig(task);
		
		return true;
	}

	private void locateEnterpriseConfigPage(TaskListManagerTopPage task) throws Exception{
		
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
		TaskUtilities.jsFindThenClick("//a[text()='"+searchData+"']");
	

		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
		TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']", MAX_TIME_OUT);
		
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
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Define Initial Configuration']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Define Initial Configuration']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Initial Configuration']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Initial Configuration']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Establish Enterprise Structures']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//div[text()='Establish Enterprise Structures']");
		TaskUtilities.jsFindThenClick("//div[text()='Establish Enterprise Structures']/../..//a[@title='Go to Task']");
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Enterprise Configuration']", MAX_TIME_OUT);
	}
	private void createEnterpriseConfig(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.jsFindThenClick("//img[@title='Create']/..");
		
		setEnterpriseNameAndDesc(task);
		manageEnterprise(task);

	}
	
	private void setEnterpriseNameAndDesc(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//div[text()='Create Enterprise Configuration']", MAX_TIME_OUT);
		
		while(getExcelData(label, colNum, "text").length()>0){
			labelLocator = getExcelData(label, defaultcolNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
			
			colNum += 1;
		}
		
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		
	}
	private void manageEnterprise(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Enterprise')]", MAX_TIME_OUT);
		setMEEnterpriseInfo(task);
	}
	
	private void setMEEnterpriseInfo(TaskListManagerTopPage task) throws Exception{
		colNum += 1;
		
		while(!getExcelData(label, colNum, "text").contains("Location Name")){
			labelLocator = getExcelData(label, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
			
			colNum += 1;
		}
	}
	
	private void setMELegalInfo(TaskListManagerTopPage task) throws Exception{
		
	}
}
