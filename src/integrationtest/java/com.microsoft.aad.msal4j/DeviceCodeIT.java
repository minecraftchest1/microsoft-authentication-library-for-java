package com.microsoft.aad.msal4j;

import Infrastructure.SeleniumExtensions;
import lapapi.LabResponse;
import lapapi.LabUser;
import lapapi.LabUserProvider;
import lapapi.NationalCloud;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

@Test
public class DeviceCodeIT {

    private final static Logger LOG = LoggerFactory.getLogger(DeviceCodeIT.class);

    private LabUserProvider labUserProvider;
    private WebDriver seleniumDriver;

    @BeforeClass
    public void setUp(){
        labUserProvider = LabUserProvider.getInstance();
        seleniumDriver = SeleniumExtensions.createDefaultWebDriver();
    }

    @Test
    public void DeviceCodeFlowTest() throws Exception {
        LabResponse labResponse = labUserProvider.getDefaultUser(
                NationalCloud.AZURE_CLOUD,
                false);
        labUserProvider.getUserPassword(labResponse.getUser());

        PublicClientApplication pca = new PublicClientApplication.Builder(
                labResponse.getAppId()).
                authority(TestConstants.AUTHORITY_ORGANIZATIONS).
                build();

        Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> {
            runAutomatedDeviceCodeFlow(deviceCode, labResponse.getUser());
        };

        AuthenticationResult result = pca.acquireTokenByDeviceCodeFlow(
                Collections.singleton(TestConstants.GRAPH_DEFAULT_SCOPE),
                deviceCodeConsumer).get();

        Assert.assertNotNull(result);
        Assert.assertTrue(!Strings.isNullOrEmpty(result.getAccessToken()));
    }

    private void runAutomatedDeviceCodeFlow(DeviceCode deviceCode, LabUser user){
        try{

            LOG.info("Loggin in ... Entering device code");
            seleniumDriver.navigate().to(deviceCode.getVerificationUrl());
            seleniumDriver.findElement(new By.ById("otc")).sendKeys(deviceCode.getUserCode());
            LOG.info("Loggin in ... click continue");
            WebElement continueBtn = SeleniumExtensions.waitForElementToBeVisibleAndEnable(
                   seleniumDriver,
                   new By.ById("idSIButton9"));
            continueBtn.click();

            SeleniumExtensions.performLogin(seleniumDriver, user);
        } catch(Exception e){
            String file = System.getenv("BUILD_STAGINGDIRECTORY");
            LOG.info("Environment variable....." +  file);
            File scrFile = ((TakesScreenshot)seleniumDriver).getScreenshotAs(OutputType.FILE);
            File destination = new File(file + "" + "/SeleniumError.png");
            try {
                LOG.info("copying file from " + scrFile.toPath());
                FileUtils.copyFile(scrFile, destination);
                LOG.info("File copied: " + destination.getPath());
            } catch(Exception exception){
                LOG.error(exception.getMessage());
            }
            LOG.error("Browser automation failed: " + e.getMessage());
            throw new RuntimeException("Browser automation failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void cleanUp(){
        if( seleniumDriver != null){
            seleniumDriver.close();
        }
    }
}
