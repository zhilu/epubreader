
package com.ereader;

import java.awt.Desktop;
import java.net.URL;

public class DesktopUtil {
    

    public static boolean launchBrowser(URL url) throws BrowserLaunchException {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI());
                return true;
            } catch (Exception ex) {
                throw new BrowserLaunchException("Browser could not be launched for "+url, ex);
            }
        }
        return false;
    }
    
    public static class BrowserLaunchException extends Exception {

        private BrowserLaunchException(String message, Throwable cause) {
            super(message, cause);
        }
        
    }
}
