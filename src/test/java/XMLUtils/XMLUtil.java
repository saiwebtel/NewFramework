package XMLUtils;
import io.restassured.path.xml.XmlPath;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import testData.TestDataCreation;


public class XMLUtil {
		public String convertToXml(Object source, Class... type) {
		String result;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext carContext = JAXBContext.newInstance(type);
			Marshaller carMarshaller = carContext.createMarshaller();
			carMarshaller.marshal(source, sw);
			result = sw.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	public static Object getXMLTag(String postBody,String path)
	{
		XmlPath xml=new XmlPath(postBody);
		Object tag=xml.get(path);		
		return tag;		
	}
}
