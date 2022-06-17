package MusalaSoftTestPkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Tests 
{
	  Properties prop;
	  String configFileName = "src/datafile.properties";
	  WebDriver driver;
	  WebDriverWait wait60; // Wait for a maximum of 60 seconds for something to appear
	  WebDriverWait wait1;  // Wait for a maximum of 1 second for something optional or to ensure it does not exist.
	
	  @BeforeClass
	  public void ReadConfig()
	  {
		  // This reads the configuration file for the 
		  // different properties needed for the tests
		  File file = new File(configFileName);
		  FileInputStream fileInput = null;
		  try 
		  {
		  	   fileInput = new FileInputStream(file);
		  } 
		  catch (FileNotFoundException e) 
		  {
			   e.printStackTrace();
		  }	 
		  prop = new Properties();
			
		  try 
		  {
		  	   prop.load(fileInput);
		  } 
		  catch (IOException e) 
		  {
			   e.printStackTrace();
		  }
		  
		  String sDriversPath = prop.getProperty("DriversPath");
		  
		  // Set the properties for both browsers drivers  
		  System.setProperty("webdriver.gecko.driver", sDriversPath + "geckodriver.exe");
	      System.setProperty("webdriver.chrome.driver",sDriversPath + "chromedriver.exe");
	  }
	  
	  @Test
      public void TestCase1() 
      {
   	    String sWrongEmail;
   	    int iNumberOfTestsForEmail;
        //  Here we should be at www.musala.com
    	  
 	    System.out.println("Starting TestCase 1...");
   	    AcceptCookies(); // Accept cookies, if needed.
 	    
        // Here, and for the rest of the tests, no try / catch for possible exceptions, if an exception occurs
        // the testcase will be counted as failed, as expected.
        iNumberOfTestsForEmail = Integer.parseInt(prop.getProperty("TestCase1NumberOfTests"));
        Assert.assertTrue(iNumberOfTestsForEmail > 0 && iNumberOfTestsForEmail < 50);
        
        
        for( int i = 1; i < 6; i++)
        {
        	// Get the i-th wrong email from the properties
        	sWrongEmail = prop.getProperty("WrongEmail" + String.valueOf(i));
        	System.out.print("Trying with email " + sWrongEmail + " ");
        	
        	// For each wrong email, do the test 5 times, or as specified in the config file
        	for( int j = 0; j < iNumberOfTestsForEmail; j++)
        	{
        		System.out.print(Integer.toString(j+1) + "... ");
          	    // Scroll down and go to ‘Contact Us’
        	    // Click on the Contact Us button
        	    wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@class='contacts']//button[@class='contact-label btn btn-1b']")));
        	    WebElement contactUsBtn = driver.findElement(By.xpath("//section[@class='contacts']//button[@class='contact-label btn btn-1b']"));
                contactUsBtn.click();	    
                
                // Wait for the apparition of the window where the data should be added.(Wait a max of 1 sec.)
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("contact_form_pop")));

                
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//input[@name='your-name']")));
                WebElement nameInput = driver.findElement(By.xpath("//div[@id='contact_form_pop']//input[@name='your-name']"));
                EnterTextInInput(driver, nameInput, "Krum Savadzhiev");

                // Fill the other required fields (Mobile is not required).
                // Fill the subject input
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//input[@name='your-subject']")));
                WebElement subjectInput = driver.findElement(By.xpath("//div[@id='contact_form_pop']//input[@name='your-subject']"));
                EnterTextInInput(driver, subjectInput, "My Subject");
                
                // Fill the message input
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//textarea[@name='your-message']")));
                WebElement messageText = driver.findElement(By.xpath("//div[@id='contact_form_pop']//textarea[@name='your-message']"));
                EnterTextInInput(driver, messageText, "My Message");
                
                // Fill the email with an invalid value
                // When we empty the email input before filling it up, if any error message is present, it disappears.
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//input[@name='your-email']")));
                WebElement emailInput = driver.findElement(By.xpath("//div[@id='contact_form_pop']//input[@name='your-email']"));
                EnterTextInInput(driver, emailInput, sWrongEmail);
        
                // Click the Send btn
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//input[@value='Send']")));
                WebElement sendButton = driver.findElement(By.xpath("//div[@id='contact_form_pop']//input[@value='Send']"));
                sendButton.click();
                
                // Check that the first error message are here 'One or more fields have an error. Please check and try again.'
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//div[@class='wpcf7-response-output']")));
                driver.findElement(By.xpath("//div[@id='contact_form_pop']//div[@class='wpcf7-response-output']"));

                // Check for the other error message
                wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='contact_form_pop']//span[contains(text(),'The e-mail address entered is invalid.')]")));
                driver.findElement(By.xpath("//div[@id='contact_form_pop']//span[contains(text(),'The e-mail address entered is invalid.')]"));
                
                // Close the Contact Us dialog
                WebElement closeBtn = driver.findElement(By.id("fancybox-close"));
                closeBtn.click();
        	}
        	System.out.println("done.");
        }
      }
      
 
      @Test
      public void TestCase2()
      {
    	  System.out.println("Starting TestCase 2...");
    	  AcceptCookies(); // Accept cookies if needed.
    	  
    	  // Click on Company on the top
    	  System.out.print("Clicking on company ... ");
    	  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='navbar']//a[@class='main-link' and contains(text(), 'Company')]")));
    	  WebElement company = driver.findElement(By.xpath("//div[@id='navbar']//a[@class='main-link' and contains(text(), 'Company')]"));
          company.click();
          System.out.println("done.");
          
          // Wait until the url changes to contain musala.com/company
          System.out.print("Checking if the URL changes correctly... ");
          String sCompanyUrl = prop.getProperty("CompanyPartOfUrl");
          wait60.until(ExpectedConditions.urlContains(sCompanyUrl));
          System.out.println("done.");
          
          // Verify that there is a Leadership section
          System.out.print("Checking the presence of a section Leadership... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@class='company-members']//*[contains(text(), 'Leadership')]")));
          System.out.println("done.");
          
          // Click the facebook link in the footer
          System.out.print("Clicking the Facebook link on the footer... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//footer//div[@class='links-buttons']/a[contains(@href,'www.facebook.com')]/span")));
          WebElement facebookLink = driver.findElement(By.xpath("//footer//div[@class='links-buttons']/a[contains(@href,'www.facebook.com')]/span"));
          facebookLink.click();
          System.out.println("done.");
          
          // Wait until the page has the correct facebook url
          System.out.print("Checking if the URL changes correctly to the facebook url... ");
          String sFacebookUrl = prop.getProperty("FacebookPartOfUrl");

          ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
  	      tabs = new ArrayList<String>(driver.getWindowHandles());
  	      driver.switchTo().window(tabs.get(1));
          
          wait60.until(ExpectedConditions.urlContains(sFacebookUrl));
          System.out.println("done.");
          
          // Click on Facebook's Only allow essential cookies
          System.out.print("Clicking the Facebook's Only allow essential cookies button... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Only allow essential cookies')]")));
          WebElement cookiesBtn = driver.findElement(By.xpath("//span[contains(text(),'Only allow essential cookies')]"));
          cookiesBtn.click();
          System.out.println("done.");
      }
      
      @Test
      public void TestCase3()
      {
    	  System.out.println("Starting TestCase 3...");
    	  AcceptCookies(); // Accept cookies if needed.
    	  
    	  // Click on Careers on the top
    	  System.out.print("Clicking on Careers ... ");
    	  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='navbar']//a[@class='main-link' and contains(text(), 'Careers')]")));
    	  WebElement careers = driver.findElement(By.xpath("//div[@id='navbar']//a[@class='main-link' and contains(text(), 'Careers')]"));
          careers.click();
          System.out.println("done.");
          
          // Click Check our open positions menu
          System.out.print("Clicking on Check our open positions ... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@class='link_field']//span[@data-alt='Check our open positions']")));
          WebElement checkPositions = driver.findElement(By.xpath("//section[@class='link_field']//span[@data-alt='Check our open positions']"));
          checkPositions.click();
          System.out.println("done.");
          
          // Wait until the url changes to join-us
          System.out.print("Checking if the URL changes correctly... ");
          String sJoinUsUrl = prop.getProperty("JoinUsPartOfUrl");
          wait60.until(ExpectedConditions.urlContains(sJoinUsUrl));
          System.out.println("done.");
          
          // Select All locations in the Location dropdown
          System.out.print("Select All locations from the dropdown... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("get_location")));
          Select drpLocation = new Select(driver.findElement(By.id("get_location")));
          drpLocation.selectByVisibleText("All Locations");
          System.out.println("done.");
          
          // The position name to be open comes from the configuration file
          String sPositionName = prop.getProperty("PositionName");
          System.out.println("Chosen position by name is *" + sPositionName + "*" );
          
          // Find the card with the position with that name, and click to open it
          System.out.print("Clicking the position to open... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@class='join-us']//article[contains(@class,'card-jobsHot')]/div[@class='card-container']/a/div[@class='card']/div[@class='front']/h2[text()='" + sPositionName + "']")));
          WebElement openPosition = driver.findElement(By.xpath("//section[@class='join-us']//article[contains(@class,'card-jobsHot')]/div[@class='card-container']/a/div[@class='card']/div[@class='front']/h2[text()='" + sPositionName + "']"));
          openPosition.click();
          System.out.println("done.");
          
          // Verifying that the section General Description is present
          CheckForSectionPresence("General description");
          CheckForSectionPresence("Requirements");
          CheckForSectionPresence("Responsibilities");
          CheckForSectionPresence("What we offer");
          
          int iNegativeDataSets = Integer.parseInt(prop.getProperty("NegativeDataSets"));
          Assert.assertTrue(iNegativeDataSets > 0 && iNegativeDataSets < 50);
          
    	  // The name of the CV document, same for all the tests in the set
    	  String documentcv = prop.getProperty("DocumentCV");
          
          for(int i = 1; i <= iNegativeDataSets; i++ )
          {
        	  System.out.println("Trying the " + Integer.toString(i) + " negative set...");
        	  
              // Checking the button Apply and clicking it
              System.out.print("Waiting for and clicking the Apply button... ");
              wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='button' and @value='Apply']")));
              WebElement applyButton = driver.findElement(By.xpath("//input[@type='button' and @value='Apply']"));
              applyButton.click();
              System.out.println("done.");
              
              // Wait for the window for data entry to appear
              System.out.print("Waiting for the data entry form to appear...");
             // wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='wpcf7-form init' and @method='post']")));
              wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("join_us_form")));
              System.out.println("done.");
        	  
        	  // Name
        	  String name = prop.getProperty("NegativeName" + Integer.toString(i));
        	  if(name.length() > 0)
        	  {
        		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='wpcf7-form-control-wrap your-name']/input[@name='your-name']")));
        		  WebElement nameInput = driver.findElement(By.xpath("//span[@class='wpcf7-form-control-wrap your-name']/input[@name='your-name']"));
        		  EnterTextInInput(driver, nameInput, name);
        		  System.out.println("   For the name, entered " + name);
        	  }
        	  else
        		  System.out.println("   Leaving the name empty.");
        	  
        	  // Email
        	  String email = prop.getProperty("NegativeEmail" + Integer.toString(i));
        	  if(email.length() > 0)
        	  {
        		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='wpcf7-form-control-wrap your-email']/input[@name='your-email']")));
        		  WebElement emailInput = driver.findElement(By.xpath("//span[@class='wpcf7-form-control-wrap your-email']/input[@name='your-email']"));
        		  EnterTextInInput(driver, emailInput, email);
        		  System.out.println("   For the email, entered " + email);
        	  }
        	  else
        		  System.out.println("   Leaving the email empty.");
        	  
        	  // Mobile
        	  String mobile = prop.getProperty("NegativeMobile" + Integer.toString(i));
        	  if(mobile.length() > 0)
        	  {
        		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='wpcf7-form-control-wrap mobile-number']/input[@name='mobile-number']")));
        		  WebElement mobileInput = driver.findElement(By.xpath("//span[@class='wpcf7-form-control-wrap mobile-number']/input[@name='mobile-number']"));
        		  EnterTextInInput(driver, mobileInput, mobile);
        		  System.out.println("   For the mobile, entered " + mobile);
        	  }
        	  else
        		  System.out.println("   Leaving the mobile empty.");
        	  
        	  // Your Message
        	  String message = prop.getProperty("NegativeMessage" + Integer.toString(i));
        	  if(message.length() > 0)
        	  {
        		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='wpcf7-form-control-wrap your-message']/textarea[@name='your-message']")));
        		  WebElement messageInput = driver.findElement(By.xpath("//span[@class='wpcf7-form-control-wrap your-message']/textarea[@name='your-message']"));
        		  EnterTextInInput(driver, messageInput, message);
        		  System.out.println("   For the message, entered " + message);
        	  }
        	  else
        		  System.out.println("   Leaving the message empty.");
        	  
        	  // Upload CV if needed
        	  String uploadcv = prop.getProperty("UploadCV" + Integer.toString(i));
        	  if(uploadcv.equals("true"))
        	  {
        		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='wpcf7-form-control-wrap uploadtextfield']/input[@name='uploadtextfield']")));
        		  WebElement uploadCvInput = driver.findElement(By.xpath("//span[@class='wpcf7-form-control-wrap uploadtextfield']/input[@name='uploadtextfield']"));
        		  EnterTextInInput(driver, uploadCvInput, documentcv);        		  
        		  System.out.println("   Uploading the cv " + documentcv);
        	  }
        	  else
        		  System.out.println("   Do not upload any CV for this test.");
        	  
        	  // Click on the I agree checkbox
        	  WebElement consentCheckbox = null;
        	  System.out.print("   Clicking on the I agree checkbox...");
        	  for(int j = 0; j < 100; j++)
        	  {
        		  // It happens that Selenium clicks the checkbox too fast, and it is not checked, so try several times until checked
        		  // If not the test will fail
        	      wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("adConsentChx")));
        	      consentCheckbox = driver.findElement(By.id("adConsentChx"));
        	      if(consentCheckbox.isSelected())
                       break;
        	      consentCheckbox.click();
        	      try 
        	      {
					Thread.sleep(100);
				  } 
        	      catch (InterruptedException e) 
        	      {
				  }
        	  }
        	  if(consentCheckbox.isSelected())
 		  	      System.out.println("done, the checkbox is selected.");
        	  else
        		  System.out.println("done, the checkbox is not selected.");
        	  
        	          	  
        	  // Click on the Sent btn
    		  System.out.print("   Clicking on the Send button...");        	  
    		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='submit' and @value='Send']")));
    		  WebElement sendBtn = driver.findElement(By.xpath("//input[@type='submit' and @value='Send']"));
              sendBtn.click();
    		  System.out.println("done");
    		  
    		  // Check the presence of the error message
    		  System.out.print("   Checking the presence of the error message...");
    		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='message-form-content']/div[@class='wpcf7-response-output' and contains(text(), 'error')]")));
    		  System.out.println("done");
    		  
    		  // Close the error message
    		  System.out.print("   Closing the error message...");
    		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='message-form-content']/button[@class='close-form']")));
    		  WebElement closeMsgBtn = driver.findElement(By.xpath("//div[@class='message-form-content']/button[@class='close-form']"));
    		  closeMsgBtn.click();
    		  System.out.println("done");
    		  // Waiting for the error message to disappear
    		  wait60.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='message-form-content']/div[@class='wpcf7-response-output']")));
    		  
        	  // Closing the data entry window
    		  System.out.print("   Closing the data entry window...");        	  
    		  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='fancybox-outer']/a[@id='fancybox-close']")));
    		  WebElement closeBtn = driver.findElement(By.xpath("//div[@id='fancybox-outer']/a[@id='fancybox-close']"));
    		  closeBtn.click();
    		  System.out.println("done");
    		  // Waiting for the error message to disappear
    		  wait60.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//form[@class='wpcf7-form init' and @method='post']")));
    		  
        	  System.out.println("done"); // For this negative test
          }
          
          System.out.println("Success."); 
      }
      
     @Test
      public void TestCase4()
      {
    	  System.out.println("Starting TestCase 4...");
    	  AcceptCookies(); // Accept cookies if needed.
    	  
    	  // Click on Careers on the top
    	  System.out.print("Clicking on Careers ... ");
    	  wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='navbar']//a[@class='main-link' and contains(text(), 'Careers')]")));
    	  WebElement careers = driver.findElement(By.xpath("//div[@id='navbar']//a[@class='main-link' and contains(text(), 'Careers')]"));
          careers.click();
          System.out.println("done.");
          
          // Click Check our open positions menu
          System.out.print("Clicking on Check our open positions ... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@class='link_field']//span[@data-alt='Check our open positions']")));
          WebElement checkPositions = driver.findElement(By.xpath("//section[@class='link_field']//span[@data-alt='Check our open positions']"));
          checkPositions.click();
          System.out.println("done.");
          
          // Select All locations in the Location dropdown
          System.out.print("Select Sofia from the dropdown... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("get_location")));
          Select drpLocation = new Select(driver.findElement(By.id("get_location")));
          drpLocation.selectByVisibleText("Sofia");
          System.out.println("done.");
          
          // TODO da izchakam da se zaredjat vsichkite
          
          System.out.println("\nSofia\n");
          List<WebElement> allOpenJobs = driver.findElements(By.xpath("//section[@class='join-us']//article[contains(@class,'card-jobsHot')]"));
          int countSofia = allOpenJobs.size();
          for(int i = 1; i <= countSofia; i++)
          {
        	   WebElement ithPosition = driver.findElement(By.xpath("(//section[@class='join-us']//article[contains(@class,'card-jobsHot')])[" + Integer.toString(i) + "]/div[@class='card-container']/a/div[@class='card']/div[@class='front']/h2"));
        	   System.out.println("Position: " + ithPosition.getText());
        	   
        	   WebElement ithMoreInfo = driver.findElement(By.xpath("(//section[@class='join-us']//article[contains(@class,'card-jobsHot')])[" + Integer.toString(i) + "]/div[@class='card-container']/a"));
        	   System.out.println("More info: " + ithMoreInfo.getAttribute("href"));
          }
          
          
          // Select Skopje in the Location dropdown
          System.out.print("Select Skopje from the dropdown... ");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("get_location")));
          drpLocation = new Select(driver.findElement(By.id("get_location")));
          drpLocation.selectByVisibleText("Skopje");
          System.out.println("done.");
          
          System.out.println("\nSkopje\n");
          allOpenJobs = driver.findElements(By.xpath("//section[@class='join-us']//article[contains(@class,'card-jobsHot')]"));
          int countSkopje = allOpenJobs.size();
          for(int i = 1; i <= countSkopje; i++)
          {
        	   WebElement ithPosition = driver.findElement(By.xpath("(//section[@class='join-us']//article[contains(@class,'card-jobsHot')])[" + Integer.toString(i) + "]/div[@class='card-container']/a/div[@class='card']/div[@class='front']/h2"));
        	   System.out.println("Position: " + ithPosition.getText());
        	   
        	   WebElement ithMoreInfo = driver.findElement(By.xpath("(//section[@class='join-us']//article[contains(@class,'card-jobsHot')])[" + Integer.toString(i) + "]/div[@class='card-container']/a"));
        	   System.out.println("More info: " + ithMoreInfo.getAttribute("href"));
          }
          
          
          System.out.println("Success.");           
    	  
      }      
      
      @BeforeMethod
      public void beforeMethod()
      {
    	  driver = LoadURL(); // Each testcase, for now, goes to the same URL at the start
    	  ConfigureWaits(); // Configure the waits with the new driver
      }
      
      @AfterMethod
      public void afterMethod() 
      {
    	  // Make sure to close the browser after each testcase
    	  driver.quit();
      }
      
      // Private helper function to create the driver instance, maximize the browser and load the URL, shared by the testcases
      private WebDriver LoadURL() 
      {
    	    WebDriver driver;
    	    String sBrowser, sUrl;
    	    
    	    sBrowser = prop.getProperty("Browser");
    	    
    	    if( sBrowser.equals("Chrome"))
    	        driver = new ChromeDriver();
    	    else 
    	        driver = new FirefoxDriver();
    	    // Maximize the browser window
    	    driver.manage().window().maximize();

    		sUrl = prop.getProperty("Url");
    	    driver.get(sUrl);
   	    
            return driver;	  
      }
      
      private void ConfigureWaits()
      {
  		  // Wait for 60 seconds maximum for expected elements
  	  	  Duration duration = Duration.ofSeconds(60);
  	  	  wait60 =  new WebDriverWait(driver, duration);
  	  	    
  	  	    
  	  	  // Wait for 1 sec maximum 
  	  	  Duration duration1 = Duration.ofSeconds(1);
  	  	  wait1 =  new WebDriverWait(driver, duration1);
      }
      
      private void CheckForSectionPresence(String sSectionName)
      {
          System.out.print("Verifying the presence of the " + sSectionName + " section...");
          wait60.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='content']/div[@class='entry-content']//div[@class='content-title']/h2[text()='" + sSectionName + "']")));
          driver.findElement(By.xpath("//div[@class='content']/div[@class='entry-content']//div[@class='content-title']/h2[text()='" + sSectionName +  "']"));
          System.out.println("done.");
      }

      
      private void EnterTextInInput(WebDriver driver, WebElement input, String text)
      {
    	  // Click in an input, remove any text that may exist in it from before,
    	  // and enter the specified text in the input.
    	  
    	  wait60.until(ExpectedConditions.visibilityOf(input));
    	  input.click();
          Actions actions = new Actions(driver);
          actions.sendKeys(Keys.HOME)
                .keyDown(Keys.SHIFT)
                .sendKeys(Keys.END)
                .keyUp(Keys.SHIFT)
                .sendKeys(text)
                .sendKeys(Keys.TAB)
                .build()
                .perform();
      }
      
      private void AcceptCookies()
      {
    	    // Accept the cookies if needed
    	    // The button to accept cookies may appear, or may not appear. Wait a max of 1 sec for it.
    	    WebElement acceptBtn;
    	    
    	    wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("wt-cli-accept-all-btn")));
    	    try 
    	    {
    	    	acceptBtn = driver.findElement(By.id("wt-cli-accept-all-btn"));
    	    	acceptBtn.click(); // If present, click to accept.
    	    	// If clicked, wait for it to disappear.
    	    	wait60.until(ExpectedConditions.visibilityOfElementLocated(By.id("wt-cli-accept-all-btn")));
    		} 
    	    catch (NoSuchElementException ignored) 
    	    {
    		    // The btn is optional, so do nothing if not here.
    		}
      }
}
