package com.cloud.gatordrive.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.json.JSONObject;

import com.cloud.gatordrive.RequestHandler;

/**
 * @author sm23772
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadFileServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		
		try {
			
			FileUpload fup = new FileUpload();
			boolean isMultipart = FileUpload.isMultipartContent(req);

			// Create a new file upload handler
			System.out.println(isMultipart);
			DiskFileUpload upload = new DiskFileUpload();

			// Parse the request
			List /* FileItem */items = upload.parseRequest(req);
			int fd = 0;
			int partitionNumber = 0;
			int numOfParts = 0;
			String username = null;
			Iterator iter = items.iterator();
			while (iter.hasNext()) {

				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					System.out.println("its a field");
					if(item.getFieldName().contentEquals("fileDescriptor")){
						fd = Integer.parseInt(item.getString());
					}
					if(item.getFieldName().contentEquals("partitionNum")){
						partitionNumber = Integer.parseInt(item.getString());
					}
					if(item.getFieldName().contentEquals("numOfParts")){
						numOfParts = Integer.parseInt(item.getString());
					}
					if(item.getFieldName().contentEquals("username")){
						username = item.getString();
					}
				} else {
					System.out.println("its a file");
					System.out.println(item.getName());
					File cfile = new File(item.getName());
					File tosave = new File("/tmp/"+cfile.getName());
							//getServletContext().getRealPath("/tmp/")+cfile.getName());
							//cfile.getName());
					
					InputStream is = item.getInputStream();
					int success = 0;
					
					if(is != null){
						RequestHandler reqHandler = new RequestHandler(username);
						if(fd != 0){
							success = reqHandler.storePartition(fd, cfile.getName(), is, partitionNumber, numOfParts);
						}else {
							reqHandler.partitionFile(is, cfile.getName());
						}
					}else{
						System.out.println("InputStream was null");
					}
					
					if(fd != 0){
						res.setContentType("text/plain");
						try {
							JSONObject json = new JSONObject();
							json.put("success", success);
							//res.getWriter().println(json.toString());
							res.getWriter().println("success="+success);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					//item.write(tosave);
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
