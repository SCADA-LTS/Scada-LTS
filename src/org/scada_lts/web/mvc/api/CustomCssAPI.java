package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Controller
public class CustomCssAPI {

    @GetMapping("/api/customcss")
    public ResponseEntity<String> getCustomCssFile(HttpServletRequest request) {
        try {

            StringBuilder customCssContent = new StringBuilder();
            File cssFile = new File(Common.ctx.getCtx().getRealPath("/resources/customers.css"));
            cssFile.createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(cssFile.getAbsolutePath()));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                customCssContent.append(currentLine);
            }

            return new ResponseEntity<>(customCssContent.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/customcss")
    public ResponseEntity<String> saveCustomCssFile(HttpServletRequest request, @RequestBody String fileContent) {
        try {

            StringBuilder customCssContent = new StringBuilder();
            File cssFile = new File(Common.ctx.getCtx().getRealPath("/resources/customers.css"));
            cssFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(cssFile.getAbsolutePath()));
            writer.write(fileContent);
            writer.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
