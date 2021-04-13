package com.example.mySaasApplication;


import com.google.appengine.api.utils.SystemProperty.Environment;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)
public class HelloAppEngine extends HttpServlet {
 
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		 ImageAnnotatorClient vision = ImageAnnotatorClient.create();
		  
			// The path to the image file to annotate
			// String fileName = "/src/Image/campingimage.jpg";
			 // FileInputStream input = new FileInputStream("src/Image/campingimage.jpg");
			// Reads the image file into memory
		     // Path path = Paths.get(fileName);
		    //  byte[] data = Files.readAllBytes(path);
		      
			  FileItemFactory itemfactory = new DiskFileItemFactory(); 
			  ServletFileUpload upload = new ServletFileUpload(itemfactory);
			  	  
				
				try {
					 List<FileItem> items  = upload.parseRequest(req);
					for(FileItem item:items){
					    
					    String contentType = item.getContentType();
					    if(!contentType.equals("image/png") && !contentType.equals("image/jpeg") ){
					     System.out.println("only png or jpeg format image files supported ..... " + contentType);
					     continue;
					    }
					   InputStream inputstr = item.getInputStream();
					   byte[] data = IOUtils.toByteArray(inputstr);
					     
					   ByteString imgBytes = ByteString.copyFrom(data);
			      
			   // Builds the image annotation request
			      List<AnnotateImageRequest> requests = new ArrayList<>();
			      Image img = Image.newBuilder().setContent(imgBytes).build();
			      Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
			      AnnotateImageRequest request =
			          AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
			      requests.add(request);
			      
			   // Performs label detection on the image file
			      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
			      List<AnnotateImageResponse> responses = response.getResponsesList();

			      for (AnnotateImageResponse res : responses) {
			        if (res.hasError()) {
			          System.out.format("Error: %s%n", res.getError().getMessage());
			          return;
			        }

			        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
			       
			        	 resp.setContentType("text/plain"); resp.setCharacterEncoding("UTF-8");
			   		  
			        	 annotation .getAllFields() .forEach((k, v) ->{
							try {
								resp.getWriter().format("%s : %s%n",k, v.toString());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
			        
			        	 annotation 
			        	 .getAllFields() 
			        	 .forEach((k, v) -> System.out.format("%s : %s%n",k, v.toString()));
							  
			        }
			       
			      }
				  			  
					 // resp.getWriter().print("Hello App Engine!\r\n");
					   }
				} catch (FileUploadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 
					 
			}
	
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) 
      throws IOException {
/*
  	 // Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", "D:\\Classes\\CS-651 WEB SYSTEM\\SAAS exercise\\projectgae-309904-05077290fb54.json");
 

	  ImageAnnotatorClient vision = ImageAnnotatorClient.create();
	  
	// The path to the image file to annotate
	// String fileName = "/src/Image/campingimage.jpg";
	 // FileInputStream input = new FileInputStream("src/Image/campingimage.jpg");
	// Reads the image file into memory
     // Path path = Paths.get(fileName);
    //  byte[] data = Files.readAllBytes(path);
      
	  FileItemFactory itemfactory = new DiskFileItemFactory(); 
	  ServletFileUpload upload = new ServletFileUpload(itemfactory);
	  	   InputStream inputstr = null;
		 
		  List<FileItem> items = null;
		try {
			items = upload.parseRequest(req);
		} catch (FileUploadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   for(FileItem item:items){
		    
		    String contentType = item.getContentType();
		    if(!contentType.equals("image/*")){
		     //out.println("only png format image files supported");
		     continue;
		    }
		   inputstr = item.getInputStream();
		   }
		   byte[] data = IOUtils.toByteArray(inputstr);
		     
		   ByteString imgBytes = ByteString.copyFrom(data);
      
   // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
      AnnotateImageRequest request =
          AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
      requests.add(request);
      
   // Performs label detection on the image file
      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.format("Error: %s%n", res.getError().getMessage());
          return;
        }

        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
       
        	 resp.setContentType("text/plain"); resp.setCharacterEncoding("UTF-8");
   		  
        	 annotation .getAllFields() .forEach((k, v) ->{
				try {
					resp.getWriter().format("%s : %s%n",k, v.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
        
        	 annotation 
        	 .getAllFields() 
        	 .forEach((k, v) -> System.out.format("%s : %s%n",k, v.toString()));
				  
        }
       
      }
	  
	  
		
		  
		  resp.getWriter().print("Hello App Engine!\r\n");
		 
*/
  }
  
}