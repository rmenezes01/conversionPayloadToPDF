package conversionPayloadToPDF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.Attachment;
import com.sap.aii.mapping.api.DynamicConfiguration;
import com.sap.aii.mapping.api.DynamicConfigurationKey;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
	
public class convertPayloadToPDF extends AbstractTransformation{	
	private static final DynamicConfigurationKey CONTENT_DISPOSITION = DynamicConfigurationKey.create("http://sap.com/xi/XI/System/REST","content-disposition");
	
	@Override
    public void transform(TransformationInput in, TransformationOutput out) throws StreamTransformationException {
		// Declarações locais
    	char quotes = '"';
    	
    	// Get Streams - Input e Output
    	InputStream inStream = in.getInputPayload().getInputStream();    	
    	OutputStream outStream = out.getOutputPayload().getOutputStream();
    	
    	// Get file Byte
    	byte[] bytes = extractPayload(inStream);

    	
    	// Nome do arquivo/anexo
    	DynamicConfiguration conf = in.getDynamicConfiguration();    	
    	String fileName = conf.get(CONTENT_DISPOSITION);
    	int sta = fileName.indexOf('"') +1;
    	int end = fileName.indexOf('"',sta);
    	fileName = fileName.substring(sta, end);    	
    	
    	// Set anexo a request
    	Attachment attachment = out.getOutputAttachments().create(fileName, "application/pdf; name="+ quotes + fileName + quotes , bytes);
    	out.getOutputAttachments().setAttachment(attachment);
    	
    	// Monta payload de saída da requisição
    	String xmlpayload = 
    	"<?xml version=" + quotes + "1.0"  + quotes + " encoding=" + quotes + "UTF-8" + quotes + "?>" +
    	"<ns0:mtNfsePdfResponse2 xmlns:ns0=" + quotes + "http://netshoes.com/tecnoSpeed/plugNotas/nfse/pdf" + quotes + "><root>" +	
    	"</root></ns0:mtNfsePdfResponse2>";
    	try {
			outStream.write(xmlpayload.getBytes());
		} catch (Exception e) {

		}

    }

    private static byte[] extractPayload(InputStream input) throws StreamTransformationException{
    	try {			
    		ByteArrayOutputStream buffer = new ByteArrayOutputStream();    		
    		byte[] data = new byte[1024];
    		int nRead;
    		while ((nRead = input.read(data)) != -1){
    			buffer.write(data, 0, nRead);
    		}
    		input.close();
    		buffer.flush();
    		return buffer.toByteArray();     			
		} catch (IOException e) {
			throw new StreamTransformationException("Error in extract stream content " + e.getMessage());
		}
    }
    
}
